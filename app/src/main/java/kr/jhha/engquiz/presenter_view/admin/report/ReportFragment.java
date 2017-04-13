package kr.jhha.engquiz.presenter_view.admin.report;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.Report;
import kr.jhha.engquiz.model.local.ReportRepository;
import kr.jhha.engquiz.presenter_view.MyToolbar;

/**
 * Created by jhha on 2016-12-16.
 */

public class ReportFragment extends Fragment implements  ReportContract.View
{
    private ReportContract.ActionsListener mActionListener;
    ReportAdapter mAdapter;

    // 다이알로그
    private AlertDialog.Builder mDialogModify = null;
    private EditText mEditTextKo = null;
    private EditText mEditTextEn = null;

    // 리스트 뷰 UI
    private ListView mItemListView;
    private Integer mSelectedListViewItemPosition = 0;

    TextView mReportCount ;

    private final String mTITLE = "Reports";

    public ReportFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActionListener = new ReportPresenter( this, ReportRepository.getInstance() );
        initDialog();
    }

    private void initDialog()
    {
        mDialogModify = new AlertDialog.Builder( getActivity() );
        mDialogModify.setTitle("Modify");
        mDialogModify.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                d.dismiss();
            }
        });

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        mEditTextKo = new EditText(getActivity());
        mEditTextEn = new EditText(getActivity());
        layout.addView(mEditTextKo);
        layout.addView(mEditTextEn);
        mDialogModify.setView(layout);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("################","ReportFragment onCreateView() called");
        setUpToolBar();

        View view = inflater.inflate(R.layout.content_report, null);
        mItemListView = (ListView) view.findViewById(R.id.report_listview);
        mItemListView.setOnItemClickListener(mOnItemClickListener);

        // report 개수
        mReportCount = (TextView) view.findViewById(R.id.report_total_count);

        mActionListener.getReportList();
        view.bringToFront(); // 리스트가 길어질 경우 가장 위로 스크롤.
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setUpToolBar(){
        final MyToolbar toolbar = MyToolbar.getInstance();
        toolbar.setToolBarTitle( mTITLE );
        toolbar.switchBackground("image");
    }

    @Override
    public void onSuccessGetReportList(List<Report> reports) {
        mAdapter = new ReportAdapter( reports );
        mItemListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        String countText = "Total Reports : " + mAdapter.getCount();
        mReportCount.setText( countText );
    }

    @Override
    public void onFailGetReportList(String msg) {
        msg = "Failed GetReportList. " + msg;
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            mSelectedListViewItemPosition = position;
            Report item = (Report) parent.getItemAtPosition(position) ;
            mActionListener.sentenceClicked(item);
        }
    };

    @Override
    public void showModifyDialog(final Report report){
        mEditTextKo.setText(report.getTextKo());
        mEditTextEn.setText(report.getTextEn());
        mDialogModify.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {

                Report modifiedSentence = new Report();
                modifiedSentence.setSentenceId(report.getSentenceId());
                modifiedSentence.setTextKo(mEditTextKo.getText().toString());
                modifiedSentence.setTextEn(mEditTextEn.getText().toString());
                mActionListener.modifySentence(modifiedSentence);
            }
        });
        mDialogModify.show();
    }

    @Override
    public void onSuccessModifyReport() {
        Toast.makeText(getActivity(), "Success Modify.", Toast.LENGTH_SHORT).show();
        mAdapter.updateIcon(mSelectedListViewItemPosition, Report.STATE_MODIFILED);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailModifyReport(String msg) {
        msg = "Failed ModifyReport. " + msg;
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}


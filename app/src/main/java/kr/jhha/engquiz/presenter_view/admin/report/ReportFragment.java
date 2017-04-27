package kr.jhha.engquiz.presenter_view.admin.report;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.Report;
import kr.jhha.engquiz.model.local.ReportRepository;
import kr.jhha.engquiz.presenter_view.MyToolbar;
import kr.jhha.engquiz.util.ui.Etc;
import kr.jhha.engquiz.util.ui.MyDialog;
import kr.jhha.engquiz.util.ui.MyLog;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.REPORT;


/**
 * Created by jhha on 2016-12-16.
 */

public class ReportFragment extends Fragment implements  ReportContract.View
{
    private ReportContract.ActionsListener mActionListener;
    private ReportAdapter mAdapter;

    private EditText mEditTextKo = null;
    private EditText mEditTextEn = null;

    // 리스트 뷰 UI
    private ListView mItemListView;
    private Integer mSelectedListViewItemPosition = 0;

    TextView mReportCount ;

    public ReportFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActionListener = new ReportPresenter( this, ReportRepository.getInstance() );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MyLog.d("ReportFragment onCreateView() called");
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
        MyToolbar.getInstance().setToolBar(REPORT);
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
    public void showModifyDialog(final Report report)
    {
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle("Modify");
        mEditTextKo = Etc.makeEditText(getActivity(), report.getTextKo());
        mEditTextEn = Etc.makeEditText(getActivity(), report.getTextEn());
        dialog.setEditText(mEditTextKo, mEditTextEn);
        dialog.setPositiveButton( new View.OnClickListener() {
            public void onClick(View arg0)
            {
                Report modifiedSentence = new Report();
                modifiedSentence.setSentenceId(report.getSentenceId());
                modifiedSentence.setTextKo(mEditTextKo.getText().toString());
                modifiedSentence.setTextEn(mEditTextEn.getText().toString());
                mActionListener.modifySentence(modifiedSentence);
                dialog.dismiss();
            }});
        dialog.setNegativeButton();
        dialog.showUp();
    }

    @Override
    public void onSuccessModifyReport( String modifiedKo, String modifiedEn ) {
        Toast.makeText(getActivity(), "Success Modify.", Toast.LENGTH_SHORT).show();
        mAdapter.updateIcon(mSelectedListViewItemPosition, Report.STATE_MODIFILED);
        mAdapter.updateSentence(mSelectedListViewItemPosition, modifiedKo, modifiedEn );
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailModifyReport(String msg) {
        msg = "Failed ModifyReport. " + msg;
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}


package kr.jhha.engquiz.presenter_view.admin.report;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.Report;
import kr.jhha.engquiz.model.local.ReportRepository;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.presenter_view.MyToolbar;
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

    MyDialog mEditSentenceDialog;       // 문장 수정 다이알로그
    TextView mScriptNameTextView;
    private EditText mEditTextKo = null;
    private EditText mEditTextEn = null;

    private LinearLayout mExtraEditLayout = null;
    private EditText mExtraEditTextKo = null;
    private EditText mExtraEditTextEn = null;

    Integer mSelectedSentenceID = 0;
    Integer mSelectedScriptID = 0;

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
    public void onSuccessGetReportList(int reportCountAll, List<Report> reports) {
        mAdapter = new ReportAdapter( reports );
        mItemListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        String countText = "Total Reports : " + reportCountAll;
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
            mSelectedSentenceID = item.getSentenceId();
            mSelectedScriptID = item.getScriptId();
            mActionListener.sentenceClicked(item);
        }
    };

    @Override
    public void showModifyDialog(final Report report)
    {
        mEditSentenceDialog = new MyDialog(getActivity());
        mEditSentenceDialog.setTitle("Modify");
        String scriptTitle = ScriptRepository.getInstance().getScriptTitleById(report.getScriptId());
        mEditSentenceDialog.setMessage(scriptTitle);
        // custom view 를 새로 생성한다.
        // 안하면 이 에러 남..  You must call removeView() on the child's parent first.
        View mEditSentence_View = createView();
        MyLog.d("!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + report.getScriptName());
        mScriptNameTextView.setText(report.getScriptName());
        mEditTextKo.setText(report.getTextKo());
        mEditTextEn.setText(report.getTextEn());
        mEditSentenceDialog.setCustomView(mEditSentence_View, getActivity());
        mEditSentenceDialog.setCancelable(true);
        mEditSentenceDialog.showUp();
    }


    private View createView(){
        // 문장 수정 관련
        View mEditSentence_View = View.inflate(getActivity(), R.layout.content_report_modify, null);
        mScriptNameTextView = ((TextView) mEditSentence_View.findViewById(R.id.report_modify__scriptname));
        mEditTextKo = ((EditText) mEditSentence_View.findViewById(R.id.report_modify__edit_ko));
        mEditTextEn = ((EditText) mEditSentence_View.findViewById(R.id.report_modify__edit_en));
        Button updateSentence = ((Button) mEditSentence_View.findViewById(R.id.report_modify__update_btn));
        Button showExtraEdit = ((Button) mEditSentence_View.findViewById(R.id.report_modify__show_extra_edit_btn));
        Button deleteSentence = ((Button) mEditSentence_View.findViewById(R.id.report_modify__del_btn));
        Button addSentence = ((Button) mEditSentence_View.findViewById(R.id.report_modify__complete_extra_edit_btn));
        Button closeDialog = ((Button) mEditSentence_View.findViewById(R.id.report_modify__close_btn));
        updateSentence.setOnClickListener(mClickListener);
        showExtraEdit.setOnClickListener(mClickListener);
        deleteSentence.setOnClickListener(mClickListener);
        addSentence.setOnClickListener(mClickListener);
        closeDialog.setOnClickListener(mClickListener);

        mExtraEditLayout = ((LinearLayout) mEditSentence_View.findViewById(R.id.report_modify__extra_edit_layout));
        mExtraEditTextKo = ((EditText) mEditSentence_View.findViewById(R.id.report_sentence_extra_edit_ko));
        mExtraEditTextEn = ((EditText) mEditSentence_View.findViewById(R.id.report_sentence_extra_edit_en));
        mExtraEditLayout.setVisibility(View.GONE);

        // 긴~ empty 레이아웃. 이거 없음 스크롤링이 안됨.
        // sentence fragment는 default가 gone이므로 건들지말고, 여기서는 visible 처리해 쓴다.
        LinearLayout scrollingWorkHelpder = ((LinearLayout) mEditSentence_View.findViewById(R.id.report_modify__help_scoll_working));
        scrollingWorkHelpder.setVisibility(View.VISIBLE);

        return mEditSentence_View;
    }

    // 문장 입력 완료 버튼 클릭
    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.report_modify__update_btn:
                    String ko = mEditTextKo.getText().toString();
                    String en = mEditTextEn.getText().toString();
                    mActionListener.modifySentenceUpdate(mSelectedScriptID, mSelectedSentenceID, ko, en);
                    mEditSentenceDialog.dismiss();
                    break;
                case R.id.report_modify__show_extra_edit_btn:
                    mExtraEditLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.report_modify__del_btn:
                    mActionListener.modifySentenceDel(mSelectedScriptID, mSelectedSentenceID);
                    mEditSentenceDialog.dismiss();
                    break;
                case R.id.report_modify__complete_extra_edit_btn:
                    ko = mExtraEditTextKo.getText().toString();
                    en = mExtraEditTextEn.getText().toString();
                    mActionListener.modifySentenceAdd(mSelectedScriptID, ko, en);
                    break;
                case R.id.report_modify__close_btn:
                    mEditSentenceDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    public void onSuccessModifyReportUpdate(String modifiedKo, String modifiedEn) {
        Toast.makeText(getActivity(), "Success Modify - Update.", Toast.LENGTH_SHORT).show();
        mAdapter.updateIcon(mSelectedListViewItemPosition, Report.STATE_MODIFILED);
        mAdapter.updateSentence(mSelectedListViewItemPosition, modifiedKo, modifiedEn );
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccessModifyReportAdd(Integer newSentenceID, String modifiedKo, String modifiedEn) {
        String sentenceID = "(SID: "+newSentenceID+") ";

        Toast.makeText(getActivity(), "Success Modify - Add. " + sentenceID, Toast.LENGTH_SHORT).show();

        modifiedKo = sentenceID + modifiedKo;
        modifiedEn = sentenceID + modifiedEn;
        mAdapter.updateIcon(mSelectedListViewItemPosition, Report.STATE_MODIFILED);
        mAdapter.updateSentence(mSelectedListViewItemPosition, modifiedKo, modifiedEn );
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccessModifyReportDel() {
        Toast.makeText(getActivity(), "Success Modify - Delete.", Toast.LENGTH_SHORT).show();
        mAdapter.updateIcon(mSelectedListViewItemPosition, Report.STATE_MODIFILED);
        mAdapter.updateSentence(mSelectedListViewItemPosition, "DELETED", "DELETED" );
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailModifyReport(String msg) {
        msg = "Failed ModifyReport. " + msg;
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}


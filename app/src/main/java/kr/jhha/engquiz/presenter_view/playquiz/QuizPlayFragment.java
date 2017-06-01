package kr.jhha.engquiz.presenter_view.playquiz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.QuizPlayRepository;
import kr.jhha.engquiz.presenter_view.FragmentHandler;
import kr.jhha.engquiz.presenter_view.MyToolbar;
import kr.jhha.engquiz.presenter_view.sync.SyncDialog;
import kr.jhha.engquiz.util.ui.MyDialog;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.PLAYQUIZ;

/**
 * Created by jhha on 2016-12-16.
 */

public class QuizPlayFragment extends Fragment implements QuizPlayContract.View
{
    private QuizPlayContract.UserActionsListener mActionListener;

    private View mView;
    private TextView mQuestionView;
    private TextView mAnswerView;
    private Button mShowAnswerBtn;
    private Button mNextQuestionButton;
    private NestedScrollView mScrollView;

    private Button mShowAddScriptFragmentBtn;
    private MyToolbar mToolbar;

    private TextView mPlayCountView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initToolbar();

        mActionListener = new QuizPlayPresenter( this, QuizPlayRepository.getInstance() );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.content_playquiz, container, false);

        mScrollView = (NestedScrollView) mView.findViewById(R.id.fragment_playquiz_scroll);

        mQuestionView = (TextView) mView.findViewById(R.id.question);
        mAnswerView = (TextView) mView.findViewById(R.id.answer);

        mShowAnswerBtn = (Button) mView.findViewById(R.id.showAnswerBtn);
        mShowAnswerBtn.setOnClickListener(mClickListener);
        mNextQuestionButton = (Button) mView.findViewById(R.id.nextQuestionBtn);
        mNextQuestionButton.setOnClickListener(mClickListener);
        // 스크립트 추가 바로가기 버튼: 스크립트가 없을때
        mShowAddScriptFragmentBtn = (Button) mView.findViewById(R.id.showAddScriptFragment);
        mShowAddScriptFragmentBtn.setOnClickListener(mClickListener);

        mPlayCountView = (TextView) mView.findViewById(R.id.play_count);

        // 화면에 알람 다이알로그 띄울거 있는지 확인 후, 있으면 띄운다.
        // 퀴즈플레이가 앱 진입 후 첫 화면이므로.
        mActionListener.checkAlarm();

        // 퀴즈 받아오기
        mActionListener.doNextQuestion();

        // Inflate the layout for this fragment
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbar.updateToolBar(PLAYQUIZ);
    }

    @Override
    public void showAlarmDialog(QuizPlayPresenter.ARALM_TYPE type) {
        switch (type){
            case SYNC:
                SyncDialog dialog = new SyncDialog(getActivity());
                dialog.show( new SyncDialog.SyncedCallback(){
                    @Override
                    public void onSynced() {
                    }
                });
                break;
        }
    }

    @Override
    public void showNextQuestion(String question) {
        mScrollView.scrollTo(0, 0);
        mAnswerView.setVisibility(View.INVISIBLE);
        mNextQuestionButton.setVisibility(View.INVISIBLE);
        mShowAnswerBtn.setVisibility(View.VISIBLE);
        mQuestionView.setText(question);

        // 현재 퀴즈 플레이 개수
        int count = mActionListener.getPlayCount();
        mPlayCountView.setText(String.valueOf(count) );
        //mToolbar.updateToolbarQuizCount( String.valueOf(count) );
    }

    @Override
    public void showNotAvailableQuiz(){
        mQuestionView.setText(getString(R.string.play__no_exist_sentence));
        // 스크립트 추가하러 바로가기 버튼 활성화
        //int color = ContextCompat.getColor(getActivity(), R.color.holo_light_orange);
       // mShowAddScriptFragmentBtn.setBackgroundColor(color);
        mShowAddScriptFragmentBtn.setVisibility(View.VISIBLE);
        mShowAnswerBtn.setVisibility(View.INVISIBLE);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.nextQuestionBtn:
                    // 퀴즈 카운팅 하기.
                    // doNextQuestion()에 카운팅을 못넣음
                    //  -> onCreateView()에서도 호출되기 때문에, 화면이 새로고침되면 저절로 카운팅.
                    // 하여, 유저가 직접 '다음' 버튼을 클릭시에만 카운팅.
                    mActionListener.increaseQuizCount();

                    // 다음 퀴즈 가져오기
                    // 카운팅 후에 가져올것.
                    mActionListener.doNextQuestion();
                    break;
                case R.id.showAnswerBtn:
                    mActionListener.getAnswer();
                    break;
                case R.id.showAddScriptFragment:
                    final FragmentHandler fragmentHandler = FragmentHandler.getInstance();
                    fragmentHandler.changeViewFragment( FragmentHandler.EFRAGMENT.SCRIPT_TAB );
                    break;
            }
        }
    };

    @Override
    public void showAnswer(String answer) {
        mShowAnswerBtn.setVisibility(View.INVISIBLE);
        mAnswerView.setVisibility(View.VISIBLE);
        mNextQuestionButton.setVisibility(View.VISIBLE);
        mAnswerView.setText(answer);
    }

    @Override
    public void showSendReportDialog(){
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.report__send_title));
        dialog.setMessage(getString(R.string.report__send_guide));
        dialog.setNeutralButton( "수정 요청", new View.OnClickListener() {
                                                public void onClick(View arg0)
                                                {
                                                    mActionListener.sendReport();
                                                    dialog.dismiss();
                                                }});
        dialog.setNegativeButton();
        dialog.showUp();
    }

    @Override
    public void onSuccessSendReport() {
        Toast.makeText(getActivity(),
                getString(R.string.report__send_succ),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailSendReport(int msgId) {
        Toast.makeText(getActivity(),
                getString(msgId),
                Toast.LENGTH_SHORT).show();
    }

   // @Override
    public void showMoveSentenceDialog(){
        final MyDialog dialog = new MyDialog(getActivity());
        dialog.setTitle(getString(R.string.report__send_title));
        dialog.setMessage(getString(R.string.report__send_guide));
        dialog.setNeutralButton( "수정 요청", new View.OnClickListener() {
            public void onClick(View arg0)
            {
                mActionListener.sendReport();
                dialog.dismiss();
            }});
        dialog.setNegativeButton();
        dialog.showUp();
    }

    /*
     Action Bar
    */
    private void initToolbar() {
        mToolbar = MyToolbar.getInstance();
        mToolbar.show(); // 액션 바 보이기
        setHasOptionsMenu(true); // 액션 바 옵션메뉴 보이기
    }

    // 메뉴버튼이 처음 눌러졌을 때 실행되는 콜백메서드
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // 화면에 보여질때 마다 호출됨
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        mToolbar.updateToolBarOptionMenu(PLAYQUIZ, menu);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_bar__send_report:
                mActionListener.sendReportBtnClicked();
                return true;
            case R.id.action_bar__help_webview:
                mActionListener.helpBtnClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

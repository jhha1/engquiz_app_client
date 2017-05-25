package kr.jhha.engquiz.presenter_view.playquiz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import kr.jhha.engquiz.util.StringHelper;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionListener = new QuizPlayPresenter( this, QuizPlayRepository.getInstance() );

        // 액션 바 보이기
        mToolbar = MyToolbar.getInstance();
        mToolbar.show();

        setHasOptionsMenu(true);
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

        mActionListener.doNextQuestion();

        // Inflate the layout for this fragment
        return mView;
    }

    // Action bar create.
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbar.setToolBar(PLAYQUIZ);
    }

    @Override
    public void showNextQuestion(String question) {
        mScrollView.scrollTo(0, 0);
        mAnswerView.setVisibility(View.INVISIBLE);
        mNextQuestionButton.setVisibility(View.INVISIBLE);
        mShowAnswerBtn.setVisibility(View.VISIBLE);
        mQuestionView.setText(question);
    }

    @Override
    public void showNotAvailableQuiz(){
        mQuestionView.setText(getString(R.string.play__no_exist_sentence));
        // 스크립트 추가하러 바로가기 버튼 활성화
        int color = ContextCompat.getColor(getActivity(), R.color.yellow100);
        mShowAddScriptFragmentBtn.setBackgroundColor(color);
        mShowAddScriptFragmentBtn.setVisibility(View.VISIBLE);
        mShowAnswerBtn.setVisibility(View.INVISIBLE);
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.nextQuestionBtn:
                    mActionListener.doNextQuestion();
                    break;
                case R.id.showAnswerBtn:
                    mActionListener.getAnswer();
                    break;
                case R.id.showAddScriptFragment:
                    final FragmentHandler fragmentHandler = FragmentHandler.getInstance();
                    fragmentHandler.changeViewFragment( FragmentHandler.EFRAGMENT.REGULAR_SCRIPTS);
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

    /*
        Action Bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_bar__send_report:
                mActionListener.sendReportBtnClicked();
                return true;
            case R.id.action_bar__help_quizplay:
                mActionListener.helpBtnClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
}

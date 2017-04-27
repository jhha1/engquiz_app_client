package kr.jhha.engquiz.presenter_view.playquiz;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import kr.jhha.engquiz.presenter_view.MainActivity;
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

        setUpToolBar();
        mView = inflater.inflate(R.layout.content_playquiz, container, false);

        mScrollView = (NestedScrollView) mView.findViewById(R.id.fragment_playquiz_scroll);

        mQuestionView = (TextView) mView.findViewById(R.id.question);
        mAnswerView = (TextView) mView.findViewById(R.id.answer);

        mShowAnswerBtn = (Button) mView.findViewById(R.id.showAnswerBtn);
        mShowAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionListener.getAnswer();
            }
        });
        mNextQuestionButton = (Button) mView.findViewById(R.id.nextQuestionBtn);
        mNextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionListener.doNextQuestion();
            }
        });

        mActionListener.initToolbarTitle();
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
    }

    private void setUpToolBar(){
        mToolbar.setToolBar(PLAYQUIZ);

        // 툴바에 현 프래그먼트 제목 출력
        mActionListener.initToolbarTitle();
    }

    @Override
    public void showTitle(String title) {
        if(StringHelper.isNull(title)){
            title = "Play Quiz";
        }
        mToolbar.setToolbarTitle( title );
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
        mShowAnswerBtn.setVisibility(View.INVISIBLE);
    }

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
}

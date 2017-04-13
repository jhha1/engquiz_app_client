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

    // 다이알로그
    private AlertDialog.Builder mDialogSendReport = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionListener = new QuizPlayPresenter( this, QuizPlayRepository.getInstance() );

        // 액션 바 보이기
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.show();

        setHasOptionsMenu(true);

        mDialogSendReport = new AlertDialog.Builder( getActivity() );
        mDialogSendReport.setTitle("문장 수정 요청");
        mDialogSendReport.setMessage("현재 퀴즈 문장이 이상한가요? " +
                                        "\n'수정요청' 버튼을 눌러 개발자에게 수정요청을 할 수 있습니다." +
                                        "\n수정 된 문장은 'Sync' 메뉴에서 업데이트 받을 수 있습니다." );
        mDialogSendReport.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                d.dismiss();
            }
        });
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
        getActivity().getMenuInflater().inflate(R.menu.activity_main_actionbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setUpToolBar(){
        final MyToolbar toolbar = MyToolbar.getInstance();
        toolbar.switchBackground("gameplay");
        // 툴바에 현 프래그먼트 제목 출력
        mActionListener.initToolbarTitle();
    }

    @Override
    public void showTitle(String title) {
        if(StringHelper.isNull(title)){
            title = "Play Quiz";
        }
        final MyToolbar toolbar = MyToolbar.getInstance();
        toolbar.setToolBarTitle( title );
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
        mQuestionView.setText("퀴즈 데이터가 없습니다. 스크립트를 추가해, 나만의 퀴즈를 만들어 퀴즈게임을 즐길 수 있습니다");
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
                showSendReportDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSendReportDialog(){
        // 이상한 문장 수정요청 보내기 다이알로그 띄우기
        mDialogSendReport.setPositiveButton( "수정 요청", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                // 퀴즈폴더삭제
                mActionListener.sendReport();
            }
        });
        mDialogSendReport.show();
    }

    @Override
    public void onSuccessSendReport() {
        String msg = "문장 수정 요청을 보냈습니다.";
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailSendReport(int what) {
        String msg = null;
        switch (what){
            case 1:
                msg = "직접 만든 문장은, 문장이 소속된 스크립트에서 수정하실 수 있습니다." +
                        "\n TODO. 수정하러 가기 버튼";
                break;
            case 2:
            default:
                msg = "일시적인 오류로 문장 수정 요청에 실패했습니다.";
                break;
        }
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}

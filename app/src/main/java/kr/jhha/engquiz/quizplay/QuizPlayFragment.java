package kr.jhha.engquiz.quizplay;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.data.local.QuizPlayModel;
import kr.jhha.engquiz.data.local.Sentence;
import kr.jhha.engquiz.MainActivity;

import static kr.jhha.engquiz.data.local.QuizPlayModel.quizManager;

/**
 * Created by jhha on 2016-12-16.
 */

public class QuizPlayFragment extends Fragment implements QuizPlayContract.View
{
    private QuizPlayContract.UserActionsListener mActionListener;

    private final String mTITLE = "TODO: MyQuiz 이름";

    private Sentence quiz;

    private TextView mQuestionView;
    private TextView mAnswerView;
    private Button mShowAnswerBtn;
    private Button mNextQuestionButton;
    private NestedScrollView mScrollView;

    // 여러 커스텀 퀴즈들 중, 어떤 것을 플레이 할 것인가
    // TODO view 말고 model이나 컨트롤러로 이동
    private static int selectedQuizID = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionListener = new QuizPlayPresenter( this, QuizPlayModel.getInstance() );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Play Quiz");

        View view = inflater.inflate(R.layout.content_playquiz, container, false);

        mScrollView = (NestedScrollView) view.findViewById(R.id.fragment_playquiz_scroll);

        mQuestionView = (TextView) view.findViewById(R.id.question);
        mAnswerView = (TextView) view.findViewById(R.id.answer);

        mShowAnswerBtn = (Button) view.findViewById(R.id.showAnswerBtn);
        mShowAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswer();
            }
        });
        mNextQuestionButton = (Button) view.findViewById(R.id.nextQuestionBtn);
        mNextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doNextQuestion();
            }
        });

        doNextQuestion();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        // 툴바에 현 프래그먼트 제목 출력
        ((MainActivity)getActivity()).setActionBarTitle( mTITLE );
        super.onResume();
    }

    private void showAnswer() {
        mShowAnswerBtn.setVisibility(View.INVISIBLE);
        mAnswerView.setVisibility(View.VISIBLE);
        mNextQuestionButton.setVisibility(View.VISIBLE);
        mAnswerView.setText(quiz.english);
    }

    private void doNextQuestion() {
        mScrollView.scrollTo(0, 0);
        mAnswerView.setVisibility(View.INVISIBLE);
        mNextQuestionButton.setVisibility(View.INVISIBLE);

        quiz = getNewQuiz();
        if(quiz == null) {
            mQuestionView.setText("퀴즈 데이터가 없습니다. 스크립트를 추가해, 나만의 퀴즈를 만들어 퀴즈게임을 즐길 수 있습니다");
            mShowAnswerBtn.setVisibility(View.INVISIBLE);
            return;
        }
        mShowAnswerBtn.setVisibility(View.VISIBLE);
        mQuestionView.setText(quiz.korean);
    }

    private Sentence getNewQuiz() {
        if(quizManager != null)
            return quizManager.getQuiz();
        return null;
    }

    public static boolean setQuizList (int quizId) {
        selectedQuizID = quizId;
        return true;
    }
}

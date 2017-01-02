package kr.jhha.engquiz.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.QuizUnit;
import kr.jhha.engquiz.view.MainActivity;

import static kr.jhha.engquiz.controller.QuizManager.quizManager;

/**
 * Created by jhha on 2016-12-16.
 */

public class PlayQuizFragment extends Fragment
{
    private final String mTITLE = "TODO: MyQuiz 이름";

    private QuizUnit quiz;

    private TextView mQuestionView;
    private TextView mAnswerView;
    private Button mShowAnswerBtn;
    private Button mNextQuestionButton;
    private NestedScrollView mScrollView;

    // 여러 커스텀 퀴즈들 중, 어떤 것을 플레이 할 것인가
    // TODO view 말고 model이나 컨트롤러로 이동
    private static int selectedQuizID = 0;

    public PlayQuizFragment() {
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
        quiz = getNewQuiz();
        if(quiz == null) {
            mQuestionView.setText("퀴즈 데이터가 없습니다. 싱크를 눌러 서버로부터 퀴즈데이터를 받아주시기 바랍니다.");
          //  Toast.makeText(this, "NewQuiz is null", Toast.LENGTH_SHORT).show();
            return;
        }
        mShowAnswerBtn.setVisibility(View.VISIBLE);
        mAnswerView.setVisibility(View.INVISIBLE);
        mNextQuestionButton.setVisibility(View.INVISIBLE);
        mQuestionView.setText(quiz.korean);
        mScrollView.scrollTo(0, 0);
    }

    private QuizUnit getNewQuiz() {
        if(quizManager != null)
            return quizManager.getQuiz();
        return null;
    }

    public static boolean setQuizList (int quizId) {
        selectedQuizID = quizId;
        return true;
    }
}

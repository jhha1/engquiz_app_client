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

/**
 * Created by jhha on 2016-12-16.
 */

public class QuizPlayFragment extends Fragment implements QuizPlayContract.View
{
    private QuizPlayContract.UserActionsListener mActionListener;

    private TextView mQuestionView;
    private TextView mAnswerView;
    private Button mShowAnswerBtn;
    private Button mNextQuestionButton;
    private NestedScrollView mScrollView;

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
                mActionListener.getAnswer();
            }
        });
        mNextQuestionButton = (Button) view.findViewById(R.id.nextQuestionBtn);
        mNextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionListener.doNextQuestion();
            }
        });

        mActionListener.initTitle();
        mActionListener.doNextQuestion();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        // 툴바에 현 프래그먼트 제목 출력
        mActionListener.initTitle();
        super.onResume();
    }

    @Override
    public void showTitle(String title) {
        ((MainActivity)getActivity()).setActionBarTitle( title );
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
}

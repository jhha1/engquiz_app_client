package kr.jhha.engquiz.quizplay;

import kr.jhha.engquiz.data.local.QuizPlayModel;
import kr.jhha.engquiz.data.local.Sentence;
import kr.jhha.engquiz.util.StringHelper;

/**
 * Created by thyone on 2017-03-15.
 */

public class QuizPlayPresenter implements QuizPlayContract.UserActionsListener {

    private final QuizPlayContract.View mView;
    private final QuizPlayModel mModel;
    private Sentence mCurrentQuiz;

    public QuizPlayPresenter(QuizPlayContract.View view, QuizPlayModel model ) {
        mModel = model;
        mView = view;
    }

    @Override
    public void initTitle() {
        String title = mModel.getPlayQuizFolderTitle();
        if(StringHelper.isNullString(title)){
            title = "Play Game";
        }
        mView.showTitle(title);
    }

    @Override
    public void doNextQuestion(){
        mCurrentQuiz = getNewQuiz();
        if(mCurrentQuiz == null) {
            mView.showNotAvailableQuiz();
            return;
        }
        mView.showNextQuestion(mCurrentQuiz.textKo);
    }
    private Sentence getNewQuiz() {
        final QuizPlayModel model = QuizPlayModel.getInstance();
        if(model != null)
            return model.getQuiz();
        return null;
    }

    @Override
    public void getAnswer(){
        mView.showAnswer(mCurrentQuiz.textEn);
    }
}

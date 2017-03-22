package kr.jhha.engquiz.quizplay;

import kr.jhha.engquiz.data.local.QuizPlayModel;

/**
 * Created by thyone on 2017-03-15.
 */

public class QuizPlayPresenter implements QuizPlayContract.UserActionsListener {

    private final QuizPlayContract.View mView;
    private final QuizPlayModel mModel;

    public QuizPlayPresenter(QuizPlayContract.View view, QuizPlayModel model ) {
        mModel = model;
        mView = view;
    }

}

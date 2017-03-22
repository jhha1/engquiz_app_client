package kr.jhha.engquiz.quizgroup;

import kr.jhha.engquiz.data.local.QuizGroupModel;

/**
 * Created by thyone on 2017-03-15.
 */

public class DeleteQuizGroupPresenter implements DeleteQuizGroupContract.UserActionsListener {

    private final DeleteQuizGroupContract.View mView;
    private final QuizGroupModel mModel;

    public DeleteQuizGroupPresenter(DeleteQuizGroupContract.View view, QuizGroupModel model ) {
        mModel = model;
        mView = view;
    }


}

package kr.jhha.engquiz.quizgroup;

import android.util.Log;

import java.util.List;

import kr.jhha.engquiz.data.local.QuizGroupModel;
import kr.jhha.engquiz.data.local.UserModel;
import kr.jhha.engquiz.data.remote.EResultCode;

/**
 * Created by thyone on 2017-03-15.
 */

public class AddQuizGroupPresenter implements AddQuizGroupContract.UserActionsListener {

    private final AddQuizGroupContract.View mView;
    private final QuizGroupModel mModel;

    public AddQuizGroupPresenter(AddQuizGroupContract.View view, QuizGroupModel model ) {
        mModel = model;
        mView = view;
    }

    public void addQuizGroup( String title, List<Integer> scriptIds ){
        Log.i("AppContent", "AddQuizGroupPresenter addQuizGroup() called");
        Integer userId = UserModel.getInstance().getUserID();
        mModel.addQuizGroup( userId, title, scriptIds, onAddQuizGroup() );
    }

    private QuizGroupModel.AddQuizGroupCallback onAddQuizGroup() {
        return new QuizGroupModel.AddQuizGroupCallback(){

            @Override
            public void onSuccess(List<QuizGroupSummary> quizGroupSummaryList) {
                mView.onAddQuizGroupSuccess();
            }

            @Override
            public void onFail(EResultCode resultCode) {
                mView.onAddQuizGroupFail();
            }
        };
    }
}

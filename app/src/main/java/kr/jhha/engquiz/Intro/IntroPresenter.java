package kr.jhha.engquiz.Intro;

import android.util.Log;

import kr.jhha.engquiz.backend_logic.ScriptManager;
import kr.jhha.engquiz.user.UserModel;

/**
 * Created by jhha on 2017-03-15.
 * This specifies the contract between the view and the presenter.
 */

public class IntroPresenter implements IntroContract.UserActionsListener {

    private final IntroContract.View mView;

    public IntroPresenter( IntroContract.View introView ) {
        mView = introView;
    }

    @Override
    public void initailizeData() {
        initScriptSummaryUserHas();
        initUser();
    }

    private void initScriptSummaryUserHas() {
        // fill script all title/id list
        ScriptManager.getInstance().init2();
    }

    private void initUser() {
        Log.d("$$$$$$$$$$$$$$$$$","initUser() called");
        final UserModel userModel = UserModel.getInstance();
        if( userModel.isExistUser() ) {
            // login
            Log.d("$$$$$$$$$$$$$$$$$","try login. user("+ userModel.toString()+")");
            userModel.logIn( userModel.getUser() );
        }   else {
            // signIn fragment show
            Log.d("$$$$$$$$$$$$$$$$$","try changeView Signin.");
            mView.changeViewFragment(IntroActivity.FRAGMENT.SIGNIN);
        }
    }
}

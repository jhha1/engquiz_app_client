package kr.jhha.engquiz.user;

import android.util.Log;

import java.util.List;

import kr.jhha.engquiz.backend_logic.QuizGroup;
import kr.jhha.engquiz.net.EResultCode;

/**
 * Created by thyone on 2017-03-15.
 */

public class LoginPresenter implements LoginContract.UserActionsListener {

    private final LoginContract.View mView;
    private final UserModel mUser = UserModel.getInstance();

    public LoginPresenter(LoginContract.View view ) {
        mView = view;
    }

    public void logIn( String userNickname ) {
        mUser.logIn( userNickname, onLogInCallback(userNickname) );
    }

    private UserModel.LogInCallback onLogInCallback( final String userNickname ) {
        return new UserModel.LogInCallback(){

            @Override
            public void onLogInSuccess(QuizGroup quizgroupForPlaying, List syncNeededSentenceIds) {
                Log.i("AppContent", "onLogInSuccess()  userNickname: " + userNickname);
                mUser.initUserData( quizgroupForPlaying, syncNeededSentenceIds );
            }

            @Override
            public void onLogInFail(EResultCode resultCode) {
                Log.e("AppContent", "onLogInFail() UnkownERROR. userNickname: " + userNickname);
                // TODO 실패 메세지
            }
        };
    }
}

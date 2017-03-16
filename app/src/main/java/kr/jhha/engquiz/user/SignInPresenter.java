package kr.jhha.engquiz.user;

import android.util.Log;

import java.util.List;

import kr.jhha.engquiz.backend_logic.QuizGroup;
import kr.jhha.engquiz.net.EResultCode;

/**
 * Created by thyone on 2017-03-15.
 */

public class SignInPresenter implements SignInContract.UserActionsListener {

    private final SignInContract.View mView;
    private final UserModel mUser = UserModel.getInstance();

    public SignInPresenter(SignInContract.View view ) {
        mView = view;
    }

    public void signIn( final String userNickname ) {
        mUser.signIn( userNickname, onSignInCallback(userNickname) );
    }

    private UserModel.SignInCallback onSignInCallback( final String userNickname ) {
        return new UserModel.SignInCallback(){
            @Override
            public void onSignInSuccess(Integer userId) {
                Log.i("AppContent", "onSignInSuccess()  userId: " + userId);
                directLogin( userId );
            }

            @Override
            public void onSignInFail(EResultCode resultCode) {
                switch ( resultCode ) {
                    case NICKNAME_DUPLICATED:
                        Log.e("AppContent", "signIn() NICKNAME_DUPLICATED : " + userNickname);
                        // TODO 실패 메세지
                        break;
                    default:
                        Log.e("AppContent", "signIn() UnkownERROR : " + userNickname);
                        break;
                }
            }
        };
    }

    private void directLogin( Integer userId ) {
        // 회원가입 성공. 바로 로긴.
        mUser.logIn( userId, onLogInCallback(userId) );
    }

    private UserModel.LogInCallback onLogInCallback( final Integer userId ) {
        return new UserModel.LogInCallback(){

            @Override
            public void onLogInSuccess(QuizGroup quizgroupForPlaying, List syncNeededSentenceIds) {
                Log.i("AppContent", "onLogInSuccess()  userId: " + userId);
                mUser.initUserData( quizgroupForPlaying, syncNeededSentenceIds );
            }

            @Override
            public void onLogInFail(EResultCode resultCode) {
                Log.e("AppContent", "onLogInFail() UnkownERROR. userId: " + userId);
                // TODO 실패 메세지
            }
        };
    }
}

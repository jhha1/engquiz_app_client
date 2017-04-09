package kr.jhha.engquiz.Intro;

import android.util.Log;

import java.util.List;

import kr.jhha.engquiz.data.local.User;
import kr.jhha.engquiz.data.local.UserRepository;
import kr.jhha.engquiz.data.remote.EResultCode;

/**
 * Created by thyone on 2017-03-15.
 */

public class IntroPresenter implements IntroContract.UserActionsListener {

    private final IntroContract.View mView;
    private final UserRepository mUser;

    public IntroPresenter(IntroContract.View view, UserRepository userRepository) {
        mUser = userRepository;
        mView = view;
    }

    public void checkUserExist() {
        User user = mUser.getUser();

        if( false == mUser.isExistUser() ){
            // 앱 파일에 유저정보가 없는 경우로, 새 빌드된 앱을 깔았을때 케이스.
            // 회원가입 창으로 넘겨서 창 내의 정보에 따라,,
            // 유저가 로긴창으로 이동하거나 or 회원가입한다.
            String msg = "계정 생성. 수업에서 사용하는 영어이름을 영어로 입력해주세요.";
            mView.showSignInDialog(msg);
            return;
        }
        String username = user.getUserName();
        mUser.logIn( user.getUserID(), onLogInCallback( username ) );
    }

    // 새로 클라빌드된경우의 login. 클라에는 데이터초기화로 없다
    // 서버로부터 userID 얻어오기.
    public void login(String username ) {
        mUser.logIn( username, onLogInCallback(username) );
    }

    private UserRepository.LogInCallback onLogInCallback(final String username ) {
        return new UserRepository.LogInCallback(){

            @Override
            public void onLogInSuccess(List syncNeededSentenceIds) {
                Log.i("AppContent", "onLogInSuccess()  user: " + username);
                mView.onLoginSuccess(username);
            }

            @Override
            public void onLogInFail(EResultCode resultCode) {
                Log.e("AppContent", "onLogInFail() UnkownERROR. user: " + username);
                switch (resultCode){
                    case INVALID_UserID:
                        mView.onLoginFail(1);
                        break;
                    default:
                        mView.onLoginFail(0);
                        break;
                }
            }
        };
    }

    @Override
    public void alreadySignIn() {
        mView.showLoginDialog();
    }

    public void signIn( final String userName ) {
        Log.i("AppContent", "SignInPresenter signIn() called  userName: " + userName);
        mUser.signIn( userName, onSignInCallback(userName) );
    }

    private UserRepository.SignInCallback onSignInCallback(final String userName ) {
        return new UserRepository.SignInCallback(){
            @Override
            public void onSignInSuccess(Integer userID, String username) {
                Log.i("AppContent", "onSignInSuccess() userId:"+userID +", userName:"+userName);

                mUser.saveUserInfo( userID, username ); // 유저정보 파일에 저장

                String userIdForLog = "" + userID;
                mUser.logIn( userID, onLogInCallback( userIdForLog ) );
            }

            @Override
            public void onSignInFail(EResultCode resultCode) {
                switch ( resultCode ) {
                    case NICKNAME_DUPLICATED:
                        Log.e("AppContent", "signIn() USERNAME_DUPLICATED : " + userName);
                        String guide = "이미 다른 사람이 사용중인 이름이에요 " +
                                    "\n다른 영어이름을 입력해주세요.";
                        mView.showSignInDialog(guide);
                        break;
                    default:
                        Log.e("AppContent", "signIn() UnkownERROR : " + userName);
                        break;
                }
            }
        };
    }
}

package kr.jhha.engquiz.user;

import android.util.Log;

import java.util.List;

import kr.jhha.engquiz.data.local.User;
import kr.jhha.engquiz.data.local.UserModel;
import kr.jhha.engquiz.data.local.QuizGroupDetail;
import kr.jhha.engquiz.data.remote.EResultCode;

/**
 * Created by thyone on 2017-03-15.
 */

public class LoginPresenter implements LoginContract.UserActionsListener {

    private final LoginContract.View mView;
    private final UserModel mUser;

    public LoginPresenter(LoginContract.View view, UserModel userModel ) {
        mUser = userModel;
        mView = view;
    }


    public void initUser() {
        User user = mUser.getUser();
        if( ! mUser.isExistUser() ){
            // 앱 파일에 유저정보가 없는 경우로, 새 빌드된 앱을 깔았을때 케이스.
            // 회원가입 창으로 넘겨서 창 내의 정보에 따라,,
            // 유저가 로긴창으로 이동하거나 or 회원가입한다.
            mView.onChangeViewToSignIn();
            return;
        }
        String userInfoForLog = user.toString();
        mUser.logIn( user.getUserID(), onLogInCallback( userInfoForLog ) );
    }

    // 일반적인 login.
    // 현재 빌드 버전의 앱을 사용 후 종료한 유저의 로긴. 앱 파일에서 유저정보 로드 해서 로긴.
    public void login(Integer userId ) {
        String userIdForLog = "" + userId;
        mUser.logIn( userId, onLogInCallback( userIdForLog ) );
    }

    // 새로 클라빌드된경우의 login. 클라에는 데이터초기화로 없다
    // 서버로부터 userID 얻어오기.
    public void login(String userNickname ) {
        mUser.logIn( userNickname, onLogInCallback(userNickname) );
    }

    private UserModel.LogInCallback onLogInCallback( final String userInfoForLog ) {
        return new UserModel.LogInCallback(){

            @Override
            public void onLogInSuccess(List syncNeededSentenceIds) {
                Log.i("AppContent", "onLogInSuccess()  user: " + userInfoForLog);
                mView.onLoginSuccess();
            }

            @Override
            public void onLogInFail(EResultCode resultCode) {
                Log.e("AppContent", "onLogInFail() UnkownERROR. user: " + userInfoForLog);
                mView.onLoginFail();
                // TODO 실패 메세지
            }
        };
    }
}

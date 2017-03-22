package kr.jhha.engquiz.user;

import android.util.Log;

import kr.jhha.engquiz.data.local.UserModel;
import kr.jhha.engquiz.data.remote.EResultCode;

/**
 * Created by thyone on 2017-03-15.
 */

public class SignInPresenter implements SignInContract.UserActionsListener {

    private final SignInContract.View mView;
    private final UserModel mUser;

    public SignInPresenter( SignInContract.View view, UserModel userModel ) {
        mUser = userModel;
        mView = view;
    }

    public void signIn( final String userNickname ) {
        Log.i("AppContent", "SignInPresenter signIn() called  userNickname: " + userNickname);
        mUser.signIn( userNickname, onSignInCallback(userNickname) );
    }

    private UserModel.SignInCallback onSignInCallback( final String userNickname ) {
        return new UserModel.SignInCallback(){
            @Override
            public void onSignInSuccess(Integer userID, String nickname, String userKey) {
                Log.i("AppContent", "onSignInSuccess() userId:"+userID +", userKey:"+userKey);

                mUser.saveUserInfo( userID, nickname, userKey ); // 유저정보 파일에 저장
                mView.onSignInSuccess( userID );
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
}

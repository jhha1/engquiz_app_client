package kr.jhha.engquiz.user;

import kr.jhha.engquiz.Intro.IntroContract;

/**
 * Created by thyone on 2017-03-15.
 */

public class LoginPresenter implements LoginContract.UserActionsListener {

    private final LoginContract.View mView;

    public LoginPresenter(LoginContract.View view ) {
        mView = view;
    }

    public void logIn( String userNickname ) {
        UserModel.getInstance().logIn( userNickname );
    }
}

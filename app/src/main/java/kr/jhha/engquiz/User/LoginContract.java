package kr.jhha.engquiz.user;

/**
 * Created by thyone on 2017-03-15.
 */

public class LoginContract {

    interface View {
        void onChangeViewToSignIn();
        void onLoginSuccess();
        void onLoginFail();
    }

    interface UserActionsListener {
        void initUser();
        void login(Integer userId );
        void login(String userNickname );
    }
}

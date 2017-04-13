package kr.jhha.engquiz.presenter_view.intro;

/**
 * Created by thyone on 2017-03-15.
 */

public class IntroContract {

    interface View {
        void showLoginDialog();
        void showSignInDialog(String msg);
        void onLoginSuccess(String username);
        void onLoginFail(int what);
        void onSignInSuccess(Integer userId);
    }

    interface UserActionsListener {
        void checkUserExist();
        void login(String username);
        void alreadySignIn();
        void signIn(String username);
    }
}

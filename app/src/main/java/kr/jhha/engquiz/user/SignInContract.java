package kr.jhha.engquiz.user;

/**
 * Created by thyone on 2017-03-16.
 */

public class SignInContract {
    interface View {
        void onSignInSuccess(Integer userId );
    }

    interface UserActionsListener {
        void signIn(String userNickname);
    }
}

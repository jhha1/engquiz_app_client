package kr.jhha.engquiz.user;

/**
 * Created by thyone on 2017-03-16.
 */

public class SignInContract {
    interface View {
    }

    interface UserActionsListener {
        void signIn(String userNickname);
    }
}

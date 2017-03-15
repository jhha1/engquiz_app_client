package kr.jhha.engquiz.user;

/**
 * Created by thyone on 2017-03-15.
 */

public class LoginContract {

    interface View {
    }

    interface UserActionsListener {
        void logIn( String userNickname );
    }
}

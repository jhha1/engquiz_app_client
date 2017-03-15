package kr.jhha.engquiz.user;

/**
 * Created by thyone on 2017-03-15.
 */

public class SignInPresenter implements SignInContract.UserActionsListener {

    private final SignInContract.View mView;

    public SignInPresenter(SignInContract.View view ) {
        mView = view;
    }

    public void signIn( String userNickname )
    {
        final UserModel user = UserModel.getInstance();
        user.signIn( userNickname, new UserModel.SignInCallback(){
            public void onSignIn( Integer resultCode ) {
                if( resultCode != 0 ) {
                    // TODO 실패 메세지
                    return;
                }
                directLogin( user );
            }
        } );
    }

    public void directLogin( final UserModel user ) {
        // 회원가입 성공.
        // 로긴
        user.logIn( user.getUser() );
        //if( resultCode != 0 ) {
            // TODO 실패 메세지
            return;
       // }
    }
}

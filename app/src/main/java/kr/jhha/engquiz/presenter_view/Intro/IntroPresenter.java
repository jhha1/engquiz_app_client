package kr.jhha.engquiz.presenter_view.intro;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.QuizFolder;
import kr.jhha.engquiz.model.local.QuizPlayRepository;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.model.local.User;
import kr.jhha.engquiz.model.local.UserRepository;
import kr.jhha.engquiz.presenter_view.FragmentHandler;
import kr.jhha.engquiz.presenter_view.MainActivity;
import kr.jhha.engquiz.presenter_view.MyNavigationView;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.ui.MyDialog;
import kr.jhha.engquiz.util.ui.MyLog;

import static kr.jhha.engquiz.presenter_view.FragmentHandler.EFRAGMENT.INTRO;

/**
 * Created by thyone on 2017-03-15.
 */

public class IntroPresenter implements IntroContract.UserActionsListener
{
    private final IntroContract.View mView;
    private final UserRepository mUser;
    private final Context mContext;

    public IntroPresenter(Context context, IntroContract.View view, UserRepository userRepository) {
        mContext = context;
        mUser = userRepository;
        mView = view;

        preInitailize();
    }

    // 서버 안 갔다와도 init 가능한 데이터 초기화
    private void preInitailize(){
        boolean bOK =  ScriptRepository.getInstance().initailize();
        if( !bOK ){
            String msg = mContext.getString(R.string.common__failed_loading);
            MyDialog.showDialogAndForcedCloseApp(mContext, msg);
            return;
        }
    }

    public void checkUserExist() {
        User user = mUser.getUser();

        if( false == mUser.isExistUser() ){
            // 앱 파일에 유저정보가 없는 경우로, 새 빌드된 앱을 깔았을때 케이스.
            // 회원가입 창으로 넘겨서 창 내의 정보에 따라,,
            // 유저가 로긴창으로 이동하거나 or 회원가입한다.
            mView.showSignInDialog(R.string.signin__guide);
            return;
        }
        String username = user.getUserName();
        mUser.logIn( user.getUserID(), onLogInCallback( username ) );
    }

    @Override
    public void alreadySignIn() {
        mView.showLoginDialog(R.string.login__guide);
    }

    public void signIn( final String userName ) {
        MyLog.i("SignInPresenter signIn() called  userName: " + userName);
        mUser.signIn( userName, onSignInCallback(userName) );
    }

    private UserRepository.SignInCallback onSignInCallback(final String userName ) {
        return new UserRepository.SignInCallback(){
            @Override
            public void onSignInSuccess(Integer userID, String username) {
                MyLog.i("onSignInSuccess() userId:"+userID +", userName:"+userName);

                mUser.saveUserInfo( userID, username ); // 유저정보 파일에 저장
                mUser.logIn( userID, onLogInCallback( username ) );
            }

            @Override
            public void onSignInFail(EResultCode resultCode) {
                switch ( resultCode ) {
                    case USERNAME_DUPLICATED:
                        MyLog.e("signIn() USERNAME_DUPLICATED : " + userName);
                        mView.showSignInDialog(R.string.signin__fail_duplicated_name);
                        break;
                    case INVALID_USERNAME:;
                        mView.showSignInDialog(R.string.signin__fail_invalid_name);
                        break;
                    default:
                        MyLog.e( "signIn() OtherError: " + resultCode);
                        mView.showSignInDialog(R.string.signin__fail);
                        break;
                }
            }
        };
    }

    // 새로 클라빌드된경우의 login. 클라에는 데이터초기화로 없다
    // 서버로부터 userID 얻어오기.
    public void login(String username ) {
        mUser.logIn( username, onLogInCallback(username) );
    }

    private UserRepository.LogInCallback onLogInCallback(final String username ) {
        return new UserRepository.LogInCallback(){

            @Override
            public void onLogInSuccess(String quizFolder, List quizFolderScriptIds, List syncNeededSentenceIds) {
                MyLog.i("onLogInSuccess()  user: " + username);
                loginPostProcess(quizFolder, quizFolderScriptIds, syncNeededSentenceIds);
                mView.onLoginSuccess(username);
            }

            @Override
            public void onLogInFail(EResultCode resultCode) {
                MyLog.e("onLogInFail() resultCode: "+resultCode+", username: " + username);
                switch (resultCode){
                    case NONEXIST_USER:
                        mView.onLoginFail(1);
                        break;
                    case NETWORK_ERR:
                        mView.onLoginFail(2);
                        break;
                    case INVALID_ARGUMENT:
                        mView.onLoginFail(3);
                        break;
                    default:
                        mView.onLoginFail(0);
                        break;
                }
            }
        };
    }

    private void loginPostProcess( String quizfolderString, List quizFolderScriptIds, List syncNeededSentenceIds )
    {
        // Admin
        final UserRepository userRepo = UserRepository.getInstance();
        if(userRepo.isAdminUser()) {
            final MyNavigationView navigationView = MyNavigationView.getInstance();
            navigationView.showAdminMenu();
        }

        // Init App Data
        // 1. playing quiz
        if( StringHelper.isNull(quizfolderString) ){
            // 첫 로긴시에는 quizFolder이 없다
        } else {
            QuizFolder quizfolderForPlaying = new QuizFolder();
            quizfolderForPlaying.deserialize(quizfolderString);

            if( QuizFolder.isNull(quizfolderForPlaying) ){
                MyLog.e("QuizFolder is Null " + quizfolderForPlaying.toString());
                return;
            }

            // 해당 스크립트 로드해 스크립트 맵에 initailize.
            final QuizPlayRepository quizPlayRepo = QuizPlayRepository.getInstance();
            quizPlayRepo.initQuizData( quizfolderForPlaying, quizFolderScriptIds );
        }

        // 2. 싱크 알람 띄우기
        if( syncNeededSentenceIds == null || syncNeededSentenceIds.isEmpty() )
            return;

        final ScriptRepository scriptRepo = ScriptRepository.getInstance();
        scriptRepo.saveSyncNeededSentencesSummary( syncNeededSentenceIds );
        final MyNavigationView navigationView = MyNavigationView.getInstance();
        navigationView.attachAlarmIcon(R.id.nav_sync);

        MyLog.i("onLoginSuccess()  " +
                "quizfolderForPlaying: " + ((quizfolderString!=null)?quizfolderString.toString():null) +
                ", syncNeededSentenceIds: " + syncNeededSentenceIds.toString()
        );
    }


}

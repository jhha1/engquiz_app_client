package kr.jhha.engquiz.model.local;

import android.util.Log;

import java.io.FileNotFoundException;
import java.util.List;

import kr.jhha.engquiz.util.FileHelper;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.model.remote.AsyncNet;
import kr.jhha.engquiz.model.remote.EProtocol;
import kr.jhha.engquiz.model.remote.EProtocol2;
import kr.jhha.engquiz.util.exception.EResultCode;

import kr.jhha.engquiz.model.remote.Request;
import kr.jhha.engquiz.model.remote.Response;

/**
 * Created by jhha on 2017-03-15.
 */

public class UserRepository {

    public interface CheckUserCallback {
        void onCheckUserSuccess( boolean bExistUser, Integer userId, String userKey );
        void onCheckUserFail( EResultCode resultCode );
    }

    public interface SignInCallback {
        void onSignInSuccess( Integer userId, String userName );
        void onSignInFail( EResultCode resultCode );
    }

    public interface LogInCallback {
        void onLogInSuccess(String quizFolder, List quizFolderScriptIds, List syncNeededSentenceIds );
        void onLogInFail( EResultCode resultCode );
    }

    private static UserRepository instance = new UserRepository();
    private UserRepository() {}
    public static UserRepository getInstance() {
        return instance;
    }

    private User user = null;
    private boolean mIsAdminUser = false;

    public User getUser() {
        if( ! isExistUser() ) {
            try {
                this.user = loadUserInfo();
            } catch (Exception e) {
                throw new IllegalStateException("Load UserInfo Error -> " + e.getMessage(), e);
            }
        }
        return this.user;
    }

    // 클라 파일에 있는 정보로 유저확인
    public boolean isExistUser() {
        return ! User.isNull( user );
    }

    public Integer getUserID() {
        if( isExistUser() ){
            return user.getUserID();
        }
        return -1;
    }

    // 서버에서 존재하는 유저인지 확인
    private Integer checkExistUser( String nickname, final UserRepository.CheckUserCallback callback ) {
        Request request = new Request( EProtocol2.PID.CheckExistUser );
        request.set(EProtocol.UserName, nickname);
        AsyncNet net = new AsyncNet( request, onCheckExistUser(callback) );
        net.execute();
        return 0;
    }

    private AsyncNet.Callback onCheckExistUser( final UserRepository.CheckUserCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    boolean bExistUser = (Boolean) response.get(EProtocol.IsExistedUser);
                    if(bExistUser) {
                        // 서버에 존재하면 userid가져옴
                        Integer userID = (Integer)response.get(EProtocol.UserID);
                        String nickname = (String)response.get(EProtocol.UserName);
                        callback.onCheckUserSuccess( bExistUser, userID, nickname );
                    } else {
                        callback.onCheckUserFail( EResultCode.ACCOUNT_NONEXIST );
                    }
                } else {
                    // 서버 응답 에러
                    Log.e("AppContent", "CheckExistUserProtocol() UnkownERROR : "+ response.getResultCodeString());
                    callback.onCheckUserFail( response.getResultCode() );
                }
            }
        };
    }

    public boolean isAdminUser(){
        return this.mIsAdminUser;
    }

    public Integer signIn(final String username, final SignInCallback callback ) {
        Request request = new Request( EProtocol2.PID.SIGNIN );
        request.set(EProtocol.UserName, username);
        AsyncNet net = new AsyncNet( request, onSignIn(callback) );
        net.execute();

        return 0;
    }

    private AsyncNet.Callback onSignIn( final UserRepository.SignInCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                Log.i("AppContent", "userModel onSignIn() called  response: " + response.toString());
                if (response.isSuccess()) {
                    Integer userID = (Integer) response.get(EProtocol.UserID);
                    String userName = (String) response.get(EProtocol.UserName);
                    callback.onSignInSuccess( userID, userName );
                } else {
                    callback.onSignInFail( response.getResultCode() );
                }
            }
        };
    }

    // 새로 클라빌드된경우의 login. 클라에는 데이터초기화로 없다
    // 서버로부터 userID 얻어오기.
    public void logIn( final String username, final LogInCallback callback  )
    {
        checkExistUser( username, new UserRepository.CheckUserCallback(){

            @Override
            public void onCheckUserSuccess(boolean bExistUser, Integer userId, String username) {
                // 유저정보 저장. 데이터초기화로 클라에는 유저정보가 없는 상태이므로.
                // checkExistUser 프로토콜로 서버에서 userId, nickname, userKey 검증 완료됨.
                // 이 후 login 결과에 상관없이, 해당유저정보는 검증된 것이므로 저장.
                saveUserInfo( userId, username );

                logIn( userId, callback ); // TODO 서버로부터 리턴코드
            }

            @Override
            public void onCheckUserFail(EResultCode resultCode) {
                switch ( resultCode ){
                    case ACCOUNT_NONEXIST:
                        // TODO 실패 메세지. 존재하지 않는 이름. 이름을 다시 확인
                        callback.onLogInFail( EResultCode.INVALID_NICKNAME );
                        break;
                    default:
                        callback.onLogInFail( resultCode );
                        break;
                }
            }
        } );
    }

    // 일반적인 login.
    // 현재 빌드 버전의 앱을 사용 후 종료한 유저의 로긴. 앱 파일에서 유저정보 로드 해서 로긴.
    public Integer logIn( Integer userID, final UserRepository.LogInCallback callback ) {
        Log.d("$$$$$$$$$$$$$$$$$", "login() called");

        Request request = new Request(EProtocol2.PID.LOGIN);
        request.set(EProtocol.UserID, userID);
        AsyncNet net = new AsyncNet(request, onLogIn(callback));
        net.execute();
        return 0;
    }

    private AsyncNet.Callback onLogIn( final UserRepository.LogInCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    mIsAdminUser = (Boolean) response.get(EProtocol.IsAdmin);
                    String quizFolder = (String) response.get(EProtocol.QuizFolder);
                    List quizFolderScriptIds = (List) response.get(EProtocol.ScriptIds);
                    List syncNeededSentenceIds = (List) response.get(EProtocol.ScriptSentences);
                    callback.onLogInSuccess( quizFolder, quizFolderScriptIds, syncNeededSentenceIds );

                } else {
                    // TODO 실패 메세지. 존재하지 않는 이름. 이름을 다시 확인
                    Log.e("AppContent", "checkUserExist() UnkownERROR : "+ response.getResultCodeString());
                    callback.onLogInFail( response.getResultCode() );
                }
            }
        };
    }

    // load from FILE.
    public User loadUserInfo() {
        final FileHelper file = FileHelper.getInstance();
        String dirPath = file.getAndroidAbsolutePath( FileHelper.UserInfoFolderPath );
        String fileName = FileHelper.UserInfoFileName;
        String userInfo = null;
        try {
            userInfo = file.readFile( dirPath, fileName );
        } catch (FileNotFoundException e) {
            Log.i("##################", "FileNotFoundException !!!!!~!! " + dirPath);
            // 유저 데이터가 없다. 새로 생성된 앱인 경우.
            return null;
        } catch ( Exception e ){
            Log.i("##################", "Exception !!!!!~!! " + dirPath);
            // TODO 입셉션처리
            e.printStackTrace();
            return null;
        }
        if(StringHelper.isNull(userInfo)) {
            User emptyUser = new User();
            return emptyUser; // nickname 입력 창으로 전환
        }

        try {
            User user = new User();
            user.unserialize( userInfo );
            return user;
        } catch ( IllegalArgumentException e ) {
            throw new IllegalStateException("ParseError :: " + e.getMessage(), e);
        }
    }

    public void saveUserInfo( Integer userID, String userName ){
        // 1. save into memory map
        this.user = new User( userID, userName );

        // 2. save into file
        final FileHelper file = FileHelper.getInstance();
        String dirPath = FileHelper.UserInfoFolderPath;
        String fileName = FileHelper.UserInfoFileName;
        String userInfoText = this.user.serialize();
        file.overwrite( dirPath, fileName, userInfoText );
    }







}


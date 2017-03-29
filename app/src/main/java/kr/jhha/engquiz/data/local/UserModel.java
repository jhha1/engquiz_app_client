package kr.jhha.engquiz.data.local;

import android.util.Log;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.util.FileHelper;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.data.remote.AsyncNet;
import kr.jhha.engquiz.data.remote.EProtocol;
import kr.jhha.engquiz.data.remote.EProtocol2;
import kr.jhha.engquiz.data.remote.EResultCode;

import kr.jhha.engquiz.data.remote.Request;
import kr.jhha.engquiz.data.remote.Response;

/**
 * Created by jhha on 2017-03-15.
 */

public class UserModel {

    public interface CheckUserCallback {
        void onCheckUserSuccess( boolean bExistUser, Integer userId, String userKey );
        void onCheckUserFail( EResultCode resultCode );
    }

    public interface SignInCallback {
        void onSignInSuccess( Integer userId, String nickname, String macID );
        void onSignInFail( EResultCode resultCode );
    }

    public interface LogInCallback {
        void onLogInSuccess(List syncNeededSentenceIds );
        void onLogInFail( EResultCode resultCode );
    }

    private static UserModel instance = new UserModel();
    private UserModel() {}
    public static UserModel getInstance() {
        return instance;
    }

    private User user = null;

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
    private Integer checkExistUser( String nickname, String macID, final UserModel.CheckUserCallback callback ) {
        Request request = new Request( EProtocol2.PID.CheckExistUser );
        request.set(EProtocol.UserNickName, nickname);
        request.set(EProtocol.MacID, macID);
        AsyncNet net = new AsyncNet( request, onCheckExistUser(callback) );
        net.execute();
        return 0;
    }

    private AsyncNet.Callback onCheckExistUser( final UserModel.CheckUserCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    boolean bExistUser = (Boolean) response.get(EProtocol.IsExistedUser);
                    if(bExistUser) {
                        // 서버에 존재하면 userid가져옴
                        Integer userID = (Integer)response.get(EProtocol.UserID);
                        String userKey = (String)response.get(EProtocol.UserKey);
                        callback.onCheckUserSuccess( bExistUser, userID, userKey );
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

    public Integer signIn( final String nickname, final SignInCallback callback ) {
        final String macID = String.valueOf(readMacID());

        Request request = new Request( EProtocol2.PID.SIGNIN );
        request.set(EProtocol.UserNickName, nickname);
        request.set(EProtocol.MacID, macID);
        AsyncNet net = new AsyncNet( request, onSignIn(callback, nickname) );
        net.execute();

        return 0;
    }

    private AsyncNet.Callback onSignIn( final UserModel.SignInCallback callback, final String nickname ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                Log.i("AppContent", "userModel onSignIn() called  response: " + response.toString());
                if (response.isSuccess()) {
                    Integer userID = (Integer) response.get(EProtocol.UserID);
                    String userKey = (String) response.get(EProtocol.UserKey);
                    callback.onSignInSuccess( userID, nickname, userKey );
                } else {
                    callback.onSignInFail( response.getResultCode() );
                }
            }
        };
    }

    // 새로 클라빌드된경우의 login. 클라에는 데이터초기화로 없다
    // 서버로부터 userID 얻어오기.
    public void logIn( final String nickname, final LogInCallback callback  )
    {
        final String macID = String.valueOf(readMacID());
        checkExistUser( nickname, macID, new UserModel.CheckUserCallback(){

            @Override
            public void onCheckUserSuccess(boolean bExistUser, Integer userId, String userKey) {
                // 유저정보 저장. 데이터초기화로 클라에는 유저정보가 없는 상태이므로.
                // checkExistUser 프로토콜로 서버에서 userId, nickname, userKey 검증 완료됨.
                // 이 후 login 결과에 상관없이, 해당유저정보는 검증된 것이므로 저장.
                saveUserInfo( userId, nickname, userKey );

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
    public Integer logIn( Integer userID, final UserModel.LogInCallback callback ) {
        Log.d("$$$$$$$$$$$$$$$$$", "login() called");

        Request request = new Request(EProtocol2.PID.LOGIN);
        request.set(EProtocol.UserID, userID);
        AsyncNet net = new AsyncNet(request, onLogIn(callback));
        net.execute();
        return 0;
    }

    private AsyncNet.Callback onLogIn( final UserModel.LogInCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {

                    Map quizfolderMap = (HashMap) response.get(EProtocol.QuizFolder);
                    List syncNeededSentenceIds = (List) response.get(EProtocol.ScriptIds);
                    initGameData( quizfolderMap, syncNeededSentenceIds );
                    callback.onLogInSuccess( syncNeededSentenceIds );

                } else {
                    // TODO 실패 메세지. 존재하지 않는 이름. 이름을 다시 확인
                    Log.e("AppContent", "initUser() UnkownERROR : "+ response.getResultCodeString());
                    callback.onLogInFail( response.getResultCode() );
                }
            }
        };
    }

    private void initGameData( Map quizfolderMap, List syncNeededSentenceIds )
    {
        // 1. playing quiz
        if( QuizFolder.isNull(quizfolderMap) ){
            // 첫 로긴시에는 quizFolder이 없다
        } else {
            QuizFolder quizfolderForPlaying = new QuizFolder();
            quizfolderForPlaying.deserialize(quizfolderMap);

            if( QuizFolder.isNull(quizfolderForPlaying) ){
                Log.e("##################", "QuizFolder is Null " + quizfolderForPlaying.toString());
                return;
            }

            // 해당 스크립트 로드해 스크립트 맵에 initailize.
            QuizPlayModel.getInstance().changePlayingQuizFolder( quizfolderForPlaying );
        }

        // 2. 싱크 알람 띄우기
        SyncModel.getInstance().saveSyncNeededSentencesSummary( syncNeededSentenceIds );
        Log.i("##################", "SYNC ALARM !!!!!~!! " + syncNeededSentenceIds.toString());

        Log.i("AppContent", "onLoginSuccess()  " +
                "quizfolderForPlaying: " + quizfolderMap.toString() +
                ", syncNeededSentenceIds: " + syncNeededSentenceIds.toString()
        );

        // 3. 퀴즈폴더리스트를 서버로부터 받아온다.
        QuizFolderRepository.getInstance().initQuizFolderList( getUserID() );
    }

    private Integer readMacID() {
        return 12345;
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
        if(StringHelper.isNullString(userInfo)) {
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

    public void saveUserInfo( Integer userID, String nickname, String userKey ){
        // 1. save into memory map
        this.user = new User( userID, nickname, userKey );

        // 2. save into file
        final FileHelper file = FileHelper.getInstance();
        String dirPath = FileHelper.UserInfoFolderPath;
        String fileName = FileHelper.UserInfoFileName;
        String userInfoText = this.user.serialize();
        file.overwrite( dirPath, fileName, userInfoText );
    }







}


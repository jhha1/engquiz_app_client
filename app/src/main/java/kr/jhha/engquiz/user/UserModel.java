package kr.jhha.engquiz.user;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.backend_logic.QuizGroup;
import kr.jhha.engquiz.backend_logic.QuizPlayManager;
import kr.jhha.engquiz.backend_logic.ScriptManager;
import kr.jhha.engquiz.net.AsyncNet;
import kr.jhha.engquiz.net.EProtocol;
import kr.jhha.engquiz.net.EProtocol2;
import kr.jhha.engquiz.net.EResultCode;

import kr.jhha.engquiz.net.Request;
import kr.jhha.engquiz.net.Response;

/**
 * Created by jhha on 2017-03-15.
 */

public class UserModel {

    interface CheckUserCallback {
        void onCheckUserSuccess( boolean bExistUser, Integer userId );
        void onCheckUserFail( EResultCode resultCode );
    }

    interface SignInCallback {
        void onSignInSuccess( Integer userId );
        void onSignInFail( EResultCode resultCode );
    }

    interface LogInCallback {
        void onLogInSuccess( QuizGroup quizgroupForPlaying, List syncNeededSentenceIds );
        void onLogInFail( EResultCode resultCode );
    }

    private static UserModel instance = new UserModel();
    private UserModel() {}
    public static UserModel getInstance() {
        return instance;
    }

    private User user = new User();

    public Integer initUser() {
        this.user = getUser();
        if( User.isNull(user) ){
            return 1; // TODO
        }
        Log.d("$$$$$$$$$$$$$$$$$","try login. user("+ user.toString()+")");
        logIn( user.getUserID(), new UserModel.LogInCallback(){

            @Override
            public void onLogInSuccess(QuizGroup quizgroupForPlaying, List syncNeededSentenceIds) {
                Log.i("AppContent", "onLogInSuccess()  userId: " + user.getUserID());
                // TODO data setting
                initUserData( quizgroupForPlaying, syncNeededSentenceIds );
            }

            @Override
            public void onLogInFail(EResultCode resultCode) {
                Log.e("AppContent", "onLogInFail() UnkownERROR. userId: " + user.getUserID());
                // TODO 실패 메세지
            }
        });
        return 0;
    }

    public void initUserData( QuizGroup quizgroupForPlaying, List syncNeededSentenceIds ) {
        Log.i("AppContent", "initUserData()  " +
                "quizgroupForPlaying: " + quizgroupForPlaying.toString() +
                ", syncNeededSentenceIds: " + syncNeededSentenceIds.toString()
        );
        // fill script all title/id list
        ScriptManager.getInstance().init2();
    }

    public User getUser() {
        if( User.isNull(user) )
            this.user = loadUserInfo();

        return this.user;
    }

    // 클라 파일에 있는 정보로 유저확인
    public boolean isExistUser() {
        return ! User.isNull( getUser() );
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
                        callback.onCheckUserSuccess( bExistUser, userID );
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
        AsyncNet net = new AsyncNet( request, onSignIn(callback) );
        net.execute();

        return 0;
    }

    private AsyncNet.Callback onSignIn( final UserModel.SignInCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    Integer userID = (Integer) response.get(EProtocol.UserID);
                    callback.onSignInSuccess( userID );
                } else {
                    callback.onSignInFail( response.getResultCode() );
                }
            }
        };
    }

    public void logIn( String nickname, final LogInCallback callback  )
    {
        // 서버로부터 userID 얻어오기.
        // (새로 클라빌드된경우, 클라에는 데이터초기화로 없다)
        final String macID = String.valueOf(readMacID());
        checkExistUser( nickname, macID, new UserModel.CheckUserCallback(){

            @Override
            public void onCheckUserSuccess(boolean bExistUser, Integer userId) {
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
                response = parseLoginResponseMore( response );
                if (response.isSuccess()) {
                    QuizGroup quizgroupForPlaying = (QuizGroup) response.get(EProtocol.QuizGroupInfo);
                    List syncNeededSentenceIds = (List) response.get(EProtocol.ScriptIds);
                    // 해당 스크립트 로드해 스크립트 맵에 init.
                    QuizPlayManager.getInstance().changePlayingQuizGroup( quizgroupForPlaying );
                    // 싱크 알람 띄우기
                    Log.i("##################", "SYNC ALARM !!!!!~!! " + syncNeededSentenceIds.toString());

                    callback.onLogInSuccess( quizgroupForPlaying, syncNeededSentenceIds );
                } else {
                    // TODO 실패 메세지. 존재하지 않는 이름. 이름을 다시 확인
                    Log.e("AppContent", "logIn() UnkownERROR : "+ response.getResultCodeString());
                    callback.onLogInFail( response.getResultCode() );
                }
            }
        };
    }

    private Response parseLoginResponseMore( Response response )
    {
        // QuizGroupInfo={id=0, userId=-1, uiOrder=-1, state=-1, title=, scriptIdsJson=, createdTime_UnixTimestamp=0, scriptIndexes=[], teminatedNEWState=true}, MSG=SUCCESS, UserID=7}
        QuizGroup quizGroup = new QuizGroup();
        Map quizgroupMap = (HashMap) response.get(EProtocol.QuizGroupInfo);
        for( Object key : quizgroupMap.keySet() ) {
            Object value = quizgroupMap.get(key);
            switch( (String)key ) {
                case "id":
                    quizGroup.id = (Integer) value;
                    break;
                case "title":
                    quizGroup.title = (String) value;
                    break;
                case "scriptIndexes":
                    quizGroup.scriptIndexes = (List) value;
                    break;
            }
        }
        response.set( EProtocol.QuizGroupInfo, quizGroup );
        return response;
    }

    private Integer readMacID() {
        return 12345;
    }

    // load from file.
    public User loadUserInfo()
    {
        // User user = FileManager.readUserInfo();
        // if( user == null )
        //      return false; // nickname 입력 창으로 전환

        User user = new User();
        if( ! User.isNull(user) ) {
            return user;
        }
        return null;
    }







}


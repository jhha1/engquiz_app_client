package kr.jhha.engquiz.user;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import kr.jhha.engquiz.backend_logic.QuizGroup;
import kr.jhha.engquiz.backend_logic.QuizPlayManager;
import kr.jhha.engquiz.net.EProtocol;
import kr.jhha.engquiz.net.EResultCode;
import kr.jhha.engquiz.net.Response;
import kr.jhha.engquiz.net.protocols.CheckExistUserProtocol;
import kr.jhha.engquiz.net.protocols.LogInProtocol;
import kr.jhha.engquiz.net.protocols.SignInProtocol;

/**
 * Created by jhha on 2017-03-15.
 */

public class UserModel {

    interface NetCallback<T> {
        void onResult(T response);
    }

    interface SignInCallback {
        void onSignIn( Integer resultCode );
    }

    private static UserModel instance = new UserModel();
    private UserModel() {}
    public static UserModel getInstance() {
        return instance;
    }

    private User user = new User();

    public User getUser() {
        if( User.isNull(user) )
            this.user = loadUserInfo();

        return this.user;
    }

    public boolean isExistUser() {
        return ! User.isNull( getUser() );
    }

    public Integer signIn( final String nickname, final SignInCallback callback )
    {
        final String macID = String.valueOf(readMacID());

        SignInAsync async = new SignInAsync(new NetCallback() {
            @Override
            public void onResult(Object response) {
                Integer resultCode = (Integer)response;
                if( resultCode > 0 ) {
                    Integer userId = resultCode;
                    user.setUserID(userId);
                    user.setMacID( 0 );
                    user.setNickName(nickname);
                }
                callback.onSignIn( resultCode );
            }
        }, macID, nickname );
        async.execute();
        return 0;
    }

    public void logIn( String nickname )
    {
        // 서버로부터 userID 얻어오기.
        // (새로 클라빌드된경우, 클라에는 데이터초기화로 없다)
        String macID = String.valueOf(readMacID());
        Integer userID = askServerUserExisted(nickname, macID);
        if (userID == 0) {
            // TODO 실패 메세지. 존재하지 않는 이름. 이름을 다시 확인
            return ;
        }
        logIn( user.getUserID() ); // TODO 서버로부터 리턴코드

        boolean bLoginSuccess = true;
        if( bLoginSuccess ) {
            user.setUserID(userID);
            user.setMacID( 0 );
            user.setNickName(nickname);
        }
    }

    public void logIn( User user )
    {
        if( User.isNull(user) ) {
            // TODO 실패 메세지. 존재하지 않는 이름. 이름을 다시 확인
            Log.e("$$$$$$$$$$$$$$$$$","No exist User");
            return ;
        }
        logIn( user.getUserID() ); // TODO 서버로부터 리턴코드
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




    private Integer askServerUserExisted( String nickname, String macID ) {
        Response response = new CheckExistUserProtocol( nickname, macID ).callServer();
        if( response.isFail() ) {
            // 서버 응답 에러
            Log.e("AppContent", "CheckExistUserProtocol() UnkownERROR : "+ response.getResultCodeString());
            return 0;
        }
        boolean bExistUser =  (Boolean) response.get(EProtocol.IsExistedUser);
        if( bExistUser ) {
            // 서버에 존재.
            Integer userID = (Integer) response.get(EProtocol.UserID);
            return userID;
        }
        return 0;
    }

    private Integer signIn( String nickname, String macID ) {
        Response response = new SignInProtocol( nickname, macID ).callServer();
        if( response.isSuccess() ) {
            Integer userID = (Integer) response.get(EProtocol.UserID);
            return userID;
        } else if ( response.getResultCode().equals( EResultCode.NICKNAME_DUPLICATED) ) {
            Log.e("AppContent", "signIn() NICKNAME_DUPLICATED : "+nickname);
        } else {
            Log.e("AppContent", "signIn() UnkownERROR : "+ response.getResultCodeString());
        }
        return 0;
    }

    private Integer logIn( Integer userID ) {
        Log.d("$$$$$$$$$$$$$$$$$","login() called");

        Response response = new LogInProtocol( userID ).callServer();
        if( response.isFail() ) {
            Log.e("AppContent", "logIn() UnkownERROR : "+ response.getResultCodeString());
            return -2;
        }

        QuizGroup quizgroupForPlaying = (QuizGroup) response.get(EProtocol.QuizGroupInfo);
        List syncNeededSentenceIds = (List) response.get(EProtocol.ScriptIds);

        // 해당 스크립트 로드해 스크립트 맵에 init.
        QuizPlayManager.getInstance().changePlayingQuizGroup( quizgroupForPlaying );
        // 싱크 알람 띄우기
        Log.i("##################", "SYNC ALARM !!!!!~!! " + syncNeededSentenceIds.toString());
        return 0;
    }
}

class SignInAsync extends AsyncTask<String, Void, Integer>
{
    String macID;
    String nickname;
    UserModel.NetCallback callback;

    public SignInAsync(
            final UserModel.NetCallback callback, String macID, String nickname ) {
        this.macID = macID;
        this.nickname = nickname;
        this.callback = callback;
    }

    protected Integer doInBackground( String... unused )
    {
        Response response = new SignInProtocol( nickname, macID ).callServer();
        if( response.isSuccess() ) {
            Integer userID = (Integer) response.get(EProtocol.UserID);
            return userID;
        } else if ( response.getResultCode().equals( EResultCode.NICKNAME_DUPLICATED) ) {
            Log.e("AppContent", "signIn() NICKNAME_DUPLICATED : "+nickname);
            return -1;
        } else {
            Log.e("AppContent", "signIn() UnkownERROR : "+ response.getResultCodeString());
            return -1;
        }
    }
    protected void onPostExecute(final Integer... unused)
    {
        Integer result = unused[0];
        callback.onResult( result );
    }
}

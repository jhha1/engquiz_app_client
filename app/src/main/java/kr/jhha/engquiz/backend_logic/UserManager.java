package kr.jhha.engquiz.backend_logic;

import android.util.Log;

import java.util.List;

import kr.jhha.engquiz.net.EProtocol;
import kr.jhha.engquiz.net.EResultCode;
import kr.jhha.engquiz.net.Response;
import kr.jhha.engquiz.net.protocols.CheckExistUserProtocol;
import kr.jhha.engquiz.net.protocols.LogInProtocol;
import kr.jhha.engquiz.net.protocols.SignInProtocol;

/**
 * Created by thyone on 2017-02-15.
 */

class User{
    private Integer userID = -1;
    private String userName = null;
    private String nickname = null;
    private Integer macID = -1;

    public static boolean isNull( User user ){
        if( user == null )
            return true;

        if( user.userID == -1
                && user.macID == -1
                && user.userName == null
                && user.nickname == null)
            return true;

        return false;
    }

    public Integer getUserID() {
        return this.userID;
    }
    public String getUserName() {
        return this.userName;
    }
    public String getNickName() {
        return this.nickname;
    }
    public Integer getMacID() {
        return macID;
    }

    public void setUserID( Integer userID ) {
        this.userID = userID;
    }
    public void setUserName( String userName ) {
        this.userName = userName;
    }
    public void setNickName( String nickname ) {
        this.nickname = nickname;
    }
    public void setMacID( Integer macID ) {
        this.macID = macID;
    }

    public void setUser( User user ) {
        this.userID = user.userID;
        this.userName = user.userName;
        this.nickname = user.nickname;
        this.macID = user.macID;
    }
}

public class UserManager {
    private static UserManager instance = new UserManager();

    private final User user = new User();

    private UserManager() {}
    public static UserManager getInstance() {
        return instance;
    }

    public User getUser() {
        return this.user;
    }

    // if(  user id가 없으면, 첫 방문 or 새로 빌드된 앱 업글 )
    public boolean init()
    {
        // 1. 클라 오프라인 파일에서 유저정보 로드
        User user = loadUserInfo();
        boolean bExistUser = ! User.isNull(user);
        if( bExistUser ) {
            user.setUser( user );
           // 1-1. 로그인
            logIn( user.getUserID() );
            return true;
        }

        // 2. 클라에 유저정보가 없다.
        // 2-1. 유저로부터 닉네임 입력받음

        // 2-2. 서버에 존재하는 유저인지 확인 (새로 클라빌드된경우, 클라에만 없을수있다)
        String macID = String.valueOf(readMacID());
        String nickname = "testjoy";
        Integer userID = askServerUserExisted( nickname, macID );
        if( userID != 0 ) {
            // 2-3. 서버에 존재
            this.user.setUserID( userID );
            this.user.setMacID( 0 );
            this.user.setNickName( nickname );

            // 2-3-1. 로그인.
            logIn( userID );
            return true;
        }

        // 3. 서버에도 존재 안함.
        // 3-1. 회원가입.
        userID = signIn( nickname, macID );
        if( userID != 0 ) {
            this.user.setUserID( userID );
            this.user.setMacID( 0 );
            this.user.setNickName( nickname );

            // 3-2. 로그인.
            logIn( userID );
        }

        return true;
    }

    private User loadUserInfo() {
        // User user = FileManager.readUserInfo();
        // if( user == null )
        //      return false; // nickname 입력 창으로 전환
        User user = new User();
        return user;
    }

    private Integer readMacID() {
        return 12345;
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

    private void logIn( Integer userID ) {
        Response response = new LogInProtocol( userID ).callServer();
        if( response.isFail() ) {
            Log.e("AppContent", "logIn() UnkownERROR : "+ response.getResultCodeString());
            return;
        }

        QuizGroup quizgroupForPlaying = (QuizGroup) response.get(EProtocol.QuizGroupInfo);
        // 해당 스크립트 로드해 스크립트 맵에 init.
        QuizPlayManager.getInstance().changePlayingQuizGroup( quizgroupForPlaying );

        List syncNeededSentenceIds = (List) response.get(EProtocol.ScriptIds);
        // 싱크 알람 띄우기
        Log.i("##################", "SYNC ALARM !!!!!~!! " + syncNeededSentenceIds.toString());

    }
}

package kr.jhha.engquiz.data.local;

import android.util.Log;

import kr.jhha.engquiz.util.Parsor;
import kr.jhha.engquiz.util.StringHelper;

/**
 * Created by thyone on 2017-03-15.
 */

public class User {
    private Integer userID = -1;
    private String userKey = null;
    private String nickname = null;
    //private Integer macID = -1;

    // serialize keys
    final public static String USER_ID = "USER_ID";
    final public static String USER_KEY = "USER_KEY";
    final public static String NICKNAME = "NICKNAME";
    final public static String MAC_ID = "MAC_ID";

    // check values
    final public static Integer NICKNAME_LEN_MIN = 1;
    final public static Integer NICKNAME_LEN_MAX = 20;

    public User() {}

    public User( Integer userID, String nickname, String userKey ) {
        this.userID = checkUserID(userID);
        this.nickname = checkNickname(nickname);
        this.userKey = checkUserKey(userKey);
    }

    public boolean isNull(){
        return User.isNull( this );
    }

    public static boolean isNull( User user ){
        if( user == null )
            return true;

        if( user.userID == -1
               // && user.macID == -1
                && user.userKey == null
                && user.nickname == null)
            return true;

        return false;
    }

    public Integer getUserID() {
        return this.userID;
    }
    public String getUserKey() {
        return this.userKey;
    }
    public String getNickName() {
        return this.nickname;
    }

    public static Integer checkUserID( String userIdString ) {
        if( StringHelper.isNullString(userIdString) ){
            throw new IllegalArgumentException("");
        }
        // TODO 정규식으로 0~9에 해당하는 문자열인지 체크?
        Integer userId = Integer.valueOf(userIdString);
        return checkUserID( userId );
    }

    public static Integer checkUserID( Integer userId ) {
        if( userId < 1 ){
            throw new IllegalArgumentException("invalid userId:"+userId);
        }
        return userId;
    }

    public static String checkNickname( String nickname ) {
        if( StringHelper.isNullString(nickname)){
            throw new IllegalArgumentException("nickname is null");
        }

        if( NICKNAME_LEN_MIN > nickname.length()
                || NICKNAME_LEN_MAX < nickname.length() ){
            throw new IllegalArgumentException("invalid nickname length("+nickname.length()+")");
        }

        return nickname;
    }

    public static String checkUserKey( String userkey ) {
        if( StringHelper.isNullString(userkey)){
            throw new IllegalArgumentException("userkey is null");
        }
        return userkey;
    }

    public static String checkMacID( String macId ) {
        if( StringHelper.isNullString(macId)){
            throw new IllegalArgumentException("macId is null");
        }
        return macId;
    }

    public String serialize() {
        StringBuilder serialized = new StringBuilder();
        serialized.append( USER_ID + Parsor.EqualSeperator + this.userID + Parsor.MainSeperator);
        serialized.append( USER_KEY + Parsor.EqualSeperator + this.userKey + Parsor.MainSeperator);
        serialized.append( NICKNAME + Parsor.EqualSeperator + this.nickname );
        return serialized.toString();
    }

    public void unserialize( String text ){
        if(StringHelper.isNullString(text)) {
            throw new IllegalArgumentException("text is null (" + text + ")");
        }

        String[] rows = text.split( Parsor.MainSeperator);
        if( rows == null  ) {
            throw new IllegalArgumentException("Invalied UserInfo. Split Rows are null  (" + text + ")");
        }
        for( String row : rows ){
            String[] splitStr = row.split( Parsor.EqualSeperator);
            if( splitStr == null || splitStr.length < 2 ) {
                throw new IllegalArgumentException("Invalied UserInfo. Split Row. " +
                        "row count(" + ((splitStr!=null)?splitStr.length:null) + ")");
            }
            String key = splitStr[0];
            String value = splitStr[1];
            if( StringHelper.isNullString(key) || StringHelper.isNullString(value) ){
                throw new IllegalArgumentException("Invalied UserInfo. null key or value. "+
                        "key("+key+"),value("+value+")");
            }

            switch ( key ) {
                case USER_ID:  this.userID = checkUserID(value); break;
                case USER_KEY:  this.userKey = checkUserKey(value); break;
                case NICKNAME:  this.nickname = checkNickname(value); break;
            }
        }
    }
}

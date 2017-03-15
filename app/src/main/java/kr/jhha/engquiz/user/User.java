package kr.jhha.engquiz.user;

/**
 * Created by thyone on 2017-03-15.
 */

public class User {
    private Integer userID = -1;
    private String userName = null;
    private String nickname = null;
    private Integer macID = -1;

    public User() {}
    public User( User user ) {
        this.userID = user.userID;
        this.userName = user.userName;
        this.nickname = user.nickname;
        this.macID = user.macID;
    }

    public static boolean isNull( kr.jhha.engquiz.user.User user ){
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

}

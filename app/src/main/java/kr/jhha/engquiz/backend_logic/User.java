package kr.jhha.engquiz.backend_logic;

/**
 * Created by thyone on 2017-02-15.
 */

public class User {
    private static User instance = new User();
    private Integer accountID = 0;
    private String nickname = null;
    private String macID = null;

    private User() {}

    public static User getInstance() {
        return instance;
    }

    public void init() {
        // TODO load db. db의 정보를 읽어 셋팅. 지금은 임시값
        this.accountID = 1; // 회원가입시 서버로부터 받아오는값
        this.nickname = "joy";
        this.macID = "tempMacID";
    }

    public void create( Integer accountID, String nickname, String macID ) {
        this.accountID = accountID;
        this.nickname = nickname;
        this.macID = macID;

        // db.insertUser( accountID, nickname, macID );
    }


    public Integer getAccountID() {
        return this.accountID;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getMacID() {
        return this.macID;
    }



    public static boolean isSignInUser() {
        // selectedRow = db.selectUserInfo .
        // if(  user db나 row가  없으면 첫 방문 or 새로 빌드된 앱 업글 )
        //      return true;
        return true;
    }
}

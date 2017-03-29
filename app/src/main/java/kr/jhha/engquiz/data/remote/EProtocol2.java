package kr.jhha.engquiz.data.remote;

/**
 * Created by thyone on 2017-03-17.
 */

public class EProtocol2 {


    public enum PID {

        CheckExistUser(1001),
        SIGNIN(1002),
        LOGIN(1003),
        ParsedSciprt(1004),
        SYNC(1005),
        AddUserQuizFolder(1006),
        DelUserQuizFolder(1007),
        GetUserQuizFolders(1008),
        AddUserQuizFolderDetail(1009),

        NONE(9999);

        private Integer value;
        private PID( Integer value )
        {
            this.value = value;
        }
        public int toInt() {return value.intValue(); }
    }
}

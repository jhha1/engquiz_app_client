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
        AddUserQuizGroup(1006),
        DelUserQuizGroup(1007),
        GetUserQuizGroupSummaryList(1008),

        NONE(9999);

        private Integer value;
        private PID( Integer value )
        {
            this.value = value;
        }
        public int toInt() {return value.intValue(); }
    }
}

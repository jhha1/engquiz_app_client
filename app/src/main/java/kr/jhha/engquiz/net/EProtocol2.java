package kr.jhha.engquiz.net;

/**
 * Created by thyone on 2017-03-17.
 */

public class EProtocol2 {


    public enum PID {

        CheckExistUser(1001),
        SIGNIN(1002),
        LOGIN(1003),

        NONE(9999);

        private Integer value;
        private PID( Integer value )
        {
            this.value = value;
        }
        public int toInt() {return value.intValue(); }
    }
}

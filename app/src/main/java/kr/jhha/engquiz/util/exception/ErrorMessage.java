package kr.jhha.engquiz.util.exception;

/**
 * Created by thyone on 2017-04-12.
 */

public class ErrorMessage {

    private static final ErrorMessage instance = new ErrorMessage();
    private ErrorMessage() {}
    public static ErrorMessage getInstance() {
        return instance;
    }

    public static String defaultErrorMsg(EResultCode resultCode){
        String msg = "";
        switch ( resultCode ){
            case NETWORK_ERR:
                msg =  "네트웍 상황이 불안정합니다.";
                break;
        }
        return msg;
    }
}

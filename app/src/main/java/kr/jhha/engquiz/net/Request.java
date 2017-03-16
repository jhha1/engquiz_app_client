package kr.jhha.engquiz.net;

import java.util.HashMap;
import java.util.Map;

import kr.jhha.engquiz.backend_logic.Utils;

/**
 * Created by thyone on 2017-03-16.
 */

public class Request {

    private Map<EProtocol, Object> requestMap = new HashMap<EProtocol, Object>();
    private String requestString;

    public Request( EProtocol2.PID pid ) {
        setRequiredFields( pid );
    }

    private void setRequiredFields( EProtocol2.PID pid ) {
        set( EProtocol.PID, pid.toInt() );
        //set( EProtocol.UserID, UserManager.getInstance().getUserID() );
        //set( EProtocol.UserName, UserManager.getInstance().getNickname() );
    }

    public void set( EProtocol key, Object vaule ) {
        if (key != null && vaule != null) {
            requestMap.put(key, vaule);
        }
    }

    public void serialize() {
        this.requestString = toJsonString();
    }

    public String getRequestString() {
        return this.requestString;
    }

    private String toJsonString() {
        Map<String, Object> stringKeyMap = Utils.enum2stringOfMapKeys(requestMap);
        System.out.println("[TEST REQ MAP] " + requestMap.toString());
        return Utils.map2json( stringKeyMap );
    }
}

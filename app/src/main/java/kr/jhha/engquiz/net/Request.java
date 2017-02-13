package kr.jhha.engquiz.net;

import java.util.HashMap;
import java.util.Map;

import kr.jhha.engquiz.backend_logic.Utils;

/**
 * Created by thyone on 2017-02-08.
 */

public class Request {
    private Map<EProtocol, Object> requestMap = new HashMap<EProtocol, Object>();
    private String requestString;

    public Request(){}

    public void set( EProtocol key, Object vaule ) {
        if (key != null && vaule != null) {
            requestMap.put(key, vaule);
        }
    }

    public String getRequestString() {
        return this.requestString;
    }

    public void serialize() {
        this.requestString = toJsonString();
    }

    private String toJsonString() {
        Map<String, Object> stringKeyMap = Utils.enum2stringOfMapKeys(requestMap);
        System.out.println("[TEST REQ MAP] " + requestMap.toString());
        return Utils.map2json( stringKeyMap );
    }
}

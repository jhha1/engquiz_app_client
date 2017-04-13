package kr.jhha.engquiz.model.remote;

import java.util.HashMap;
import java.util.Map;

import kr.jhha.engquiz.model.local.UserRepository;
import kr.jhha.engquiz.util.StringHelper;

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
        Integer userId = UserRepository.getInstance().getUserID();
        set( EProtocol.UserID, userId );
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
        Map<String, Object> stringKeyMap = enum2stringOfMapKeys(requestMap);
        System.out.println("[TEST REQ MAP] " + requestMap.toString());
        return StringHelper.map2json( stringKeyMap );
    }

    private static Map<String, Object> enum2stringOfMapKeys(Map<EProtocol, Object> map )
    {
        if( map == null || map.isEmpty() )
            System.out.println("EResultCode.INVALID_ARGUMENT. ResponseMap is null or empty (requestMap:"+ map +")");

        Map<String, Object> stringKeyResponseMap = new HashMap<String, Object>();
        for( Map.Entry<EProtocol, Object> e : map.entrySet() )
        {
            if( e.getKey() == null )
                System.out.println("EResultCode.INVALID_ARGUMENT, Invalid Network Field:" + e.getKey());

            String stringKey = ((EProtocol) e.getKey()).value();
            if( stringKey == null )
                System.out.println("EResultCode.INVALID_ARGUMENT, Invalid Network Field:" + e.getKey() + "," + stringKey);

            String upperStringKey = stringKey.trim().toUpperCase();
            stringKeyResponseMap.put( upperStringKey, e.getValue() );
        }
        return stringKeyResponseMap;
    }
}

package kr.jhha.engquiz.model.remote;

import java.util.HashMap;
import java.util.Map;

import kr.jhha.engquiz.model.local.UserRepository;
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.util.ui.MyLog;

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
        set( EProtocol.sPID, pid.name() );  // for logging. pid와 매핑되는 프로토콜 이름
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
        return StringHelper.map2json( stringKeyMap );
    }

    private static Map<String, Object> enum2stringOfMapKeys(Map<EProtocol, Object> map )
    {
        if( map == null || map.isEmpty() )
            MyLog.e("EResultCode.INVALID_ARGUMENT. ResponseMap is null or empty (requestMap:"+ map +")");

        Map<String, Object> stringKeyResponseMap = new HashMap<String, Object>();
        for( Map.Entry<EProtocol, Object> e : map.entrySet() )
        {
            if( e.getKey() == null )
                MyLog.e("EResultCode.INVALID_ARGUMENT, Invalid Network Field:" + e.getKey());

            String stringKey = ((EProtocol) e.getKey()).value();
            if( stringKey == null )
                MyLog.e("EResultCode.INVALID_ARGUMENT, Invalid Network Field:" + e.getKey() + "," + stringKey);

            String upperStringKey = stringKey.trim().toUpperCase();
            stringKeyResponseMap.put( upperStringKey, e.getValue() );
        }
        return stringKeyResponseMap;
    }
}

package kr.jhha.engquiz.data.remote;

import java.util.HashMap;
import java.util.Map;

import kr.jhha.engquiz.util.JsonHelper;

/**
 * Created by thyone on 2017-02-08.
 */

public class Response {

    private Map<EProtocol, Object> responseMap = new HashMap<EProtocol, Object>();
    private String responseString = null;

    public Response(){}

    public Object get( EProtocol key ) {
        if( responseMap.containsKey(key) ){
            return  responseMap.get(key);
        }
        return null;
    }

    public boolean set( EProtocol key, Object value ) {
        if( key == null || value == null ) {
            System.out.println("ERROR Respse.set() param is null or empty. key[" +key+"], value["+value+"]");
            return false;
        }
        responseMap.put(key, value);
        return true;
    }

    public String getResponseString() {
        return this.responseString;
    }

    public boolean isSuccess() {
        EResultCode code = (EResultCode) responseMap.get(EProtocol.CODE);
        return code.equals(EResultCode.SUCCESS);
    }

    public boolean isFail() {
        return ! isSuccess();
    }

    public EResultCode getResultCode() {
        EResultCode code = (EResultCode) responseMap.get(EProtocol.CODE);
        return code;
    }

    public String getResultCodeString() {
        EResultCode code = getResultCode();
        return (code != null) ? code.toString() : null;
    }

    public void unserialize( String responseString )
    {
        if( responseString == null || responseString.isEmpty() ) {
            System.out.println("ERROR responseString is null or empty[" +responseString+"]");
            return;
        }
        this.responseString = responseString;

        // extracting pure json string
        // JSON={..}  '='를 기점으로 'JSON'과 {}를 분리.
        int jsonBodyStartIndex = responseString.indexOf( "=" ) + 1;
        int jsonHeadEndIndex = "JSON=".length();
        if( jsonBodyStartIndex != jsonHeadEndIndex ) {
            System.out.println("ERROR invalid responseString:" + responseString);
            return;
        }
        String responseJson = responseString.substring(jsonBodyStartIndex);

        // json to map
        Map<String, Object> map = JsonHelper.json2map( responseJson );
        if( map == null ) {
            System.out.println("ERROR resmap is null. responseString:" + responseJson);
            return;
        }

        // change map keys to 'EProtocol format'
        this.responseMap = string2enumOfMapKeys( map );
        // convert resultCode type (String -> Enum)
        if( responseMap.containsKey(EProtocol.CODE) ) {
            String codeValueString = (String) responseMap.get(EProtocol.CODE);
            EResultCode code = EResultCode.findBy( codeValueString );
            responseMap.put(EProtocol.CODE, code);
        }
        System.out.println("[RES MAP] " + responseMap.toString());
    }

    private static Map<EProtocol, Object> string2enumOfMapKeys( Map<String, Object> map )
    {
        if( map == null || map.isEmpty() )
            System.out.println("EResultCode.INVALID_ARGUMENT, RequestMap is null or empty (map:"+map+")");

        Map<EProtocol, Object> dst = new HashMap<EProtocol, Object>();
        for( Map.Entry<String, Object> e : map.entrySet() )
        {
            if( e.getKey() == null )
                System.out.println("EResultCode.INVALID_ARGUMENT, Invalid Network Field:" + e.getKey());
            EProtocol enumKey = EProtocol.toEnum( e.getKey() );
            if( EProtocol.NULL == enumKey )
                System.out.println("EResultCode.INVALID_ARGUMENT, Invalid Network Field:" + e.getKey());

            dst.put( enumKey, e.getValue() );
        }
        return dst;
    }
}

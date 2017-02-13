package kr.jhha.engquiz.backend_logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import kr.jhha.engquiz.net.EProtocol;

/**
 * Created by thyone on 2017-02-04.
 */

public class Utils {

    private static ObjectMapper jsonMapper = new ObjectMapper();

    public static boolean isNullString(String str ) {
        return (str == null) || str.isEmpty();
    }

    public static Map<String, Object> json2map(String jsonString )
    {
        if( jsonString == null || jsonString.isEmpty() || jsonString.equals("{}") )
            throw new IllegalArgumentException("invalid Json("+ jsonString + ")");

        try
        {
            return jsonMapper.readValue(jsonString, new TypeReference<HashMap<String, Object>>() {});
        }
        catch ( Exception e )
        {
            throw new IllegalStateException("JsonAPI ERR", e);
        }
    }

    public static String map2json( Map<String, Object> map )
    {
        if( map == null || map.isEmpty() )
            throw new IllegalArgumentException("map is null or empty:("+ map +")");

        try
        {
            return jsonMapper.writeValueAsString(map);
        }
        catch ( Exception e )
        {
            throw new IllegalStateException("JsonAPI ERR", e);
        }
    }

    public static String list2json( List<Object> list )
    {
        if( list == null || list.isEmpty() )
            throw new IllegalArgumentException("list is null or empty:("+ list +")");

        try
        {
            return jsonMapper.writeValueAsString(list);
        }
        catch ( Exception e )
        {
            throw new IllegalStateException("JsonAPI ERR", e);
        }
    }

    public static Map<String, Object> enum2stringOfMapKeys(Map<EProtocol, Object> map )
    {
        if( map == null || map.isEmpty() )
            System.out.println("EResultCode.INVALID_ARGUMENT. ResponseMap is null or empty (requestMap:"+ map +")");

        Map<String, Object> stringKeyResponseMap = new HashMap<String, Object>();
        for( Map.Entry<EProtocol, Object> e : map.entrySet() )
        {
            if( e.getKey() == null )
                System.out.println("EResultCode.INVALID_ARGUMENT, Invalid Protocol Field:" + e.getKey());

            String stringKey = ((EProtocol) e.getKey()).value();
            if( stringKey == null )
                System.out.println("EResultCode.INVALID_ARGUMENT, Invalid Protocol Field:" + e.getKey() + "," + stringKey);

            String upperStringKey = stringKey.trim().toUpperCase();
            stringKeyResponseMap.put( upperStringKey, e.getValue() );
        }
        return stringKeyResponseMap;
    }

    public static Map<EProtocol, Object> string2enumOfMapKeys( Map<String, Object> map )
    {
        if( map == null || map.isEmpty() )
            System.out.println("EResultCode.INVALID_ARGUMENT, RequestMap is null or empty (map:"+map+")");

        Map<EProtocol, Object> dst = new HashMap<EProtocol, Object>();
        for( Map.Entry<String, Object> e : map.entrySet() )
        {
            if( e.getKey() == null )
                System.out.println("EResultCode.INVALID_ARGUMENT, Invalid Protocol Field:" + e.getKey());
            EProtocol enumKey = EProtocol.toEnum( e.getKey() );
            if( EProtocol.NULL == enumKey )
                System.out.println("EResultCode.INVALID_ARGUMENT, Invalid Protocol Field:" + e.getKey());

            dst.put( enumKey, e.getValue() );
        }
        return dst;
    }

}

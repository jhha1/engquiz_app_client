package kr.jhha.engquiz.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thyone on 2017-03-21.
 */

public class JsonHelper {
    private static ObjectMapper jsonMapper = new ObjectMapper();

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

    public static String map2json(Map<String, Object> map )
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

    public static String list2json(List list )
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

    public static Object json2Object(String jsonString )
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
}

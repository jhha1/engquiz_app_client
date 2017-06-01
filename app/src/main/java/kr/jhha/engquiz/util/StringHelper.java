package kr.jhha.engquiz.util;

import android.text.Html;
import android.text.Spanned;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.util.exception.EResultCode;

/**
 * Created by thyone on 2017-02-04.
 */

public class StringHelper {

    private static ObjectMapper jsonMapper = new ObjectMapper();

    public static boolean isNull(String str ) {
        return (str == null) || str.isEmpty();
    }

    public static final String EMPTY_STRING = new String();

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

    public static Spanned formatHtml(String source){
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
            // noinspection deprecation
            return Html.fromHtml(source);
        }
        return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
    }


}

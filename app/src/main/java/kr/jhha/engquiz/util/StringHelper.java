package kr.jhha.engquiz.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import kr.jhha.engquiz.data.remote.EProtocol;

/**
 * Created by thyone on 2017-02-04.
 */

public class StringHelper {

    public static boolean isNullString(String str ) {
        return (str == null) || str.isEmpty();
    }

}

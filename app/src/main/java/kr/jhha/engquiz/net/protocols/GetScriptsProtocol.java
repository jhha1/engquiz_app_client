package kr.jhha.engquiz.net.protocols;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.backend_logic.Script;
import kr.jhha.engquiz.backend_logic.Utils;
import kr.jhha.engquiz.net.EProtocol;
import kr.jhha.engquiz.net.Protocol;
import kr.jhha.engquiz.net.Request;
import kr.jhha.engquiz.net.Response;

// 스크립트 이름으로 서버에서 검색해,
// 서버에 있으면 스크립트(파싱된) 다운로드
 public class GetScriptsProtocol extends Protocol
{
    public final static Integer PID = 1003;

    public GetScriptsProtocol( List<Integer> scriptIndexes )
    {
        super( PID );
        makeRequest( scriptIndexes );
    }

    public void makeRequest( List<Integer> scriptIndexes )
    {
        request.set( EProtocol.ScriptIndex, scriptIndexes);
    }

    @Override
    protected Response parseResponseMore()
    {
        Map<Integer, Script> parsedScript2 = new HashMap<Integer, Script>();

        Map<String, HashMap> parsedScript = (HashMap) response.get(EProtocol.ParsedSciprt);
        for( Map.Entry<String, HashMap> e : parsedScript.entrySet() ){
            String indexString = e.getKey();
            Map<String , Object> scriptMap = e.getValue();

            if( Utils.isNullString(indexString) || scriptMap == null) {
                Log.e("!!!!!!!!!!!!",
                        "GetScriptsProtocol.parseResponse() parsedScript is null." +
                                "indexString["+indexString+"],scriptMap["+scriptMap+"]");
                continue;
            }
            Integer index = Integer.parseInt(indexString);
            Script script = new Script(scriptMap);
            parsedScript2.put( index, script );

            response.set( EProtocol.ParsedSciprt,parsedScript2 );
        }
        return response;
    }

}

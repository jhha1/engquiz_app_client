package kr.jhha.engquiz.net.protocols;

import java.util.Map;

import kr.jhha.engquiz.net.EProtocol;
import kr.jhha.engquiz.net.Http;
import kr.jhha.engquiz.net.Protocol;
import kr.jhha.engquiz.net.Request;
import kr.jhha.engquiz.net.Response;

 public class CheckToNeedSyncProtocol extends Protocol
{
    public final static Integer PID = 1005;

    public CheckToNeedSyncProtocol( Map<String, String> cliScriptIndexsAndRevisions )
    {
        super( PID );
        makeRequest( cliScriptIndexsAndRevisions );
    }

    private void makeRequest( Map<String, String> cliScriptIndexsAndRevisions )
    {
        request.set( EProtocol.ScriptIndex, cliScriptIndexsAndRevisions );
    }

    @Override
    protected Response parseResponseMore()
    {
        return response;
    }
}

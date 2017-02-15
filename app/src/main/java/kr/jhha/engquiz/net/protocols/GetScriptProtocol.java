package kr.jhha.engquiz.net.protocols;

import kr.jhha.engquiz.net.EProtocol;
import kr.jhha.engquiz.net.Protocol;
import kr.jhha.engquiz.net.Request;
import kr.jhha.engquiz.net.Response;

// 스크립트 이름으로 서버에서 검색해,
// 서버에 있으면 스크립트(파싱된) 다운로드
 public class GetScriptProtocol extends Protocol
{
    public final static Integer PID = 1002;

    public GetScriptProtocol( String pdfFileName )
    {
        super( PID );
        makeRequest( pdfFileName );
    }

    private void makeRequest( String pdfFileName )
    {
        request.set( EProtocol.ScriptTitle, pdfFileName);
    }

    protected Response parseResponseMore()
    {
        return response;
    }

}

package kr.jhha.engquiz.net.protocols;

import kr.jhha.engquiz.net.EProtocol;
import kr.jhha.engquiz.net.Protocol;
import kr.jhha.engquiz.net.Request;
import kr.jhha.engquiz.net.Response;

// 스크립트 이름으로 서버에서 검색해,
// 서버에 있으면 스크립트(파싱된) 다운로드
 public class GetScriptProtocol implements Protocol
{
    private Request request = new Request();
    private Response response = new Response();

    private Integer pid = 1002;

    public GetScriptProtocol(String pdfFileName )
    {
        makeRequest( pdfFileName );
    }

    @Override
    public Request getRequest() {
        return request;
    }
    @Override
    public Response getResponse() {
        return response;
    }

    public void makeRequest( String pdfFileName )
    {
        request.set( EProtocol.MacID, "dfdfdfd" );
        request.set( EProtocol.PID, pid );
        request.set( EProtocol.ScriptTitle, pdfFileName);
        request.serialize();
    }

    @Override
    public Object parseResponse( String responseString )
    {
        response.setResponseString( responseString );
        response.unserialize();
        return response;
    }

}

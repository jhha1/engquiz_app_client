package kr.jhha.engquiz.net.protocols;

import java.util.List;

import kr.jhha.engquiz.net.EProtocol;
import kr.jhha.engquiz.net.Protocol;
import kr.jhha.engquiz.net.Request;
import kr.jhha.engquiz.net.Response;

// 서버에 파싱되어 저장된 pdf 파일 중, 클라가 몇개나 가지고 있는지 확인.
// 클라가 가지고 있는 스크립트만 서버로부터 파싱받아 게임에 적용시킬수 있다.
// 유저별 수업수강기간이 다른 점을 감안. 유저는 본인이 수강한 내용의 스크립트만 게임할수 있는것이다.
public class MatchScriptProtocol extends Protocol
{
    public final static Integer PID = 1004;

   public MatchScriptProtocol( List<String> pdfFileNames )
   {
       super( PID );
       makeRequest( pdfFileNames );
   }

    private void makeRequest( List<String> pdfFileNames )
   {
       request.set( EProtocol.ScriptTitle, pdfFileNames );
   }

   @Override
   protected Response parseResponseMore()
   {
       return response;
   }

}

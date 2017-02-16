package kr.jhha.engquiz.net.protocols;

import kr.jhha.engquiz.net.EProtocol;
import kr.jhha.engquiz.net.Protocol;
import kr.jhha.engquiz.net.Response;

public class SignInProtocol extends Protocol
{
   public final static Integer PID = 1006;

   public SignInProtocol( String nickname )
   {
       super( PID );
       makeRequest( nickname );
   }

   private void makeRequest( String nickname )
   {
       request.set( EProtocol.UserName, nickname );
   }

   @Override
   protected Response parseResponseMore()
   {
       return response;
   }
}

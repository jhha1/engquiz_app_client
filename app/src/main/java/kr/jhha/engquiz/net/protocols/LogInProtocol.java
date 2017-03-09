package kr.jhha.engquiz.net.protocols;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.backend_logic.QuizGroup;
import kr.jhha.engquiz.net.EProtocol;
import kr.jhha.engquiz.net.Protocol;
import kr.jhha.engquiz.net.Response;

public class LogInProtocol extends Protocol
{
   public final static Integer PID = 1003;

   public LogInProtocol( Integer userID )
   {
       super( PID );
       makeRequest( userID );
   }

   private void makeRequest( Integer userID )
   {
       request.set( EProtocol.UserID, userID );
   }

   @Override
   protected Response parseResponseMore()
   {
       // QuizGroupInfo={id=0, userId=-1, uiOrder=-1, state=-1, title=, scriptIdsJson=, createdTime_UnixTimestamp=0, scriptIndexes=[], teminatedNEWState=true}, MSG=SUCCESS, UserID=7}
       QuizGroup quizGroup = new QuizGroup();
       Map quizgroupMap = (HashMap) response.get(EProtocol.QuizGroupInfo);
       for( Object key : quizgroupMap.keySet() ) {
           Object value = quizgroupMap.get(key);
           switch( (String)key ) {
               case "id":
                   quizGroup.id = (Integer) value;
                   break;
               case "title":
                   quizGroup.title = (String) value;
                   break;
               case "scriptIndexes":
                   quizGroup.scriptIndexes = (List) value;
                   break;
           }
       }
       response.set( EProtocol.QuizGroupInfo, quizGroup );
       return response;
   }
}

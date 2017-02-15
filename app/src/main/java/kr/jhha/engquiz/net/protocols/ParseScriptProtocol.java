package kr.jhha.engquiz.net.protocols;

import kr.jhha.engquiz.backend_logic.ScriptManager;
import kr.jhha.engquiz.net.EProtocol;
import kr.jhha.engquiz.net.Protocol;
import kr.jhha.engquiz.net.Request;
import kr.jhha.engquiz.net.Response;

/**
 * Created by thyone on 2017-02-04.
 */
/*
// AsyncTask<1,2,3>
// 1: doInBackground의 파라메터. doInBackground의 return 파라메터는  onPostExecute의 인자값 파라메터
// 2: onProgressUpdate 파라메터
// 3: onPostExecute 파라메터.
public class ParseScriptPacket extends AsyncTask<Object, Integer, Object>
{
    // 별도 쓰레드에서의 http 처리값을, 메인쓰레드의 호출한 UI에 돌려주기 위해 사용.
    public interface AsyncHandler {
        void onPreExecute();
        void onProgressUpdate( Integer progressState  );
        void onPostExecute(Object result );
    }
    public AsyncHandler delegate = null;

    public ParseScriptPacket(AsyncHandler delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute()
    {
        delegate.onPreExecute();
    }

    // 별도 쓰레드로 돌아가는 함수. http call은 메인쓰레드에서 사용못함.
    @Override
    protected Object doInBackground( Object... params )
    {
        publishProgress(0);
        if( checkParams(params) == false ) {
            System.out.println("ERR invalid param");
            return null;
        }
        String pdfFilepath = (String)params[0];
        String pdfFilename = (String)params[1];

        Protocol protocol = new ParseScriptProtocol( pdfFilepath, pdfFilename );
        String requestString = protocol.getRequest().getRequestString();

        publishProgress(1);        // 서버 처리중
        String responseString = new Http().httpRequestPost( requestString );

        publishProgress(2);        // 결과 처리중
        return protocol.parseResponse( responseString );
    }

    // doInBackground()가 동작하는 동안 Ui작업
    // Main thread 에서 돌아가는 함수
    @Override
    protected void onProgressUpdate( Integer... progressState ) {
        // 다운로드 퍼센티지 표시작업
        delegate.onProgressUpdate( progressState[0] );
    }

    // 모두 작업을 마치고 실행할 일 (메소드 등등)
    // Main thread 에서 돌아가는 함수
    @Override
    protected void onPostExecute( Object result ) {
        delegate.onPostExecute( result );
    }

    // 외부에서 강제로 취소할때 호출되는 메소드
    @Override
    protected void onCancelled()
    {
        isCanceled = true;
        publishProgress(0);
        Toast.makeText(AsyncTaskExampleActivity.this, "취소됨", Toast.LENGTH_SHORT).show();
    }

    private boolean checkParams( Object... params ) {
        if(params == null) {
            System.out.println("ERR no param.");
            return false;
        }
        if(params.length < 2) {
            System.out.println("ERR params.length is false");
            return false;
        }
        return true;
    }
}
*/
 public class ParseScriptProtocol extends Protocol
{
    public final static Integer PID = 1001;

    public ParseScriptProtocol( String pdfFilePath, String pdfFileName )
    {
        super( PID );
        makeRequest( pdfFilePath, pdfFileName );
    }

    private void makeRequest( String pdfFilePath, String pdfFileName )
    {
        byte[] pdfFile = ScriptManager.getInstance().loadPDF( pdfFilePath, pdfFileName );
        request.set( EProtocol.ScriptTitle, pdfFileName);
        request.set( EProtocol.SciprtPDF, pdfFile);
        //byte[] test = new byte[10];for(int i=0;i<10;++i) test[i] = (byte)i;
        //protocol.setRequestParam( EProtocol.SciprtPDF, test);
    }

    @Override
    protected Response parseResponseMore()
    {
        return response;

        /*
        // parsing script list.  List<HashMap> -> List<Sentence>
        //  : 서버에서 Sentence Object를 json string으로 변환시에, HashMap포맷으로 변환된다.
        List<HashMap> sentencesMap = (List<HashMap>) response.get(EProtocol.ParsedSciprt);
        List<Sentence> parsedSentences = new LinkedList<Sentence>();
        if( sentencesMap != null ) {
            for( HashMap sentenceMap : sentencesMap )
            {
                String ko = null;
                String en = null;
                if( sentenceMap.containsKey(Sentence.KOREAN) )
                    ko = (String) sentenceMap.get(Sentence.KOREAN);
                if( sentenceMap.containsKey(Sentence.ENGLIST) )
                    en = (String) sentenceMap.get(Sentence.ENGLIST);

                Sentence s = new Sentence( ko, en );
                parsedSentences.add( s );
            }
        }

        Integer scriptIndex = (Integer) response.get(EProtocol.ScriptIndex);
        Integer scriptRevision = (Integer) response.get(EProtocol.ScriptRevision);
        String scriptTitle = (String) response.get(EProtocol.ScriptTitle);
        return new Script( scriptTitle, scriptIndex, scriptRevision, parsedSentences );*/
    }

}

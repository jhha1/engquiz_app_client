package kr.jhha.engquiz.data.local;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.data.remote.AsyncNet;
import kr.jhha.engquiz.data.remote.EProtocol2;
import kr.jhha.engquiz.data.remote.EResultCode;
import kr.jhha.engquiz.data.remote.Request;
import kr.jhha.engquiz.util.FileHelper;
import kr.jhha.engquiz.util.Parsor;
import kr.jhha.engquiz.data.remote.EProtocol;
import kr.jhha.engquiz.data.remote.Response;

import static kr.jhha.engquiz.util.FileHelper.ParsedFile_AndroidPath;


/**
 * Created by jhha on 2016-10-14.
 */

public class ScriptRepository {

    public interface ParseScriptCallback {
        void onSuccess();
        void onFail( EResultCode resultCode );
    }

    private static final ScriptRepository instance = new ScriptRepository();

    // 유저가 서버로부터 파싱받은 '전체 스크립트'의 id, title 만 가진 리스트.
    private Map<Integer, String> mAllScriptIdsAndTitles = new HashMap<>();
    private Map<String, Integer> mAllScriptIdsAndTitles_ReMap = new HashMap<String, Integer>();

    // '일부 스크립트'들을 로드한 스크립트맵.
    // 파싱된 스크립트 문장들이 들어있다.
    // 사이즈가 있으므로, 전체 스크립트를 로드 안하고
    // 앱 실행중 스크립트의 문장을 읽을 필요가 있을때만 파일로부터 읽어 이 memory map에 캐싱한다.
    private Map<Integer, Script> mCachedScriptMap = new HashMap<>();

    private ScriptRepository() {
        initailize();
    }
    public static ScriptRepository getInstance() {
        return instance;
    }

    public void initailize()
    {
        // 1.  유저가 서버로부터 파싱받은 '전체 스크립트'의 id, title 만 업로드.
        final FileHelper file = FileHelper.getInstance();
        List<String> fileNames = file.listFileNames( getParsedScriptLocation() );
        for( String filename : fileNames ) {
            String[] splitTitle = Parsor.splitParsedScriptTitleAndId( filename );
            if( splitTitle == null ) {
                Log.e("########", "Failed split script title. " +
                        "but, CONTINUE Upload script titles without this.  " +
                        "title before split("+filename+")");
                continue;
            }
            Integer scriptId = Integer.parseInt( splitTitle[0] );
            String scriptTitle = splitTitle[1];
            addScriptIdAndTitleInMemory(scriptId, scriptTitle);
        }
    }

    public String getParsedScriptLocation() {
        return FileHelper.getInstance().getAndroidAbsolutePath(ParsedFile_AndroidPath);
    }

    public String getAbsoluteFilePath( String path ) {
        return FileHelper.getInstance().getAndroidAbsolutePath(path);
    }

    public String getScriptTitleAsId(Integer scriptId ) {
        return mAllScriptIdsAndTitles.get(scriptId);
    }

    public Integer getScriptIdAsTitle(@NonNull String title ) {
        if( mAllScriptIdsAndTitles_ReMap.containsKey(title) ) {
            return mAllScriptIdsAndTitles_ReMap.get(title);
        }
        return -1;
    }

    public String[] getScriptTitleAll(){
        final Map<String, Integer> titleMap = mAllScriptIdsAndTitles_ReMap;
        String[] arr = new String[titleMap.size()];
        return titleMap.keySet().toArray(arr);
    }

    public Script getScript( Integer scriptId )
    {
        // 유저가 가지고 있는 스크립트인지 체크
        if (false == hasScript(scriptId)) {
            Log.e("############", "invalid scriptID[" + scriptId + "]");
            return null;
        }

        if (hasCachedScript(scriptId)) {
            // 스크립트가 메모리캐시에 있으면 리턴.
            return this.mCachedScriptMap.get(scriptId);
        } else {
            // 메모리캐시에 스크립트가 없으면, 파일에서 로드.
            String scriptTitle = this.mAllScriptIdsAndTitles.get(scriptId);
            String scriptText = null;
            try {
                scriptText = FileHelper.getInstance().readFile(getParsedScriptLocation(), scriptTitle);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            Script script = Parsor.parse(scriptText);
            // 메모리캐시에 추가
            this.mCachedScriptMap.put( scriptId, script );
            Log.i("!!!!!!!!!!!!!!", "parsedScripts. scriptTitle:" + scriptTitle + ",map: " + script.toString());
            return script;
        }
    }

    public byte[] loadPDF( String filepath, String filename )
    {
        // TODO check if is pdf by filename.

        byte[] pdfFile = FileHelper.getInstance().readBinary( filepath, filename );

        System.out.println("[TEST pdffile] filename:" + filename + ", filepath:" +filepath
                + ", pdfsize:" + pdfFile.length +", pdf:" + pdfFile);
        return pdfFile;
    }

    public void addScript( Integer userId, String pdfFilePath, String pdfFileName,
                           final ScriptRepository.ParseScriptCallback callback )
    {
        byte[] pdfFile = loadPDF( pdfFilePath, pdfFileName );

        Request request = new Request( EProtocol2.PID.ParsedSciprt );
        request.set( EProtocol.UserID, userId );
        request.set( EProtocol.ScriptTitle, pdfFileName);
        request.set( EProtocol.SciprtPDF, pdfFile);
        AsyncNet net = new AsyncNet( request, onParseScript(callback) );
        net.execute();
    }

    private AsyncNet.Callback onParseScript(final ScriptRepository.ParseScriptCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    Map parsedScriptMap = (Map) response.get(EProtocol.ParsedSciprt);
                    Script script = saveParsedScript( parsedScriptMap );
                    callback.onSuccess();
                } else {
                    // 서버 응답 에러
                    Log.e("AppContent", "CheckExistUserProtocol() UnkownERROR : "+ response.getResultCodeString());
                    callback.onFail( response.getResultCode() );
                }
            }
        };
    }

    private Script saveParsedScript( Map scriptMap ) {
        Script newScript = new Script( scriptMap );
        if( newScript == null ) {
            Log.e("AppContent", "script is null");
            return null;
        }

        // 메모리 맵에 저장
        if( hasCachedScript(newScript.scriptId)
                || hasScript(newScript.title) ) {
            Log.e("AppContent", "already exist script [" +newScript.title+ "]" +
                    "mCachedScriptMap in " + hasCachedScript(newScript.scriptId) +
                    ", mAllScriptIdsAndTitles_ReMap in " + hasScript(newScript.title));
            return null;
        }
        addScriptIdAndTitleInMemory( newScript.scriptId, newScript.title );
        mCachedScriptMap.put( newScript.scriptId, newScript );

        // 오프라인 파일에 저장
        String fileName = newScript.title + ".txt";
        boolean bOK = FileHelper.getInstance().overwrite( ParsedFile_AndroidPath,
                fileName, newScript.toTextFileFormat());
        if( false == bOK ) {
            Log.e("AppContent",
                    "Failed overwrite script into file ["+ newScript.title +"]");
            return null;
        }

        return newScript;
    }

    private void addScriptIdAndTitleInMemory(Integer scriptId, String title ){
        if(hasScript(scriptId) || hasScript(title)){
            Log.e("AppContent", "already exist script [" +scriptId+ ","+title+"]" );
            return;
        }
        mAllScriptIdsAndTitles.put( scriptId, title );
        mAllScriptIdsAndTitles_ReMap.put( title, scriptId );
    }

    // 캐시된 스크립트 중에서 존재여부 체크
    private boolean hasCachedScript(Integer scriptId ){
        return mCachedScriptMap.containsKey(scriptId);
    }

    // 유저가 가진 전체 스크립트 중에서 존재여부 체크
    private boolean hasScript(String title ){
        return mAllScriptIdsAndTitles_ReMap.containsKey(title);
    }
    // 유저가 가진 전체 스크립트 중에서 존재여부 체크
    private boolean hasScript(Integer scriptId ){
        return mAllScriptIdsAndTitles.containsKey(scriptId);
    }

    public boolean checkFileFormat( String filename ) {
        // 옛날 pdf는 (with answers)가 없으므로 체크안함.
        String PDF = ".pdf";
        return filename.contains(PDF);
    }

    private String toStringScriptMap() {
        StringBuffer buf = new StringBuffer();
        buf.append("////////////////// mCachedScriptMap( script conut:"+ mCachedScriptMap.size()+") /////////////////////\n");
        for(Map.Entry<Integer, Script> e : mCachedScriptMap.entrySet()) {
            Integer index = e.getKey();
            Script script = e.getValue();
            buf.append(script.toString() + "\n");
        }
        buf.append("////////////////////////////////////////////////////");
        return buf.toString();
    }
}

 /*
        // parsing script list.  List<HashMap> -> List<Sentence>
        //  : 서버에서 Sentence Object를 json string으로 변환시에, HashMap포맷으로 변환된다.
        List<HashMap> sentencesMap = (List<HashMap>) response.toInt(EProtocol.ParsedSciprt);
        List<Sentence> parsedSentences = new LinkedList<Sentence>();
        if( sentencesMap != null ) {
            for( HashMap sentenceMap : sentencesMap )
            {
                String ko = null;
                String en = null;
                if( sentenceMap.containsKey(Sentence.KOREAN) )
                    ko = (String) sentenceMap.toInt(Sentence.KOREAN);
                if( sentenceMap.containsKey(Sentence.ENGLIST) )
                    en = (String) sentenceMap.toInt(Sentence.ENGLIST);

                Sentence s = new Sentence( ko, en );
                parsedSentences.add( s );
            }
        }

        Integer scriptIndex = (Integer) response.toInt(EProtocol.ScriptId);
        Integer scriptRevision = (Integer) response.toInt(EProtocol.ScriptRevision);
        String scriptTitle = (String) response.toInt(EProtocol.ScriptTitle);
        return new Script( scriptTitle, scriptIndex, scriptRevision, parsedSentences );*/
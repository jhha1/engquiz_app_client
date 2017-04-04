package kr.jhha.engquiz.data.local;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
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
import kr.jhha.engquiz.util.StringHelper;

import static kr.jhha.engquiz.util.FileHelper.ParsedFile_AndroidPath;


/**
 * Created by jhha on 2016-10-14.
 */

public class ScriptRepository {

    public interface ParseScriptCallback {
        void onSuccess( Script parsedScript );
        void onFail( EResultCode resultCode );
    }

    private static final ScriptRepository instance = new ScriptRepository();

    // 유저가 서버로부터 파싱받은 '전체 스크립트'의 id, title 만 가진 리스트.
    private Map<Integer, String> mAllParsedScriptIdsAndTitles = new HashMap<>();
    private Map<String, Integer> mAllParsedScriptIdsAndTitles_ReMap = new HashMap<String, Integer>();

    // '일부 스크립트'들을 로드한 스크립트맵.
    // 파싱된 스크립트 문장들이 들어있다.
    // 사이즈가 있으므로, 전체 스크립트를 로드 안하고
    // 앱 실행중 스크립트의 문장을 읽을 필요가 있을때만 파일로부터 읽어 이 memory map에 캐싱한다.
    private Map<Integer, Script> mCachedParsedScriptMap = new HashMap<>();

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
            addScriptIdAndTitleIntoMemory(scriptId, scriptTitle);
        }
    }

    public String getParsedScriptLocation() {
        return FileHelper.getInstance().getAndroidAbsolutePath(ParsedFile_AndroidPath);
    }

    public String getAbsoluteFilePath( String path ) {
        return FileHelper.getInstance().getAndroidAbsolutePath(path);
    }

    public Integer scriptCount(){
        return mAllParsedScriptIdsAndTitles.size();
    }

    public String getParsedScriptTitleAsId(Integer scriptId ) {
        return mAllParsedScriptIdsAndTitles.get(scriptId);
    }

    public Integer getParsedScriptIdAsTitle(@NonNull String title ) {
        if( mAllParsedScriptIdsAndTitles_ReMap.containsKey(title) ) {
            return mAllParsedScriptIdsAndTitles_ReMap.get(title);
        }
        return -1;
    }

    public String[] getScriptTitleAll(){
        final Map<String, Integer> titleMap = mAllParsedScriptIdsAndTitles_ReMap;
        if( titleMap == null || titleMap.isEmpty() ){
            return null;
        }
        String[] arr = new String[titleMap.size()];
        return titleMap.keySet().toArray(arr);
    }

    public Script getScript( Integer scriptId )
    {
        if (hasCachedParsedScript(scriptId)) {
            // 스크립트가 메모리캐시에 있으면 리턴.
            return this.mCachedParsedScriptMap.get(scriptId);
        } else {
            // 메모리캐시에 스크립트가 없으면, 파일에서 로드.
            Script script = loadParsedTextScript(scriptId);
            if( Script.isNull(script) ) {
                Log.i("!!!!!!!!!!!!!!", "Failed loadScriptFile. scriptId:" + scriptId);
                return null;
            }

            // 메모리캐시에 추가
            this.mCachedParsedScriptMap.put( scriptId, script );
            Log.i("!!!!!!!!!!!!!!", "parsedScripts. scriptTitle:" + script.title + ",map: " + script.toString());
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

    public Script loadParsedTextScript( Integer scriptId ){
        String scriptTitle = this.mAllParsedScriptIdsAndTitles.get(scriptId);

        // load from file
        String scriptFileName = Script.makeFileSavedScriptName(scriptId, scriptTitle);
        if(StringHelper.isNullString(scriptFileName)){
            return null;
        }

        String scriptText = null;
        try {
            final FileHelper file = FileHelper.getInstance();
            final String path = getParsedScriptLocation();
            scriptText = file.readFile(path, scriptFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // create script object
        Script script = new Script();
        script.scriptId = scriptId;
        script.title = scriptTitle;
        script.sentences = Parsor.parse(scriptText);
        return script;
    }

    public void addScript( Integer userId, String pdfFilePath, String pdfFileName,
                           final ScriptRepository.ParseScriptCallback callback )
    {
        // 파싱되어 메모리에 저장된 스크립트가 있다면, 그것을 리턴
        Integer scriptId = getParsedScriptIdAsTitle(pdfFileName);
        if( scriptId > 0 ){
            if( hasCachedParsedScript( scriptId ) ){
                Script script = getScript( scriptId );
                callback.onSuccess(script);
                return;
            }
        }

        // 없다면, PDF원본을 APP에서 로드해 서버로부터 파싱받아 메모리&파일에 저장.
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
                    Script script = saveParsedScriptInLocalspace( parsedScriptMap );
                    callback.onSuccess(script);
                } else {
                    // 서버 응답 에러
                    Log.e("AppContent", "CheckExistUserProtocol() UnkownERROR : "+ response.getResultCodeString());
                    callback.onFail( response.getResultCode() );
                }
            }
        };
    }

    private Script saveParsedScriptInLocalspace(Map scriptMap ) {
        Script newScript = new Script( scriptMap );
        if( Script.isNull(newScript)) {
            Log.e("AppContent", "script is null");
            return null;
        }
        Script.checkScriptID(newScript.scriptId);

        // 메모리 맵에 저장
        addScriptIdAndTitleIntoMemory( newScript.scriptId, newScript.title );
        mCachedParsedScriptMap.put( newScript.scriptId, newScript );

        // 오프라인 파일에 저장
        String fileName = newScript.makeFileSavedScriptName();
        String fileText = newScript.makeFileSavedScriptText();
        boolean bOK = FileHelper.getInstance().overwrite(
                ParsedFile_AndroidPath, fileName, fileText);
        if( false == bOK ) {
            Log.e("AppContent",
                    "Failed overwrite script into file ["+ newScript.title +"]");
            return null;
        }

        return newScript;
    }

    private void addScriptIdAndTitleIntoMemory(Integer scriptId, String title ){
        mAllParsedScriptIdsAndTitles.put( scriptId, title );
        mAllParsedScriptIdsAndTitles_ReMap.put( title, scriptId );
    }

    // 캐시된 스크립트 중에서 존재여부 체크
    private boolean hasCachedParsedScript(Integer scriptId ){
        return mCachedParsedScriptMap.containsKey(scriptId);
    }

    public boolean checkFileFormat( String filename ) {
        // 옛날 pdf는 (with answers)가 없으므로 체크안함.
        String PDF = ".pdf";
        return filename.contains(PDF);
    }

    private String toStringScriptMap() {
        StringBuffer buf = new StringBuffer();
        buf.append("////////////////// mCachedParsedScriptMap( script conut:"+ mCachedParsedScriptMap.size()+") /////////////////////\n");
        for(Map.Entry<Integer, Script> e : mCachedParsedScriptMap.entrySet()) {
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
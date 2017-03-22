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
import kr.jhha.engquiz.util.StringHelper;
import kr.jhha.engquiz.data.remote.EProtocol;
import kr.jhha.engquiz.data.remote.Response;

import static kr.jhha.engquiz.util.FileHelper.ParsedFile_AndroidPath;


/**
 * Created by jhha on 2016-10-14.
 */

public class ScriptRepository {
    private static final ScriptRepository instance = new ScriptRepository();

    private Map<Integer, Script> scriptMap = new HashMap<>();
    private Map<String, Integer> scriptIndexMapByName = new HashMap<String, Integer>();

    private Map<Integer, String> allScriptTitleById = new HashMap<>();

    private ScriptRepository() {
    }

    public static ScriptRepository getInstance() {
        return instance;
    }

    // 파싱된 스크립트를 메모리 맵에 셋팅만 한다.
    // 스크립트 파싱은 Initializer 에서
    public void init(Map<Integer, Script> parsedScripts) {
        if (parsedScripts == null) {
            Log.e("AppContent", "Failed Init ScriptRepository. invalid param. parsedScriptMap is null");
            return;
        }

        // 1. scriptMap 에 셋팅
        this.scriptMap = parsedScripts;

        // 2. scriptIndexMapByName 에 셋팅.
        for (Map.Entry<Integer, Script> e : this.scriptMap.entrySet()) {
            Integer scriptIndex = Integer.parseInt(String.valueOf(e.getKey()));
            Integer key = e.getKey();
            Script script = e.getValue();

            if (scriptIndex < 0) {
                Log.e("AppContent", "Failed Init ScriptRepository. invalid param. scriptIndex[" + scriptIndex + "]");
                return;
            }
            if (script == null || StringHelper.isNullString(script.title)) {
                Log.e("AppContent", "Failed Init ScriptRepository. invalid param. script[" + ((script != null) ? script.toString() : null) + "]");
                return;
            }
            this.scriptIndexMapByName.put(script.title, scriptIndex);
        }

        // log
        Log.i("!!!!!!!!!!!!!!", "ScriptRepository INIT result. " +
                "scriptIndexMapByName [" + scriptIndexMapByName.toString() + "],"
                + " parsedScripts [" + scriptMap.toString() + "]");
    }

    public void init2()
    {
        // fill allScriptTitleById
        List<String> fileNames = FileHelper.getInstance().listFileNames( getParsedScriptLocation() );
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
            this.allScriptTitleById.put(scriptId, scriptTitle);
        }
    }

    public Script getScript( Integer scriptId )
    {
        if (this.scriptMap.containsKey(scriptId)) {
            return this.scriptMap.get(scriptId);
        }

        boolean hasScript = this.allScriptTitleById.containsKey(scriptId);
        if (false == hasScript) {
            Log.e("############", "invalid scriptID[" + scriptId + "]");
            return null;
        }
        String scriptTitle = this.allScriptTitleById.get(scriptId);
        String scriptText = null;
        try {
            scriptText = FileHelper.getInstance().readFile( getParsedScriptLocation(), scriptTitle );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Script script  = Parsor.parse(scriptText);

        this.scriptMap.put( scriptId, script );
        Log.i("!!!!!!!!!!!!!!", "parsedScripts. scriptTitle:" + scriptTitle + ",map: " + script.toString());
        return script;
    }

    public String getParsedScriptLocation() {
        return FileHelper.getInstance().getAndroidAbsolutePath(ParsedFile_AndroidPath);
    }

    public String getScriptTitleAsIndex( Integer index ) {
        if( scriptMap.containsKey(index) ) {
            Script script = scriptMap.get(index);
            if( script != null ) {
                return script.title;
            }
        }
        return new String();
    }

    public Integer getScriptIndexAsTitle(@NonNull String title ) {
        if( scriptIndexMapByName.containsKey(title) ) {
            return scriptIndexMapByName.get(title);
        }
        return -1;
    }

    private Boolean addScript( Script newScript )
    {
        if( newScript == null) {
            Log.e("AppContent", "script is null");
            return false;
        }

        if( scriptMap.containsKey(newScript.index)
                || scriptIndexMapByName.containsKey(newScript.title) ) {
            Log.e("AppContent", "already exist script [" +newScript.title+ "]" +
                    "scriptMap in " + scriptMap.containsKey(newScript.index) +
                    ", scriptIndexMapByName in " + scriptIndexMapByName.containsKey(newScript.title));
            return false;
        }
        scriptMap.put( newScript.index, newScript );
        scriptIndexMapByName.put( newScript.title, newScript.index );
        return true;
    }

    public Boolean replaceScript( Script newScript )
    {
        if (newScript == null) {
            Log.e("AppContent", "script is null");
            return false;
        }

        if( false == scriptMap.containsKey(newScript.index)) {
            Log.e("AppContent", "Failed replace script into map. None exist script [" +newScript.title+ "]" );
            return false;
        }

        Script oldScript = scriptMap.get(newScript.index);
        scriptMap.put(newScript.index, newScript);
        scriptIndexMapByName.remove(oldScript.title);
        scriptIndexMapByName.put(newScript.title, newScript.index);

        // 오프라인 파일에 저장
        String fileName = newScript.title + ".txt";
        boolean bOK = FileHelper.getInstance().overwrite( ParsedFile_AndroidPath,
                fileName, newScript.toTextFileFormat());
        if( false == bOK ) {
            Log.e("AppContent",
                    "Failed overwrite script into file ["+ newScript.title +"]");
            return false;
        }
        return true;
    }

    public boolean checkFileFormat( String filename ) {
        // 옛날 pdf는 (with answers)가 없으므로 체크안함.
        String PDF = ".pdf";
        return filename.contains(PDF);
    }

    public String getAbsoluteFilePath( String path ) {
        return FileHelper.getInstance().getAndroidAbsolutePath(path);
    }

    public byte[] loadPDF( String filepath, String filename )
    {
        // TODO check if is pdf by filename.

        byte[] pdfFile = FileHelper.getInstance().readBinary( filepath, filename );

        System.out.println("[TEST pdffile] filename:" + filename + ", filepath:" +filepath
                + ", pdfsize:" + pdfFile.length +", pdf:" + pdfFile);
        return pdfFile;
    }



    public interface ParseScriptCallback {
        void onSuccess( Script script );
        void onFail( EResultCode resultCode );
    }

    public void addScript( String pdfFilePath, String pdfFileName, final ScriptRepository.ParseScriptCallback callback )
    {
        byte[] pdfFile = loadPDF( pdfFilePath, pdfFileName );

        Request request = new Request( EProtocol2.PID.ParsedSciprt );
        request.set( EProtocol.ScriptTitle, pdfFileName);
        request.set( EProtocol.SciprtPDF, pdfFile);
        AsyncNet net = new AsyncNet( request, onParseScript(callback) );
        net.execute();
    }

    private AsyncNet.Callback onParseScript( final ScriptRepository.ParseScriptCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    Map parsedScriptMap = (Map) response.get(EProtocol.ParsedSciprt);
                    Script script = saveParsedScript( parsedScriptMap );
                    callback.onSuccess( script );
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

        // 스크립트 맵에 삽입
        boolean bOK = addScript( newScript );
        if( false == bOK ) {
            Log.e("AppContent",
                    "Failed put script into map ["+ newScript.title +"]");
            return null;
        }

        // 오프라인 파일에 저장
        String fileName = newScript.title + ".txt";
        bOK = FileHelper.getInstance().overwrite( ParsedFile_AndroidPath,
                fileName, newScript.toTextFileFormat());
        if( false == bOK ) {
            Log.e("AppContent",
                    "Failed overwrite script into file ["+ newScript.title +"]");
            return null;
        }

        return newScript;
    }

    public String[] getScriptTitleAll(){
        return this.scriptIndexMapByName.keySet().toArray( new String[scriptIndexMapByName.size()] );
    }

    private String toStringScriptMap() {
        StringBuffer buf = new StringBuffer();
        buf.append("////////////////// scriptMap( script conut:"+ scriptMap.size()+") /////////////////////\n");
        for(Map.Entry<Integer, Script> e : scriptMap.entrySet()) {
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

        Integer scriptIndex = (Integer) response.toInt(EProtocol.ScriptIndex);
        Integer scriptRevision = (Integer) response.toInt(EProtocol.ScriptRevision);
        String scriptTitle = (String) response.toInt(EProtocol.ScriptTitle);
        return new Script( scriptTitle, scriptIndex, scriptRevision, parsedSentences );*/
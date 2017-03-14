package kr.jhha.engquiz.backend_logic;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.net.EProtocol;
import kr.jhha.engquiz.net.Response;
import kr.jhha.engquiz.net.protocols.GetScriptProtocol;
import kr.jhha.engquiz.net.protocols.ParseScriptProtocol;

import static kr.jhha.engquiz.backend_logic.FileManager.ParsedFile_AndroidPath;


/**
 * Created by jhha on 2016-10-14.
 */

public class ScriptManager {
    private static final ScriptManager instance = new ScriptManager();

    private Map<Integer, Script> scriptMap = new HashMap<>();
    private Map<String, Integer> scriptIndexMapByName = new HashMap<String, Integer>();

    private Map<Integer, String> allScriptTitleById = new HashMap<>();

    private ScriptManager() {
    }

    public static ScriptManager getInstance() {
        return instance;
    }

    // 파싱된 스크립트를 메모리 맵에 셋팅만 한다.
    // 스크립트 파싱은 Initializer 에서
    public void init(Map<Integer, Script> parsedScripts) {
        if (parsedScripts == null) {
            Log.e("AppContent", "Failed Init ScriptManager. invalid param. parsedScriptMap is null");
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
                Log.e("AppContent", "Failed Init ScriptManager. invalid param. scriptIndex[" + scriptIndex + "]");
                return;
            }
            if (script == null || Utils.isNullString(script.title)) {
                Log.e("AppContent", "Failed Init ScriptManager. invalid param. script[" + ((script != null) ? script.toString() : null) + "]");
                return;
            }
            this.scriptIndexMapByName.put(script.title, scriptIndex);
        }

        // log
        Log.i("!!!!!!!!!!!!!!", "ScriptManager INIT result. " +
                "scriptIndexMapByName [" + scriptIndexMapByName.toString() + "],"
                + " parsedScripts [" + scriptMap.toString() + "]");
    }

    public void init2()
    {
        // fill allScriptTitleById
        List<String> fileNames = FileManager.getInstance().listFileNames( getParsedScriptLocation() );
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
        String scriptText = FileManager.getInstance().readFile( getParsedScriptLocation(), scriptTitle );
        Script script  = Parsor.parse(scriptText);

        this.scriptMap.put( scriptId, script );
        Log.i("!!!!!!!!!!!!!!", "parsedScripts. scriptTitle:" + scriptTitle + ",map: " + script.toString());
        return script;
    }

    public String getParsedScriptLocation() {
        return FileManager.getInstance().getAndroidAbsolutePath(ParsedFile_AndroidPath);
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

    public Integer getScriptIndexAsTitle( String title ) {
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
        boolean bOK = FileManager.getInstance().overwrite( ParsedFile_AndroidPath,
                newScript.title, newScript.toTextFileFormat());
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
        return FileManager.getInstance().getAndroidAbsolutePath(path);
    }

    public byte[] loadPDF( String filepath, String filename )
    {
        // TODO check if is pdf by filename.

        byte[] pdfFile = FileManager.getInstance().readBinary( filepath, filename );

        System.out.println("[TEST pdffile] filename:" + filename + ", filepath:" +filepath
                + ", pdfsize:" + pdfFile.length +", pdf:" + pdfFile);
        return pdfFile;
    }

    public Object addScript( String pdfFilepath, String pdfFilename )
    {
        // 서버로 파싱된 스크립트 요청
        // 서버에 있으면 받아옴
        Response response = new GetScriptProtocol( pdfFilename ).callServer();
        Boolean hasParsedScript = (Boolean) response.get(EProtocol.HasParsedScript);
        if( false == hasParsedScript )
        {
            // 서버에 없으면, pdf 파일을 업로드 하여 파싱받아옴
            response = new ParseScriptProtocol( pdfFilepath, pdfFilename ).callServer();
        }
        Map scriptMap = (Map) response.get(EProtocol.ParsedSciprt);
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
        bOK = FileManager.getInstance().overwrite( ParsedFile_AndroidPath,
                newScript.title, newScript.toTextFileFormat());
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


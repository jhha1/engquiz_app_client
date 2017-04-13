package kr.jhha.engquiz.model.local;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.model.remote.AsyncNet;
import kr.jhha.engquiz.model.remote.EProtocol2;
import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.model.remote.ObjectBundle;
import kr.jhha.engquiz.model.remote.Request;
import kr.jhha.engquiz.util.FileHelper;
import kr.jhha.engquiz.util.Parsor;
import kr.jhha.engquiz.model.remote.EProtocol;
import kr.jhha.engquiz.model.remote.Response;
import kr.jhha.engquiz.util.StringHelper;

import static kr.jhha.engquiz.util.FileHelper.ParsedFile_AndroidPath;


/**
 * Created by jhha on 2016-10-14.
 */

public class ScriptRepository {

    public interface GetSentenceListCallback {
        void onSuccess(List<Sentence> sentences);
        void onFail(EResultCode resultCode);
    }

    public interface UpdateSenteceCallback {
        void onSuccess();
        void onFail(EResultCode resultCode);
    }

    public interface ParseScriptCallback {
        void onSuccess(Script parsedScript);
        void onFail(EResultCode resultCode);
    }

    public interface SyncCallback {
        void onSuccess(List<Sentence> sentencesForSync);
        void onFail(EResultCode resultCode);
    }

    public interface SyncFailedCallback {
        void onSuccess();
        void onFail(EResultCode resultCode);
    }

    // 유저가 서버로부터 파싱받은 '전체 스크립트'의 sentenceId, title 만 가진 리스트.
    private Map<Integer, String> mAllParsedScriptIdsAndTitles = new HashMap<>();
    private Map<String, Integer> mAllParsedScriptIdsAndTitles_ReMap = new HashMap<String, Integer>();

    // '일부 스크립트'들을 로드한 스크립트맵.
    // 파싱된 스크립트 문장들이 들어있다.
    // 사이즈가 있으므로, 전체 스크립트를 로드 안하고
    // 앱 실행중 스크립트의 문장을 읽을 필요가 있을때만 파일로부터 읽어 이 memory map에 캐싱한다.
    private Map<Integer, Script> mCachedScriptMap = new HashMap<>();

    class SyncNeededSentenceSummary {
        Integer scriptId = 0;
        Integer senteceId = 0;
    }

    private List<Integer> mSyncNeededSenteceIds = new LinkedList<>();

    private static final ScriptRepository instance = new ScriptRepository();

    private ScriptRepository() {
        initailize();
    }

    public static ScriptRepository getInstance() {
        return instance;
    }

    public void initailize() {
        // 1.  유저가 서버로부터 파싱받은 '전체 스크립트'의 sentenceId, title 만 업로드.
        final FileHelper file = FileHelper.getInstance();
        List<String> fileNames = file.listFileNames(getParsedScriptLocation());
        for (String filename : fileNames) {
            String[] splitTitle = Parsor.splitParsedScriptTitleAndId(filename);
            if (splitTitle == null) {
                Log.e("########", "Failed split script title. " +
                        "but, CONTINUE Upload script titles without this.  " +
                        "title before split(" + filename + ")");
                continue;
            }
            Integer scriptId = Integer.parseInt(splitTitle[0]);
            String scriptTitle = splitTitle[1];
            addScriptIdAndTitleIntoMemory(scriptId, scriptTitle);
        }
    }

    public String getParsedScriptLocation() {
        return FileHelper.getInstance().getAndroidAbsolutePath(ParsedFile_AndroidPath);
    }

    public String getAbsoluteFilePath(String path) {
        return FileHelper.getInstance().getAndroidAbsolutePath(path);
    }

    public Integer scriptCount() {
        return mAllParsedScriptIdsAndTitles.size();
    }

    public String getScriptTitleById(Integer scriptId) {
        return mAllParsedScriptIdsAndTitles.get(scriptId);
    }

    public Integer getScriptIdByTitle(@NonNull String title) {
        if (mAllParsedScriptIdsAndTitles_ReMap.containsKey(title)) {
            return mAllParsedScriptIdsAndTitles_ReMap.get(title);
        }
        return -1;
    }

    public String[] getScriptTitleAll() {
        final Map<String, Integer> titleMap = mAllParsedScriptIdsAndTitles_ReMap;
        if (titleMap == null || titleMap.isEmpty()) {
            return null;
        }
        String[] arr = new String[titleMap.size()];
        return titleMap.keySet().toArray(arr);
    }

    public Integer[] getScriptIdAll() {
        final Map<Integer, String> idMap = mAllParsedScriptIdsAndTitles;
        if (idMap == null || idMap.isEmpty()) {
            return null;
        }
        Integer[] arr = new Integer[idMap.size()];
        return idMap.keySet().toArray(arr);
    }

    public boolean isUserMadeScript( Integer scriptId ){
        if( scriptId >= 10000 ){
            return true;
        }
        return false;
    }

    public List<String> getUserMadeScriptTitleAll() {
        List<String> titles = new LinkedList<>();
        Integer[] scriptIdAll= getScriptIdAll();
        for(Integer scriptId: scriptIdAll){
            if( isUserMadeScript(scriptId) ) {
                String title = getScriptTitleById(scriptId);
                titles.add(title);
            }
        }
        return titles;
    }

    public Script getScript(Integer scriptId) {
        if (hasCachedParsedScript(scriptId)) {
            // 스크립트가 메모리캐시에 있으면 리턴.
            return this.mCachedScriptMap.get(scriptId);
        } else {
            // 메모리캐시에 스크립트가 없으면, 파일에서 로드.
            Script script = loadParsedTextScript(scriptId);
            if (Script.isNull(script)) {
                Log.i("!!!!!!!!!!!!!!", "Failed loadScriptFile. scriptId:" + scriptId);
                return null;
            }

            // 메모리캐시에 추가
            this.mCachedScriptMap.put(scriptId, script);
            Log.i("!!!!!!!!!!!!!!", "parsedScripts. scriptTitle:" + script.title + ",map: " + script.toString());
            return script;
        }
    }

    public byte[] loadPDF(String filepath, String filename) {
        // TODO check if is pdf by filename.

        byte[] pdfFile = FileHelper.getInstance().readBinary(filepath, filename);

        System.out.println("[TEST pdffile] filename:" + filename + ", filepath:" + filepath
                + ", pdfsize:" + pdfFile.length + ", pdf:" + pdfFile);
        return pdfFile;
    }

    public Script loadParsedTextScript(Integer scriptId) {
        String scriptTitle = this.mAllParsedScriptIdsAndTitles.get(scriptId);

        // load from file
        String scriptFileName = Script.makeScriptFileName(scriptId, scriptTitle);
        if (StringHelper.isNull(scriptFileName)) {
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
        script.sentences = Parsor.parse(scriptId, scriptText);
        return script;
    }

    public void addPDFScript(Integer userId, String pdfFilePath, String pdfFileName,
                             final ScriptRepository.ParseScriptCallback callback) {
        // 파싱되어 메모리에 저장된 스크립트가 있다면, 그것을 리턴
        Integer scriptId = getScriptIdByTitle(pdfFileName);
        if (scriptId > 0) {
            if (hasCachedParsedScript(scriptId)) {
                Script script = getScript(scriptId);
                callback.onSuccess(script);
                return;
            }
        }

        // 없다면, PDF원본을 APP에서 로드해 서버로부터 파싱받아 메모리&파일에 저장.
        byte[] pdfFile = loadPDF(pdfFilePath, pdfFileName);

        Request request = new Request(EProtocol2.PID.ParseSciprt);
        request.set(EProtocol.UserID, userId);
        request.set(EProtocol.ScriptTitle, pdfFileName);
        request.set(EProtocol.SciprtPDF, pdfFile);
        AsyncNet net = new AsyncNet(request, onParseScript(callback));
        net.execute();
    }

    private AsyncNet.Callback onParseScript(final ScriptRepository.ParseScriptCallback callback) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    Map parsedScriptMap = (Map) response.get(EProtocol.ParsedSciprt);
                    Script newScript = Script.deserialize(parsedScriptMap);
                    if (Script.isNull(newScript)) {
                        Log.e("AppContent", "script is null");
                        callback.onFail(EResultCode.SYSTEM_ERR);
                        return;
                    }
                    boolean bOK = saveScriptInLocal(newScript);
                    if( false == bOK ){
                        callback.onFail(EResultCode.SYSTEM_ERR);
                        return;
                    }
                    callback.onSuccess(newScript);
                } else {
                    // 서버 응답 에러
                    Log.e("AppContent", "CheckExistUserProtocol() UnkownERROR : " + response.getResultCodeString());
                    callback.onFail(response.getResultCode());
                }
            }
        };
    }

    private boolean saveScriptInLocal(Script script) {
        Script.checkScriptID(script.scriptId);

        // 오프라인 파일에 저장
        String fileName = script.makeScriptFileName();
        String fileText = script.makeScriptFileText();
        boolean bOK = FileHelper.getInstance().overwrite(
                ParsedFile_AndroidPath, fileName, fileText);
        if (false == bOK) {
            Log.e("AppContent",
                    "Failed overwrite script into file [" + script.title + "]");
            return false;
        }

        // 메모리 맵에 저장
        addScriptIdAndTitleIntoMemory(script.scriptId, script.title);
        mCachedScriptMap.put(script.scriptId, script);

        return true;
    }

    private void addScriptIdAndTitleIntoMemory(Integer scriptId, String title) {
        mAllParsedScriptIdsAndTitles.put(scriptId, title);
        mAllParsedScriptIdsAndTitles_ReMap.put(title, scriptId);
    }

    public void addUserCustomScript(Script script){
        saveScriptInLocal(script);
    }

    // 캐시된 스크립트 중에서 존재여부 체크
    private boolean hasCachedParsedScript(Integer scriptId) {
        return mCachedScriptMap.containsKey(scriptId);
    }

    public boolean checkFileFormat(String filename) {
        // 옛날 pdf는 (with answers)가 없으므로 체크안함.
        String PDF = ".pdf";
        return filename.contains(PDF);
    }



    /*
        Sentence
     */
    public void getSentencesByScriptId(Integer scriptId, GetSentenceListCallback callback) {
        Script script = getScript(scriptId);
        if (Script.isNull(script)) {
            Log.e("############", "getSentencesByScriptId() invalid scriptId:" + scriptId);
            callback.onFail(EResultCode.INVALID_ARGUMENT);
            return;
        }

        List<Sentence> sentences = script.sentences;
        if (sentences == null || sentences.isEmpty()) {
            Log.e("############", "getSentencesByScriptId() sentences is null. scriptId:" + scriptId);
            callback.onFail(EResultCode.INVALID_ARGUMENT);
            return;
        }

        callback.onSuccess(sentences);
    }

    public void updateSentence(final Sentence newSentence, final UpdateSenteceCallback callback){
        getSentencesByScriptId(newSentence.scriptId, new ScriptRepository.GetSentenceListCallback(){

            @Override
            public void onSuccess(List<Sentence> sentences) {
                Sentence oldSentence = null;
                for(Sentence sentence : sentences){
                    if(sentence.sentenceId == newSentence.sentenceId){
                        oldSentence = sentence;
                    }
                }
                if( oldSentence == null ) {
                    callback.onFail(EResultCode.NOEXSIT);
                    return;
                }
                sentences.remove(oldSentence);
                sentences.add(newSentence);
                callback.onSuccess();
            }

            @Override
            public void onFail(EResultCode resultCode) {
                callback.onFail(resultCode);
            }
        });
    }

    /*
        Sync
     */
    public void getSentencesForSync(final SyncCallback callback) {
        Request request = new Request(EProtocol2.PID.SYNC);
        AsyncNet net = new AsyncNet(request, onSync(callback));
        net.execute();
    }

    private AsyncNet.Callback onSync(final SyncCallback callback) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    List<String> sentenceBundles = (List) response.get(EProtocol.ScriptSentences);
                    List<Sentence> sentencesForSync = new LinkedList<>();
                    for (String bundleJson : sentenceBundles) {
                        ObjectBundle bundle = new ObjectBundle(bundleJson);
                        Sentence sentence = new Sentence(bundle);
                        sentence.sentenceId = bundle.getInt(Sentence.Field_SENTENCE_ID);
                        sentence.scriptId = bundle.getInt(Sentence.Field_SCRIPT_ID);
                        sentence.textKo = bundle.getString(Sentence.Field_SENTENCE_KO);
                        sentence.textEn = bundle.getString(Sentence.Field_SENTENCE_EN);
                        sentencesForSync.add(sentence);
                        Log.d("SSSSSSSSSSSSSSS", "onSync() mSyncNeededSentences (" + sentence.toString() + ")");
                    }
                    callback.onSuccess(sentencesForSync);
                } else {
                    callback.onFail(response.getResultCode());
                }
            }
        };
    }

    public void sendSyncFailed(List<Integer> updateFailedResult, final SyncFailedCallback callback) {
        Request request = new Request(EProtocol2.PID.Sync_SendResult);
        request.set(EProtocol.SyncResult, updateFailedResult);
        AsyncNet net = new AsyncNet(request, onFailedSync(callback));
        net.execute();
    }

    private AsyncNet.Callback onFailedSync(final SyncFailedCallback callback) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    callback.onSuccess();
                } else {
                    callback.onFail(response.getResultCode());
                }
            }
        };
    }

    public List<Integer> syncClient(List<Sentence> sentencesForSync) {
        List<Integer> updateFailedResult = new LinkedList<>();
        for(Sentence sentence :sentencesForSync)
        {
            boolean bUpdated = syncSentence(sentence);
            if (bUpdated == false) {
                Log.e("AppContent", "getSentencesForSync(). Client Sync Failed. sentenceId:" + sentence.sentenceId);
                updateFailedResult.add(sentence.sentenceId);
            }
        }

        mSyncNeededSenteceIds = updateFailedResult;
        return updateFailedResult;
    }

    public boolean syncSentence(Sentence modifiedSentence){
        Log.i("###############", "Sync. syncSentence() called. modifiedSentence:"+modifiedSentence.toString());

        if( Sentence.isNull(modifiedSentence) ){
            Log.e("###############", "Failed Sync. modifiedSentence is null. ");
            return false;
        }
        Script script = getScript(modifiedSentence.scriptId);
        if( Script.isNull(script) ){
            Log.e("###############", "Failed Sync. Script is null. ");
            return false;
        }

        List<Sentence> sentences = script.sentences;
        if(sentences == null || sentences.isEmpty()){
            Log.e("###############", "Failed Sync. Sentences in Script is null. ");
            return false;
        }

        for(Sentence oldSentence : sentences){
            if(Sentence.isNull(oldSentence)){
                continue;
            }
            // oldSentence.sentenceId == modifiedSentence.sentenceId 가 안먹힘
            boolean bSameSentence = oldSentence.sentenceId.equals(modifiedSentence.sentenceId);
            if(bSameSentence){
                oldSentence.textKo = modifiedSentence.textKo;
                oldSentence.textEn = modifiedSentence.textEn;
                return true;
            }
        }
        return false;
    }

    public Integer getSyncNeededCount() {
        if( mSyncNeededSenteceIds == null
                || mSyncNeededSenteceIds.isEmpty() )
            return 0;

        return mSyncNeededSenteceIds.size();
    }

    public Integer saveSyncNeededSentencesSummary( List sentences )
    {
        if( sentences == null ){
            Log.e("##########", "saveSyncNeededSentencesSummary() sentences is null ");
            return -1;
        }

        for(Object sentenceSummary : sentences ){
            Integer sentenceId = parseSentenceSymmary(sentenceSummary);
            if( sentenceId == null || sentenceId <= 0 ){
                Log.e("##########", "saveSyncNeededSentencesSummary() summary is null ");
                continue;
            }
            this.mSyncNeededSenteceIds.add( sentenceId );
        }
        return getSyncNeededCount();
    }

    private Integer parseSentenceSymmary(Object sentenceSummary ){
        Map<String, Integer> map = (Map)sentenceSummary;
        if(map == null) {
            Log.e("##########", "saveSyncNeededSentencesSummary() sentenceSummary is null ");
            return null;
        }

        if( false == map.containsKey("sentenceId")) {
            Log.e("##########", "saveSyncNeededSentencesSummary() sentenceSummary keys are not exist. map: " + map.toString() );
            return null;
        }
        return map.get("sentenceId");
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

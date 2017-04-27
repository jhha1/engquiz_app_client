package kr.jhha.engquiz.model.local;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
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
import kr.jhha.engquiz.util.ui.MyLog;

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

    public interface DeleteSenteceCallback {
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

    private final static int UPDATE__ONLY_TITLE_ID_MAP = 0;
    private final static int UPDATE__ONLY_CACHED_SCRIPT_MAP = 1;
    private final static int UPDATE__ALL = 2;

    private List<Integer> mSyncNeededSenteceIds = new LinkedList<>();

    private static final ScriptRepository instance = new ScriptRepository();

    private ScriptRepository() {
    }

    public static ScriptRepository getInstance() {
        return instance;
    }

    public boolean initailize()
    {
        // 1.  유저가 서버로부터 파싱받은 '전체 스크립트'의 sentenceId, title 만 업로드.
        final FileHelper file = FileHelper.getInstance();
        boolean bOK = file.makeDirectoryIfNotExist(ParsedFile_AndroidPath);
        if( !bOK ){
            // 파싱된 파일이 저장될 디렉토리 생성에 실패하면 더이상 게임진행이 불가능. 앱 종료..
            MyLog.e("Failed make directory for parsed file... " +
                    "dir (" + ParsedFile_AndroidPath + ")");
            return false;
        }

        List<String> fileNames = file.listFileNames(ParsedFile_AndroidPath);
        if( fileNames == null ){
            // 첨 앱 설치시 등. 저장된 파일이 없는 경우는 에러가 아님.
            fileNames = new ArrayList<>();
        }

        for (String filename : fileNames) {
            String[] splitTitle = Parsor.splitParsedScriptTitleAndId(filename);
            if (splitTitle == null) {
                MyLog.e("Failed split script title. " +
                        "but, CONTINUE Upload script titles without this.  " +
                        "title before split(" + filename + ")");
                continue;
            }
            Integer scriptId = Integer.parseInt(splitTitle[0]);
            String scriptTitle = splitTitle[1];
            bOK = putScriptIntoMemory(UPDATE__ONLY_TITLE_ID_MAP, scriptId, scriptTitle, null);
            if( !bOK ){
                MyLog.e("Failed putScriptIntoMemory. " +
                        "but, CONTINUE Upload script titles without this.  " +
                        "title (" + filename + ")");
            }
        }
        return true;
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
        if( scriptIdAll == null ){
            return titles;
        }

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
                MyLog.e("Failed loadScriptFile. scriptId:" + scriptId);
                return null;
            }

            // 메모리캐시에 추가 (title and id map은 추가안해도됨. 앱 로드시 전체 추가되어있음)
            boolean bOK = putScriptIntoMemory(UPDATE__ONLY_CACHED_SCRIPT_MAP, scriptId, script.title, script);
            if( ! bOK ){
                MyLog.e("Failed loadScriptFile. scriptId:" + scriptId);
                return null;
            }
            MyLog.i("parsedScripts. scriptTitle:" + script.title + ",map: " + script.toString());
            return script;
        }
    }

    public byte[] loadPDF(String filepath, String filename) {
        // TODO check if is pdf by filename.

        byte[] pdfFile = FileHelper.getInstance().readBinary(filepath, filename);

        MyLog.e("[Load PDF] filename:" + filename + ", filepath:" + filepath
                + ", pdfsize:" + pdfFile.length + ", pdf:" + pdfFile);
        return pdfFile;
    }

    public Script loadParsedTextScript(Integer scriptId) {
        String scriptTitle = this.mAllParsedScriptIdsAndTitles.get(scriptId);

        // load from file
        String scriptFileName = Script.makeScriptFileName(scriptId, scriptTitle);
        if (StringHelper.isNull(scriptFileName)) {
            MyLog.e("makeScriptFileName is null. filename("+scriptFileName+")");
            return null;
        }

        final FileHelper file = FileHelper.getInstance();
        final String path = ParsedFile_AndroidPath;
        String scriptText = file.readFile(path, scriptFileName);
        if( StringHelper.isNull(scriptText) ){
            MyLog.e("read file is null. filename("+scriptFileName+")");
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
                        MyLog.e("script is null");
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
                    MyLog.e("CheckExistUserProtocol() UnkownERROR : " + response.getResultCodeString());
                    callback.onFail(response.getResultCode());
                }
            }
        };
    }

    private boolean saveScriptInLocal(Script script) {
        Script.checkScriptID(script.scriptId);

        // 오프라인 파일에 저장
        String fileName = script.makeScriptFileName();
        if(StringHelper.isNull(fileName)) {
            MyLog.e("Failed saveScriptInLocal[" + script.title + "]");
            return false;
        }
        String fileText = script.makeScriptFileText();
        boolean bOK = FileHelper.getInstance().overwrite(
                ParsedFile_AndroidPath, fileName, fileText);
        if (false == bOK) {
            MyLog.e("Failed overwrite script into file [" + script.title + "]");
            return false;
        }

        // 메모리 맵에 저장
        return putScriptIntoMemory(UPDATE__ALL, script.scriptId, script.title, script);
    }

    private boolean putScriptIntoMemory(int addType, Integer scriptId, String title, Script script)
    {
        if(addType == UPDATE__ALL || addType == UPDATE__ONLY_CACHED_SCRIPT_MAP){
            if(Script.isNull(script)){
                MyLog.e("Failed put Script Object Into Memory. script null");
                return false;
            }
        }
        Integer checkedScriptID = (scriptId==null || scriptId<0)?script.scriptId:scriptId;
        if( checkedScriptID == null || checkedScriptID < 0){
            MyLog.e("Failed putScriptIntoMemory. scriptId null");
            return false;
        }
        String checkedScriptTitle = StringHelper.isNull(title)?script.title:title;
        if(StringHelper.isNull(checkedScriptTitle)){
            MyLog.e("Failed putScriptIntoMemory. ScriptTitle null");
            return false;
        }


        switch (addType){
            case UPDATE__ONLY_TITLE_ID_MAP:
                mAllParsedScriptIdsAndTitles.put(checkedScriptID, checkedScriptTitle);
                mAllParsedScriptIdsAndTitles_ReMap.put(checkedScriptTitle, checkedScriptID);
                break;
            case UPDATE__ONLY_CACHED_SCRIPT_MAP:
                mCachedScriptMap.put(checkedScriptID, script);
                break;
            case UPDATE__ALL:
                mAllParsedScriptIdsAndTitles.put(checkedScriptID, checkedScriptTitle);
                mAllParsedScriptIdsAndTitles_ReMap.put(checkedScriptTitle, checkedScriptID);
                mCachedScriptMap.put(checkedScriptID, script);
                break;
            default:
                return false;
        }
        return true;
    }

    private boolean removeScriptFromMemory(int removeType, Integer scriptId, String title)
    {
        if(scriptId==null || scriptId<0){
            MyLog.e("Failed removeScriptFromMemory. scriptId null");
            return false;
        }
        if(removeType == UPDATE__ALL || removeType == UPDATE__ONLY_TITLE_ID_MAP){
            if(StringHelper.isNull(title)) {
                MyLog.e("Failed removeScriptFromMemory. ScriptTitle null");
                return false;
            }
        }

        switch (removeType){
            case UPDATE__ONLY_TITLE_ID_MAP:
                mAllParsedScriptIdsAndTitles.remove(scriptId);
                mAllParsedScriptIdsAndTitles_ReMap.remove(title);
                break;
            case UPDATE__ONLY_CACHED_SCRIPT_MAP:
                mCachedScriptMap.remove(scriptId);
                break;
            case UPDATE__ALL:
                mAllParsedScriptIdsAndTitles.remove(scriptId);
                mAllParsedScriptIdsAndTitles_ReMap.remove(title);
                mCachedScriptMap.remove(scriptId);
                break;
            default:
                return false;
        }
        return true;
    }

    public void addUserCustomScript(Script script){
        saveScriptInLocal(script);
    }

    public boolean deleteUserCustomScriptPermenantly(Integer scriptId){
        Script script = getScript(scriptId);
        if( script == null){
            MyLog.e("Failed deleteFile[ Script is null]");
            return true;
        }
        String fileName = script.makeScriptFileName();
        if(StringHelper.isNull(fileName)) {
            MyLog.e("Failed deleteFile[" + script.title + "]");
            return false;
        }
        final FileHelper fileHelper = FileHelper.getInstance();
        boolean bOK = fileHelper.deleteFile(ParsedFile_AndroidPath, fileName);
        if (false == bOK) {
            MyLog.e("Failed deleteFile[" + script.title + "]");
            return false;
        }

        return removeScriptFromMemory(UPDATE__ALL, scriptId, script.title);
    }

    // 캐시된 스크립트 중에서 존재여부 체크
    private boolean hasCachedParsedScript(Integer scriptId) {
        return mCachedScriptMap.containsKey(scriptId);
    }

    public boolean checkFileFormat(String filename) {
        if( StringHelper.isNull(filename) ){
            MyLog.e("checkFileFormat() filename is null");
            return false;
        }
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
            MyLog.e("getSentencesByScriptId() invalid scriptId:" + scriptId);
            callback.onFail(EResultCode.INVALID_ARGUMENT);
            return;
        }

        List<Sentence> sentences = script.sentences;
        if (sentences == null || sentences.isEmpty()) {
            MyLog.e("getSentencesByScriptId() sentences is null. scriptId:" + scriptId);
            callback.onFail(EResultCode.INVALID_ARGUMENT);
            return;
        }

        callback.onSuccess(sentences);
    }

    public void updateSentence(final Sentence newSentence, final UpdateSenteceCallback callback)
    {
        if( Sentence.isNull( newSentence )){
            MyLog.e("updateSentence() newSentence is null");
            callback.onFail(EResultCode.INVALID_ARGUMENT);
            return;
        }

        int scriptId = newSentence.scriptId;
        Script script = getScript(scriptId);
        if (Script.isNull(script)) {
            MyLog.e("updateSentence() invalid scriptId:" + scriptId);
            callback.onFail(EResultCode.INVALID_ARGUMENT);
            return;
        }

        List<Sentence> sentences = script.sentences;
        if (sentences == null || sentences.isEmpty()) {
            MyLog.e("updateSentence() sentences is null. scriptId:" + scriptId);
            callback.onFail(EResultCode.INVALID_ARGUMENT);
            return;
        }

        // Update sentence
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
        saveScriptInLocal(script);

        callback.onSuccess();
    }

    public void deleteSentence(final Sentence deleteSentence, final DeleteSenteceCallback callback)
    {
        if( Sentence.isNull( deleteSentence )){
            MyLog.e("updateSentence() newSentence is null");
            callback.onFail(EResultCode.INVALID_ARGUMENT);
            return;
        }

        int scriptId = deleteSentence.scriptId;
        Script script = getScript(scriptId);
        if (Script.isNull(script)) {
            MyLog.e("updateSentence() invalid scriptId:" + scriptId);
            callback.onFail(EResultCode.INVALID_ARGUMENT);
            return;
        }

        List<Sentence> sentences = script.sentences;
        if (sentences == null || sentences.isEmpty()) {
            MyLog.e("updateSentence() sentences is null. scriptId:" + scriptId);
            callback.onFail(EResultCode.INVALID_ARGUMENT);
            return;
        }

        // Update sentence
        Sentence targetSentence = null;
        for(Sentence sentence : sentences){
            if(sentence.sentenceId == deleteSentence.sentenceId){
                targetSentence = sentence;
            }
        }
        if( targetSentence == null ) {
            callback.onFail(EResultCode.NOEXSIT);
            return;
        }

        sentences.remove(targetSentence);
        saveScriptInLocal(script);
        callback.onSuccess();
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
                        MyLog.d("onSync() mSyncNeededSentences (" + sentence.toString() + ")");
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
        MyLog.i("Sync. syncSentence() called. modifiedSentence:"+modifiedSentence.toString());

        if( Sentence.isNull(modifiedSentence) ){
            MyLog.e("Failed Sync. modifiedSentence is null. ");
            return false;
        }
        Script script = getScript(modifiedSentence.scriptId);
        if( Script.isNull(script) ){
            MyLog.e("Failed Sync. Script is null. ");
            return false;
        }

        List<Sentence> sentences = script.sentences;
        if(sentences == null || sentences.isEmpty()){
            MyLog.e("Failed Sync. Sentences in Script is null. ");
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
            MyLog.e("saveSyncNeededSentencesSummary() sentences is null ");
            return -1;
        }

        for(Object sentenceSummary : sentences ){
            Integer sentenceId = parseSentenceSymmary(sentenceSummary);
            if( sentenceId == null || sentenceId <= 0 ){
                MyLog.e("saveSyncNeededSentencesSummary() summary is null ");
                continue;
            }
            this.mSyncNeededSenteceIds.add( sentenceId );
        }
        return getSyncNeededCount();
    }

    private Integer parseSentenceSymmary(Object sentenceSummary ){
        Map<String, Integer> map = (Map)sentenceSummary;
        if(map == null) {
            MyLog.e("saveSyncNeededSentencesSummary() sentenceSummary is null ");
            return null;
        }

        if( false == map.containsKey("sentenceId")) {
            MyLog.e("saveSyncNeededSentencesSummary() sentenceSummary keys are not exist. map: " + map.toString() );
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

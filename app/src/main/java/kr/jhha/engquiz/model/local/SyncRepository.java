package kr.jhha.engquiz.model.local;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.model.remote.AsyncNet;
import kr.jhha.engquiz.model.remote.EProtocol;
import kr.jhha.engquiz.model.remote.EProtocol2;
import kr.jhha.engquiz.model.remote.ObjectBundle;
import kr.jhha.engquiz.model.remote.Request;
import kr.jhha.engquiz.model.remote.Response;
import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.ui.MyLog;


/**
 * Created by jhha on 2016-10-14.
 */

public class SyncRepository {

    public interface SyncCallback {
        void onSuccess(List<Sentence> sentencesForSync);
        void onFail(EResultCode resultCode);
    }

    public interface SyncFailedCallback {
        void onSuccess();
        void onFail(EResultCode resultCode);
    }

    private List<Integer> mSyncNeededSenteceIds = new LinkedList<>();

    private final ScriptRepository mScriptRepository = ScriptRepository.getInstance();
    private static final SyncRepository instance = new SyncRepository();
    private SyncRepository() {}
    public static SyncRepository getInstance() {
        return instance;
    }

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
                        Sentence sentence = new Sentence();
                        sentence.sentenceId = bundle.getInt(Sentence.Field_SENTENCE_ID);
                        sentence.scriptId = bundle.getInt(Sentence.Field_SCRIPT_ID);
                        sentence.textKo = bundle.getString(Sentence.Field_SENTENCE_KO);
                        sentence.textEn = bundle.getString(Sentence.Field_SENTENCE_EN);
                        sentence.type = Sentence.TYPE.REGULAR;
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
        List<Integer> needUpdateScriptIDs = new LinkedList<>();

        // update memory
        for(Sentence sentenceNoFilledAllDatas :sentencesForSync)
        {
            Integer scriptId = sentenceNoFilledAllDatas.scriptId;
            Integer sentenceId = sentenceNoFilledAllDatas.sentenceId;

            // sentenceNoFilledAllDatas 에는 Sentence Object가 가져야 할 일부값만 있으므로,
            // 모든데이터가 저장된 원래 Sentence Object를 가져와 업데이트된 문장만 바꿔치기한다.
            Sentence originSentence = mScriptRepository.getSentence(scriptId, sentenceId);
            originSentence.textKo = sentenceNoFilledAllDatas.textKo;
            originSentence.textEn = sentenceNoFilledAllDatas.textEn;

            boolean bUpdated = mScriptRepository.updateSentenceOnlyMemory( originSentence );
            if (bUpdated == false) {
                MyLog.e("syncClient(). Client Sync Failed. sentenceId:" + sentenceId);
                updateFailedResult.add(sentenceId);
            } else {
                needUpdateScriptIDs.add(scriptId);
            }
        }

        // update file
        for( Integer scriptId : needUpdateScriptIDs ){
            mScriptRepository.saveScript( mScriptRepository.getScript(scriptId) );
        }

        mSyncNeededSenteceIds = updateFailedResult;
        return updateFailedResult;
    }

    public boolean syncSentence(Sentence modifiedSentence){
        boolean bOk = mScriptRepository.updateSentenceOnlyMemory(modifiedSentence);
        if( ! bOk ){
            return false;
        }

        if( Sentence.isNull(modifiedSentence) ){
            MyLog.e("Failed Sync. modifiedSentence is null. ");
            return false;
        }
        Script script = mScriptRepository.getScript(modifiedSentence.scriptId);
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

}

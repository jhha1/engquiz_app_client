package kr.jhha.engquiz.data.local;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kr.jhha.engquiz.data.local.Sentence;
import kr.jhha.engquiz.data.remote.AsyncNet;
import kr.jhha.engquiz.data.remote.EProtocol;
import kr.jhha.engquiz.data.remote.EProtocol2;
import kr.jhha.engquiz.data.remote.EResultCode;
import kr.jhha.engquiz.data.remote.Request;
import kr.jhha.engquiz.data.remote.Response;

/**
 * Created by thyone on 2017-03-19.
 */

public class SyncModel {

    public interface SyncCallback {
        void onSuccess( Integer userId, List sentencesForSync );
        void onFail( EResultCode resultCode );
    }

    class SyncNeededSentenceSummary {
        Integer scriptId = 0;
        Integer senteceId = 0;
    }
    private List<SyncNeededSentenceSummary> mSyncNeededSentecesSummary = new LinkedList<>();
    private List<Sentence> mSyncNeededSentences = new LinkedList<>();

    private static SyncModel instance = new SyncModel();
    private SyncModel() {}
    public static SyncModel getInstance() {
        return instance;
    }

    public void sync( Integer userId, final SyncCallback callback  ) {
        Request request = new Request( EProtocol2.PID.SYNC );
        request.set(EProtocol.UserID, userId);
        AsyncNet net = new AsyncNet( request, onSync(callback) );
        net.execute();
    }

    private AsyncNet.Callback onSync( final SyncCallback callback ) {
        return new AsyncNet.Callback() {
            @Override
            public void onResponse(Response response) {
                if (response.isSuccess()) {
                    Integer userID = (Integer) response.get(EProtocol.UserID);
                    List sentencesForSync = (List) response.get(EProtocol.ScriptSentences);
                    Log.d("SSSSSSSSSSSSSSS", "onSync() mSyncNeededSentences ("+ sentencesForSync.toString() +")");
                    saveSyncNeededSentences( sentencesForSync );
                    callback.onSuccess( userID, sentencesForSync );
                } else {
                    callback.onFail( response.getResultCode() );
                }
            }
        };
    }

    private void saveSyncNeededSentences( List sentences ) {

    }

    public Integer getSyncNeededCount() {
        if( mSyncNeededSentecesSummary == null
                || mSyncNeededSentecesSummary.isEmpty() )
            return 0;

        return mSyncNeededSentecesSummary.size();
    }

    public Integer saveSyncNeededSentencesSummary( List sentences )
    {
        if( sentences == null ){
            Log.e("##########", "saveSyncNeededSentencesSummary() sentences is null ");
            return -1;
        }

        for(Object sentenceSummary : sentences ){
            SyncNeededSentenceSummary summary = parseSentenceSymmary(sentenceSummary);
            if( summary == null ){
                Log.e("##########", "saveSyncNeededSentencesSummary() summary is null ");
                continue;
            }

            this.mSyncNeededSentecesSummary.add( summary );
        }
        return getSyncNeededCount();
    }

    private SyncNeededSentenceSummary parseSentenceSymmary( Object sentenceSummary ){
        Map<String, Integer> map = (Map)sentenceSummary;
        if(map == null) {
            Log.e("##########", "saveSyncNeededSentencesSummary() sentenceSummary is null ");
            return null;
        }

        SyncNeededSentenceSummary summary = new SyncNeededSentenceSummary();
        if( ! map.containsKey("scriptId")
                || ! map.containsKey("sentenceId")) {
            Log.e("##########", "saveSyncNeededSentencesSummary() sentenceSummary keys are not exist. map: " + map.toString() );
            return null;
        }

        summary.scriptId = map.get("scriptId");
        summary.senteceId = map.get("sentenceId");
        if( summary.scriptId == null
                || summary.scriptId <= 0
                || summary.senteceId == null
                || summary.senteceId <= 0 ) {
            Log.e("##########", "saveSyncNeededSentencesSummary() invalid sentenceSummary values. map: " + map.toString() );
            return null;
        }
        return summary;
    }
}

package kr.jhha.engquiz.presenter_view.sync;

import android.content.Context;
import android.util.Log;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.model.local.Sentence;
import kr.jhha.engquiz.presenter_view.MyNavigationView;
import kr.jhha.engquiz.util.exception.EResultCode;
import kr.jhha.engquiz.util.ui.MyLog;

/**
 * Created by thyone on 2017-03-15.
 */

public class SyncPresenter implements SyncContract.UserActionsListener {

    private final SyncContract.View mView;
    private ScriptRepository mModel;
    private Context mContext;

    public SyncPresenter( Context context, SyncContract.View view, ScriptRepository model ) {
        mView = view;
        mModel = model;
        mContext = context;
    }

    public void initView(){
        Integer count = mModel.getSyncNeededCount();
        if( count > 0 ){
            float sizeMB = (float)count * 0.01f;
            sizeMB = Math.round(sizeMB*100f) / 100f;
            mView.onSyncReady(sizeMB);
        } else {
            mView.onSynced();
        }
    }

    @Override
    public void sync() {
        mModel.getSentencesForSync( onSyncCallback() );
    }

    private ScriptRepository.SyncCallback onSyncCallback() {
        return new ScriptRepository.SyncCallback(){

            @Override
            public void onSuccess( List<Sentence> sentencesForSync ) {
                MyLog.d( "getSentencesForSync().onSuccess() sentencesForSyncCount:" + sentencesForSync.size());

                List<Integer> updateFailedResult = mModel.syncClient(sentencesForSync);
                if(false == updateFailedResult.isEmpty()){
                    sendSyncFailed(updateFailedResult);
                    String msg = "동기화에 실패 했습니다. \n 앱 재시작 후 다시 시도해주시기 바랍니다.";
                    mView.onFailedSync(msg);
                } else {
                    final MyNavigationView navigationView = MyNavigationView.getInstance();
                    navigationView.detachAlarmIcon(R.id.nav_sync);
                    mView.onSynced();
                }
            }

            @Override
            public void onFail(EResultCode resultCode) {
                MyLog.e("sync().onFail() UnkownERROR. resultCode: " + resultCode);
                String msg = "동기화에 실패 했습니다. \n 앱 재시작 후 다시 시도해주시기 바랍니다.";
                mView.onFailedSync(msg);
            }
        };
    }

    private void sendSyncFailed(  List<Integer> updateFailedResult ) {
        mModel.sendSyncFailed(updateFailedResult, onSyncFailedCallback());
    }

    private ScriptRepository.SyncFailedCallback onSyncFailedCallback() {
        return new ScriptRepository.SyncFailedCallback(){

            @Override
            public void onSuccess() {
                MyLog.d( "onSyncFailedCallback().onSuccess() ");
                String msg = "일부 동기화에 실패 했습니다. \n 앱 재시작 후 다시 동기화 받으실 수 있습니다.";
                mView.onFailedSync(msg);
            }

            @Override
            public void onFail(EResultCode resultCode) {
                MyLog.e("onSyncFailedCallback().onFail() resultCode: " + resultCode);
                String msg = "일부 동기화에 실패 했습니다.";
                mView.onFailedSync(msg);
            }
        };
    }
}

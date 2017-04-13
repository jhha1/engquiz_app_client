package kr.jhha.engquiz.presenter_view.sync;

import android.content.Context;
import android.util.Log;

import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.model.local.ScriptRepository;
import kr.jhha.engquiz.model.local.Sentence;
import kr.jhha.engquiz.presenter_view.MainActivity;
import kr.jhha.engquiz.util.exception.EResultCode;

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
            String msg = "총 " + sizeMB + "MB의 데이터를 다운로드 합니다."
                        +"\n Wifi 접속이 아닐경우 데이터 요금이 부과됩니다."
                        + " \n계속하시겠습니까?";
            mView.showView(msg);
        } else {
            String msg = "모든 스크립트가 최신입니다.";
            mView.showView(msg);
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
                Log.i("AppContent", "getSentencesForSync().onSuccess() sentencesForSyncCount:" + sentencesForSync.size());

                List<Integer> updateFailedResult = mModel.syncClient(sentencesForSync);
                if(false == updateFailedResult.isEmpty()){
                    sendSyncFailed(updateFailedResult);
                    String msg = "동기화에 실패 했습니다. \n 앱 재시작 후 다시 시도해주시기 바랍니다.";
                    mView.showView(msg);
                } else {
                    ((MainActivity)mContext).changeDrawerHamburgerIcon("normal");
                    ((MainActivity)mContext).changeDrawerMenuIcon(R.id.nav_sync, R.drawable.ic_nav_menu__sync_white_24dp);
                    mView.onSuccessSync();
                }
            }

            @Override
            public void onFail(EResultCode resultCode) {
                Log.e("AppContent", "sync().onFail() UnkownERROR. resultCode: " + resultCode);
                String msg = "동기화에 실패 했습니다. \n 앱 재시작 후 다시 시도해주시기 바랍니다.";
                mView.showView(msg);
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
                Log.i("AppContent", "onSyncFailedCallback().onSuccess() ");
                String msg = "일부 동기화에 실패 했습니다. \n 앱 재시작 후 다시 동기화 받으실 수 있습니다.";
                mView.onFailedSync(msg);
            }

            @Override
            public void onFail(EResultCode resultCode) {
                Log.e("AppContent", "onSyncFailedCallback().onFail() resultCode: " + resultCode);
                String msg = "일부 동기화에 실패 했습니다.";
                mView.onFailedSync(msg);
            }
        };
    }
}

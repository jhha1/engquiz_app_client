package kr.jhha.engquiz.sync;

import android.util.Log;

import java.util.List;

import kr.jhha.engquiz.data.local.SyncModel;
import kr.jhha.engquiz.data.remote.EResultCode;
import kr.jhha.engquiz.data.local.UserModel;

/**
 * Created by thyone on 2017-03-15.
 */

public class SyncPresenter implements SyncContract.UserActionsListener {

    private final SyncContract.View mView;
    private SyncModel mModel;

    public SyncPresenter( SyncContract.View view, SyncModel model ) {
        mView = view;
        mModel = model;
    }

    @Override
    public void sync() {
        Integer userId = UserModel.getInstance().getUserID();
        mModel.sync( userId, onSyncCallback(userId) );
    }

    private SyncModel.SyncCallback onSyncCallback(final Integer userId ) {
        return new SyncModel.SyncCallback(){

            @Override
            public void onSuccess( Integer userId, List sentencesForSync ) {
                Log.i("AppContent", "onSuccess()  userId: " + userId + ", sentencesForSync:" + sentencesForSync.toString());

            }

            @Override
            public void onFail(EResultCode resultCode) {
                Log.e("AppContent", "onFail() UnkownERROR. resultCode: " + resultCode);
                // TODO 실패 메세지
            }
        };
    }

}

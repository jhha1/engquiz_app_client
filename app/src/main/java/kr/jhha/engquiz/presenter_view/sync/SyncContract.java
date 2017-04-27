package kr.jhha.engquiz.presenter_view.sync;

/**
 * Created by thyone on 2017-03-15.
 */

public class SyncContract {

    interface View {
        void onSyncReady(float sizeMB);
        void onSynced();
        void onFailedSync(String msg);
    }

    interface UserActionsListener {
        void initView();
        void sync();
    }
}

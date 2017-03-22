package kr.jhha.engquiz.sync;

/**
 * Created by thyone on 2017-03-15.
 */

public class SyncContract {

    interface View {
        void onDrawSyncNeededImg();
    }

    interface UserActionsListener {
        void sync();
    }
}

package kr.jhha.engquiz.presenter_view.sync;

/**
 * Created by thyone on 2017-03-15.
 */

public class SyncContract {

    interface View {
        void onSuccessSync();
        void onFailedSync(String msg);

        void showView(String msg);
    }

    interface UserActionsListener {
        void initView();
        void sync();
    }
}

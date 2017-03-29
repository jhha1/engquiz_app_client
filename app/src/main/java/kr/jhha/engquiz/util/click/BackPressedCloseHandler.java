package kr.jhha.engquiz.util.click;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by thyone on 2017-03-30.
 */

public class BackPressedCloseHandler implements ClickDetector {

    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressedCloseHandler(Activity context) {
        this.activity = context;
    }

    @Override
    public void onClick(int arg) {
        onBackPressed();
    }

    private void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            SystemExit();
        }
    }

    private void SystemExit() {
        activity.moveTaskToBack(true);
        activity.finish();
        toast.cancel();
        android.os.Process.killProcess(android.os.Process.myPid() );
        System.exit(0);
    }

    private void showGuide() {
        toast = Toast.makeText(activity,
                "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}


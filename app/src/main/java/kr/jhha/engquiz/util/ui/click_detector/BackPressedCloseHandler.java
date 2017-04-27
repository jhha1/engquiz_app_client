package kr.jhha.engquiz.util.ui.click_detector;

import android.app.Activity;
import android.widget.Toast;

import kr.jhha.engquiz.presenter_view.FragmentHandler;
import kr.jhha.engquiz.util.ui.MyLog;

/**
 * Created by thyone on 2017-03-30.
 */

public class BackPressedCloseHandler implements ClickDetector {

    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;
    private FragmentHandler fragmentHandler;

    public BackPressedCloseHandler(Activity context) {
        this.activity = context;
        fragmentHandler = FragmentHandler.getInstance();
    }

    @Override
    public void onClick(int arg) {
        onBackPressed();
    }

    private void onBackPressed() {
        fragmentHandler.onBackPressed();

        boolean firstClicked = System.currentTimeMillis() > backKeyPressedTime + 2000;
        if (firstClicked) {
            if( isEndPointFragment() ) {
                backKeyPressedTime = System.currentTimeMillis();
                showGuide();
            }
            return;
        }

        boolean doubleClicked = System.currentTimeMillis() <= backKeyPressedTime + 2000;
       if (doubleClicked) {
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

    private boolean isEndPointFragment(){
        boolean bEndpoint = false;
        switch (fragmentHandler.getCurrentFragmentID()){
            case NONE:
            case INTRO:
            case PLAYQUIZ:
                bEndpoint = true;
                break;
            default:
                bEndpoint = false;
                break;
        }
        //MyLog.d("fragmentID: "+fragmentHandler.getCurrentFragmentID().toString()+",bEndpoint:"+bEndpoint);
        return bEndpoint;
    }

    private void showGuide() {
        toast = Toast.makeText(activity,
                "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}


package kr.jhha.engquiz.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;

/**
 * 1. StrictMode?
 *
 * Main Thread 에서 사용성을 떨어뜨리는 작업들( 대표적으로 IO )을 하지 않도록
 * 개발자에게 Log, 강제 종료, dropbox, dialog, splash 등의 방법으로 알려주는 API 이다.
 *
 * GingerBread 부터 소개되었다.

 * IO 작업 중에서도 file, network access 는 특히 위험하다.
 * 그 이유는 android file system ( YAFFS, Yet Another Flash File System ) 의 경우
 * 한 process 가 해당 파일에 접속하면, 다른 process 가 접근하지 못하도록 lock 이 걸려버린다.
 * 따라서 여러 process 가 동시에 한 파일에 접근한다면 대기시간은 엄청나게 길어질 수 있다.
 * Network 의 경우 예측 불가능한 환경이 너무 빈번하게 일어나기 때문에 매우 위험하며,
 * ICS 부터는 Network와 File 작업을 Main thread 에서 하게 되면 무조건 Exception 을 던지도록(강제종료) 기본 설정되어있다.
 *
 *
 * 2. 가장 좋은 시험 방법을 알려줘!

 panaltyDropBox() 의 경우 다음 명령어를 통해 txt 파일로 뽑아낼 수 있고, 정보가 가장 detail 하게 나오는 로그의 종류이다.
 adb shell dumpsys dropbox data_app_strictmode –print > strict.txt
 Strict Mode 를 Thread 와 VM 의 detectAll() 선언하고, penaltyDropBox() 를 output 으로 세팅한 후, Monkey Test 를 돌리면 어느 정도 확실한 테스트가 될 것으로 보인다.

 출처: http://aroundck.tistory.com/2074 [돼지왕 왕돼지 놀이터]

 */

public class MyStrictMode
{
    private static final MyStrictMode singletonInstance = new MyStrictMode();

    public static final boolean SUPPORT_STRICT_MODE = Build.VERSION_CODES.FROYO < Build.VERSION.SDK_INT;

    private MyStrictMode(){}
    public static MyStrictMode getInstance() {
        return singletonInstance;
    }

    public void turnOn(Context context)
    {
        boolean debaggable = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

        if (SUPPORT_STRICT_MODE && debaggable) {
            StrictMode.ThreadPolicy.Builder builder = new StrictMode.ThreadPolicy.Builder();
            builder.detectCustomSlowCalls(); // api level 11
            builder.detectNetwork();
            builder.detectDiskReads();
            builder.detectDiskWrites();
            builder.detectNetwork();

            builder.penaltyLog();
            builder.penaltyDropBox();
            builder.penaltyDialog();
            builder.penaltyDeath();
            builder.penaltyDeathOnNetwork(); // api level 11, detectNetwork() has to be called in advance.
            builder.penaltyFlashScreen(); // api level 11, red rectangle will be shown around the window.

            StrictMode.setThreadPolicy(builder.build());

            /*
            detectAll() ?
             DETECT_VM_ACTIVITY_LEAKS | DETECT_VM_CURSOR_LEAKS
                        | DETECT_VM_CLOSABLE_LEAKS | DETECT_VM_REGISTRATION_LEAKS
                        | DETECT_VM_FILE_URI_EXPOSURE;
             */
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
    }
}

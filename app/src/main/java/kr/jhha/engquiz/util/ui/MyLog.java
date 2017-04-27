package kr.jhha.engquiz.util.ui;

import android.util.Log;

/**
 * Created by thyone on 2017-04-26.
 */

public final class MyLog {

    private static final String TAG = "App";

    public static void e( String msg ) {
        Log.e(TAG, buildLogMsg(msg));
    }

    public static void w( String msg ) {
        Log.w(TAG, buildLogMsg(msg));
    }

    public static void i( String msg ) {
        Log.i(TAG, buildLogMsg(msg));
    }

    public static void d( String msg ) {
        Log.d(TAG, buildLogMsg(msg));
    }

    private static String buildLogMsg(String message) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];
        StringBuilder sb = new StringBuilder();
        sb.append(ste.getFileName().replace(".java", ""));
        sb.append(".");
        sb.append(ste.getMethodName());
        sb.append("(");
        sb.append(ste.getLineNumber());
        sb.append(") ");
        sb.append(message);
        return sb.toString();
    }

}

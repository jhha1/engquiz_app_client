package kr.jhha.engquiz.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import kr.jhha.engquiz.R;
import kr.jhha.engquiz.presenter_view.MainActivity;
import kr.jhha.engquiz.util.ui.MyDialog;

/**
 * Created by thyone on 2017-05-22.
 */

public class PermmissionHelper {

    public static final int PERMISSION_STORAGE = 1;
    private static int mPermissionRefuseCount = 0;

    public static boolean isMaxPermissionRefused(){
        // 한번 이상 거부시에 앱 종료 창으로.
        return (mPermissionRefuseCount >= 1);
    }

    public static void increasePermissionRefuseCount(){
        ++mPermissionRefuseCount;
    }

    public static boolean checkAndPermission(final Fragment fragment, final int permissionRequestCode, String... permissions)
    {
        final String[] requiredPermissions = getRequiredPermissions(fragment.getContext() != null ?
                fragment.getContext() : fragment.getActivity(), permissions);

        return (requiredPermissions.length > 0 && fragment.isAdded());
    }

    public static boolean checkAndRequestPermission(Activity activity, int permissionRequestCode, String... permissions) {
        String[] requiredPermissions = getRequiredPermissions(activity, permissions);

        if (requiredPermissions.length > 0 ) {
            ActivityCompat.requestPermissions(activity, requiredPermissions, permissionRequestCode);
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkAndRequestPermission(final Fragment fragment, final int permissionRequestCode, String... permissions)
    {
        final String[] requiredPermissions = getRequiredPermissions(fragment.getContext() != null ?
                fragment.getContext() : fragment.getActivity(), permissions);

        boolean bPermissionNotAllowed = (requiredPermissions.length > 0 && fragment.isAdded());
        if( bPermissionNotAllowed ){
            showRequestPermissionDialog(fragment.getActivity(), fragment, requiredPermissions, permissionRequestCode);
            return false;
        } else {
            return true;
        }
    }

    public static String[] getRequiredPermissions(Context context, String... permissions) {
        List<String> requiredPermissions = new ArrayList<>();

        // Context가 null이면 무조건 권한을 요청하도록 requiredPermissions가 존재한다고 reutrn 한다
        if (context == null) return requiredPermissions.toArray(new String[1]);

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(permission);
            }
        }

        return requiredPermissions.toArray(new String[requiredPermissions.size()]);
    }

    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) return false;

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    public static void showRequestPermissionDialog(final Context context, final Fragment fragment,
                                                   final String[] requiredPermissions, final int permissionRequestCode) {
        final MyDialog dialog = new MyDialog(context);
        dialog.setTitle("영어문장퀴즈는 [저장공간] 권한을 필요로 해요.");
        dialog.setMessage(context.getString(R.string.permission_request));
        dialog.setPositiveButton("권한 허용",  new View.OnClickListener() {
            public void onClick(View arg0)
            {
                dialog.dismiss(); // 다이알로그 닫기
                fragment.requestPermissions(requiredPermissions, permissionRequestCode);
            }});
        dialog.setNegativeButton( "앱 닫기",  new View.OnClickListener() {
            public void onClick(View arg0)
            {
                dialog.dismiss(); // 다이알로그 닫기
                ((MainActivity)context).finishApp();
            }});
        dialog.showUp();
    }

    public static void showGoToAppSettingForPermissionDialog(final Context context) {
        final MyDialog dialog = new MyDialog(context);
        dialog.setTitle("영어문장퀴즈는 [저장공간] 권한을 필요로 해요.");
        dialog.setMessage(context.getString(R.string.permission_guide_final));
        dialog.setPositiveButton("권한 허용 설정",  new View.OnClickListener() {
            public void onClick(View arg0)
            {
                dialog.dismiss(); // 다이알로그 닫기
                try {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();

                    Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    context.startActivity(intent);
                }
                //((MainActivity)context).finishApp();
            }});
        dialog.setNegativeButton( "앱 닫기",  new View.OnClickListener() {
            public void onClick(View arg0)
            {
                dialog.dismiss(); // 다이알로그 닫기
                ((MainActivity)context).finishApp();
            }});
        dialog.showUp();
    }

}

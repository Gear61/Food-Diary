package com.randomappsinc.foodjournal.utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionUtils {

    public static void requestPermission(Activity activity, String permission) {
        requestPermission(activity, permission, 1);
    }

    public static void requestPermission(Activity activity, String permission, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }

    public static void requestPermission(Fragment fragment, String permission) {
        FragmentCompat.requestPermissions(fragment, new String[]{permission}, 1);
    }

    public static boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(MyApplication.getAppContext(), permission)
                == PackageManager.PERMISSION_GRANTED;
    }
}

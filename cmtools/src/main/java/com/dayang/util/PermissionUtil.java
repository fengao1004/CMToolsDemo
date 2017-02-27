package com.dayang.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.dayang.activity.MediaPlugin;

import java.util.ArrayList;

/**
 * Created by 冯傲 on 2017/2/22.
 * e-mail 897840134@qq.com
 */

public class PermissionUtil {
    public static void checkPermission(String[] permission, Activity activity, final PermissionListener listener, int requestCode) {
        ArrayList<String> permissionList = new ArrayList<>();
        for (int i = 0; i < permission.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, permission[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission[i]);
            }
        }
        if (permissionList.size() == 0) {
            listener.permissionAllowed();
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                listener.permissionRefused();
                return;
            }
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
            final int code = requestCode;
            MediaPlugin.permissionsResultListener = new MediaPlugin.PermissionsResultListener() {
                @Override
                public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                    if (code == requestCode) {
                        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            listener.permissionAllowed();
                        } else {
                            listener.permissionRefused();
                        }
                    }
                }
            };
        }
    }

    public interface PermissionListener {
        void permissionAllowed();

        void permissionRefused();
    }
}


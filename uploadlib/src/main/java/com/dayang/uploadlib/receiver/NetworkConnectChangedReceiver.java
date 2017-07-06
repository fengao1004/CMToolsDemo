package com.dayang.uploadlib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.dayang.uploadlib.UploadFileManager;

/**
 * Created by 冯傲 on 2017/6/2.
 * e-mail 897840134@qq.com
 */

public class NetworkConnectChangedReceiver extends BroadcastReceiver {
    public static final int WIFI = 123;
    public static final int NONE = 0;
    public static final int MOBILE = 121;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            if (activeNetwork != null) {
                if (activeNetwork.isConnected()) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        UploadFileManager.getInstance().networkStateChange(WIFI);
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        UploadFileManager.getInstance().networkStateChange(MOBILE);
                    }
                } else {
                    UploadFileManager.getInstance().networkStateChange(NONE);
                }
            } else {
                UploadFileManager.getInstance().networkStateChange(NONE);
            }
        }
    }
}

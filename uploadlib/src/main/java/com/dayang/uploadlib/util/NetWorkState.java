package com.dayang.uploadlib.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by 冯傲 on 2017/6/2.
 * e-mail 897840134@qq.com
 */

public class NetWorkState {
    public static final int WIFI = 123;
    public static final int NONE = 0;
    public static final int MOBILE = 121;

    public static int GetNetype(Context context) {
        int netType = NONE;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            netType = MOBILE;
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = WIFI;
        }
        return netType;
    }
}

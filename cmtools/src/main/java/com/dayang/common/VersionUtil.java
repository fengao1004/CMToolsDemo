package com.dayang.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by 冯傲 on 2016/9/13.
 * e-mail 897840134@qq.com
 */
public class VersionUtil {

    Context context;
    static String TAG =  Canstant.TAG;
    Handler handler = null;

    public VersionUtil(Context context,Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    public void checkVersion() {
        OkHttpUtil okHttpUtil = new OkHttpUtil();
        okHttpUtil.callGet(Constants.VERSION_URL, new OkHttpUtil.OkHttpCallBack() {
            @Override
            public void success(Response response) throws Exception {
                String version = getVersionName();
                String newVersion = response.body().string();
                if (!newVersion.equals(version)) {
                  handler.sendEmptyMessage(Canstant.NEEDUPDATA);
                }
            }

            @Override
            public void error(Request request, IOException e) {

            }
        });
    }



    private String getVersionName() throws Exception {
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        String version = packInfo.versionName;
        return version;
    }

}

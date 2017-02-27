package com.dayang.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dayang.common.Canstant;
import com.dayang.common.JSONFactory;
import com.dayang.common.OkHttpUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 冯傲 on 2017/1/5.
 * e-mail 897840134@qq.com
 */

public class ApkUpdateUtil {
    public static final String TAG = Canstant.TAG;
    public static final int AUTOUPDATE = 1;
    public static final int NORMALUPDATE = 2;
    private Context context;
    private String checkUrl = "http://appservice.dayang.com/versionupdate/version.json";
    public ApkUpdateUtil(Context context) {
        this.context = context;
    }

    /**
     * @param handler
     * @return 检查当前版本是否是最新版
     */
    public void checkApkVersion(final Handler handler) {
        OkHttpUtil okHttpUtil = new OkHttpUtil();
        okHttpUtil.callGet(checkUrl, new OkHttpUtil.OkHttpCallBack() {
            @Override
            public void success(Response response) throws Exception {
                boolean needUpdate = false;
                String string = response.body().string();
                JSONObject jsonObject = new JSONObject(string);
                String version = jsonObject.get("version").toString();
                String filePath = jsonObject.get("filePath").toString();
                JsonObject jsonArray = JSONFactory.parseJsonStr(string);
                ArrayList<String> updateLog = new ArrayList<>();
                try {
                    JsonArray updateLogStringArray = jsonArray.get("description").getAsJsonArray();
                    for (int i = 0; i < updateLogStringArray.size(); i++) {
                        updateLog.add(updateLogStringArray.get(i).getAsString());
                    }
                } catch (Exception e) {
                    Log.i(TAG, "json_error: " + e.toString());
                }

                String localVersion = getAppVersionName(context);
                Log.i(TAG, "本地版本 " + localVersion);
                Log.i(TAG, "云版本 " + version);
                if (!localVersion.equals("")) {
                    Log.i(TAG, "版本是否相同" + localVersion.equals(version));
                    if (!localVersion.equals(version)) {
                        needUpdate = true;
                    }
                } else {
                    needUpdate = true;
                }
                if (needUpdate) {
                    Message mes = new Message();
                    mes.what = Canstant.NEEDUPDATA;
                    mes.obj = new Object[]{filePath, updateLog};
                    handler.sendMessage(mes);
                } else {
                    Message mes = new Message();
                    mes.what = Canstant.NOTNEEDUPDATA;
                    handler.sendMessage(mes);
                }
            }

            @Override
            public void error(Request request, IOException e) {
                Message mes = new Message();
                mes.what = Canstant.Error;
                handler.sendMessage(mes);
            }
        });
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }
}

package com.dayang.common;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.HBuilder.integrate.SDK_WebApp;
import com.dayang.activity.MediaPlugin;
import com.dayang.uploadlib.UploadFileManager;
import com.dayang.util.SharedPreferencesUtils;
import com.dayang.util.TeamWorkGoUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.quanshi.TangSdkApp;
import com.quanshi.sdk.BaseResp;
import com.quanshi.sdk.ConferenceReq;
import com.quanshi.sdk.TangCallback;
import com.quanshi.sdk.TangInterface;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 冯傲 on 2017/3/1.
 * e-mail 897840134@qq.com
 */

public class CMToolsManager {
    /**
     * 设置媒体文件录制的根目录
     *
     * @param parameter
     * @return
     */
    private static CMToolsManager cMToolsManager;
    private Context context;
    private int mode;
    String data;
    String teamToken;
    String appId;
    public static final int MODE_NORMAL = 124;
    public static final int MODE_WITHOUT_CONFERENCE = 123;

    public void setMediaFileRootPath(String rootPath) {
        MediaPlugin.setMediaFileRootPath(rootPath);
    }

    private CMToolsManager() {

    }

    public static CMToolsManager getInstance() {
        if (cMToolsManager == null) {
            cMToolsManager = new CMToolsManager();
        }
        return cMToolsManager;
    }

    public CMToolsManager init(Context context, int mode) {
        this.mode = mode;
        if (cMToolsManager == null) {
            return null;
        }
        this.context = context;
        MediaPlugin.init();
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(context);
        ImageLoader.getInstance().init(configuration);
        if (mode == MODE_NORMAL) {
            TangSdkApp.initSdk(context);
            TangInterface.initLanguage(context, TangInterface.Language.CHINESE);//会中退会按钮显示英文
            TangInterface.initEnvironment(TangInterface.Environment.ONLINE);
        }
        UploadFileManager.getInstance().init(context);
        return cMToolsManager;
    }

    public void destroy() {
        UploadFileManager.getInstance().destroy();
    }

    public void beforehandLogin(final String teamToken, final String appId) {
        String url = "http://LBAppVerifySrv-1569599441.cn-north-1.elb.amazonaws.com.cn/AppVerifyService/api/queryUserInfoByToken";
        this.teamToken = teamToken;
        this.appId = appId;
        OkHttpUtil okHttpUtil = new OkHttpUtil();
        okHttpUtil.call(url, "{\"teamToken\":\"" + teamToken + "\",\"appId\":\"" + appId + "\"}", new OkHttpUtil.OkHttpCallBack() {
            @Override
            public void success(Response response) throws Exception {
                CMToolsManager.this.data = response.body().string();
                SharedPreferencesUtils.setParam(context, "userInfo", CMToolsManager.this.data);
                SharedPreferencesUtils.setParam(context, "teamToken", teamToken);
                SharedPreferencesUtils.setParam(context, "appId", appId);
                Log.i("fengao", "success: " + data);
                response.body().close();
            }

            @Override
            public void error(Call call, IOException e) {
                Log.i("fengao", "error: " + e.toString());
            }
        });
    }

    public void beforehandLogin() {
        String teamToken = TeamWorkGoUtil.getTeamToken();
        String appId = TeamWorkGoUtil.getAppId();
//        appId = "bd9c3f45c6fdd5524ff6f07f70c4d395";
//        teamToken = "8b527227aa0e1ce947d6b92e3175861";
        beforehandLogin(teamToken, appId);
    }


    public void openWebApp(Context context, String url) {
        if (!url.contains("teamToken")) {
            if (url.contains("?")) {
                url = url + "&teamToken=" + TeamWorkGoUtil.getTeamToken();
            } else {
                url = url + "?&teamToken=" + TeamWorkGoUtil.getTeamToken();
            }
        }
        if (!url.contains("appId")) {
            if (url.contains("?")) {
                url = url + "&appId=" + TeamWorkGoUtil.getAppId();
            } else {
                url = url + "?&appId=" + TeamWorkGoUtil.getAppId();
            }
        }
        Log.i("fengao", "openWebApp: " + url);
        Intent intent = new Intent(context, SDK_WebApp.class);
        intent.putExtra("url", url);
        String userInfo = SharedPreferencesUtils.getParam(context, "userInfo", "");
        String teamToken = SharedPreferencesUtils.getParam(context, "teamToken", "");
        String appId = SharedPreferencesUtils.getParam(context, "appId", "");
        intent.putExtra("userInfo", userInfo);
        intent.putExtra("teamToken", teamToken);
        intent.putExtra("appId", appId);
        context.startActivity(intent);
    }

    public void joinLiveConnection(final Context context, String url) {
        Log.i("fengao", "joinLiveConnection: " + url);
        if (!url.contains("DAY/Meeting") || mode == MODE_WITHOUT_CONFERENCE) {
            return;
        }
        url = url.substring("DAY/Meeting?".length());
        String[] params = url.split("&");
        String pCode = "";
        String userName = "";
        for (int i = 0; i < params.length; i++) {
            String[] split = params[i].split("=");
            if (split[0].equals("pCode")) {
                pCode = split[1];
            }
            if (split[0].equals("userName")) {
                userName = split[1];
            }
        }
        ConferenceReq req = new ConferenceReq("", userName, pCode);
        req.setCallSelf(false);
        TangInterface.joinConference(context, req, new TangCallback() {
            @Override
            public void onCallback(BaseResp baseResp) {
                boolean result = baseResp.getResult();
                if (result == false) {
                    Toast.makeText(context, baseResp.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


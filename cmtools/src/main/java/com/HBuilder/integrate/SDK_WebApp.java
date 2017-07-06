package com.HBuilder.integrate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dayang.activity.MediaPlugin;
import com.dayang.activity.MessageActivity;
import com.dayang.cmtools.R;
import com.dayang.common.Canstant;
import com.dayang.common.JSONFactory;
import com.dayang.common.OkHttpUtil;
import com.dayang.info.WebAppStartParamsInfo;
import com.dayang.service.LocationService;
import com.dayang.service.UpdateService;
import com.dayang.util.ApkUpdateUtil;
import com.dayang.view.Dialog;
import com.dayang.view.DialogNotify;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import java.io.IOException;
import java.util.ArrayList;

import io.dcloud.EntryProxy;
import io.dcloud.RInformation;
import io.dcloud.common.DHInterface.IApp;
import io.dcloud.common.DHInterface.IApp.IAppStatusListener;
import io.dcloud.common.DHInterface.ICore;
import io.dcloud.common.DHInterface.ICore.ICoreStatusListener;
import io.dcloud.common.DHInterface.IOnCreateSplashView;
import io.dcloud.common.DHInterface.ISysEventListener.SysEventType;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.IWebviewStateListener;
import io.dcloud.feature.internal.sdk.SDK;
import okhttp3.Call;
import okhttp3.Response;

import static com.dayang.common.Canstant.TAG;

public class SDK_WebApp extends Activity {
    EntryProxy mEntryProxy = null;
    private String content;
    private String url;
    private String guid;
    private String classifyId;
    private String classifyName;
    private String appId;
    public static String webAppUrl;
    public static String firmId;
    public static String teamToken;
    public static String userInfo;

    public static boolean isFromGT() {
        return isFromGT;
    }

    public static String messageContent = "";

    public static ProgressBar getProgressBar() {
        return progressBar;
    }

    public static int updateMode = 0;
    public static ProgressBar progressBar;
    public static boolean isFromGT = false;
    private static FrameLayout f;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i(TAG, "handleMessage: ");
            if (msg.what == Canstant.NEEDUPDATA) {
                update((Object[]) msg.obj);
            } else if (msg.what == Canstant.NOTNEEDUPDATA && updateMode == ApkUpdateUtil.NORMALUPDATE) {
                Toast.makeText(getApplicationContext(), "当前已是最新版本", 0).show();
            } else if (msg.what == Canstant.Error && updateMode == ApkUpdateUtil.NORMALUPDATE) {
                Toast.makeText(getApplicationContext(), "获取最新版本失败,请稍后再试", 0).show();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        Intent intent = getIntent();
        webAppUrl = intent.getStringExtra("url");
        userInfo = intent.getStringExtra("userInfo");
        teamToken = intent.getStringExtra("teamToken");
        firmId = intent.getStringExtra("appId");
        Log.i(TAG, "onCreate: " + webAppUrl);
        if (mEntryProxy == null) {
            View view = View.inflate(getApplicationContext(), R.layout.actuvity_procress, null);
            f = (FrameLayout) view.findViewById(R.id.fl_content);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            WebappMode wm = new WebappMode(this, f);
            mEntryProxy = EntryProxy.init(this, wm);
            mEntryProxy.onCreate(savedInstanceState, f,
                    SDK.IntegratedMode.WEBAPP, wm);
            setContentView(view);
        }
        WebView.setWebContentsDebuggingEnabled(true);

    }

    public static String getMessageContent() {
        return messageContent;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        MediaPlugin.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void enterUrl(final String json) {
        messageContent = json;
        isFromGT = true;
        content = json;
        url = "";
        JsonObject jsonObject = JSONFactory.parseJsonStr(json);
        try {
            content = jsonObject.get("content").getAsString();
        } catch (Exception e) {
        }
        try {
            url = jsonObject.get("url").getAsString();
        } catch (Exception e) {
        }
        try {
            appId = jsonObject.get("appId").getAsString();
        } catch (Exception e) {
        }
        try {
            classifyId = jsonObject.get("classifyId").getAsString();
        } catch (Exception e) {
        }
        try {
            guid = jsonObject.get("guid").getAsString();
        } catch (Exception e) {
        }
        try {
            classifyName = jsonObject.get("classifyName").getAsString();
        } catch (Exception e) {
        }
        MediaPlugin.showMessgae(url, appId, classifyId, guid, classifyName);
    }

    public void unBindGT() {
        if (MediaPlugin.bindInfo != null && !MediaPlugin.gtUrl.equals("")) {
            String json = new Gson().toJson(MediaPlugin.bindInfo);
            String url = MediaPlugin.gtUrl;
            if (!url.endsWith("unbind")) {
                url = url.replace("bind", "unbind");
            }
            new OkHttpUtil().call(url, json, new OkHttpUtil.OkHttpCallBack() {
                @Override
                public void success(Response response) throws Exception {
                    response.body().close();
                }

                @Override
                public void error(Call request, IOException e) {
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onPostResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void update(Object[] obj) {
        final String path = (String) obj[0];
        ArrayList<String> description = (ArrayList<String>) obj[1];
        final Dialog.Builder builder = new Dialog.Builder(this);
        builder.setContant("发现新版本是否更新");
        builder.setTitle("更新提示");
        final Dialog dialog = builder.create();
        builder.setUpdateLog(description);
        builder.setOnClick(new Dialog.onUpClickListener() {
            @Override
            public void onEnterClick() {
                //跳转应用市场
                // LocalAppVersionUtil.upload(path,SDK_WebApp.this);
                Intent intent = new Intent(SDK_WebApp.this, UpdateService.class);
                intent.putExtra("app_name", R.string.app_name);
                intent.putExtra("downurl", path);
                SDK_WebApp.this.startService(intent);
                dialog.dismiss();
            }

            @Override
            public void onCancelClick() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void update(final String path) {
        final Dialog.Builder builder = new Dialog.Builder(this);
        builder.setContant("发现新版本是否更新");
        builder.setTitle("更新提示");
        final Dialog dialog = builder.create();
        builder.setOnClick(new Dialog.onUpClickListener() {
            @Override
            public void onEnterClick() {
                //跳转应用市场
                // LocalAppVersionUtil.upload(path,SDK_WebApp.this);
                Log.i(TAG, "onEnterClick: ");
                Intent intent = new Intent(SDK_WebApp.this, UpdateService.class);
                intent.putExtra("app_name", R.string.app_name);
                intent.putExtra("downurl", path);
                SDK_WebApp.this.startService(intent);
                dialog.dismiss();
            }

            @Override
            public void onCancelClick() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mEntryProxy.onActivityExecute(this,
                SysEventType.onCreateOptionMenu, menu);
    }

    @Override
    public void onPause() {
        super.onPause();
        mEntryProxy.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mEntryProxy.onResume(this);
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getFlags() != 0x10600000) {// 非点击icon调用activity时才调用newintent事件
            mEntryProxy.onNewIntent(this, intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageActivity.setSdk_activity(null);
        mEntryProxy.onStop(this);
        stopService(new Intent(this, LocationService.class));
        //  unBindGT();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean _ret = mEntryProxy.onActivityExecute(this,
                SysEventType.onKeyDown, new Object[]{keyCode, event});
        return _ret ? _ret : super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean _ret = mEntryProxy.onActivityExecute(this,
                SysEventType.onKeyUp, new Object[]{keyCode, event});
        return _ret ? _ret : super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        boolean _ret = mEntryProxy.onActivityExecute(this,
                SysEventType.onKeyLongPress, new Object[]{keyCode, event});
        return _ret ? _ret : super.onKeyLongPress(keyCode, event);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        try {
            int temp = this.getResources().getConfiguration().orientation;
            if (mEntryProxy != null) {
                mEntryProxy.onConfigurationChanged(this, temp);
            }
            super.onConfigurationChanged(newConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mEntryProxy.onActivityExecute(this, SysEventType.onActivityResult,
                new Object[]{requestCode, resultCode, data});
    }


}

class WebappMode implements ICoreStatusListener, IOnCreateSplashView {
    Activity activity;
    View splashView = null;
    ViewGroup rootView;
    IApp app = null;
    ProgressDialog pd = null;

    public WebappMode(Activity activity, ViewGroup rootView) {
        this.activity = activity;
        this.rootView = rootView;
    }

    @Override
    public void onCoreInitEnd(ICore coreHandler) {
        String args = "";
        // 创建默认webapp，赋值appid
        String appBasePath = "/apps/Launcher";// 表示
//      String appBasePath = "/apps/DYAppPortal";// 表示
        Log.i(TAG, "onCoreInitEnd: " + SDK_WebApp.webAppUrl);
        WebAppStartParamsInfo webAppStartParamsInfo = new WebAppStartParamsInfo(SDK_WebApp.webAppUrl, SDK_WebApp.firmId, SDK_WebApp.userInfo, SDK_WebApp.teamToken);
        String s = new Gson().toJson(webAppStartParamsInfo);
        if (SDK_WebApp.webAppUrl != null && !SDK_WebApp.webAppUrl.equals("")) {
            args = s;// 设置启动参数
        }
        //String path = "file:///"+ Environment.getExternalStorageDirectory().getAbsolutePath()+"/com.dayang.tools/DYCMTools";
        app = SDK.startWebApp(activity, appBasePath, args,
                new IWebviewStateListener() {
                    @Override
                    public Object onCallBack(int pType, Object pArgs) {

                        switch (pType) {
                            case IWebviewStateListener.ON_WEBVIEW_READY:
                                // 准备完毕之后添加webview到显示父View中，设置排版不显示状态，避免显示webview时，html内容排版错乱问题
                                View view = ((IWebview) pArgs).obtainApp()
                                        .obtainWebAppRootView().obtainMainView();
                                view.setVisibility(View.INVISIBLE);
                                rootView.addView(view, 0);
                                break;
                            case IWebviewStateListener.ON_PAGE_STARTED:
                                // pd = ProgressDialog.show(activity, "加载中",
                                // "0/100");
                                break;
                            case IWebviewStateListener.ON_PROGRESS_CHANGED:
                            /*
                             * if(pd != null){ pd.setMessage(pArgs + "/100"); }
							 */
                                break;
                            case IWebviewStateListener.ON_PAGE_FINISHED:
                            /*
                             * if(pd != null){ pd.dismiss(); pd = null; }
							 */
                                // 页面加载完毕，设置显示webview
                                app.obtainWebAppRootView().obtainMainView()
                                        .setVisibility(View.VISIBLE);
                                break;
                        }
                        return null;
                    }
                }, this);// 启动webapp
        app.setIAppStatusListener(new IAppStatusListener() {

            @Override
            public boolean onStop() {
                rootView.removeView(app.obtainWebAppRootView().obtainMainView());
                return true;
            }

            @Override
            public String onStoped(boolean b, String s) {
                rootView.removeView(app.obtainWebAppRootView().obtainMainView());
                return null;
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onPause(IApp oldApp, IApp newApp) {

            }
        });
    }

    @Override
    public void onCoreReady(ICore coreHandler) {
        // 加载自定runtime使用的路径
        SDK.initSDK(coreHandler);
        SDK.requestAllFeature();
    }

    @Override
    public boolean onCoreStop() {
        // 当返回false时候回关闭activity
        return false;
    }

    @Override
    public Object onCreateSplash(Context pContextWrapper) {
        splashView = new FrameLayout(activity);
        splashView.setBackgroundResource(RInformation.DRAWABLE_SPLASH);
        rootView.addView(splashView);
        return null;
    }

    @Override
    public void onCloseSplash() {
        rootView.removeView(splashView);
    }
}

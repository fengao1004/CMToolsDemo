package com.dayang.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dayang.common.Canstant;
import com.dayang.common.JSONFactory;
import com.dayang.common.OkHttpUtil;
import com.dayang.info.AppVersionInfo;
import com.dayang.info.UpdataVersionInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.codehaus.jackson.JsonFactory;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by 冯傲 on 2016/10/18.
 * e-mail 897840134@qq.com
 */
public class LocalAppVersionUtil {
//    static String TAG = Canstant.TAG;
//    Context context;
//    String appName = "";
//    String versionCode = "";
//    public static final int AUTOUPDATE = 1;
//    public static final int NORMALUPDATE = 2;
//
//    public LocalAppVersionUtil(Context context) {
//        this.context = context;
//    }
//
//    public ArrayList<AppVersionInfo> getAssestAppVersion() {
//        AssetManager assets = context.getAssets();
//        try {
//            InputStream open = assets.open("LocalAppResource/AppConfig.json");
//            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//            byte[] b = new byte[1024];
//            int len = 0;
//            while ((len = open.read(b)) != -1) {
//                byteOut.write(b, 0, len);
//            }
//            byteOut.flush();
//            String json = byteOut.toString();
//            byteOut.close();
//            open.close();
//            JsonObject jsonObject = JSONFactory.parseJsonStr(json);
//            JsonArray data = jsonObject.getAsJsonArray("data");
//            ArrayList<AppVersionInfo> appVersionInfos = new ArrayList<>();
//            for (int i = 0; i < data.size(); i++) {
//                JsonElement element = data.get(i);
//                JsonObject asJsonObject = element.getAsJsonObject();
//                String proName = asJsonObject.get("proName").getAsString();
//                String fileName = asJsonObject.get("fileName").getAsString();
//                String version = asJsonObject.get("version").getAsString();
//                appVersionInfos.add(new AppVersionInfo(proName, new Integer(version), fileName));
//            }
//            return appVersionInfos;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public Map<String, String> getLocalAppVersion() {
//        File filesDir = context.getFilesDir();
//        Map<String, String> map = new TreeMap<>();
//        if (filesDir.isDirectory()) {
//            File[] list = filesDir.listFiles();
//            for (int i = 0; i < list.length; i++) {
//                File file = list[i];
//                if (file.isDirectory()) {
//                    File versionJson = new File(file.getAbsolutePath() + "/version.json");
//                    if (versionJson.exists()) {
//                        try {
//                            FileInputStream fileInputStream = new FileInputStream(versionJson);
//                            ByteArrayOutputStream b = new ByteArrayOutputStream();
//                            byte[] by = new byte[1024];
//                            int len = 0;
//                            while ((len = fileInputStream.read(by)) != -1) {
//                                b.write(by, 0, len);
//                            }
//                            b.flush();
//                            String json = b.toString();
//                            Log.i(TAG, "getLocalAppVersion: " + json);
//                            b.close();
//                            fileInputStream.close();
//                            JsonObject jsonObject = JSONFactory.parseJsonStr(json);
//                            String name = jsonObject.get("name").getAsString();
//                            String version = jsonObject.get("version").getAsString();
//                            map.put(name, version);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Log.i(TAG, "getLocalAppVersion: " + e.toString());
//                        }
//                    }
//                }
//            }
//        }
//        return map;
//    }
//
//    //升级本地H5的入口
//    public void getNetAppVersion() {
//        new OkHttpUtil().callGet(Canstant.VERSIONURL, new OkHttpUtil.OkHttpCallBack() {
//            @Override
//            public void success(Response response) throws Exception {
//                InputStream inputStream = response.body().byteStream();
//                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//                byte[] b = new byte[1024];
//                int len = 0;
//                while ((len = inputStream.read(b)) != -1) {
//                    byteOut.write(b, 0, len);
//                }
//                byteOut.flush();
//                String json = byteOut.toString();
//                byteOut.close();
//                inputStream.close();
//                JsonObject jsonObject = JSONFactory.parseJsonStr(json);
//                JsonArray data = jsonObject.getAsJsonArray("data");
//                ArrayList<AppVersionInfo> appVersionInfos = new ArrayList<>();
//                for (int i = 0; i < data.size(); i++) {
//                    JsonElement element = data.get(i);
//                    JsonObject asJsonObject = element.getAsJsonObject();
//                    String proName = asJsonObject.get("proName").getAsString();
//                    String fileName = asJsonObject.get("filePath").getAsString();
//                    String version = asJsonObject.get("version").getAsString();
//                    appVersionInfos.add(new AppVersionInfo(proName, new Integer(version), fileName));
//                }
//                checkVersion(appVersionInfos);
//            }
//
//            @Override
//            public void error(Request request, IOException e) {
//                Log.i(TAG, "error: " + e.toString());
//                checkVersion(new ArrayList<AppVersionInfo>());
//            }
//        });
//    }
//
//    public void checkVersion(ArrayList<AppVersionInfo> netAppVersions) {
//
//        Map<String, String> localAppVersion = getLocalAppVersion();
//        ArrayList<AppVersionInfo> assestAppVersion = getAssestAppVersion();
//        ArrayList<UpdataVersionInfo> updataInfos = new ArrayList<>();
//        if (netAppVersions != null && netAppVersions.size() > 0) {
//            for (int i = 0; i < netAppVersions.size(); i++) {
//                String s = localAppVersion.get(netAppVersions.get(i).name);
//                if (s != null) {
//                    int sv = Integer.parseInt(s);
//                    if (sv != netAppVersions.get(i).versionCode) {
//                        updataInfos.add(new UpdataVersionInfo(netAppVersions.get(i).fileName, netAppVersions.get(i).name, true, netAppVersions.get(i).versionCode));
//                    }
//                } else {
//                    updataInfos.add(new UpdataVersionInfo(netAppVersions.get(i).fileName, netAppVersions.get(i).name, true, netAppVersions.get(i).versionCode));
//                }
//            }
//            for (int i = 0; i < updataInfos.size(); i++) {
//                for (int j = 0; j < assestAppVersion.size(); j++) {
//                    if ((assestAppVersion.get(j).name.equals(updataInfos.get(i).name) && (assestAppVersion.get(j).versionCode == updataInfos.get(i).newstVersionCode))) {
//                        updataInfos.get(i).updataUrl = assestAppVersion.get(j).fileName;
//                    }
//                }
//            }
//        } else {
//            for (int i = 0; i < assestAppVersion.size(); i++) {
//                String s = localAppVersion.get(assestAppVersion.get(i).name);
//                if (s != null) {
//                    int sv = Integer.parseInt(s);
//                    if (sv != assestAppVersion.get(i).versionCode) {
//                        updataInfos.add(new UpdataVersionInfo(assestAppVersion.get(i).fileName, assestAppVersion.get(i).name, true, assestAppVersion.get(i).versionCode));
//                    }
//                }
//            }
//        }
//        for (int i = 0; i < updataInfos.size(); i++) {
//            Log.i(TAG, "checkVersion:111 " + updataInfos.get(i).updataUrl);
//            if (!updataInfos.get(i).updataUrl.contains("http")) {
//                new AssetsUtil(context).getFileFromAssets("LocalAppResource/" + updataInfos.get(i).updataUrl, context.getFilesDir().getAbsolutePath(), new AssetsUtil.GetFileListener() {
//                    @Override
//                    public void getFileComplete() {
//                        Log.i(TAG, "getFileComplete: ");
//                    }
//
//                    @Override
//                    public void getFileError() {
//                        Log.i(TAG, "getFileError: ");
//                    }
//                });
//            } else {
//                String[] split = updataInfos.get(i).updataUrl.split("/");
//                String name = split[split.length - 1];
//                final String filePath = context.getFilesDir().getAbsolutePath() + "/" + name;
//                HttpUtils ht = new HttpUtils();
//                ht.download(
//                        updataInfos.get(i).updataUrl,
//                        filePath, null, new RequestCallBack<File>() {
//                            @Override
//                            public void onLoading(long total, long current,
//                                                  boolean isUploading) {
//                                Log.i(TAG, "onLoading: " + current);
//                            }
//
//                            @Override
//                            public void onSuccess(ResponseInfo<File> arg0) {
//                                UnZip unzipFile = new UnZip();
//                                boolean unzip = unzipFile.unzip(filePath, context.getFilesDir().getAbsolutePath());
//                                if (unzip) {
//                                    File localfile = new File(filePath);
//                                    localfile.delete();
//                                    Log.i(TAG, "onSuccess: delelte");
//                                    SharedPreferencesUtils.setParam(context, appName, versionCode);
//                                    File file = new File(context.getFilesDir().getAbsolutePath());
//                                    getFiles(file);
//                                } else {
//                                    Log.i(TAG, "onSuccess: delelte false");
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(HttpException arg0, String arg1) {
//                                // TODO Auto-generated method stub
//
//                            }
//                        });
//            }
//        }
//
//    }
//
//    //在okhttp的回调中调用 为子线程
//    private void updateVersion(String url) {
//        final String dirPath = context.getFilesDir().getAbsolutePath();
//        final String fileName = url.split("/")[url.split("/").length - 1];
//        final String filePath = dirPath + "/" + fileName;
//        HttpUtils ht = new HttpUtils();
//        ht.download(
//                "http://100.0.1.248:8080/H5Demo/SXP.zip",
//                filePath, null, new RequestCallBack<File>() {
//                    @Override
//                    public void onLoading(long total, long current,
//                                          boolean isUploading) {
//                        Log.i(TAG, "onLoading: " + current);
//                    }
//
//                    @Override
//                    public void onSuccess(ResponseInfo<File> arg0) {
//                        UnZip unzipFile = new UnZip();
//                        boolean unzip = unzipFile.unzip(filePath, dirPath);
//                        if (unzip) {
//                            File localfile = new File(filePath);
//                            localfile.delete();
//                            Log.i(TAG, "onSuccess: delelte");
//                            SharedPreferencesUtils.setParam(context, appName, versionCode);
//                            File file = new File(dirPath);
//                            getFiles(file);
//                        } else {
//                            Log.i(TAG, "onSuccess: delelte false");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(HttpException arg0, String arg1) {
//                        // TODO Auto-generated method stub
//
//                    }
//                });
//    }
//
//    private void getFiles(File file) {
//        File[] files = file.listFiles();
//        for (int i = 0; i < files.length; i++) {
//            if (files[i].isDirectory()) {
//                getFiles(files[i]);
//            } else {
//                Log.i(TAG, "getFiles: " + files[i].getAbsolutePath());
//            }
//        }
//
//    }
//
//    public static void upload(String url, final Context context) {
//        OkHttpUtil okHttpUtil = new OkHttpUtil();
//        okHttpUtil.callGet(url, new OkHttpUtil.OkHttpCallBack() {
//            @Override
//            public void success(Response response) throws Exception {
//                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DyShell/asd.apk");
//                Log.i(TAG, "sucess: " + file.getAbsolutePath());
//                if (!file.exists()) {
//                    new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DyShell/").mkdirs();
//                    file.createNewFile();
//                }
//                InputStream inputStream = response.body().byteStream();
//                FileOutputStream fileOutputStream = new FileOutputStream(file);
//                byte[] b = new byte[1024];
//                int len;
//                while ((len = inputStream.read(b)) != -1) {
//                    fileOutputStream.write(b, 0, len);
//                }
//                inputStream.close();
//                fileOutputStream.close();
//                String path = file.getAbsolutePath();
////                String command     = "chmod " + "777" + " " + path;
////                Runtime runtime = Runtime.getRuntime();
////                runtime.exec(command);
//                if (file.exists() && file.length() > 0) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
//                    context.startActivity(intent);
//                }
//            }
//
//            @Override
//            public void error(Request request, IOException e) {
//
//            }
//        });
//    }
//
//    //检查apk版本入口
//    public void checkVersion(final Handler handler) {
//        Log.i(TAG, "checkVersion: " + Thread.currentThread().getName());
//        OkHttpUtil okHttpUtil = new OkHttpUtil();
//        okHttpUtil.callGet("http://100.0.1.248:8080/version/version.json", new OkHttpUtil.OkHttpCallBack() {
//            @Override
//            public void success(Response response) throws Exception {
//                Log.i(TAG, "checkVersion: " + Thread.currentThread().getName());
//                boolean needUpdata = false;
//                String string = response.body().string();
//                JSONObject jsonObject = new JSONObject(string);
//                String version = jsonObject.get("version").toString();
//                String filePath = jsonObject.get("filePath").toString();
//                JsonObject jsonArray = JSONFactory.parseJsonStr(string);
//                ArrayList<String> updateLog = new ArrayList<>();
//                try {
//                    JsonArray updateLogStringArray = jsonArray.get("description").getAsJsonArray();
//                    for (int i = 0; i < updateLogStringArray.size(); i++) {
//                        updateLog.add(updateLogStringArray.get(i).getAsString());
//                    }
//                } catch (Exception e) {
//                    Log.i(TAG, "json_error: " + e.toString());
//                }
//
//                String localVersion = getAppVersionName(context);
//                Log.i(TAG, "sucess: localVersion" + localVersion);
//                Log.i(TAG, "sucess: version" + version);
//                if (!localVersion.equals("")) {
//                    //localVersion = localVersion.split("/")[localVersion.split("/").length - 2];
//                    Log.i(TAG, "sucess: localVersion" + localVersion.equals(version));
//                    if (!localVersion.equals(version)) {
//                        needUpdata = true;
//                    }
//                } else {
//                    needUpdata = true;
//                }
//                if (needUpdata) {
//                    Log.i(TAG, "sucess: " + needUpdata);
//                    Message mes = new Message();
//                    mes.what = Canstant.NEEDUPDATA;
//                    mes.obj = new Object[]{filePath, updateLog};
//                    //mes.obj = filePath;
//                    handler.sendMessage(mes);
//                }else {
//                    Message mes = new Message();
//                    mes.what = Canstant.NOTNEEDUPDATA;
//                    //mes.obj = filePath;
//                    handler.sendMessage(mes);
//                }
//            }
//
//            @Override
//            public void error(Request request, IOException e) {
//                Message mes = new Message();
//                mes.what = Canstant.Error;
//                handler.sendMessage(mes);
//            }
//        });
//    }
//
//    public static String getAppVersionName(Context context) {
//        String versionName = "";
//        try {
//            PackageManager pm = context.getPackageManager();
//            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
//            versionName = pi.versionName;
//            if (versionName == null || versionName.length() <= 0) {
//                return "";
//            }
//        } catch (Exception e) {
//            Log.e("VersionInfo", "Exception", e);
//        }
//        return versionName;
//    }
}

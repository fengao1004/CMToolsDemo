package com.dayang.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.util.Log;

import com.dayang.common.Canstant;
import com.dayang.common.JSONFactory;
import com.dayang.common.OkHttpUtil;
import com.google.gson.JsonObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by 冯傲 on 2017/1/5.
 * e-mail 897840134@qq.com
 */

public class HtmlUpdateUtil {
//    public static final String TAG = Canstant.TAG;
//    public static final int AUTOUPDATE = 1;
//    public static final int NORMALUPDATE = 2;
//    private static final String PORTALPATH = "portalpath";
//    private static final String PORTALVERSION = "portalversion";
//    private static final String APPVERSION = "appversion";
//    private Context context;
//    private String checkUrl = "http://100.0.1.248:8080/version/version.json";
//    public String ASSETSPORTALVERSIONCODE = "assetsportalversioncode";
//
//    public HtmlUpdateUtil(Context context) {
//        this.context = context;
//    }
//
//    /**
//     * @param
//     * @return 检查当前版本是否是最新版
//     */
//    public void checkPortalVersion() {
//        String appVersionFromSP = SharedPreferencesUtils.getParam(context, APPVERSION, "");
//        String appVersion = getAppVersionName(context);
//        if (!appVersion.equals(appVersionFromSP)) {
//            SharedPreferencesUtils.setParam(context, APPVERSION, appVersion);
//            getHtmlLoginPath();
//        }
//        OkHttpUtil okHttpUtil = new OkHttpUtil();
//        okHttpUtil.callGet(checkUrl, new OkHttpUtil.OkHttpCallBack() {
//            @Override
//            public void success(Response response) throws Exception {
//                String string = response.body().string();
//                JSONArray jsonArray1 = new JSONArray(string);
//                JSONObject jsonObject = jsonArray1.getJSONObject(0);
//                String fileVersion = jsonObject.get("fileVersion").toString();
//                String downLoadPath = jsonObject.get("downLoadPath").toString();
//                String fileMD5 = jsonObject.get("fileMD5").toString();
//                int localVersion = SharedPreferencesUtils.getParam(context, PORTALVERSION, 0);
//                int netVersion = Integer.parseInt(fileVersion);
//                if (netVersion > localVersion) {
//                    update(downLoadPath, fileMD5, netVersion);
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
//    private void update(String path, final String fileMD5, final int netVersion) {
//        final String dirPath = context.getFilesDir().getAbsolutePath() + "/portal/" + netVersion + "/";
//        final String fileName = path.split("/")[path.split("/").length - 1];
//        final String fileNameNotSuffix = fileName.split("\\.")[0];
//        final String filePath = dirPath + fileName;
//        HttpUtils ht = new HttpUtils();
//        ht.download(
//                path,
//                filePath, null, new RequestCallBack<File>() {
//                    @Override
//                    public void onLoading(long total, long current,
//                                          boolean isUploading) {
//                        Log.i(TAG, "onLoading: " + current);
//                    }
//
//                    @Override
//                    public void onSuccess(ResponseInfo<File> arg0) {
//                        File file = new File(filePath);
//                        if (!file.isFile()) {
//                            return;
//                        }
//                        MessageDigest digest = null;
//                        FileInputStream in = null;
//                        byte buffer[] = new byte[1024];
//                        int len;
//                        try {
//                            digest = MessageDigest.getInstance("MD5");
//                            in = new FileInputStream(file);
//                            while ((len = in.read(buffer, 0, 1024)) != -1) {
//                                digest.update(buffer, 0, len);
//                            }
//                            in.close();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        BigInteger bigInt = new BigInteger(1, digest.digest());
//                        String md5 = bigInt.toString(16);
////                        if (fileMD5.equals(md5)) {
//                        if (true) {
//                            UnZip unzipFile = new UnZip();
////                            boolean unzip = unzipFile.unzip(filePath, dirPath);
//                            if (unzip) {
//                                Log.i(TAG, "压缩文件解压成功");
//                                String loginPath = "file://"+context.getFilesDir().getAbsolutePath() + "/portal/" + netVersion +"/"+fileNameNotSuffix+ "/login.html";
//                                SharedPreferencesUtils.setParam(context, PORTALPATH, loginPath);
//                                SharedPreferencesUtils.setParam(context, PORTALVERSION, netVersion);
//                                printDir(context.getFilesDir().getAbsolutePath());
//                            } else {
//                                Log.i(TAG, "压缩文件解压失败");
//                            }
//                            file.delete();
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
//    private void printDir(String absolutePath) {
//        File file = new File(absolutePath);
//        if(file.exists()){
//            File[] files = file.listFiles();
//            for(int i= 0; i< files.length;i++){
//                if(files[i].isDirectory()){
//                    printDir(files[i].getAbsolutePath());
//                }else {
//                    Log.i(TAG, "printDir: "+files[i].getAbsolutePath());
//                }
//            }
//        }
//    }
//
//    public String getHtmlLoginPath() {
//        String path = SharedPreferencesUtils.getParam(context, PORTALPATH, "");
//        if (path.equals("")) {
//            int portalHtmlVersionFromAssets = getPortalHtmlVersionFromAssets();
//            Log.i(TAG, "portalHtmlVersionFromAssets: " + portalHtmlVersionFromAssets);
//            int localPortalHtmlVersion = getLocalPortalHtmlVersion();
//            Log.i(TAG, "localPortalHtmlVersion: " + localPortalHtmlVersion);
//            if (portalHtmlVersionFromAssets > localPortalHtmlVersion) {
//                String portalPath = "login.html";
//                SharedPreferencesUtils.setParam(context, PORTALPATH, portalPath);
//                SharedPreferencesUtils.setParam(context, PORTALVERSION, portalHtmlVersionFromAssets);
//                Log.i(TAG, "getHtmlLoginPath: " + path);
//                return portalPath;
//            } else {
//                String portalPath ="file://"+ context.getFilesDir().getAbsolutePath() + "/portal/" + localPortalHtmlVersion + "/PORTAL/login.html";
//                SharedPreferencesUtils.setParam(context, PORTALPATH, portalPath);
//                SharedPreferencesUtils.setParam(context, PORTALVERSION, localPortalHtmlVersion);
//                Log.i(TAG, "getHtmlLoginPath: " + path);
//                return portalPath;
//            }
//        } else {
//            Log.i(TAG, "getHtmlLoginPath: " + path);
//            return path;
//        }
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
//
//    public int getPortalHtmlVersionFromAssets() {
//        AssetManager assets = context.getAssets();
//        try {
//            InputStream open = assets.open("apps/DYAppPortal/www/manifest.json");
//            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//            byte[] b = new byte[1024];
//            int len;
//            while ((len = open.read(b)) != -1) {
//                byteOut.write(b, 0, len);
//            }
//            byteOut.flush();
//            String json = byteOut.toString();
//            byteOut.close();
//            open.close();
//            JsonObject jsonObject = JSONFactory.parseJsonStr(json);
//            JsonObject version = jsonObject.getAsJsonObject("version");
//            String code = version.get("code").getAsString();
//            SharedPreferencesUtils.setParam(context, ASSETSPORTALVERSIONCODE, code);
//            return Integer.parseInt(code);
//        } catch (Exception e) {
//            Log.i(TAG, "getPortalHtmlVersionFromAssets: " + e.toString());
//        }
//        return 0;
//    }
//
//    public int getLocalPortalHtmlVersion() {
//        File filesDir = new File(context.getFilesDir().getAbsolutePath() + "/portal/");
//        int code = 0;
//        if (!filesDir.exists()) {
//            return code;
//        }
//        if (filesDir.isDirectory()) {
//            File[] list = filesDir.listFiles();
//            for (int i = 0; i < list.length; i++) {
//                String[] strings = list[i].getAbsolutePath().split("/");
//                Integer integer = Integer.parseInt(strings[strings.length - 1]);
//                if (code < integer) {
//                    code = integer;
//                }
//            }
//        }
//        return code;
//    }
}

package com.dayang.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.HBuilder.integrate.SDK_WebApp;
import com.dayang.common.BindGeTuiBean;
import com.dayang.common.Canstant;
import com.dayang.common.Constants;
import com.dayang.common.CustomBitmapFactory;
import com.dayang.common.DataCleanManager;
import com.dayang.common.JSONFactory;
import com.dayang.common.MediaFile;
import com.dayang.common.OkHttpUtil;
import com.dayang.info.FileAndIndexInfo;
import com.dayang.inter.KeyValueData;
import com.dayang.inter.impl.KeyValueDataImpl;
import com.dayang.sysevent.IMediaEventExt;
import com.dayang.upload.FtpUpload;
import com.dayang.upload.HttpUpload;
import com.dayang.upload.NewHttpUpload;
import com.dayang.upload.UploadFileThread;
import com.dayang.util.PermissionUtil;
import com.dayang.vo.BrowerBase;
import com.dayang.vo.Duration;
import com.dayang.vo.Fileinfos;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.dcloud.common.DHInterface.IApp;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.StandardFeature;
import io.dcloud.common.util.JSUtil;

public class MediaPlugin extends StandardFeature {
    public static final String shellVersion = "1.0.20160218";
    public static final int UPDATEPROGRESSBAR = 123;
    public static int i = 0;
    public static File cameraFile = null;
    public static File cropFile = null;
    public static String fileType = null;
    public static BindGeTuiBean bindInfo = null;
    public static Activity activity = null;
    public static Handler handler = null;    //上传线程执行结束后发出的消息函数
    public static String gtUrl = "";
    public static IMediaEventExt sysEventExtWebView = null;
    public static boolean asyncFlag = true;
    public static ProgressDialog progressDialog = null;
    public static IWebview indexWebView = null;
    public static String TAG = Canstant.TAG;
    public static PermissionsResultListener permissionsResultListener;
    public IWebview locationIWebView = null;
    public String locationCallbackId = null;
    public ProgressBar progressBar;
    public WebView webView = null;
    public KeyValueData keyValueData = null;
    public int webViewProgress = 0; //用于显示进度条的progress
    public int progressIncrement = 0;

    public Handler handlerUpdate = new Handler() { //用于更新webView进度的handler
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATEPROGRESSBAR) {
                updateProgressBar();
            } else {
                progressBar.setProgress(msg.what);
            }
        }
    };

    /**
     * 保存key:value键值对
     *
     * @param parameter
     * @return
     */
    public String saveKVData(IWebview pWebview, JSONArray array) {
        String parameter = array.optString(0);
        activity = pWebview.getActivity();
        keyValueData = new KeyValueDataImpl(activity.getApplicationContext(), activity);
        String result = keyValueData.saveKVData(parameter);
        return JSUtil.wrapJsVar(result);
    }

    /**
     * 根据key获取value的值
     *
     * @param parameter
     * @return
     */
    public String getKVData(IWebview pWebview, JSONArray array) {
        String parameter = array.optString(0);
        activity = pWebview.getActivity();
        keyValueData = new KeyValueDataImpl(activity.getApplicationContext(),
                activity);
        String result = keyValueData.getKVData(parameter);
        return JSUtil.wrapJsVar(result);
    }

    /**
     * 根据key删除该键值对
     *
     * @param parameter
     * @return
     */
    public String delKVData(IWebview pWebview, JSONArray array) {
        String parameter = array.optString(0);
        activity = pWebview.getActivity();
        keyValueData = new KeyValueDataImpl(activity.getApplicationContext(),
                activity);
        String result = keyValueData.delKVData(parameter);
        return JSUtil.wrapJsVar(result);
    }

    /**
     * 从图库选择图片并返回base64
     *
     * @param parameter
     * @return
     */
    public void selectFiles(IWebview pWebview, JSONArray array) {
        Log.i(TAG, "selectFiles: " + "选择文件");
        this.webView = pWebview.obtainWebview();
        activity = pWebview.getActivity();
        String CallBackID = array.optString(0);
        String parameter = array.optString(1);
        JsonObject jsonObject = JSONFactory.parseJsonStr(parameter);
        try {
            IApp app = pWebview.obtainFrameView().obtainApp();
            Log.i(TAG, "selectFiles: CallBackID" + CallBackID);
            IMediaEventExt sysEventExt = IMediaEventExt.getInstance(app, pWebview,
                    CallBackID);
            sysEventExtWebView = sysEventExt;
            app.registerSysEventListener(sysEventExt,
                    SysEventType.onActivityResult);
            int maxNum = jsonObject.get("allowSelectNum").getAsInt();
            final Intent intent = new Intent(activity, PickImageActivity.class);
            intent.putExtra("imgNum", maxNum);
            Log.i(TAG, "selectFiles: " + "最大数目是  " + maxNum);
            int code = (int) new Date().getTime();
            PermissionUtil.checkPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, activity, new PermissionUtil.PermissionListener() {
                @Override
                public void permissionAllowed() {
                    activity.startActivityForResult(intent, Constants.FILE_SELECT_REQUEST);
                }

                @Override
                public void permissionRefused() {
                    Toast.makeText(activity, "没有足够的权限，请进入权限设置更改权限", Toast.LENGTH_SHORT).show();
                }
            }, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 手动检测版本
     *
     * @param parameter
     * @return
     */
    public void checkUpdate(IWebview pWebview, JSONArray array) {
        this.activity = pWebview.getActivity();
//        SDK_WebApp.updateMode = ApkUpdateUtil.NORMALUPDATE;//手动升级
//        new ApkUpdateUtil(activity).checkApkVersion(((SDK_WebApp) activity).handler);
    }

    /**
     * 根据传入的文件列表进行文件上传
     *
     * @param parameter
     * @return
     */
    @SuppressWarnings("unchecked")
    public void uploadFiles(IWebview pWebview, JSONArray array) {
        Log.i(TAG, "uploadFiles: ");
        this.webView = pWebview.obtainWebview();
        activity = pWebview.getActivity();
        IApp app = pWebview.obtainFrameView().obtainApp();
        String CallBackID = array.optString(0);

        String parameter = array.optString(1);
        JsonObject uploadRequestjson = JSONFactory.parseJsonStr(parameter);
        //对文件做非空判断
        JsonArray localPathjsonArray1 = uploadRequestjson.get(
                "filesLocalPathArr").getAsJsonArray();
        //对文件做非空判断
        JsonObject localpathJsonObject1 = localPathjsonArray1.get(i)
                .getAsJsonObject();
        //对文件做非空判断
        String path1 = localpathJsonObject1.get("path").getAsString();
        //对文件做非空判断
        File file = new File(path1);
        Log.i(TAG, "path" + path1);
        if (file == null || file.length() == 0) {
            Log.i(TAG, "return");
            return;
        }
        JsonElement asyncelement = uploadRequestjson.get("async");
        if (asyncelement == null) {
            asyncFlag = false;
            progressDialog = getProgressDialog();
            progressDialog.show();
        } else {
            asyncFlag = uploadRequestjson.get("async").getAsBoolean();
            if (asyncFlag != true) {
                progressDialog = getProgressDialog();
                progressDialog.show();
            } else {
                JSUtil.execCallback(pWebview, CallBackID, "正在上传", JSUtil.OK,
                        false);
            }
        }
        UploadFileThread uploadFileThread = null;
        JsonObject returnJsonObject = new JsonObject();
        List<FileAndIndexInfo> uploadFileList = new ArrayList<FileAndIndexInfo>();
        try {
            IMediaEventExt sysEventExt = IMediaEventExt.getInstance(app, pWebview,
                    CallBackID);
            sysEventExtWebView = sysEventExt;
            app.registerSysEventListener(sysEventExt,
                    SysEventType.onActivityResult);
            if (uploadRequestjson != null) {
                JsonArray localPathjsonArray = uploadRequestjson.get(
                        "filesLocalPathArr").getAsJsonArray();
                for (int i = 0; i < localPathjsonArray.size(); i++) {
                    JsonObject localpathJsonObject = localPathjsonArray.get(i)
                            .getAsJsonObject();
                    String path = localpathJsonObject.get("path").getAsString();
                    String indexNo = localpathJsonObject.get("indexNO")
                            .getAsString();
                    boolean isRename = true;
                    //String fileSessionId = UUID.randomUUID().toString();
                    String fileSessionId = "";
                    try {
                        fileSessionId = localpathJsonObject.get("fileSessionId").getAsString();
                        isRename = localpathJsonObject.get("isRename").getAsBoolean();
                    } catch (Exception e) {
                        Log.i(TAG, "uploadFiles: " + e);
                        Log.i(TAG, "uploadFiles: " + "取不到新版http断点续传指定参数");
                    }

                    uploadFileList.add(new FileAndIndexInfo(path, indexNo, fileSessionId, isRename));
                }
                String storageURL = uploadRequestjson.get("storageURL")
                        .getAsString();
                String fileStatusNotifyURL = uploadRequestjson.get(
                        "fileStatusNotifyURL").getAsString();
                String taskId = uploadRequestjson.get("taskId").getAsString();
                String tenantId = "";
                try {
                    tenantId = uploadRequestjson.get("tenantId").getAsString();
                } catch (Exception e) {
                    Log.i(TAG, "uploadFiles: " + "并没有租户id字段");
                }
                String uploadTrunkInfoURL = "";
                try {
                    uploadTrunkInfoURL = uploadRequestjson.get("uploadTrunkInfoURL").getAsString();
                } catch (Exception e) {
                    Log.i(TAG, "uploadFiles: " + e);
                    Log.i(TAG, "uploadFiles: " + "取不到新版http断点续传指定参数");
                }
                if (storageURL.startsWith("http")) {
                    URL url = new URL(storageURL);
                    if (storageURL.contains("?")) {
                        storageURL = url.getProtocol() + "://" + url.getAuthority() + url.getPath();
                        String remoteRootPath = url.getQuery().split("=")[1];
                        if (!remoteRootPath.startsWith("/")) {
                            remoteRootPath = "/" + remoteRootPath;
                        }
                        if (!remoteRootPath.endsWith("/")) {
                            remoteRootPath = remoteRootPath + "/";
                        }
                        if (!tenantId.equals("")) {
                            remoteRootPath = "/" + tenantId + remoteRootPath;
                        }
                        remoteRootPath = remoteRootPath + taskId;
                        uploadFileThread = new NewHttpUpload(storageURL,
                                fileStatusNotifyURL, handler,
                                Constants.UPLOADMUTIPLE, null, uploadFileList,
                                taskId, activity, tenantId, remoteRootPath, uploadTrunkInfoURL);
                        uploadFileThread.start();
                    } else {
                        uploadFileThread = new HttpUpload(storageURL,
                                fileStatusNotifyURL, handler,
                                Constants.UPLOADMUTIPLE, null, uploadFileList,
                                taskId, activity, tenantId);
                        uploadFileThread.start();
                    }
                } else if (storageURL.startsWith("ftp")) {
                    Log.i(TAG, "uploadFiles: " + fileStatusNotifyURL);
                    uploadFileThread = new FtpUpload(storageURL,
                            fileStatusNotifyURL, handler,
                            Constants.UPLOADMUTIPLE, null, uploadFileList,
                            taskId, activity, tenantId);
                    uploadFileThread.start();
                }

            } else {
                returnJsonObject.addProperty("success", "false");
                returnJsonObject.addProperty("description", "参数传入有误！");
                JSONObject json = null;
                try {
                    json = new JSONObject(returnJsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSUtil.execCallback(pWebview, CallBackID,
                        json, JSUtil.OK, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "uploadFiles: 参数解析错误");
            returnJsonObject.addProperty("success", "false");
            returnJsonObject.addProperty("description", e.toString());
            JSONObject json = null;
            try {
                json = new JSONObject(returnJsonObject.toString());
            } catch (JSONException e1) {
                e.printStackTrace();
            }
            JSUtil.execCallback(pWebview, CallBackID,
                    json, JSUtil.OK, false);
        }
        /*
         * String returnStr = JSONFactory.objectToJsonStr(returnJsonObject);
		 * JSUtil.execCallback(pWebview, CallBackID, returnStr, JSUtil.OK,
		 * false);
		 */
    }

    public ProgressDialog getProgressDialog() {
        if (progressDialog != null) {
            return progressDialog;
        } else {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("正在上传中,请稍等...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            return progressDialog;
        }
    }


    /**
     * 根据传入的图片文件进行base64的转换
     *
     * @param parameter
     * @return
     */
    @SuppressWarnings("unchecked")
    public void getMediaBase64Infos(IWebview pWebview, JSONArray array) {
        this.webView = pWebview.obtainWebview();
        activity = pWebview.getActivity();
        String CallBackID = array.optString(0);
        String parameter = array.optString(1);
        JsonObject jsonObject = JSONFactory.parseJsonStr(parameter);
        List<File> fileList = null;
        BrowerBase returnObject = new BrowerBase();
        try {
            if (jsonObject != null) {
                fileList = new ArrayList<File>();
                JsonArray fileNamePath = jsonObject.get("filesLocalPathArr")
                        .getAsJsonArray();
                String taskId = jsonObject.get("taskId").getAsString();
                returnObject.setTaskId(taskId);
                List<Fileinfos> fileinfoList = new ArrayList<Fileinfos>();
                for (int i = 0; i < fileNamePath.size(); i++) {
                    String base64code = null;
                    String fileName = fileNamePath.get(i).getAsString();
                    File file = new File(fileName);
                    if (!file.exists()) {
                        throw new Exception("文件不存在");
                    }
                    Fileinfos fileinfos = new Fileinfos();
                    if (MediaFile.isImageFileType(file.getAbsolutePath())) {
                        fileinfos.setFileType(Constants.FILE_IMAGE_TYPE);
                        base64code = imageToBase64(file);
                    } else if (MediaFile
                            .isVideoFileType(file.getAbsolutePath())) {
                        fileinfos.setFileType(Constants.FILE_VIDEO_TYPE);
                        Bitmap firstPicture = getVideoThumbnail(
                                file.getAbsolutePath(), 100, 100,
                                MediaStore.Images.Thumbnails.MICRO_KIND);
                        base64code = Base64Bitmap(firstPicture);
                    } else {
                        fileinfos.setFileType(Constants.FILE_AUDIO_TYPE);
                    }
                    fileinfos.setLocalPath(file.getAbsolutePath());
                    fileinfos.setName(file.getName());
                    fileinfos.setFileSize(file.length());
                    fileinfos.setImageBase64(base64code);
                    fileinfoList.add(fileinfos);
                }
                returnObject.setSuccess("true");
                returnObject.setDescription("返回成功！");
                returnObject.setFileInfos(fileinfoList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setSuccess("false");
            returnObject.setDescription("返回失败！");
        }
        String returnStr = JSONFactory.objectToJsonStr(returnObject);
        JSONObject json = null;
        try {
            json = new JSONObject(returnStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSUtil.execCallback(pWebview, CallBackID, json, JSUtil.OK, false);
    }

    /**
     * 橱窗显示图片，播放视频，播放录音文件
     *
     * @param parameter
     * @return
     */
    @SuppressWarnings("unchecked")
    public void browseMedia(IWebview pWebview, JSONArray array) {
        this.webView = pWebview.obtainWebview();
        activity = pWebview.getActivity();
        String CallBackID = array.optString(0);
        String parameter = array.optString(1);
        JsonObject jsonObject = JSONFactory.parseJsonStr(parameter);
        JsonObject returnObject = new JsonObject();
        ArrayList<String> filesList = new ArrayList<String>();
        ArrayList<String> thumbList = new ArrayList<String>();
        Log.i(TAG, "browseMedia: 1111");
        int index = 0;
        try {
            JsonArray jsonArray = jsonObject.get("allFiles").getAsJsonArray();
            JsonObject jsonObject1 = jsonObject.get("currentClick")
                    .getAsJsonObject();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject fileObject = jsonArray.get(i).getAsJsonObject();
                String fileType = fileObject.get("fileType").getAsString();
                String filePath = fileObject.get("filePath").getAsString();
                if (!filePath.contains("http://")) {
                    filePath = "file://" + filePath;
                }
                String thumbPath = "";
                Log.i(TAG, "browseMedia: " + fileObject.toString());
                try {
                    thumbPath = fileObject.get("image").getAsString();
                    if (thumbPath.length() > 200) {
                        thumbPath = "";
                    }
                    Log.i(TAG, "browseMedia: " + thumbPath);
                } catch (Exception e) {
                }
                thumbList.add(thumbPath);
                filesList.add(filePath);
            }
            String fileNamePath = jsonObject1.get("filePath").getAsString();
            for (int j = 0; j < filesList.size(); j++) {
                String fileName1 = filesList.get(j);
                if (fileName1.contains("file:///")) {
                    fileName1 = fileName1.substring(7);
                }
                if (fileName1.equals(fileNamePath)) {
                    index = j;
                    break;
                }
            }
            Intent intent = new Intent(activity, PagerThumbnailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("fileNamePath", filesList);
            bundle.putStringArrayList("thumbNamePath", thumbList);
            bundle.putInt("index", index);
            intent.putExtras(bundle);
            activity.startActivity(intent);
            returnObject.addProperty("success", "true");
            returnObject.addProperty("description", "播放成功！");
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.addProperty("success", "false");
            returnObject.addProperty("description", e.toString());
        }
        String returnStr = returnObject.toString();

        JSONObject json = null;
        try {
            json = new JSONObject(returnStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSUtil.execCallback(pWebview, CallBackID, json, JSUtil.OK, false);
    }

    /**
     * 获取手机定位成功后的地址列表
     */
    @SuppressWarnings("unchecked")
    public void getLocations(final IWebview pWebview, JSONArray array) {
//        this.webView = pWebview.obtainWebview();
//        activity = pWebview.getActivity();
//        final String CallBackID = array.optString(0);
//        String parameter = array.optString(1);
//        List<String> addresses = null;
//        final JsonObject returnObject = new JsonObject();
//        try {
//            final Location location = LocationService.getLocation();
//
//            if (location != null) {
//                final String keyPropery = "locationName";
//                final JsonArray js = new JsonArray();
//                String apiurl = Constants.GEOCODE_REGEORESTURL + "?location=" + Double.valueOf(location.getLongitude()) + "," + Double.valueOf(location.getLatitude()) + "&key=" + Constants.GaoDe_GEOCODE_KEY + "&radius=1000&extensions=all";
//                Log.e("debug", "开始定位");
//                new OkHttpUtil().callGet(apiurl, new OkHttpUtil.OkHttpCallBack() {
//                    @Override
//                    public void success(Response response) throws Exception {
//                        List<String> addresses = null;
//                        String json = response.body().string();
//                        response.body().close();
//                        JsonObject responseJsonObject = JSONFactory
//                                .parseJsonStr(json);
//                        if (responseJsonObject.get("status").getAsString().equals("0")) {
//                            throw new Exception("定位失败");
//                        } else {
//                            addresses = getAddressList(responseJsonObject);
//                            JsonObject jsonObject1 = null;
//                            for (int i = 0; i < addresses.size(); i++) {
//                                jsonObject1 = new JsonObject();
//                                jsonObject1.addProperty(keyPropery, addresses.get(i));
//                                js.add(jsonObject1);
//                            }
//                            JsonObject jsonObjectCoor = new JsonObject();
//                            jsonObjectCoor.addProperty("latitude", location.getLatitude());
//                            jsonObjectCoor
//                                    .addProperty("longitude", location.getLongitude());
//                            returnObject.addProperty("success", "true");
//                            returnObject.addProperty("description", "获取地址成功");
//                            returnObject.add("locations", js);
//                            returnObject.add("locationCoordinate", jsonObjectCoor);
//                            String returnStr = returnObject.toString();
//                            JSONObject json1 = null;
//                            try {
//                                json1 = new JSONObject(returnStr);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            JSUtil.execCallback(pWebview, CallBackID, json1, JSUtil.OK, false);
//                        }
//                    }
//
//                    @Override
//                    public void error(Request request, IOException e) {
//
//                    }
//                });
//
//
//            } else {
//                returnObject.addProperty("success", "false");
//                returnObject.addProperty("description", "定位失败！");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            returnObject.addProperty("success", "false");
//            returnObject.addProperty("description", e.toString());
//            String returnStr = returnObject.toString();
//            JSONObject json = null;
//            try {
//                json = new JSONObject(returnStr);
//            } catch (JSONException e1) {
//                e1.printStackTrace();
//            }
//            JSUtil.execCallback(pWebview, CallBackID, json, JSUtil.OK, false);
//        }

    }

    /**
     * 根据输入的文件路径，返回200*200缩略图的Base64编码
     *
     * @param parameter
     */

    @SuppressWarnings("unchecked")
    public void getThumbnails(IWebview pWebview, JSONArray array) {
        this.webView = pWebview.obtainWebview();
        activity = pWebview.getActivity();
        String CallBackID = array.optString(0);
        String parameter = array.optString(1);
        JsonObject jsonObject = JSONFactory.parseJsonStr(parameter);
        List<File> fileList = null;
        BrowerBase returnObject = new BrowerBase();
        try {
            if (jsonObject != null) {
                fileList = new ArrayList<File>();
                JsonArray fileNamePath = jsonObject.get("filesLocalPathArr")
                        .getAsJsonArray();
                String taskId = jsonObject.get("taskId").getAsString();
                returnObject.setTaskId(taskId);
                List<Fileinfos> fileinfoList = new ArrayList<Fileinfos>();
                for (int i = 0; i < fileNamePath.size(); i++) {
                    String base64code = null;
                    JsonObject fileNamejson = fileNamePath.get(i)
                            .getAsJsonObject();
                    String fileName = fileNamejson.get("path").getAsString();
                    String indexNO = fileNamejson.get("indexNO").getAsString();
                    File file = new File(fileName);
                    if (!file.exists()) {
                        throw new Exception("文件不存在");
                    }
                    Fileinfos fileinfos = new Fileinfos();
                    fileinfos.setIndexNO(indexNO);
                    if (MediaFile.isImageFileType(file.getAbsolutePath())) {
                        fileinfos.setFileType(Constants.FILE_IMAGE_TYPE);
                        base64code = imageToBase64thumbnail(file, 200, 200);
                    } else if (MediaFile
                            .isVideoFileType(file.getAbsolutePath())) {
                        fileinfos.setFileType(Constants.FILE_VIDEO_TYPE);
                        Bitmap firstPicture = getVideoThumbnail(
                                file.getAbsolutePath(),
                                Constants.THUMBNAILWIDTH,
                                Constants.THUMBNAILHEIGHT,
                                MediaStore.Images.Thumbnails.MICRO_KIND);
                        base64code = Base64Bitmap(firstPicture);
                        String[] returnoObjects = MediaFile.getPlayTime(file
                                .getAbsolutePath());
                        fileinfos.setDuration(new Duration(
                                (String) returnoObjects[0],
                                (String) returnoObjects[1]));
                    } else {
                        fileinfos.setFileType(Constants.FILE_AUDIO_TYPE);
                    }
                    fileinfos.setLocalPath(file.getAbsolutePath());
                    fileinfos.setName(file.getName());
                    fileinfos.setFileSize(file.length());
                    fileinfos.setThumbnail(base64code);
                    fileinfoList.add(fileinfos);
                }
                returnObject.setSuccess("true");
                returnObject.setDescription("返回成功！");
                returnObject.setFileInfos(fileinfoList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setSuccess("false");
            returnObject.setDescription("返回失败！");
        }
        String returnStr = JSONFactory.objectToJsonStr(returnObject);
        JSONObject json = null;
        try {
            json = new JSONObject(returnStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSUtil.execCallback(pWebview, CallBackID, json, JSUtil.OK, false);
    }

    /**
     * 通过拍照或者从图库中选择照片返回头像的缩略图 大的200*200 小的100*100
     *
     * @param parameter
     */
    @SuppressWarnings("unchecked")
    public void setUserProfile(IWebview pWebview, JSONArray array) {
        this.webView = pWebview.obtainWebview();
        activity = pWebview.getActivity();
        String CallBackID = array.optString(0);
        String parameter = array.optString(1);
        JsonObject jsonObject = JSONFactory.parseJsonStr(parameter);
        try {
            IApp app = pWebview.obtainFrameView().obtainApp();
            IMediaEventExt sysEventExt = IMediaEventExt.getInstance(app, pWebview,
                    CallBackID);
            sysEventExtWebView = sysEventExt;
            app.registerSysEventListener(sysEventExt,
                    SysEventType.onActivityResult);
            String actionName = jsonObject.get("actionName").getAsString();
            if (actionName.equals("takePhoto")) {
                this.cameraFile = takePhotoCrop(CallBackID);
            } else if (actionName.equals("pickMediaFile")) {
                takeLocalImageCrop(CallBackID);
            }
            this.fileType = Constants.FILE_IMAGE_TYPE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * 标准接口，根据传入的参数进行拍照，录制视频，或者录制音频操作
     *
     * @param parameter
     * @return
     */
    public void takeMedia(IWebview pWebview, JSONArray array) {
        this.webView = pWebview.obtainWebview();
        activity = pWebview.getActivity();
        String CallBackID = array.optString(0);
        String parameter = array.optString(1);
        JsonObject jsonObject = JSONFactory.parseJsonStr(parameter);
        try {
            IApp app = pWebview.obtainFrameView().obtainApp();
            IMediaEventExt sysEventExt = IMediaEventExt.getInstance(app, pWebview,
                    CallBackID);
            sysEventExtWebView = sysEventExt;
            app.registerSysEventListener(sysEventExt,
                    SysEventType.onActivityResult);
            String actionName = jsonObject.get("actionName").getAsString();
            if (actionName.equals(Constants.TAKE_PHOTO)) {
                this.cameraFile = standardTakePhoto();
                this.fileType = Constants.FILE_IMAGE_TYPE;
            } else if (actionName.equals(Constants.RECORD_VIDEO)) {
                Log.i(TAG, "takeMedia: ");
                this.cameraFile = standardRecordVideo(CallBackID);
                this.fileType = Constants.FILE_VIDEO_TYPE;
            } else if (actionName.equals(Constants.RECORD_AUDIO)) {
                int code = (int) new Date().getTime();
                PermissionUtil.checkPermission(new String[]{Manifest.permission.RECORD_AUDIO}, activity, new PermissionUtil.PermissionListener() {
                    @Override
                    public void permissionAllowed() {
                        MediaPlugin.this.fileType = Constants.FILE_AUDIO_TYPE;
                        Intent recordIntent = new Intent(activity,
                                RecordAudioActivity.class);
                        activity.startActivityForResult(recordIntent,
                                Constants.RECORD_STANDARDAUDIO_REQUEST);
                    }

                    @Override
                    public void permissionRefused() {
                        Toast.makeText(activity, "没有足够的权限，请进入权限设置更改权限", Toast.LENGTH_SHORT).show();
                    }
                }, code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * 标准接口，从图库选择图片
     *
     * @param parameter
     * @return
     */
    public String pickMediaFiles(IWebview pWebview, JSONArray array) {
        this.webView = pWebview.obtainWebview();
        activity = pWebview.getActivity();
        String CallBackID = array.optString(0);
        String parameter = array.optString(1);
        JsonObject jsonObject = JSONFactory.parseJsonStr(parameter);
        try {
            IApp app = pWebview.obtainFrameView().obtainApp();
            IMediaEventExt sysEventExt = IMediaEventExt.getInstance(app, pWebview,
                    CallBackID);
            sysEventExtWebView = sysEventExt;
            app.registerSysEventListener(sysEventExt,
                    SysEventType.onActivityResult);
            int code = (int) new Date().getTime();
            final int maxnum = jsonObject.get("allowSelectNum").getAsInt();
            PermissionUtil.checkPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, activity, new PermissionUtil.PermissionListener() {
                @Override
                public void permissionAllowed() {
                    Intent intent = new Intent(activity, PickImageActivity.class);
                    intent.putExtra("imgNum", maxnum);
                    activity.startActivityForResult(intent, Constants.STANDARD_FILE_SELECT_REQUEST);
                }

                @Override
                public void permissionRefused() {
                    Toast.makeText(activity, "没有足够的权限，请进入权限设置更改权限", Toast.LENGTH_SHORT).show();
                }
            }, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据传入的媒体文件单个播放
     *
     * @param parameter
     * @return
     */
    @SuppressWarnings("unchecked")
    public void playMedia(IWebview pWebview, JSONArray array) {
        this.webView = pWebview.obtainWebview();
        activity = pWebview.getActivity();
        String CallBackID = array.optString(0);
        String parameter = array.optString(1);
        JsonObject jsonObject = JSONFactory.parseJsonStr(parameter);
        JsonObject returnObject = new JsonObject();
        Intent intent = null;
        try {
            JsonObject jsonElement = jsonObject.get("fileInfo")
                    .getAsJsonObject();
            String fileType = jsonElement.get("fileType").getAsString();
            String filePath = jsonElement.get("filePath").getAsString();
            if (fileType.equals("1")) {
                intent = new Intent(activity, PlayerActivity.class);
                intent.putExtra("path", filePath);
            } else if (fileType.equals("2")) {
                intent = new Intent(activity, PlayAudioActivity.class);
                intent.putExtra("path", filePath);
            }
            activity.startActivity(intent);
            returnObject.addProperty("success", "true");
            returnObject.addProperty("description", "播放成功！");
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.addProperty("success", "false");
            returnObject.addProperty("description", e.toString());
        }
        String returnStr = returnObject.toString();
        JSONObject json = null;
        try {
            json = new JSONObject(returnStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSUtil.execCallback(pWebview, CallBackID, json, JSUtil.OK, false);
    }

    /**
     * 根据传入的参数进行拍照，录制视频，或者录制音频操作
     *
     * @param parameter
     * @return
     */
    public void doMedia(IWebview pWebview, JSONArray array) {
        activity = pWebview.getActivity();
        this.webView = pWebview.obtainWebview();
        //webView.loadUrl(path);
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        activity = pWebview.getActivity();
        String CallBackID = array.optString(0);
        String parameter = array.optString(1);
        JsonObject jsonObject = JSONFactory.parseJsonStr(parameter);
        try {
            IApp app = pWebview.obtainFrameView().obtainApp();
            IMediaEventExt sysEventExt = IMediaEventExt.getInstance(app, pWebview,
                    CallBackID);
            sysEventExtWebView = sysEventExt;
            app.registerSysEventListener(sysEventExt,
                    SysEventType.onActivityResult);
            String fileType = "";
            String actionName = jsonObject.get("actionName").getAsString();
            if (actionName.equals(Constants.TAKE_PHOTO)) {
                this.cameraFile = takePhoto(CallBackID);
                this.fileType = Constants.FILE_IMAGE_TYPE;
            } else if (actionName.equals(Constants.RECORD_VIDEO)) {
                this.cameraFile = recordVideo(CallBackID);
                this.fileType = Constants.FILE_VIDEO_TYPE;
            } else if (actionName.equals(Constants.RECORD_AUDIO)) {
                int code = (int) new Date().getTime();
                PermissionUtil.checkPermission(new String[]{Manifest.permission.RECORD_AUDIO}, activity, new PermissionUtil.PermissionListener() {
                    @Override
                    public void permissionAllowed() {
                        MediaPlugin.this.fileType = Constants.FILE_AUDIO_TYPE;
                        Intent recordIntent = new Intent(activity,
                                RecordAudioActivity.class);
                        activity.startActivityForResult(recordIntent, Constants.RECORD_STANDARDAUDIO_REQUEST);
                    }

                    @Override
                    public void permissionRefused() {
                        Toast.makeText(activity, "没有足够的权限，请进入权限设置更改权限", Toast.LENGTH_SHORT).show();
                    }
                }, code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
    /*************** 新增加的两个接口开始 ********************/
    /**
     * 清除缓存
     */
    public void clearCache(IWebview pWebview, JSONArray array) {
        this.webView = pWebview.obtainWebview();
        activity = pWebview.getActivity();
        String CallBackID = array.optString(0);
        JsonObject returnJsonObject = new JsonObject();
        try {
            int code = (int) new Date().getTime();
            PermissionUtil.checkPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, activity, new PermissionUtil.PermissionListener() {
                @Override
                public void permissionAllowed() {
                    String databasecache = "/data/data/"
                            + webView.getContext().getPackageName() + "/databases/";
                    String cacheDirPath = activity.getFilesDir().getAbsolutePath()
                            + Constants.APP_CACAHE_DIRNAME;
                    DataCleanManager.cleanApplicationData(
                            activity.getApplicationContext(), databasecache,
                            cacheDirPath);
                }

                @Override
                public void permissionRefused() {
                    Toast.makeText(activity, "没有足够的权限，请进入权限设置更改权限", Toast.LENGTH_SHORT).show();
                }
            }, code);
            returnJsonObject.addProperty("success", "true");
            returnJsonObject.addProperty("description", "清除缓存成功！");
        } catch (Exception e) {
            returnJsonObject.addProperty("success", "false");
            returnJsonObject.addProperty("description", "清除缓存失败！");
        }
        String returnStr = returnJsonObject.toString();
        JSONObject json = null;
        try {
            json = new JSONObject(returnStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSUtil.execCallback(pWebview, CallBackID, json, JSUtil.OK, false);
    }

    /**
     * 检测壳版本和js版本是否一致
     */
    public void checkjsVersion(IWebview pWebview, JSONArray array) {
        JsonObject returnJsonObject = new JsonObject();
        this.webView = pWebview.obtainWebview();
        activity = pWebview.getActivity();
        String CallBackID = array.optString(0);
        String parameter = array.optString(1);
        JsonObject jsonObject = JSONFactory.parseJsonStr(parameter);
        try {
            String jsVersion = jsonObject.get("jsVersion").getAsString();
            String jsVersionNum = "";
            String shellVersionNum = "";
            if (jsVersion != null && !jsVersion.equals("")) {
                jsVersionNum = getVersionNum(jsVersion);
            }
            shellVersionNum = getVersionNum(shellVersion);
            if (jsVersionNum.equals(shellVersionNum)) {
                returnJsonObject.addProperty("success", "true");
                returnJsonObject.addProperty("description",
                        "匹配成功。设备类型：ANDROID，js版本：" + jsVersion + "，shell版本："
                                + shellVersion + "!");
            } else {
                returnJsonObject.addProperty("success", "false");
                returnJsonObject.addProperty("description",
                        "匹配失败。设备类型：ANDROID，js版本：" + jsVersion + "，shell版本："
                                + shellVersion + "!");
            }
        } catch (Exception e) {
            returnJsonObject.addProperty("success", "false");
            returnJsonObject.addProperty("description", "匹配失败" + e.toString());
        }
        String returnStr = returnJsonObject.toString();
        JSONObject json = null;
        try {
            json = new JSONObject(returnStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSUtil.execCallback(pWebview, CallBackID, json, JSUtil.OK, false);
    }

    public void videoPreviewEdit(IWebview pWebview, JSONArray array) {
        this.webView = pWebview.obtainWebview();
        activity = pWebview.getActivity();
        Log.i(TAG, "videoPreviewEdit: " + webView.hashCode());
        String CallBackID = array.optString(0);
        String parameter = array.optString(1);
        JsonObject jsonObject = JSONFactory.parseJsonStr(parameter);
        try {
            IApp app = pWebview.obtainFrameView().obtainApp();
            IMediaEventExt sysEventExt = IMediaEventExt.getInstance(app, pWebview,
                    CallBackID);
            sysEventExtWebView = sysEventExt;
            app.registerSysEventListener(sysEventExt,
                    SysEventType.onActivityResult);
            final String indexNO = jsonObject.get("indexNO").getAsString();
            final String filePath = jsonObject.get("filePath").getAsString();
            int code = (int) new Date().getTime();
            PermissionUtil.checkPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, activity, new PermissionUtil.PermissionListener() {
                @Override
                public void permissionAllowed() {
                    Intent intent = null;
                    intent = new Intent(activity, PreviewEditActivity.class);
                    intent.putExtra("path", filePath);
                    intent.putExtra("indexNO", indexNO);
                    activity.startActivityForResult(intent, Constants.VIDEOEDIT_REQUEST);
                }

                @Override
                public void permissionRefused() {
                    Toast.makeText(activity, "没有足够的权限，请进入权限设置更改权限", Toast.LENGTH_SHORT).show();
                }
            }, code);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*************** 新增加接口 ********************/
    /**
     * 拨打电话
     *
     * @return
     * @throws InterruptedException
     */
    public void dialNumberUrl(IWebview pWebview, JSONArray array) {
        this.webView = pWebview.obtainWebview();
        JsonObject returnJsonObject = new JsonObject();
        activity = pWebview.getActivity();
        String CallBackID = array.optString(0);
        String parameter = array.optString(1);
        JsonObject jsonObject = JSONFactory.parseJsonStr(parameter);
        try {
            IApp app = pWebview.obtainFrameView().obtainApp();
            IMediaEventExt sysEventExt = IMediaEventExt.getInstance(app, pWebview,
                    CallBackID);
            sysEventExtWebView = sysEventExt;
            app.registerSysEventListener(sysEventExt,
                    SysEventType.onActivityResult);
            final String Number = jsonObject.get("phoneNumber").getAsString();
            int code = (int) new Date().getTime();
            PermissionUtil.checkPermission(new String[]{Manifest.permission.CALL_PHONE}, activity, new PermissionUtil.PermissionListener() {
                @Override
                public void permissionAllowed() {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Number));
                    activity.startActivity(intent);
                }

                @Override
                public void permissionRefused() {
                    Toast.makeText(activity, "没有足够的权限，请进入权限设置更改权限", Toast.LENGTH_SHORT).show();
                }
            }, code);
            returnJsonObject.addProperty("success", "true");
            returnJsonObject.addProperty("description", "");
        } catch (Exception e) {
            returnJsonObject.addProperty("success", "false");
            returnJsonObject.addProperty("description", "呼叫失败");
        }
        String returnStr = returnJsonObject.toString();
        JSONObject json = null;
        try {
            json = new JSONObject(returnStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSUtil.execCallback(pWebview, CallBackID, json, JSUtil.OK, false);

    }

    /*************** 新增加接口 ********************/
    /**
     * 发送短信
     *
     * @return
     */
    public void sendMessageUrl(IWebview pWebview, JSONArray array) {
        JsonObject returnJsonObject = new JsonObject();
        this.webView = pWebview.obtainWebview();
        activity = pWebview.getActivity();
        String CallBackID = array.optString(0);
        String parameter = array.optString(1);
        JsonObject jsonObject = JSONFactory.parseJsonStr(parameter);
        Intent intent = null;
        try {
            IApp app = pWebview.obtainFrameView().obtainApp();
            IMediaEventExt sysEventExt = IMediaEventExt.getInstance(app, pWebview,
                    CallBackID);
            sysEventExtWebView = sysEventExt;
            app.registerSysEventListener(sysEventExt,
                    SysEventType.onActivityResult);
            final String Number = jsonObject.get("phoneNumber").getAsString();
            final Uri smsToUri = Uri.parse("smsto:" + Number);
            intent = new Intent(Intent.ACTION_SENDTO, smsToUri); // 传递收短信的地址,需要知道
            activity.startActivity(intent);
            int code = (int) new Date().getTime();
            PermissionUtil.checkPermission(new String[]{Manifest.permission.BROADCAST_SMS}, activity, new PermissionUtil.PermissionListener() {
                @Override
                public void permissionAllowed() {
                    Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri); // 传递收短信的地址,需要知道
                    activity.startActivity(intent);
                }

                @Override
                public void permissionRefused() {
                    Toast.makeText(activity, "没有足够的权限，请进入权限设置更改权限", Toast.LENGTH_SHORT).show();
                }
            }, code);
            returnJsonObject.addProperty("success", "true");
            returnJsonObject.addProperty("description", "");
        } catch (Exception e) {
            returnJsonObject.addProperty("success", "false");
            returnJsonObject.addProperty("description", "发送失败");
        }
        String returnStr = returnJsonObject.toString();
        JSONObject json = null;
        try {
            json = new JSONObject(returnStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSUtil.execCallback(pWebview, CallBackID, json, JSUtil.OK, false);

    }


    public String getVersionNum(String version) {
        String versionarr[];
        String versionNum = "";
        if (version != null && !version.equals("")) {
            versionarr = version.split("\\.");
            versionNum = versionarr[0] + "." + versionarr[1];
        }
        return versionNum;
    }

    /*************** 新增加的两个接口结束 ********************/
    /**
     * 摄像
     *
     * @return
     * @throws InterruptedException
     */
    public File takePhoto(String CallBackID) throws InterruptedException {
        Log.e("debug", "开始摄像并调用摄像机");
        final File imageFile = this.getFileByDate("images", "jpg");
        int code = (int) new Date().getTime();
        PermissionUtil.checkPermission(new String[]{Manifest.permission.CAMERA}, activity, new PermissionUtil.PermissionListener() {
            @Override
            public void permissionAllowed() {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 构造intent
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                activity.startActivityForResult(cameraIntent, Constants.CAMERA_REQUEST);// 发出intent，并要求返回调用结果
            }

            @Override
            public void permissionRefused() {
                Toast.makeText(activity, "没有足够的权限，请进入权限设置更改权限", Toast.LENGTH_SHORT).show();
            }
        }, code);
        Log.e("debug", "图像保存并显示成功");
        return imageFile;
    }

    /**
     * 调用裁剪功能的拍照
     *
     * @return
     * @throws InterruptedException
     */
    public File takePhotoCrop(String CallBackId) throws InterruptedException {
        Log.e("debug", "开始摄像并调用摄像机");
        final File imageFile = this.getFileByDate("images", "jpg");
        this.cameraFile = imageFile;
        int code = (int) new Date().getTime();
        PermissionUtil.checkPermission(new String[]{Manifest.permission.BROADCAST_SMS}, activity, new PermissionUtil.PermissionListener() {
            @Override
            public void permissionAllowed() {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 构造intent
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                activity.startActivityForResult(cameraIntent, Constants.CROP_CAMERIMAGE_REQUEST);// 发出intent，并要求返回调用结果
            }

            @Override
            public void permissionRefused() {
                Toast.makeText(activity, "没有足够的权限，请进入权限设置更改权限", Toast.LENGTH_SHORT).show();
            }
        }, code);
        Log.e("debug", "图像保存并显示成功");
        return imageFile;
    }

    /**
     * 调用系统本地相册
     *
     * @return
     * @throws InterruptedException
     */
    public void takeLocalImageCrop(String CallBackId) throws InterruptedException {
        Log.e("debug", "开始从相册获取图片");
        int code = (int) new Date().getTime();
        PermissionUtil.checkPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, activity, new PermissionUtil.PermissionListener() {
            @Override
            public void permissionAllowed() {
                Intent imageIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activity.startActivityForResult(imageIntent, Constants.CROP_LOCALIMGES_REQUEST);// 发出intent，并要求返回调用结果
            }
            @Override
            public void permissionRefused() {
                Toast.makeText(activity, "没有足够的权限，请进入权限设置更改权限", Toast.LENGTH_SHORT).show();
            }
        }, code);
    }

    /**
     * 录制视频
     *
     * @return
     * @throws InterruptedException
     */
    public File recordVideo(String CallBackId) throws InterruptedException {
        Log.e("debug", "开始录像并调用摄像机");
        final File out = getFileByDate("videos", "mp4");
        int code = (int) new Date().getTime();
        PermissionUtil.checkPermission(new String[]{Manifest.permission.CAMERA}, activity, new PermissionUtil.PermissionListener() {
            @Override
            public void permissionAllowed() {
                Intent cameraVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);// 构造intent
                Log.i(TAG, "recordVideo: " + out);
                Uri uri = Uri.fromFile(out);
                cameraVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                activity.startActivityForResult(cameraVideoIntent, Constants.CAMERA_VIDEO_REQUEST);// 发出intent，并要求返回调用结果
            }

            @Override
            public void permissionRefused() {
                Toast.makeText(activity, "没有足够的权限，请进入权限设置更改权限", Toast.LENGTH_SHORT).show();
            }
        }, code);
        Log.e("debug", "图像保存并显示成功");
        return out;
    }

    /**
     * 摄像
     *
     * @return
     * @throws InterruptedException
     */
    public File standardTakePhoto() throws InterruptedException {
        Log.e("debug", "开始摄像并调用摄像机");
        final File imageFile = this.getFileByDate("images", "jpg");
        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 构造intent
        int code = (int) new Date().getTime();
        PermissionUtil.checkPermission(new String[]{Manifest.permission.CAMERA}, activity, new PermissionUtil.PermissionListener() {
            @Override
            public void permissionAllowed() {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                activity.startActivityForResult(cameraIntent, Constants.CAMERA_STANDARDREQUEST);
            }

            @Override
            public void permissionRefused() {
                Toast.makeText(activity, "没有足够的权限，请进入权限设置更改权限", Toast.LENGTH_SHORT).show();
            }
        }, code);
        Log.e("debug", "图像保存并显示成功");
        return imageFile;
    }

    /**
     * 录制视频
     *
     * @return
     * @throws InterruptedException
     */
    public File standardRecordVideo(String CallBackId) throws InterruptedException {
        Log.e("debug", "开始录像并调用摄像机");
        final Intent cameraVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);// 构造intent
        File out = getFileByDate("videos", "mp4");
        final Uri uri = Uri.fromFile(out);
        int code = (int) new Date().getTime();
        PermissionUtil.checkPermission(new String[]{Manifest.permission.CAMERA}, activity, new PermissionUtil.PermissionListener() {
            @Override
            public void permissionAllowed() {
                cameraVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                activity.startActivityForResult(cameraVideoIntent, Constants.CAMERA_STANDARDVIDEO_REQUEST);// 发出intent，并要求返回调用结果
            }

            @Override
            public void permissionRefused() {
                Toast.makeText(activity, "没有足够的权限，请进入权限设置更改权限", Toast.LENGTH_SHORT).show();
            }
        }, code);
        return out;
    }

    /**
     * 根据图像文件转换成图像的Base64
     *
     * @param imgfile
     * @return
     */
    public String imageToBase64(File imgfile) {
        InputStream in = null;
        String strBase64 = null;
        byte[] data = null;
        try {
            in = new FileInputStream(imgfile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        strBase64 = Base64.encodeToString(data, Base64.NO_WRAP);
        return strBase64;// 返回Base64编码过的字节数组字符串
    }

    /**
     * 把Bitmap的图像缩放成指定宽度和高度的图像
     *
     * @param source
     * @param width
     * @param height
     * @return
     */
    public Bitmap scaleBitMap(Bitmap source, int width, int height) {
        Bitmap target = null;
        try {
            target = Bitmap.createBitmap(width, height, source.getConfig());
            Canvas canvas = new Canvas(target);
            canvas.drawBitmap(source, null, new Rect(0, 0, target.getWidth(),
                    target.getHeight()), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return target;

    }

    /**
     * 根据经度和纬度获取中文地址列表
     *
     * @param latitude
     * @param longitude
     * @return
     */
    public List<String> getAddress(double latitude, double longitude) {
        String apiurl = Constants.GEOCODE_REGEORESTURL + "?location="
                + longitude + "," + latitude + "&key="
                + Constants.GaoDe_GEOCODE_KEY + "&radius=1000&extensions=all";
        BufferedReader br = null;
        HttpClient httpclient = new DefaultHttpClient();
        List<String> addressList = null;
        Log.e("debug", "开始定位");
        try {
            HttpGet httpget = new HttpGet(apiurl);
            HttpResponse response = httpclient.execute(httpget);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                InputStream inputStream = response.getEntity().getContent();
                br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer bufferStr = new StringBuffer();
                String line;
                while ((line = br.readLine()) != null) {
                    bufferStr.append(line);
                }
                JsonObject responseJsonObject = JSONFactory
                        .parseJsonStr(bufferStr.toString());
                if (responseJsonObject.get("status").getAsString().equals("0")) {
                    throw new Exception("定位失败");
                } else {
                    addressList = getAddressList(responseJsonObject);
                }
            } else {
                throw new Exception("定位异常，清查看网络!");
            }

        } catch (Exception e) {
            Log.e("debug", e.toString());// TODO Auto-generated catch block
        } finally {
            try {
                httpclient.getConnectionManager().shutdown();
            } catch (Exception ignore) {
                Log.e("debug", ignore.toString());
            }
        }
        return addressList;
    }

    /**
     * 解析调用高德地址解析的API返回结果的json字符串, 获取地址列表信息
     *
     * @param responseJsonObject
     * @return List<String>
     */
    public List<String> getAddressList(JsonObject responseJsonObject) {
        List<String> addressList = null;
        try {
            JsonObject regeocode = responseJsonObject.get("regeocode")
                    .getAsJsonObject();
            JsonObject addressComjJsonObject = regeocode
                    .get("addressComponent").getAsJsonObject();
            JsonArray poisjsonArray = regeocode.get("pois").getAsJsonArray();
            String province = addressComjJsonObject.get("province")
                    .getAsString();
            String city = "";
            if (!province.contains("北京") && !province.contains("上海")
                    && !province.contains("天津") && !province.contains("重庆")) {
                city = addressComjJsonObject.get("city").getAsString();
            }
            String district = addressComjJsonObject.get("district")
                    .getAsString();
            String township = addressComjJsonObject.get("township")
                    .getAsString();
            addressList = new ArrayList<String>();
            for (int i = 0; i < poisjsonArray.size(); i++) {
                String addressStr = "";
                JsonObject poisJsonObject = poisjsonArray.get(i)
                        .getAsJsonObject();
                String address = "";
                JsonElement poisElement = poisJsonObject.get("address");
                if (poisElement.isJsonObject()) {
                    address = poisElement.getAsString();
                }
                String name = poisJsonObject.get("name").getAsString();
                addressStr = name;
                addressList.add(addressStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addressList;
    }

    /**
     * 把图像缩放成指定大小，然后转换为Base64串返回
     *
     * @param imgfile
     * @param width
     * @param height
     * @return
     */
    public String imageToBase64thumbnail(File imgfile, int width, int height) {
        String base64str = null;
        try {
            Bitmap bmp = CustomBitmapFactory.decodeBitmap(imgfile
                    .getAbsolutePath());
            Bitmap thumbnail = scaleBitMap(bmp, width, height);
            base64str = Base64Bitmap(thumbnail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base64str;
    }

    /**
     * 根据输入的文件夹和文件后缀名生成文件
     *
     * @param fileDir
     * @param fileExtType
     * @return
     */
    public File getFileByDate(String fileDir, String fileExtType) {
        File fileout = null;
        try {
            File out = null;
            SimpleDateFormat sDateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            String date = sDateFormat.format(new Date());
            date = date.replaceAll(" |:|-", "");
            String uploadPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + fileDir + "/";
            out = new File(uploadPath);
            if (!out.exists()) {
                out.mkdirs();
            }
            String uplaodFileName = date.toString() + "." + fileExtType;
            fileout = new File(uploadPath, uplaodFileName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Log.i(TAG, "getFileByDate: " + fileout.getAbsolutePath());
        return fileout;
    }

    public String getTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new Date());
        date = date.replaceAll(" |:|-", "");
        date = date.substring(8);
        return date;
    }

    /**
     * 根据视频路径抽取该视频的首帧图片
     *
     * @param videoPath
     * @param width
     * @param height
     * @param kind
     * @return
     */
    private Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                     int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 把bitmap的图片转换成Base64串
     *
     * @param bitmap
     * @return
     */
    public String Base64Bitmap(Bitmap bitmap) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
            bStream.flush();
            bStream.close();
            bytes = bStream.toByteArray();
        } catch (Exception e) {
            Log.e("debug", e.toString());
        }
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }


    /**
     * 定位初始化并且实现定位功能的及时更新
     */
    public void initLocation(IWebview pWebview, JSONArray array) {
        activity = pWebview.getActivity();
        locationIWebView = pWebview;
        locationCallbackId = array.optString(0);
        JSUtil.execCallback(locationIWebView, locationCallbackId,
                "true", JSUtil.OK, false);

    }

    public void checkLocalAppVersion(IWebview pWebview, JSONArray array) {
        String CallBackID = array.optString(0);
        String parameter = array.optString(1);
        JsonObject jsonObject = JSONFactory.parseJsonStr(parameter);
        String userId = jsonObject.get("AppName").getAsString();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void startUpdateLocation(IWebview pWebview, JSONArray array) {
//        Log.i(TAG, "startUpdateLocation: ");
//        if (firstStartUpLocation) {
//            firstStartUpLocation = false;
//            LocationService.setIsOpen(true);
//            JsonObject returnJsonObject = new JsonObject();
//            this.webView = pWebview.obtainWebview();
//            activity = pWebview.getActivity();
//            String CallBackID = array.optString(0);
//            String parameter = array.optString(1);
//            JsonObject jsonObject = JSONFactory.parseJsonStr(parameter);
//            Intent intent = null;
//            try {
//                IApp app = pWebview.obtainFrameView().obtainApp();
//                IMediaEventExt sysEventExt = IMediaEventExt.getInstance(app, pWebview,
//                        CallBackID);
//                sysEventExtWebView = sysEventExt;
//                app.registerSysEventListener(sysEventExt,
//                        SysEventType.onActivityResult);
//                String userId = jsonObject.get("userId").getAsString();
//                String userName = jsonObject.get("userName").getAsString();
//                String tenantId = "";
//                try {
//                    tenantId = jsonObject.get("tenantId").getAsString();
//                } catch (Exception e) {
//                }
//                String locationUrl = jsonObject.get("locationURL").getAsString();
//                if (locationUrl.contains("/")) {
//
//                } else {
//                    locationUrl = "http://" + locationUrl + "/app/api/sendGpsInfos";
//                }
//                intent = new Intent(activity, LocationService.class);
//                intent.putExtra("userId", userId);
//                intent.putExtra("tenantId", tenantId);
//                intent.putExtra("userName", userName);
//                intent.putExtra("locationUrl", locationUrl);
//                activity.startService(intent);
//                returnJsonObject.addProperty("success", "true");
//                returnJsonObject.addProperty("description", "");
//            } catch (Exception e) {
//                returnJsonObject.addProperty("success", "false");
//                returnJsonObject.addProperty("description", "启动失败");
//            }
//            String returnStr = returnJsonObject.toString();
//            JSONObject json = null;
//            try {
//                json = new JSONObject(returnStr);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            JSUtil.execCallback(pWebview, CallBackID, json, JSUtil.OK, false);
//        } else {
//            Log.i(TAG, "startUpdateLocation: LocationService.setIsOpen");
//            LocationService.setIsOpen(true);
//            try {
//                JsonObject returnJsonObject = new JsonObject();
//                String CallBackID = array.optString(0);
//                String parameter = array.optString(1);
//                JsonObject jsonObject = JSONFactory.parseJsonStr(parameter);
//                String userId = jsonObject.get("userId").getAsString();
//                String userName = jsonObject.get("userName").getAsString();
//                String tenantId = "";
//                try {
//                    tenantId = jsonObject.get("tenantId").getAsString();
//                } catch (Exception e) {
//                }
//                String locationUrl = jsonObject.get("locationURL").getAsString();
//                if (!locationUrl.contains("/")) {
//                    locationUrl = "http://" + locationUrl + "/app/api/sendGpsInfos";
//                }
//                LocationService.setUserId(userId);
//                LocationService.setUserName(userName);
//                LocationService.setPostUrl(locationUrl);
//                LocationService.setTenantId(tenantId);
//            } catch (Exception e) {
//                Log.i(TAG, "startUpdateLocation: " + e.toString());
//            }
//        }
    }

    public void stopUpdateLocation(IWebview pWebview, JSONArray array) {
        Log.i(TAG, "stopUpdateLocation: ");
        SDK_WebApp.isFromGT = false;
        SDK_WebApp.messageContent = "";
//        LocationService.setIsOpen(false);
    }

    int pro = 0;

    public void loadProgressBar(IWebview pWebview, JSONArray array) {
        progressBar = SDK_WebApp.getProgressBar();
        if (progressBar == null) {
            return;
        }
        Log.i(TAG, "loadProgressBar: " + Thread.currentThread().getName());
        progressBar.setY(-10.0f);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
        webViewProgress = 0;
        progressIncrement = 1000;
        updateProgressBar();
    }

    public void updateProgressBar() {
        webViewProgress = webViewProgress + progressIncrement;
        progressIncrement = progressIncrement - 8;
        progressBar.setProgress(webViewProgress / 800);
        if (progressIncrement > 0 && webViewProgress < 80000) {
            handlerUpdate.sendEmptyMessageDelayed(UPDATEPROGRESSBAR, 50);
        } else if (webViewProgress >= 80000) {
            progressBar.setProgress(100);
            progressBar.setVisibility(View.GONE);
            progressIncrement = 0;
            webViewProgress = 0;
        }
    }

    public void removeProgressBar(IWebview pWebview, JSONArray array) {
        String parameter = array.optString(1);
        JsonObject jsonObject = JSONFactory.parseJsonStr(parameter);
        String headerOffset = jsonObject.get("endMold").getAsString();
        int model = Integer.parseInt(headerOffset);
        if (model == 1) {
            //正常退出
            progressIncrement = 2000;
            updateProgressBar();
        } else {
            //直接退出
            progressBar.setVisibility(View.GONE);
            progressIncrement = 0;
            webViewProgress = 0;
        }

    }

    /**
     * 查询是否是通过通知栏点击打开的应用
     *
     * @param parameter
     * @return
     */

    public void querySkip(IWebview pWebview, JSONArray array) {
        JsonObject returnJsonObject = new JsonObject();
        activity = pWebview.getActivity();
        this.webView = pWebview.obtainWebview();
        String CallBackID = array.optString(0);
        boolean fromGT = SDK_WebApp.isFromGT();
        String content = SDK_WebApp.getMessageContent();
        //geturl(content);
        Log.i(TAG, "querySkip: " + fromGT);
        Log.i(TAG, "querySkip: " + content);
        JsonObject jsonObject = JSONFactory.parseJsonStr(content);
        String url = "";
        String appId = "";
        String classifyId = "";
        String guid = "";
        String classifyName = "";

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
        if (fromGT) {
            returnJsonObject.addProperty("loadMsg", "true");
            returnJsonObject.addProperty("messageUrl", url);
            returnJsonObject.addProperty("classifyId", classifyId);
            returnJsonObject.addProperty("classifyName", classifyName);
            returnJsonObject.addProperty("guid", guid);
            returnJsonObject.addProperty("appId", appId);
        } else {
            returnJsonObject.addProperty("loadMsg", "false");
            returnJsonObject.addProperty("messageUrl", "");
            returnJsonObject.addProperty("appId", appId);
        }
        SDK_WebApp.isFromGT = false;
        SDK_WebApp.messageContent = "";
        JSONObject json = null;
        try {
            json = new JSONObject(returnJsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSUtil.execCallback(pWebview, CallBackID, json, JSUtil.OK, false);
    }

    public void setWebview(IWebview pWebview, JSONArray array) {
        indexWebView = pWebview;
    }

    public void exit(IWebview pWebview, JSONArray array) {
        SDK_WebApp.isFromGT = false;
        SDK_WebApp.messageContent = "";
        indexWebView = null;
        //登出操作
    }

    public void updatePortal(IWebview pWebview, JSONArray array) {
//        new HtmlUpdateUtil(pWebview.getActivity()).checkPortalVersion();
    }

    public void getLoginPath(IWebview pWebview, JSONArray array) {
//        String htmlLoginPath = new HtmlUpdateUtil(pWebview.getActivity()).getHtmlLoginPath();
//        Log.i(TAG, "getLoginPath: " + htmlLoginPath);
//        JSONObject returnJsonObject = new JSONObject();
//        try {
//            returnJsonObject.put("path", htmlLoginPath);
//            returnJsonObject.put("success", true);
//            JSUtil.execCallback(pWebview, array.optString(0), returnJsonObject, JSUtil.OK, false);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public void bindGeTuiCid(IWebview pWebview, JSONArray array) {
        Log.i(TAG, "bindGeTuiCid: ");
        final JsonObject returnJsonObject = new JsonObject();
        this.webView = pWebview.obtainWebview();
        activity = pWebview.getActivity();
        String CallBackID = array.optString(0);
        String parameter = array.optString(1);
        JsonObject jsonObject = JSONFactory.parseJsonStr(parameter);
        Intent intent = null;
        try {
            String userId = jsonObject.get("userid").getAsString();
            String workNo = jsonObject.get("workNo").getAsString();
            String locationUrl = jsonObject.get("bindGeTuiUrl").getAsString();
            String tenantCode = jsonObject.get("tenantCode").getAsString();
            if (locationUrl.contains("/")) {
            } else {
                locationUrl = "http://" + locationUrl + "/messagecenter/api/app/alias/bind";
            }
            String clientid = "";
            bindInfo = new BindGeTuiBean(Canstant.GETUI_APP_ID, userId, clientid, workNo, tenantCode);
            String json = new Gson().toJson(bindInfo);
            OkHttpUtil okHttpUtil = new OkHttpUtil();
            gtUrl = locationUrl;
            Log.i(TAG, "bindGeTuiCid: " + locationUrl);
            Log.i(TAG, "BindGeTuiCid: " + json);
            okHttpUtil.call(locationUrl, json, new OkHttpUtil.OkHttpCallBack() {
                @Override
                public void success(Response response) {
                    String string = "";
                    try {
                        string = response.body().string();
                        Log.i(TAG, "sucess: " + string);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (string.contains("true")) {
                        returnJsonObject.addProperty("success", "true");
                        returnJsonObject.addProperty("description", "");
                    } else {
                        returnJsonObject.addProperty("success", "false");
                        returnJsonObject.addProperty("description", "绑定失败");
                    }

                }

                @Override
                public void error(Request request, IOException e) {
                    returnJsonObject.addProperty("success", "false");
                    returnJsonObject.addProperty("description", "绑定失败");
                }
            });
        } catch (Exception e) {
            returnJsonObject.addProperty("success", "false");
            returnJsonObject.addProperty("description", "绑定失败");
            JSONObject json = null;
            try {
                json = new JSONObject(returnJsonObject.toString());
                JSUtil.execCallback(pWebview, CallBackID, json, JSUtil.OK, false);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        String returnStr = returnJsonObject.toString();
        JSONObject json = null;
        try {
            json = new JSONObject(returnStr);
            JSUtil.execCallback(pWebview, CallBackID, json, JSUtil.OK, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void showMessgae(String url, String appId, String classifyId, String guid, String classifyName) {
        Log.i(TAG, "showMessgae: " + url);
        if (indexWebView != null) {
            String js = "openMsgUrl(\"" + url + "\",\"" + appId + "\",\"" + classifyId + "\",\"" + guid + "\",\"" + classifyName + "\")";
            indexWebView.evalJS(js);
        }
    }

    public static void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        permissionsResultListener.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public interface PermissionsResultListener {
        void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults);
    }

}

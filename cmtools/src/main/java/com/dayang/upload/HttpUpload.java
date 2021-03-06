package com.dayang.upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.dayang.common.Canstant;
import com.dayang.common.Constants;
import com.dayang.common.JSONFactory;
import com.dayang.info.FileAndIndexInfo;
import com.google.gson.JsonObject;

/**
 * 此类用于实现http上传
 *
 * @author renyuwei
 */
public class HttpUpload extends UploadFileThread {
    private String httpUrl;
    private String fileName;
    private int uploadtype;
    private Handler handler;
    private List<FileAndIndexInfo> fileNameList;
    private String taskId;
    private String customParam;
    private String fileStatusNotifyURL;
    String fileSessionId;

    public HttpUpload(String url, String fileStatusNotifyURL, Handler handler, int uploadType,
                      String singleFilePath, List<FileAndIndexInfo> fileNameList, String taskId, Activity activity, String tenantId, String customParam) {
        super(activity);
        this.httpUrl = url;
        this.fileName = singleFilePath;
        this.fileNameList = fileNameList;
        this.uploadtype = uploadType;
        this.handler = handler;
        this.customParam = customParam;
        this.taskId = taskId;
        this.fileStatusNotifyURL = fileStatusNotifyURL;
        super.tenantId = tenantId;
    }

    String TAG = Canstant.TAG;

    /**
     * 批量上传
     */
    @Override
    public boolean uploadAllFiles() {
        boolean uploadFlag = false;
        BufferedReader br = null;
        HttpClient httpclient = new DefaultHttpClient();
        Log.e(TAG, "http开始上传");
        try {
            HttpPost httppost = new HttpPost(this.httpUrl);
            if (fileNameList.size() <= 0) {
                throw new Exception("没有可上传的文件！");
            }
            for (int i = 0; i < fileNameList.size(); i++) {
                String uploadfilename = fileNameList.get(i).filePath;
                String fileName = getFileNameFromPath(uploadfilename);
            }
            for (FileAndIndexInfo fileName : fileNameList) {
                fileSessionId = fileName.fileSessionId;
                if (fileName.filePath.equals("")) continue;
                StringBuffer bufferStr = new StringBuffer();
                File uploadFile = new File(fileName.filePath);
                FileBody bin = new FileBody(uploadFile, "utf-8");
                Log.i(TAG, "uploadAllFiles: " + uploadFile.length() / 1024);
                StringBody taskId = new StringBody(this.taskId);
                StringBody tenantId = new StringBody(this.tenantId);
                MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName("UTF-8"));
                reqEntity.addPart("filename", bin);// filename为请求后台的File upload;属性
                reqEntity.addPart("taskId", taskId);
                if (!this.tenantId.equals("")) {
                    reqEntity.addPart("tenantId", tenantId);
                }
                httppost.setEntity(reqEntity);
                Log.i(TAG, "uploadAllFiles: " + "执行上传");
                HttpResponse response = null;
                try {
                    response = httpclient.execute(httppost);
                } catch (Exception e) {
                    response = null;
                    Log.e(TAG, "uploadAllFiles: " + e.toString());
                }
                Log.i(TAG, "uploadAllFiles: " + "上传完毕");
                int statusCode = 0;
                if (response != null) {
                    statusCode = response.getStatusLine().getStatusCode();
                }
                Log.i(TAG, "uploadAllFiles: " + statusCode);
                if (statusCode == HttpStatus.SC_OK) {
                    InputStream inputStream = response.getEntity().getContent();
                    br = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = br.readLine()) != null) {
                        bufferStr.append(line);
                    }
                    Log.i(TAG, "uploadAllFiles: " + bufferStr);
                    try {
                        JsonObject responseJsonObject = JSONFactory.parseJsonStr(bufferStr.toString());
                        String success = responseJsonObject.get("success").getAsString();
                        String description = responseJsonObject.get("description").getAsString();
                        if (!success.equals("true")) {
                            uploadFlag = true;
                            this.fileStatusNotifyCallBack(fileStatusNotifyURL, getNewHttpNotifyRequestParam(fileSessionId, "", false, fileName.filePath, this.taskId, customParam));
                        } else {
                            this.fileStatusNotifyCallBack(fileStatusNotifyURL, getNewHttpNotifyRequestParam(fileSessionId, "", true, fileName.filePath, this.taskId, customParam));
                        }
                        Log.d("debug", description);
                    } catch (Exception e) {
                        this.fileStatusNotifyCallBack(fileStatusNotifyURL, getNotifyRequestParam(false, fileName.filePath, this.taskId));
                    }
                } else {
                    this.fileStatusNotifyCallBack(fileStatusNotifyURL, getNotifyRequestParam(false, fileName.filePath, this.taskId));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());// TODO Auto-generated catch block
            Log.i(TAG, "uploadAllFiles: " + "结果false");
        } finally {
            try {
                httpclient.getConnectionManager().shutdown();
            } catch (Exception ignore) {
                Log.e("debug", ignore.toString());
            }
        }
        return uploadFlag;
    }

    /**
     * 单个文件上传
     */

    @Override
    public boolean uploadSingleFile() {
        boolean uploadFlag = false;
        HttpClient httpclient = new DefaultHttpClient();
        Log.e(TAG, "http单个开始上传");
        try {
            HttpPost httppost = new HttpPost(this.httpUrl);
            File uploadFile = new File(this.fileName);
            FileBody bin = new FileBody(uploadFile);
            StringBody taskId = new StringBody(this.taskId);
            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("filename", bin);// file为请求后台的File upload;属性
            reqEntity.addPart("taskId", taskId);
            httppost.setEntity(reqEntity);
            HttpResponse response = httpclient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                System.out.println("服务器正常响应.....");
                Log.i(TAG, "uploadSingleFile: " + "结果只为true");
                uploadFlag = true;
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
        this.fileStatusNotifyCallBack(fileStatusNotifyURL, getNotifyRequestParam(uploadFlag, this.fileName, this.taskId));
        return uploadFlag;
    }

    /**
     * 用于上传和上传后的消息通知
     */
    @Override
    public void run() {
        boolean upload = false;
        Log.i(TAG, "run: " + "http");
        try {
            if (uploadtype == Constants.UPLOADSINGLE) {
                Log.i(TAG, "run: " + "单个上传");
                upload = this.uploadSingleFile();
                if (upload) {
                    this.handler.sendEmptyMessage(Constants.UPLOADSUCCESS);
                } else {
                    this.handler.sendEmptyMessage(Constants.UPLOADFAILTURE);
                }
            } else if (uploadtype == Constants.UPLOADMUTIPLE) {
                Log.i(TAG, "run: " + "多个上传");
                upload = this.uploadAllFiles();
                if (upload) {
                    this.handler.sendEmptyMessage(Constants.UPLOADSUCCESS);
                } else {
                    this.handler.sendEmptyMessage(Constants.UPLOADFAILTURE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

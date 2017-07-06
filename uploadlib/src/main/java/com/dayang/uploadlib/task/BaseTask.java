package com.dayang.uploadlib.task;

import android.os.AsyncTask;
import android.util.Log;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by 冯傲 on 2017/6/14.
 * e-mail 897840134@qq.com
 */

public abstract class BaseTask implements Runnable {
    private static final String TAG = "cmtools_log";
    String tenantId = "";
    private RequestErrorTask requestErrorTask;

    public BaseTask() {
        requestErrorTask = new RequestErrorTask();
    }

    /**
     * 上传成功后向服务后台发起通知的请求参数封装
     *
     * @param uploadStatus
     * @param localpath
     * @param taskid
     * @return
     */
    public String getNotifyRequestParam(boolean uploadStatus, String localpath, String taskid) {
        JSONObject jsonObject = new JSONObject();
        try {
            int uploadStatusInt = 1;
            jsonObject.put("taskId", taskid);
            jsonObject.put("localPath", localpath);
            if (uploadStatus) {
                uploadStatusInt = 0;
            } else {
                uploadStatusInt = 1;
            }
            jsonObject.put("fileStatus", uploadStatusInt);
            if (!(tenantId == null || tenantId.equals(""))) {
                jsonObject.put("tenantId", tenantId);
            }
        } catch (Exception e) {
            Log.d("debug", e.toString());
        }
        return jsonObject.toString();
    }

    public String getNewHttpNotifyRequestParam(String fileSessionId, String fileNewName, boolean uploadStatus, String localPath, String taskId, String customParam) {
        JSONObject jsonObject = new JSONObject();
        try {
            File file = new File(localPath);
            String fileSize = file.length() + "";
            String fileName = file.getName();
            int uploadStatusInt;
            jsonObject.put("taskId", taskId);
            jsonObject.put("localPath", localPath);
            if (uploadStatus) {
                uploadStatusInt = 0;
            } else {
                uploadStatusInt = 1;
            }
            jsonObject.put("fileStatus", uploadStatusInt);
            jsonObject.put("fileSessionId", fileSessionId);
            jsonObject.put("fileNewName", fileNewName);
            jsonObject.put("fileSize", fileSize);
            jsonObject.put("fileName", fileName);
            if (!(tenantId == null || tenantId.equals(""))) {
                jsonObject.put("tenantId", tenantId);
            }
            if (!(customParam == null || customParam.equals(""))) {
                jsonObject.put("customParam", customParam);
            }
        } catch (Exception e) {
            Log.d("debug", e.toString());
        }
        return jsonObject.toString();
    }

    /**
     * 向服务后台通知上传状态
     *
     * @param url          通知的接口地址
     * @param requestParam 通知的请求参数
     * @return
     */
    public boolean fileStatusNotifyCallBack(String url, String requestParam) {
        boolean notifystatus = false;
        HttpClient httpclient = new DefaultHttpClient();
        Log.e(TAG, "开始上传通知");
        try {
            BufferedReader br;
            HttpPost httppost = new HttpPost(url);
            Log.i(TAG, "上传通知 requestParam: " + requestParam);
            StringEntity reqEntity = new StringEntity(requestParam, "utf-8");
            reqEntity.setContentEncoding("UTF-8");
            reqEntity.setContentType("application/String");
            httppost.setEntity(reqEntity);
            HttpResponse response = httpclient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                notifystatus = true;
                StringBuffer bufferStr = new StringBuffer();
                InputStream inputStream = response.getEntity().getContent();
                br = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = br.readLine()) != null) {
                    bufferStr.append(line);
                }
                JSONObject responseJsonObject = new JSONObject(bufferStr.toString());
                String success = responseJsonObject.getString("success");
                String description = responseJsonObject.getString("description");
                Log.i(TAG, "上传通知 success: " + success);
                Log.i(TAG, "上传通知 description: " + description);
            }
            throw new Exception();
        } catch (Exception e) {
            if (!notifystatus) {
                requestErrorTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, requestParam);
            }
        } finally {
            try {
                httpclient.getConnectionManager().shutdown();
            } catch (Exception ignore) {
                Log.e("debug", "上传通知" + ignore.toString());
            }
        }
        return notifystatus;
    }

    public String getFileNameFromPath(String path) {
        String[] split = path.split("/");
        String s = split[split.length - 1];
        return s;
    }
}

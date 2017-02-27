package com.dayang.upload;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dayang.common.Canstant;
import com.dayang.common.JSONFactory;
import com.dayang.upload.netprocess.TaskData;
import com.dayang.upload.netprocess.TaskManager;
import com.google.gson.JsonObject;

/**
 * 上传的抽象类
 *
 * @author renyuwei
 */
public abstract class UploadFileThread extends Thread {
    private static final String TAG = Canstant.TAG;
    public Activity activity;
    private TaskManager taskManager;
    String tenantId = "";

    public abstract boolean uploadAllFiles();

    public abstract boolean uploadSingleFile();


    public UploadFileThread(Activity activity) {
        taskManager = new TaskManager(activity);
        this.activity = activity;
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
        JsonObject jsonObject = new JsonObject();
        try {
            int uploadStatusInt = 1;
            jsonObject.addProperty("taskId", taskid);
            jsonObject.addProperty("localPath", localpath);
            if (uploadStatus) {
                uploadStatusInt = 0;
            } else {
                uploadStatusInt = 1;
            }
            jsonObject.addProperty("fileStatus", uploadStatusInt);
            if (!(tenantId == null || tenantId.equals(""))) {
                jsonObject.addProperty("tenantId", tenantId);
            }
        } catch (Exception e) {
            Log.d("debug", e.toString());
        }
        return jsonObject.toString();
    }

    public String getNewHttpNotifyRequestParam(String fileSessionId, String fileNewName, boolean uploadStatus, String localpath, String taskid) {
        JsonObject jsonObject = new JsonObject();
        try {
            int uploadStatusInt = 1;
            jsonObject.addProperty("taskId", taskid);
            jsonObject.addProperty("localPath", localpath);
            if (uploadStatus) {
                uploadStatusInt = 0;
            } else {
                uploadStatusInt = 1;
            }
            jsonObject.addProperty("fileStatus", uploadStatusInt);
            jsonObject.addProperty("fileSessionId", fileSessionId);
            jsonObject.addProperty("fileNewName", fileNewName);
            if (!(tenantId == null || tenantId.equals(""))) {
                jsonObject.addProperty("tenantId", tenantId);
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
        Log.e("debug", "开始上传通知");
        try {
            BufferedReader br = null;
            HttpPost httppost = new HttpPost(url);
            Log.i(TAG, "fileStatusNotifyCallBack: " + requestParam);
            StringEntity reqEntity = new StringEntity(requestParam, "utf-8");
            reqEntity.setContentEncoding("UTF-8");
            reqEntity.setContentType("application/String");
            httppost.setEntity(reqEntity);
            HttpResponse response = httpclient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                StringBuffer bufferStr = new StringBuffer();
                InputStream inputStream = response.getEntity().getContent();
                br = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = br.readLine()) != null) {
                    bufferStr.append(line);
                }
                JsonObject responseJsonObject = JSONFactory.parseJsonStr(bufferStr.toString());
                String success = responseJsonObject.get("success").getAsString();
                String description = responseJsonObject.get("description").getAsString();
                if (!success.equals("true")) {
                    throw new Exception("通知" + description);
                } else {
                    notifystatus = true;
                    Log.e("debug", "通知" + description);
                }

            }
        } catch (Exception e) {
            Log.e("debug", "上传通知" + e.toString());// TODO Auto-generated catch block
        } finally {
            try {
                httpclient.getConnectionManager().shutdown();
            } catch (Exception ignore) {
                Log.e("debug", "上传通知" + ignore.toString());
            }
        }
        /*if (!notifystatus) {
            boolean netstatus = NetworkStatusMonitor.netCheck(activity);
			if (!netstatus) {
				this.saveAndStartMonitor(url, requestParam);
				taskManager.startMonitor();
			}
		}*/
        return notifystatus;
    }
    public String getFileNameFromPath(String path) {
        String[] split = path.split("/");
        String s = split[split.length - 1];
        return s;
    }

    public void saveAndStartMonitor(String url, String requestParam) {
        TaskData taskData = new TaskData();
        JsonObject jsonObject = JSONFactory.parseJsonStr(requestParam);
        taskData.setFileStatusNotifyURL(url);
        taskData.setTaskId(jsonObject.get("taskId").getAsString());
        taskData.setLocalPath(jsonObject.get("localPath").getAsString());
        taskData.setFileStatus(jsonObject.get("fileStatus").getAsString());
        taskManager.addTask(taskData);
    }
}

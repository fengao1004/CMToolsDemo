package com.dayang.uploadlib.task;

import android.util.Log;

import com.dayang.uploadlib.model.MissionInfo;
import com.dayang.uploadlib.service.UpLoadService;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;

/**
 * Created by 冯傲 on 2017/6/1.
 * e-mail 897840134@qq.com
 */

public class HttpUploadTask extends BaseTask {
    private String storageURL;
    private String filePath;
    private String taskId;
    private String fileStatusNotifyURL;
    private MissionInfo missionInfo;
    private UpLoadService service;

    public HttpUploadTask(MissionInfo missionInfo, UpLoadService service) {
        super();
        super.tenantId = missionInfo.getTenantId();
        this.storageURL = missionInfo.getStorageURL();
        this.filePath = missionInfo.getFilPath();
        this.missionInfo = missionInfo;
        this.service = service;
        this.taskId = missionInfo.getTaskId();
        this.fileStatusNotifyURL = missionInfo.getFileStatusNotifyURL();
    }

    String TAG = "cmtools_log";

    public boolean uploadFile() {
        boolean uploadFlag = false;
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpPost httppost = new HttpPost(this.storageURL);
            File uploadFile = new File(this.filePath);
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
        if (uploadFlag) {
            missionInfo.setStatus(MissionInfo.UPLOADCOMPLETED);
        } else {
            missionInfo.setStatus(MissionInfo.UPLOADERROR);
        }
        this.fileStatusNotifyCallBack(fileStatusNotifyURL, getNotifyRequestParam(uploadFlag, this.filePath, this.taskId));
        return uploadFlag;
    }

    /**
     * 用于上传和上传后的消息通知
     */
    @Override
    public void run() {
        this.uploadFile();
        service.taskComplete();
    }
}

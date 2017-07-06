package com.dayang.uploadlib.task;

import android.os.SystemClock;
import android.util.Log;

import com.dayang.dyhfileuploader.DYHFileUploadInfo;
import com.dayang.dyhfileuploader.DYHFileUploadTask;
import com.dayang.dyhfileuploader.DYHFileUploader;
import com.dayang.uploadlib.model.MissionInfo;
import com.dayang.uploadlib.service.UpLoadService;
import com.dayang.uploadlib.util.NetWorkState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by 冯傲 on 2017/6/1.
 * e-mail 897840134@qq.com
 */

public class NginxHttpUploadTask extends BaseTask {
    MissionInfo missionInfo;
    UpLoadService service;
    private static final String TAG = "cmtools_log";
    private boolean isRename;
    private String uploadTrunkInfoURL;
    String tenantIdPath = "";
    String filePath;
    private String taskId;
    private String customParam;
    private String sessionId;
    private String newFileName;
    private String remoteRootPath;
    private String fileStatusNotifyURL;
    private String storageURL;
    private DYHFileUploader fileUploader = null;
    private DYHFileUploadTask taskInfo;

    private boolean working;
    private boolean error;
    private boolean finished;
    private boolean pause;
    private boolean del;

    private long progress;
    private DYHFileUploader.OnInfoUpdatedListener mStatusUpdatedListener = new DYHFileUploader.OnInfoUpdatedListener() {
        @Override
        public void onInfoUpdated(DYHFileUploadInfo dyhFileUploadInfo) {
            int status = dyhFileUploadInfo.status;
            //TODO 输出log日志
            switch (status) {
                case DYHFileUploadInfo.StatusError:
                    Log.i(TAG, "StatusError");
                    error = true;
                    break;
                case DYHFileUploadInfo.StatusFinished:
                    String json = dyhFileUploadInfo.uploadMigrateInfo;
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(json);
                        newFileName = jsonObject.getString("migrateFileName");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onInfoUpdated: 新文件名获取错误");
                        newFileName = new File(filePath).getName();
                    }
                    Log.i(TAG, "StatusFinished");
                    working = false;
                    finished = true;
                    break;
                case DYHFileUploadInfo.StatusProcessing:
                    if (progress == 0 || dyhFileUploadInfo.uploadedBytes != progress) {
                        missionInfo.setProgress((int) (dyhFileUploadInfo.uploadedBytes / 1024));
                        missionInfo.setSpeed(dyhFileUploadInfo.speed);
                        Log.i(TAG, "Progress: " + dyhFileUploadInfo.uploadedBytes + " totalBytes: " + dyhFileUploadInfo.totalBytes);
                    }
                    progress = dyhFileUploadInfo.uploadedBytes;
                    Log.i(TAG, "StatusProcessing" + dyhFileUploadInfo.uploadedBytes);
                    break;
                case DYHFileUploadInfo.StatusStopped:
                    Log.i(TAG, "StatusStopped");
                    working = false;
                    break;
                case DYHFileUploadInfo.StatusUnkown:
                    Log.i(TAG, "StatusUnkown");
                    error = true;
                    break;
            }
        }
    };

    public NginxHttpUploadTask(MissionInfo missionInfo, UpLoadService service) {
        super();
        this.missionInfo = missionInfo;
        this.storageURL = missionInfo.getStorageURL();
        super.tenantId = missionInfo.getTenantId();
        this.fileStatusNotifyURL = missionInfo.getFileStatusNotifyURL();
        this.taskId = missionInfo.getTaskId();
        this.customParam = missionInfo.getCustomParam();
        this.uploadTrunkInfoURL = missionInfo.getUploadTrunkInfoURL();
        this.tenantIdPath = tenantId.equals("") ? "" : tenantId + "/";
        this.isRename = missionInfo.isRename();
        this.sessionId = missionInfo.getSessionId();
        this.service = service;
        this.filePath = missionInfo.getFilePath();
        fileUploader = new DYHFileUploader();
        fileUploader.init(DYHFileUploader.TypeHTTP_nginxResume);
        fileUploader.setOnInfoUpdatedListener(mStatusUpdatedListener);
        missionInfo.setPauseListener(new MissionInfo.PauseListener() {
            @Override
            public void pause() {
                pause = true;
                fileUploader.stop();
            }
        });
        missionInfo.setDelListener(new MissionInfo.DelListener() {
            @Override
            public void del() {
                del = true;
                fileUploader.stop();
            }
        });
        URL url = null;
        try {
            url = new URL(storageURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
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
        this.remoteRootPath = remoteRootPath + taskId;
    }

    /**
     * 拼接http路径地址
     *
     * @param localPath 文件本地路径
     */
    public String solveHttpPath(String localPath) {
        String[] split = localPath.split("/");
        filePath = split[split.length - 1];
        if (!storageURL.endsWith("/")) {
            storageURL = storageURL + "/";
        }
        String url = storageURL + filePath + "?redirectParam={\"fileSessionId\":\"" + sessionId + "\",\"fileSavePath\":\"" + remoteRootPath + "\",\"isRename\":\"" + isRename + "\"}";
        return url;
    }

    /**
     * 文件上传功能的实现，单个文件上传
     *
     * @param
     * @return
     * @throws Exception
     */
    public void uploadSingleFile() {
        taskInfo = getTaskInfoFromUI();
        Log.i(TAG, "sessionID: " + taskInfo.sessionID);
        Log.i(TAG, "localUrl: " + taskInfo.localUrl);
        Log.i(TAG, "remoteUrl: " + taskInfo.remoteUrl);
        Log.i(TAG, "password: " + taskInfo.password);
        Log.i(TAG, "user: " + taskInfo.user);
        Log.i(TAG, "FTPMode: " + taskInfo.FTPMode);
        Log.i(TAG, "resume: " + taskInfo.resume);
        Log.i(TAG, "timeOut: " + taskInfo.timeOut);
        Log.i(TAG, "uploadTrunkInfoURL: " + taskInfo.uploadTrunkInfoURL);
        //TODO 提前判断任务状态
        working = true;
        fileUploader.setTask(taskInfo);
        DYHFileUploadInfo dyhFileUploadInfo = new DYHFileUploadInfo();
        fileUploader.getInfo(dyhFileUploadInfo);
        int status = dyhFileUploadInfo.status;
        switch (status) {
            case DYHFileUploadInfo.StatusError:
                Log.i(TAG, "上传前: StatusError");
                missionInfo.setStatus(MissionInfo.UPLOADERROR);
                error = true;
                working = false;
                break;
            case DYHFileUploadInfo.StatusFinished:
                Log.i(TAG, "上传前: StatusFinished");
                missionInfo.setProgress((int) ((dyhFileUploadInfo.uploadedBytes) / 1024));
                missionInfo.setLength((int) ((dyhFileUploadInfo.totalBytes) / 1024));
                working = false;
                finished = true;
                break;
            case DYHFileUploadInfo.StatusStopped:
                Log.i(TAG, "上传前: StatusStopped");
                missionInfo.setStatus(MissionInfo.UPLOADING);
                missionInfo.setProgress((int) ((dyhFileUploadInfo.uploadedBytes) / 1024));
                missionInfo.setLength((int) ((dyhFileUploadInfo.totalBytes) / 1024));
                break;
            case DYHFileUploadInfo.StatusProcessing:
                Log.i(TAG, "上传前: StatusProcessing");
                missionInfo.setStatus(MissionInfo.UPLOADING);
                missionInfo.setProgress((int) ((dyhFileUploadInfo.uploadedBytes) / 1024));
                missionInfo.setLength((int) ((dyhFileUploadInfo.totalBytes) / 1024));
                break;
            case DYHFileUploadInfo.StatusUnkown:
                Log.i(TAG, "上传前: StatusUnkown");
                missionInfo.setProgress((int) ((dyhFileUploadInfo.uploadedBytes) / 1024));
                missionInfo.setLength((int) ((dyhFileUploadInfo.totalBytes) / 1024));
                break;
        }
        boolean ret = fileUploader.start(false);
        if (!ret) {
            working = false;
            error = true;
        }
        while (working) {
            SystemClock.sleep(50);
            if (error) {
                break;
            }
        }
        if (error) {
            int i = NetWorkState.GetNetype(service);
            if (i == NetWorkState.NONE) {
                missionInfo.setStatus(MissionInfo.WAITINGNETWORDK);
                return;
            }
        }
        if (del) {
            //TODO 发送删除通知
            missionInfo.setStatus(MissionInfo.REMOVED);
            return;
        }
        if (pause) {
            missionInfo.setStatus(MissionInfo.PAUSEING);
            return;
        }
        //TODO 新版上传
        if (finished) {
            missionInfo.setStatus(MissionInfo.UPLOADCOMPLETED);
            fileStatusNotifyCallBack(fileStatusNotifyURL,
                    //String fileSessionId, String fileNewName, boolean uploadStatus, String localPath, String taskId, String customParam
                    getNewHttpNotifyRequestParam(missionInfo.getSessionId(),newFileName,true, filePath, taskId,missionInfo.getCustomParam()));
        } else {
            missionInfo.setStatus(MissionInfo.UPLOADERROR);
            fileStatusNotifyCallBack(fileStatusNotifyURL,
                    getNewHttpNotifyRequestParam(missionInfo.getSessionId(),newFileName,false, filePath, taskId,missionInfo.getCustomParam()));
        }
        return;
    }


    public void run() {
        this.uploadSingleFile();
        service.taskComplete();
    }

    private DYHFileUploadTask getTaskInfoFromUI() {
        DYHFileUploadTask task = new DYHFileUploadTask();
        task.sessionID = missionInfo.getSessionId();
        String path = this.filePath;
        task.FTPMode = DYHFileUploadTask.FTPModePassivePASV;
        task.remoteUrl = solveHttpPath(path);
        task.timeOut = 0;
        task.localUrl = path;
        task.resume = true;
        if (!uploadTrunkInfoURL.equals("")) {
            task.uploadTrunkInfoURL = uploadTrunkInfoURL;
        }
        task.user = "";
        task.password = "";
        task.bucketName = "";
        task.regionName = "";
        task.fileKey = "";
        task.host = "";
        task.awsAccessKey = "";
        task.awsSecretKey = "";
        task.uploadId = "";
        taskInfo = task;
        return task;
    }
}

package com.dayang.uploadlib.task;

import android.os.SystemClock;
import android.util.Log;

import com.dayang.dyhfileuploader.DYHFileUploadInfo;
import com.dayang.dyhfileuploader.DYHFileUploadTask;
import com.dayang.dyhfileuploader.DYHFileUploader;
import com.dayang.uploadlib.model.MissionInfo;
import com.dayang.uploadlib.service.UpLoadService;
import com.dayang.uploadlib.util.NetWorkState;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.util.Date;

/**
 * Created by 冯傲 on 2017/6/1.
 * e-mail 897840134@qq.com
 */

public class FtpUploadTask extends BaseTask {

    private static FTPClient ftpClient = new FTPClient();
    private String ftpUrl;
    public final static String TAG = "cmtools_log";
    private String port;
    private long progress;
    private String username;
    private String password;
    private String filePath;
    private String remotePath;
    private String taskId;
    private String fileStatusNotifyURL;
    private String dir;
    private boolean working;
    private boolean error;
    private boolean finished;
    private boolean pause;
    private boolean del;
    private DYHFileUploader fileUploader = null;
    private DYHFileUploadTask taskInfo;
    int i;
    MissionInfo missionInfo;
    UpLoadService service;

    private DYHFileUploader.OnInfoUpdatedListener mStatusUpdatedListener = new DYHFileUploader.OnInfoUpdatedListener() {
        @Override
        public void onInfoUpdated(DYHFileUploadInfo dyhFileUploadInfo) {
            //TODO 输出log日志
            int status = dyhFileUploadInfo.status;
            switch (status) {
                case DYHFileUploadInfo.StatusError:
                    Log.i(TAG, "StatusError");
                    error = true;
                    break;
                case DYHFileUploadInfo.StatusFinished:
                    Log.i(TAG, "StatusFinished");
                    working = false;
                    finished = true;
                    break;
                case DYHFileUploadInfo.StatusProcessing:
                    long progress = dyhFileUploadInfo.uploadedBytes / 1024;
                    if (progress == 0 || progress > FtpUploadTask.this.progress + dyhFileUploadInfo.totalBytes / 1024 / 500) {
                        Log.i(TAG, "onInfoUpdated:  " + progress  + " progress: " + dyhFileUploadInfo.uploadedBytes  + " totalBytes: " +  dyhFileUploadInfo.totalBytes);
                        i++;
                        missionInfo.setProgress((int) progress);
                        missionInfo.setSpeed(dyhFileUploadInfo.speed);
                        FtpUploadTask.this.progress = progress;
                    }
                    break;
                case DYHFileUploadInfo.StatusStopped:
                    Log.i(TAG, "StatusStopped");
                    working = false;
                    break;
                case DYHFileUploadInfo.StatusUnkown:
                    Log.i(TAG, "StatusUnkown");
                    error = true;
                    break;
                default:
                    break;
            }
        }
    };

    public FtpUploadTask(MissionInfo missionInfo, UpLoadService service) {
        super();
        super.tenantId = missionInfo.getTenantId();
        this.missionInfo = missionInfo;
        this.service = service;
        this.fileStatusNotifyURL = missionInfo.getFileStatusNotifyURL();
        this.taskId = missionInfo.getTaskId();
        this.filePath = missionInfo.getFilPath();
        solveFtpPath(missionInfo.getStorageURL());
        fileUploader = new DYHFileUploader();
        fileUploader.init(DYHFileUploader.TypeFTP);
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
    }

    /**
     * 解析ftp路径地址
     *
     * @param ftpPath
     */
    public void solveFtpPath(String ftpPath) {
        if (ftpPath.endsWith("/")) {
            ftpPath = ftpPath.substring(0, ftpPath.length() - 1);
        }
        String pathArr[];
        if (ftpPath != null) {
            Log.i(TAG, " ftp path :   " + ftpPath);
            pathArr = ftpPath.split(":");
            this.username = pathArr[1].substring(2);
            this.password = pathArr[2].substring(0, pathArr[2].indexOf("@"));
            this.ftpUrl = pathArr[2].substring(pathArr[2].indexOf("@") + 1);
            if (!pathArr[3].contains("/")) {
                dir = this.taskId;
                this.port = pathArr[3];
                if (tenantId.equals("")) {
                    this.remotePath = "ftp://" + ftpUrl + ":" + pathArr[3] + "/"
                            + this.taskId + "/";
                } else {
                    this.remotePath = "ftp://" + ftpUrl + ":" + pathArr[3] + "/"
                            + tenantId + "/" + this.taskId + "/";
                }
            } else {
                this.port = pathArr[3].substring(0, pathArr[3].indexOf("/"));
                this.remotePath = "ftp://" + ftpUrl + ":" + port + "/"
                        + pathArr[3].substring(pathArr[3].indexOf("/") + 1)
                        + "/" + this.taskId + "/";
                dir = pathArr[3].substring(pathArr[3].indexOf("/") + 1) + "/"
                        + this.taskId;
            }
        }
    }

    public void uploadFile() {
        taskInfo = getTaskInfoFromUI();
        Log.i(TAG, "file sessionID: " + taskInfo.sessionID);
        Log.i(TAG, "file localUrl: " + taskInfo.localUrl);
        Log.i(TAG, "file remoteUrl: " + taskInfo.remoteUrl);
        Log.i(TAG, "file password: " + taskInfo.password);
        Log.i(TAG, "file user: " + taskInfo.user);
        Log.i(TAG, "file FTPMode: " + taskInfo.FTPMode);
        Log.i(TAG, "file resume: " + taskInfo.resume);
        Log.i(TAG, "file timeOut: " + taskInfo.timeOut);
        Log.i(TAG, "file length: " + new File(filePath).length());
        working = true;
        error = false;
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
        if (working == true) {
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
        if (finished) {
            missionInfo.setStatus(MissionInfo.UPLOADCOMPLETED);
            fileStatusNotifyCallBack(fileStatusNotifyURL,
                    getNotifyRequestParam(finished, filePath, taskId));
        } else {
            missionInfo.setStatus(MissionInfo.UPLOADERROR);
            fileStatusNotifyCallBack(fileStatusNotifyURL,
                    getNotifyRequestParam(finished, filePath, taskId));
        }
    }

    public void makeDirectory() {
        try {
            ftpClient.enterLocalPassiveMode();
            int portnum = Integer.valueOf(this.port);
            ftpClient.connect(this.ftpUrl, portnum);
            boolean loginResult = ftpClient.login(username, password);
            int returnCode = ftpClient.getReplyCode();
            if (loginResult && FTPReply.isPositiveCompletion(returnCode)) {// 如果登录成功
                String directory = new String(dir.getBytes("gbk"), "ISO8859-1");
                boolean ret = ftpClient.makeDirectory(directory);
                Log.i(TAG, "ret   " + ret);
            }
            ftpClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ftp连接", "连接失败！");
        }
    }

    /**
     * 线程的run方法，用于实现文件上传和完成后的消息通知
     */
    public void run() {
        makeDirectory();
        this.uploadFile();
        service.taskComplete();
    }

    private DYHFileUploadTask getTaskInfoFromUI() {
        DYHFileUploadTask task = new DYHFileUploadTask();
        task.sessionID = missionInfo.getSessionId();
        task.FTPMode = DYHFileUploadTask.FTPModePassivePASV;
        String[] split = filePath.split("/");
        task.remoteUrl = remotePath + split[split.length - 1];
        task.localUrl = this.filePath;
        task.resume = true;
        task.user = username;
        task.password = password;
        task.uploadTrunkInfoURL = "";
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

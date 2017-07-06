package com.dayang.upload;

import android.app.Activity;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.dayang.common.Canstant;
import com.dayang.common.CheckNetWorking;
import com.dayang.common.Constants;
import com.dayang.common.JSONFactory;
import com.dayang.dyhfileuploader.DYHFileUploadInfo;
import com.dayang.dyhfileuploader.DYHFileUploadTask;
import com.dayang.dyhfileuploader.DYHFileUploader;
import com.dayang.info.FileAndIndexInfo;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.List;

/**
 * Created by 冯傲 on 2016/10/20.
 * e-mail 897840134@qq.com
 */
public class NewHttpUpload extends UploadFileThread {
    private static final String TAG = Canstant.TAG;
    private String fileSessionId;
    private boolean isRename;
    private String uploadTrunkInfoURL;
    String tenantIdPath = "";
    private String uploadFileName;
    private Handler handler;
    String fileName;
    private int uploadType;
    private String singleFilePath;
    private boolean isWrong;
    private boolean isOk;
    private List<FileAndIndexInfo> filePathList;
    private String taskId;
    private long loadProgress;
    private String remoteRootPath;
    int position;
    private String fileStatusNotifyURL;
    private String httpUrl;
    private DYHFileUploader fileUploader = null;
    private DYHFileUploadTask taskInfo;
    private DYHFileUploader.OnInfoUpdatedListener mStatusUpdatedListener = new DYHFileUploader.OnInfoUpdatedListener() {
        @Override
        public void onInfoUpdated(DYHFileUploadInfo dyhFileUploadInfo) {
            int status = dyhFileUploadInfo.status;
            Log.i(TAG, "uploadedBytes: " + dyhFileUploadInfo.uploadedBytes + "*******totalBytes" + dyhFileUploadInfo.totalBytes);
            switch (status) {
                case DYHFileUploadInfo.StatusError:
                    Log.i(TAG, "StatusError");
                    if (dyhFileUploadInfo.totalBytes == dyhFileUploadInfo.uploadedBytes) {
                        isWrong = false;
                    } else {
                        if (!againUpload()) {
                            isOk = false;
                        } else {
                            isOk = true;
                        }
                    }
                    break;
                case DYHFileUploadInfo.StatusFinished:
                    Log.i(TAG, "  完成");
                    String json = dyhFileUploadInfo.uploadMigrateInfo;
                    JsonObject jsonObject = JSONFactory.parseJsonStr(json);
                    try {
                        fileName = jsonObject.get("migrateFileName").getAsString();
                    } catch (Exception e) {
                        Log.e(TAG, "onInfoUpdated: 新文件名获取错误");
                    }
                    isWrong = false;
                    break;
                case DYHFileUploadInfo.StatusProcessing:
                    Log.i(TAG, "StatusProcessing" + dyhFileUploadInfo.uploadedBytes);
                    if (loadProgress != 0 && loadProgress == dyhFileUploadInfo.uploadedBytes) {
                        Log.i(TAG, "重启上传");
                        fileUploader.stop();
                    }
                    loadProgress = dyhFileUploadInfo.uploadedBytes;
                    break;
                case DYHFileUploadInfo.StatusStopped:
                    Log.i(TAG, "StatusStopped");
                    fileUploader.start(false);
                    break;
                case DYHFileUploadInfo.StatusUnkown:
                    Log.i(TAG, "StatusUnkown");
                    break;
                default:
                    break;
            }
        }
    };

    public NewHttpUpload(String url, String fileStatusNotifyURL, Handler handler,
                         int uploadType, String singleFilePath, List<FileAndIndexInfo> filePathList,
                         String taskId, Activity activity, String tenantId, String remoteRootPath, String uploadTrunkInfoURL) {
        super(activity);
        this.httpUrl = url;
        super.tenantId = tenantId;
        this.fileStatusNotifyURL = fileStatusNotifyURL;
        this.handler = handler;
        this.uploadType = uploadType;
        this.filePathList = filePathList;
        this.singleFilePath = singleFilePath;
        this.taskId = taskId;
        this.remoteRootPath = remoteRootPath;
        this.uploadTrunkInfoURL = uploadTrunkInfoURL;
        this.tenantIdPath = tenantId.equals("") ? "" : tenantId + "/";
        fileUploader = new DYHFileUploader();
        fileUploader.init(DYHFileUploader.TypeHTTP_nginxResume);
        fileUploader.setOnInfoUpdatedListener(mStatusUpdatedListener);
    }

    /**
     * 拼接http路径地址
     *
     * @param localPath 文件本地路径
     */
    public String solveHttpPath(String localPath) {
        String[] split = localPath.split("/");
        fileName = split[split.length - 1];

        if (!httpUrl.endsWith("/")) {
            httpUrl = httpUrl + "/";
        }
        String url = httpUrl + fileName + "?redirectParam={\"fileSessionId\":\"" + fileSessionId + "\",\"fileSavePath\":\"" + remoteRootPath + "\",\"isRename\":\"" + isRename + "\"}";
        return url;
    }

    /**
     * 文件上传功能的实现，单个文件上传
     *
     * @param
     * @return
     * @throws Exception
     */
    public boolean uploadSingleFile() {
        boolean uploadStatus = false;
        uploadFileName = singleFilePath;
        position = -1;
        taskInfo = getTaskInfoFromUI();
        isWrong = true;
        fileUploader.setTask(taskInfo);
        boolean ret = fileUploader.start(false);
        if (!ret) {
            isWrong = false;
            isOk = false;
        }
        while (isWrong) {
            SystemClock.sleep(50);
            if (!isOk) {
                break;
            }
        }
        if (!isOk) {
//            fileStatusNotifyCallBack(fileStatusNotifyURL,
//                    getNewHttpNotifyRequestParam(fileSessionId, fileName, false, uploadFileName, taskId));
//            uploadStatus = false;
        } else {
//            fileStatusNotifyCallBack(fileStatusNotifyURL,
//                    getNewHttpNotifyRequestParam(fileSessionId, fileName, true, uploadFileName, taskId));
//            uploadStatus = true;
        }

        return uploadStatus;
    }


    /**
     * 文件上传功能的实现，批量上传
     *
     * @param
     * @return
     */
    public boolean uploadAllFiles() {
        boolean uploadStatus = false;
        //因为之前的上传时同步上传，后来用的研究院的sdk改为异步上传，用一个比较low的死循环暂时顶替一下
        //将将要上传的对象加入上传文件展示队列中
        for (int i = 0; i < this.filePathList.size(); i++) {
            String uploadfilename = this.filePathList.get(i).filePath;
            String fileName = getFileNameFromPath(uploadfilename);
        }
        for (int i = 0; i < this.filePathList.size(); i++) {
            String uploadfilename = this.filePathList.get(i).filePath;
            isWrong = true;
            isOk = true;
            isRename = this.filePathList.get(i).isRename;
            fileSessionId = this.filePathList.get(i).fileSessionId;
            this.uploadFileName = uploadfilename;
            taskInfo = null;
            try {
                position = Integer.parseInt(this.filePathList.get(i).fileIndex);
            } catch (Exception e) {
                Log.d(TAG, "uploadAllFiles: " + e.toString());
                Log.d(TAG, "uploadAllFiles: " + "插入文件索引错误");
                position = i;
            }
            taskInfo = getTaskInfoFromUI();
            Log.i(TAG, "uploadAllFiles: " + taskInfo.sessionID);
            Log.i(TAG, "uploadAllFiles: " + taskInfo.localUrl);
            Log.i(TAG, "uploadAllFiles: " + taskInfo.remoteUrl);
            Log.i(TAG, "uploadAllFiles: " + taskInfo.password);
            Log.i(TAG, "uploadAllFiles: " + taskInfo.user);
            Log.i(TAG, "uploadAllFiles: " + taskInfo.FTPMode);
            Log.i(TAG, "uploadAllFiles: " + taskInfo.resume);
            Log.i(TAG, "uploadAllFiles: " + taskInfo.timeOut);
            Log.i(TAG, "uploadAllFiles: " + taskInfo.uploadTrunkInfoURL);
            fileUploader.setTask(taskInfo);
            boolean ret = false;
            try {
                ret = fileUploader.start(false);
            } catch (Exception e) {
                Log.i(TAG, "uploadAllFiles: " + e.toString());
            }
            if (!ret) {
                uploadStatus = false;
                break;
            }
            while (isWrong) {
                SystemClock.sleep(50);
                if (!isOk) {
                    break;
                }
            }
            if (!isOk) {
                Log.i(TAG, "失败发送");
//                fileStatusNotifyCallBack(fileStatusNotifyURL,
//                        getNewHttpNotifyRequestParam(fileSessionId, fileName, false, uploadfilename, taskId));
                uploadStatus = false;
            } else {
                Log.i(TAG, "成功发送");
//                fileStatusNotifyCallBack(fileStatusNotifyURL,
//                        getNewHttpNotifyRequestParam(fileSessionId, fileName, true, uploadfilename, taskId));
                uploadStatus = true;
            }
        }
        Log.e(TAG, "new_http上传成功");
        return uploadStatus;
    }

    /**
     * 线程的run方法，用于实现文件上传和完成后的消息通知
     */
    public void run() {
        boolean upload;
        super.run();
        try {
            if (uploadType == Constants.UPLOADSINGLE) {
                upload = this.uploadSingleFile();
                if (upload) {
                    Log.i(TAG, "run: 7");
                    this.handler.sendEmptyMessage(Constants.UPLOADSUCCESS);
                } else {
                    this.handler.sendEmptyMessage(Constants.UPLOADFAILTURE);
                }
            } else if (uploadType == Constants.UPLOADMUTIPLE) {
                upload = this.uploadAllFiles();
                if (upload) {
                    Log.i(TAG, "run: 8");
                    this.handler.sendEmptyMessage(Constants.UPLOADSUCCESS);// UPLOADSUCCESS
                } else {
                    this.handler.sendEmptyMessage(Constants.UPLOADFAILTURE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean againUpload() {
        boolean succeed;
        SystemClock.sleep(2000);
        int times = 0;
        while (true) {
            if (CheckNetWorking.checkNetWorking(super.activity)) {
                getTaskInfoFromUI();
                fileUploader.setTask(taskInfo);
                fileUploader.start(false);
                succeed = true;
                break;
            }
            SystemClock.sleep(1000);
            if (times >= 60) { // 设置断网尝试时间
                succeed = false;
                break;
            }
            times++;
        }
        return succeed;
    }

    private DYHFileUploadTask getTaskInfoFromUI() {
        DYHFileUploadTask task = new DYHFileUploadTask();
        task.sessionID = fileSessionId;
        String path = this.uploadFileName;
        //String file = Environment.getExternalStorageDirectory().getPath() + "/10M.mp4";
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

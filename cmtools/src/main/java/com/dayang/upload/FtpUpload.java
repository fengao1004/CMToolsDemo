package com.dayang.upload;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.dayang.common.Canstant;
import com.dayang.common.CheckNetWorking;
import com.dayang.common.Constants;
import com.dayang.dyhfileuploader.DYHFileUploadInfo;
import com.dayang.dyhfileuploader.DYHFileUploadTask;
import com.dayang.dyhfileuploader.DYHFileUploader;
import com.dayang.info.FileAndIndexInfo;
import com.dayang.info.FileUpdateInfo;
import com.dayang.info.FilesUpdateManager;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.util.List;

/**
 * 此类用于实现Ftp上传
 *
 * @author renyuwei
 */
public class FtpUpload extends UploadFileThread {

    private static final String TAG = Canstant.TAG;
    private static FTPClient ftpClient = new FTPClient();
    private String ftpUrl;
    private String port;
    int position = 0;
    private String username;
    private String password;
    private String uploadfilename;
    private String remotePath;
    private Handler handler;
    private int uploadType;
    private String singleFilePath;
    private boolean isWoring;
    private boolean isOk;
    private List<FileAndIndexInfo> filePathList;
    private String taskId;
    private long loadprogress;
    int i;// 控制log次数的变量
    private String fileStatusNotifyURL;
    private DYHFileUploader fileUploader = null;
    private String dir;
    String sessionId;
    private DYHFileUploadTask taskInfo;
    private DYHFileUploader.OnInfoUpdatedListener mStatusUpdatedListener = new DYHFileUploader.OnInfoUpdatedListener() {
        @Override
        public void onInfoUpdated(DYHFileUploadInfo dyhFileUploadInfo) {
            int status = dyhFileUploadInfo.status;
            i++;
            switch (status) {
                case DYHFileUploadInfo.StatusError:
                    Log.i(TAG, "StatusError");
                    if (!againUpload()) {
                        isOk = false;
                    } else {
                        isOk = true;
                    }
                    break;
                case DYHFileUploadInfo.StatusFinished:
                    Log.i(TAG, "   完成");
                    isWoring = false;
                    break;
                case DYHFileUploadInfo.StatusProcessing:
                    info.setUpdateProgress(dyhFileUploadInfo.uploadedBytes);
                    if (i % 15 == 0) {
                        Log.i(TAG, "StatusProcessing" + dyhFileUploadInfo.uploadedBytes);
                        if (loadprogress != 0 && loadprogress == dyhFileUploadInfo.uploadedBytes) {
                            Log.i(TAG, "重启上传");
                            fileUploader.stop();
                        }
                        loadprogress = dyhFileUploadInfo.uploadedBytes;
                    }
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
    private FileUpdateInfo info;

    public FtpUpload(String url, String fileStatusNotifyURL, Handler handler,
                     int uploadType, String singleFilePath, List<FileAndIndexInfo> filePathList,
                     String taskId, Activity activity, String tenantId) {
        super(activity);
        super.tenantId = tenantId;
        this.fileStatusNotifyURL = fileStatusNotifyURL;
        this.handler = handler;
        this.uploadType = uploadType;
        this.filePathList = filePathList;
        this.singleFilePath = singleFilePath;
        this.taskId = taskId;
        solveFtpPath(url);
        fileUploader = new DYHFileUploader();
        fileUploader.init(DYHFileUploader.TypeFTP);
        fileUploader.setOnInfoUpdatedListener(mStatusUpdatedListener);
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
            Log.i(TAG, " path :   " + ftpPath);
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

    /**
     * 文件上传功能的实现，单个文件上传
     *
     * @param fileNameList
     * @return
     * @throws Exception
     */
    public boolean uploadSingleFile() {
        boolean uploadStatus = false;
        position = -1;
        uploadfilename = singleFilePath;
        taskInfo = getTaskInfoFromUI();
        isWoring = true;
        fileUploader.setTask(taskInfo);
        boolean ret = fileUploader.start(false);
        if (!ret) {
            uploadStatus = false;
            isWoring = false;
            isOk = false;
        }
        while (isWoring) {
            SystemClock.sleep(50);
            if (!isOk) {
                break;
            }
        }
        if (!isOk) {
            fileStatusNotifyCallBack(fileStatusNotifyURL,
                    getNotifyRequestParam(false, uploadfilename, taskId));
            uploadStatus = false;
        } else {
            fileStatusNotifyCallBack(fileStatusNotifyURL,
                    getNotifyRequestParam(true, uploadfilename, taskId));
            uploadStatus = true;
        }

        return uploadStatus;
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
     * 文件上传功能的实现，批量上传
     *
     * @param fileNameList
     * @return
     */
    public boolean uploadAllFiles() {
        boolean uploadStatus = false;
        FilesUpdateManager filesUpdateManager = FilesUpdateManager.getInstance();
        //因为之前的上传时同步上传，后来用的研究院的sdk改为异步上传，用一个比较low的死循环暂时顶替一下
        for(int i = 0; i < this.filePathList.size(); i++){
            String uploadfilename = this.filePathList.get(i).filePath;
            String fileName = getFileNameFromPath(uploadfilename);
            FileUpdateInfo fileUpdateInfo = new FileUpdateInfo(fileName, uploadfilename, new File(uploadfilename).length(), 0, true, this.taskId + "_" + this.filePathList.get(i).fileIndex, FileUpdateInfo.WAITTING);
            filesUpdateManager.addFileUpdateInfo(fileUpdateInfo);
        }

        for (int i = 0; i < this.filePathList.size(); i++) {
            String uploadfilename = this.filePathList.get(i).filePath;
            isWoring = true;
            isOk = true;
            this.uploadfilename = uploadfilename;
            taskInfo = null;
            position = i;
            sessionId = this.filePathList.get(i).fileSessionId;
            taskInfo = getTaskInfoFromUI();
            Log.i(TAG, "uploadAllFiles: " + taskInfo.sessionID);
            Log.i(TAG, "uploadAllFiles: " + taskInfo.localUrl);
            Log.i(TAG, "uploadAllFiles: " + taskInfo.remoteUrl);
            Log.i(TAG, "uploadAllFiles: " + taskInfo.password);
            Log.i(TAG, "uploadAllFiles: " + taskInfo.user);
            Log.i(TAG, "uploadAllFiles: " + taskInfo.FTPMode);
            Log.i(TAG, "uploadAllFiles: " + taskInfo.resume);
            Log.i(TAG, "uploadAllFiles: " + taskInfo.timeOut);
            Log.i(TAG, "uploadAllFiles: " + new File(uploadfilename).length());
            info = FilesUpdateManager.getInstance().get(this.taskId+"_"+this.filePathList.get(i).fileIndex);
            info.setUpdateState(FileUpdateInfo.UPDATEING);
            fileUploader.setTask(taskInfo);
            boolean ret = fileUploader.start(false);
            if (!ret) {
                uploadStatus = false;
                break;
            }
            while (isWoring) {
                SystemClock.sleep(50);
                if (!isOk) {
                    break;
                }
            }
            if (!isOk) {
                Log.i(TAG, "失败发送");
                fileStatusNotifyCallBack(fileStatusNotifyURL,
                        getNewHttpNotifyRequestParam(sessionId, "", false, uploadfilename, taskId));
                uploadStatus = false;
            } else {
                Log.i(TAG, "成功发送");
                fileStatusNotifyCallBack(fileStatusNotifyURL,
                        getNewHttpNotifyRequestParam(sessionId, "", true, uploadfilename, taskId));
                uploadStatus = true;
            }
            filesUpdateManager.removeFileUpdateInfo(info);
        }
        filesUpdateManager.updateFinished();
        Log.e("ftp上传", "上传成功");

        return uploadStatus;
    }


    /**
     * 线程的run方法，用于实现文件上传和完成后的消息通知
     */
    public void run() {
        boolean upload = false;
        super.run();
        try {
            makeDirectory();

            if (uploadType == Constants.UPLOADSINGLE) {
                upload = this.uploadSingleFile();
                if (upload) {
                    Log.i(TAG, "run: 3");
                    this.handler.sendEmptyMessage(Constants.UPLOADSUCCESS);
                } else {
                    Log.i(TAG, "run: 4");
                    this.handler.sendEmptyMessage(Constants.UPLOADFAILTURE);
                }
            } else if (uploadType == Constants.UPLOADMUTIPLE) {
                upload = this.uploadAllFiles();
                if (upload) {
                    Log.i(TAG, "run: 4");
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
        boolean succeed = false;
        SystemClock.sleep(2000);
        int times = 0;
        while (true) {
            if (CheckNetWorking.checkNetWorking((Context) super.activity)) {
                taskInfo = getTaskInfoFromUI();
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
        if (position == -1) {
            task.sessionID = taskId;
        } else {
            task.sessionID = taskId + "_" + position;
        }
        task.FTPMode = DYHFileUploadTask.FTPModePassivePASV;
        String[] split = uploadfilename.split("/");
        task.remoteUrl = remotePath + split[split.length - 1];
        task.localUrl = this.uploadfilename;
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

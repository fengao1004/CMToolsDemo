package com.dayang.info;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

/**
 * Created by 冯傲 on 2017/2/7.
 * e-mail 897840134@qq.com
 * 文件上传信息对象
 */

public class FileUpdateInfo {
    public String fileName;
    public String taskID;
    public String filePath;
    //标识此文件是否具有进度属性，普通的http上传是没有进度的
    public boolean hasProgress;
    private FileUpdatePauseListener fileUpdatePauseListener;
    private ProgressListener progressListener;
    private FileUpdateStartListener fileUpdateStartListener;
    private UpdateStateChangeListener updateStateChangeListener;
    public long totalProgress;
    public long updateProgress;
    public final static int WAITTING = 144;
    public final static int UPDATEING = 145;
    public final static int FINISHED = 146;
    public int updateState;

    /**
     * 预留的暂停功能 通知上传线程暂定上传（上传线程中暂未实现）
     *
     * @param parameter
     * @return
     */
    public void pauseUpdate() {
        if (fileUpdatePauseListener != null) {
            fileUpdatePauseListener.pauseUpdate();
        }
    }


    /**
     * 预留的开始上传功能 通知上传线程继续上传（上传线程中暂未实现）
     *
     * @param parameter
     * @return
     */
    public void startUpdate() {
        if (fileUpdateStartListener != null) {
            fileUpdateStartListener.startUpdate();
        }
    }

    /**
     * 有上传线程调用，传入处理暂定上传的监听器，在里边进行暂停上传操作
     *
     * @param parameter
     * @return
     */
    public void setFileUpdatePauseLinstener(FileUpdatePauseListener fileUpdatePauseLinstener) {
        this.fileUpdatePauseListener = fileUpdatePauseLinstener;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    /**
     * 有上传线程调用，传入处理继续上传的监听器，在里边进行继续上传操作
     *
     * @param parameter
     * @return
     */
    public void setFileUpdateStartListener(FileUpdateStartListener fileUpdateStartListener) {
        this.fileUpdateStartListener = fileUpdateStartListener;
    }

    public FileUpdateInfo(String fileName, String filePath, long totalProgress, long updateProgress, boolean hasProgress, String taskID, int updateState) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.totalProgress = totalProgress;
        this.updateProgress = updateProgress;
        this.hasProgress = hasProgress;
        this.taskID = taskID;
        this.updateState = updateState;
    }

    /**
     * 跟新上传进度
     *
     * @param parameter
     * @return
     */
    public void setUpdateProgress(long updateProgress) {
        this.updateProgress = updateProgress;
        if (progressListener != null) {
            Message message = new Message();
            message.what = FilesUpdateManager.UPDATEPROGRESS;
            message.obj = new Object[]{progressListener, updateProgress};
            FilesUpdateManager.handler.sendMessage(message);
        }
    }

    /**
     * 设置上传状态
     *
     * @param parameter
     * @return
     */
    public void setUpdateState(int updateState) {
        this.updateState = updateState;
        if (updateStateChangeListener != null) {
            Message message = new Message();
            message.what = FilesUpdateManager.UPDATESTATECHANGE;
            message.obj = new Object[]{updateStateChangeListener, updateState};
            FilesUpdateManager.handler.sendMessage(message);
        }
    }

    /**
     * 设置上传状态监听器用于改变上传进度条的状态
     *
     * @param parameter
     * @return
     */
    public void setUpdateStateChangeListener(UpdateStateChangeListener updateStateChangeListener) {
        this.updateStateChangeListener = updateStateChangeListener;
    }

    public interface FileUpdatePauseListener {
        void pauseUpdate();
    }

    public interface FileUpdateStartListener {
        void startUpdate();
    }

    public interface ProgressListener {
        void progressUpdate(long progress);
    }

    public interface UpdateStateChangeListener {
        void updateStateChange(int UpdateState);
    }
}

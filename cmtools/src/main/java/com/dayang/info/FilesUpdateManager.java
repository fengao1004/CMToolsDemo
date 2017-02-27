package com.dayang.info;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;

/**
 * Created by 冯傲 on 2017/2/7.
 * e-mail 897840134@qq.com
 * 管理上传队列的类
 * 暂有的功能
 * 添加 移除正在上传的文件信息
 * 通知上传结束
 */

public class FilesUpdateManager {
    private static FilesUpdateManager mFilesUpdateManager;
    private ArrayList<FileUpdateInfo> infos;
    private AddFileUpdateInfoListener addFileUpdateInfoListener;
    private RemoveFileUpdateInfoListener removeFileUpdateInfoListener;
    private FileUpdateFinishedListener fileUpdateFinishedListener;
    public final static int UPDATEFINISHED = 1;
    public final static int ADDFILE = 2;
    public final static int REMOVEFILR = 3;
    public final static int UPDATEPROGRESS = 4;
    public final static int UPDATESTATECHANGE = 5;
    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATEFINISHED:
                    mFilesUpdateManager.fileUpdateFinishedListener.FileUpdateFinished();
                    break;
                case ADDFILE:
                    mFilesUpdateManager.addFileUpdateInfoListener.addFileUpdateInfo((Integer) msg.obj);
                    break;
                case REMOVEFILR:
                    mFilesUpdateManager.removeFileUpdateInfoListener.removeFileUpdateInfo((Integer) msg.obj);
                    break;
                case UPDATEPROGRESS:
                    Object[] obj1 = (Object[]) msg.obj;
                    FileUpdateInfo.ProgressListener listener = (FileUpdateInfo.ProgressListener) obj1[0];
                    listener.progressUpdate((Long) obj1[1]);
                    break;
                case UPDATESTATECHANGE:
                    Object[] obj2 = (Object[]) msg.obj;
                    FileUpdateInfo.UpdateStateChangeListener updateStateChangeListener = (FileUpdateInfo.UpdateStateChangeListener) obj2[0];
                    updateStateChangeListener.updateStateChange((int) obj2[1]);
                    break;
            }
        }
    };

    private FilesUpdateManager() {

    }

    /**
     * 此类设计为单例模式
     *
     * @param parameter
     * @return
     */
    public static FilesUpdateManager getInstance() {
        if (mFilesUpdateManager == null) {
            mFilesUpdateManager = new FilesUpdateManager();
        }
        return mFilesUpdateManager;
    }


    /**
     * 添加上传的文件对象，会触发添加监听用来更新展示列表
     *
     * @param info 上传文件对象
     * @return
     */

    public void addFileUpdateInfo(FileUpdateInfo info) {
        if (infos == null) {
            infos = new ArrayList<>();
        }
        infos.add(info);
        if (addFileUpdateInfoListener != null) {
            Message message = new Message();
            message.what = ADDFILE;
            message.obj = infos.size() - 1;
            handler.sendMessage(message);
        }
    }

    /**
     * 移除上传的文件对象，会触发移除监听，用来更新展示列表
     *
     * @param info 上传文件对象
     * @return
     */
    public void removeFileUpdateInfo(FileUpdateInfo info) {
        if (infos == null || infos.size() == 0) {
            return;
        }
        int index = infos.indexOf(info);
        infos.remove(info);
        if (removeFileUpdateInfoListener != null) {
            Message message = new Message();
            message.what = REMOVEFILR;
            message.obj = index;
            handler.sendMessage(message);
        }
    }

    /**
     * 用来通知上传完毕的方法，经判断集合中是否还存在文件，如果不存在通知上传完毕，dialog执行dismiss
     *
     * @param parameter
     * @return
     */
    public void updateFinished() {
        if (fileUpdateFinishedListener != null && infos.size() == 0) {
            Message message = new Message();
            message.what = UPDATEFINISHED;
            handler.sendMessage(message);
        }
    }

    public FileUpdateInfo get(int index) {
        if (infos == null) {
            return null;
        } else {
            return infos.get(index);
        }
    }

    public void setRemoveFileUpdateInfoListener(RemoveFileUpdateInfoListener removeFileUpdateInfoListener) {
        this.removeFileUpdateInfoListener = removeFileUpdateInfoListener;
    }

    public void setAddFileUpdateInfoListener(AddFileUpdateInfoListener addFileUpdateInfoListener) {
        this.addFileUpdateInfoListener = addFileUpdateInfoListener;
    }

    public void setFileUpdateFinishedListener(FileUpdateFinishedListener fileUpdateFinishedListener) {
        this.fileUpdateFinishedListener = fileUpdateFinishedListener;
    }

    public void clear() {
        if (infos != null) {
            infos.clear();
        }
    }

    public int size() {
        if (infos != null) {
            return infos.size();
        }
        return 0;
    }

    /**
     * 根据TaskID获取文件对象，要保证此文件添加至队列里边了
     *
     * @param parameter
     * @return
     */
    public FileUpdateInfo get(String taskID) {
        if (infos == null) {
            return null;
        } else {
            for (int i = 0; i < infos.size(); i++) {
                if (infos.get(i).taskID.equals(taskID)) {
                    return infos.get(i);
                }
            }
            return null;
        }
    }

    public interface AddFileUpdateInfoListener {
        void addFileUpdateInfo(int index);
    }

    public interface RemoveFileUpdateInfoListener {
        void removeFileUpdateInfo(int index);
    }

    public interface FileUpdateFinishedListener {
        void FileUpdateFinished();
    }
}

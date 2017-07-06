package com.dayang.uploadlib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.dayang.uploadlib.activity.MissionActivity;
import com.dayang.uploadlib.iterface.UpLoadLibInterface;
import com.dayang.uploadlib.model.MissionInfo;
import com.dayang.uploadlib.model.MissionInfoDao;
import com.dayang.uploadlib.service.UpLoadService;
import com.dayang.uploadlib.service.UploadServiceBinder;
import com.dayang.uploadlib.util.Constant;
import com.dayang.uploadlib.util.SharedPreferencesUtils;

import java.util.Date;
import java.util.List;

import static com.dayang.uploadlib.task.FtpUploadTask.TAG;

/**
 * Created by 冯傲 on 2017/5/25.
 * e-mail 897840134@qq.com
 */

public class UploadFileManager implements UpLoadLibInterface {
    static UploadFileManager manager;
    Context context;
    private UploadServiceBinder binder;

    private UploadFileManager() {

    }

    public static UploadFileManager getInstance() {
        if (manager == null) {
            manager = new UploadFileManager();
        }
        return manager;
    }

    public UploadFileManager init(Context context) {
        this.context = context;
        context.bindService(new Intent(context, UpLoadService.class), conn, Context.BIND_AUTO_CREATE);
        return manager;
    }

    private ServiceConnection conn = new ServiceConnection() {


        /** 获取服务对象时的操作 */
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("fengao", "onServiceConnected: " + new Date().getTime());
            binder = (UploadServiceBinder) service;
            binder.startDBMission();
        }

        /** 无法获取到服务对象时的操作 */
        public void onServiceDisconnected(ComponentName name) {
            Log.i("fengao", "onServiceDisconnected: ");

        }

    };

    @Override
    public void pauseAllMission() {
        binder.pauseAllMission();
    }

    @Override
    public void startAllMission() {
        binder.startAllMission();
    }

    @Override
    public void startMission(MissionInfo info) {
        binder.startMission(info);
    }

    @Override
    public void promotePriority(MissionInfo info) {
        binder.promotePriority(info);
    }

    @Override
    public void setMaxUploadMissionCount(int max) {
        binder.setMaxUploadMissionCount(max);
    }

    @Override
    public void deleteMission(MissionInfo info) {
        binder.deleteMission(info);
    }

    @Override
    public void deleteMission(String sessionId) {
        binder.deleteMission(sessionId);
    }

    @Override
    public void deleteAllMission() {
        binder.deleteAllMission();
    }

    @Override
    public void deleteCompleteMission() {
        binder.deleteCompleteMission();
    }

    @Override
    public void addMission(MissionInfo info) {
        binder.addMission(info);
    }

    @Override
    public void addMissions(List<MissionInfo> infoList) {
        binder.addMissions(infoList);
    }

    @Override
    public List<MissionInfo> getMissions() {
        return binder.getMissions();
    }

    @Override
    public void exchangMissionPosition(MissionInfo info1, MissionInfo info2) {
//         binder.exchangMissionPosition();
    }

    @Override
    public void isForbidMobileNetworkUpload(boolean isForbid) {
        binder.isForbidMobileNetworkUpload(isForbid);
    }

    @Override
    public void networkStateChange(int networkState) {
        if (binder != null) {
            binder.networkStateChange(networkState);
        }
    }

    @Override
    public boolean get4gStrategy() {
        return SharedPreferencesUtils.getParam(context, Constant.STRATEGY_4G, false);
    }

    public int getThreadCount() {
        return binder.getThreadCount();
    }

    public MissionInfoDao getDBHelper() {
        return binder.getDBHelper();
    }

    @Override
    public void set4gStrategy(boolean strategy) {
        binder.isForbidMobileNetworkUpload(strategy);
    }

    @Override
    public void destroy() {
        Log.i(TAG, "destroy: ");
        context.unbindService(conn);
    }

    public void setStartAppMode(boolean isStart) {
        SharedPreferencesUtils.setParam(context, Constant.STARTAPPMODE, isStart);
    }

    public boolean getStartAppMode() {
        return SharedPreferencesUtils.getParam(context, Constant.STARTAPPMODE, false);
    }

    public void openMissionManager() {
        Intent intent = new Intent(context, MissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

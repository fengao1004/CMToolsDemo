package com.dayang.uploadlib.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dayang.uploadlib.iterface.UpLoadServiceInterface;
import com.dayang.uploadlib.model.DaoMaster;
import com.dayang.uploadlib.model.DaoSession;
import com.dayang.uploadlib.model.MissionInfo;
import com.dayang.uploadlib.model.MissionInfoDao;
import com.dayang.uploadlib.receiver.NetworkConnectChangedReceiver;
import com.dayang.uploadlib.util.Constant;
import com.dayang.uploadlib.util.NetWorkState;
import com.dayang.uploadlib.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 冯傲 on 2017/6/1.
 * e-mail 897840134@qq.com
 */

public class UpLoadService extends Service implements UpLoadServiceInterface {
    String TAG = "fengao";
    List<MissionInfo> infoList;
    ExecutorService threadPool;
    private NetworkConnectChangedReceiver mNetworkChangeListener;
    private MissionInfoDao missionInfoDao;
    private List<MissionInfo> dbList;
    boolean inited = false;

    @Override
    public void onCreate() {
        super.onCreate();
        threadPool = Executors.newFixedThreadPool(5);
        IntentFilter filter = new IntentFilter();
        mNetworkChangeListener = new NetworkConnectChangedReceiver();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(mNetworkChangeListener, filter);
        infoList = new ArrayList<>();
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        missionInfoDao = daoSession.getMissionInfoDao();
        dbList = missionInfoDao
                .queryBuilder()
                .where(MissionInfoDao.Properties.Status.notEq(MissionInfo.REMOVED))
                .build()
                .list();
        inited = true;
    }

    private void addDBMission() {
        if (infoList.size() != 0) {
            return;
        }
        int uploadTaskCount;
        int maxThreadCount;
        if (dbList == null || dbList.size() == 0) {
            return;
        }
        infoList.addAll(dbList);
        //默认不自动开启未完成任务
        Boolean param = SharedPreferencesUtils.getParam(this, Constant.STARTAPPMODE, false);
        if (param) {
            //会遍历四边结合
            //1 找出上传状态的任务 这种优先级最高
            //2 找出等待上传的任务 优先级第二
            //3 找出关闭时正在上传或者等待网路的任务 优先级第三
            //4 找出关闭时正在等待的任务 优先级最低
            for (MissionInfo info : infoList) {
                if (info.getStatus() == MissionInfo.UPLOADING
                        || info.getStatus() == MissionInfo.WAITINGNETWORDK
                        || info.getStatus() == MissionInfo.WAITINGNETWORDK_WIFI) {
                    startTask(info);
                }
            }
            uploadTaskCount = getUploadTaskCount();
            maxThreadCount = getMaxThreadCount();
            while (maxThreadCount > uploadTaskCount) {
                MissionInfo uploadTask = getUploadTask();
                if (uploadTask == null) {
                    break;
                }
                startTask(getUploadTask());
                uploadTaskCount = getUploadTaskCount();
            }
            for (MissionInfo info : infoList) {
                if (info.getStatus() == MissionInfo.CLOSEAPP_UPLOADING) {
                    startTask(info);
                }
                if (info.getStatus() == MissionInfo.CLOSEAPP_WAITINGUPLOAD) {
                    info.setStatus(MissionInfo.WAITINGUPLOAD);
                }
            }
            uploadTaskCount = getUploadTaskCount();
            while (maxThreadCount > uploadTaskCount) {
                MissionInfo uploadTask = getUploadTask();
                if (uploadTask == null) {
                    break;
                }
                startTask(getUploadTask());
                uploadTaskCount = getUploadTaskCount();
            }
        } else {
            for (MissionInfo info : infoList) {
                if (info.getStatus() == MissionInfo.UPLOADING
                        || info.getStatus() == MissionInfo.WAITINGUPLOAD
                        || info.getStatus() == MissionInfo.WAITINGNETWORDK
                        || info.getStatus() == MissionInfo.WAITINGNETWORDK_WIFI
                        || info.getStatus() == MissionInfo.CLOSEAPP_UPLOADING
                        || info.getStatus() == MissionInfo.CLOSEAPP_WAITINGUPLOAD) {
                    info.pauseMission();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        for (MissionInfo m : infoList) {
            int status = m.getStatus();
            if (status == MissionInfo.UPLOADING) {
                m.getPauseListener().pause();
                m.setStatus(MissionInfo.CLOSEAPP_UPLOADING);
            }
            if (status == MissionInfo.WAITINGNETWORDK || status == MissionInfo.WAITINGNETWORDK_WIFI) {
                m.setStatus(MissionInfo.CLOSEAPP_UPLOADING);
            }
            if (status == MissionInfo.WAITINGUPLOAD) {
                m.setStatus(MissionInfo.CLOSEAPP_WAITINGUPLOAD);
            }
        }
        unregisterReceiver(mNetworkChangeListener);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new UploadServiceBinder(this);
    }


    @Override
    public synchronized void pauseAllMission() {
        for (MissionInfo info : infoList) {
            if (info.getStatus() == MissionInfo.UPLOADING || info.getStatus() == MissionInfo.WAITINGUPLOAD || info.getStatus() == MissionInfo.WAITINGNETWORDK || info.getStatus() == MissionInfo.WAITINGNETWORDK_WIFI)
                info.pauseMission();
        }
    }

    @Override
    public synchronized void startAllMission() {
        for (MissionInfo info : infoList) {
            if (info.getStatus() == MissionInfo.PAUSEING) {
                info.setStatus(MissionInfo.WAITINGUPLOAD);
            }
        }
        int maxThreadCount = getMaxThreadCount();
        int uploadTaskCount = getUploadTaskCount();
        while (maxThreadCount > uploadTaskCount) {
            MissionInfo uploadTask = getUploadTask();
            if (uploadTask == null) {
                break;
            }
            startTask(uploadTask);
            uploadTaskCount = getUploadTaskCount();
        }
    }

    @Override
    public void setMaxUploadMissionCount(int max) {
        max = max > 5 ? 5 : max < 1 ? 1 : max;
        SharedPreferencesUtils.setParam(this, "maxThreadCount", max);
        int uploadTaskCount = getUploadTaskCount();
        while (uploadTaskCount < max) {
            MissionInfo uploadTask = getUploadTask();
            if (uploadTask == null) {
                break;
            } else {
                startTask(uploadTask);
            }
            uploadTaskCount = getUploadTaskCount();
        }

        while (uploadTaskCount > max) {
            int min = 1000001;
            MissionInfo minMissionInfo = null;
            for (MissionInfo missionInfo : infoList) {
                if (missionInfo.getStatus() == MissionInfo.UPLOADING
                        || missionInfo.getStatus() == MissionInfo.WAITINGNETWORDK
                        || missionInfo.getStatus() == MissionInfo.WAITINGNETWORDK_WIFI) {
                    int priority = missionInfo.getPriority();
                    if (priority < min) {
                        minMissionInfo = missionInfo;
                        min = missionInfo.getPriority();
                    }
                }
            }
            if (minMissionInfo == null) {
                break;
            } else {
                minMissionInfo.pauseMission();
            }
        }
    }

    @Override
    public void deleteMission(MissionInfo info) {
        info.del();
        infoList.remove(info);
        info.setStatus(MissionInfo.REMOVED);
    }

    @Override
    public void deleteMission(String sessionId) {
        MissionInfo info = getMissionInfoBySessionId(sessionId);
        deleteMission(info);
    }

    private MissionInfo getMissionInfoBySessionId(String sessionId) {
        for (MissionInfo info : infoList) {
            if (info.getSessionId().equals(sessionId))
                return info;
        }
        return null;
    }

    @Override
    public void deleteAllMission() {
        for (MissionInfo info : infoList) {
            deleteMission(info);
        }
    }

    @Override
    public void deleteCompleteMission() {
        Iterator<MissionInfo> iterator = infoList.iterator();
        while (iterator.hasNext()) {
            MissionInfo next = iterator.next();
            if (next.getStatus() == MissionInfo.UPLOADCOMPLETED) {
                next.setStatus(MissionInfo.REMOVED);
                iterator.remove();
            }
        }
    }

    @Override
    public void promotePriority(MissionInfo info) {
        //TODO 提高任务的优先级
    }

    @Override
    public void startMission(MissionInfo info) {
        info.setStatus(MissionInfo.WAITINGUPLOAD);
        startTask(info);
    }

    @Override
    public synchronized void networkStateChange(int networkState) {
        //只处理联网情况 不处理断网情况 因为断网后任务会自动制成等待网络中
        Boolean forbidMobileNetworkUpload = SharedPreferencesUtils.getParam(this, Constant.STRATEGY_4G, false);
        switch (networkState) {
            case NetworkConnectChangedReceiver.MOBILE:
                //获取网络策略
                if (forbidMobileNetworkUpload) {
                    //有等待网络的任务制成等待wifi
                    List<MissionInfo> waitNetWorkMissions = getWaitNetWorkMissions();
                    for (MissionInfo missionInfo : waitNetWorkMissions) {
                        missionInfo.setStatus(MissionInfo.WAITINGNETWORDK_WIFI);
                    }
                    for (MissionInfo missionInfo : infoList) {
                        if (missionInfo.getStatus() == MissionInfo.UPLOADING) {
                            missionInfo.getPauseListener().pause();
                            missionInfo.setStatus(MissionInfo.WAITINGNETWORDK_WIFI);
                        }
                    }
                    return;
                } else {
                    //开始等待网络任务
                    List<MissionInfo> waitNetWorkMissions = getWaitNetWorkMissions();
                    for (MissionInfo missionInfo : waitNetWorkMissions) {
                        startTask(missionInfo);
                    }
                    List<MissionInfo> waitWifiMissions = getWaitWifiMissions();
                    for (MissionInfo missionInfo : waitWifiMissions) {
                        startTask(missionInfo);
                    }
                    int maxThreadCount = getMaxThreadCount();
                    int uploadTaskCount = getUploadTaskCount();
                    while (maxThreadCount > uploadTaskCount) {
                        MissionInfo uploadTask = getUploadTask();
                        if (uploadTask == null) {
                            break;
                        }
                        startTask(uploadTask);
                        uploadTaskCount = getUploadTaskCount();
                    }
                }
                break;
            case NetworkConnectChangedReceiver.NONE:
                //检查是否还正在上传任务
                //考虑在此做操作是否会和断网任务失败造成冲突
                break;
            case NetworkConnectChangedReceiver.WIFI:
                //开始等待网络任务
                List<MissionInfo> waitNetWorkMissions = getWaitNetWorkMissions();
                for (MissionInfo missionInfo : waitNetWorkMissions) {
                    startTask(missionInfo);
                }
                List<MissionInfo> waitWifiMissions = getWaitWifiMissions();
                for (MissionInfo missionInfo : waitWifiMissions) {
                    startTask(missionInfo);
                }
                int maxThreadCount = getMaxThreadCount();
                int uploadTaskCount = getUploadTaskCount();
                while (maxThreadCount > uploadTaskCount) {
                    MissionInfo uploadTask = getUploadTask();
                    if (uploadTask == null) {
                        break;
                    }
                    startTask(uploadTask);
                    uploadTaskCount = getUploadTaskCount();
                }
                break;
        }
    }

    private List<MissionInfo> getWaitNetWorkMissions() {
        List<MissionInfo> list = new ArrayList<>();
        for (MissionInfo info : infoList) {
            if (info.getStatus() == MissionInfo.WAITINGNETWORDK) {
                list.add(info);
            }
        }
        return list;
    }

    private List<MissionInfo> getWaitWifiMissions() {
        List<MissionInfo> list = new ArrayList<>();
        for (MissionInfo info : infoList) {
            if (info.getStatus() == MissionInfo.WAITINGNETWORDK_WIFI) {
                list.add(info);
            }
        }
        return list;
    }

    @Override
    public void addMission(MissionInfo info) {
        if (infoList == null) {
            infoList = new ArrayList<>();
        }
        int min = getMinimumPriority();
        info.setPriority(min - 1);
        infoList.add(info);
        missionInfoDao.insert(info);
        startTask(info);
    }

    private int getUploadTaskCount() {
        int count = 0;
        for (MissionInfo info : infoList) {
            if ((info.getStatus() == MissionInfo.UPLOADING) || (info.getStatus() == MissionInfo.WAITINGNETWORDK) || (info.getStatus() == MissionInfo.WAITINGNETWORDK_WIFI))
                count++;
        }
        return count;
    }

    /**
     * 调用时间
     * 恢复暂停的任务时
     * TODO 策略考虑恢复时假如没有空闲线程 1 做等待操作 2 暂停一个优先级最低的任务，开启此任务 3 选取优先级最低的上传任务 做优先级判断 暂时第一种策略
     * 往任务池里添加任务后  这个时候任务的状态应该都是等待上传
     * 从数据库读出来缓存任务后 TODO 在这个操作中有一层判断在上级做 就是这时只会添加 UPLOADING状态的任务
     * 此方法只会判断任务池是否还有空闲，和对没网4g wifi的判断
     *
     * @param info 上传任务对象
     * @return void
     */
    private synchronized void startTask(MissionInfo info) {
        if (info == null) {
            return;
        }
        int uploadTaskCount = getUploadTaskCount();
        Integer maxThreadCount = getMaxThreadCount();
        if (uploadTaskCount < maxThreadCount || (info.getStatus() == MissionInfo.UPLOADING) || (info.getStatus() == MissionInfo.WAITINGNETWORDK) || (info.getStatus() == MissionInfo.WAITINGNETWORDK_WIFI)) {
            //获取4g wifi策略
            //true 禁止4g上传 false允许4g上传 默认false
            Boolean forbidMobileNetworkUpload = SharedPreferencesUtils.getParam(this, Constant.STRATEGY_4G, false);
            int netType = NetWorkState.GetNetype(this);
            if (netType == NetWorkState.NONE) {
                info.setStatus(MissionInfo.WAITINGNETWORDK);
            } else if (netType == NetWorkState.MOBILE && forbidMobileNetworkUpload) {
                info.setStatus(MissionInfo.WAITINGNETWORDK_WIFI);
            } else {
                info.setStatus(MissionInfo.UPLOADING);
                Runnable task = info.getTask(this);
                threadPool.execute(task);
            }
        } else {
            info.setStatus(MissionInfo.WAITINGUPLOAD);
        }
    }

    public int getMaxThreadCount() {
        return SharedPreferencesUtils.getParam(this, "maxThreadCount", 3);
    }

    /**
     * 上传任务完成之后被调用，判断list里边是否还有等待上传的任务，如果有挑取优先级最高的任务进行上传，判读的时候需要保证线程安全
     * 在任务线程被调用
     *
     * @param
     * @return
     */
    public synchronized void taskComplete() {
        MissionInfo info = getUploadTask();
        if (info != null) {
            startTask(info);
        }
    }

    /**
     * 获取下一个任务对象
     *
     * @param
     * @return
     */
    private synchronized MissionInfo getUploadTask() {
        if (infoList.size() == 0)
            return null;
        int index = -1;
        int maxPriority = -1;
        for (int i = 0; i < infoList.size(); i++) {
            if (infoList.get(i).getStatus() == MissionInfo.WAITINGUPLOAD) {
                if (maxPriority == -1) {
                    maxPriority = infoList.get(i).getPriority();
                    index = i;
                    continue;
                }
                if (maxPriority < infoList.get(i).getPriority()) {
                    maxPriority = infoList.get(i).getPriority();
                    index = i;
                }
            }
        }
        if (index == -1)
            return null;
        else
            return infoList.get(index);
    }

    private synchronized int getMinimumPriority() {
        if (infoList.size() == 0)
            return 1000000;
        int min = infoList.get(0).getPriority();
        for (int i = 1; i < infoList.size(); i++) {
            min = infoList.get(i).getPriority() < min ? infoList.get(i).getPriority() : min;
        }
        return min;
    }

    @Override
    public synchronized void addMissions(List<MissionInfo> infoList) {
        for (MissionInfo info : infoList) {
            addMission(info);
        }
    }

    @Override
    public List<MissionInfo> getMissions() {
        return infoList;
    }

    @Override
    public void exchangeMissionPosition(MissionInfo info1, MissionInfo info2) {
        int priority1 = info1.getPriority();
        int priority2 = info2.getPriority();
        info1.setPriority(priority2);
        info2.setPriority(priority1);
        Collections.swap(infoList, infoList.indexOf(info1), infoList.indexOf(info2));
    }

    @Override
    public synchronized void isForbidMobileNetworkUpload(boolean isForbid) {
        SharedPreferencesUtils.setParam(this, Constant.STRATEGY_4G, isForbid);
        Log.i(TAG, "isForbidMobileNetworkUpload: " + SharedPreferencesUtils.getParam(this, Constant.STRATEGY_4G, isForbid));
        int netType = NetWorkState.GetNetype(this);
        //获取网络状态
        //如果是4g则暂停上传并设置为等待wifi
        if (isForbid) {
            if (netType == NetWorkState.MOBILE) {
                for (MissionInfo missionInfo : infoList) {
                    if (missionInfo.getStatus() == MissionInfo.UPLOADING) {
                        missionInfo.getPauseListener().pause();
                        missionInfo.setStatus(MissionInfo.WAITINGNETWORDK_WIFI);
                    }
                }
            }
        } else {
            if (netType != NetWorkState.NONE) {
                for (MissionInfo missionInfo : infoList) {
                    if (missionInfo.getStatus() == MissionInfo.WAITINGNETWORDK_WIFI || missionInfo.getStatus() == MissionInfo.WAITINGNETWORDK) {
                        startTask(missionInfo);
                    }
                }
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public MissionInfoDao getDBHelper() {
        return missionInfoDao;
    }

    @Override
    public void startDBMission() {
        addDBMission();
    }

    @Override
    public void setStartAppMode(boolean isStart) {
        SharedPreferencesUtils.setParam(this, Constant.STARTAPPMODE, isStart);
    }

    @Override
    public int getThreadCount() {
        return SharedPreferencesUtils.getParam(this, "maxThreadCount", 3);
    }


}

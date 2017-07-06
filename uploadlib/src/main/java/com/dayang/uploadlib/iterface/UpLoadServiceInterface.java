package com.dayang.uploadlib.iterface;

import com.dayang.uploadlib.model.MissionInfo;
import com.dayang.uploadlib.model.MissionInfoDao;

import java.util.List;

/**
 * Created by 冯傲 on 2017/6/1.
 * e-mail 897840134@qq.com
 */

public interface UpLoadServiceInterface {
    void pauseAllMission();

    void startAllMission();

    void setMaxUploadMissionCount(int max);

    void deleteMission(MissionInfo info);

    void deleteMission(String sessionId);

    void deleteAllMission();

    void deleteCompleteMission();

    void addMission(MissionInfo info);

    void addMissions(List<MissionInfo> infoList);

    List<MissionInfo> getMissions();

    void exchangeMissionPosition(MissionInfo info1, MissionInfo info2);

    void isForbidMobileNetworkUpload(boolean isForbid);

    void promotePriority(MissionInfo info);

    void startMission(MissionInfo info);

    void networkStateChange(int networkState);

    MissionInfoDao getDBHelper();

    void startDBMission();

    void setStartAppMode(boolean isStart);

    int getThreadCount();
}

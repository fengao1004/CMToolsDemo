package com.dayang.uploadlib.iterface;

import android.content.Context;

import com.dayang.uploadlib.model.MissionInfo;

import java.util.List;

/**
 * Created by 冯傲 on 2017/6/1.
 * e-mail 897840134@qq.com
 */

public interface UpLoadLibInterface {

    void pauseAllMission();

    void startAllMission();

    void startMission(MissionInfo info);

    //提升稿件优先级
    void promotePriority(MissionInfo info);

    void setMaxUploadMissionCount(int max);

    void deleteMission(MissionInfo info);

    void deleteMission(String sessionId);

    void deleteAllMission();

    void deleteCompleteMission();

    void addMission(MissionInfo info);

    void addMissions(List<MissionInfo> infoList);

    List<MissionInfo> getMissions();

    void exchangMissionPosition(MissionInfo info1, MissionInfo info2);

    void isForbidMobileNetworkUpload(boolean isForbid);

    void networkStateChange(int networkState);

    boolean get4gStrategy();

    void set4gStrategy(boolean strategy);

    void destroy();
}

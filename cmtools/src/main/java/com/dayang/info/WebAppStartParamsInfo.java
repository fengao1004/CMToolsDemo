package com.dayang.info;

/**
 * Created by 冯傲 on 2017/4/13.
 * e-mail 897840134@qq.com
 */

public class WebAppStartParamsInfo {

    /**
     * url : url
     * firmId : firmId
     * userInfo : userInfo
     * teamToken : teamToken
     */

    private String url;
    private String appId;
    private String userInfo;
    private String teamToken;

    public String getUrl() {
        return url;
    }

    public WebAppStartParamsInfo(String url, String firmId, String userInfo, String teamToken) {
        this.url = url;
        this.appId = firmId;
        this.userInfo = userInfo;
        this.teamToken = teamToken;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFirmId() {
        return appId;
    }

    public void setFirmId(String firmId) {
        this.appId = firmId;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public String getTeamToken() {
        return teamToken;
    }

    public void setTeamToken(String teamToken) {
        this.teamToken = teamToken;
    }
}

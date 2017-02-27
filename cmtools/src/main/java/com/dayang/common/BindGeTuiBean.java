package com.dayang.common;

/**
 * Created by 冯傲 on 2016/8/22.
 * e-mail 897840134@qq.com
 */
public class BindGeTuiBean {
    public String applicationId;
    public String userId;
    public String clientId;
    public String workNo;
    public String tenantCode;

    public BindGeTuiBean(String applicationId, String userId, String clientId, String workNo, String tenantCode) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.clientId = clientId;
        this.workNo = workNo;
        this.tenantCode = tenantCode;
    }

    public BindGeTuiBean(String clientId, String userId, String applicationId) {
        this.clientId = clientId;
        this.userId = userId;
        this.applicationId = applicationId;
    }
}

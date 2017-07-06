package com.dayang.info;

import java.util.List;

/**
 * Created by 冯傲 on 2017/3/8.
 * e-mail 897840134@qq.com
 */

public class MeetingListResponseInfo {

    /**
     * commonResponse : {"success":true,"errorDesc":""}
     * records : [{"name":"胡玉龙team-1","account":"huangxiang@dayang.com.cn","type":2,"pcode":"327256157495"}]
     */

    private CommonResponseEntity commonResponse;
    private List<RecordsEntity> records;

    public void setCommonResponse(CommonResponseEntity commonResponse) {
        this.commonResponse = commonResponse;
    }

    public void setRecords(List<RecordsEntity> records) {
        this.records = records;
    }

    public CommonResponseEntity getCommonResponse() {
        return commonResponse;
    }

    public List<RecordsEntity> getRecords() {
        return records;
    }

    public static class CommonResponseEntity {
        /**
         * success : true
         * errorDesc :
         */

        private boolean success;
        private String errorDesc;

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public void setErrorDesc(String errorDesc) {
            this.errorDesc = errorDesc;
        }

        public boolean getSuccess() {
            return success;
        }

        public String getErrorDesc() {
            return errorDesc;
        }
    }

    public static class RecordsEntity {
        /**
         * name : 胡玉龙team-1
         * account : huangxiang@dayang.com.cn
         * type : 2
         * pcode : 327256157495
         */

        private String name;
        private String account;
        private int type;
        private String pcode;

        public void setName(String name) {
            this.name = name;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setPcode(String pcode) {
            this.pcode = pcode;
        }

        public String getName() {
            return name;
        }

        public String getAccount() {
            return account;
        }

        public int getType() {
            return type;
        }

        public String getPcode() {
            return pcode;
        }
    }
}

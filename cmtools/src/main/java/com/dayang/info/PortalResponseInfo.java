package com.dayang.info;

/**
 * Created by 冯傲 on 2017/3/8.
 * e-mail 897840134@qq.com
 */

public class PortalResponseInfo {

    /**
     * status : true
     * data : {"backendservice":"http://192.168.20.68:8080/portal/api/queryUserInfoByToken","casservice":"http://192.168.20.227:9999/DYPDNewsCommandWeb/api/joinMeeting","description":"","domainname":"houjian.com","extendattribute":"","productcode":"cmtools","projectid":"123","projectname":"厚建测试","projserviceguid":"064c46ec-bca9-44d3-9358-8af5e2152a94","status":0}
     * description : 成功
     */

    private boolean status;
    private DataEntity data;
    private String description;

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getStatus() {
        return status;
    }

    public DataEntity getData() {
        return data;
    }

    public String getDescription() {
        return description;
    }

    public static class DataEntity {
        /**
         * backendservice : http://192.168.20.68:8080/portal/api/queryUserInfoByToken
         * casservice : http://192.168.20.227:9999/DYPDNewsCommandWeb/api/joinMeeting
         * description :
         * domainname : houjian.com
         * extendattribute :
         * productcode : cmtools
         * projectid : 123
         * projectname : 厚建测试
         * projserviceguid : 064c46ec-bca9-44d3-9358-8af5e2152a94
         * status : 0
         */

        private String backendservice;
        private String casservice;
        private String description;
        private String domainname;
        private String extendattribute;
        private String productcode;
        private String projectid;
        private String projectname;
        private String projserviceguid;
        private int status;

        public void setBackendservice(String backendservice) {
            this.backendservice = backendservice;
        }

        public void setCasservice(String casservice) {
            this.casservice = casservice;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setDomainname(String domainname) {
            this.domainname = domainname;
        }

        public void setExtendattribute(String extendattribute) {
            this.extendattribute = extendattribute;
        }

        public void setProductcode(String productcode) {
            this.productcode = productcode;
        }

        public void setProjectid(String projectid) {
            this.projectid = projectid;
        }

        public void setProjectname(String projectname) {
            this.projectname = projectname;
        }

        public void setProjserviceguid(String projserviceguid) {
            this.projserviceguid = projserviceguid;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getBackendservice() {
            return backendservice;
        }

        public String getCasservice() {
            return casservice;
        }

        public String getDescription() {
            return description;
        }

        public String getDomainname() {
            return domainname;
        }

        public String getExtendattribute() {
            return extendattribute;
        }

        public String getProductcode() {
            return productcode;
        }

        public String getProjectid() {
            return projectid;
        }

        public String getProjectname() {
            return projectname;
        }

        public String getProjserviceguid() {
            return projserviceguid;
        }

        public int getStatus() {
            return status;
        }
    }
}

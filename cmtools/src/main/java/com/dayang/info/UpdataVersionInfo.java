package com.dayang.info;

/**
 * Created by 冯傲 on 2016/11/2.
 * e-mail 897840134@qq.com
 */

public class UpdataVersionInfo {
    public boolean needUpdata;
    public String updataUrl;
    public String name;
    public int newstVersionCode;
    public UpdataVersionInfo(String updataUrl, String name, boolean needUpdata,int newstVersionCode) {
        this.updataUrl = updataUrl;
        this.name = name;
        this.needUpdata = needUpdata;
        this.newstVersionCode = newstVersionCode;
    }

}

package com.dayang.info;

/**
 * Created by 冯傲 on 2016/11/2.
 * e-mail 897840134@qq.com
 */

public class AppVersionInfo {
    public String name;
    public String fileName;
    public int versionCode;
    public AppVersionInfo(String name, int versionCode, String fileName) {
        this.name = name;
        this.versionCode = versionCode;
        this.fileName = fileName;
    }
}

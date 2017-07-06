package com.dayang.info;

import android.graphics.Bitmap;

/**
 * Created by 冯傲 on 2017/3/6.
 * e-mail 897840134@qq.com
 */

public class MeetingListInfo {
    public String name;
    public String password;
    public String account;
    public String url;
    public Bitmap bitmap;
    public int resource;
    public int imgType;
    public final int IMGTYPE_URL = 0;
    public final int IMGTYPE_BITMAP = 1;
    public final int IMGTYPE_RESOURCE = 2;

    public MeetingListInfo(String name, Bitmap bitmap) {
        imgType = IMGTYPE_BITMAP;
        this.name = name;
        this.bitmap = bitmap;
    }

    public MeetingListInfo(String name, int resource) {
        imgType = IMGTYPE_RESOURCE;
        this.name = name;
        this.resource = resource;
    }

    public MeetingListInfo(String name, String account, String password) {
        imgType = IMGTYPE_RESOURCE;
        this.name = name;
        this.password = password;
        this.account = account;
    }
}

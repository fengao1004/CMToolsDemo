package com.dayang.common;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class MyAppliction extends Application {
    static Context context = null;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //ImageLoader 初始化用于加载图片
        CMToolsManager.getInstance().init(this,CMToolsManager.MODE_NORMAL).setMediaFileRootPath(Environment.getExternalStorageState());
        //文件上传展示列表清空
    }

    public static Context getContext() {
        if (context == null) {
            return null;
        }
        return context;
    }

}

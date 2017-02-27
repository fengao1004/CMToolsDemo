package com.dayang.common;

import android.app.Application;
import android.content.Context;

import com.dayang.info.FilesUpdateManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class MyAppliction extends Application {
    static Context context = null;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //ImageLoader 初始化用于加载图片
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
//        PushManager.getInstance().initialize(this.getApplicationContext());
        //文件上传展示列表清空
        FilesUpdateManager.getInstance().clear();
    }

    public static Context getContext() {
        if (context == null) {
            return null;
        }
        return context;
    }

}

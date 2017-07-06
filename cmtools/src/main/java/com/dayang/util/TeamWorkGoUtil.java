package com.dayang.util;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 说明：协同平台跳转工具类
 * 作者：wangya
 * 时间：2017/4/13
 */

public class TeamWorkGoUtil {


    /**
     * 跳转阿米协同工具类
     *
     * @param mContext
     * @param outlink
     * @param bundle
     */
    public static void goTo(Context mContext, String outlink, Bundle bundle) {
        try {
            Class cls = Class.forName("com.hoge.android.cooperation.base.util.Go2Util");
            Method setMethod = cls.getDeclaredMethod("goTo", Context.class, String.class, Bundle.class);
            setMethod.invoke(cls.newInstance(), mContext, outlink, bundle);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取appid
     *
     * @return
     */
    public static String getAppId() {
        // Variable.APP_ID
        // Variable.USER_TOKEN
        String className = "com.hoge.android.cooperation.base.constans.Variable";
        String variableName = "APP_ID";
        return getString(className, variableName);

    }

    /**
     * 获取teamtoken
     *
     * @return
     */
    public static String getTeamToken() {
        // Variable.USER_TOKEN
        String className = "com.hoge.android.cooperation.base.constans.Variable";
        String variableName = "USER_TOKEN";
        return getString(className, variableName);
    }


    /**
     * 获取工号
     *
     * @return
     */
    public static String getJobNum() {
        // Variable.USER_JOBNO
        String className = "com.hoge.android.cooperation.base.constans.Variable";
        String variableName = "USER_JOBNO";
        return getString(className, variableName);
    }


    /**
     * 反射获取静态变量
     *
     * @param className
     * @param variableName
     * @return
     */
    private static String getString(String className, String variableName) {
        String s = null;
        try {
            Class<?> clazz = Class.forName(className);
            Field field = clazz.getDeclaredField(variableName);
            s = field.get(null).toString();
        } catch (Exception e) {
            Log.e("fengao", e.getMessage());
        }
        return s;
    }

}

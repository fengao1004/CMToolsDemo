package com.dayang.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.dayang.common.Canstant;
import com.dayang.common.OkHttpUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;

import java.io.IOException;

/**
 * Created by 冯傲 on 2016/7/5.
 * e-mail 897840134@qq.com
 */
public class LocationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
//    String TAG = Canstant.TAG;
//    private String deviceId;
//    private static String userId;
//    private static String tenantId;
//    private static String userName;
//    private static String postUrl;
//    static double longitude;
//
//    public static void setIsOpen(boolean isOpen) {
//        LocationService.isOpen = isOpen;
//    }
//
//    public static boolean isOpen = true;
//
//    public static Location getLocation() {
//        return location;
//    }
//
//    static Location location = null;
//
//    public static double getLongitude() {
//        return longitude;
//    }
//
//    public static double getLatitude() {
//        return latitude;
//    }
//
//    static double latitude;
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    static public void setUserId(String userId_) {
//        userId = userId_;
//    }
//
//    static public void setTenantId(String tenantId_) {
//        tenantId = tenantId_;
//    }
//
//    static public void setUserName(String userName_) {
//        userName = userName_;
//    }
//
//    static public void setPostUrl(String postUrl_) {
//        postUrl = postUrl_;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.i(TAG, "进入定位服务 ");
//        if (intent != null) {
//            userName = intent.getStringExtra("userName");
//            userId = intent.getStringExtra("userId");
//            tenantId = intent.getStringExtra("tenantId");
//            postUrl = intent.getStringExtra("locationUrl");
//            TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
//            deviceId = tm.getDeviceId();
//            initLocation();
//        }
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    // 高德定位的代理对象
//    private LocationManagerProxy mLocationManagerProxy;
//    // 定位广播通知的ACTION串
//    public static final String GPSLOCATION_BROADCAST_ACTION = "com.location.apis.gpslocation.broadcast";
//    private PendingIntent mPendingIntent;
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            initLocation();
//        }
//    };
//    // 定位成功后的通知接受器
//    private BroadcastReceiver mGPSLocationReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // 接受广播
//            if (intent.getAction().equals(GPSLOCATION_BROADCAST_ACTION)) {
//                // 只进行一次定位，定位成功后移除定位请求
//                mLocationManagerProxy.removeUpdates(mPendingIntent);
//                Bundle bundle = intent.getExtras();
//                // 获取bundle里的数据
//                Parcelable parcelable = bundle
//                        .getParcelable(LocationManagerProxy.KEY_LOCATION_CHANGED);
//                location = (Location) parcelable;
//                if (location == null) {
//                    return;
//                }
//                // 定位成功回调信息，设置相关消息
//                longitude = location.getLongitude();
//                latitude = location.getLatitude();
//                Log.i(TAG, "返回得经纬度信息 " + location.getLongitude() + "  " + location.getLatitude());
//                if (isOpen) {
//                    post(location.getLongitude() + "", location.getLatitude() + "");
//                }
//            }
//        }
//    };
//
//    /**
//     * 定位初始化并且实现定位功能的及时更新
//     */
//    public void initLocation() {
//        Log.i(TAG, "定位初始化！");
//        mHandler.sendEmptyMessageDelayed(1, 1000 * 60);
//        if (isOpen) {
//            IntentFilter fliter = new IntentFilter(
//                    ConnectivityManager.CONNECTIVITY_ACTION);
//            fliter.addAction(GPSLOCATION_BROADCAST_ACTION);
//            this.registerReceiver(mGPSLocationReceiver, fliter);
//            Intent intent = new Intent(GPSLOCATION_BROADCAST_ACTION);
//            mPendingIntent = PendingIntent.getBroadcast(
//                    this.getApplicationContext(), 0, intent, 0);
//            mLocationManagerProxy = LocationManagerProxy.getInstance(this);
//            // 采用peddingIntent方式进行对定位调用
//            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
//            // 在定位结束后，在合适的生命周期调用destroy()方法
//            // 其中如果间隔时间为-1，则定位只定一次
//            // 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
//            mLocationManagerProxy.requestLocationUpdates(
//                    LocationProviderProxy.AMapNetwork, 60 * 1000, 15,
//                    mPendingIntent);
//            mLocationManagerProxy.setGpsEnable(false);
//            // 如果一直定位失败则2min后停止定位
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mLocationManagerProxy.removeUpdates(mPendingIntent);
//                }
//            }, 55 * 1000);
//        }
//    }
//
//    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//
//    public void post(String x, String y) {
//        UserLocation user = new UserLocation(deviceId, userName, x, y, "Android", userId, tenantId);
//        Gson gson = new Gson();
//        String json = gson.toJson(user);
//        try {
//            OkHttpUtil okHttpUtil = new OkHttpUtil();
//            Log.i(TAG, "gps发送地址: " + postUrl);
//            okHttpUtil.call(postUrl, json, new OkHttpUtil.OkHttpCallBack() {
//                @Override
//                public void success(Response response) throws Exception {
//                    Log.i(TAG, "gps请求返回: " + response.body().string());
//                    response.body().close();
//                }
//
//                @Override
//                public void error(Request request, IOException e) {
//
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//    @Override
//    public void onDestroy() {
//        mHandler.removeMessages(1);
//    }
//
//    class UserLocation {
//        public UserLocation(String deviceId, String userName, String longitude, String lantitude, String osInfos, String userId, String tenantId) {
//            this.deviceId = deviceId;
//            this.userId = userId;
//            this.userName = userName;
//            this.lantitude = lantitude;
//            this.longitude = longitude;
//            this.osInfos = osInfos;
//            this.tenantId = tenantId;
//        }
//
//        String tenantId;
//        String deviceId;
//        String userName;
//        String longitude;
//        String lantitude;
//        String osInfos;
//        String userId;
//    }
}

package com.dayang.device;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class GpsNetNotifyDevice extends Device {
	public GpsNetNotifyDevice(Context context, Activity activity) {
		super(context, activity);
	}

	/**
	 * 获取gps信息
	 * 
	 * @param
	 * @return
	 */
	@JavascriptInterface
	public String getGpsLocation() {
		Location location = null;
		String locationStr = null;
		try {
			if (!openGPSSettings()) {
				Toast.makeText(this.context, "请开启GPS！", Toast.LENGTH_SHORT)
						.show();
			} else {
				location = this.getLocation();
				if (location != null) {
					locationStr = location.getLongitude() + ";"
							+ location.getLatitude();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return locationStr;
	}

	/**
	 * 验证是否打开GPS
	 * 
	 * @return
	 */
	public boolean openGPSSettings() {
		LocationManager alm = (LocationManager) this.context
				.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return true;
		}
		return false;

	}

	/**
	 * 得到GPS定位的信息
	 * 
	 * @return
	 */
	public Location getLocation() {
		Location location = null;
		// 获取位置管理服务
		try {
			LocationManager locationManager;
			String serviceName = Context.LOCATION_SERVICE;
			locationManager = (LocationManager) this.activity
					.getSystemService(serviceName);
			// 查找到服务信息
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗

			String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
			location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
		} catch (Exception e) {
			e.printStackTrace();
		}
		return location;
	}

	/**
	 * 通知栏消息展示
	 * 
	 * @param
	 * @return
	 */
	@JavascriptInterface
	public boolean notifyInfo(String content, String infoTitle) {

		try {
			// 定义NotificationManager
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) this.context
					.getSystemService(ns);
			// 定义通知栏展现的内容信息
			int icon = android.R.drawable.stat_notify_chat;
			CharSequence tickerText = "我的通知栏标题";
			long when = System.currentTimeMillis();
			Notification notification = new Notification(icon, tickerText, when);
			notification.defaults = Notification.DEFAULT_SOUND;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			CharSequence contentTitle = "审片通知";
			// CharSequence contentText = "我的通知栏展开详细内容";
			Intent notificationIntent = new Intent(this.context,
					this.activity.getClass());
			PendingIntent contentIntent = PendingIntent.getActivity(
					this.context, 0, notificationIntent,
					PendingIntent.FLAG_CANCEL_CURRENT);

			NotificationCompat.Builder builder =
					new NotificationCompat.Builder(this.context)
							.setContentTitle(contentTitle)
							.setContentText(content)
					.setContentIntent(contentIntent);
			notification = builder.build();
			mNotificationManager.notify(1, notification);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 检查网络是否可用
	 * 
	 * @param
	 * @return
	 */
	@JavascriptInterface
	public boolean netCheck() {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) this.context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		} else {
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}

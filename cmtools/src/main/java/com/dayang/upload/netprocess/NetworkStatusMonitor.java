package com.dayang.upload.netprocess;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStatusMonitor {

	private static NetworkTaskDelegate taskDelegate;
	private boolean stopMonitorFlag;
	private boolean isMonitoring;
	
	private Timer timer = null;
	private TimerTask timerNetMonitorTask = null;
	
	public NetworkStatusMonitor(NetworkTaskDelegate taskDelgate,final Activity activity) {
		this.taskDelegate = taskDelgate;
		timer = new Timer();
		timerNetMonitorTask = new TimerTask() {
			@Override
			public void run() {
				if (taskDelegate != null) {
					if (netCheck(activity)) {
						taskDelegate.processTask();
						taskDelegate.isTaskFinished();
					}
				}
			}
		};
	}
	public void start() {
		if (timer != null && timerNetMonitorTask != null) {
			timer.schedule(timerNetMonitorTask, 1000);
		}
		stopMonitorFlag = false;
		isMonitoring = true;
	}
	
	public void stop() {
		timer.cancel();
		stopMonitorFlag = true;
		isMonitoring = false;
	}
	
	
	public static boolean netCheck(Activity activity) {
		ConnectivityManager connectivityManager = (ConnectivityManager) activity.getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		} else {
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public NetworkTaskDelegate getTaskDelegate() {
		return taskDelegate;
	}
	public void setTaskDelegate(NetworkTaskDelegate taskDelegate) {
		this.taskDelegate = taskDelegate;
	}
	public boolean isStopMonitorFlag() {
		return stopMonitorFlag;
	}
	public void setStopMonitorFlag(boolean stopMonitorFlag) {
		this.stopMonitorFlag = stopMonitorFlag;
	}
	
	
	
}


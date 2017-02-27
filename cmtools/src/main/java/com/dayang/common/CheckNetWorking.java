package com.dayang.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNetWorking {
	public static boolean checkNetWorking(Context context) {
		boolean iswork = false;
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		if (info == null) {
			return iswork;
		} else {
			iswork = info.isAvailable();
		}
		return iswork;
	}
}

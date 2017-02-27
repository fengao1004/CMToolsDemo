package com.dayang.common;

import android.util.Log;

public class WaitUploadThread extends Thread{
	@Override
	public void run() {
		synchronized (Constants.lockObject) {
				try {
					Constants.lockObject.wait();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		Log.e("debug", "锁被释放");
	}
}

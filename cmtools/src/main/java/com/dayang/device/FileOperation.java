package com.dayang.device;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.dayang.common.Canstant;
import com.dayang.pickfile.ImgFileListActivity;

/**
 * 用于打开手机文件和多媒体文件
 * 
 * @author renyuwei
 * 
 */
public class FileOperation extends Device {
	public FileOperation(Context context, Activity activity, Handler handler) {
		super(context, activity, handler);
	}


	/**
	 * 打开多媒体文件
	 * 
	 * @param
	 * @return
	 */
	public void pickMediaFile() {
		try {
		int maxnum = 1;
		Intent intent = new Intent(activity,ImgFileListActivity.class);
		intent.putExtra("imgNum", maxnum);
		activity.startActivityForResult(intent,Canstant.FILE_SELECT_REQUEST);} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

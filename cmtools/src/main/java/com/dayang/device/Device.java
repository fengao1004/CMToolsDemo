package com.dayang.device;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 摄像机，麦克风设备的抽象父类
 * 
 * @author renyuwei
 * 
 */
public abstract class Device {
	Context context;
	Activity activity;
	Handler handler;

	public Device(Context context, Activity activity, Handler handler) {
		this.context = context;
		this.activity = activity;
		this.handler = handler;
	}

	public Device(Context context, Activity activity) {
		this.context = context;
		this.activity = activity;
	}

	/**
	 * 根据日期名新建文件
	 * 
	 * @param fileDir
	 * @param fileExtType
	 * @return
	 */
	public File getFileByDate(String fileDir, String fileExtType) {
		File fileout = null;
		try {
			File out = null;
			SimpleDateFormat sDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String date = sDateFormat.format(new Date());
			date = date.replaceAll(" |:|-", "");
			String uploadPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + File.separator + fileDir + "/";
			out = new File(uploadPath);
			if (!out.exists()) {
				out.mkdirs();
			}
			String uplaodFileName = date.toString() + "." + fileExtType;
			fileout = new File(uploadPath, uplaodFileName);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return fileout;
	}
}

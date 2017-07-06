package com.dayang.device;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import com.dayang.common.Canstant;

import java.io.File;

/**
 * 摄像和录制视频
 * 
 * @author renyuwei
 * 
 */
public class CameraDevice extends Device {

	public CameraDevice(Context context, Activity activity, Handler handler) {
		super(context,activity, handler);
	}

	/**
	 * 摄像
	 * 
	 * @return
	 */
	public File takePhoto() {
		Log.e("debug", "开始摄像并调用摄像机");
		File imageFile = this.getFileByDate("images", "jpg");
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 构造intent
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
		this.activity.startActivityForResult(cameraIntent,
				Canstant.CAMERA_REQUEST);// 发出intent，并要求返回调用结果
		Log.e("debug", "图像保存并显示成功");
		return imageFile;
	}

	/**
	 * 录制视频
	 * 
	 * @return
	 */
	public File recordVideo() {
		Log.e("debug", "开始录像并调用摄像机");
		Intent cameraVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);// 构造intent
		File out = getFileByDate("videos", "mp4");
		Uri uri = Uri.fromFile(out);
		cameraVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		cameraVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		this.activity.startActivityForResult(cameraVideoIntent,
				Canstant.CAMERA_VIDEO_REQUEST);// 发出intent，并要求返回调用结果
		return out;
	}

}

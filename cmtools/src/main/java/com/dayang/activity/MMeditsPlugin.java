package com.dayang.activity;

import io.dcloud.common.DHInterface.IApp;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.StandardFeature;
import io.dcloud.common.util.JSUtil;

import java.io.File;

import org.json.JSONArray;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;

import com.dayang.common.Canstant;
import com.dayang.common.Constants;
import com.dayang.common.UploadStatus;
import com.dayang.common.WaitUploadThread;
import com.dayang.device.AudioDevice;
import com.dayang.device.CameraDevice;
import com.dayang.device.FileOperation;
import com.dayang.device.GpsNetNotifyDevice;
import com.dayang.sysevent.ISysEventExt;
import com.dayang.upload.UploadFileThread;

public class MMeditsPlugin extends StandardFeature {
	public static int uploadStatus;
	public static Handler handler = null;
	private static String cameraImagePath = null;
	private CameraDevice cameraDevice = null;
	private AudioDevice audioDevice = null;
	private FileOperation fileOperation = null;
	private GpsNetNotifyDevice gpsNetNotifyDevice = null;
	public static UploadFileThread ftpUpload;
	public static UploadFileThread httpUpload;
	private ProgressDialog progressDialog = null;
	public static String url = "";
	public static String uploadType = "";
	public static File uploadFile;

	public static String uploadFilePath = "";

	private WebView webView = null;
	private Activity activity = null;

	public static Thread currentThread = null;
	public void takePhoto(IWebview pWebview, JSONArray array) {
		String callbackId = array.optString(0);
		String url = array.optString(1);
		String uploadType = array.optString(2);
		
		try {
			this.webView = pWebview.obtainWebview();
			activity = pWebview.getActivity();
			IApp app = pWebview.obtainFrameView().obtainApp();
			ISysEventExt sysEventExt = new ISysEventExt(app, pWebview,callbackId);
			app.registerSysEventListener(sysEventExt,
					SysEventType.onActivityResult);
			cameraDevice = new CameraDevice(activity.getApplicationContext(),
					activity, handler);
			this.url = url;
			this.uploadType = uploadType;
			this.uploadFile = cameraDevice.takePhoto();
		} catch (Exception e) {
			e.printStackTrace();
		   JSUtil.execCallback(pWebview, callbackId, Canstant.UPLOADFAILTURETAG, JSUtil.OK, false);
		}
	}

	public void recordVideo(IWebview pWebview, JSONArray array) {
		String callbackId = array.optString(0);
		String url = array.optString(1);
		String uploadType = array.optString(2);
		try {
			this.webView = pWebview.obtainWebview();
			activity = pWebview.getActivity();
			IApp app = pWebview.obtainFrameView().obtainApp();
			ISysEventExt sysEventExt = new ISysEventExt(app, pWebview,callbackId);
			app.registerSysEventListener(sysEventExt,
					SysEventType.onActivityResult);
			cameraDevice = new CameraDevice(activity.getApplicationContext(),
					activity, handler);
			this.url = url;
			this.uploadType = uploadType;
			this.uploadFile = cameraDevice.recordVideo();
		} catch (Exception e) {
			e.printStackTrace();
			JSUtil.execCallback(pWebview, callbackId, Canstant.UPLOADFAILTURETAG, JSUtil.OK, false);
		}
	}

	public void recordAudio(IWebview pWebview, JSONArray array) {
		this.webView = pWebview.obtainWebview();
		activity = pWebview.getActivity();
		audioDevice = new AudioDevice(activity.getApplicationContext(),
				activity, handler);
		String callbackId = array.optString(0);
		String url = array.optString(1);
		String uploadType = array.optString(2);
		try {
			this.webView = pWebview.obtainWebview();
			activity = pWebview.getActivity();
			IApp app = pWebview.obtainFrameView().obtainApp();
			ISysEventExt sysEventExt = new ISysEventExt(app, pWebview,callbackId);
			app.registerSysEventListener(sysEventExt,
					SysEventType.onActivityResult);
			cameraDevice = new CameraDevice(activity.getApplicationContext(),
					activity, handler);
			this.url = url;
			this.uploadType = uploadType;
			Intent recordIntent = new Intent(activity,
					RecordAudioActivity.class);
			activity.startActivityForResult(recordIntent,
					Canstant.RECORD_AUDIO_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			JSUtil.execCallback(pWebview, callbackId, Canstant.UPLOADFAILTURETAG, JSUtil.OK, false);
		}
	}

	public void pickMediaFile(IWebview pWebview, JSONArray array) {
		this.webView = pWebview.obtainWebview();
		activity = pWebview.getActivity();
		fileOperation = new FileOperation(activity.getApplicationContext(),
				activity, handler);
		String callbackId = array.optString(0);
		String url = array.optString(1);
		String uploadType = array.optString(2);
		try {
			this.webView = pWebview.obtainWebview();
			activity = pWebview.getActivity();
			IApp app = pWebview.obtainFrameView().obtainApp();
			ISysEventExt sysEventExt = new ISysEventExt(app, pWebview,callbackId);
			app.registerSysEventListener(sysEventExt,
					SysEventType.onActivityResult);
			cameraDevice = new CameraDevice(activity.getApplicationContext(),
					activity, handler);
			this.url = url;
			this.uploadType = uploadType;
			fileOperation.pickMediaFile();
		} catch (Exception e) {
			e.printStackTrace();
			JSUtil.execCallback(pWebview, callbackId, Canstant.UPLOADFAILTURETAG, JSUtil.OK, false);
		}
	}

}

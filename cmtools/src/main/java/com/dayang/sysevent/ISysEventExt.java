package com.dayang.sysevent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.dayang.activity.MMeditsPlugin;
import com.dayang.common.Canstant;
import com.dayang.upload.HttpUpload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.dcloud.common.DHInterface.IApp;
import io.dcloud.common.DHInterface.ISysEventListener;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.util.JSUtil;

public class ISysEventExt implements ISysEventListener {
	private static ProgressDialog progressDialog = null;
	private IApp iApp;
	private Activity activity;
    private Handler handler;
    private IWebview pWebview;
    private String CallBackID;
    public static String returnresult = "";
	public ISysEventExt(IApp iApp,IWebview pwebview,String callbackId) {
		this.iApp = iApp;
		this.activity = pwebview.getActivity();
		this.pWebview = pwebview;
		this.CallBackID = callbackId;
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try {
					if (msg.what == Canstant.UPLOADSUCCESS) {

						MMeditsPlugin.uploadStatus = Canstant.UPLOADSUCCESSSTATUS;
					} else if (msg.what == Canstant.UPLOADFAILTURE) {

						MMeditsPlugin.uploadStatus = Canstant.UPLOADFAILTURESTATUS;
					} else if (msg.what == Canstant.UPLOADCANCELSTATUS) {
						MMeditsPlugin.uploadStatus = Canstant.UPLOADCANCELSTATUS;
					}
					/*if (progressDialog != null) {
						progressDialog.dismiss();
					}*/
					returnresult = returnResult();

					JSUtil.execCallback(pWebview, CallBackID,returnresult, JSUtil.OK, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}

		};
		MMeditsPlugin.handler = handler;
	}
	

	@Override
	public boolean onExecute(SysEventType pEventType, Object pArgs) {
		   Object[] _args = (Object[])pArgs;
           int requestCode = (Integer)_args[0];
           Intent data = (Intent)_args[2];
		if (pEventType == SysEventType.onActivityResult) {
			onActivityResultCall(requestCode,data);
		}
		return false;
	}
	public void onActivityResultCall(int requestCode, Intent data) {
		switch (requestCode) {
		case Canstant.CAMERA_REQUEST:
			Log.e("debug", "摄像结果回调");
			cameraImageResult(data, requestCode);
			break;
		case Canstant.CAMERA_VIDEO_REQUEST:
			Log.e("debug", "录像结果回调");
			cameraVideoResult(data, requestCode);
			break;
		case Canstant.RECORD_AUDIO_REQUEST:
			Log.e("debug", "录音结果回调");
			mkefengAudioResult(data, requestCode);
			break;
		case Canstant.FILE_SELECT_REQUEST:
			Log.e("debug", "媒体文件选择后结果回调");
			imageSelectResult(data, requestCode);
			break;
		}	
	
	}
	
	/*稿件编辑回调*/
	/**
	 * 保存摄像机拍摄的照片
	 * 
	 * @param data
	 * @param requestCode
	 * @return
	 */
	public void cameraImageResult(Intent data, int requestCode) {
		try {
			if (MMeditsPlugin.uploadFile.exists()) {
				MMeditsPlugin.uploadFilePath = MMeditsPlugin.uploadFile.getAbsolutePath();
			} else {
				throw new Exception("取消操作！");
			}
			if (MMeditsPlugin.uploadType.equals(Canstant.FTPUPLOAD)) {
				Log.e("debug", "ftp上传");
//				MMeditsPlugin.ftpUpload = new FtpUpload(MMeditsPlugin.url,"", MMeditsPlugin.handler,
//						Canstant.UPLOADSINGLE, MMeditsPlugin.uploadFilePath, null,"",activity,"");
				//MMeditsPlugin.ftpUpload.start();
			} else if (MMeditsPlugin.uploadType.equals(Canstant.HTTPUPLOAD)) {
//				MMeditsPlugin.httpUpload = new HttpUpload(MMeditsPlugin.url,"",  MMeditsPlugin.handler,
//						Canstant.UPLOADSINGLE, MMeditsPlugin.uploadFilePath,null, "",activity,"");
//				MMeditsPlugin.httpUpload.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			MMeditsPlugin.handler.sendEmptyMessage(Canstant.UPLOADCANCELSTATUS);
		}
	}

	/**
	 * 保存摄像机录像的视频
	 * 
	 * @param data
	 * @param requestCode
	 * @return
	 */
	@SuppressLint("NewApi")
	public void cameraVideoResult(Intent data, int requestCode) {
		/*progressDialog = getProgressDialog();
		progressDialog.show();*/
		try {
			Log.e("debug", "摄像后进行视频保存");
			if (requestCode == Canstant.CAMERA_VIDEO_REQUEST) {
				if (MMeditsPlugin.uploadFile.exists()) {
					MMeditsPlugin.uploadFilePath = MMeditsPlugin.uploadFile.getAbsolutePath();
				} else {
					throw new Exception("取消操作！");
				}
				if (MMeditsPlugin.uploadType.equals(Canstant.FTPUPLOAD)) {
					Log.e("debug", "ftp上传");
//					MMeditsPlugin.ftpUpload = new FtpUpload(MMeditsPlugin.url,"",  MMeditsPlugin.handler,
//							Canstant.UPLOADSINGLE, MMeditsPlugin.uploadFilePath, null,"",activity,"");
					MMeditsPlugin.ftpUpload.start();
				} else if (MMeditsPlugin.uploadType.equals(Canstant.HTTPUPLOAD)) {
//					MMeditsPlugin.httpUpload = new HttpUpload(MMeditsPlugin.url,"", MMeditsPlugin.handler,
//							Canstant.UPLOADSINGLE, MMeditsPlugin.uploadFilePath,null, "",activity,"");
//					MMeditsPlugin.httpUpload.start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			MMeditsPlugin.handler.sendEmptyMessage(Canstant.UPLOADCANCELSTATUS);
		}
	}

	
	public void mkefengAudioResult(Intent data, int requestCode) {
		/*progressDialog = getProgressDialog();
		progressDialog.show();*/
		try {
			File vedioFile = null;
			Log.e("debug", "录音后进行视频保存");
			if (requestCode == Canstant.RECORD_AUDIO_REQUEST) {
				String filePath = (String)data.getExtras().get("audiofilePath");
				if (filePath != null && !filePath.equals("")) {
					vedioFile = new File(filePath);
					MMeditsPlugin.uploadFilePath = vedioFile.getAbsolutePath();
					MMeditsPlugin.uploadFile = vedioFile;
				} else {
					throw new Exception("没有录音可用");
				}
				if (MMeditsPlugin.uploadType.equals(Canstant.FTPUPLOAD)) {
					Log.e("debug", "ftp上传");
//					MMeditsPlugin.ftpUpload = new FtpUpload(MMeditsPlugin.url,"", MMeditsPlugin.handler,
//							Canstant.UPLOADSINGLE, MMeditsPlugin.uploadFilePath, null,"",activity,"");
					MMeditsPlugin.ftpUpload.start();
				} else if (MMeditsPlugin.uploadType.equals(Canstant.HTTPUPLOAD)) {
//					MMeditsPlugin.httpUpload = new HttpUpload(MMeditsPlugin.url,"", MMeditsPlugin.handler,
//							Canstant.UPLOADSINGLE, MMeditsPlugin.uploadFilePath, null,"",activity,"");
//					MMeditsPlugin.httpUpload.start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			MMeditsPlugin.handler.sendEmptyMessage(Canstant.UPLOADCANCELSTATUS);
		}
	}
	
	
	/**
	 * 选择多媒体文件后的回调函数
	 * 
	 * @param data
	 * @param requestCode
	 */
	public void imageSelectResult(Intent data, int requestCode) {
		/*progressDialog = getProgressDialog();
		progressDialog.show();*/
		try {
			Log.e("debug", "多媒体文件选择后结果回调");
			if (requestCode == Canstant.FILE_SELECT_REQUEST) {
				if(data == null || data.getExtras() == null) {
					throw new Exception("返回错误！");
				}
				Bundle bundle =  data.getExtras();
				ArrayList<String> listfile = null;
				if (bundle != null) {
					if (bundle.getStringArrayList("files") != null) {
					    listfile = bundle.getStringArrayList("files");
					    String filepath = listfile.get(0);
					    filepath = URLDecoder.decode(filepath, "utf-8");
						MMeditsPlugin.uploadFile = new File(filepath);
						MMeditsPlugin.uploadFilePath = filepath;
						Log.e("debug", filepath);
						if (MMeditsPlugin.uploadType.equals(Canstant.FTPUPLOAD)) {
							Log.e("debug", "ftp上传");
//							MMeditsPlugin.ftpUpload = new FtpUpload(MMeditsPlugin.url,"", MMeditsPlugin.handler,
//									Canstant.UPLOADSINGLE, filepath, null,"",activity,"");
							MMeditsPlugin.ftpUpload.start();
						} else if (MMeditsPlugin.uploadType.equals(Canstant.HTTPUPLOAD)) {
//							MMeditsPlugin.httpUpload = new HttpUpload(MMeditsPlugin.url,"", MMeditsPlugin.handler,
//									Canstant.UPLOADSINGLE, filepath,null, "",activity,"");
//							MMeditsPlugin.httpUpload.start();
						}
					} else {
						throw new Exception("没有选择文件！");
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			MMeditsPlugin.handler.sendEmptyMessage(Canstant.UPLOADCANCELSTATUS);
		}
	}

	public  ProgressDialog getProgressDialog() {
		if (progressDialog != null) {
			return progressDialog;
		} else {
			progressDialog = new ProgressDialog(this.activity);
			progressDialog.setMessage("正在上传中,请稍等...");
			progressDialog.setCanceledOnTouchOutside(false);
		    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    return progressDialog;
		}
	}
	
	public String imageToBase64(File imgfile) {
		InputStream in = null;
		String strBase64 = null;
		byte[] data = null;
		try {
			in = new FileInputStream(imgfile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		strBase64 = Base64.encodeToString(data, Base64.NO_WRAP);
		return strBase64;// 返回Base64编码过的字节数组字符串
	}
	
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
	
    private Bitmap getVideoThumbnail(String videoPath, int width, int height,  
            int kind) {  
        Bitmap bitmap = null;  
        // 获取视频的缩略图  
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);  
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
        return bitmap;  
    }  
	public String Base64Bitmap(Bitmap bitmap) {
		   byte [] bytes = null;
			try {
				ByteArrayOutputStream bStream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
				bStream.flush();
				bStream.close();
			    bytes = bStream.toByteArray();
			} catch (Exception e) {
				Log.e("debug", e.toString());
			} 
	      return Base64.encodeToString(bytes, Base64.NO_WRAP);
		}
	
	public String returnResult() {
		try {
			Log.e("等待结束", "uploadStatus=" + MMeditsPlugin.uploadStatus);
			if (MMeditsPlugin.uploadStatus == Canstant.UPLOADSUCCESSSTATUS) {
				return MMeditsPlugin.uploadFile.getName();
			} else if (MMeditsPlugin.uploadStatus== Canstant.UPLOADFAILTURESTATUS) {
				JSUtil.execCallback(pWebview, CallBackID, Canstant.UPLOADFAILTURETAG, JSUtil.OK, false);
				return Canstant.UPLOADFAILTURETAG;
			} else if (MMeditsPlugin.uploadStatus== Canstant.UPLOADCANCELSTATUS) {
				return Canstant.UPLOADCANCELTAG;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Canstant.UPLOADFAILTURETAG;
		}
		return Canstant.UPLOADFAILTURETAG;
	}
	
}

package com.dayang.device;

import android.app.Activity;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import com.dayang.common.Canstant;
import com.dayang.upload.HttpUpload;
import com.dayang.upload.UploadFileThread;

import java.io.File;
import java.io.IOException;

/**
 * 用于实现音频录制并上传
 * 
 * @author renyuwei
 * 
 */
public class AudioDevice extends Device {
	private MediaRecorder mRecorder = null;
	private File fileAudio;

	public AudioDevice(Context context, Activity activity, Handler handler) {
		super(context, activity, handler);
	}

	/**
	 * 录制音频
	 */
	public boolean recordAudio() {
		mRecorder = new MediaRecorder();
		fileAudio = getFileByDate("audios", "mp4");
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mRecorder.setOutputFile(fileAudio.getAbsolutePath());
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			mRecorder.prepare();
			mRecorder.start();
		} catch (IOException e) {
			Log.e("debug", "record audio failed");
			return false;
		}
		return true;
	}

	/**
	 * 停止音频录制并上传
	 * 
	 * @param url
	 * @param uploadType
	 * @return
	 */
	public File stopRecAudAndUpload(String url, String uploadType) {
		try {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
			if (uploadType.equals(Canstant.FTPUPLOAD)) {
				Log.e("debug", "ftp上传");
				Log.e("debug", "音频路径是：" + this.fileAudio.getAbsolutePath());
//				UploadFileThread ftpUpload = new FtpUpload(url,"", this.handler,
//						Canstant.UPLOADSINGLE,
//						this.fileAudio.getAbsolutePath(), null,"",activity,"");
//				ftpUpload.start();
			} else if (uploadType.equals(Canstant.HTTPUPLOAD)) {
				UploadFileThread httpUpload = new HttpUpload(url,"", this.handler,
						Canstant.UPLOADSINGLE,
						this.fileAudio.getAbsolutePath(),null, "",activity,"");
				httpUpload.start();
			}
			return fileAudio;
		} catch (Exception e) {
			Log.e("debug", e.toString());
			return null;
		}
	}

}

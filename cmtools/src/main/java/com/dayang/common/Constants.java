package com.dayang.common;

import android.os.Environment;

public class Constants {
public static Object lockObject = new Object(); 

    public static final String GaoDe_GEOCODE_KEY = "2a41783a2724089436490307b02ed4ad";
    public static final String GEOCODE_REGEORESTURL = "http://restapi.amap.com/v3/geocode/regeo";
    public static final String APP_CACAHE_DIRNAME = "cachedir";
	public static final int CAMERA_REQUEST = 1;
	public static final int CAMERA_VIDEO_REQUEST = 2;
	public static final int FILE_MANAGE_REQUEST = 3;
	public static final int LOCAL_IMAGE_REQUEST = 4;
	public static final int RECORD_AUDIO_REQUEST = 5;
	public static final int FILE_SELECT_REQUEST = 6;
	public static final int FILE_DETAIL_REQUEST = 7;
	public static final int FILE_MOREDETAIL_REQUEST = 8;
	public static final int FILE_SELECT_RESULT = 9;
	public static final int FILE_SELECTDETAIL_RESULT = 10;
	public static final int UPLOADSUCCESS = 1;
	public static final int UPLOADFAILTURE = 0;
	public static String HTTPUPLOAD = "0";
	public static final int UPLOADSUCCESSSTATUS = 1;
	public static final int UPLOADMUTIPLE = 1;
	public static String FTPUPLOAD = "1";
	public static final int  UPLOADFAILTURESTATUS= 0;
	public static final int UPLOADSINGLE = 0;
	
	
 public static final String FILE_IMAGE_TYPE = "0";
 public static final String FILE_VIDEO_TYPE = "1";
 public static final String FILE_AUDIO_TYPE = "2";
 
 public static final String TAKE_PHOTO = "takePhoto";
 public static final String RECORD_VIDEO = "recordVideo";
 public static final String RECORD_AUDIO = "recordAudio";
 
	public static int IMAGEWIDTH = 240;
	public static int IMAGEHEIGHT = 230;


	/**
	 * ��Ŀ¼
	 */
	public static String fish_saying_root = "/audios";
	
	/**
	 * ��ȡ��Ƶ����Ŀ¼
	 */
	public static String AUDIO_DIRECTORY = Environment.getExternalStorageDirectory() +
			fish_saying_root + "/audio_record";
	
	/**
	 * ¼����Ƶ��ʼ��׺
	 */
	public static final String PCM_SUFFIX = ".pcm";
	/**
	 * 用来请求最新版本版本号的url
	 */

	public static final String VERSION_URL = "http://100.0.1.248:8080/H5Demo/servlet/Version";


	/**
	 * ת����aac��Ƶ��׺
	 */
	public static final String AAC_SUFFIX = ".aac";
	
	/**
	 * ת����m4a��Ƶ��׺
	 */
	public static final String M4A_SUFFIX = ".m4a";
	

	public static final String MAINURLKEYWORD = "revelation";
	
	
	public static final int THUMBNAILWIDTH = 200;
	public static final int THUMBNAILHEIGHT = 200;
	public static final int CROP_IMAGE_REQUEST = 231;
	public static final int CROP_CAMERIMAGE_REQUEST = 232;
	public static final int CROP_LOCALIMGES_REQUEST = 233;
	
	//��׼�ӿڵ�����Code
	public static final int CAMERA_STANDARDREQUEST = 31;
	public static final int CAMERA_STANDARDVIDEO_REQUEST = 32;
	public static final int RECORD_STANDARDAUDIO_REQUEST = 33;
	public static final int STANDARD_FILE_SELECT_REQUEST = 34;
	
	public static final int VIDEOEDIT_REQUEST = 35;
}

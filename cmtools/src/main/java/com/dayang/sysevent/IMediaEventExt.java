package com.dayang.sysevent;

import io.dcloud.common.DHInterface.IApp;
import io.dcloud.common.DHInterface.ISysEventListener;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.util.JSUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.dayang.activity.MediaPlugin;
import com.dayang.common.Canstant;
import com.dayang.common.Constants;
import com.dayang.common.CustomBitmapFactory;
import com.dayang.common.JSONFactory;
import com.dayang.common.MediaFile;
import com.dayang.vo.Duration;
import com.dayang.vo.FileReturn;
import com.dayang.vo.Fileinfos;
import com.dayang.vo.ProfileImage;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class IMediaEventExt implements ISysEventListener {
    private static final String TAG = Canstant.TAG;
    private Activity activity;
    private Handler handler;
    private IWebview pWebview;
    private String CallBackID;
    public static String returnresult = "";
    public static IMediaEventExt instance;

    private IMediaEventExt(IApp iApp, IWebview pwebview, String callbackId) {
        this.activity = pwebview.getActivity();
        this.pWebview = pwebview;
        this.CallBackID = callbackId;
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.i(TAG, "handleMessage: ");
                JsonObject returnJsonObject = new JsonObject();
                if (msg.what == Constants.UPLOADSUCCESS) {
                    Toast.makeText(activity, "上传成功", 8000).show();
                    returnJsonObject.addProperty("success", "true");
                    returnJsonObject.addProperty("description", "上传成功!");
                } else if (msg.what == Constants.UPLOADFAILTURE) {
                    Toast.makeText(activity, "上传失败", 8000).show();
                    returnJsonObject.addProperty("success", "false");
                    returnJsonObject.addProperty("description", "上传失败!");
                }
                returnresult = returnJsonObject.toString();
                if (MediaPlugin.asyncFlag != true
                        && MediaPlugin.progressDialog != null) {
                    MediaPlugin.progressDialog.dismiss();
                }
                JSONObject json = null;
                try {
                    json = new JSONObject(returnresult);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSUtil.execCallback(pWebview, CallBackID, json,
                        JSUtil.OK, false);
            }
        };
        MediaPlugin.handler = handler;
    }

    public static IMediaEventExt getInstance(IApp iApp, IWebview pwebview, String callbackId) {
        if (instance == null) {
            instance = new IMediaEventExt(iApp, pwebview, callbackId);
        } else {
            instance.CallBackID = callbackId;
            instance.activity = pwebview.getActivity();
            instance.pWebview = pwebview;
        }
        return instance;
    }

    @Override
    public boolean onExecute(SysEventType pEventType, Object pArgs) {
        Object[] _args = (Object[]) pArgs;
        int requestCode = (Integer) _args[0];
        int resultCode = (Integer) _args[1];
        Intent data = (Intent) _args[2];
        if (pEventType == SysEventType.onActivityResult) {
            onActivityResultCall(requestCode, resultCode, data);
        }
        return false;
    }

    /**
     * 接收intent传回的信息并进行业务逻辑的操作和处理
     */
    public void onActivityResultCall(int requestCode, int resultCode,
                                     Intent data) {
        switch (requestCode) {
            case Constants.CAMERA_REQUEST:
                Log.e("debug", "摄像结果回调");
                if (resultCode == 0 && data == null)
                    return; // 取消
                cameraFileResult(data);
                break;
            case Constants.CAMERA_VIDEO_REQUEST:
                Log.e("debug", "录像结果回调");
                if (resultCode == 0 && data == null)
                    return; // 取消
                cameraFileResult(data);
                break;
            case Constants.RECORD_AUDIO_REQUEST:
                Log.e("debug", "录音结果回调");
                cameraFileResult(data);
                break;
            case Constants.FILE_SELECT_REQUEST:
                Log.e("debug", "文件选择结果回调");
                selectFilesResult(data);
                break;

            case Constants.CAMERA_STANDARDREQUEST:
                Log.e("debug", "标准摄像结果回调");
                standardCameraFileResult(data);
                break;
            case Constants.CAMERA_STANDARDVIDEO_REQUEST:
                Log.e("debug", "标准录像结果回调");
                standardCameraFileResult(data);
                break;
            case Constants.RECORD_STANDARDAUDIO_REQUEST:
                Log.e("debug", "标准录音结果回调");
                standardCameraFileResult(data);
                break;
            case Constants.STANDARD_FILE_SELECT_REQUEST:
                Log.e("debug", "标准文件选择结果回调");
                standardSelectFilesResult(data);
                break;

            case Constants.CROP_CAMERIMAGE_REQUEST:
                Log.e("debug", "带有裁剪功能的拍照");
                cameraCropFileResult(data);
                break;
            case Constants.CROP_LOCALIMGES_REQUEST:
                Log.e("debug", "带有裁剪功能的选择图片");
                localImageCropFileResult(data);
                break;
            case Constants.CROP_IMAGE_REQUEST:
                Log.e("debug", "裁剪图像结果回调");
                cameraCropReturnResult(data);
                break;
            case Constants.VIDEOEDIT_REQUEST:
                Log.e("debug", "视频编辑回调");
                videoPreviewEditResult(data);
                break;
        }
    }

    /**
     * 拍照，录像后的回调函数
     */
    public void cameraFileResult(Intent data) {
        File file = MediaPlugin.cameraFile;
        if ((file == null || file.length() <= 10 || file.getName().equals("")) && (MediaPlugin.fileType != Constants.FILE_AUDIO_TYPE)) {
            return;
        }
        // checkExifinterface(file);
        List<Fileinfos> fileinfoList = null;
        FileReturn fileReturn = new FileReturn();
        fileReturn.setSuccess("false");
        fileReturn.setDescription("返回失败");
        String base64 = "";
        try {
            Fileinfos fileinfos = new Fileinfos();
            if (MediaPlugin.fileType.equals(Constants.FILE_VIDEO_TYPE)) {
                Bitmap firstPicture = getVideoThumbnail(file.getAbsolutePath(),
                        200, 200, MediaStore.Images.Thumbnails.MICRO_KIND);
                base64 = Base64Bitmap(firstPicture);
                String[] returnoObjects = MediaFile.getPlayTime(file
                        .getAbsolutePath());
                fileinfos.setDuration(new Duration((String) returnoObjects[0],
                        (String) returnoObjects[1]));
            } else if (MediaPlugin.fileType.equals(Constants.FILE_IMAGE_TYPE)) {
                // if (android.os.Build.MODEL.contains("SM")) {
                // //三星手机拍照后的图片为倒旋转90度，需要顺时针旋转90度纠正
                // file = ImageUtil.rotaingImageView(90, file);
                // }
                checkExifinterface(file);
                base64 = imageToBase64thumbnail(file, 200, 200);
            } else {
                String filePath = (String) data.getExtras()
                        .get("audiofilePath");
                if (filePath != null && !filePath.equals("")) {
                    file = new File(filePath);
                } else {
                    throw new Exception("没有录音可用");
                }
                base64 = null;
            }
            fileReturn.setDescription("返回成功");
            fileReturn.setSuccess("true");
            fileinfoList = new ArrayList<Fileinfos>();
            fileinfos.setFileType(MediaPlugin.fileType);
            fileinfos.setLocalPath(file.getAbsolutePath());
            fileinfos.setName(file.getName());
            fileinfos.setFileSize(file.length());
            fileinfos.setThumbnail(base64);
            fileinfoList.add(fileinfos);
            fileReturn.setFileInfos(fileinfoList);
        } catch (Exception e) {
            fileReturn.setDescription(e.toString());
        }
        String returnStr = JSONFactory.objectToJsonStr(fileReturn);
        Log.i(TAG, "cameraFileResult: " + returnStr);
        JSONObject json = null;
        try {
            json = new JSONObject(returnStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSUtil.execCallback(pWebview, CallBackID, json, JSUtil.OK, false);

    }

    /**
     * 选择媒体文件后的结果回调函数
     */
    public void selectFilesResult(Intent dataPara) {
        String returnStr = null;
        FileReturn fileReturn = new FileReturn();
        try {
            Intent data = dataPara;
            if (data == null || data.getExtras() == null) {
                throw new Exception("返回错误！");
            }
            Bundle bundle = data.getExtras();
            ArrayList<String> listfile = null;
            if (bundle != null) {
                if (bundle.getStringArrayList("files") != null) {
                    String base64code = null;
                    listfile = bundle.getStringArrayList("files");
                    fileReturn.setDescription("返回成功");
                    fileReturn.setSuccess("true");
                    List<Fileinfos> fileinfoList = null;
                    if (listfile != null && listfile.size() > 0) {
                        fileinfoList = new ArrayList<Fileinfos>();
                        for (String fileStr : listfile) {
                            File file = new File(fileStr);
                            Fileinfos fileinfos = new Fileinfos();
                            if (MediaFile.isImageFileType(file
                                    .getAbsolutePath())) {
                                fileinfos
                                        .setFileType(Constants.FILE_IMAGE_TYPE);
                                base64code = imageToBase64thumbnail(file, 200,
                                        200);
                            } else if (MediaFile.isVideoFileType(file
                                    .getAbsolutePath())) {
                                fileinfos
                                        .setFileType(Constants.FILE_VIDEO_TYPE);
                                Bitmap firstPicture = getVideoThumbnail(
                                        file.getAbsolutePath(), 200, 200,
                                        MediaStore.Images.Thumbnails.MICRO_KIND);
                                base64code = Base64Bitmap(firstPicture);
                                String[] returnoObjects = MediaFile
                                        .getPlayTime(file.getAbsolutePath());
                                fileinfos.setDuration(new Duration(
                                        (String) returnoObjects[0],
                                        (String) returnoObjects[1]));
                            } else {
                                fileinfos
                                        .setFileType(Constants.FILE_AUDIO_TYPE);
                            }

                            fileinfos.setLocalPath(file.getAbsolutePath());
                            fileinfos.setName(file.getName());
                            fileinfos.setFileSize(file.length());
                            fileinfos.setThumbnail(base64code);
                            fileinfoList.add(fileinfos);
                        }
                    }
                    fileReturn.setFileInfos(fileinfoList);
                    returnStr = JSONFactory.objectToJsonStr(fileReturn);
                    JSONObject json = null;
                    try {
                        json = new JSONObject(returnStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "selectFilesResult: " + this);
                    Log.i(TAG, "selectFilesResult: " + CallBackID);
                    JSUtil.execCallback(pWebview, CallBackID, json,
                            JSUtil.OK, false);
                }
            }
        } catch (Exception e) {
            fileReturn.setSuccess("false");
            fileReturn.setDescription(e.toString());
            returnStr = JSONFactory.objectToJsonStr(fileReturn);
            JSONObject json = null;
            try {
                json = new JSONObject(returnStr);
            } catch (JSONException e1) {
                e.printStackTrace();
            }
            JSUtil.execCallback(pWebview, CallBackID, json, JSUtil.OK,
                    false);
        }

    }

    /**
     * 拍照后并裁剪的回调函数
     *
     * @param data
     */
    public void cameraCropFileResult(Intent data) {
        File file = MediaPlugin.cameraFile;
        try {
            Uri uripath = Uri.fromFile(file);
            File cropfile = this.getFileByDate("images", "jpg");
            MediaPlugin.cropFile = cropfile;
            cropImageUri(uripath, 200, 200, Uri.fromFile(MediaPlugin.cropFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从图库选择图片并裁剪的回调函数
     *
     * @param data
     */
    public void localImageCropFileResult(Intent data) {
        try {
            if (data == null) {
                return;
            }
            Uri uri = data.getData();
            String[] proj = {MediaStore.Images.Media.DATA};
            @SuppressWarnings("deprecation")
            Cursor cursor = activity.managedQuery(uri, proj, // Which
                    null, // WHERE clause; which rows to return (all rows)
                    null, // WHERE clause selection arguments (none)
                    null); // Order-by clause (ascending by name)
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            File cropfile = this.getFileByDate("images", "jpg");
            MediaPlugin.cropFile = cropfile;
            cropImageUri(Uri.fromFile(new File(path)), 200, 200,
                    Uri.fromFile(MediaPlugin.cropFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 裁剪图片后的回调函数
     *
     * @param data
     */
    public void cameraCropReturnResult(Intent data) {
        File file = MediaPlugin.cropFile;
        ProfileImage fileReturn = new ProfileImage();
        fileReturn.setSuccess("false");
        fileReturn.setDescription("返回失败");
        try {
            String bigimage = imageToBase64(file);
            String smallimage = imageToBase64thumbnail(file, 100, 100);
            fileReturn.setProfileImage(bigimage);
            fileReturn.setThumbnail(smallimage);
            fileReturn.setDescription("返回成功");
            fileReturn.setSuccess("true");
        } catch (Exception e) {
            fileReturn.setDescription(e.toString());
        }
        String returnStr = JSONFactory.objectToJsonStr(fileReturn);
        JSONObject json = null;
        try {
            json = new JSONObject(returnStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSUtil.execCallback(pWebview, CallBackID, json, JSUtil.OK, false);
    }

    private void cropImageUri(Uri uri, int outputX, int outputY, Uri saveUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectY", 1);
        intent.putExtra("aspectX", 1); // 宽高比例1:1
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, saveUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        activity.startActivityForResult(intent, Constants.CROP_IMAGE_REQUEST);
    }

    /**
     * 标准接口的拍照，录像,录音后的回调函数
     */
    public void standardCameraFileResult(Intent data) {
        File file = MediaPlugin.cameraFile;
        // checkExifinterface(file);
        List<Fileinfos> fileinfoList = null;
        FileReturn fileReturn = new FileReturn();
        fileReturn.setSuccess("false");
        fileReturn.setDescription("返回失败");
        try {
            Fileinfos fileinfos = new Fileinfos();
            if (MediaPlugin.fileType.equals(Constants.FILE_AUDIO_TYPE)) {
                String filePath = (String) data.getExtras()
                        .get("audiofilePath");
                if (filePath != null && !filePath.equals("")) {
                    file = new File(filePath);
                } else {
                    throw new Exception("没有录音可用");
                }
            } else if (MediaPlugin.fileType.equals(Constants.FILE_VIDEO_TYPE)) {
                String[] returnoObjects = MediaFile.getPlayTime(file
                        .getAbsolutePath());
                fileinfos.setDuration(new Duration((String) returnoObjects[0],
                        (String) returnoObjects[1]));
            }
            fileReturn.setDescription("返回成功");
            fileReturn.setSuccess("true");
            fileinfoList = new ArrayList<Fileinfos>();
            fileinfos.setFileType(MediaPlugin.fileType);
            fileinfos.setLocalPath(file.getAbsolutePath());
            fileinfos.setName(file.getName());
            fileinfos.setFileSize(file.length());
            fileinfoList.add(fileinfos);
            fileReturn.setFileInfos(fileinfoList);
        } catch (Exception e) {
            fileReturn.setDescription(e.toString());
        }
        String returnStr = JSONFactory.objectToJsonStr(fileReturn);
        JSONObject json = null;
        try {
            json = new JSONObject(returnStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSUtil.execCallback(pWebview, CallBackID, json, JSUtil.OK, false);
    }

    /**
     * 标准接口的选择媒体文件后的结果回调函数
     */
    public void standardSelectFilesResult(Intent dataPara) {
        String returnStr = null;
        FileReturn fileReturn = new FileReturn();
        try {
            Intent data = dataPara;
            if (data == null || data.getExtras() == null) {
                throw new Exception("返回错误！");
            }
            Bundle bundle = data.getExtras();
            ArrayList<String> listfile = null;
            if (bundle != null) {
                if (bundle.getStringArrayList("files") != null) {
                    listfile = bundle.getStringArrayList("files");
                    fileReturn.setDescription("返回成功");
                    fileReturn.setSuccess("true");
                    List<Fileinfos> fileinfoList = null;
                    if (listfile != null && listfile.size() > 0) {
                        fileinfoList = new ArrayList<Fileinfos>();
                        for (String fileStr : listfile) {
                            File file = new File(fileStr);
                            Fileinfos fileinfos = new Fileinfos();
                            if (MediaFile.isImageFileType(file
                                    .getAbsolutePath())) {
                                fileinfos
                                        .setFileType(Constants.FILE_IMAGE_TYPE);
                            } else if (MediaFile.isVideoFileType(file
                                    .getAbsolutePath())) {
                                fileinfos
                                        .setFileType(Constants.FILE_VIDEO_TYPE);
                                String[] returnoObjects = MediaFile
                                        .getPlayTime(file.getAbsolutePath());
                                fileinfos.setDuration(new Duration(
                                        (String) returnoObjects[0],
                                        (String) returnoObjects[1]));
                            } else {
                                fileinfos
                                        .setFileType(Constants.FILE_AUDIO_TYPE);
                            }

                            fileinfos.setLocalPath(file.getAbsolutePath());
                            fileinfos.setName(file.getName());
                            fileinfos.setFileSize(file.length());
                            fileinfoList.add(fileinfos);
                        }
                    }

                    fileReturn.setFileInfos(fileinfoList);
                    returnStr = JSONFactory.objectToJsonStr(fileReturn);
                    JSONObject json = null;
                    try {
                        json = new JSONObject(returnStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSUtil.execCallback(pWebview, CallBackID, json,
                            JSUtil.OK, false);
                }
            }
        } catch (Exception e) {
            fileReturn.setSuccess("false");
            fileReturn.setDescription(e.toString());
            returnStr = JSONFactory.objectToJsonStr(fileReturn);
            JSONObject json = null;
            try {
                json = new JSONObject(returnStr);
            } catch (JSONException e1) {
                e.printStackTrace();
            }
            JSUtil.execCallback(pWebview, CallBackID, json, JSUtil.OK,
                    false);
        }

    }

    /**
     * 保存编辑视频结果后回调方法
     *
     * @param data
     */
    public void videoPreviewEditResult(Intent data) {
        Log.i(TAG, "videoPreviewEditResult: ");
        FileReturn fileReturn = new FileReturn();
        fileReturn.setSuccess("false");
        String filePath = "";
        String status = "";
        String indexNO = "";
        if (data != null) {
            filePath = data.getExtras().getString("filePath");
            indexNO = data.getExtras().getString("indexNO");
            status = data.getExtras().getString("status");
        }
        try {
            if (status.equals("1")) {
                fileReturn.setDescription("返回成功");
            } else if (status.equals("0")) {
                throw new Exception("返回失败!");
            } else if (status.equals("2")) {
                fileReturn.setDescription("取消截取视频");
                fileReturn.setSuccess("true");
                String returnStr1 = JSONFactory.objectToJsonStr(fileReturn);
                JSONObject json = null;
                try {
                    json = new JSONObject(returnStr1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSUtil.execCallback(pWebview, CallBackID, json,
                        JSUtil.OK, false);
                return;
            }
            File clipVideoFile = new File(filePath);
            if (clipVideoFile.exists()) {
                Fileinfos fileinfos = new Fileinfos();
                fileinfos.setIndexNO(indexNO);
                fileinfos.setName(clipVideoFile.getName());
                fileinfos.setFileType(Constants.FILE_VIDEO_TYPE);
                fileinfos.setLocalPath(clipVideoFile.getAbsolutePath());
                fileinfos.setFileSize(clipVideoFile.length());
                Bitmap firstPicture = getVideoThumbnail(
                        clipVideoFile.getAbsolutePath(),
                        Constants.THUMBNAILWIDTH, Constants.THUMBNAILHEIGHT,
                        MediaStore.Images.Thumbnails.MICRO_KIND);
                String base64code = Base64Bitmap(firstPicture);
                fileinfos.setThumbnail(base64code);
                String[] returnoObjects = MediaFile.getPlayTime(clipVideoFile
                        .getAbsolutePath());
                fileinfos.setDuration(new Duration((String) returnoObjects[0],
                        (String) returnoObjects[1]));
                fileReturn.setFileInfo(fileinfos);
                fileReturn.setSuccess("true");
            } else {
                throw new Exception("截取文件不存在！");
            }
        } catch (Exception e) {
            fileReturn.setDescription(e.toString());
            fileReturn.setSuccess("false");
        }
        String returnStr = JSONFactory.objectToJsonStr(fileReturn);
        JSONObject json = null;
        try {
            json = new JSONObject(returnStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSUtil.execCallback(pWebview, CallBackID, json, JSUtil.OK, false);
    }

    /**
     * 根据图像文件转换成图像的Base64
     *
     * @param imgfile
     * @return
     */
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

    /**
     * 把Bitmap的图像缩放成指定宽度和高度的图像
     *
     * @param source
     * @param width
     * @param height
     * @return
     */
    public Bitmap scaleBitMap(Bitmap source, int width, int height) {
        Bitmap target = null;
        try {
            target = Bitmap.createBitmap(width, height, source.getConfig());
            Canvas canvas = new Canvas(target);
            canvas.drawBitmap(source, null, new Rect(0, 0, target.getWidth(),
                    target.getHeight()), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return target;

    }

    /**
     * 把图像缩放成指定大小，然后转换为Base64串返回
     *
     * @param imgfile
     * @param width
     * @param height
     * @return
     */
    public String imageToBase64thumbnail(File imgfile, int width, int height) {
        String base64str = null;
        try {
            Bitmap bmp = CustomBitmapFactory.decodeBitmap(imgfile
                    .getAbsolutePath());
            Bitmap thumbnail = scaleBitMap(bmp, width, height);
            base64str = Base64Bitmap(thumbnail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base64str;
    }

    /**
     * 根据输入的文件夹和文件后缀名生成文件
     *
     * @param fileDir
     * @param fileExtType
     * @return
     */
    @SuppressLint("SimpleDateFormat")
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

    /**
     * 根据视频路径抽取该视频的首帧图片
     *
     * @param videoPath
     * @param width
     * @param height
     * @param kind
     * @return
     */
    private Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                     int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 把bitmap的图片转换成Base64串
     *
     * @param bitmap
     * @return
     */
    public String Base64Bitmap(Bitmap bitmap) {
        byte[] bytes = null;
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

    /**
     * 对照片的旋转状态进行检查如果照片是旋转的进行旋转操作 用来确保照片不会旋转
     */
    private void checkExifinterface(File file) {
        try {
            ExifInterface exifInterface = new ExifInterface(file.getPath());
            int result = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            int rotate = 0;
            switch (result) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                default:
                    break;
            }
            if (rotate == 0) {

                return;
            }
            Matrix matrix = new Matrix();

            matrix.reset();
            matrix.postRotate(rotate);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

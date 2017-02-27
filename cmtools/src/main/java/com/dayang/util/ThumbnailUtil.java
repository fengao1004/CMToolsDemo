package com.dayang.util;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.widget.ImageView;
import com.dayang.cmtools.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by 冯傲 on 2016/10/11.
 * e-mail 897840134@qq.com
 */
public class ThumbnailUtil {
    public Bitmap getThumbnail(String path){
        return null;
    }
    public static Bitmap  getThumbnailByVidio(String path){
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(path, 1);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 100,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
        //return null;
    }
    /** 将文件路径的缩略图在imageview里边显示出来
     *  @param parameter
     *  @return
     */
    public static void displayThumbnail(final ImageView iamgeView,String path){
        int fileType = TypeUtils.getFileType(path);
        ImageLoader imageLoader = ImageLoader.getInstance();
        switch (fileType){
            case TypeUtils.ADIOU:
                iamgeView.setImageResource(R.drawable.music_o);
                break;
            case TypeUtils.IMAGE:
                DisplayImageOptions options = new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.EXACTLY).build();
                imageLoader.displayImage("file://"+path,iamgeView,options);
                break;
            case TypeUtils.VIDIO:
                new AsyncTask<String,Void,Bitmap>(){
                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        iamgeView.setImageBitmap(bitmap);
                    }
                    @Override
                    protected Bitmap doInBackground(String... params) {
                        Bitmap bitmap = null;
                        bitmap = ThumbnailUtils.createVideoThumbnail(params[0], 1);
                        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 100,
                                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                        return bitmap;
                    }
                }.execute(path);

                break;
            case 0:
                iamgeView.setImageResource(R.drawable.error);
                break;
        }

    }

}

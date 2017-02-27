package com.dayang.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static com.dayang.common.Canstant.TAG;

/**
 * Created by 冯傲 on 2016/11/2.
 * e-mail 897840134@qq.com
 */

public class AssetsUtil {
//    private Context context;
//
//    public AssetsUtil(Context context) {
//        this.context = context;
//    }
//
//    /**
//     * @param fileName   需要从assets中解压出来的文件名称
//     * @param targetPath 解压目标路径
//     * @return null
//     */
//    public void getFileFromAssets(final String fileName,final String targetPath,final GetFileListener getFileListener) {
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                try {
//                    Log.i(TAG, "run: "+new Date().getTime());
//                    AssetManager assets = context.getAssets();
//                    InputStream open = assets.open(fileName);
//                    Log.i(TAG, "run: "+targetPath);
//                    File file = new File(targetPath + "/SXP.zip");
//                    if(!file.exists()){
//                        file.createNewFile();
//                    }
//                    FileOutputStream fos = new FileOutputStream(file);
//                    byte[] buffer = new byte[1024];
//                    int byteCount = 0;
//                    while ((byteCount = open.read(buffer)) != -1) {//循环从输入流读取 buffer字节
//                        fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
//                    }
//                    fos.flush();//刷新缓冲区
//                    open.close();
//                    fos.close();
//                    UnZip unzipFile = new UnZip();
//                    boolean unzip = unzipFile.unzip(targetPath + "/SXP.zip", targetPath);
//                    Log.i(TAG, "run: "+new Date().getTime());
//                    if(unzip){
//                        File file1 = new File(targetPath + "/SXP.zip");
//                        file1.delete();
//                        getFileListener.getFileComplete();
//                    }else {
//                        getFileListener.getFileError();
//                    }
//                } catch (IOException e) {
//                    Log.i(TAG, "run: "+e.toString());
//                    getFileListener.getFileError();
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//    }
//
//  public  interface GetFileListener {
//        void getFileComplete();
//
//        void getFileError();
//    }
}

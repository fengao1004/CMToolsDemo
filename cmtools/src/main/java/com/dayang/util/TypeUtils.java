package com.dayang.util;

/**
 * Created by 冯傲 on 2016/10/11.
 * e-mail 897840134@qq.com
 */
public class TypeUtils {
    public static final int ADIOU = 1;
    public static final int IMAGE = 2;
    public static final int VIDIO = 3;

    public static int getFileType(String path) {
        boolean isVideo = MediaFile.isVideoFileType(path);
        boolean isImage = MediaFile.isImageFileType(path);
        boolean isAudio = MediaFile.isAudioFileType(path);
        if(isVideo){
            return VIDIO;
        }
        if(isImage){
            return IMAGE;
        }
        if(isAudio){
            return ADIOU;
        }
        return 0;
    }

}

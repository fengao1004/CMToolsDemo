package com.dayang.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint({ "SimpleDateFormat", "SdCardPath", "DefaultLocale" })
public class CommonUtil {

	public static String getStringDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}
	

    @SuppressWarnings("resource")
	public static String stringForTime(int timeMs) {
    	StringBuilder mFormatBuilder= new StringBuilder();
    	Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d:00", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("00:%02d:%02d:00", minutes, seconds).toString();
        }
    }


	public static String encode(String str, String charset)
			throws UnsupportedEncodingException {
		String zhPattern = "[\\u4e00-\\u9fa5]+";
		str = str.replaceAll(" ", " ");// 对空字符串进行处�?
		Pattern p = Pattern.compile(zhPattern);
		Matcher m = p.matcher(str);
		StringBuffer b = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(b, URLEncoder.encode(m.group(0), charset));
		}
		m.appendTail(b);
		return b.toString();
	}


	public static boolean isScreenOriatationPortrait(Context context) {
		return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
	}
	


        public static boolean isFormatFile(String fName,String format) {
                boolean re;
                String end = fName
                                .substring(fName.lastIndexOf(".") + 1, fName.length())
                                .toLowerCase();


                if (end.equals(format)) {
                        re = true;
                } else {
                        re = false;
                }
                return re;
        }
        

        public static int px2dip(Context context, float pxValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }
        
        

        @SuppressWarnings({ "deprecation", "unused" })
		public static boolean isPad(Context context) {
         WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
         Display display = wm.getDefaultDisplay();
         // 屏幕宽度
         float screenWidth = display.getWidth();
         // 屏幕高度
         float screenHeight = display.getHeight();
         DisplayMetrics dm = new DisplayMetrics();
         display.getMetrics(dm);
         double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
         double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
         // 屏幕尺寸
         double screenInches = Math.sqrt(x + y);
         // 大于6尺寸则为Pad
         System.out.println("手机尺寸========"+screenInches);
         if (screenInches >= 5.0) {
          return true;
         }
         return false;
        }



}

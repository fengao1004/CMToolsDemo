package com.dayang.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;

public class CustomBitmapFactory {
	public static Bitmap decodeBitmap(String path) {
		FileInputStream fs=null; 
		Bitmap bmp = null; 
			BitmapFactory.Options bfOptions=new BitmapFactory.Options(); 
			bfOptions.inDither=false; 
			bfOptions.inPurgeable=true; 
			bfOptions.inTempStorage=new byte[32 * 1024];  
			bfOptions.inInputShareable=true;
			File file = new File(path); 
			try { 
			    fs = new FileInputStream(file); 
			    if(fs != null) 
			        bmp = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions); 
			     } catch (Exception e) { 
			    	 Log.e("bitmapdecodeeror", e.toString());
			}
		return bmp;
	}
}

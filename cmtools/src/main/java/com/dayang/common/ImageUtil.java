package com.dayang.common;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ImageUtil {
	public static Bitmap scaleBitMap(Bitmap source,int width,int height) {
		Bitmap target = null;
		try {
			target = Bitmap.createBitmap(width, height, source.getConfig());
			Canvas canvas = new Canvas(target);
			canvas.drawBitmap(source, null, new Rect(0, 0, target.getWidth(), target.getHeight()), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return target;
		
	}
	/**
     * 旋转图片
     * @author renyuwei
     * @time 20160505
     * @param angle
     * @param file
     * @return
     */
	public static File rotaingImageView(int angle, File file) {
		File result = null;
		Bitmap bitmap = CustomBitmapFactory.decodeBitmap(file.getAbsolutePath());
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		// 创建新的图片
		CompressFormat format = Bitmap.CompressFormat.JPEG;
		int quality = 100;
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		resizedBitmap.compress(format, quality, stream);
		result = new File(file.getAbsolutePath());
		return result;
	}
}

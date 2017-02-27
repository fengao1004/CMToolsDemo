package com.dayang.common;

import android.util.Log;

import java.util.UUID;
/**
 * 自动生成数据库记录的主键，全球唯一
 * @author 任育伟
 *
 */
public class Util {
	public static String getGenerateGUID() {
		UUID uuid = null;
		try {
			Log.e("debug", "生成主键");
			uuid = UUID.randomUUID();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uuid.toString();
	}
}

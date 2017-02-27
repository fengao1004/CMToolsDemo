package com.dayang.inter.impl;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dayang.common.JSONFactory;
import com.dayang.inter.KeyValueData;
import com.dayang.vo.KVReturnValue;
import com.google.gson.JsonObject;

public class KeyValueDataImpl implements KeyValueData{

	private Context context;
	private Activity activity;
	private SharedPreferences sharedPreferences = null;
	public KeyValueDataImpl(Context context,Activity activity) {
		this.context = context;
		this.activity = activity;
		sharedPreferences = this.activity.getSharedPreferences(
				"configuration", 0);
	}
	@Override
	public String saveKVData(String parameter) {
		// TODO Auto-generated method stub
		JsonObject  jsonObject = JSONFactory.parseJsonStr(parameter);
		KVReturnValue kvReturnValue = new KVReturnValue();
		try {
			if (jsonObject != null) {
				String key = jsonObject.get("key").getAsString();
				String value = jsonObject.get("value").getAsString();
				Editor editor = sharedPreferences.edit();
				editor.putString(key, value);
				editor.commit();
				kvReturnValue.setSuccess("true");
				kvReturnValue.setDescription("保存成功");
				kvReturnValue.setContent("");
			}
		} catch (Exception e) {
			e.printStackTrace();
			kvReturnValue.setDescription("保存失败！" + e.toString());
		}
		return JSONFactory.objectToJsonStr(kvReturnValue);
	}

	@Override
	public String getKVData(String parameter) {
		// TODO Auto-generated method stub
		JsonObject  jsonObject = JSONFactory.parseJsonStr(parameter);
		KVReturnValue kvReturnValue = new KVReturnValue();
		try {
			if (jsonObject != null) {
				String key = jsonObject.get("key").getAsString();
				String value = sharedPreferences.getString(key, "");
				if (value == null || value.equals("")) {
					kvReturnValue.setDescription("获取值失败！" + "为空或者不存在");
				} else {
					kvReturnValue.setSuccess("true");
					kvReturnValue.setDescription("得到保存的值");
					kvReturnValue.setContent(value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			kvReturnValue.setDescription("获取值失败！" + e.toString());
		}
		return JSONFactory.objectToJsonStr(kvReturnValue);
	}

	@Override
	public String delKVData(String parameter) {
		// TODO Auto-generated method stub
		JsonObject  jsonObject = JSONFactory.parseJsonStr(parameter);
		KVReturnValue kvReturnValue = new KVReturnValue();
		try {
			if (jsonObject != null) {
				String key = jsonObject.get("key").getAsString();
				Editor editor = sharedPreferences.edit();
				editor.remove(key);
				editor.commit();
				kvReturnValue.setSuccess("true");
				kvReturnValue.setDescription("删除成功");
				kvReturnValue.setContent("");
			}
		} catch (Exception e) {
			e.printStackTrace();
			kvReturnValue.setDescription("删除失败" + e.toString());
		}
		return JSONFactory.objectToJsonStr(kvReturnValue);
	}

}

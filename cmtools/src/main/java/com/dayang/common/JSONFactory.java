package com.dayang.common;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;




public class JSONFactory {
 public static JsonObject parseJsonStr(String jsonstr) {
	 JsonObject jsonObject = null;
	 try {
		 Gson gson = new Gson();
		 jsonObject = gson.fromJson(jsonstr, JsonObject.class);
		 return jsonObject;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return jsonObject;
 }
 
 public static JsonArray parseJsonStrToArr(String jsonstr) {
	 JsonArray jsonArray = null;
	 try {
		 Gson gson = new Gson();
		 jsonArray = gson.fromJson(jsonstr, JsonArray.class);
		 return jsonArray;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return jsonArray;
 }
 
 public static String parseJsonObject(String jsonObject) {
	 String jsonstr = null;
	 try {
		 jsonstr = jsonObject.toString();
		 return jsonstr;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return jsonstr;
 }
 
 public static String objectToJsonStr(Object object) {
	 String jsonstr = null;
	 Gson gson = null;
	 try {
		 gson = new Gson();
		 jsonstr = gson.toJson(object);
		 return jsonstr;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return jsonstr;
	 
 }
 
 public static <T> T JsonStrToobject(String jsonStr,Class<T> type) {
	 Gson gson = null;
	 try {
		 gson = new Gson();
		 T object  =  gson.fromJson(jsonStr, type); 
		 return object;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
	 
 }
}

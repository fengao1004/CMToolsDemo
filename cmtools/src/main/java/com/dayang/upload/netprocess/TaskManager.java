package com.dayang.upload.netprocess;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.util.Log;

import com.dayang.common.JSONFactory;
import com.dayang.inter.KeyValueData;
import com.dayang.inter.impl.KeyValueDataImpl;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TaskManager implements NetworkTaskDelegate {

	List<TaskData> mapTask;
	NetworkStatusMonitor monitorNS;

	boolean isFinished;

	private Activity activity;

	private KeyValueData keyValueData;

	public TaskManager(Activity activity) {
		this.activity = activity;
//		keyValueData = new KeyValueDataImpl(activity.getApplicationContext(),
//				activity);
	}

	@Override
	public boolean processTask() {
		// TODO Auto-generated method stub
		isFinished = false;
		try {
			if (mapTask == null) {
				mapTask = this.getMapTask();
			}
			List<TaskData> taskDatas = new ArrayList<TaskData>();
			for (int i = 0; i < mapTask.size(); i++) {
				JsonObject jsonObject = new JsonObject();
				TaskData taskData = mapTask.get(i);
				jsonObject.addProperty("taskId", taskData.getTaskId());
				jsonObject.addProperty("localPath", taskData.getLocalPath());
				jsonObject.addProperty("fileStatus", taskData.getFileStatus());
				boolean result = this.fileStatusNotifyCallBack(taskData.getFileStatusNotifyURL(),jsonObject.toString());
			    if (!result) {
			    	taskDatas.add(taskData);
			    }
			}
			if (taskDatas.size() == 0) {
				isFinished = true;
			}
			updateTaskList(taskDatas);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isTaskFinished() {
		if (monitorNS != null && isFinished) {
			monitorNS.stop();
			return true;
		} else {
			return false;
		}
	}
	
	public void startMonitor() {
		if (monitorNS == null) {
			monitorNS = new NetworkStatusMonitor(this, activity);
		}
		monitorNS.start();
	}

	public void addTask(TaskData taskData) {
		JsonArray jsonArray = null;
        JsonObject savejsonObject = null;
		if (keyValueData != null && taskData != null) {
			JsonObject keyjson = new JsonObject();
			keyjson.addProperty("key", "dataFailedTask");
			String data = keyValueData.getKVData(keyjson.toString());
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("fileStatusNotifyURL",
					taskData.getFileStatusNotifyURL());
			jsonObject.addProperty("taskId", taskData.getTaskId());
			jsonObject.addProperty("localPath", taskData.getLocalPath());
			jsonObject.addProperty("fileStatus", taskData.getFileStatus());

			JsonObject jsonObject2 = JSONFactory.parseJsonStr(data);
			if (jsonObject2.get("success").getAsString().equals("true")) {
					String valuejson = jsonObject2.get("content").getAsString();
					jsonArray = JSONFactory.parseJsonStrToArr(valuejson);
					jsonArray.add(jsonObject);
			} else {
				jsonArray = new JsonArray();
				jsonArray.add(jsonObject);
			}
			savejsonObject = new JsonObject();
			savejsonObject.addProperty("key", "dataFailedTask");
			savejsonObject.addProperty("value", jsonArray.toString());
			keyValueData.saveKVData(savejsonObject.toString());
		} else {
		}
	}

	public void updateTaskList(List<TaskData> taskDataList) {
		JsonArray jsonArray = null;
        JsonObject savejsonObject = null;
		if (keyValueData != null) {
			JsonObject deleteJsonObject = new JsonObject();
			deleteJsonObject.addProperty("key", "dataFailedTask");
			keyValueData.delKVData(deleteJsonObject.toString());
			jsonArray = new JsonArray();
			if (taskDataList != null) {
				for (int i = 0; i < taskDataList.size(); i++) {
					TaskData taskData = taskDataList.get(i);
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("fileStatusNotifyURL",
							taskData.getFileStatusNotifyURL());
					jsonObject.addProperty("taskId", taskData.getTaskId());
					jsonObject.addProperty("localPath", taskData.getLocalPath());
					jsonObject.addProperty("fileStatus", taskData.getFileStatus());
					jsonArray.add(jsonObject);
				}
			}
			savejsonObject = new JsonObject();
			savejsonObject.addProperty("key", "dataFailedTask");
			savejsonObject.addProperty("value", jsonArray.toString());
			keyValueData.saveKVData(savejsonObject.toString());
		}
	}
	
	public List<TaskData> getMapTask() {
		JsonArray jsonArray = null;
		List<TaskData> listdata = new ArrayList<TaskData>();
		if (keyValueData != null) {
			JsonObject keyjson = new JsonObject();
			keyjson.addProperty("key", "dataFailedTask");
			String data = keyValueData.getKVData(keyjson.toString());
			JsonObject jsonObject2 = JSONFactory.parseJsonStr(data);
			if (jsonObject2.get("success").getAsString().equals("true")) {
				String valueJson = jsonObject2.get("content").getAsString();
				jsonArray = JSONFactory.parseJsonStrToArr(valueJson);
				for (int i = 0; i < jsonArray.size(); i++) {
					TaskData taskData = new TaskData();
					JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
					taskData.setFileStatusNotifyURL(jsonObject.get("fileStatusNotifyURL").getAsString());
					taskData.setFileStatus(jsonObject.get("fileStatus").getAsString());
					taskData.setLocalPath(jsonObject.get("localPath").getAsString());
					taskData.setTaskId(jsonObject.get("taskId").getAsString());
					listdata.add(taskData);
				}
			}
		}
		return listdata;
	}


	public boolean fileStatusNotifyCallBack(String url,String requestParam) {
		boolean notifystatus = false;
		HttpClient httpclient = new DefaultHttpClient();
		try {
			BufferedReader br = null;
			HttpPost httppost = new HttpPost(url);
			StringEntity reqEntity = new StringEntity(requestParam, "utf-8");
			reqEntity.setContentEncoding("UTF-8");
			reqEntity.setContentType("application/String");
			httppost.setEntity(reqEntity);
			HttpResponse response = httpclient.execute(httppost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				StringBuffer bufferStr = new StringBuffer(); 
				InputStream inputStream = response.getEntity().getContent();
				br = new BufferedReader(new InputStreamReader(inputStream));
				String line;     
	            while((line=br.readLine()) != null){     
	            	bufferStr.append(line);
	            }     
				JsonObject responseJsonObject = JSONFactory.parseJsonStr(bufferStr.toString());
				String success = responseJsonObject.get("success").getAsString();
				String description = responseJsonObject.get("description").getAsString();
				if(!success.equals("true")) {
					throw new Exception("֪ͨ"+description);
				} else {
					notifystatus = true;
					Log.e("debug", "֪ͨ"+description);
				}
				
			}
		} catch (Exception e) {
			return notifystatus;
		} finally {
			try {
				httpclient.getConnectionManager().shutdown();
			} catch (Exception ignore) {
			}
		}
		return notifystatus;
	}
	
	public void setMapTask(List<TaskData> mapTask) {
		this.mapTask = mapTask;
	}

	public NetworkStatusMonitor getMonitorNS() {
		return monitorNS;
	}

	public void setMonitorNS(NetworkStatusMonitor monitorNS) {
		this.monitorNS = monitorNS;
	}

}

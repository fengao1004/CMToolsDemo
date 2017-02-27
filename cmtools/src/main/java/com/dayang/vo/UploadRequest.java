package com.dayang.vo;

import java.util.List;

public class UploadRequest {
	   private String taskId;
	   private String storageURL;
	   private String fileStatusNotifyURL;
	   private List<String> filesLocalPathArr;
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getStorageURL() {
		return storageURL;
	}
	public void setStorageURL(String storageURL) {
		this.storageURL = storageURL;
	}
	public String getFileStatusNotifyURL() {
		return fileStatusNotifyURL;
	}
	public void setFileStatusNotifyURL(String fileStatusNotifyURL) {
		this.fileStatusNotifyURL = fileStatusNotifyURL;
	}
	public List<String> getFilesLocalPathArr() {
		return filesLocalPathArr;
	}
	public void setFilesLocalPathArr(List<String> filesLocalPathArr) {
		this.filesLocalPathArr = filesLocalPathArr;
	}


	   
}

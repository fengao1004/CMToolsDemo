package com.dayang.upload.netprocess;

public class TaskData {

	private String taskId;
	private String fileStatusNotifyURL;
	private String localPath;
	private String fileStatus;
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getFileStatusNotifyURL() {
		return fileStatusNotifyURL;
	}
	public void setFileStatusNotifyURL(String fileStatusNotifyURL) {
		this.fileStatusNotifyURL = fileStatusNotifyURL;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	public String getFileStatus() {
		return fileStatus;
	}
	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}
	
	
}

package com.dayang.vo;

import java.util.List;

public class BrowerBase {
	   private String success;
	   private String description;
	   private String taskId;
	   private List<Fileinfos> fileInfos;
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<Fileinfos> getFileInfos() {
		return fileInfos;
	}
	public void setFileInfos(List<Fileinfos> fileInfos) {
		this.fileInfos = fileInfos;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
}

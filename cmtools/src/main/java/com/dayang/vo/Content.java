package com.dayang.vo;

import java.util.List;

public class Content {
	   private String taskId;
	   private List<Fileinfos> fileInfos;
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public List<Fileinfos> getFileInfos() {
		return fileInfos;
	}
	public void setFileInfos(List<Fileinfos> fileInfos) {
		this.fileInfos = fileInfos;
	}
	   
	   
}

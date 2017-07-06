package com.dayang.vo;

import java.util.List;

public class FileReturn {
	   private String success;
	   private String description;
	   private List<Fileinfos> fileInfos;
	   private Fileinfos fileInfo;
	    public void setSuccess(String success) {
	        this.success = success;
	    }
	    public String getSuccess() {
	        return success;
	    }
	    

	    public void setDescription(String description) {
	        this.description = description;
	    }
	    public String getDescription() {
	        return description;
	    }
		public List<Fileinfos> getFileInfos() {
			return fileInfos;
		}
		public void setFileInfos(List<Fileinfos> fileInfos) {
			this.fileInfos = fileInfos;
		}
		public Fileinfos getFileInfo() {
			return fileInfo;
		}
		public void setFileInfo(Fileinfos fileInfo) {
			this.fileInfo = fileInfo;
		}
	    

	    
}

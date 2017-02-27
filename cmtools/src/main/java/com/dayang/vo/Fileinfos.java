package com.dayang.vo;

public class Fileinfos {


	   private String name;
	   private String fileType;
	   private String localPath;
	   private Long fileSize;
	   private String imageBase64;
	   private String thumbnail;
	   private String indexNO;
	   private Duration duration;
	    public void setName(String name) {
	        this.name = name;
	    }
	    public String getName() {
	        return name;
	    }
		public String getFileType() {
			return fileType;
		}
		public void setFileType(String fileType) {
			this.fileType = fileType;
		}
		public String getLocalPath() {
			return localPath;
		}
		public void setLocalPath(String localPath) {
			this.localPath = localPath;
		}
		public Long getFileSize() {
			return fileSize;
		}
		public void setFileSize(Long fileSize) {
			this.fileSize = fileSize;
		}
		public String getImageBase64() {
			return imageBase64;
		}
		public void setImageBase64(String imageBase64) {
			this.imageBase64 = imageBase64;
		}
		public String getThumbnail() {
			return thumbnail;
		}
		public void setThumbnail(String thumbnail) {
			this.thumbnail = thumbnail;
		}
		public String getIndexNO() {
			return indexNO;
		}
		public void setIndexNO(String indexNO) {
			this.indexNO = indexNO;
		}
		public Duration getDuration() {
			return duration;
		}
		public void setDuration(Duration duration) {
			this.duration = duration;
		}
}

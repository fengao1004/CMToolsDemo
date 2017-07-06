package com.dayang.vo;

public class Duration {
	   private String value;
	   private String display;
	   
	 public Duration(String value,String display) {
		 this.value = value;
		 this.display = display;
	 }
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	   
	   
}

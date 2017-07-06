package com.dayang.vo;

public class KVReturnValue {
    private String success;
    private String description;
    private String content;


    public KVReturnValue() {
        this.success = "false";
        this.description = "失败";
    }

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


    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }


}

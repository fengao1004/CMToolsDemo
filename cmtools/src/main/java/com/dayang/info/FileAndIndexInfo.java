package com.dayang.info;

/**
 * Created by 冯傲 on 2016/10/26.
 * e-mail 897840134@qq.com
 */
public class FileAndIndexInfo {
    public String filePath;
    public String fileIndex;
    public String fileSessionId;
    public boolean isRename;

    public FileAndIndexInfo(String filePath, String fileIndex, String fileSessionId, boolean isRename) {
        this.filePath = filePath;
        this.fileIndex = fileIndex;
        this.fileSessionId = fileSessionId;
        this.isRename = isRename;
    }
}

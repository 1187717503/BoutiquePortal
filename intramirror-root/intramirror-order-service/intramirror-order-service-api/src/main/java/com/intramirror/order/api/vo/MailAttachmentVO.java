package com.intramirror.order.api.vo;

/**
 * Created by 123 on 2018/5/21.
 */
public class MailAttachmentVO {
    private String fileName;
    private String fileUrl;

    public MailAttachmentVO() {}

    public MailAttachmentVO(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}

package com.guokrspace.cloudschoolbus.parents.event;

import com.guokrspace.cloudschoolbus.parents.entity.UploadFile;

/**
 * Created by macbook on 15/9/13.
 */
public class FileUploadedEvent {
    private UploadFile mUploadFile;
    private boolean isSuccess;

    public FileUploadedEvent(UploadFile uploadFile) {
        this.mUploadFile = uploadFile;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}

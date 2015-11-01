package com.guokrspace.cloudschoolbus.teacher.event;

import com.guokrspace.cloudschoolbus.teacher.database.daodb.UploadArticleFileEntity;

/**
 * Created by macbook on 15/9/13.
 */
public class FileUploadedEvent {
    private UploadArticleFileEntity mUploadFile;
    private boolean isSuccess;

    public FileUploadedEvent(UploadArticleFileEntity uploadFile) {
        this.mUploadFile = uploadFile;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public UploadArticleFileEntity getmUploadFile() {
        return mUploadFile;
    }

    public void setmUploadFile(UploadArticleFileEntity mUploadFile) {
        this.mUploadFile = mUploadFile;
    }
}

package com.guokrspace.cloudschoolbus.teacher.entity;

/**
 * Created by kai on 11/26/14.
 */
public class ImageFile {
    private String filename;
    private String source;
    private String fext;
    private String size;
    private String isCloud;

    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getFext() {
        return fext;
    }
    public void setFext(String fext) {
        this.fext = fext;
    }
    public String getIsCloud() {
        return isCloud;
    }
    public void setIsCloud(String isCloud) {
        this.isCloud = isCloud;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }
}

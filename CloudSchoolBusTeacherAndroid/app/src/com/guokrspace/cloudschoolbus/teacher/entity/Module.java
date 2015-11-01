package com.guokrspace.cloudschoolbus.teacher.entity;

import java.io.Serializable;

public class Module implements Serializable {
    String icon;
    String url;
    String title;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

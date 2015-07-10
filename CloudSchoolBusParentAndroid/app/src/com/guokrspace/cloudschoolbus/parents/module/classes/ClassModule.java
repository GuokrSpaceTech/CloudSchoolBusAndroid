package com.guokrspace.cloudschoolbus.parents.module.classes;

/**
 * Created by wangjianfeng on 15/7/10.
 */
public class ClassModule {
    private String title;
    private Integer imageRes;

    public ClassModule(String title, Integer imageRes) {
        this.title = title;
        this.imageRes = imageRes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getImageRes() {
        return imageRes;
    }

    public void setImageRes(Integer imageRes) {
        this.imageRes = imageRes;
    }
}

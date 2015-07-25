package com.guokrspace.cloudschoolbus.parents.module.explore.classify;

/**
 * Created by wangjianfeng on 15/7/10.
 */
public class ClassifyModule {
    private String title;
    private Integer imageRes;

    public ClassifyModule(String title, Integer imageRes) {
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

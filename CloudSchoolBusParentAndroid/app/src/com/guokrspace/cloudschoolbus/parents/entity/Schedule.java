package com.guokrspace.cloudschoolbus.parents.entity;

/**
 * Created by wangjianfeng on 15/7/21.
 */
public class Schedule {
    private String scheduletime;
    private String cnname;
    private String enname;
    private String week;

    public String getScheduletime() {
        return scheduletime;
    }

    public void setScheduletime(String scheduletime) {
        this.scheduletime = scheduletime;
    }

    public String getCnname() {
        return cnname;
    }

    public void setCnname(String cnname) {
        this.cnname = cnname;
    }

    public String getEnname() {
        return enname;
    }

    public void setEnname(String enname) {
        this.enname = enname;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }
}

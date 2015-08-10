package com.guokrspace.cloudschoolbus.parents.entity;

import java.io.Serializable;

/**
 * Created by Kai on 15/8/10.
 */
public class LatestLetter implements Serializable{
    String teacherid;
    String lastchat;
    String picture;

    public String getTeacherid() {
        return teacherid;
    }

    public void setTeacherid(String teacherid) {
        this.teacherid = teacherid;
    }

    public String getLastchat() {
        return lastchat;
    }

    public void setLastchat(String lastchat) {
        this.lastchat = lastchat;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}

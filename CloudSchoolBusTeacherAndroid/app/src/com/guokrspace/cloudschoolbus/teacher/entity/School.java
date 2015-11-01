package com.guokrspace.cloudschoolbus.teacher.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kai on 9/17/15.
 */
public class School implements Serializable{
    String id;
    String logo;
    String cover;
    String groupid;
    String name;
    String remark;
    String address;

    List<TagT> tags;

    Setting settings;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<TagT> getTags() {
        return tags;
    }

    public void setTags(List<TagT> tags) {
        this.tags = tags;
    }

    public Setting getSettings() {
        return settings;
    }

    public void setSettings(Setting settings) {
        this.settings = settings;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}


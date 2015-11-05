package com.guokrspace.cloudschoolbus.parents.entity;

import java.util.List;

/**
 * Created by macbook on 15-8-4.
 */
public class SchoolInfo {
    List<ClassInfo> classes;
    String logo;
    String schoolid;
    String cover;
    String schoolname;
    String address;
    List<TagT> tags;

    public List<ClassInfo> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassInfo> classes) {
        this.classes = classes;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSchoolid() {
        return schoolid;
    }

    public void setSchoolid(String schoolid) {
        this.schoolid = schoolid;
    }

    public String getSchoolname() {
        return schoolname;
    }

    public void setSchoolname(String schoolname) {
        this.schoolname = schoolname;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<TagT> getTags() {
        return tags;
    }

    public void setTags(List<TagT> tags) {
        this.tags = tags;
    }
}

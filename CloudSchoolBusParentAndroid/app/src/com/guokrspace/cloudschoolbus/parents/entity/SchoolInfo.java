package com.guokrspace.cloudschoolbus.parents.entity;

import java.util.List;

/**
 * Created by macbook on 15-8-4.
 */
public class SchoolInfo {
    List<Classinfo> classes;
    String address;
    String schoolid;
    String schoolname;

    public List<Classinfo> getClasses() {
        return classes;
    }

    public void setClasses(List<Classinfo> classes) {
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
}
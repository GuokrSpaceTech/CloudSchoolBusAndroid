package com.guokrspace.cloudschoolbus.parents.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kai on 12/25/14.
 */
public class Baseinfo implements Serializable {
    private String istrain;
    private List<Teacher> teacherlist;
    private Classinfo classinfo;

    public List<Teacher> getTeacherlist() {
        return teacherlist;
    }

    public void setTeacherlist(List<Teacher> teacherlist) {
        this.teacherlist = teacherlist;
    }

    public Classinfo getClassinfo() {
        return classinfo;
    }

    public void setClassinfo(Classinfo classinfo) {
        this.classinfo = classinfo;
    }

    public String getIstrain() {
        return istrain;
    }

    public void setIstrain(String istrain) {
        this.istrain = istrain;
    }

}


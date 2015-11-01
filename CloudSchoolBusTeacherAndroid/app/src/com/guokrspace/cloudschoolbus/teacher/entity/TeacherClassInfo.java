package com.guokrspace.cloudschoolbus.teacher.entity;

import java.io.Serializable;

public class TeacherClassInfo implements Serializable {
    String classid;
    String dutyid;
    String duty;

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getDutyid() {
        return dutyid;
    }

    public void setDutyid(String dutyid) {
        this.dutyid = dutyid;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }
}

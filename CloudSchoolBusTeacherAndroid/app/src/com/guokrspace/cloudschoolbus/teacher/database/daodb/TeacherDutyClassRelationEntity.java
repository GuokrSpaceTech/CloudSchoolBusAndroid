package com.guokrspace.cloudschoolbus.teacher.database.daodb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table TEACHER_DUTY_CLASS_RELATION_ENTITY.
 */
public class TeacherDutyClassRelationEntity {

    private String classid;
    private String dutyid;
    private String teacherid;

    public TeacherDutyClassRelationEntity() {
    }

    public TeacherDutyClassRelationEntity(String classid, String dutyid, String teacherid) {
        this.classid = classid;
        this.dutyid = dutyid;
        this.teacherid = teacherid;
    }

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

    public String getTeacherid() {
        return teacherid;
    }

    public void setTeacherid(String teacherid) {
        this.teacherid = teacherid;
    }

}

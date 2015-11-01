package com.guokrspace.cloudschoolbus.teacher.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kai on 9/17/15.
 */
public class StudentT implements Serializable {
    String studentid;
    String cnname;
    String nickname;
    String sex;
    String birthday;
    String pictureid;
    String classid;
    String avatar;
    List<String> classids;

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getCnname() {
        return cnname;
    }

    public void setCnname(String cnname) {
        this.cnname = cnname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPictureid() {
        return pictureid;
    }

    public void setPictureid(String pictureid) {
        this.pictureid = pictureid;
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<String> getClassids() {
        return classids;
    }

    public void setClassids(List<String> classsids) {
        this.classids = classsids;
    }
}

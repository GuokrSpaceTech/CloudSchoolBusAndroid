package com.guokrspace.cloudschoolbus.parents.entity;

import java.util.List;

/**
 * Created by macbook on 15-8-4.
 */
//{"ismass":"0",
// "body":["消息体 详见各应用模块"],
// "tag":"标签",
// "isreaded":"0",
// "sender":
//         {"id":"1283",
//          "role":"老师",
//          "avatar":"头像",
//          "classname":"对老师：所属班级名称",
//          "name":"名称 小美，"},
// "id":"24",
// "title":"标题",
// "description":"描述",
// "isconfirm":"0",
// "sendtime":"1438222559",
// "apptype":"Notice",
// "studentid":"36008"}

public class Timeline {
    String ismass;
    List<String> body;
    String tag;
    String isreaded;
    Sender sender;
    String id;
    String title;
    String description;
    String isconfirm;
    String sendtime;
    String apptype;
    String studentid;

    public String getIsmass() {
        return ismass;
    }

    public void setIsmass(String ismass) {
        this.ismass = ismass;
    }

    public List<String> getBody() {
        return body;
    }

    public void setBody(List<String> body) {
        this.body = body;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getIsreaded() {
        return isreaded;
    }

    public void setIsreaded(String isreaded) {
        this.isreaded = isreaded;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsconfirm() {
        return isconfirm;
    }

    public void setIsconfirm(String isconfirm) {
        this.isconfirm = isconfirm;
    }

    public String getSendtime() {
        return sendtime;
    }

    public void setSendtime(String sendtime) {
        this.sendtime = sendtime;
    }

    public String getApptype() {
        return apptype;
    }

    public void setApptype(String apptype) {
        this.apptype = apptype;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }
}

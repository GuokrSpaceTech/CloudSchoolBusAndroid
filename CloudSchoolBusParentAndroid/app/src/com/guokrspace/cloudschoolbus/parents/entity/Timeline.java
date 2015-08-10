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
// "apptype":"NoticeBody",
// "studentid":"36008"}

public class Timeline {
    String ismass;
    String senderid;
    List<String> body;
    String pushedtime;
    String tag;
    String receiverrole;
    String userid;
    String ispushed;
    String userrole;
    String isreaded;
    String receiverids;
    Sender sender;
    String id;
    String title;
    String description;
    String isconfirm;
    String senderrole;
    String messageid;
    String sendtime;
    String apptype;
    String studentid;

    public String getIsmass() {
        return ismass;
    }

    public void setIsmass(String ismass) {
        this.ismass = ismass;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public List<String> getBody() {
        return body;
    }

    public void setBody(List<String> body) {
        this.body = body;
    }

    public String getPushedtime() {
        return pushedtime;
    }

    public void setPushedtime(String pushedtime) {
        this.pushedtime = pushedtime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getReceiverrole() {
        return receiverrole;
    }

    public void setReceiverrole(String receiverrole) {
        this.receiverrole = receiverrole;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getIspushed() {
        return ispushed;
    }

    public void setIspushed(String ispushed) {
        this.ispushed = ispushed;
    }

    public String getUserrole() {
        return userrole;
    }

    public void setUserrole(String userrole) {
        this.userrole = userrole;
    }

    public String getIsreaded() {
        return isreaded;
    }

    public void setIsreaded(String isreaded) {
        this.isreaded = isreaded;
    }

    public String getReceiverids() {
        return receiverids;
    }

    public void setReceiverids(String receiverids) {
        this.receiverids = receiverids;
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

    public String getSenderrole() {
        return senderrole;
    }

    public void setSenderrole(String senderrole) {
        this.senderrole = senderrole;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
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

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
    Sender sender;
    String body;
    String title;
    String tag;
    String description;
    String isconfirm;
    String messageid;
    String isreaded;
    String apptype;
    String sendtime;
    String studentid;

    public String getIsmass() {
        return ismass;
    }

    public void setIsmass(String ismass) {
        this.ismass = ismass;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getIsreaded() {
        return isreaded;
    }

    public void setIsreaded(String isreaded) {
        this.isreaded = isreaded;
    }

    public String getApptype() {
        return apptype;
    }

    public void setApptype(String apptype) {
        this.apptype = apptype;
    }

    public String getSendtime() {
        return sendtime;
    }

    public void setSendtime(String sendtime) {
        this.sendtime = sendtime;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    /**
     * Created by macbook on 15-8-4.
     */
    //         {"id":"1283",
    //          "role":"老师",
    //          "avatar":"头像",
    //          "classname":"对老师：所属班级名称",
    //          "name":"名称 小美，"},
    public static class Sender {
        String id;
        String role;
        String avatar;
        String classname;
        String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getClassname() {
            return classname;
        }

        public void setClassname(String classname) {
            this.classname = classname;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

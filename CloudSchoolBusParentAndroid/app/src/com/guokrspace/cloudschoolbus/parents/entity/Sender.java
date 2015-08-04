package com.guokrspace.cloudschoolbus.parents.entity;

/**
 * Created by macbook on 15-8-4.
 */
//         {"id":"1283",
//          "role":"老师",
//          "avatar":"头像",
//          "classname":"对老师：所属班级名称",
//          "name":"名称 小美，"},
public class Sender {
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

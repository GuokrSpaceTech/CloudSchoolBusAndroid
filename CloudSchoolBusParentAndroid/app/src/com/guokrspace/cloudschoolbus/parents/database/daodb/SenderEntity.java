package com.guokrspace.cloudschoolbus.parents.database.daodb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table SENDER_ENTITY.
 */
public class SenderEntity {

    /** Not-null value. */
    private String id;
    private String role;
    private String avatar;
    private String classname;
    private String name;

    public SenderEntity() {
    }

    public SenderEntity(String id) {
        this.id = id;
    }

    public SenderEntity(String id, String role, String avatar, String classname, String name) {
        this.id = id;
        this.role = role;
        this.avatar = avatar;
        this.classname = classname;
        this.name = name;
    }

    /** Not-null value. */
    public String getId() {
        return id;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
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

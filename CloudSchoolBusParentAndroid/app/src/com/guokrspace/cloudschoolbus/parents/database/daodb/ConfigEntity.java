package com.guokrspace.cloudschoolbus.parents.database.daodb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import java.io.Serializable;

/**
 * Entity mapped to table CONFIG_ENTITY.
 */
public class ConfigEntity implements Serializable{

    private Long id;
    private String sid;
    private String token;
    private String mobile;

    public ConfigEntity() {
    }

    public ConfigEntity(Long id) {
        this.id = id;
    }

    public ConfigEntity(Long id, String sid, String token, String mobile) {
        this.id = id;
        this.sid = sid;
        this.token = token;
        this.mobile = mobile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}

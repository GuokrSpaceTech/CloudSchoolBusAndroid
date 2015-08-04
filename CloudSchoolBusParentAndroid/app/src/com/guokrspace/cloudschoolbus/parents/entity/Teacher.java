package com.guokrspace.cloudschoolbus.parents.entity;

import java.io.Serializable;

/**
 * Created by kai on 12/25/14.
 */

// {"avatar":"http:\/\/cloud.yunxiaoche.com\/images\/teacher.jpg",
//              "id":"1234",
//              "duty":"班主任",
//              "name":"小美"}],
public class Teacher implements Serializable {
    private String id;
    private String duty;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

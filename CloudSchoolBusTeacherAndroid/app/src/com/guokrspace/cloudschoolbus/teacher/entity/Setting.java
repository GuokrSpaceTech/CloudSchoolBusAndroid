package com.guokrspace.cloudschoolbus.teacher.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kai on 9/17/15.
 */
public class Setting implements Serializable {
    List<String> message_type;
    List<Module> class_module;
    HashMap teacher_duty;

    public List<String> getMessage_type() {
        return message_type;
    }

    public void setMessage_type(List<String> message_type) {
        this.message_type = message_type;
    }

    public void setClass_module(List<Module> class_module) {
        this.class_module = class_module;
    }

    public List<Module> getClass_module() {
        return class_module;
    }

    public HashMap getTeacher_duty() {
        return teacher_duty;
    }

    public void setTeacher_duty(HashMap teacher_duty) {
        this.teacher_duty = teacher_duty;
    }

    // Return ,,,, format
    public String getMessageTypeString()
    {
        String ret = "";
        for(String s:message_type)
        {
            ret += s;
        }

        if(!ret.equals(""))
            ret.substring(0,ret.lastIndexOf(','));

        return ret;
    }

    public List<String> getMessageTypeArray(String s)
    {
        List<String> ret = new ArrayList<>();
        String[] strArr =  s.split(",");
        for(int i=0; i < strArr.length; i++)
        {
            ret.add(strArr[i]);
        }

        return ret;
    }
}


package com.guokrspace.cloudschoolbus.parents.entity;

import java.io.Serializable;

public class TagT implements Serializable {
    String tagid;
    String tagname;
    String tagname_en;
    String tagnamedesc;
    String tagnamedesc_en;
    String schoolid;
    String idelete;

    public String getTagid() {
        return tagid;
    }

    public void setTagid(String tagid) {
        this.tagid = tagid;
    }

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

    public String getTagname_en() {
        return tagname_en;
    }

    public void setTagname_en(String tagname_en) {
        this.tagname_en = tagname_en;
    }

    public String getTagnamedesc() {
        return tagnamedesc;
    }

    public void setTagnamedesc(String tagnamedesc) {
        this.tagnamedesc = tagnamedesc;
    }

    public String getTagnamedesc_en() {
        return tagnamedesc_en;
    }

    public void setTagnamedesc_en(String tagnamedesc_en) {
        this.tagnamedesc_en = tagnamedesc_en;
    }

    public String getSchoolid() {
        return schoolid;
    }

    public void setSchoolid(String schoolid) {
        this.schoolid = schoolid;
    }

    public String getIdelete() {
        return idelete;
    }

    public void setIdelete(String idelete) {
        this.idelete = idelete;
    }
}

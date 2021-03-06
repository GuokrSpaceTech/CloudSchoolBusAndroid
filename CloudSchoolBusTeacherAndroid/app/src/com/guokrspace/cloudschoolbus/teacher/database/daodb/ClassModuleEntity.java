package com.guokrspace.cloudschoolbus.teacher.database.daodb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table CLASS_MODULE_ENTITY.
 */
public class ClassModuleEntity {

    /** Not-null value. */
    private String id;
    private String icon;
    private String url;
    private String title;
    /** Not-null value. */
    private String schoolid;

    public ClassModuleEntity() {
    }

    public ClassModuleEntity(String id) {
        this.id = id;
    }

    public ClassModuleEntity(String id, String icon, String url, String title, String schoolid) {
        this.id = id;
        this.icon = icon;
        this.url = url;
        this.title = title;
        this.schoolid = schoolid;
    }

    /** Not-null value. */
    public String getId() {
        return id;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /** Not-null value. */
    public String getSchoolid() {
        return schoolid;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setSchoolid(String schoolid) {
        this.schoolid = schoolid;
    }

}

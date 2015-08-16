package com.guokrspace.cloudschoolbus.parents.database.daodb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table ATTENDANCE_ENTITY.
 */
public class AttendanceEntity {

    private String month;
    private String day;
    /** Not-null value. */
    private String timestamp;
    private String imageUrl;

    public AttendanceEntity() {
    }

    public AttendanceEntity(String timestamp) {
        this.timestamp = timestamp;
    }

    public AttendanceEntity(String month, String day, String timestamp, String imageUrl) {
        this.month = month;
        this.day = day;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    /** Not-null value. */
    public String getTimestamp() {
        return timestamp;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}

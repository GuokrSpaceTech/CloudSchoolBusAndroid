package com.guokrspace.cloudschoolbus.teacher.module.chat;

/**
 * Created by macbook on 15-8-18.
 */
public class UserInbox {
    String lastmessage_timestamp;
    String lastmessage_id;
    String lastmessage_content;

    public String getLastmessage_timestamp() {
        return lastmessage_timestamp;
    }

    public void setLastmessage_timestamp(String lastmessage_timestamp) {
        this.lastmessage_timestamp = lastmessage_timestamp;
    }

    public String getLastmessage_id() {
        return lastmessage_id;
    }

    public void setLastmessage_id(String lastmessage_id) {
        this.lastmessage_id = lastmessage_id;
    }

    public String getLastmessage_content() {
        return lastmessage_content;
    }

    public void setLastmessage_content(String lastmessage_content) {
        this.lastmessage_content = lastmessage_content;
    }
}

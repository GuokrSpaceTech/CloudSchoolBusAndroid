package com.guokrspace.cloudschoolbus.parents.event;

import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntity;

/**
 * Created by macbook on 15-8-9.
 */
public class NewMessageEvent{
    private int message_count;

    public NewMessageEvent(int message_count) {
        this.message_count = message_count;
    }

    public int getMessage_count() {
        return message_count;
    }
}

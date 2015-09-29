package com.guokrspace.cloudschoolbus.parents.widget;

import android.content.Context;

import com.dexafree.materialList.cards.SimpleCard;
import com.guokrspace.cloudschoolbus.parents.R;

/**
 * Created by Yang Kai on 15/7/14.
 */
public class AttendanceRecordCard extends SimpleCard {

    private String teacherAvatarUrl;
    private String teacherName;
    private String className;
    private String sentTime;
    private String cardType;
    private String recordTime;
    private String recordPicture;
    private Context context;

    public String getTeacherAvatarUrl() {
        return teacherAvatarUrl;
    }

    public void setTeacherAvatarUrl(String teacherAvatarUrl) {
        this.teacherAvatarUrl = teacherAvatarUrl;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public String getRecordPicture() {
        return recordPicture;
    }

    public void setRecordPicture(String recordPicture) {
        this.recordPicture = recordPicture;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public AttendanceRecordCard(Context context) {
        super(context);
        this.setContext(context);
    }

    @Override
    public int getLayout() {
        return R.layout.material_attendance_card_layout;
    }
}
package com.dexafree.materialList.cards;

import android.content.Context;
import com.dexafree.materialList.R;

/**
 * Created by Yang Kai on 15/7/14.
 */
public class CustomCard extends SimpleCard{

    private String teacherAvatarUrl;
    private String teacherName;
    private String kindergarten;
    private String sentTime;

    public String getKindergarten() {
        return kindergarten;
    }

    public void setKindergarten(String kindergarten) {
        this.kindergarten = kindergarten;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherAvatarUrl() {
        return teacherAvatarUrl;
    }

    public void setTeacherAvatarUrl(String teacherAvatarUrl) {
        this.teacherAvatarUrl = teacherAvatarUrl;
    }

    public CustomCard(Context context) {
        super(context);
    }

    @Override
    public int getLayout() {
        return R.layout.material_customer_card_layout;
    }
}
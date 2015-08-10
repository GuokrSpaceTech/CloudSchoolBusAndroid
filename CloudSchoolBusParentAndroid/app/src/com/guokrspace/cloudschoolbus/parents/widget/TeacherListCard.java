package com.guokrspace.cloudschoolbus.parents.widget;

import android.content.Context;
import android.view.View;

import com.dexafree.materialList.cards.SimpleCard;
import com.guokrspace.cloudschoolbus.parents.R;

/**
 * Created by Yang Kai on 15/7/14.
 */
public class TeacherListCard extends SimpleCard {

    private String teacherAvatarUrl;
    private String teacherName;
    private String classname;
    private Context context;

    public TeacherListCard(Context context) {
        super(context);
        context = context;
    }

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

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public int getLayout() {
        return R.layout.material_teacher_list_card_layout;
    }
}
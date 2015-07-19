package com.dexafree.materialList.cards;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dexafree.materialList.R;
import com.dexafree.materialList.controller.CommonRecyclerItemClickListener;

/**
 * Created by Yang Kai on 15/7/14.
 */
public class NoticeCard extends SimpleCard{

    private String teacherAvatarUrl;
    private String teacherName;
    private String kindergarten;
    private String sentTime;
    private View.OnClickListener mConfirmButtonClickListener;
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

    public View.OnClickListener getmConfirmButtonClickListener() {
        return mConfirmButtonClickListener;
    }

    public void setmConfirmButtonClickListener(View.OnClickListener mConfirmButtonClickListener) {
        this.mConfirmButtonClickListener = mConfirmButtonClickListener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public NoticeCard(Context context) {
        super(context);
        this.setContext(context);
    }

    @Override
    public int getLayout() {
        return R.layout.material_notice_card_layout;
    }
}
package com.guokrspace.cloudschoolbus.parents.widget;

import android.content.Context;
import android.view.View;

import com.dexafree.materialList.cards.SimpleCard;
import com.guokrspace.cloudschoolbus.parents.R;

/**
 * Created by Yang Kai on 15/7/14.
 */
public class NoticeCard extends SimpleCard {

    private String teacherAvatarUrl;
    private String teacherName;
    private String className;
    private String sentTime;
    private String cardType;
    private String isNeedConfirm;
    private View.OnClickListener mConfirmButtonClickListener;
    private Context context;

    public NoticeCard(Context context) {
        super(context);
        this.context = context;
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

    public View.OnClickListener getmConfirmButtonClickListener() {
        return mConfirmButtonClickListener;
    }

    public void setmConfirmButtonClickListener(View.OnClickListener mConfirmButtonClickListener) {
        this.mConfirmButtonClickListener = mConfirmButtonClickListener;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getIsNeedConfirm() {
        return isNeedConfirm;
    }

    public void setIsNeedConfirm(String isNeedConfirm) {
        this.isNeedConfirm = isNeedConfirm;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public int getLayout() {
        return R.layout.material_notice_card_layout;
    }
}
package com.guokrspace.cloudschoolbus.teacher.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.dexafree.materialList.cards.SimpleCard;
import com.guokrspace.cloudschoolbus.teacher.R;

public class ChatMessageCard extends SimpleCard {
    private String senderImageUrl;
    private String timestamp;
    private Drawable mDrawableSender;
    private String roleType;
    private String letterType;
    private String contentText;
    private String contentImage;
    private Drawable mDrawableContentImage;

    public String getSenderImageUrl() {
        return senderImageUrl;
    }

    public void setSenderImageUrl(String senderImageUrl) {
        this.senderImageUrl = senderImageUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Drawable getmDrawableSender() {
        return mDrawableSender;
    }

    public void setmDrawableSender(Drawable mDrawableSender) {
        this.mDrawableSender = mDrawableSender;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getLetterType() {
        return letterType;
    }

    public void setLetterType(String letterType) {
        this.letterType = letterType;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getContentImage() {
        return contentImage;
    }

    public void setContentImage(String contentImage) {
        this.contentImage = contentImage;
    }

    public Drawable getmDrawableContentImage() {
        return mDrawableContentImage;
    }

    public void setmDrawableContentImage(Drawable mDrawableContentImage) {
        this.mDrawableContentImage = mDrawableContentImage;
    }

    public ChatMessageCard(final Context context) {
        super(context);
    }

    @Override
    public int getLayout() {
        return R.layout.material_chat_message_card;
    }
}
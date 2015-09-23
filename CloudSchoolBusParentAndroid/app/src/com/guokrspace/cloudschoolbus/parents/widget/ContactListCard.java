package com.guokrspace.cloudschoolbus.parents.widget;

import android.content.Context;
import android.view.View;

import com.dexafree.materialList.cards.SimpleCard;
import com.guokrspace.cloudschoolbus.parents.R;

/**
 * Created by Yang Kai on 15/7/14.
 */
public class ContactListCard extends SimpleCard {

    private String contactAvatarUrl;
    private String contactName;
    private String classname;
    private String timestamp;
    private Context context;
    private int     badgeCount;
    private View.OnClickListener onClickListener;

    public ContactListCard(Context context) {
        super(context);
        context = context;
    }

    public String getContactAvatarUrl() {
        return contactAvatarUrl;
    }

    public void setContactAvatarUrl(String contactAvatarUrl) {
        this.contactAvatarUrl = contactAvatarUrl;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public int getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(int badgeCount) {
        this.badgeCount = badgeCount;
    }

    @Override
    public int getLayout() {
        return R.layout.material_teacher_list_card_layout;
    }
}
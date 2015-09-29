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
    private String childrenname;
    private String phonenumber;
    private String timestamp;
    private Context mContext;
    private int     badgeCount;
    private View.OnClickListener onClickListener;

    public ContactListCard(Context context) {
        super(context);
        mContext = context;
    }

    public String getChildrenname() {
        return contactAvatarUrl;
    }

    public void setChildrenname(String childrenname) {
        this.contactAvatarUrl = childrenname;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getSubtitle() {
        return childrenname;
    }

    public void setSubtitle(String subtitle) {
        this.childrenname = subtitle;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        this.mContext = context;
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
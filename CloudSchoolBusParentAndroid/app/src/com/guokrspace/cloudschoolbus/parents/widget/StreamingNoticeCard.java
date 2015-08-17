package com.guokrspace.cloudschoolbus.parents.widget;

import android.content.Context;
import android.view.View;

import com.dexafree.materialList.cards.SimpleCard;
import com.guokrspace.cloudschoolbus.parents.R;

/**
 * Created by Kai on 15/7/26.
 */
public class StreamingNoticeCard extends SimpleCard {

    private String kindergartenAvatar;
    private String kindergartenName;
    private String className;
    private String sentTime;
    private String cardType;
    private View.OnClickListener clickListener;
    private Context context;

    public StreamingNoticeCard(Context context) {
        super(context);
        this.context = context;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getKindergartenAvatar() {
        return kindergartenAvatar;
    }

    public void setKindergartenAvatar(String kindergartenAvatar) {
        this.kindergartenAvatar = kindergartenAvatar;
    }

    public String getKindergartenName() {
        return kindergartenName;
    }

    public void setKindergartenName(String kindergartenName) {
        this.kindergartenName = kindergartenName;
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

    public View.OnClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public int getLayout() {
        return R.layout.material_streaming_notice_card;
    }

}

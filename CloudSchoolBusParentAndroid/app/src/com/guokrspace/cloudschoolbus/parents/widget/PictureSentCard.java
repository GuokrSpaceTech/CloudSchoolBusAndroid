package com.guokrspace.cloudschoolbus.parents.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.android.support.utils.DateUtils;
import com.dexafree.materialList.cards.SimpleCard;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.module.photo.adapter.SentPictureAdapter;

/**
 * Created by Yang Kai on 15/7/14.
 */
public class PictureSentCard extends SimpleCard {

    private String sentTime;
    private String cardType;
    private SentPictureAdapter imageAdapter;
    private RecyclerView.Adapter tagAdapter;
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public SentPictureAdapter getImageAdapter() {
        return imageAdapter;
    }

    public void setImageAdapter(SentPictureAdapter imageAdapter) {
        this.imageAdapter = imageAdapter;
    }

    public RecyclerView.Adapter getTagAdapter() {
        return tagAdapter;
    }

    public void setTagAdapter(RecyclerView.Adapter tagAdapter) {
        this.tagAdapter = tagAdapter;
    }

    public String getSentTime() {
        return DateUtils.timelineTimestamp(sentTime, context);
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public PictureSentCard(Context context) {
        super(context);
        this.setContext(context);
    }

    @Override
    public int getLayout() {
        return R.layout.material_picture_sent_card_layout;
    }
}
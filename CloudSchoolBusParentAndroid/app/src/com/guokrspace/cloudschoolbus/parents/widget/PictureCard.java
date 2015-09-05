package com.guokrspace.cloudschoolbus.parents.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dexafree.materialList.cards.SimpleCard;
import com.dexafree.materialList.controller.CommonRecyclerItemClickListener;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.module.explore.ImageAdapter;

/**
 * Created by Yang Kai on 15/7/14.
 */
public class PictureCard extends SimpleCard {

    private String teacherAvatarUrl;
    private String teacherName;
    private String kindergarten;
    private String sentTime;
    private String cardType;
    private ImageAdapter imageAdapter;
    private RecyclerView.Adapter tagAdapter;
    private CommonRecyclerItemClickListener mOnItemSelectedListener;
    private View.OnClickListener mShareButtonClickListener;
    private Context context;

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

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public CommonRecyclerItemClickListener getmOnItemSelectedListener() {
        return mOnItemSelectedListener;
    }

    public void setmOnItemSelectedListener(CommonRecyclerItemClickListener mOnItemSelectedListener) {
        this.mOnItemSelectedListener = mOnItemSelectedListener;
    }

    public View.OnClickListener getmShareButtonClickListener() {
        return mShareButtonClickListener;
    }

    public void setmShareButtonClickListener(View.OnClickListener mShareButtonClickListener) {
        this.mShareButtonClickListener = mShareButtonClickListener;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public ImageAdapter getImageAdapter() {
        return imageAdapter;
    }

    public void setImageAdapter(ImageAdapter imageAdapter) {
        this.imageAdapter = imageAdapter;
    }

    public RecyclerView.Adapter getTagAdapter() {
        return tagAdapter;
    }

    public void setTagAdapter(RecyclerView.Adapter tagAdapter) {
        this.tagAdapter = tagAdapter;
    }

    public PictureCard(Context context) {
        super(context);
        this.setContext(context);
    }

    @Override
    public int getLayout() {
        return R.layout.material_picture_card_layout;
    }
}
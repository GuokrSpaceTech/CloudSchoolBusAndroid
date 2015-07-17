package com.dexafree.materialList.cards;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;

import com.dexafree.materialList.R;
import com.dexafree.materialList.controller.CommonRecyclerItemClickListener;
import com.dexafree.materialList.controller.RecyclerItemClickListener;

/**
 * Created by Yang Kai on 15/7/14.
 */
public class CustomCard extends SimpleCard{

    private String teacherAvatarUrl;
    private String teacherName;
    private String kindergarten;
    private String sentTime;
    private RecyclerView.Adapter adapter;
    private String likesNum;
    private String commentNum;
    private CommonRecyclerItemClickListener mOnItemSelectedListener;
    private View.OnClickListener mShareButtonClickListener;
    private View.OnClickListener mLikeButtonClickListener;
    private View.OnClickListener mCommentButtonClickListener;
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

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getLikesNum() {
        return likesNum;
    }

    public void setLikesNum(String likesNum) {
        this.likesNum = likesNum;
    }

    public String getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
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

    public View.OnClickListener getmLikeButtonClickListener() {
        return mLikeButtonClickListener;
    }

    public void setmLikeButtonClickListener(View.OnClickListener mLikeButtonClickListener) {
        this.mLikeButtonClickListener = mLikeButtonClickListener;
    }

    public View.OnClickListener getmCommentButtonClickListener() {
        return mCommentButtonClickListener;
    }

    public void setmCommentButtonClickListener(View.OnClickListener mCommentButtonClickListener) {
        this.mCommentButtonClickListener = mCommentButtonClickListener;
    }

    public CustomCard(Context context) {
        super(context);
        this.setContext(context);
    }

    @Override
    public int getLayout() {
        return R.layout.material_customer_card_layout;
    }
}
/**
 * Copyright 2015 Soulwolf Ching
 * Copyright 2015 The Android Open Source Project for PictureLib
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.guokrspace.cloudschoolbus.teacher.module.photo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.support.utils.ImageUtil;
import com.guokrspace.cloudschoolbus.teacher.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.teacher.R;
import com.guokrspace.cloudschoolbus.teacher.base.activity.GalleryActivityUrl;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.UploadArticleFileEntity;
import com.guokrspace.cloudschoolbus.teacher.module.photo.service.UploadFileHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.BlurTransformation;


/**
 * author: Soulwolf Created on 2015/7/13 23:49.
 * email : Ching.Soulwolf@gmail.com
 */
public class SentPictureAdapter extends BaseAdapter {

    Context mContext;
    List<UploadArticleFileEntity> mPictureList;
    CloudSchoolBusParentsApplication mApplication;

    public SentPictureAdapter(Context context, List<UploadArticleFileEntity> pictures){
        this.mContext = context;
        this.mPictureList = pictures;
        mApplication = (CloudSchoolBusParentsApplication)mContext.getApplicationContext();
    }

    @Override
    public int getCount() {
        return mPictureList == null ? 0 : mPictureList.size();
    }

    @Override
    public UploadArticleFileEntity getItem(int position) {
        return mPictureList == null ? null : mPictureList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            holder = new ViewHolder();
            convertView =  LayoutInflater.from(mContext).inflate(R.layout.listview_picture_item, null);
            holder.mPictureView = (ImageView)convertView.findViewById(R.id.picture_item_image);
            holder.mRetryIcon   = (ImageView)convertView.findViewById(R.id.retry_icon);
            holder.mProgressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        UploadArticleFileEntity uploadFile = getItem(position);
        if(uploadFile.getThumb()==null || uploadFile.getThumb().isEmpty() || uploadFile.getThumb()=="") {
            String url = getItem(position).getFbody();
            Bitmap bm = ImageUtil.decodeSampledBitmapFromUri(url, 80, 80);
            String thumb = ImageUtil.saveImage(bm);
            uploadFile.setThumb(thumb);
            mApplication.mDaoSession.getUploadArticleFileEntityDao().update(uploadFile);
        }
        String thumb = "file://" + uploadFile.getThumb();

        //Upload is in progress
        BlurTransformation transformation = new BlurTransformation(mContext, 25, 1);
        if(getItem(position).getIsSuccess() == null) {

            Picasso.with(mContext).load(thumb).transform(transformation).fit().centerCrop().into(holder.mPictureView);
            holder.mPictureView.setOnClickListener(null);
            holder.mRetryIcon.setVisibility(View.INVISIBLE);
            holder.mProgressBar.setVisibility(View.VISIBLE);

        // Upload failed
        } else if(!getItem(position).getIsSuccess()){
            Picasso.with(mContext).load(thumb).transform(transformation).fit().centerCrop().into(holder.mPictureView);
            holder.mPictureView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.mRetryIcon.setVisibility(View.INVISIBLE);
                    holder.mProgressBar.setVisibility(View.VISIBLE);
                    UploadFileHelper.getInstance().retryFailedFile(mPictureList.get(position));
                }
            });
            holder.mRetryIcon.setVisibility(View.VISIBLE);
            holder.mProgressBar.setVisibility(View.INVISIBLE);
        // Upload Success
        } else {
            Picasso.with(mContext).load(thumb).fit().centerCrop().into(holder.mPictureView);
            final List<String> mFilePaths = new ArrayList<>();
            for (UploadArticleFileEntity file : mPictureList) {
                String filepath = file.getFbody();
                mFilePaths.add("file://" + filepath);
            }
            holder.mPictureView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, GalleryActivityUrl.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("fileUrls", (ArrayList<String>) mFilePaths);
                    bundle.putInt("currentFile", position);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            });
            holder.mRetryIcon.setVisibility(View.INVISIBLE);
            holder.mProgressBar.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    static class ViewHolder{
        public ImageView mPictureView;
        public ImageView mRetryIcon;
        public ProgressBar mProgressBar;
    }
}
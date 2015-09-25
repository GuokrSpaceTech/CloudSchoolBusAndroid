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
package com.guokrspace.cloudschoolbus.parents.module.photo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.BlurMaskFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.database.daodb.UploadArticleFileEntity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import jp.wasabeef.picasso.transformations.BlurTransformation;


/**
 * author: Soulwolf Created on 2015/7/13 23:49.
 * email : Ching.Soulwolf@gmail.com
 */
public class SentPictureAdapter extends BaseAdapter {

    Context mContext;

    List<UploadArticleFileEntity> mPictureList;

    public SentPictureAdapter(Context context, List<UploadArticleFileEntity> pictures){
        this.mContext = context;
        this.mPictureList = pictures;
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
        ViewHolder holder;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            holder = new ViewHolder();
            convertView =  LayoutInflater.from(mContext).inflate(R.layout.listview_picture_item, null);
            holder.mPictureView = (ImageView)convertView.findViewById(R.id.picture_item_image);
            holder.mRetryIcon   = (ImageView)convertView.findViewById(R.id.retry_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        String url = getItem(position).getFbody();
//        url = "file://" + url;
        Bitmap bm = decodeSampledBitmapFromUri(url, 220, 220);

//        Picasso.with(mContext).load(url).transform(new BlurTransformation(mContext, 25, 1)).centerCrop().fit().into(imageView);

        if(getItem(position).getIsSuccess() == null) {
            BlurTransformation transformation = new BlurTransformation(mContext, 25, 1);
            bm = transformation.transform(bm);
            holder.mPictureView.setImageBitmap(bm);
        } else if(!getItem(position).getIsSuccess()){
//            holder.mPictureView.setImageBitmap(bm);
        } else {
            holder.mPictureView.setImageBitmap(bm);
        }

        return convertView;
//        // load image
//        String url = getItem(position);
//
//        if(url.contains("http://") || url.contains("file://")) {
//            if (url.contains("orig")) { url = url.replace("orig", "thumbs"); }
//            url = url + ".tiny.jpg";
//            Picasso.with(mContext).load(url).centerCrop().fit().into(holder.mPictureView);
//        } else {
//            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(url), 60, 60);
//            holder.mPictureView.setImageBitmap(ThumbImage);
//            url = "file://" + url;
//            mPictureList.set(position, url);
////            Picasso.with(mContext).load(url).centerCrop().fit().into(imageView);
//
////            Picasso.with(mContext)
////                    .load(new File(url)).centerCrop().fit().error(R.drawable.pd_empty_picture)
////                    .into(holder.mPictureView);
//        }
//        return convertView;
    }

    static class ViewHolder{
        public ImageView mPictureView;
        public ImageView mRetryIcon;
    }

    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {

        Bitmap bm = null;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }

    public int calculateInSampleSize(

            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }

        return inSampleSize;
    }

    public void setUploadFileSuccess(int position)
    {

    }

}

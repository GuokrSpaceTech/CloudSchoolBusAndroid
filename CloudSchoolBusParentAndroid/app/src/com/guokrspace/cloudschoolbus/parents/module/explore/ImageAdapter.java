package com.guokrspace.cloudschoolbus.parents.module.explore;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.guokrspace.cloudschoolbus.parents.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mPicUrls = new ArrayList<>();

    public ImageAdapter(Context c, List<String> urls) {
        mContext = c;
        mPicUrls = urls;
    }

    @Override
    public int getCount() {
        return mPicUrls.size();
    }

    @Override
    public Object getItem(int i) {
        return mPicUrls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            if(mPicUrls.size()==1)
                imageView.setLayoutParams(new GridView.LayoutParams(
                        (int)mContext.getResources().getDimension(R.dimen.timeline_one_thumb_pic_width),
                        (int)mContext.getResources().getDimension(R.dimen.timeline_one_thumb_pic_height)));
            else
                imageView.setLayoutParams(new GridView.LayoutParams(
                        (int)mContext.getResources().getDimension(R.dimen.timeline_thumb_pic_width),
                        (int)mContext.getResources().getDimension(R.dimen.timeline_thumb_pic_height)));

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(1, 1, 1, 1);
        } else {
            imageView = (ImageView) convertView;
            imageView.forceLayout();
        }

        String url = mPicUrls.get(position);

        Picasso.with(mContext).load(url).into(imageView);

        return imageView;
    }
}

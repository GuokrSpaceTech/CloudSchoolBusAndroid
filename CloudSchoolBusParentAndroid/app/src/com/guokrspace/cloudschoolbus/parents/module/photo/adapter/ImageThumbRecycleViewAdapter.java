package com.guokrspace.cloudschoolbus.parents.module.photo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.guokrspace.cloudschoolbus.parents.R;
import com.squareup.picasso.Picasso;

import net.soulwolf.image.picturelib.model.Picture;

import java.util.List;

/**
 * Created by kai on 7/16/15.
 */
public class ImageThumbRecycleViewAdapter extends RecyclerView.Adapter<ImageThumbRecycleViewAdapter.ViewHolder> {
    private List<Picture> mDataset;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mThumbnail;
        public ViewHolder(ImageView v) {
            super(v);
            mThumbnail = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ImageThumbRecycleViewAdapter(Context context, List<Picture> myDataset) {
        mDataset = myDataset;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.thumb_image, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder((ImageView)v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mThumbnail.setTag(position);
        Picasso.with(mContext).load(mDataset.get(position).getPicturePath()).centerCrop().fit().into(holder.mThumbnail);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

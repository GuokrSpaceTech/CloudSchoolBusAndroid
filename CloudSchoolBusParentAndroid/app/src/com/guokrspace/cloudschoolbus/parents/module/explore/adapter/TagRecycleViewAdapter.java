package com.guokrspace.cloudschoolbus.parents.module.explore.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.support.handlerui.HandlerToastUI;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TagEntity;

import java.util.List;

/**
 * Created by kai on 7/16/15.
 */
public class TagRecycleViewAdapter extends RecyclerView.Adapter<TagRecycleViewAdapter.ViewHolder> {
    private List<TagEntity> mDataset;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTagButton;
        public ViewHolder(TextView v) {
            super(v);
            mTagButton = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TagRecycleViewAdapter(List<TagEntity> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tag_button, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder((TextView)v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTagButton.setText(mDataset.get(position).getTagName());
        holder.mTagButton.setTag(mDataset.get(position).getTagnamedesc());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public List<TagEntity> getmDataset() {
        return mDataset;
    }
}

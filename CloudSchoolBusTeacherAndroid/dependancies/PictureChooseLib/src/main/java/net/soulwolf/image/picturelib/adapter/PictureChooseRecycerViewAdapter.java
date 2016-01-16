/**
 * <pre>
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
 * </pre>
 */
package net.soulwolf.image.picturelib.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.soulwolf.image.picturelib.R;
import net.soulwolf.image.picturelib.listener.RecyclerItemClickListener;
import net.soulwolf.image.picturelib.model.Picture;
import net.soulwolf.image.picturelib.utils.Utils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * author: Soulwolf Created on 2015/7/13 23:49.
 * email : Ching.Soulwolf@gmail.com
 */
public class PictureChooseRecycerViewAdapter extends RecyclerView.Adapter<PictureChooseRecycerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Picture> mDataset;
    private List<Integer> mPictureChoose;
    private HashMap<String, List<Picture>> mPictureChooseMap;
    private String mCurrentFolder;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mPictureView;
        public FrameLayout mPictureState;
        public TextView mNoteTextView;

        public ViewHolder(View v) {
            super(v);
            mPictureView = (ImageView) v.findViewById(R.id.pi_picture_choose_item_image);
            mPictureState = (FrameLayout) v.findViewById(R.id.pi_picture_choose_item_select);
            mNoteTextView = (TextView) v.findViewById(R.id.pi_picture_choose_item_note);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PictureChooseRecycerViewAdapter(Context context, List<Picture> myDataset) {
        this.mDataset = myDataset;
        this.mContext = context;
        this.mPictureChoose = new ArrayList<>();
        this.mPictureChooseMap = new HashMap<>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_picture_choose_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(mDataset.get(position).isDrawable) {
            if(position == 0)
                holder.mPictureView.setImageDrawable(mDataset.get(position).drawable);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            holder.mPictureView.setLayoutParams(layoutParams);
            holder.mNoteTextView.setVisibility(View.VISIBLE);
            holder.mNoteTextView.setText("拍摄照片");
            Picasso.with(mContext).cancelRequest(holder.mPictureView);
            holder.mPictureView.invalidate();
            holder.mNoteTextView.forceLayout();
        } else {
            holder.mPictureView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            holder.mNoteTextView.setVisibility(View.INVISIBLE);
            String url = mDataset.get(position).getPicturePath();
            if (url == null) url = mDataset.get(position).getThumbPath();
            //      ImageLoadTask.getInstance().display(holder.mPictureView, Utils.urlFromFile(url));

            Picasso.with(mContext)
                    .load(Utils.urlFromFile(url))
                    .fit().centerCrop()
                    .into(holder.mPictureView);
        }

        if(this.contains(position)){
            if(holder.mPictureState.getVisibility() != View.VISIBLE){
                holder.mPictureState.setVisibility(View.VISIBLE);
            }
        }else {
            if(holder.mPictureState.getVisibility() != View.GONE){
                holder.mPictureState.setVisibility(View.GONE);
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addPictureChoose(View view,int position){
//        this.mPictureChoose.add(position);
        this.mPictureChooseMap.get(mCurrentFolder).add(mDataset.get(position));

        FrameLayout pictureState = (FrameLayout)view.findViewById(R.id.pi_picture_choose_item_select);
        pictureState.setVisibility(View.VISIBLE);
    }

    public void removePictureChoose(View view,int position){
        FrameLayout pictureState = (FrameLayout)view.findViewById(R.id.pi_picture_choose_item_select);
//        this.mPictureChoose.remove(position);
        this.mPictureChooseMap.get(mCurrentFolder).remove(mDataset.get(position));
        pictureState.setVisibility(View.GONE);
    }

    public void clearPictureChoose(){
        this.mPictureChoose.clear();
    }

    public int pictureChooseSize(){
        int size = 0;
        //return this.mPictureChoose.size();
        for(Map.Entry<String,List<Picture>> entry : mPictureChooseMap.entrySet())
        {
            size += entry.getValue().size();
        }
        return size;
    }

    public ArrayList<Picture> getPictureChoosePath(){
        ArrayList<Picture> pictures = new ArrayList<>();
        for (int position:mPictureChoose){
            pictures.add(mDataset.get(position));
        }

        ArrayList<Picture> selectedPictures = new ArrayList<>();
        for (Map.Entry<String,List<Picture>> entry : mPictureChooseMap.entrySet()) {
            selectedPictures.addAll(entry.getValue());
        }

        return selectedPictures;
    }

    public List<Picture> getmPictureList() {
        return mDataset;
    }

    public void setmPictureList(List<Picture> mPictureList) {
        this.mDataset = mPictureList;
    }

    public boolean contains(int position){
        //return this.mPictureChoose.contains(position);
        return this.mPictureChooseMap.get(mCurrentFolder).contains(mDataset.get(position));
    }

    public void changeFolder(String folder)
    {
        this.mCurrentFolder = folder;

        Boolean found = false;
        for (Map.Entry<String,List<Picture>> entry : mPictureChooseMap.entrySet()) {
            if(entry.getKey().equals(folder))
                found = true;
        }

        if(!found)
        {
            mPictureChooseMap.put(folder,new ArrayList<Picture>());
        }
    }

}
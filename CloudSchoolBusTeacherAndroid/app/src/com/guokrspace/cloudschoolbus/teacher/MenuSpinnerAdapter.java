package com.guokrspace.cloudschoolbus.teacher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kai on 9/18/15.
 */
public class MenuSpinnerAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<MessageType> mDataSet;
    LayoutInflater mInflater;

    public MenuSpinnerAdapter(Context mContext, ArrayList<MessageType> mDataSet) {
        this.mContext = mContext;
        this.mDataSet = mDataSet;
        this.mInflater = (LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSet.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataSet.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View actionBarView = mInflater.inflate(R.layout.spinner_action_title, null);
        TextView title = (TextView)actionBarView.findViewById(R.id.textViewTitle);
        TextView subTitle = (TextView)actionBarView.findViewById(R.id.textViewSubtitle);
        title.setText(mContext.getResources().getString(R.string.module_explore));
        subTitle.setText(mDataSet.get(i).description);

        return actionBarView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View dropDownView = mInflater.inflate(R.layout.spinner_action_item, null);
        TextView title = (TextView)dropDownView.findViewById(R.id.textViewDesc);
        ImageView icon = (ImageView)dropDownView.findViewById(R.id.imageViewIcon);
        title.setText(mDataSet.get(position).description);
        if(position==0) {
            //workaround to hide the "All" from the dropdown list
            icon.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
        }else{
            icon.setBackgroundResource(mDataSet.get(position).iconRes);
        }

        return dropDownView;
    }

    public static class MessageType
    {
        public String messageType;
        public String description;
        public int iconRes;
    }
}

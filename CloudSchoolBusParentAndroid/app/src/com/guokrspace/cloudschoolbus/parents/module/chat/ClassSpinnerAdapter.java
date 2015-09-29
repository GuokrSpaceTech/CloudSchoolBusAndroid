package com.guokrspace.cloudschoolbus.parents.module.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntityT;

import java.util.ArrayList;

/**
 * Created by kai on 9/18/15.
 */
public class ClassSpinnerAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<ClassEntityT> mDataSet;
    LayoutInflater mInflater;

    public ClassSpinnerAdapter(Context mContext, ArrayList<ClassEntityT> mDataSet) {
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
        View actionBarView = mInflater.inflate(R.layout.spinner_class_action_title, null);
        TextView title = (TextView)actionBarView.findViewById(R.id.textViewTitle);
        title.setText(mContext.getResources().getString(R.string.action_contact, mDataSet.get(i).getClassname()));

        return actionBarView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View dropDownView = mInflater.inflate(R.layout.spinner_class_action_item, null);
        TextView title = (TextView)dropDownView.findViewById(R.id.textViewDesc);
        title.setText(mDataSet.get(position).getClassname());

        return dropDownView;
    }
}

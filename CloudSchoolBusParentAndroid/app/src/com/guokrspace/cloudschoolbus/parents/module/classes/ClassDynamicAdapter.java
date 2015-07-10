package com.guokrspace.cloudschoolbus.parents.module.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guokrspace.cloudschoolbus.parents.R;

import org.askerov.dynamicgrid.BaseDynamicGridAdapter;

import java.util.List;

public class ClassDynamicAdapter extends BaseDynamicGridAdapter {
    public ClassDynamicAdapter(Context context, List<?> items, int columnCount) {
        super(context, items, columnCount);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ClassViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_class_grid_item, null);
            holder = new ClassViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ClassViewHolder) convertView.getTag();
        }

        ClassModule classModule = (ClassModule)getItem(position);

        holder.build(classModule.getTitle(),classModule.getImageRes());
        return convertView;
    }

    private class ClassViewHolder {
        private TextView titleText;
        private ImageView image;

        private ClassViewHolder(View view) {
            titleText = (TextView) view.findViewById(R.id.item_title);
            image = (ImageView) view.findViewById(R.id.item_img);
        }

        void build(String title, int imageRes) {
            titleText.setText(title);
            image.setImageResource(imageRes);
        }
    }
}
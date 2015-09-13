package com.guokrspace.cloudschoolbus.parents.module.classes.photo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.guokrspace.cloudschoolbus.parents.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntity;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 学生选择adapter
 * 
 * @author lenovo
 * 
 */
public class StuSelectAdapter extends BaseAdapter {

	private static final String ALL_SELECT = "all_select";
	private static final String ITEM = "item";

	private Context mContext;
	private ArrayList<StudentEntity> mStudents = new ArrayList<>();
    private HashMap<Integer, StudentEntity> mSelections = new HashMap<>();

	public StuSelectAdapter(Context context, List<StudentEntity> students) {
		mContext = context;
		mStudents = (ArrayList)students;
	}

	@Override
	public int getCount() {
		return mStudents.size() + 1;
	}

	@Override
	public Object getItem(int arg0) {
		if (0 == arg0) {
			return "";
		}
		return mStudents.get(arg0 - 1);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View view, ViewGroup arg2) {

		if (null == view) {
			if (0 == position) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.adapter_stu_select_all, null);
				view.setTag(ALL_SELECT);
			} else if (position > 0) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.adapter_stu_select_item, null);
				view.setTag(ITEM);
			}
		} else {
			if (0 == position && !ALL_SELECT.equals(view.getTag())) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.adapter_stu_select_all, null);
				view.setTag(ALL_SELECT);
			} else if (position > 0 && !ITEM.equals(view.getTag())) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.adapter_stu_select_item, null);
				view.setTag(ITEM);
			}
		}

		if (0 == position) {
			final ImageView allSelectImageView = (ImageView) view
					.findViewById(R.id.allSelectImageView);
			final ViewGroup mainLayout = (ViewGroup) view
					.findViewById(R.id.mainLayout);
			mainLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
                    if(!mSelections.containsKey(0))
                    {
						StudentEntity studentEntity = new StudentEntity(); //Dummy object indicates select all
						mSelections.put(0, studentEntity);
                        allSelectImageView.setVisibility(View.GONE);
                    } else {
						mSelections.remove(0);
                        allSelectImageView.setVisibility(View.VISIBLE);
                    }
                }
			});
		} else if (position > 0) {
            final int studentPosition = position - 1;
			final StudentEntity student = mStudents.get(studentPosition);
			final ImageView selectImageView = (ImageView) view.findViewById(R.id.selectImageView);

			ImageView headImageView = (ImageView) view
					.findViewById(R.id.headImageView);
			if (!TextUtils.isEmpty(student.getAvatar())) {
                Picasso.with(mContext).load(student.getAvatar()).fit().centerCrop().into(headImageView);
			}
			TextView stuNameTextView = (TextView) view
					.findViewById(R.id.stuNameTextView);
			stuNameTextView.setText(student.getCnname());

			ViewGroup mainLayout = (ViewGroup) view
					.findViewById(R.id.mainLayout);
			mainLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
                    if(mSelections.containsKey(position))
                    {
                        selectImageView.setVisibility(View.GONE);
                        mSelections.remove(position);
                    } else {
                        mSelections.put(position,student);
                        selectImageView.setVisibility(View.VISIBLE);
                    }
				}
			});
		}

		return view;
	}

    public HashMap<Integer, StudentEntity> getmSelections() {
        return mSelections;
    }
}

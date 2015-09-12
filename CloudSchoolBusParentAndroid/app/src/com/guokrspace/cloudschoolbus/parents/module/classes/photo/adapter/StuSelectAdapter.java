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

import java.util.Collection;
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
	private CloudSchoolBusParentsApplication mApplication;
	private List<StudentEntity> mStudents;
    private Map<Integer, StudentEntity> mSelections;

	public StuSelectAdapter(Context context, List<StudentEntity> students) {
		mContext = context;
		mStudents = students;
		mApplication = (CloudSchoolBusParentsApplication) mContext
				.getApplicationContext();
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
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		if (null == arg1) {
			if (0 == arg0) {
				arg1 = LayoutInflater.from(mContext).inflate(
						R.layout.adapter_stu_select_all, null);
				arg1.setTag(ALL_SELECT);
			} else if (arg0 > 0) {
				arg1 = LayoutInflater.from(mContext).inflate(
						R.layout.adapter_stu_select_item, null);
				arg1.setTag(ITEM);
			}
		} else {
			if (0 == arg0 && !ALL_SELECT.equals(arg1.getTag())) {
				arg1 = LayoutInflater.from(mContext).inflate(
						R.layout.adapter_stu_select_all, null);
				arg1.setTag(ALL_SELECT);
			} else if (arg0 > 0 && !ITEM.equals(arg1.getTag())) {
				arg1 = LayoutInflater.from(mContext).inflate(
						R.layout.adapter_stu_select_item, null);
				arg1.setTag(ITEM);
			}
		}

		if (0 == arg0) {
			final ImageView allSelectImageView = (ImageView) arg1
					.findViewById(R.id.allSelectImageView);
			final ViewGroup mainLayout = (ViewGroup) arg1
					.findViewById(R.id.mainLayout);
			mainLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
                    if(mSelections==null || mSelections.containsKey(0))
                    {
                        StudentEntity dummyStudent = mSelections.get(0);
                        mSelections.remove(dummyStudent);
//                        allSelectImageView.setVisibility(View.GONE);
                    } else {
                        StudentEntity studentEntity = new StudentEntity(); //Dummy object indicates select all
                        mSelections.put(0,studentEntity);
//                        allSelectImageView.setVisibility(View.VISIBLE);
                    }
                }
			});
		} else if (arg0 > 0) {
            final int studentPosition = arg0 - 1;
			final StudentEntity student = mStudents.get(studentPosition);
			final ImageView selectImageView = (ImageView) arg1.findViewById(R.id.selectImageView);

			if (mSelections==null || mSelections.containsValue(student)) {
				selectImageView.setVisibility(View.VISIBLE);
			} else {
				selectImageView.setVisibility(View.GONE);
			}
			ImageView headImageView = (ImageView) arg1
					.findViewById(R.id.headImageView);
			if (!TextUtils.isEmpty(student.getAvatar())) {
                Picasso.with(mContext).load(student.getAvatar()).fit().centerCrop().into(headImageView);
			}
			TextView stuNameTextView = (TextView) arg1
					.findViewById(R.id.stuNameTextView);
			stuNameTextView.setText(student.getCnname());

			ViewGroup mainLayout = (ViewGroup) arg1
					.findViewById(R.id.mainLayout);
			mainLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
                    if(mSelections==null || mSelections.containsValue(student))
                    {
                        selectImageView.setVisibility(View.GONE);
                        mSelections.remove(student);
                    } else {
                        mSelections.put(studentPosition,student);
                        selectImageView.setVisibility(View.VISIBLE);
                    }
				}
			});
		}

		return arg1;
	}

    public Map<Integer, StudentEntity> getmSelections() {
        return mSelections;
    }
}

package com.guokrspace.cloudschoolbus.parents.module.photo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntityT;
import com.guokrspace.cloudschoolbus.parents.module.photo.SelectStudentActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	private ArrayList<StudentEntityT> mStudents = new ArrayList<>();
    private HashMap<Integer, StudentEntityT> mSelections = new HashMap<>();

	public StuSelectAdapter(Context context, List<StudentEntityT> students) {
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
						StudentEntityT studentEntity = new StudentEntityT(); //Dummy object indicates select all
						mSelections.put(0, studentEntity);
						selectAllStudents();
						notifyDataSetChanged();
//                        allSelectImageView.setVisibility(View.GONE);
                    } else {
						mSelections.clear();
//                        allSelectImageView.setVisibility(View.VISIBLE);
                        notifyDataSetChanged();
                    }

                    if(getmSelections().size()!=0)
                        ((SelectStudentActivity)mContext).mUploadAction.setEnabled(true);
                    else
                        ((SelectStudentActivity)mContext).mUploadAction.setEnabled(false);
                }
			});
		} else if (position > 0) {
            final int studentPosition = position - 1;
			final StudentEntityT student = mStudents.get(studentPosition);
			final ImageView selectImageView = (ImageView) view.findViewById(R.id.selectImageView);

			ImageView headImageView = (ImageView) view
					.findViewById(R.id.headImageView);
			if (!TextUtils.isEmpty(student.getAvatar())) {
				String avatarpath="";
				if(student.getAvatar().contains("jpg."))
					avatarpath=student.getAvatar().substring(0,student.getAvatar().lastIndexOf('.'));
                if(!avatarpath.equals(""))
                    Picasso.with(mContext).load(avatarpath).fit().centerCrop().into(headImageView);
			}
			TextView stuNameTextView = (TextView) view
					.findViewById(R.id.stuNameTextView);
			stuNameTextView.setText(student.getCnname());

			if(mSelections.containsKey(position)) {
				selectImageView.setVisibility(View.VISIBLE);
			} else {
				selectImageView.setVisibility(View.INVISIBLE);
			}

			ViewGroup mainLayout = (ViewGroup) view
					.findViewById(R.id.mainLayout);
			mainLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (mSelections.containsKey(position)) {
						selectImageView.setVisibility(View.GONE);
						mSelections.remove(position);
					} else {
						mSelections.put(position, student);
						selectImageView.setVisibility(View.VISIBLE);
					}

                    if(getmSelections().size()!=0)
                        ((SelectStudentActivity)mContext).mUploadAction.setEnabled(true);
                    else
                        ((SelectStudentActivity)mContext).mUploadAction.setEnabled(false);


                }
			});
		}

		return view;
	}

    public HashMap<Integer, StudentEntityT> getmSelections() {
        return mSelections;
    }

	private void selectAllStudents()
	{
		int i=1; // 0 - Dummy Student indicating selecting all
		for( StudentEntityT student : mStudents) {
			mSelections.put(i, student);
            i++;
		}
	}
}

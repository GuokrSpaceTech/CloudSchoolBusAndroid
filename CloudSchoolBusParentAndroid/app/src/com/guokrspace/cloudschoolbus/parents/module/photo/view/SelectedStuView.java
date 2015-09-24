package com.guokrspace.cloudschoolbus.parents.module.photo.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;


import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.BaseLinearLayout;
import com.guokrspace.cloudschoolbus.parents.database.daodb.DaoSession;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntityT;
import com.guokrspace.cloudschoolbus.parents.module.photo.SelectStudentActivity;
import com.guokrspace.cloudschoolbus.parents.module.photo.model.UploadFile;
import com.guokrspace.cloudschoolbus.parents.module.photo.adapter.StuSelectAdapter;

import net.soulwolf.image.picturelib.model.Picture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 选择同学发送文件
 * 
 * @author lenovo
 * 
 */
public class SelectedStuView extends BaseLinearLayout {

	private GridView mGridView;
	private StuSelectAdapter mStuSelectAdapter;
	private AdapterView.OnItemClickListener mItemClickListener;


	private List<StudentEntityT> mStudents;

	private List<UploadFile> mUploadFiles = new ArrayList<UploadFile>();
	/** 0表示所有照片，1表示相机拍摄 */


	public SelectedStuView(Context context,  AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		init();
	}

	public SelectedStuView(Context context, List<StudentEntityT> students) {
		super(context);
        mContext = context;
		mStudents = students;

		init();
	}

	private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.view_selected_stu, this, true);

        mGridView = (GridView) findViewById(R.id.gridView);
        mGridView.setHorizontalSpacing(1);
        mGridView.setVerticalSpacing(2);
        mStuSelectAdapter = new StuSelectAdapter(mContext, mStudents);
        mGridView.setAdapter(mStuSelectAdapter);
    }

	public AdapterView.OnItemClickListener getmItemClickListener() {
		return mItemClickListener;
	}

	public void setmItemClickListener(AdapterView.OnItemClickListener mItemClickListener) {
		this.mItemClickListener = mItemClickListener;
		mGridView.setOnItemClickListener(mItemClickListener);
	}

	public void updateStudentSelectedDb(String pickey) {
        DaoSession db = ((SelectStudentActivity)mContext).mApplication.mDaoSession;
        HashMap map = mStuSelectAdapter.getmSelections();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            if(pair.getKey() == 0) //Select all students
            {
                for(StudentEntityT student:mStudents)
                {
                    student.setPickey(pickey);
                    db.getStudentEntityTDao().update(student);
                }

            } else {
                StudentEntityT student = (StudentEntityT)pair.getValue();
                student.setPickey(pickey);
                db.getStudentEntityTDao().update(student);
            }
        }
    }

    public boolean hasSelection()
    {
        return (mStuSelectAdapter.getmSelections().isEmpty());
    }
}

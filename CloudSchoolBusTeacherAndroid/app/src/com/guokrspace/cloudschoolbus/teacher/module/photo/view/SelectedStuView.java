package com.guokrspace.cloudschoolbus.teacher.module.photo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.GridView;


import com.guokrspace.cloudschoolbus.teacher.R;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.DaoSession;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.StudentEntityT;
import com.guokrspace.cloudschoolbus.teacher.module.photo.SelectStudentActivity;
import com.guokrspace.cloudschoolbus.teacher.module.photo.adapter.StudentSelectAdapter;

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
	private StudentSelectAdapter mStudentSelectAdapter;
	private AdapterView.OnItemClickListener mItemClickListener;


	private List<StudentEntityT> mStudents;


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
        mStudentSelectAdapter = new StudentSelectAdapter(mContext, mStudents);
        mGridView.setAdapter(mStudentSelectAdapter);
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
        HashMap map = mStudentSelectAdapter.getmSelections();
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
        return (mStudentSelectAdapter.getmSelections().isEmpty());
    }
}

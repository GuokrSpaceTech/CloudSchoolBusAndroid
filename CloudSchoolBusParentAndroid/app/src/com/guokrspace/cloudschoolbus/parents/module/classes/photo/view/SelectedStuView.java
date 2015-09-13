package com.guokrspace.cloudschoolbus.parents.module.classes.photo.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;


import com.android.support.debug.DebugLog;
import com.android.support.handlerui.HandlerToastUI;
import com.android.support.utils.ScreenUtils;
import com.android.support.utils.ToolUtils;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.BaseLinearLayout;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntity;
import com.guokrspace.cloudschoolbus.parents.entity.UploadFile;
import com.guokrspace.cloudschoolbus.parents.module.classes.photo.adapter.ImageThumbRecycleViewAdapter;
import com.guokrspace.cloudschoolbus.parents.module.classes.photo.adapter.StuSelectAdapter;

import net.soulwolf.image.picturelib.model.Picture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
	private TextView mContentTextView;
	private TextView mPictureNumberTextView;


	private List<StudentEntity> mStudents;
	private List<Picture> mPictures;

	private List<UploadFile> mUploadFiles = new ArrayList<UploadFile>();
	/** 0表示所有照片，1表示相机拍摄 */


	public SelectedStuView(Context context,  AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		init();
	}

	public SelectedStuView(Context context, List<Picture> pictures, List<StudentEntity> students) {
		super(context);
        mContext = context;
		mPictures = pictures;
		mStudents = students;

		init();
	}

	private void init() {
		LayoutInflater.from(mContext).inflate(R.layout.view_selected_stu, this, true);


		ViewGroup pagerLayout = (ViewGroup) findViewById(R.id.pagerLayout);
		int height = (ScreenUtils.getScreenBounds(mContext)[1] - ToolUtils.dipToPx(mContext, 45)) / 2;
//		DebugLog.logI("viewpager height : " + height);
//		pagerLayout.setLayoutParams(new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.MATCH_PARENT, height));

		mContentTextView = (TextView) findViewById(R.id.contentTextView);
		mPictureNumberTextView = (TextView) findViewById(R.id.pictureNumberTextView);
		mPictureNumberTextView.setText("1/" + mPictures.size());

		mGridView = (GridView) findViewById(R.id.gridView);
		//Test Purpose
		ArrayList<StudentEntity> studentEntities = new ArrayList<>();
		studentEntities.addAll(mStudents);
		studentEntities.addAll(mStudents);
		studentEntities.addAll(mStudents);
		studentEntities.addAll(mStudents);
		studentEntities.addAll(mStudents);
		studentEntities.addAll(mStudents);
		studentEntities.addAll(mStudents);
		mStuSelectAdapter = new StuSelectAdapter(mContext, studentEntities);
		mGridView.setAdapter(mStuSelectAdapter);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 0: {
			// 查看大图返回
			switch (resultCode) {
			case Activity.RESULT_OK: {
				// 删除图片成功返回
				if (null != data) {
					int delPicIndex = data.getIntExtra("DelPicIndex", -1);
					if (-1 != delPicIndex) {
						mPictures.remove(delPicIndex);
						mUploadFiles.remove(delPicIndex);

						if (0 == mPictures.size()) {
							((Activity) mContext).finish();
							return;
						} else {
						}
					}
				}
				break;
			}
			case Activity.RESULT_CANCELED: {
				break;
			}
			default:
				break;
			}
			break;
		}
		default:
			break;
		}
	}

    public String getSelectionString() {
        String retStr="";
        HashMap map = mStuSelectAdapter.getmSelections();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            if(pair.getKey() == 0) //Select all students
            {
                for(StudentEntity entity:mStudents)
                    retStr += entity.getStudentid() + ",";
            } else {
                retStr += ((StudentEntity)pair.getValue()).getStudentid() + ",";
            }
        }

        return retStr.substring(0, retStr.lastIndexOf(',') - 1);
    }
}

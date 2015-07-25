package com.guokrspace.cloudschoolbus.parents.base.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.support.debug.DebugLog;
import com.android.support.dialog.CustomWaitDialog;
import com.android.support.dialog.CustomWaitDialog.OnKeyCancel;
import com.guokrspace.cloudschoolbus.parents.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.parents.entity.Classinfo;
import com.guokrspace.cloudschoolbus.parents.entity.Student;

import java.util.ArrayList;
import java.util.List;

public class BaseFragment extends Fragment {
	
	protected Context mParentContext;
	protected Fragment mFragment;
	
	protected CloudSchoolBusParentsApplication mApplication;

	private CustomWaitDialog mCustomWaitDialog;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mParentContext = activity;
		mFragment = this;
		mApplication = (CloudSchoolBusParentsApplication) mParentContext
				.getApplicationContext();
		DebugLog.setTag(mFragment.getClass().getName());
	}
	

	protected void setViewData(View view) {
		
	}
	
	protected void setListener(View view) {

	}

	protected void setTitleNavBar(View view) {

	}
	
	/**
	 * 显示等待对话框
	 * 
	 * @param messageString
	 * @param
	 */
	protected void showWaitDialog(String messageString,
			final OnKeyCancel onKeyCancel) {
		if (null == mCustomWaitDialog) {
			mCustomWaitDialog = new CustomWaitDialog(mParentContext,
					com.android.support.R.style.CustomWaitDialog);
			mCustomWaitDialog.setCancelable(true);
			mCustomWaitDialog.setCanceledOnTouchOutside(false);
			mCustomWaitDialog.setMessage(messageString);
			mCustomWaitDialog.setOnKeyCancelListener(new OnKeyCancel() {

				@Override
				public void onKeyCancelListener() {
					if (null != onKeyCancel) {
						onKeyCancel.onKeyCancelListener();
					}
					if (null != mCustomWaitDialog) {
						mCustomWaitDialog.cancel();
						mCustomWaitDialog = null;
					}
				}
			});
			mCustomWaitDialog.show();
		}
	}

	protected void hideWaitDialog() {
		if (null != mCustomWaitDialog) {
			mCustomWaitDialog.cancel();
			mCustomWaitDialog = null;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(null != savedInstanceState){
			DebugLog.logI("Activity onRestoreInstanceState");
	//		HandlerToastUI.getHandlerToastUI(mContext.getApplicationContext(), "onRestoreInstanceState");
//			NetworkClient.getNetworkClient().setLoginToken(savedInstanceState.getString("loginToken"));
			mApplication.mStudentList = (List<Student>) savedInstanceState.getSerializable("StudentList");
//			mApplication.mTeacher = (Teacher) savedInstanceState.getSerializable("Teacher");
//			mApplication.mClassInfo = (Classinfo) savedInstanceState.getSerializable("ClassInfo");
//			mApplication.mLoginSetting = (LoginSetting) savedInstanceState.getSerializable("LoginSetting");
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		
		DebugLog.logI("Fragment onSaveInstanceState");
//		HandlerToastUI.getHandlerToastUI(mContext.getApplicationContext(), "onSaveInstanceState");
//		outState.putString("loginToken", NetworkClient.getNetworkClient().getLoginToken());
		outState.putSerializable("StudentList", (ArrayList<Student>) mApplication.mStudentList);
//		outState.putSerializable("Teacher", mApplication.mTeacher);
//		outState.putSerializable("ClassInfo", mApplication.mClassInfo);
//		outState.putSerializable("LoginSetting", mApplication.mLoginSetting);
		
		super.onSaveInstanceState(outState);
	}
	
}

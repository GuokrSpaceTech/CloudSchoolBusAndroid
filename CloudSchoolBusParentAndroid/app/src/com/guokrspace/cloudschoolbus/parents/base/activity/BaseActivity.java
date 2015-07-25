package com.guokrspace.cloudschoolbus.parents.base.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.ActionBarContainer;


import com.android.support.debug.DebugLog;
import com.guokrspace.cloudschoolbus.parents.CloudSchoolBusParentsApplication;
import com.android.support.dialog.*;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.entity.Classinfo;
import com.guokrspace.cloudschoolbus.parents.entity.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * activity基类
 * 
 * @author lenovo
 * 
 */
abstract public class BaseActivity extends ActionBarActivity {

	protected Context mContext;

	public CloudSchoolBusParentsApplication mApplication;

	private CustomWaitDialog mCustomWaitDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFormat(PixelFormat.RGBA_8888);

		mContext = this;

		mApplication = (CloudSchoolBusParentsApplication) mContext
				.getApplicationContext();

		DebugLog.setTag(mContext.getClass().getName());
	}

	protected void init() {

	}

	protected void setListener() {

	}

	protected void setTitleNavBar() {

	}

	/**
	 * 显示等待对话框
	 *
	 * @param messageString
	 * @param onDismissListener
	 */
	protected void showWaitDialog(String messageString,
			final CustomWaitDialog.OnKeyCancel onKeyCancel) {
		if (null == mCustomWaitDialog) {
			mCustomWaitDialog = new CustomWaitDialog(mContext,
					com.android.support.R.style.CustomWaitDialog);
			mCustomWaitDialog.setCancelable(true);
			mCustomWaitDialog.setCanceledOnTouchOutside(false);
			mCustomWaitDialog.setMessage(messageString);
			mCustomWaitDialog.setOnKeyCancelListener(new CustomWaitDialog.OnKeyCancel() {

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

	/**
	 * 判断网络连接监听
	 */
	private BroadcastReceiver mNetConnectBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent
					.getAction())) {
//				UploadFileUtils.getUploadUtils().setContext(mContext);
//				UploadFileUtils.getUploadUtils().uploadFileService();
			}
		}
	};
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		DebugLog.logI("Activity onSaveInstanceState");
//		HandlerToastUI.getHandlerToastUI(mContext.getApplicationContext(), "onSaveInstanceState");
//		outState.putString("loginToken", NetworkClient.getNetworkClient().getLoginToken());
		outState.putSerializable("StudentList", (ArrayList<Student>) mApplication.mStudentList);
//		outState.putSerializable("ClassInfo", mApplication.mClassInfo);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		
		super.onRestoreInstanceState(savedInstanceState);
		if(null != savedInstanceState){
			DebugLog.logI("Activity onRestoreInstanceState");
	//		HandlerToastUI.getHandlerToastUI(mContext.getApplicationContext(), "onRestoreInstanceState");
//			NetworkClient.getNetworkClient().setLoginToken(savedInstanceState.getString("loginToken"));
			mApplication.mStudentList = (List<Student>) savedInstanceState.getSerializable("StudentList");
//			mApplication.mClassInfo = (Classinfo) savedInstanceState.getSerializable("ClassInfo");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// 注册广播
//		if (!(this instanceof LoginActivity) && !(this instanceof InitActivity)) {
//			IntentFilter netConnectIntentFilter = new IntentFilter(
//					ConnectivityManager.CONNECTIVITY_ACTION);
//			mContext.registerReceiver(mNetConnectBroadcastReceiver,
//					netConnectIntentFilter);
//		}else {
////			DebugLog.logI("onResume LoginActivity InitActivity");
//		}
//		MobclickAgent.onResumeume(this);
	}

	@Override
	public void onPause() {
        super.onPause();
//		if (!(this instanceof LoginActivity) && !(this instanceof InitActivity)) {
//			mContext.unregisterReceiver(mNetConnectBroadcastReceiver);
//		}else {
////			DebugLog.logI("onPause LoginActivity InitActivity");
//		}
//
////		MobclickAgent.onPause(this);
//	}
    }
}

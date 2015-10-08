package com.guokrspace.cloudschoolbus.parents.base.activity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;


import com.alibaba.fastjson.JSONException;
import com.android.support.debug.DebugLog;
import com.guokrspace.cloudschoolbus.parents.CloudSchoolBusParentsApplication;
import com.android.support.dialog.*;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntityDao;
import com.guokrspace.cloudschoolbus.parents.event.NetworkStatusEvent;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.otto.Subscribe;

import org.apache.http.Header;

import java.util.ArrayList;

import de.greenrobot.dao.query.QueryBuilder;

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

		mApplication.networkStatusEvent = new NetworkStatusEvent(false,false,false);
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo        mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo        activeInfo = manager.getActiveNetworkInfo();
		if(activeInfo!=null)
		{
			mApplication.networkStatusEvent.setIsNetworkConnected(true);
			if(wifiInfo.isConnected())
				mApplication.networkStatusEvent.setIsWifiConnected(true);
			if(mobileInfo.isConnected())
				mApplication.networkStatusEvent.setIsMobileNetworkConnected(true);
		}

		DebugLog.setTag(mContext.getClass().getName());
	}

	protected void init() {

	}

	/**
	 * 显示等待对话框
	 *
	 * @param messageString
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
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
        DebugLog.logI("Activity onSaveInstanceState");

////		HandlerToastUI.getHandlerToastUI(mContext.getApplicationContext(), "onSaveInstanceState");
//		outState.putSerializable("mConfig", mApplication.mConfig);
//		outState.putSerializable("mSchools", (ArrayList<SchoolEntity>)mApplication.mSchools);
//		outState.putSerializable("mClasses", (ArrayList<ClassEntity>)mApplication.mClasses);
//		outState.putSerializable("mTeachers",(ArrayList<TeacherEntity>)mApplication.mTeachers);
//		outState.putSerializable("mStudents",(ArrayList<StudentEntity>)mApplication.mStudents);

		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		
		super.onRestoreInstanceState(savedInstanceState);

        mApplication.initDB();

        mApplication.initConfig();

        mApplication.initBaseinfo();

        mApplication.initCacheFile();

//		if(null != savedInstanceState){
//			DebugLog.logI("Activity onRestoreInstanceState");
//			mApplication.mConfig = (ConfigEntity)savedInstanceState.getSerializable("mConfig");
//			mApplication.mSchools = (List<SchoolEntity>)savedInstanceState.getSerializable("mSchools");
//			mApplication.mClasses = (List<ClassEntity>)savedInstanceState.getSerializable("mClasses");
//			mApplication.mTeachers = (List<TeacherEntity>)savedInstanceState.getSerializable("mTeachers");
//			mApplication.mStudents = (List<StudentEntity>)savedInstanceState.getSerializable("mStudents");
//		}
	}

	@Subscribe
	public void renew_sid() throws JSONException {

		if(!mApplication.networkStatusEvent.isNetworkConnected()) {
			return;
		}

		RequestParams params = new RequestParams();
		params.put("token", mApplication.mConfig.getToken());

		CloudSchoolBusRestClient.post("login", params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, org.json.JSONObject response) {
				String retCode = "";

				for (int i = 0; i < headers.length; i++) {
					Header header = headers[i];
					if ("code".equalsIgnoreCase(header.getName())) {
						retCode = header.getValue();
						break;
					}
				}

				if (!retCode.equals("1")) {
					return;
				} else {
					try {
						String sid = response.getString("sid");
						String userid = response.getString("userid");
						String imToken = response.getString("imToken");
						ConfigEntityDao configEntityDao = mApplication.mDaoSession.getConfigEntityDao();
						ConfigEntity oldConfigEntity = configEntityDao.queryBuilder().limit(1).list().get(0);
						ConfigEntity configEntity = new ConfigEntity(null, mApplication.mConfig.getToken(), sid, mApplication.mConfig.getMobile(), userid, imToken, oldConfigEntity.getCurrentChild());
						configEntityDao.update(configEntity);
					} catch (org.json.JSONException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, org.json.JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	public Fragment getCurrentFragment(){
		FragmentManager fragmentManager = getSupportFragmentManager();
		String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
		Fragment currentFragment = getSupportFragmentManager()
				.findFragmentByTag(fragmentTag);
		return currentFragment;
	}

	//Get filtered messages from cache
	public ArrayList<MessageEntity> GetMessageFromCache(String messageType) {
		MessageEntityDao messageEntityDao = mApplication.mDaoSession.getMessageEntityDao();
		QueryBuilder queryBuilder = messageEntityDao.queryBuilder();
		return (ArrayList<MessageEntity>)queryBuilder.where(MessageEntityDao.Properties.Apptype.eq(messageType)).list();
	}

	//Get all messages from cache
	private ArrayList<MessageEntity> GetMessageFromCache() {
		MessageEntityDao messageEntityDao = mApplication.mDaoSession.getMessageEntityDao();
		QueryBuilder queryBuilder = messageEntityDao.queryBuilder();
		return (ArrayList<MessageEntity>)queryBuilder.list();
	}
}

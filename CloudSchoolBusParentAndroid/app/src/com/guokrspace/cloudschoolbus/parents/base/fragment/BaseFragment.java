package com.guokrspace.cloudschoolbus.parents.base.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;

import com.android.support.debug.DebugLog;
import com.android.support.dialog.CustomWaitDialog;
import com.android.support.dialog.CustomWaitDialog.OnKeyCancel;
import com.android.support.utils.DateUtils;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.dexafree.materialList.controller.CommonRecyclerItemClickListener;
import com.guokrspace.cloudschoolbus.parents.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.parents.MainActivity;
import com.guokrspace.cloudschoolbus.parents.R;
import com.android.support.fastjson.FastJsonTools;
import com.guokrspace.cloudschoolbus.parents.base.DataWrapper;
import com.guokrspace.cloudschoolbus.parents.base.ServerInteractions;
import com.guokrspace.cloudschoolbus.parents.base.include.HandlerConstant;
import com.guokrspace.cloudschoolbus.parents.base.include.Version;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ConfigEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ParentEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SchoolEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SenderEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SenderEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentClassRelationEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntityT;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentParentRelationEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TagEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TagEntityDao;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherDutyClassRelationEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntityT;
import com.guokrspace.cloudschoolbus.parents.entity.ActivityBody;
import com.guokrspace.cloudschoolbus.parents.entity.AttendanceRecord;
import com.guokrspace.cloudschoolbus.parents.entity.Food;
import com.guokrspace.cloudschoolbus.parents.entity.Ipcparam;
import com.guokrspace.cloudschoolbus.parents.entity.NoticeBody;
import com.guokrspace.cloudschoolbus.parents.entity.Schedule;
import com.guokrspace.cloudschoolbus.parents.entity.StudentReport;
import com.guokrspace.cloudschoolbus.parents.entity.Timeline;
import com.guokrspace.cloudschoolbus.parents.event.BusProvider;
import com.guokrspace.cloudschoolbus.parents.event.InfoSwitchedEvent;
import com.guokrspace.cloudschoolbus.parents.event.SidExpireEvent;
import com.guokrspace.cloudschoolbus.parents.module.classes.Streaming.StreamingChannelsFragment;
import com.guokrspace.cloudschoolbus.parents.module.explore.adapter.ImageAdapter;
import com.guokrspace.cloudschoolbus.parents.module.explore.adapter.TagRecycleViewAdapter;
import com.guokrspace.cloudschoolbus.parents.protocols.CloudSchoolBusRestClient;
import com.guokrspace.cloudschoolbus.parents.protocols.ProtocolDef;
import com.guokrspace.cloudschoolbus.parents.widget.ActivityCard;
import com.guokrspace.cloudschoolbus.parents.widget.AttendanceRecordCard;
import com.guokrspace.cloudschoolbus.parents.widget.FoodNoticeCard;
import com.guokrspace.cloudschoolbus.parents.widget.NoticeCard;
import com.guokrspace.cloudschoolbus.parents.widget.PictureCard;
import com.guokrspace.cloudschoolbus.parents.widget.ReportListCard;
import com.guokrspace.cloudschoolbus.parents.widget.ScheduleNoticeCard;
import com.guokrspace.cloudschoolbus.parents.widget.StreamingNoticeCard;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import de.greenrobot.dao.query.QueryBuilder;

public class BaseFragment extends Fragment {
	
	protected Context mParentContext;
	protected Fragment mFragment;
	
	protected CloudSchoolBusParentsApplication mApplication;
	private   CustomWaitDialog mCustomWaitDialog;
	public    List<MessageEntity> mMesageEntities = new ArrayList<>();

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mParentContext = activity;
		mFragment = this;
		mApplication = (CloudSchoolBusParentsApplication) mParentContext.getApplicationContext();
        ServerInteractions.getInstance().init(mApplication,mParentContext);
        DataWrapper.getInstance().init(mApplication);
		BusProvider.getInstance().register(this);
		DebugLog.setTag(mFragment.getClass().getName());
	}

	@Override
	public void onDetach() {
		super.onDetach();
		BusProvider.getInstance().unregister(this);
	}

	protected void setViewData(View view) {
		
	}
	
	protected void setListener() {

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
			mApplication.mSchools = (List<SchoolEntity>)savedInstanceState.getSerializable("mSchools");
			mApplication.mClasses = (List<ClassEntity>)savedInstanceState.getSerializable("mClasses");
			mApplication.mTeachers = (List<TeacherEntity>)savedInstanceState.getSerializable("mTeachers");
			mApplication.mStudents = (List<StudentEntity>)savedInstanceState.getSerializable("mStudents");
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		
		DebugLog.logI("Fragment onSaveInstanceState");
//		HandlerToastUI.getHandlerToastUI(mContext.getApplicationContext(), "onSaveInstanceState");
		outState.putSerializable("mConfig", mApplication.mConfig);
		outState.putSerializable("mSchools", (ArrayList<SchoolEntity>)mApplication.mSchools);
		outState.putSerializable("mClasses", (ArrayList<ClassEntity>)mApplication.mClasses);
		outState.putSerializable("mTeachers",(ArrayList<TeacherEntity>)mApplication.mTeachers);
		outState.putSerializable("mStudents",(ArrayList<StudentEntity>)mApplication.mStudents);

		super.onSaveInstanceState(outState);
	}
}

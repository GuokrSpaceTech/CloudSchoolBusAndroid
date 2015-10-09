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
import com.guokrspace.cloudschoolbus.parents.base.DataWrapper;
import com.guokrspace.cloudschoolbus.parents.base.ServerInteractions;
import com.guokrspace.cloudschoolbus.parents.database.daodb.ClassEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.MessageEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.SchoolEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.StudentEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.TeacherEntity;
import com.guokrspace.cloudschoolbus.parents.event.BusProvider;

import java.util.ArrayList;
import java.util.List;

public class BaseFragment extends Fragment {

    protected Context mParentContext;
    protected Fragment mFragment;

    protected CloudSchoolBusParentsApplication mApplication;
    private CustomWaitDialog mCustomWaitDialog;
    public List<MessageEntity> mMesageEntities = new ArrayList<>();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mParentContext = activity;
        mFragment = this;
        mApplication = (CloudSchoolBusParentsApplication) mParentContext.getApplicationContext();
        ServerInteractions.getInstance().init(mApplication, mParentContext);
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
        if (null != savedInstanceState) {
            DebugLog.logI("Activity onRestoreInstanceState");
            //		HandlerToastUI.getHandlerToastUI(mContext.getApplicationContext(), "onRestoreInstanceState");
            mApplication.mSchools = (List<SchoolEntity>) savedInstanceState.getSerializable("mSchools");
            mApplication.mClasses = (List<ClassEntity>) savedInstanceState.getSerializable("mClasses");
            mApplication.mTeachers = (List<TeacherEntity>) savedInstanceState.getSerializable("mTeachers");
            mApplication.mStudents = (List<StudentEntity>) savedInstanceState.getSerializable("mStudents");
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        DebugLog.logI("Fragment onSaveInstanceState");
//		HandlerToastUI.getHandlerToastUI(mContext.getApplicationContext(), "onSaveInstanceState");
        outState.putSerializable("mConfig", mApplication.mConfig);
        outState.putSerializable("mSchools", (ArrayList<SchoolEntity>) mApplication.mSchools);
        outState.putSerializable("mClasses", (ArrayList<ClassEntity>) mApplication.mClasses);
        outState.putSerializable("mTeachers", (ArrayList<TeacherEntity>) mApplication.mTeachers);
        outState.putSerializable("mStudents", (ArrayList<StudentEntity>) mApplication.mStudents);

        super.onSaveInstanceState(outState);
    }
}

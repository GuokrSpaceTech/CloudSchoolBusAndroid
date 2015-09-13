package com.guokrspace.cloudschoolbus.parents.module.classes.photo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;


import com.android.support.debug.DebugLog;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.database.daodb.UploadingPhotoEntity;
import com.guokrspace.cloudschoolbus.parents.entity.UploadFile;
import com.guokrspace.cloudschoolbus.parents.module.classes.photo.adapter.UploadQueueAdapter;
import com.guokrspace.cloudschoolbus.parents.module.classes.photo.service.UploadFileHelper;

import java.util.ArrayList;
import java.util.List;

public class UploadListFragment extends BaseFragment {

	/** 更新上传列表 */
	public static final String ACTION_UPDATE_UPLOAD_LIST = "action_update_upload_list";
    private static final int MENU_CONTEXT_DELETE_ID = 0xF;

    public ListView mListView;
	public UploadQueueAdapter mUploadFileAdapter;

	private List<UploadFile> mUploadFiles = new ArrayList<UploadFile>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_upload_list, null);
		setViewData(view);
		return view;
	}

	@Override
	protected void setViewData(View view) {

		mListView = (ListView) view
				.findViewById(R.id.listView);

		UploadFileHelper.getUploadUtils().setContext(mParentContext);
		UploadFileHelper.getUploadUtils().setFragment(mFragment);

		List<UploadFile> uploadFiles = UploadFileHelper.getUploadUtils().getUploadFiles();
		mUploadFiles.clear();
		mUploadFiles.addAll(uploadFiles);

		if (mUploadFiles.size() > 0) {
			haveResult();
		} else {
			noResult();
		}
		mUploadFileAdapter = new UploadQueueAdapter(mParentContext, mUploadFiles);
		mListView.setAdapter(mUploadFileAdapter);
        registerForContextMenu(mListView);

		UploadFileHelper.getUploadUtils().uploadFileService();

		setListener(view);
	}

	private void noResult() {
		mListView.setVisibility(View.GONE);
	}

	private void haveResult() {
		mListView.setVisibility(View.VISIBLE);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home:
            default:
        }

        return super.onOptionsItemSelected(item);
    }

	protected void setListener(View view) {
		mListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
											int arg2, long arg3) {
						if (mUploadFileAdapter.getDeleteUploadFile()) {
							// 删除
							ImageView checkImageView = (ImageView) arg1
									.findViewById(R.id.checkImageView);
							checkImageView.setSelected(!checkImageView
									.isSelected());
							UploadFile uploadFile = mUploadFiles.get(arg2);
							uploadFile.isSelected =
									checkImageView.isSelected();
						} else {
							UploadFile uploadFile = mUploadFiles.get(arg2);
						}
					}
				});
	}


	private BroadcastReceiver mUpdateBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (ACTION_UPDATE_UPLOAD_LIST.equals(intent.getAction())) {
				mUploadFiles.clear();

//				UploadUtils.getUploadUtils().setContext(context);
//				List<UploadFile> uploadFiles = UploadUtils.getUploadUtils()
//						.getUploadFiles();
				UploadFileHelper.getUploadUtils().setContext(context);
				List<UploadFile> uploadFiles = UploadFileHelper.getUploadUtils()
						.getUploadFiles();

				mUploadFiles.addAll(uploadFiles);
				mUploadFileAdapter.notifyDataSetChanged();
				

				if (mUploadFiles.size() > 0) {
					haveResult();
				} else {
					noResult();
				}
			}
		}
	};


	
	/**
	 * 判断网络连接监听
	 */
	private BroadcastReceiver mNetConnectBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
				UploadFileHelper.getUploadUtils().setContext(mParentContext);
				UploadFileHelper.getUploadUtils().setFragment(mFragment);
				UploadFileHelper.getUploadUtils().uploadFileService();
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		// 注册广播
		IntentFilter intentFilter = new IntentFilter(ACTION_UPDATE_UPLOAD_LIST);
		mParentContext.registerReceiver(mUpdateBroadcastReceiver, intentFilter);
		
		DebugLog.logI("UploadListFragment onResume");
		
		IntentFilter netConnectIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		mParentContext.registerReceiver(mNetConnectBroadcastReceiver, netConnectIntentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		mParentContext.unregisterReceiver(mUpdateBroadcastReceiver);
		
		mParentContext.unregisterReceiver(mNetConnectBroadcastReceiver);
		
		DebugLog.logI("UploadListFragment onPause");
	}


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listView) {
//            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
//            String title = ((UploadFile) mUploadFileAdapter.getItem(info.position)).picFileString;
//            menu.setHeaderTitle(getString(R.string.operate));

            menu.add(Menu.NONE, MENU_CONTEXT_DELETE_ID, Menu.NONE, mParentContext.getString(R.string.delete));
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_CONTEXT_DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Log.d("", "removing item pos=" + info.position);

                // 删除线程，和网络请求

                UploadFile uploadFile = (UploadFile)mUploadFileAdapter.getItem(info.position);
                if (null != uploadFile.requestHandle) {
                    DebugLog.logI("uploadFile.requestHandle.cancel(true);");
                    uploadFile.requestHandle.cancel(true);
                }

                // 删除数据库
                List<UploadFile> tempUploadFiles = new ArrayList<UploadFile>();

                tempUploadFiles.add(uploadFile);
                mApplication.mDaoSession.getUploadingPhotosDao().delete(objToDbEntity(uploadFile));

                // 删除内存缓存
                UploadFileHelper.getUploadUtils().remove(uploadFile);

                // 删除列表
                mUploadFiles.removeAll(tempUploadFiles);

                if (0 == mUploadFiles.size()) {

                    mUploadFileAdapter.setDeleteUploadFile(false);
                    noResult();
                }
                mUploadFileAdapter.notifyDataSetChanged();

                UploadFileHelper.getUploadUtils().uploadFileService();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
	private UploadingPhotoEntity objToDbEntity(UploadFile obj)
	{
		UploadingPhotoEntity entity = new UploadingPhotoEntity();
		entity.setClassuid(obj.classuid);
		entity.setIntro(obj.intro);
		entity.setPhotoTag(obj.photoTag);
		entity.setPicFileString(obj.picFileString);
		entity.setPicPathString(obj.picPathString);
		entity.setStudentId(obj.studentIdList);
		entity.setPicSizeString(obj.picSizeString);
		entity.setTeacherid(obj.teacherid);
		return entity;
	}
}

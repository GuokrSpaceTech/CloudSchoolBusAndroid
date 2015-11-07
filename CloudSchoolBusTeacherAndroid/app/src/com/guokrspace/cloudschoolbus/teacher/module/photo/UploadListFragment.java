package com.guokrspace.cloudschoolbus.teacher.module.photo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import com.android.support.debug.DebugLog;
import com.guokrspace.cloudschoolbus.teacher.R;
import com.guokrspace.cloudschoolbus.teacher.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.UploadArticleFileEntity;
import com.guokrspace.cloudschoolbus.teacher.event.BusProvider;
import com.guokrspace.cloudschoolbus.teacher.event.FileUploadedEvent;
import com.guokrspace.cloudschoolbus.teacher.module.photo.adapter.UploadQueueAdapter;
import com.guokrspace.cloudschoolbus.teacher.module.photo.service.UploadFileHelper;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class UploadListFragment extends BaseFragment {

	/** 更新上传列表 */
	public static final String ACTION_UPDATE_UPLOAD_LIST = "action_update_upload_list";
    private static final int MENU_CONTEXT_DELETE_ID = 0xF;

    public ListView mListView;
	public UploadQueueAdapter mUploadFileAdapter;

    boolean mIsBound;

	private List<UploadArticleFileEntity> mUploadQ = new ArrayList<>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_upload_list, null);
		setViewData(view);
        setHasOptionsMenu(true);
		return view;
	}

	@Override
	protected void setViewData(View view) {

		mListView = (ListView) view
				.findViewById(R.id.listView);

//		UploadFileHelper.getInstance().setContext(mParentContext);
//		UploadFileHelper.getInstance().setFragment(mFragment);
		mUploadQ = readUploadFileQ();

		if (mUploadQ.size() > 0) {
			haveResult();
		} else {
			noResult();
		}
		mUploadFileAdapter = new UploadQueueAdapter(mParentContext, mUploadQ);
        mUploadFileAdapter.setmRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadArticleFileEntity uploadFile = (UploadArticleFileEntity)view.getTag();
                uploadFile.setIsSuccess(null);
                mApplication.mDaoSession.getUploadArticleFileEntityDao().update(uploadFile);
                mUploadQ = readUploadFileQ();
                mUploadFileAdapter.setmUploadFiles(mUploadQ);
                mUploadFileAdapter.notifyDataSetChanged();
                mBoundService.retryFailedFile(uploadFile);

            }
        });
		mListView.setAdapter(mUploadFileAdapter);
        registerForContextMenu(mListView);

        //Kickoff the upload process
//        mBoundService.startUploadFileService();

		setListener(view);

        setHasOptionsMenu(true);
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
//                getFragmentManager().popBackStack(); //Do not break, let the activity to finish itself.
            default:
        }

        return super.onOptionsItemSelected(item); // Let the parenting activity handles
    }

	protected void setListener(View view) {
    }

	
	/**
	 * 判断网络连接监听
	 */
	private BroadcastReceiver mNetConnectBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
//				UploadFileHelper.getInstance().setContext(mParentContext);
//				UploadFileHelper.getInstance().setFragment(mFragment);
//				UploadFileHelper.getInstance().startUploadFileService();
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
//		// 注册广播
//		IntentFilter intentFilter = new IntentFilter(ACTION_UPDATE_UPLOAD_LIST);
//		mParentContext.registerReceiver(mUpdateBroadcastReceiver, intentFilter);
		
		DebugLog.logI("UploadListFragment onResume");
		
		IntentFilter netConnectIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		mParentContext.registerReceiver(mNetConnectBroadcastReceiver, netConnectIntentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
//		mParentContext.unregisterReceiver(mUpdateBroadcastReceiver);
		
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
                UploadArticleFileEntity uploadFile = (UploadArticleFileEntity)mUploadFileAdapter.getItem(info.position);
//                if (null != uploadFile.requestHandle) {
//                    DebugLog.logI("uploadFile.requestHandle.cancel(true);");
//                    uploadFile.requestHandle.cancel(true);
//                }

                //Remove the DB Queue
                UploadFileHelper.getInstance().MarkUplodSuccess(uploadFile);


                //Notify the Fragement/Activity to update its ListView
                FileUploadedEvent event = new FileUploadedEvent(uploadFile);
                event.setIsSuccess(true);
                BusProvider.getInstance().post(event);

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Subscribe public void onReceiveFileUploadEvent(FileUploadedEvent event)
    {
        mUploadQ = readUploadFileQ();

        mUploadFileAdapter.setmUploadFiles(mUploadQ);

        mUploadFileAdapter.notifyDataSetChanged();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private UploadFileHelper mBoundService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((UploadFileHelper.LocalBinder)service).getService();

            // Tell the user about this for our demo.
//            Toast.makeText(Binding.this, R.string.local_service_connected,
//                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
//            Toast.makeText(Binding.this, R.string.local_service_disconnected,
//                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        mParentContext.bindService(new Intent(mParentContext, UploadFileHelper.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            mParentContext.unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        doBindService();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        doUnbindService();
    }

    public List<UploadArticleFileEntity> readUploadFileQ ()
    {
        return mApplication.mDaoSession.getUploadArticleFileEntityDao().queryBuilder().list();
    }
}

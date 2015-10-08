package com.guokrspace.cloudschoolbus.parents.module.photo;

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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;


import com.android.support.debug.DebugLog;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.parents.MainActivity;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.parents.database.daodb.UploadArticleEntity;
import com.guokrspace.cloudschoolbus.parents.database.daodb.UploadArticleFileEntity;
import com.guokrspace.cloudschoolbus.parents.event.FileUploadedEvent;
import com.guokrspace.cloudschoolbus.parents.event.IsUploadingEvent;
import com.guokrspace.cloudschoolbus.parents.module.photo.adapter.SentPictureAdapter;
import com.guokrspace.cloudschoolbus.parents.module.photo.adapter.TagDisplyAdapter;
import com.guokrspace.cloudschoolbus.parents.module.photo.service.UploadFileHelper;
import com.guokrspace.cloudschoolbus.parents.widget.PictureSentCard;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class SentRecordFragment extends BaseFragment {

	/** 更新上传列表 */
    private static final int MENU_CONTEXT_DELETE_ID = 0xF;

    public MaterialListView mListView;
	private List<UploadArticleEntity> mUploadQ = new ArrayList<>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_sent_record, null);
		setViewData(view);
        setHasOptionsMenu(true);
		return view;
	}

	@Override
	protected void setViewData(View view) {

		mListView = (MaterialListView) view.findViewById(R.id.material_listview);
		UploadFileHelper.getInstance().setContext(mParentContext);
		UploadFileHelper.getInstance().setFragment(mFragment);
		mUploadQ = UploadFileHelper.getInstance().readUploadArticleQ();
        registerForContextMenu(mListView);

        for(UploadArticleEntity article:mUploadQ) {
            PictureSentCard card = buildSentRecordCard(article);
            mListView.add(card);
        }

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
                getFragmentManager().popBackStack();
                break;
            default:

        }

//        return false;
        return super.onOptionsItemSelected(item); // Let the parenting activity handles PhotoTakenAction
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
				UploadFileHelper.getInstance().setContext(mParentContext);
				UploadFileHelper.getInstance().setFragment(mFragment);
//				UploadFileHelper.getInstance().uploadFileService();
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

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Subscribe public void onReceiveFileUploadEvent(FileUploadedEvent event)
    {
        mUploadQ = UploadFileHelper.getInstance().readUploadArticleQ();
        mListView.clear();
        for(UploadArticleEntity article:mUploadQ) {
            PictureSentCard card = buildSentRecordCard(article);
            mListView.add(card);
        }
    }

    @Subscribe public void onReceiveIsUploadingEvent(IsUploadingEvent event)
    {
        mUploadQ = UploadFileHelper.getInstance().readUploadArticleQ();
        mListView.clear();
        for(UploadArticleEntity article:mUploadQ) {
            PictureSentCard card = buildSentRecordCard(article);
            mListView.add(card);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_teacher, menu);
        ((MainActivity)mParentContext).getSupportActionBar().setTitle(getResources().getString(R.string.upload_record));
//        super.onCreateOptionsMenu(menu, inflater);
    }

    private PictureSentCard buildSentRecordCard(UploadArticleEntity article)
    {
        PictureSentCard card = new PictureSentCard(mParentContext);
        card.setDescription(article.getContent());
        card.setSentTime(article.getSendtime());

        List<String> pictures = new ArrayList<>();
        for(UploadArticleFileEntity uploadFile:article.getUploadArticleFileEntityList())
        {
            pictures.add(uploadFile.getFbody());
        }
        SentPictureAdapter imageAdapter = new SentPictureAdapter(mParentContext,article.getUploadArticleFileEntityList());
        TagDisplyAdapter tagsAdapter = new TagDisplyAdapter(article.getTagsEntityTList());

        card.setImageAdapter(imageAdapter);
        card.setTagAdapter(tagsAdapter);

        return card;
    }

}
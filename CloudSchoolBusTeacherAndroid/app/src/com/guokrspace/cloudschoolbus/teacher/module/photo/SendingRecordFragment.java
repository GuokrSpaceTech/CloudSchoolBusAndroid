package com.guokrspace.cloudschoolbus.teacher.module.photo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;


import com.android.support.debug.DebugLog;
import com.dexafree.materialList.view.MaterialListView;
import com.guokrspace.cloudschoolbus.teacher.MainActivity;
import com.guokrspace.cloudschoolbus.teacher.R;
import com.guokrspace.cloudschoolbus.teacher.base.fragment.BaseFragment;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.TagsEntityT;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.TagsEntityTDao;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.UploadArticleEntity;
import com.guokrspace.cloudschoolbus.teacher.database.daodb.UploadArticleFileEntity;
import com.guokrspace.cloudschoolbus.teacher.event.FileUploadedEvent;
import com.guokrspace.cloudschoolbus.teacher.event.IsUploadingEvent;
import com.guokrspace.cloudschoolbus.teacher.module.photo.adapter.SentPictureAdapter;
import com.guokrspace.cloudschoolbus.teacher.module.photo.adapter.TagDisplyAdapter;
import com.guokrspace.cloudschoolbus.teacher.module.photo.service.UploadFileHelper;
import com.guokrspace.cloudschoolbus.teacher.widget.PictureSentCard;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class SendingRecordFragment extends BaseFragment {

	/** 更新上传列表 */
    private static final int MENU_CONTEXT_DELETE_ID = 0xF;

    public MaterialListView mListView;
    private Fragment mFragment;
    private List<UploadArticleEntity> mUploadQ = new ArrayList<>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_sent_record, null);
        setHasOptionsMenu(true);
        setViewData(view);
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

        TextView promptTextview = (TextView)view.findViewById(R.id.prompt);
        if(mUploadQ.size()==0)
        {
            promptTextview.setVisibility(View.VISIBLE);
            promptTextview.setText(mParentContext.getResources().getString(R.string.allisuploaded));
        } else {
            promptTextview.setVisibility(View.GONE);
        }

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
//        inflater.inflate(R.menu.main_teacher, menu);
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

        List<TagsEntityT> tagEntities = new ArrayList<>();
        if (article.getTagids() != null && article.getTagids().contains(",")) {
            String tagids[] = article.getTagids().split(",");
            for (String tagid : tagids) {
                tagEntities.addAll(mApplication.mDaoSession.getTagsEntityTDao().queryBuilder().where(TagsEntityTDao.Properties.Tagid.eq(tagid)).list());
            }
        }
        TagDisplyAdapter tagsAdapter = new TagDisplyAdapter(tagEntities);

        card.setImageAdapter(imageAdapter);
        card.setTagAdapter(tagsAdapter);

        return card;
    }

}

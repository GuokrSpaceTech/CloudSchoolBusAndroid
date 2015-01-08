package com.Manga.Activity.myChildren.Shuttlebus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.adapter.ShuttlebusStopNoticeAdapter;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.myChildren.MyChildrenActivity;
import com.Manga.Activity.utils.ActivityUtil;
import com.cytx.utility.FastJsonTools;
import com.umeng.analytics.MobclickAgent;

public class ShuttlebusNoticeActivity extends BaseActivity {

	static final int MSG_NO_NETWORK    = 1;
	static final int MSG_NO_CONTENT    = 2;
	static final int MSG_RECV_CONTENT  = 3;
	static final int MSG_FOLLOWED      = 4;
	static final int MSG_CONTENT_ERROR = 5;

	private ShuttlebusStopListDto mShuttlebusstopList; 
	private List<ShuttlebusStopNoticeDto> mNoticeList; //Returned from server
	private List<String> mNoticeStringList;            //Received from Bundle
	private ListView     mListViewNotice;
	private ShuttlebusStopNoticeAdapter mAdapter;
	private TextView     mTextViewArrivalDate;
	private TextView     mTextViewTitle;
	private int          mBusStopId;
	private String       mBusStopName;
	
	private Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message mess) {
			switch (mess.what) {
			case MSG_RECV_CONTENT:
				mAdapter.setmListNotice(mNoticeList);
				mAdapter.notifyDataSetChanged();
				break;
			case MSG_NO_NETWORK:
				Toast.makeText(ShuttlebusNoticeActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}		
			return false;
		}
	});
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shuttlebusstop_notice);
		
		ActivityUtil.shuttlebusnoticeactivity = this;
		
		mListViewNotice = (ListView) findViewById(R.id.listview_shuttlebus_notice);
		mTextViewArrivalDate = (TextView) findViewById(R.id.shuttlebus_arrival_time);
		mTextViewTitle = (TextView)findViewById(R.id.shuttlebusstop_notice_title);
		mAdapter = new ShuttlebusStopNoticeAdapter(ShuttlebusNoticeActivity.this);
		mListViewNotice.setAdapter(mAdapter);
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Long timestamp = System.currentTimeMillis();
		String str_date = formatter.format(new Date(timestamp));
		mTextViewArrivalDate.setText(str_date);
		
		//Get the intent
		Intent intent = this.getIntent();
		mNoticeStringList = intent.getStringArrayListExtra("notices");
		mBusStopName      = intent.getStringExtra("busstop");
		mBusStopId        = intent.getIntExtra("id", -1);
		
		//Set the title
		String str_title = mBusStopName + "到站通知";
		mTextViewTitle.setText(str_title);
		
		//Check if this is first time follow or already followed
		if(mNoticeStringList != null) //Already followed
		{
			//Convert the String list to the ShuttlebusStopNoticeDto list
			mNoticeList = new ArrayList<ShuttlebusStopNoticeDto>();
			for(int i=0; i<mNoticeStringList.size(); i++)
			{
			    ShuttlebusStopNoticeDto notice = new ShuttlebusStopNoticeDto();
			    notice.setTime(mNoticeStringList.get(i));
			    mNoticeList.add(notice);
			}
			
			mAdapter.setmListNotice(mNoticeList);
			mAdapter.notifyDataSetChanged();	
		}
		else if(mBusStopId != -1) // Just followed 
		{
			//Get notices from server
			init_data();
		}				
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
	}
	
	//Back button click
	public void dismiss(View view) {
		ActivityUtil.shuttlebusactivity.finish();
		finish();
	}
	
	//Change bus stop button
	public void changeBusStop(View view)
	{
		finish();
	}
	
	//Get the notice list from the server
	public void init_data()
	{
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (HttpUtil.isNetworkConnected(ShuttlebusNoticeActivity.this))
				{
					//Get the notices cause at the time, the parent has followed a bus stop
					Result result = HttpUtil.httpGet(ShuttlebusNoticeActivity.this, new Params("geofenceparents",null));
					if (result == null) {
						handler.sendEmptyMessage(MSG_NO_CONTENT);
					}
					else if ("1".equals(result.getCode()))
					{
						mShuttlebusstopList = FastJsonTools.getObject(result.getContent(), ShuttlebusStopListDto.class);
						mNoticeList = mShuttlebusstopList.getNotice();
					    handler.sendEmptyMessage(MSG_RECV_CONTENT);
				    }
					else
					{
						handler.sendEmptyMessage(MSG_CONTENT_ERROR);
					}
				}
				else
				{
					handler.sendEmptyMessage(MSG_NO_NETWORK);
				}
			}
		});
		
		thread.start();
	}
}

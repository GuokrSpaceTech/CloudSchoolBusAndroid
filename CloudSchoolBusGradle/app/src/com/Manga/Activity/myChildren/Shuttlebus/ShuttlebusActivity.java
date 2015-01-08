package com.Manga.Activity.myChildren.Shuttlebus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.adapter.ShuttlebusStopAdapter;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.myChildren.morningCheck.MoringCheckActivity;
import com.Manga.Activity.utils.ActivityUtil;
import com.cytx.utility.FastJsonTools;
import com.umeng.analytics.MobclickAgent;

public class ShuttlebusActivity extends BaseActivity {
	
	static final int MSG_NO_NETWORK = 1;
	static final int MSG_NO_CONTENT = 2;
	static final int MSG_RECV_CONTENT_1 = 3;
	static final int MSG_RECV_CONTENT_2 = 4;
	static final int MSG_FOLLOWED = 5;
	static final int MSG_FOLLOW_FAILED = 6;
	static final int MSG_NO_SERVICE = 7;
	
	//static final 
	
	ListView mListView;
	ShuttlebusStopListDto mShuttlebusstopList;
	ShuttlebusStopAdapter mAdapter;
	List<ShuttlebusStopNoticeDto> mNoticeList;
	private int mBusStopId = -1;
	private String mBusStopName;
	private Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message mess) {
			switch (mess.what) {
			case MSG_RECV_CONTENT_1: //All info include notices
				if(mBusStopId != -1)
				    mAdapter.setmCurrentBusStopId(mBusStopId);
				
				Intent intent = new Intent(ShuttlebusActivity.this, ShuttlebusNoticeActivity.class);
				Bundle bundle = new Bundle();
				
				//Convert the List<ShuttleBusStopNoticeDto> to ArrayList<String>
				ArrayList<String> strArr = new ArrayList<String>();
				for(int i=0; i< mNoticeList.size(); i++)
				{
					String str_notice = mNoticeList.get(i).getTime();
					strArr.add(str_notice);
				}
				bundle.putStringArrayList("notices", strArr);
				bundle.putString("busstop", mBusStopName);
				intent.putExtras(bundle);

				//Move to notice page
				startActivity(intent);
				break;
			
			case MSG_RECV_CONTENT_2: //Does not include notices 	
			     mAdapter.setmShuttlebusStopList(mShuttlebusstopList.getAllstop());
			     mAdapter.notifyDataSetChanged();	    
			     break;
				
			case MSG_FOLLOWED:
				if(mBusStopId != -1)
				    mAdapter.setmCurrentBusStopId(mBusStopId);
				
				intent = new Intent(ShuttlebusActivity.this, ShuttlebusNoticeActivity.class);
				bundle = new Bundle();				
				bundle.putInt("id",mBusStopId);
				bundle.putString("busstop", mBusStopName);
				intent.putExtras(bundle);
				
				startActivity(intent);
				break;
			case MSG_NO_SERVICE:
				Toast.makeText(ShuttlebusActivity.this, R.string.no_shuttlebus, Toast.LENGTH_SHORT).show();
				break;
			default:
				Toast.makeText(ShuttlebusActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				break;
			}
			return false;
		}
	});
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_shuttlebusstop);
		
		ActivityUtil.shuttlebusactivity = this;
		
		mListView = (ListView)findViewById(R.id.shuttlebusstop_listview);
		mAdapter = new ShuttlebusStopAdapter(ShuttlebusActivity.this);
		mListView.setAdapter(mAdapter);
		mListView.setLongClickable(true);
		mListView.setDivider(null);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				mBusStopId = mShuttlebusstopList.getAllstop().get(arg2).getGeofenceid();
                mBusStopName = mShuttlebusstopList.getAllstop().get(arg2).getName();
                
				followBusStop(mBusStopId);
			}
		});
		
		init_data();
	}
	
	public void onResume() {
		//This handles the situation when user comes back from the notice page
		if(mShuttlebusstopList != null )
			if( mShuttlebusstopList.getAllstop() != null)
			{
				//show all bus stop
			    mAdapter.setmShuttlebusStopList(mShuttlebusstopList.getAllstop());
			    mAdapter.notifyDataSetChanged();
			}
		
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
	}
	
	//Get the shuttle bus stop list data from the server
	private void init_data()
	{
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (HttpUtil.isNetworkConnected(ShuttlebusActivity.this))
				{
					//HashMap<String, String> map = new HashMap<String, String>();
					Result result = HttpUtil.httpGet(ShuttlebusActivity.this, new Params("geofenceparents", null));
					
					if (result == null) {
						handler.sendEmptyMessage(MSG_NO_CONTENT);
					}
					else if ("1".equals(result.getCode())) //Followed, return will include notices
					{
						mShuttlebusstopList = FastJsonTools.getObject(result.getContent(), ShuttlebusStopListDto.class);
						mNoticeList = mShuttlebusstopList.getNotice();
						mBusStopName = mShuttlebusstopList.getCurrentstop();
						mBusStopId = mShuttlebusstopList.getCurrentstopid();
						handler.sendEmptyMessage(MSG_RECV_CONTENT_1);
				    }				
					else if ("2".equals(result.getCode())) //never followed before, return will not include notices
					{
						mShuttlebusstopList = FastJsonTools.getObject(result.getContent(), ShuttlebusStopListDto.class);
						handler.sendEmptyMessage(MSG_RECV_CONTENT_2);
					}
				    else
				    {
					    handler.sendEmptyMessage(MSG_NO_NETWORK);
				    }
			    }
			}
		});
		
		thread.start();
	}
	
	private void followBusStop(final int busStopId)
	{
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (HttpUtil.isNetworkConnected(ShuttlebusActivity.this))
				{
				    HashMap<String, String> map = new HashMap<String, String>();
				    map.put("geofenceid", String.valueOf(busStopId));
				    map.put("state", "1"); // 1: Follow  2: Cancel Follow
				    Result result = HttpUtil.httpPost(ShuttlebusActivity.this, new Params("geofenceparents", map));
				    if(result.getCode().equals("1"))
				    	handler.sendEmptyMessage(MSG_FOLLOWED);
				    else
				    	handler.sendEmptyMessage(MSG_FOLLOW_FAILED);
				}
				else
				{
				    handler.sendEmptyMessage(MSG_NO_NETWORK);
				}			    
			}
		});
		
		thread.start();
	}
	
	/**
	 * 返回键
	 * 
	 * @param v
	 */
	public void backMenu(View v) 
	{
		ActivityUtil.close(this);
	}
}

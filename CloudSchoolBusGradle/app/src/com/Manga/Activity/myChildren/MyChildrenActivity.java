/*
 * Summary: The fourth Tab of the main UI page
 * Functions: 
 *   - Change family members
 *   - Attendance record tracking
 *   - View kid's basic information
 *   - etc
 * Copyright: 2014@Beijing Guokrspace Tech co.,Ltd
 */

package com.Manga.Activity.myChildren;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.base.BaseInfoNewActivity;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.myChildren.DoctorConsult.DoctorActivity;
import com.Manga.Activity.myChildren.Reports.ReportActivity;
import com.Manga.Activity.myChildren.Shuttlebus.ShuttlebusActivity;
import com.Manga.Activity.myChildren.Streaming.IpcSelectionActivity;
import com.Manga.Activity.myChildren.Streaming.Preview;
import com.Manga.Activity.myChildren.Streaming.entity.Dvr;
import com.Manga.Activity.myChildren.Streaming.entity.Ipcparam;
import com.Manga.Activity.myChildren.SwitchChildren.ManageChildrenActivity;
import com.Manga.Activity.myChildren.morningCheck.MoringCheckActivity;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.ImageUtil;
import com.Manga.Activity.utils.Student_Info;
import com.cytx.ConsultActivity;
import com.cytx.utility.FastJsonTools;
import com.umeng.analytics.MobclickAgent;

public class MyChildrenActivity extends BaseActivity {
	private View avatarImg;
	private TextView nickName;
	private TextView tuitionDue;
	private ImageView newReportMark;

	public static String serverStatus;

	private boolean isNetworkWorking = true;
	private boolean isStreamingWorking = false;
	
	public  final static int TIMEOUT = 2;
	public  final static int NEWREPORT = 3;
	private final static int NOSERVERSETTINGS = 4;
	private final static int NONETWORK = 5;
	private final static int GOTSERVERSETTINGS = 6;
	
	public String m_sStreamIP   = "";//221.122.97.78
	public int    m_iStreamPort = 0;
	public String m_sDVRName    = "";
    public List<Dvr> mDvrList = new ArrayList<Dvr>();
    private Ipcparam mIpcparam =  new Ipcparam();


    /*
	 * Network fault handling design, at the point when user clicking the button, following conditions may be met:
	 * 1. No network: 
	 *    Do not let user start play video, prompt user no network.
	 * 2. Having network but no server setting:
	 *    Do not let user start play video, prompt user service is not available.
	 * 3. Having network and has server setting:
	 *    Start play
	 */
	private BroadcastReceiver connectionReceiver = new BroadcastReceiver() { 
		@Override 
		public void onReceive(Context context, Intent intent) { 
		ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE); 
		NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
		NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
		if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) { 
			    Log.i("", "unconnect"); 
			    isNetworkWorking = false; 
			}else { 
				isNetworkWorking = true;
			} 
		} 
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		//Get UI element pointers
		setContentView(R.layout.setting);		
		avatarImg = findViewById(R.id.header_pic_content);
		nickName = (TextView) findViewById(R.id.txt_nikename);
		tuitionDue = (TextView) findViewById(R.id.txt_type);
		newReportMark = (ImageView) findViewById(R.id.image_update);
		
		//Get student info from local DB
		init();
		
		//Register Intend receiver
		IntentFilter intentFilter = new IntentFilter(); 
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); 
		registerReceiver(connectionReceiver, intentFilter); 
		
		//Set the activity navigation
		ActivityUtil.mychildren = this;
	}

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case TIMEOUT:
				Toast.makeText(MyChildrenActivity.this, getResources().getString(R.string.out_time), Toast.LENGTH_SHORT).show();
				break;
			case NEWREPORT:
				newReportMark.setVisibility(View.VISIBLE);
				break;
			case NONETWORK:
				newReportMark.setVisibility(View.INVISIBLE);
				isNetworkWorking = false;
				Toast.makeText(MyChildrenActivity.this, getResources().getString(R.string.out_time), Toast.LENGTH_SHORT).show();
				break;
			case NOSERVERSETTINGS:
                isStreamingWorking = false;
				break;
			case GOTSERVERSETTINGS:
				isNetworkWorking = true;
                isStreamingWorking = true;
				break;
			}
			
			return false;
		}
	});

	//Doctor consultation
	public void doctorConsult(View view) {
		if ("0".equals(Student_Info.chunyuisopen)) {
			Intent intent = new Intent(this, DoctorActivity.class);
			ActivityUtil.main.comeIn(intent);
		} else {
			Intent intent = new Intent(this, ConsultActivity.class);
			intent.putExtra("user_id", Student_Info.username);
			startActivity(intent);
		}
	}

	//Morning check and attendance tracking
	public void morningChecking(View v) {
		Intent intent = new Intent(this, MoringCheckActivity.class);
		//Intent intent = new Intent(this, AttendanceManagerActivity.class);
		ActivityUtil.main.comeIn(intent);
	}
	
	//Class Report
	public void childReport(View v) {
		newReportMark.setVisibility(View.GONE);
		Intent intent = new Intent(this, ReportActivity.class);
		ActivityUtil.main.comeIn(intent);
	}
	
	//ShuttlebusStop List
	public void ShuttlebusStop(View v)
	{
		Intent intent = new Intent(this, ShuttlebusActivity.class);
		ActivityUtil.main.comeIn(intent);
	}
	
	//Video
	public void classVideostreaming(View v) {
		
		//Check the current network condition
		if(isNetworkWorking && isStreamingWorking)
		{
			Intent intent = new Intent(this, IpcSelectionActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("ipcparam", mIpcparam);
            intent.putExtras(bundle);

			intent.putExtra("ip", this.m_sStreamIP);
			intent.putExtra("device", this.m_sDVRName);
			intent.putExtra("port", this.m_iStreamPort);
            intent.putExtra("ip", "54.223.156.59");
            intent.putExtra("device", "dvr");
            intent.putExtra("port", 600);
			
			ActivityUtil.main.comeIn(intent);
		}
		else if(isNetworkWorking == false)
		{
			Toast.makeText(MyChildrenActivity.this, getResources().getString(R.string.out_time), Toast.LENGTH_SHORT)
			.show();
		}
		else if(isStreamingWorking == false)
		{
			Toast.makeText(MyChildrenActivity.this, getResources().getString(R.string.no_streaming_service), Toast.LENGTH_SHORT)
			.show();
		}
			
	}

	@SuppressWarnings("deprecation")
	public void init() {
		DB db = new DB(this);
		SQLiteDatabase sql = db.getReadableDatabase();
		Cursor cursor = sql.query("student_info", null, "uid=?", new String[] { Student_Info.uid }, null, null, null);
		if (cursor == null || cursor.getCount() == 0) {

		} else {
			cursor.moveToFirst();
			avatarImg.setBackgroundDrawable(new BitmapDrawable(ImageUtil.base64ToBitmap(cursor.getString(cursor
					.getColumnIndex("avatar")))));
			nickName.setText(cursor.getString(cursor.getColumnIndex("nikename")));
			String tmp = "";
			tmp = cursor.getString(cursor.getColumnIndex("orderendtime"));
			if ("".equals(tmp)) {
				serverStatus = tmp;
			} else {
				SimpleDateFormat foo = new SimpleDateFormat("yyyy-MM-dd");
				tmp = foo.format(new Date(Long.parseLong(tmp) * 1000));
				serverStatus = tmp + getResources().getString(R.string.dao_qi);
				tuitionDue.setText(serverStatus);
			}
		}
		sql.close();
		db.close();
	}

	private void getClassReport() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				if (HttpUtil.isNetworkConnected(MyChildrenActivity.this)) {
					Params params = new Params("status", null);
					Result result = HttpUtil.httpGet(MyChildrenActivity.this, params);
					if (result == null) {
						handler.sendEmptyMessage(TIMEOUT);
					} else if ("1".equals(result.getCode())) {
						try {
							JSONObject myJson = new JSONObject(result.getContent());
							int report = myJson.getInt("report");
							if (report > 0) {
								handler.sendEmptyMessage(NEWREPORT);
							}
							myJson.get("report");
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		thread.start();
	}
	
	private void getIPCsettings() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (HttpUtil.isNetworkConnected(MyChildrenActivity.this)) {
//					Result result = HttpUtil.httpGet(MyChildrenActivity.this, new Params("setting", null));
                    Result result = HttpUtil.httpGet(MyChildrenActivity.this, new Params("camera", null));
                    if (result == null) {
						handler.sendEmptyMessage(TIMEOUT);
					} else {

                        mIpcparam = FastJsonTools.getObject(result.getContent(), Ipcparam.class);

                        //Check the parameters
                        if(mIpcparam!=null)
                        {
                            m_sStreamIP = mIpcparam.getDdns();
                            m_iStreamPort = Integer.parseInt(mIpcparam.getPort());
                            mDvrList = mIpcparam.getDvr();
                        }

                        if( mDvrList.size()==0 || m_iStreamPort==0 || m_sStreamIP.equals("") )
                            handler.sendEmptyMessage(NOSERVERSETTINGS);
                        else
                            handler.sendEmptyMessage(GOTSERVERSETTINGS);
					}
				} else {
					handler.sendEmptyMessage(NONETWORK);
				}
			}
		});
		thread.start();
	}


	public void baseinfo(View view) {
		Intent intent = new Intent(this, BaseInfoNewActivity.class);
		ActivityUtil.main.comeIn(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (ActivityUtil.main != null) {
				ActivityUtil.main.move();
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	@Override
	public void onResume() {
		
		//Get the reports
		getClassReport();
		
		//Get the Streaming server status
		getIPCsettings();
		
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (connectionReceiver != null) { 
			unregisterReceiver(connectionReceiver); 
		} 
	}
}
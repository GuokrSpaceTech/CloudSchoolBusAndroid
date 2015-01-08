package com.Manga.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

import android.view.View;

import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.R;
import com.Manga.Activity.ClassUpdate.ClassUpdateActivity;
import com.Manga.Activity.activity.ActivityRegisterActivity;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.myChildren.MyChildrenActivity;
import com.Manga.Activity.myChildren.Shuttlebus.ShuttlebusActivity;
import com.Manga.Activity.myChildren.morningCheck.MoringCheckActivity;
import com.Manga.Activity.notification.NotificationActivity;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class HomeActivity extends BaseActivityGroup {
	private RelativeLayout contentView;

	/**
	 * 分享按钮
	 */
	private Button classShareBtn;
	/**
	 * 通知按钮
	 */
	private Button notificationBtn;
	/**
	 * 活动按钮
	 */
	private Button activityBtn;
	/**
	 * 教育咨询按钮
	 */
	private Button mychildrenBtn;
	private View share;
	private View notification;
	private View activity;
	private View mychildren;
	private TextView textshare;
	private TextView textnotice;
	private TextView textevents;
	/**
	 * 检查版本无网络
	 */
	private static final int CHECKVERSIONNONETWORK = 0;
	/**
	 * 检查版本网络超时
	 */
	private static final int CHECKVERSIONNETWORKOUTTIME = 1;
	/**
	 * 分享提示显示
	 */
	private static final int SHAREVISIBLE = 2;
	/**
	 * 分享提示隐藏
	 */
	private static final int SHAREGONE = 3;
	/**
	 * 通知提示显示
	 */
	private static final int NOTICEVISIBLE = 4;
	/**
	 * 通知提示隐藏
	 */
	private static final int NOTICEGONE = 5;
	/**
	 * 活动提示显示
	 */
	private static final int EVENTSVISIBLE = 6;
	/**
	 * 活动提示隐藏
	 */
	private static final int EVENTSGONE = 7;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		ActivityUtil.home = this;
		
		contentView = (RelativeLayout) findViewById(R.id.main_content);
		classShareBtn = (Button) findViewById(R.id.class_share);
		notificationBtn = (Button) findViewById(R.id.notification);
		activityBtn = (Button) findViewById(R.id.activity);
		mychildrenBtn = (Button) findViewById(R.id.mychildren);
		textshare = (TextView) findViewById(R.id.class_share_notice);
		textnotice = (TextView) findViewById(R.id.notification_notice);
		textevents = (TextView) findViewById(R.id.activity_notice);

		checkStatus();
		Intent intent = new Intent(this, ClassUpdateActivity.class);
		change("share", intent);	
	}
	
	private void getHeight(){
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_top);
		SharedPreferences sp1 = HomeActivity.this.getSharedPreferences("Top",Context.MODE_PRIVATE);
		Editor editor=sp1.edit();
		editor.putInt("Top", layout.getHeight());
		editor.commit();
	}

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message mess) {
			switch (mess.what) {
			case CHECKVERSIONNONETWORK:
				Toast.makeText(HomeActivity.this, R.string.check_version_no_version, Toast.LENGTH_SHORT).show();
				break;
			case CHECKVERSIONNETWORKOUTTIME:
				Toast.makeText(HomeActivity.this, R.string.check_version_out_time, Toast.LENGTH_SHORT).show();
				break;
			case SHAREGONE:
				textshare.setVisibility(View.GONE);
				break;
			case NOTICEGONE:
				textnotice.setVisibility(View.GONE);
				break;
			case EVENTSGONE:
				textevents.setVisibility(View.GONE);
				break;
			case SHAREVISIBLE:
				textshare.setVisibility(View.VISIBLE);
				break;
			case NOTICEVISIBLE:
				textnotice.setVisibility(View.VISIBLE);
				break;
			case EVENTSVISIBLE:
				textevents.setVisibility(View.VISIBLE);
				break;
			}
			return false;
		}
	});

	public void CloseStatusShare() {
		handler.sendEmptyMessage(SHAREGONE);
	}

	public void CloseStatusNotice() {
		handler.sendEmptyMessage(NOTICEGONE);
	}

	public void CloseStatusEvents() {
		handler.sendEmptyMessage(EVENTSGONE);
	}

	/**
	 * Check if there is new updates
	 */
	private void checkStatus() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (HttpUtil.isNetworkConnected(HomeActivity.this)) {
					Result result = HttpUtil.httpGet(HomeActivity.this, new Params("status", null));
					if (result == null) {
						handler.sendEmptyMessage(CHECKVERSIONNETWORKOUTTIME);
					} else {
						try {
							JSONObject myJson = new JSONObject(result.getContent());
							String shareTitle = myJson.getString("notice");
							String notiTitle = myJson.getString("article");
							String eventsTitle = myJson.getString("events");

							textshare.setText(shareTitle);
							textnotice.setText(notiTitle);
							textevents.setText(eventsTitle);
							if (shareTitle.trim().equals("0")) {
								handler.sendEmptyMessage(SHAREGONE);
							} else {
								handler.sendEmptyMessage(SHAREVISIBLE);
							}
							if (notiTitle.trim().equals("0")) {
								handler.sendEmptyMessage(NOTICEGONE);
							} else {
								handler.sendEmptyMessage(NOTICEVISIBLE);
							}
							if (eventsTitle.trim().equals("0")) {
								handler.sendEmptyMessage(EVENTSGONE);
							} else {
								handler.sendEmptyMessage(EVENTSVISIBLE);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				} else {
					handler.sendEmptyMessage(CHECKVERSIONNONETWORK);
				}
			}
		});
		thread.start();
	}

	public void change(String id, Intent intent) {
		contentView.removeAllViews();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		if ("share".equals(id)) {
			if (share == null) {
				share = HomeActivity.this.getLocalActivityManager().startActivity(id, intent).getDecorView();
			}
			contentView.addView(share, params);
		} else if ("notification".equals(id)) {
			if (notification == null) {
				notification = HomeActivity.this.getLocalActivityManager().startActivity(id, intent).getDecorView();
			}
			contentView.addView(notification, params);
		} else if ("activity".equals(id)) {
			if (activity == null) {
				activity = HomeActivity.this.getLocalActivityManager().startActivity(id, intent).getDecorView();
			}
			contentView.addView(activity, params);
		} else if ("mychildren".equals(id)) {
			if (mychildren == null) {
				mychildren = HomeActivity.this.getLocalActivityManager().startActivity(id, intent).getDecorView();
			}
			contentView.addView(mychildren, params);
		}
	}

	/**
	 * show menu
	 * 
	 * @param v
	 */
	public void move(View v) {
		getHeight();
		ActivityUtil.main.move();
	}
	/**
	 * Attendance
	 * 
	 * @param
	 */
	public void attendance() {
		ActivityUtil.main.showPRO();
		Intent intent = new Intent(this, MoringCheckActivity.class);
		ActivityUtil.startActivity(ActivityUtil.main, intent);
	}
	
	/**
	 * ShuttleBusStop
	 * 
	 * @param
	 */
	public void shuttlebusstops() {
		ActivityUtil.main.showPRO();
		Intent intent = new Intent(this, ShuttlebusActivity.class);
		ActivityUtil.startActivity(ActivityUtil.main, intent);
	}

	/**
	 * 班级分享按钮单机时间
	 * 
	 * @param v
	 */
	public void classShare(View v) {
		classShareBtn.setBackgroundResource(R.drawable.down_bottom_classshare);
		notificationBtn.setBackgroundResource(R.drawable.bottom_notice);
		activityBtn.setBackgroundResource(R.drawable.bottom_activity);
		mychildrenBtn.setBackgroundResource(R.drawable.bottom_education);
		Intent intent = new Intent(this, ClassUpdateActivity.class);
		change("share", intent);
	}

	/**
	 * 通知
	 * 
	 * @param v
	 */
	public void notification(View v) {
		classShareBtn.setBackgroundResource(R.drawable.bottom_classshare);
		notificationBtn.setBackgroundResource(R.drawable.down_bottom_notice);
		activityBtn.setBackgroundResource(R.drawable.bottom_activity);
		mychildrenBtn.setBackgroundResource(R.drawable.bottom_education);
		Intent intent = new Intent(this, NotificationActivity.class);
		change("notification", intent);
	}

	/**
	 * 活动
	 * 
	 * @param v
	 */
	public void activity(View v) {
		classShareBtn.setBackgroundResource(R.drawable.bottom_classshare);
		notificationBtn.setBackgroundResource(R.drawable.bottom_notice);
		activityBtn.setBackgroundResource(R.drawable.down_bottom_activity);
		mychildrenBtn.setBackgroundResource(R.drawable.bottom_education);
		Intent intent = new Intent(this, ActivityRegisterActivity.class);
		change("activity", intent);
	}

	/**
	 * 4th Tab, myChildren
	 * @param v
	 */
	public void mychildren(View v) {
		classShareBtn.setBackgroundResource(R.drawable.bottom_classshare);
		notificationBtn.setBackgroundResource(R.drawable.bottom_notice);
		activityBtn.setBackgroundResource(R.drawable.bottom_activity);
		mychildrenBtn.setBackgroundResource(R.drawable.down_bottom_education);
		Intent intent = new Intent(this, MyChildrenActivity.class);
		change("mychildren", intent);
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}

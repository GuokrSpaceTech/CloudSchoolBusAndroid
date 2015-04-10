package com.Manga.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import cn.sharesdk.framework.ShareSDK;

import com.Manga.Activity.Gestures.GesturesSetActivity;
import com.Manga.Activity.LeftSetting.LeftSettingActivity;
import com.Manga.Activity.TeacherMessageBox.InboxActivity;
import com.Manga.Activity.account.MyAccountActivity;
import com.Manga.Activity.adapter.ManageChildrenAdapter;
import com.Manga.Activity.cookbook.FoodMenuActivity;
import com.Manga.Activity.feekback.IderFeekBackActivity;
import com.Manga.Activity.myChildren.FamilyMembers.FamilyMembersActivity;
import com.Manga.Activity.syllabus.SyllabusActivity;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.DensityUtil;
import com.Manga.Activity.utils.Push_Info;
import com.Manga.Activity.utils.Student_Info;
import com.Manga.Activity.utils.Utils;
import com.Manga.Activity.widget.ScrollRelaLayout;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.umeng.analytics.MobclickAgent;


public class MainActivity extends BaseActivityGroup {
	private ScrollRelaLayout scrollLayoutMore;
	private RelativeLayout contentView;
	private String         currentActivity;
	private View           homeView;
	private RelativeLayout mainMyAccount;
	public  RelativeLayout relativeLayoutMenu;
	public  RelativeLayout relativeLayoutSchedule;
	private RelativeLayout relativeLayoutFeedback;
	private RelativeLayout relativeLayoutSetting;
	private ProgressDialog progressDialog;
	private static int     backWidth;
	/**
	 * 进度条
	 */
	private static final int SHOWPROGRESS = 5;
	/**
	 * 取消进度条显示
	 */
	private static final int DISMISSPROGRESS = 6;

	/**
	 * 退出登录
	 */
	private static final int OUTEXIT = 12;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message mess) {
			switch (mess.what) {
			case SHOWPROGRESS:
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(MainActivity.this);
					progressDialog.setMessage(getResources().getString(R.string.init_view));
					progressDialog.setCancelable(false);
				}
				progressDialog.show();
				break;
			case DISMISSPROGRESS:
				if(progressDialog != null) progressDialog.dismiss();
				break;
				case OUTEXIT:
				try {
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					View view = View.inflate(MainActivity.this, R.layout.msg_exit, null);
					Button exit = (Button) view.findViewById(R.id.exit);
					Button cancel = (Button) view.findViewById(R.id.cancel);
					final AlertDialog dialog = builder.create();
					dialog.setView(view, 0, 0, 0, 0);
					dialog.show();

					exit.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							try {
								Push_Info.getInstance().setBlnIsLogin(false);
								dialog.dismiss();
								Student_Info.uid = "";
								SharedPreferences sp = MainActivity.this.getSharedPreferences("LoggingData",
										Context.MODE_PRIVATE);
								Editor editor = sp.edit();
								editor.putString("uid", "");
								editor.putBoolean("isAutoLogging", false);
								editor.putString("password", "");
								editor.putString("username", "");
								editor.commit();
								finish();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					cancel.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);		
		ActivityUtil.main = this;
		
		//OnekeyShare init
		ShareSDK.initSDK(this);
		PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
				Utils.getMetaValue(MainActivity.this, "api_key"));
		
		//Update Push Info
		Push_Info.getInstance().setBlnIsLogin(true);
        
		//Init views
		scrollLayoutMore = (ScrollRelaLayout) findViewById(R.id.more_layout);
		contentView = (RelativeLayout) findViewById(R.id.con_layout);
		mainMyAccount = (RelativeLayout) findViewById(R.id.main_myaccount);
		relativeLayoutMenu = (RelativeLayout) findViewById(R.id.main_cookbook);
		relativeLayoutSchedule = (RelativeLayout) findViewById(R.id.main_syllabus);
		relativeLayoutFeedback = (RelativeLayout) findViewById(R.id.main_ider);
		relativeLayoutSetting = (RelativeLayout) findViewById(R.id.main_setting);

		//Slide width
		backWidth = DensityUtil.dip2px(this, 60);
		
		//If need gesture password
		checkGesture();

		//????
		if (FamilyMembersActivity.childReceiverList != null) {
			FamilyMembersActivity.childReceiverList = null;
		}
		
		//Start Home view (also pass the flag if the start from push notification)
		Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra("push", getIntent().getStringExtra("push"));
		change("home", intent);
	}

	/**
	 * click menu button
	 * @param v
	 */
	public void foodmenu(View v) {
		Intent intent = new Intent(this, FoodMenuActivity.class);
		change("cookbook", intent);
		checkView(v);
		move();
	}

	/**
	 * click menu button
	 * 
	 * @param v
	 */
	public void myaccount(View v) {
		Intent intent = new Intent(this, MyAccountActivity.class);
		change("myaccount", intent);
		checkView(v);
		move();
	}

    /**
     * click menu button
     *
     * @param v
     */
    public void teacher_msgbox(View v) {
        Intent intent = new Intent(this, InboxActivity.class);
        change("messagebox", intent);
        checkView(v);
        move();
    }

	/**
	 * click syllabus button
	 * 
	 * @param v
	 */
	public void syllabus(View v) {
		Intent intent = new Intent(this, SyllabusActivity.class);
		change("syllabus", intent);
		checkView(v);
		move();
	}

	/**
	 * click setting button
	 * 
	 * @param v
	 */
	public void setting(View v) {
		Intent intent = new Intent(this, LeftSettingActivity.class);
		change("leftsetting", intent);
		checkView(v);
		move();
	}

	/**
	 * click ider back button;
	 * 
	 * @param v
	 */
	public void ider(View v) {
		Intent intent = new Intent(this, IderFeekBackActivity.class);
		change("idear", intent);
		checkView(v);
		move();
	}

	/**
	 * click out button
	 * 
	 * @param v
	 */
	public void exitOut(View v) {
		handler.sendEmptyMessage(OUTEXIT);
	}

	/**
	 * 显示菜单
	 * 
	 * @param v
	 */
	public void move() {
		WindowManager wm = (WindowManager) this.getSystemService(WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		scrollLayoutMore.beginScroll(width - backWidth - DensityUtil.dip2px(this, 10));
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.blackframe);
		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		SharedPreferences sp1 = MainActivity.this.getSharedPreferences("Top", Context.MODE_PRIVATE);
		params1.setMargins(0, sp1.getInt("Top", 0), 0, 0);
		layout.setLayoutParams(params1);
		if (layout.getVisibility() == View.VISIBLE) {
			layout.setVisibility(View.GONE);
		} else {
			layout.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 引入界面
	 * 
	 * @param
	 */
	public void comeIn(Intent intent) {
		ActivityUtil.startActivity(this, intent);
	}

	/**
	 * 当id为home时 会保存View
	 * 
	 * @param id
	 */
	public void change(String id, Intent intent) {
		contentView.removeAllViews();
		currentActivity = id;
		View viewContent = null;
		if (id.equals("home")) {
			if (homeView == null) {
				viewContent = MainActivity.this.getLocalActivityManager().startActivity(currentActivity, intent)
						.getDecorView();
				homeView = viewContent;
			} else {
				viewContent = homeView;
			}
		} else {
			viewContent = MainActivity.this.getLocalActivityManager().startActivity(currentActivity, intent)
					.getDecorView();
		}
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(viewContent, params);
	}

	public void frame(View view) {
		move();
	}

	public void home(View v) {
		mainMyAccount.setBackgroundResource(R.drawable.account_selector);
		relativeLayoutMenu.setBackgroundResource(R.drawable.cookbook_selector);
		relativeLayoutSchedule.setBackgroundResource(R.drawable.syllabus_selector);
		relativeLayoutFeedback.setBackgroundResource(R.drawable.idea_selector);
		relativeLayoutSetting.setBackgroundResource(R.drawable.setting_selector);
		if (homeView == null) {
			Intent intent = new Intent(this, HomeActivity.class);
			change("home", intent);
		} else {
			contentView.removeAllViews();
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			contentView.addView(homeView, params);
		}
		move();
	}

	/**
	 * 设置背景按钮
	 * 
	 * @param v
	 */
	private void checkView(View v) {
		mainMyAccount.setBackgroundResource(R.drawable.account_selector);
		relativeLayoutMenu.setBackgroundResource(R.drawable.cookbook_selector);
		relativeLayoutSchedule.setBackgroundResource(R.drawable.syllabus_selector);
		relativeLayoutFeedback.setBackgroundResource(R.drawable.idea_selector);
		relativeLayoutSetting.setBackgroundResource(R.drawable.setting_selector);
		switch (v.getId()) {
		case R.id.main_cookbook:
			v.setBackgroundResource(R.drawable.down_cookbook_selector);
			break;
		case R.id.main_syllabus:
			v.setBackgroundResource(R.drawable.down_syllabus_selector);
			break;
		case R.id.main_ider:
			v.setBackgroundResource(R.drawable.down_idea_selector);
			break;
		case R.id.main_myaccount:
			v.setBackgroundResource(R.drawable.down_account_selector);
			break;
		case R.id.main_setting:
			v.setBackgroundResource(R.drawable.down_setting_selector);
			break;
		}
	}

	public void MsgBirthday() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.dialog_happy_birthday, null);
		Button kaiqi = (Button) view.findViewById(R.id.set);
		final AlertDialog dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		kaiqi.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
	}

	private void checkGesture() {
		if (ManageChildrenAdapter.manage == 0) {
			SharedPreferences sp = this.getSharedPreferences("GestureData", Context.MODE_PRIVATE);
			boolean isOpen = sp.getBoolean("isOpen", false);
			if (!isOpen) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				View view = View.inflate(this, R.layout.msg_please_psd, null);
				Button kaiqi = (Button) view.findViewById(R.id.kaiqi);
				Button hulue = (Button) view.findViewById(R.id.hulue);
				final AlertDialog dialog = builder.create();
				dialog.setView(view, 0, 0, 0, 0);
				dialog.show();
				kaiqi.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(MainActivity.this, GesturesSetActivity.class);
						ActivityUtil.startActivity(MainActivity.this, intent);
						dialog.dismiss();
					}
				});
				hulue.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

			}
		} else {
			ManageChildrenAdapter.manage = 0;
		}
	}

	/**
	 * 显示进度
	 */
	public void showPRO() {
		handler.sendEmptyMessage(SHOWPROGRESS);
	}

	/**
	 * 取消进度
	 */
	public void disPRO() {
		handler.sendEmptyMessage(DISMISSPROGRESS);
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

	public void setCancelPRO() {
		progressDialog.setCancelable(true);
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

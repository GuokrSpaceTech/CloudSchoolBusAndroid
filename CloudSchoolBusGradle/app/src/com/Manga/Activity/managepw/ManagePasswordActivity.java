package com.Manga.Activity.managepw;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.Gestures.GesturesSetActivity;
import com.Manga.Activity.modifi.SetPasswordActivity;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class ManagePasswordActivity extends BaseActivity {
	private Button checkBox;
	private Drawable close;
	private Drawable open;
	private RelativeLayout layoutChange;
	private RelativeLayout layoutCheck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_pw);
		layoutCheck = (RelativeLayout) findViewById(R.id.check_relative);
		layoutChange = (RelativeLayout) findViewById(R.id.change_relative);
		checkBox = (Button) findViewById(R.id.checkBox);
		close = getResources().getDrawable(R.drawable.close);
		open = getResources().getDrawable(R.drawable.open);
		checkSetting();
		Intent intent = getIntent();
		ActivityUtil.managepassword = this;
		/*
		 * boolean open=intent.getBooleanExtra("Open", true); if(open){
		 * 
		 * }else{ modifiGesture(); }
		 */
	}

	/**
	 * 初始化开关状态
	 */
	public void checkSetting() {
		// TODO Auto-generated method stub
		SharedPreferences sp = this.getSharedPreferences("GestureData",
				Context.MODE_PRIVATE);
		boolean isOpen = sp.getBoolean("isOpen", false);
		if (isOpen) {
			checkBox.setBackgroundDrawable(open);
			layoutChange.setVisibility(View.VISIBLE);
			layoutCheck.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.setting_content_bg_selector_top));
		} else {
			checkBox.setBackgroundDrawable(close);
			layoutChange.setVisibility(View.GONE);
			layoutCheck.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.setting_content_bg_selector));
		}
	}

	public void changePW(View v) {
		Intent intent = new Intent(this, SetPasswordActivity.class);
		ActivityUtil.startActivity(this, intent);
	}

	/**
	 * 单击修改手势密码
	 * 
	 * @param v
	 */
	public void modifiGesturePw(View v) {
		modifiGesture();
	}

	public void backMenu(View v) {
		ActivityUtil.close(this);
	}

	/**
	 * 单击开关
	 * 
	 * @param v
	 */
	public void checkBox(View v) {
		if (checkBox.getBackground().equals(open)) {
			checkBox.setBackgroundDrawable(close);
			layoutChange.setVisibility(View.GONE);
			layoutCheck.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.setting_content_bg_selector));
			SharedPreferences sp = this.getSharedPreferences("GestureData",
					Context.MODE_PRIVATE);
			sp.edit().putString("strLockOld", "").commit();
			sp.edit().putString("strLockSuccess", "").commit();
			sp.edit().putString("strLock", "").commit();
			sp.edit().putBoolean("isOpen", false).commit();
		} else {
			modifiGesture();
			/*
			 * SharedPreferences sp
			 * =this.getSharedPreferences("GestureData",Context.MODE_PRIVATE);
			 * checkBox.setBackgroundDrawable(open);
			 * sp.edit().putBoolean("isOpen", true).commit();
			 * modifiGesturePw.setVisibility(View.VISIBLE); modifiGesture();
			 */
		}
	}

	/**
	 * 修改手势密码
	 */
	private void modifiGesture() {
		Intent intent = new Intent(this, GesturesSetActivity.class);
		startActivity(intent);
	}

	public void openCheck() {
		SharedPreferences sp = this.getSharedPreferences("GestureData",
				Context.MODE_PRIVATE);
		layoutChange.setVisibility(View.VISIBLE);
		layoutCheck.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.setting_content_bg_selector_top));
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

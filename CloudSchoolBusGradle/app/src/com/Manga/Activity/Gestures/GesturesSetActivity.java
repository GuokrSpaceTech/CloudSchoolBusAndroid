/*
 * Description: Setup the gesture password
 * Function: 
 * Copyright: 2014 @Beijing Guokrspace Tech Co.,Ltd 
 */

package com.Manga.Activity.Gestures;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Manga.Activity.R;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class GesturesSetActivity extends Activity {
	/** Called when the activity is first created. */
	private TextView textViewMessage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Get the UI 
		setContentView(R.layout.locksetfirst);
		
		//Attach the customized the Gesture drawing view 
		RelativeLayout view = (RelativeLayout) findViewById(R.id.view1);
		drawlSet drawl1 = new drawlSet(this);
		view.addView(drawl1);

		//Init the preference to save the Gesture password
		SharedPreferences sp = this.getSharedPreferences("GestureData", Context.MODE_PRIVATE);
		sp.edit().putString("strLockOld", "").commit();
		sp.edit().putString("strLockSuccess", "").commit();
		sp.edit().putBoolean("isActive", true).commit();
		
		textViewMessage = (TextView) findViewById(R.id.txt_Message);
		textViewMessage.setText(getResources().getString(R.string.lock_setgusture));
		
		//Init the UI
		viewClear();

		//Set the Activity navigations 
		ActivityUtil.select1 = this;

	}

	private void viewClear() {
		View view1 = (View) findViewById(R.id.viewselect1);
		view1.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
		View view2 = (View) findViewById(R.id.viewselect2);
		view2.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
		View view3 = (View) findViewById(R.id.viewselect3);
		view3.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
		View view4 = (View) findViewById(R.id.viewselect4);
		view4.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
		View view5 = (View) findViewById(R.id.viewselect5);
		view5.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
		View view6 = (View) findViewById(R.id.viewselect6);
		view6.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
		View view7 = (View) findViewById(R.id.viewselect7);
		view7.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
		View view8 = (View) findViewById(R.id.viewselect8);
		view8.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
		View view9 = (View) findViewById(R.id.viewselect9);
		view9.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
	}

	public void UpdateOldLock(String str) {
		SharedPreferences sp = this.getSharedPreferences("GestureData", Context.MODE_PRIVATE);
		sp.edit().putString("strLockOld", str).commit();
		changeView(str);
		textViewMessage.setText(getResources().getString(R.string.lock_setgusture_again));
	}

	public void UpdateNewLock(String str) {
		SharedPreferences sp = this.getSharedPreferences("GestureData", Context.MODE_PRIVATE);
		sp.getString("strLockOld", "");
		if (str.equals(sp.getString("strLockOld", ""))) {
			textViewMessage.setText(getResources().getString(R.string.lock_success));
			sp.edit().putString("strLockSuccess", "success").commit();
			sp.edit().putString("strLock", str).commit();
			mhandler.sendEmptyMessageDelayed(1, 1000);
			sp.edit().putBoolean("isOpen", true).commit();
		} else {
			viewClear();
			textViewMessage.setText(getResources().getString(R.string.lock_success));
			sp.edit().putString("strLockOld", "").commit();
			textViewMessage.setText(getResources().getString(R.string.lock_ungusture));
		}
	}

	public void UpdateNumber() {
		textViewMessage.setText(getResources().getString(R.string.lock_number));
	}

	private void changeView(String str) {
		for (int ii = 0; ii < str.length(); ii++) {
			char chr = str.charAt(ii);
			String strIndex = chr + "";
			int nNumber = Integer.parseInt(strIndex);
			switch (nNumber) {
			case 1:
				View view1 = (View) findViewById(R.id.viewselect1);
				view1.setBackgroundColor(Color.parseColor("#FF176095"));
				break;
			case 2:
				View view2 = (View) findViewById(R.id.viewselect2);
				view2.setBackgroundColor(Color.parseColor("#FF176095"));
				break;
			case 3:
				View view3 = (View) findViewById(R.id.viewselect3);
				view3.setBackgroundColor(Color.parseColor("#FF176095"));
				break;
			case 4:
				View view4 = (View) findViewById(R.id.viewselect4);
				view4.setBackgroundColor(Color.parseColor("#FF176095"));
				break;
			case 5:
				View view5 = (View) findViewById(R.id.viewselect5);
				view5.setBackgroundColor(Color.parseColor("#FF176095"));
				break;
			case 6:
				View view6 = (View) findViewById(R.id.viewselect6);
				view6.setBackgroundColor(Color.parseColor("#FF176095"));
				break;
			case 7:
				View view7 = (View) findViewById(R.id.viewselect7);
				view7.setBackgroundColor(Color.parseColor("#FF176095"));
				break;
			case 8:
				View view8 = (View) findViewById(R.id.viewselect8);
				view8.setBackgroundColor(Color.parseColor("#FF176095"));
				break;
			case 9:
				View view9 = (View) findViewById(R.id.viewselect9);
				view9.setBackgroundColor(Color.parseColor("#FF176095"));
				break;
			default:
				break;
			}
		}
	}

	public void close(View view) {
		SharedPreferences sp = this.getSharedPreferences("GestureData", Context.MODE_PRIVATE);
		sp.edit().putString("strLockOld", "").commit();
		sp.edit().putString("strLockSuccess", "").commit();
		GesturesSetActivity.this.finish();
	}

	private Handler mhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				if (ActivityUtil.managepassword != null) {
					ActivityUtil.managepassword.checkSetting();
				}
				GesturesSetActivity.this.finish();
				break;
			default:
				break;
			}
		};
	};

	public float getStartX(int select) {
		ImageView imageView1 = null;
		int[] location = new int[2];
		switch (select) {
		case 1:
			imageView1 = (ImageView) findViewById(R.id.select1);
			imageView1.getLocationInWindow(location);
			return location[0];
		case 2:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[0];
		case 3:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[0];
		case 4:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[0];
		case 5:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[0];
		case 6:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[0];
		case 7:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[0];
		case 8:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[0];
		case 9:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[0];
		default:
			return 0;
		}
	}

	/**
	 * 获取状态栏高度
	 * 
	 * @return
	 */
	private int getStatusBarHeight() {
		Class<?> c = null;
		Object obj = null;
		java.lang.reflect.Field field = null;
		int x = 0;
		int statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = getResources().getDimensionPixelSize(x);
			return statusBarHeight;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}

	public float getStartY(int select) {
		ImageView imageView1 = null;
		int[] location = new int[2];
		// int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
		int contentTop = getStatusBarHeight();
		switch (select) {
		case 1:
			imageView1 = (ImageView) findViewById(R.id.select1);
			// imageView1.getLocationInWindow(location);
			imageView1.getLocationInWindow(location);
			return location[1] - contentTop;
		case 2:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[1];
		case 3:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[1] - contentTop;
		case 4:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[1] - contentTop;
		case 5:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[1] - contentTop;
		case 6:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[1] - contentTop;
		case 7:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[1] - contentTop;
		case 8:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[1] - contentTop;
		case 9:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[1] - contentTop;
		default:
			return 0;
		}
	}

	public float getWidth() {
		ImageView imageView1 = null;
		imageView1 = (ImageView) findViewById(R.id.select1);
		return imageView1.getWidth();
	}

	public float getHeight() {

		ImageView imageView1 = null;
		imageView1 = (ImageView) findViewById(R.id.select1);
		return imageView1.getHeight();
	}

	public void ChangePicBg(String strIndex) {
		if ("1".equals(strIndex)) {
			ImageView imageView1 = (ImageView) findViewById(R.id.select1);
			imageView1.setBackgroundResource(R.drawable.select_keysel);
		} else if ("2".equals(strIndex)) {
			ImageView imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.setBackgroundResource(R.drawable.select_keysel);
		} else if ("3".equals(strIndex)) {
			ImageView imageView1 = (ImageView) findViewById(R.id.select3);
			imageView1.setBackgroundResource(R.drawable.select_keysel);
		} else if ("4".equals(strIndex)) {
			ImageView imageView1 = (ImageView) findViewById(R.id.select4);
			imageView1.setBackgroundResource(R.drawable.select_keysel);
		} else if ("5".equals(strIndex)) {
			ImageView imageView1 = (ImageView) findViewById(R.id.select5);
			imageView1.setBackgroundResource(R.drawable.select_keysel);
		} else if ("6".equals(strIndex)) {
			ImageView imageView1 = (ImageView) findViewById(R.id.select6);
			imageView1.setBackgroundResource(R.drawable.select_keysel);
		} else if ("7".equals(strIndex)) {
			ImageView imageView1 = (ImageView) findViewById(R.id.select7);
			imageView1.setBackgroundResource(R.drawable.select_keysel);
		} else if ("8".equals(strIndex)) {
			ImageView imageView1 = (ImageView) findViewById(R.id.select8);
			imageView1.setBackgroundResource(R.drawable.select_keysel);
		} else if ("9".equals(strIndex)) {
			ImageView imageView1 = (ImageView) findViewById(R.id.select9);
			imageView1.setBackgroundResource(R.drawable.select_keysel);
		} else if ("0".equals(strIndex)) {
			ImageView imageView1 = (ImageView) findViewById(R.id.select1);
			imageView1.setBackgroundResource(R.drawable.select_key);
			ImageView imageView2 = (ImageView) findViewById(R.id.select2);
			imageView2.setBackgroundResource(R.drawable.select_key);
			ImageView imageView3 = (ImageView) findViewById(R.id.select3);
			imageView3.setBackgroundResource(R.drawable.select_key);
			ImageView imageView4 = (ImageView) findViewById(R.id.select4);
			imageView4.setBackgroundResource(R.drawable.select_key);
			ImageView imageView5 = (ImageView) findViewById(R.id.select5);
			imageView5.setBackgroundResource(R.drawable.select_key);
			ImageView imageView6 = (ImageView) findViewById(R.id.select6);
			imageView6.setBackgroundResource(R.drawable.select_key);
			ImageView imageView7 = (ImageView) findViewById(R.id.select7);
			imageView7.setBackgroundResource(R.drawable.select_key);
			ImageView imageView8 = (ImageView) findViewById(R.id.select8);
			imageView8.setBackgroundResource(R.drawable.select_key);
			ImageView imageView9 = (ImageView) findViewById(R.id.select9);
			imageView9.setBackgroundResource(R.drawable.select_key);

		}
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

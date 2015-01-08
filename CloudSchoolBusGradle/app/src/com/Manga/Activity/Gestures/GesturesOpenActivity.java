package com.Manga.Activity.Gestures;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.LoginActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.ImageUtil;
import com.Manga.Activity.utils.Student_Info;
import com.umeng.analytics.MobclickAgent;

public class GesturesOpenActivity extends BaseActivity {
	/** Called when the activity is first created. */
	private TextView textViewMessage;
	private RelativeLayout relativeLayouthead;
	private int nCount = 5;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lockopen);
		RelativeLayout view = (RelativeLayout) findViewById(R.id.view1);
		ActivityUtil.select2 = this;
		drawlOpen drawl1 = new drawlOpen(this);

		view.addView(drawl1);
		super.onCreate(savedInstanceState);
		textViewMessage = (TextView) findViewById(R.id.txt_Message);
		relativeLayouthead = (RelativeLayout) findViewById(R.id.relativeLayout_head);
		textViewMessage.setText(getResources().getString(R.string.lock_setgusture));
		init();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// 按下键盘上返回按钮
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public void UpdateNewLock(String str) {
		SharedPreferences sp = this.getSharedPreferences("GestureData", Context.MODE_PRIVATE);
		if (str.equals(sp.getString("strLock", ""))) {
			if (ActivityUtil.main == null) {
				Intent intent = new Intent(GesturesOpenActivity.this, LoginActivity.class);
				startActivity(intent);
			}
			finish();
		} else {
			nCount--;
			if (nCount > 0) {
				textViewMessage.setText(getString(R.string.lock_msgprev) + nCount + getString(R.string.lock_msgend));
			} else {
				Intent intent = new Intent(GesturesOpenActivity.this, LoginActivity.class);
				Student_Info.uid = "";
				SharedPreferences sp1 = GesturesOpenActivity.this.getSharedPreferences("LoggingData",
						Context.MODE_PRIVATE);
				Editor editor = sp1.edit();
				editor.putString("uid", "");
				editor.putBoolean("isAutoLogging", false);
				editor.putString("password", "");
				editor.putString("username", "");
				editor.commit();
				startActivity(intent);
				finish();
			}
		}
	}

	private void init() {
		DB db = new DB(this);
		SQLiteDatabase sql = db.getReadableDatabase();
		try {
			SharedPreferences sp1 = GesturesOpenActivity.this.getSharedPreferences("LoggingData", Context.MODE_PRIVATE);
			Cursor cursor = sql.query("student_info", null, "uid=?", new String[] { sp1.getString("uid", "") }, null,
					null, null);
			if (cursor == null || cursor.getCount() == 0) {

			} else {
				cursor.moveToFirst();
				relativeLayouthead.setBackgroundDrawable(new BitmapDrawable(ImageUtil.base64ToBitmap(cursor
						.getString(cursor.getColumnIndex("avatar")))));
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		sql.close();
		db.close();
	}

	public void UpdateNumber() {
		textViewMessage.setText(getResources().getString(R.string.lock_number));
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
		int contentTop = getStatusBarHeight();
		switch (select) {
		case 1:
			imageView1 = (ImageView) findViewById(R.id.select1);
			imageView1.getLocationInWindow(location);
			return location[1] - contentTop;
		case 2:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[2] - contentTop;
		case 3:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[3] - contentTop;
		case 4:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[4] - contentTop;
		case 5:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[5] - contentTop;
		case 6:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[6] - contentTop;
		case 7:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[7] - contentTop;
		case 8:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[8] - contentTop;
		case 9:
			imageView1 = (ImageView) findViewById(R.id.select2);
			imageView1.getLocationInWindow(location);
			return location[9] - contentTop;
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

	public void close(View view) {
		Intent intent = new Intent(GesturesOpenActivity.this, LoginActivity.class);
		Student_Info.uid = "";
		SharedPreferences sp = GesturesOpenActivity.this.getSharedPreferences("LoggingData", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("isAutoLogging", false);
		editor.putString("password", "");
		editor.putString("username", "");
		editor.commit();

		sp = this.getSharedPreferences("GestureData", Context.MODE_PRIVATE);
		sp.edit().putString("strLockOld", "").commit();
		sp.edit().putString("strLockSuccess", "").commit();
		sp.edit().putBoolean("isActive", true).commit();
		sp.edit().putBoolean("isOpen", false).commit();
		startActivity(intent);
		finish();
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

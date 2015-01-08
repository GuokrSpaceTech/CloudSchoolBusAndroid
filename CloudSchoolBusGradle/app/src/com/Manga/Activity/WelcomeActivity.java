package com.Manga.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.Manga.Activity.R;
import com.Manga.Activity.Gestures.GesturesOpenActivity;
import com.Manga.Activity.utils.Push_Info;
import com.umeng.analytics.MobclickAgent;

public class WelcomeActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		
		//Need user stroke gesture password
		SharedPreferences sp1 = getSharedPreferences("GestureData", Context.MODE_PRIVATE);
		if (!sp1.getBoolean("isActive", true)) {
			if ("success".equals(sp1.getString("strLockSuccess", ""))) {
				mhandler.sendEmptyMessageDelayed(2, 2000);
				return;
			}
		}
		
		//Opened from a push notification
		if (Push_Info.getInstance().getStrPushOpen().equals("push")) {
			mhandler.sendEmptyMessage(1);
		} else {
		//Check if need user guide page since it is first time 
			mhandler.sendEmptyMessage(3);
		}
	}

	private Handler mhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				WelcomeActivity.this.finish();
				break;
			case 2:
				SharedPreferences sp1 = getSharedPreferences("GestureData", Context.MODE_PRIVATE);
				sp1.edit().putBoolean("isActive", true).commit();
				Intent intent1 = new Intent(WelcomeActivity.this, GesturesOpenActivity.class);
				startActivity(intent1);
				WelcomeActivity.this.finish();
				break;
			case 3:
				SharedPreferences sp = getSharedPreferences("rookie", Context.MODE_PRIVATE);
				if (sp.getBoolean("rookie", false)) {
					Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
					finish();
					break;
				} else {
					Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
					finish();
				}
				WelcomeActivity.this.finish();
			default:
				break;
			}
		};
	};

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}

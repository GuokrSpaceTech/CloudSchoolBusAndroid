package com.Manga.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.Manga.Activity.R;
import com.umeng.analytics.MobclickAgent;

public class GuideActivity extends Activity {
	private ScrollView scrollView;
	private RelativeLayout layout_rookie;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rookie);
		layout_rookie = (RelativeLayout) findViewById(R.id.layout_rookie);
		scrollView = (ScrollView) findViewById(R.id.scrollall);
		scrollView.post(new Runnable() {
			public void run() {
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
		// if (getResources().getConfiguration().locale.getCountry().equals("CN")) {
		// layout_rookie.setBackgroundResource(R.drawable.rookie_bg);
		// } else if (getResources().getConfiguration().locale.getCountry().equals("US")
		// || getResources().getConfiguration().locale.getCountry().equals("UK")) {
		// layout_rookie.setBackgroundResource(R.drawable.rookie_en_bg);
		//
		// }
	}

	public void login(View view) {
		SharedPreferences sp = getSharedPreferences("rookie", Context.MODE_PRIVATE);
		sp.edit().putBoolean("rookie", true).commit();
		Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
		startActivity(intent);
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

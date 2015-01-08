package com.Manga.Activity.Msg;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class MsgVersionActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msg_version);
		TextView textview = (TextView) findViewById(R.id.textview);

		textview.setText(ActivityUtil.leftsetting.getVersionExplain());
		String str = ActivityUtil.leftsetting.getVersionExplain();
		String aa = str;

	}

	public void canel(View view) {
		finish();
	}

	public void version(View view) {
		Uri uri = Uri.parse(ActivityUtil.leftsetting.getVersionUrl());
		Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
		downloadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(downloadIntent);
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

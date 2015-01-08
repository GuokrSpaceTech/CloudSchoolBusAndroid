package com.Manga.Activity.Msg;

import android.os.Bundle;
import android.view.View;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.umeng.analytics.MobclickAgent;

public class MsgActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msg_msg);
	}

	public void ok(View view) {
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

package com.Manga.Activity.ClassUpdate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class TagDialogActivity extends BaseActivity {
	private TextView textviewlabel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_tag);
		textviewlabel = (TextView) findViewById(R.id.textviewlabel);
		textviewlabel.setText(ActivityUtil.shareMain.tagDesc);
		Intent intent = getIntent();
		textviewlabel.setText(intent.getStringExtra("description"));
	}

	public void cancel(View view) {
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

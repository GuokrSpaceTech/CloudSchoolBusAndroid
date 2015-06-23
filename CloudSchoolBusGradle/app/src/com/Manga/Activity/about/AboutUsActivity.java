package com.Manga.Activity.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class AboutUsActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);
	}

	public void backMenu(View v) {
		ActivityUtil.close(this);
	}

	public void termsofuse(View view) {

		Intent intent = new Intent(this, ShowHtmlPolicyActivity.class);
		ActivityUtil.startActivity(this, intent);
		/*
		 * Uri uri = Uri.parse(getResources().getString(R.string.about_us_url));
		 * Intent downloadIntent = new Intent( Intent.ACTION_VIEW, uri);
		 * downloadIntent .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * startActivity(downloadIntent);
		 */
	}

	public void phone(View view) {
		try {
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "4006063996"));
			startActivity(intent);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void yunxiaoche(View view) {
		try {
			Uri uri = Uri.parse("http://www.yunxiaoche.com");
			Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
			downloadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(downloadIntent);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void guokr(View view) {
		try {
			String[] receiver = new String[] { "service@yunxiaoche.com" };

			Intent email = new Intent(Intent.ACTION_SEND);
			email.setType("message/rfc822");
			// 设置邮件发收人
			email.putExtra(Intent.EXTRA_EMAIL, receiver);

			startActivity(Intent.createChooser(email, ""));
		} catch (Exception e) {
			// TODO: handle exception
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

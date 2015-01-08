package com.Manga.Activity.about;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class ShowHtmlPolicyActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_html_policy);
		WebView webView = (WebView) findViewById(R.id.webView);
		try {
			webView.loadUrl(getResources().getString(R.string.about_us_url));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void backMenu(View v) {
		ActivityUtil.close(this);
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

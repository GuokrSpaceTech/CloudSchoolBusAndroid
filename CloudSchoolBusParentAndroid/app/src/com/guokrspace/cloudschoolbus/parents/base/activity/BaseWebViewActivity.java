package com.guokrspace.cloudschoolbus.parents.base.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.base.activity.BaseActivity;

/**
 * webview的基类
 * 
 * @author lenovo
 * 
 */
public class BaseWebViewActivity extends BaseActivity {

	protected WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String title = intent.getExtras().getString("title");
        getSupportActionBar().setTitle(title);
	}

	/**
	 * 初始化webview
	 * 
	 * @param webView
	 */
	protected void initWebView(WebView webView) {
		mWebView.getSettings().setJavaScriptEnabled(true);
		// 设置可以支持缩放
		mWebView.getSettings().setSupportZoom(false);
		// 设置出现缩放工具
		mWebView.getSettings().setBuiltInZoomControls(false);
		// 扩大比例的缩放
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
		// 自适应屏幕
		mWebView.getSettings()
				.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		mWebView.getSettings().setLoadWithOverviewMode(true);

		// 不保存密码
		mWebView.getSettings().setSavePassword(false);
		mWebView.getSettings().setAppCacheEnabled(false);
		// mWebView.getSettings()
		// .setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
	}


	protected void cancel() {
		if (mWebView.canGoBack()) {
			// 返回键退回
			mWebView.goBack();
		} else {
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			cancel();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		return super.onPrepareOptionsMenu(menu);
	}
}

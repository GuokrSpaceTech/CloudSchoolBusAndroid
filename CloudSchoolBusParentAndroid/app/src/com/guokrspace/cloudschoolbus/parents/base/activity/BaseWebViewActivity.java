package com.guokrspace.cloudschoolbus.parents.base.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.guokrspace.cloudschoolbus.parents.R;

import im.delight.android.webview.AdvancedWebView;

/**
 * Created by wangjianfeng on 15/7/27.
 */
public class BaseWebViewActivity extends BaseActivity implements AdvancedWebView.Listener {

	private AdvancedWebView mWebView;
	private SwipeRefreshLayout mRefreshView;
	private String mUrl = "";
	private String mTitle = "";
	private String mParams = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		mUrl        = (String)bundle.get("url");
		mTitle      = (String)bundle.get("title");
		mParams     = (String)bundle.get("params");

		setContentView(R.layout.activity_webview);

		getSupportActionBar().setTitle(mTitle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_cross);
		mWebView = (AdvancedWebView)findViewById(R.id.webView);
		mWebView.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View p_v, MotionEvent p_event) {
				// this will disallow the touch request for parent scroll on touch of child view
				p_v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		mWebView.canGoBack();
		mWebView.loadUrl(mUrl + mParams);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		mWebView.onResume();
		// ...
	}

	@Override
	public void onPause() {
		mWebView.onPause();
		// ...
		super.onPause();
	}

	@Override
	public void onDestroy() {
		mWebView.onDestroy();
		// ...
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		mWebView.onActivityResult(requestCode, resultCode, intent);
		// ...
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home )
		    finish();

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPageStarted(String url, Bitmap favicon) { }

	@Override
	public void onPageFinished(String url) { }

	@Override
	public void onPageError(int errorCode, String description, String failingUrl) { }

	@Override
	public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) { }

	@Override
	public void onExternalPageRequest(String url) {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if( mWebView.onBackPressed() ) //到了顶级页面
			{
				finish();
			}
		}

		return true;
	}
}

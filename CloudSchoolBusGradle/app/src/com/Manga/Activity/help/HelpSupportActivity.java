package com.Manga.Activity.help;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.Manga.Activity.R;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.widget.ProgressWebView;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("SetJavaScriptEnabled")
public class HelpSupportActivity extends Activity {
	private ProgressWebView webView;
	private TextView noWeb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_support);
		webView = (ProgressWebView) findViewById(R.id.help_web);
		webView.loadData("正在加载...", "text/html", "utf-8");
		noWeb = (TextView) findViewById(R.id.help_web_no);
		if (HttpUtil.isNetworkConnected(this)) {
			webView.setVisibility(View.VISIBLE);
			noWeb.setVisibility(View.GONE);
		} else {
			webView.setVisibility(View.GONE);
			noWeb.setVisibility(View.VISIBLE);
		}
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}
		});
		webView.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
					long contentLength) {
				// TODO Auto-generated method stub

			}
		});
		webView.loadUrl("http://www.yunxiaoche.com/help");
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(false);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ActivityUtil.close(this);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
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

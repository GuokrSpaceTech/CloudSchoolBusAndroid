package com.Manga.Activity.myChildren.DoctorConsult;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.Manga.Activity.R;

public class CostWebDialog extends Dialog {
	WebView webView;
	Context context;
	String url;
	Handler handler;
	ProgressBar answer_progressBar;

	public CostWebDialog(Context context, Handler registerHandler, String url) {

		super(context, R.style.processdialog);
		this.context = context;
		this.handler = registerHandler;
		this.url = url;
	}

	public CostWebDialog(Context context, int theme) {
		super(context, R.style.processdialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_pay);
		webView = (WebView) findViewById(R.id.pay_page);
		answer_progressBar = (ProgressBar) findViewById(R.id.answer_progressBar);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webView.freeMemory();
		webView.loadUrl(url);
		webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.setBackgroundColor(0);
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				answer_progressBar.setVisibility(View.GONE);
			}

		});
		webView.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

			}
		});
		webView.addJavascriptInterface(new Object() {

			public void returnapp(int a) {
				if (a == 1) {
					Message message = new Message();
					handler.sendMessage(message);
				} else {
					Toast.makeText(context, "支付失败,请重新支付", Toast.LENGTH_SHORT).show();
				}
				dismiss();
			}

		}, "myjs");

	}
}

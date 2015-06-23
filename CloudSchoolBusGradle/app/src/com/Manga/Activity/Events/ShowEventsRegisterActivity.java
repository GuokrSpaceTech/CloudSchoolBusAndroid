package com.Manga.Activity.Events;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.Manga.Activity.R;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class ShowEventsRegisterActivity extends Activity {
	private String events_id;
	private int isSignup;
	private WebView webView;
	private Button register;
	private Button textsize;
	/**
	 * 设置文字大小布局
	 */
	private LinearLayout showSize;
	private TextView noData;
	/**
	 * 网络没有连通
	 */
	private static final int NETISNOTWORKING = 0;
	/**
	 * 报名成功
	 */
	private static final int APPLY_SUCCESS = 1;
	/**
	 * 取消报名
	 */
	private static final int APPLY_SUCCESS_ = 2;
	/**
	 * 操作失败
	 */
	private static final int MANGAGEFAIL = 3;
	/**
	 * 操作失败未开始
	 */
	private static final int REGISTERFU5 = 4;
	/**
	 * 操作失败已经结束
	 */
	private static final int REGISTERFU6 = 5;
	/**
	 * 操作失败满员
	 */
	private static final int REGISTERFU7 = 6;
	/**
	 * 操作失败报名未成功
	 */
	private static final int REGISTERFU8 = 7;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case NETISNOTWORKING:
				Toast.makeText(ShowEventsRegisterActivity.this, R.string.have_no_network, Toast.LENGTH_SHORT).show();
				break;
			case APPLY_SUCCESS:
				Toast.makeText(ShowEventsRegisterActivity.this, R.string.apply_success, Toast.LENGTH_SHORT).show();
				register.setBackgroundResource(R.drawable.activity_apply_have);
				break;
			case APPLY_SUCCESS_:
				Toast.makeText(ShowEventsRegisterActivity.this, R.string.apply_success_, Toast.LENGTH_SHORT).show();
				register.setBackgroundResource(R.drawable.activity_apply);
				break;
			case MANGAGEFAIL:
				Toast.makeText(ShowEventsRegisterActivity.this, R.string.manage_fail, Toast.LENGTH_SHORT).show();
				break;
			case REGISTERFU5:
				Toast.makeText(ShowEventsRegisterActivity.this, R.string.registerimg_msg_fu5, Toast.LENGTH_SHORT).show();
				break;
			case REGISTERFU6:
				Toast.makeText(ShowEventsRegisterActivity.this, R.string.registerimg_msg_fu6, Toast.LENGTH_SHORT).show();
				break;
			case REGISTERFU7:
				Toast.makeText(ShowEventsRegisterActivity.this, R.string.registerimg_msg_fu7, Toast.LENGTH_SHORT).show();
				break;
			case REGISTERFU8:
				Toast.makeText(ShowEventsRegisterActivity.this, R.string.registerimg_msg_fu8, Toast.LENGTH_SHORT).show();
				break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_register);

		webView = (WebView) findViewById(R.id.webView);
		showSize = (LinearLayout) findViewById(R.id.show_set_size);
		noData = (TextView) findViewById(R.id.no_data);
		register = (Button) findViewById(R.id.register_button);
		textsize = (Button) findViewById(R.id.textsize);
		Intent intent = getIntent();
		events_id = intent.getStringExtra("events_id");
		isSignup = Integer.parseInt(intent.getStringExtra("isSignup"));
		String htmlurl = intent.getStringExtra("htmlurl");
		initSize();
		ShareSDK.initSDK(this);
		ShareSDK.setNetworkDevInfoEnable(false);
		if (htmlurl == null || htmlurl.equals("")) {
			noData.setVisibility(View.VISIBLE);
			webView.setVisibility(View.GONE);
		} else {
			noData.setVisibility(View.GONE);
			webView.setVisibility(View.VISIBLE);
 			webView.loadUrl(htmlurl);
		}
		if (isSignup == 1) {
			register.setBackgroundResource(R.drawable.activity_apply_have);
		} else {
			register.setBackgroundResource(R.drawable.activity_apply);
		}
		//UpdateSizePic(2);
	}
	private void initSize(){
		SharedPreferences sp =this.getSharedPreferences("Activity",Context.MODE_PRIVATE);
		int tag = sp.getInt("size", 2);
		switch (tag) {
		case 1:
			setSizeOne(null);
			break;
		case 2:
			setSizeTwo(null);
			break;
		case 3:
			setSizeThree(null);
			break;
		case 4:
			setSizeFour(null);
			break;
		default:
			setSizeTwo(null);
			break;
		}
	}
	public void backMenu(View v) {
		ActivityUtil.close(this);
	}

	private void showShare(boolean silent, String platform) {
		OnekeyShare oks = new OnekeyShare();
		oks.setNotification(R.drawable.icon, getString(R.string.app_name));
		Intent intent = getIntent();
		oks.setTitle(intent.getStringExtra("title"));
		oks.setText(intent.getStringExtra("title"));
		oks.setSilent(silent);
		oks.setPlatform(platform);

		oks.show(ShowEventsRegisterActivity.this);
	}

	/*
	 * 单机报名
	 */
	public void register(View v) {
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		View view=View.inflate(this, R.layout.dialog_apply, null);
		final AlertDialog dia=builder.create();
		dia.setView(view,0,0,0,0);
		TextView message=(TextView) view.findViewById(R.id.message);
		Button cancel=(Button) view.findViewById(R.id.cancel);
		Button apply=(Button) view.findViewById(R.id.set);
		if (isSignup == 1) {
			message.setText(R.string.apply_dialog_cancel);
			apply.setText(R.string.apply_dialog_baoming_cancel);
		} else {
			message.setText(R.string.apply_dialog);
			apply.setText(R.string.apply_dialog_baoming);
		}
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dia.dismiss();
			}
		});
		apply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				register();
				dia.dismiss();
			}
		});
		dia.show();
	}
	/**
	 * 报名
	 */
	private void register(){
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (HttpUtil.isNetworkConnected(ShowEventsRegisterActivity.this)) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("id", events_id);
					Params param = new Params("events", map);
					Result result = HttpUtil.httpPost(ShowEventsRegisterActivity.this, param);
					if (result == null) {

					} else if ("1".equals(result.getCode())) {
						switch (isSignup) {
						case 0:
							handler.sendEmptyMessage(APPLY_SUCCESS);
							isSignup = 1;
							break;
						case 1:
							handler.sendEmptyMessage(APPLY_SUCCESS_);
							isSignup = 0;
							break;
						}

					} else if ("-5".equals(result.getCode())) {
						handler.sendEmptyMessage(REGISTERFU5);
					} else if ("-6".equals(result.getCode())) {
						handler.sendEmptyMessage(REGISTERFU6);
					} else if ("-7".equals(result.getCode())) {
						handler.sendEmptyMessage(REGISTERFU7);
					} else if ("-8".equals(result.getCode())) {
						handler.sendEmptyMessage(REGISTERFU8);
					} else {
						handler.sendEmptyMessage(REGISTERFU8);
					}
				} else {
					handler.sendEmptyMessage(NETISNOTWORKING);
				}
			}
		});
		thread.start();
	}

	/**
	 * 设置文字大小
	 * 
	 * @param v
	 */
	public void setSize(View v) {
		if (View.VISIBLE == showSize.getVisibility()) {
			textsize.setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_size));
			showSize.setVisibility(View.GONE);
		} else {
			textsize.setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_size_have));
			showSize.setVisibility(View.VISIBLE);
		}
	}

	public void setSizeOne(View v) {
		webView.getSettings().setTextSize(TextSize.SMALLER);
		SharedPreferences sp =this.getSharedPreferences("Activity",Context.MODE_PRIVATE);
		sp.edit().putInt("size", 1).commit();
		UpdateSizePic(1);
	}

	public void setSizeTwo(View v) {
		webView.getSettings().setTextSize(TextSize.NORMAL);
		SharedPreferences sp =this.getSharedPreferences("Activity",Context.MODE_PRIVATE);
		sp.edit().putInt("size", 2).commit();
		UpdateSizePic(2);
	}

	public void setSizeThree(View v) {
		webView.getSettings().setTextSize(TextSize.LARGER);
		SharedPreferences sp =this.getSharedPreferences("Activity",Context.MODE_PRIVATE);
		sp.edit().putInt("size", 3).commit();
		UpdateSizePic(3);
	}

	public void setSizeFour(View v) {
		webView.getSettings().setTextSize(TextSize.LARGEST);
		SharedPreferences sp =this.getSharedPreferences("Activity",Context.MODE_PRIVATE);
		sp.edit().putInt("size", 4).commit();
		UpdateSizePic(4);
	}

	public void share(View view) {
		showShare(false, "");
	}

	private void UpdateSizePic(int nindex) {
		Button button01 = (Button) findViewById(R.id.btnsize01);
		Button button02 = (Button) findViewById(R.id.btnsize02);
		Button button03 = (Button) findViewById(R.id.btnsize03);
		Button button04 = (Button) findViewById(R.id.btnsize04);
		switch (nindex) {
		case 1:
			button01.setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_set_size_one_have));
			button02.setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_set_size_two));
			button03.setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_set_size_three));
			button04.setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_set_size_four));
			break;
		case 2:
			button01.setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_set_size_one));
			button02.setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_set_size_two_have));
			button03.setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_set_size_three));
			button04.setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_set_size_four));
			break;
		case 3:
			button01.setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_set_size_one));
			button02.setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_set_size_two));
			button03.setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_set_size_three_have));
			button04.setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_set_size_four));
			break;
		case 4:
			button01.setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_set_size_one));
			button02.setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_set_size_two));
			button03.setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_set_size_three));
			button04.setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_set_size_four_have));
			break;
		default:
			break;
		}
	}public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}

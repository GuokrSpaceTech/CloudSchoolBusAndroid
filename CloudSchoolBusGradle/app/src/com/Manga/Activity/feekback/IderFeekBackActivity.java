package com.Manga.Activity.feekback;

import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class IderFeekBackActivity extends BaseActivity {
	/**
	 * 连接超时
	 */
	private static final int OUTTIME=0;
	/**
	 * 进度条
	 */
	private static final int SHOWPROGRESS=1;
	/**
	 * 取消进度条显示
	 */
	private static final int DISMISSPROGRESS=2;
	/**
	 * 内容为空
	 */
	private static final int CONTENTNULL=3;
	/**
	 * 网络不存在
	 */
	private static final int NETISNTWORK=4;
	/**
	 * 递交成功
	 */
	private static final int SUBMITSUSS=5;
	/**
	 * 递交失败
	 */
	private static final int SUBMITFAIL=6;
	private ProgressDialog progressDialog;
	private EditText content;
	private Handler handler=new Handler(new Callback() {
		
		@Override
		public boolean handleMessage(Message mess) {
			// TODO Auto-generated method stub
			switch(mess.what){
			case OUTTIME:
				Toast.makeText(IderFeekBackActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				break;
			case SHOWPROGRESS:
				if(progressDialog==null){
					progressDialog=new ProgressDialog(IderFeekBackActivity.this);
					progressDialog.setMessage(getResources().getString(R.string.init_view));
				}
				progressDialog.show();
				break;
			case DISMISSPROGRESS:
				progressDialog.dismiss();
				break;
			case CONTENTNULL:
				Toast.makeText(IderFeekBackActivity.this, R.string.feekback_content_null, Toast.LENGTH_SHORT).show();
				break;
			case NETISNTWORK:
				Toast.makeText(IderFeekBackActivity.this, R.string.net_isnotwork, Toast.LENGTH_SHORT).show();
				break;
			case SUBMITSUSS:
				Toast.makeText(IderFeekBackActivity.this, R.string.feekback_su, Toast.LENGTH_SHORT).show();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(content.getWindowToken(), 0);
				content.setText("");
				break;
			case SUBMITFAIL:
				Toast.makeText(IderFeekBackActivity.this, R.string.feekback_fail, Toast.LENGTH_SHORT).show();
				InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				imm1.hideSoftInputFromWindow(content.getWindowToken(), 0);
				content.setText("");
				break;
			}
			return false;
		}
	});
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.idear_feekback);
		content=(EditText) findViewById(R.id.feekback_content);
	}
	public void backMenu(View v){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(content.getWindowToken(), 0);
		content.setText("");
		ActivityUtil.main.move();
	}
	public void submit(View v){
		if(content.getText().toString().trim().equals("")){
			handler.sendEmptyMessage(CONTENTNULL);
			return;
		}
		handler.sendEmptyMessage(SHOWPROGRESS);
		Thread thread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(HttpUtil.isNetworkConnected(IderFeekBackActivity.this)){
					HashMap<String, String> map=new HashMap<String, String>();
					map.put("content", content.getText().toString());
					Params param=new Params("feedback", map);
					Result result=HttpUtil.httpPost(IderFeekBackActivity.this, param);
					if(result==null){
						handler.sendEmptyMessage(OUTTIME);
					}else if("1".equals(result.getCode())){
						handler.sendEmptyMessage(SUBMITSUSS);
					}else{
						handler.sendEmptyMessage(SUBMITFAIL);
					}
				}else{
					handler.sendEmptyMessage(NETISNTWORK);
				}
				handler.sendEmptyMessage(DISMISSPROGRESS);
			}
		});
		thread.start();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (ActivityUtil.main != null) {
				ActivityUtil.main.move();
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
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

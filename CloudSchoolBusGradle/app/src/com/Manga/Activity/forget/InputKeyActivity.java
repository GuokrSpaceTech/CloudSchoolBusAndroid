package com.Manga.Activity.forget;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.LoginActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class InputKeyActivity extends BaseActivity {
	private TextView count;
	private TextView again;
	private TextView phoneInfo;
	private boolean clickClock;
	private EditText phoneNum;
	private LinearLayout lowe;
	private Thread thread;
	private String num;
	private String key;
	/**
	 * 网络没有连通
	 */
	private static final int NETISNOTWORKING=0;
	/**
	 * 号码为空
	 */
	private static final int PHONE_NULL=1;
	/**
	 * 号码位数不正确
	 */
	private static final int PHONE_LENGTH_WRONG=2;
	/**
	 * 号码未绑定
	 */
	private static final int PHONE_NO_BIND=3;
	private static final int REFRESH=4;
	
	private static final int REFRESH_LATE=5;
	private static final int KEY_WRONG=6;
	private static final int JUMP=7;
	private static final int SHOW=8;
	private static final int DISSHOW=9;
	private Handler handler=new Handler(new Callback() {
		
		@Override
		public boolean handleMessage(Message mes) {
			// TODO Auto-generated method stub
			switch(mes.what){
				case REFRESH:
					count.setText((String)mes.obj);
					break;
				case NETISNOTWORKING:
					Toast.makeText(InputKeyActivity.this, R.string.have_no_network, Toast.LENGTH_SHORT).show();
					break;
				case PHONE_NULL:
					Toast.makeText(InputKeyActivity.this, R.string.input_phone_num_null, Toast.LENGTH_SHORT).show();
					break;
				case PHONE_LENGTH_WRONG:
					Toast.makeText(InputKeyActivity.this, R.string.input_phone_num_length_wrong, Toast.LENGTH_SHORT).show();
					break;
				case PHONE_NO_BIND:
					Toast.makeText(InputKeyActivity.this, R.string.input_phone_no_bind, Toast.LENGTH_SHORT).show();
					break;
				case REFRESH_LATE:
					Toast.makeText(InputKeyActivity.this, R.string.please_input_phone_check_late, Toast.LENGTH_SHORT).show();
					break;
				case KEY_WRONG:
					Toast.makeText(InputKeyActivity.this, R.string.key_is_wrong, Toast.LENGTH_SHORT).show();
					break;
				case JUMP:
					ActivityUtil.startActivity(InputKeyActivity.this, (Intent)mes.obj);
					break;
				case SHOW:
					clickClock=true;
					lowe.setVisibility(View.VISIBLE);
					count.setVisibility(View.INVISIBLE);
					break;
				case DISSHOW:
					clickClock=false;
					count.setVisibility(View.VISIBLE);
					lowe.setVisibility(View.GONE);
					break;
			}
			return false;
		}
	});
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgetpw_intput_key);
		count=(TextView) findViewById(R.id.textView3);
		again=(TextView) findViewById(R.id.again);
		phoneInfo=(TextView) findViewById(R.id.textView2);
		phoneNum=(EditText) findViewById(R.id.phone_num);
		lowe=(LinearLayout) findViewById(R.id.lowe);
		num=getIntent().getStringExtra("foo");
		key=getIntent().getStringExtra("key");
		phoneInfo.setText(getResources().getString(R.string.please_input_phone_check_info_befor)+num+getResources().getString(R.string.please_input_phone_check_info_after));
		again.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		count(60);
	}

	public void next(View v) {
		Thread thread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(phoneNum.getText().toString().trim().equals(key)){
					Intent intent=new Intent(InputKeyActivity.this,AgainSetPwActivity.class);
					intent.putExtra("mobile", num);
					intent.putExtra("key", key);
					Log.i("key", key);
					handler.sendMessage(handler.obtainMessage(JUMP, intent));
					//ActivityUtil.startActivity(InputKeyActivity.this, intent);
				}else{
					handler.sendEmptyMessage(KEY_WRONG);
				}
			}
		});
		thread.start();
	}

	public void backMenu(View v) {
		ActivityUtil.close(this);
	}
	public void again(View v){
		
		Thread thread_=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(clickClock){
					if(HttpUtil.isNetworkConnected(InputKeyActivity.this)){
						HashMap<String, String> map=new HashMap<String, String>();
						map.put("mobile",num);
						Result result=HttpUtil.httpGet(InputKeyActivity.this, new Params("forgetpwd", map));
						if(result==null){
							handler.sendEmptyMessage(PHONE_NO_BIND);
						}else if("1".equals(result.getCode())){
							try {
								JSONObject object=new JSONObject(result.getContent());
								key=object.getString("key");
								Log.i("key", key);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							count(60);
						}else{
							handler.sendEmptyMessage(PHONE_NO_BIND);
						}
					}else{
						handler.sendEmptyMessage(NETISNOTWORKING);				
					}
				}else{
					handler.sendEmptyMessage(REFRESH_LATE);
				}
			}
		});
		thread_.start();
	}
	/**
	 * 启动刷新
	 */
	private void count(final int top){
		thread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int max=top;
				handler.sendEmptyMessage(DISSHOW);
				while(max>0){
					max--;
					handler.sendMessage(handler.obtainMessage(REFRESH, max+getResources().getString(R.string.please_input_phone_check_info_one)));
					try {
						thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				handler.sendEmptyMessage(SHOW);
			}
		});
		thread.start();
	}public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}

package com.Manga.Activity.forget;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.LoginActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.encryption.ooo;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class AgainSetPwActivity extends BaseActivity {
	private String mobile;
	private String key;
	private EditText newPw;
	private EditText newPwMore;
	private static final int PW_IS_NULL=0;
	private static final int PW_NOT_SAME=1;
	/**
	 * 网络没有连通
	 */
	private static final int NETISNOTWORKING=2;
	/**
	 * 密码修改失败
	 */
	private static final int PW_FAIL=3;
	private static final int JUMP=4;
	private Handler handler=new Handler(new Callback() {
		
		@Override
		public boolean handleMessage(Message mes) {
			// TODO Auto-generated method stub
			switch(mes.what){
				case PW_IS_NULL:
					Toast.makeText(AgainSetPwActivity.this, R.string.pw_cannt_empty, Toast.LENGTH_SHORT).show();
					break;
				case PW_NOT_SAME:
					Toast.makeText(AgainSetPwActivity.this, R.string.pw_net_isnot_eq, Toast.LENGTH_SHORT).show();
					break;
				case NETISNOTWORKING:
					Toast.makeText(AgainSetPwActivity.this, R.string.have_no_network, Toast.LENGTH_SHORT).show();
					break;
				case PW_FAIL:
					Toast.makeText(AgainSetPwActivity.this, R.string.pw_fail, Toast.LENGTH_SHORT).show();
					newPw.setText("");
					newPwMore.setText("");
					break;
				case JUMP:
					ActivityUtil.startActivity(AgainSetPwActivity.this, (Intent)mes.obj);
					break;
			}
			return false;
		}
	});
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forget_set_again_pw);
		newPw=(EditText) findViewById(R.id.new_pw);
		newPwMore=(EditText) findViewById(R.id.new_pw_more);
		Intent intent=getIntent();
		mobile=intent.getStringExtra("mobile");
		key=intent.getStringExtra("key");
	}
	public void wancheng(View v){
		Thread thread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(newPw.getText().toString().trim().equals("")||newPwMore.getText().toString().trim().equals("")){
					handler.sendEmptyMessage(PW_IS_NULL);
				}else if(newPw.getText().toString().trim().equals(newPwMore.getText().toString().trim())){
					if(HttpUtil.isNetworkConnected(AgainSetPwActivity.this)){
						HashMap<String, String> map=new HashMap<String, String>();
						map.put("mobile", mobile);
						map.put("key", key);
						map.put("password", ooo.h(newPw.getText().toString().trim(), "mactop", 0));
						Result result=HttpUtil.httpPost(AgainSetPwActivity.this, new Params("forgetpwd", map));
						if(result==null){
							handler.sendEmptyMessage(PW_FAIL);
						}else if("1".equals(result.getCode())){
							Intent intent=new Intent(AgainSetPwActivity.this,LoginActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							handler.sendMessage(handler.obtainMessage(JUMP, intent));
						}else{
							handler.sendEmptyMessage(PW_FAIL);
						}
					}else{
						handler.sendEmptyMessage(NETISNOTWORKING);
					}
				}else{
					handler.sendEmptyMessage(PW_NOT_SAME);
				}
			}
		});
		thread.start();
	}
	public void backMenu(View view){
		ActivityUtil.close(this);
	}public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}

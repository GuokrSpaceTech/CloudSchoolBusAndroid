package com.Manga.Activity.modifi;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.Student_Info;
import com.umeng.analytics.MobclickAgent;

public class SetPasswordActivity extends BaseActivity {
	/**
	 * 连接超时
	 */
	private static final int OUTTIME = 0;
	/**
	 * 密码为空
	 */
	private static final int PWISEMPTY = 1;
	/**
	 * 网络没有连通
	 */
	private static final int NETISNOTWORKING = 2;
	/**
	 * 两次密码不一致
	 */
	private static final int PWISNTEQUALS = 3;
	/**
	 * 修改成功
	 */
	private static final int MODIFIPWSUCC = 4;
	/**
	 * 密码修改失败
	 */
	private static final int PWMODIFIFAILE = 5;
	private EditText oldPW;
	private EditText newPW;
	private EditText newPWM;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case OUTTIME:
				Toast.makeText(SetPasswordActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				break;
			case PWISEMPTY:
				Toast.makeText(SetPasswordActivity.this, R.string.pw_cannt_empty, Toast.LENGTH_SHORT).show();
				break;
			case NETISNOTWORKING:
				Toast.makeText(SetPasswordActivity.this, R.string.pw_net_isnot_online, Toast.LENGTH_SHORT).show();
				break;
			case PWISNTEQUALS:
				Toast.makeText(SetPasswordActivity.this, R.string.pw_net_isnot_eq, Toast.LENGTH_SHORT).show();
				break;
			case MODIFIPWSUCC:
				Toast.makeText(SetPasswordActivity.this, R.string.pw_sucesss, Toast.LENGTH_SHORT).show();
				break;
			case PWMODIFIFAILE:
				Toast.makeText(SetPasswordActivity.this, R.string.pw_fail, Toast.LENGTH_SHORT).show();
				break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modification_password);
		oldPW = (EditText) findViewById(R.id.old_password);
		newPW = (EditText) findViewById(R.id.new_pw);
		newPWM = (EditText) findViewById(R.id.new_pw_more);

	}

	public void backMenu(View v) {
		ActivityUtil.close(this);
	}

	public void submit(View v) {
		if (oldPW.getText().toString().trim().equals("") || newPW.getText().toString().trim().equals("")
				|| newPWM.getText().toString().trim().equals("")) {
			handler.sendEmptyMessage(PWISEMPTY);
			return;
		}
		if (!newPW.getText().toString().trim().equals(newPWM.getText().toString().trim())) {
			handler.sendEmptyMessage(PWISNTEQUALS);
			return;
		}
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (HttpUtil.isNetworkConnected(SetPasswordActivity.this)) {
					ActivityUtil.main.showPRO();
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("old", com.Manga.Activity.encryption.ooo.h(oldPW.getText().toString(), "mactop", 0));
					map.put("new", com.Manga.Activity.encryption.ooo.h(newPW.getText().toString(), "mactop", 0));
					Params param = new Params("password", map);
					Result result = HttpUtil.httpPost(SetPasswordActivity.this, param);
					if (result == null) {
						ActivityUtil.main.disPRO();
						handler.sendEmptyMessage(OUTTIME);
					} else if ("1".equals(result.getCode())) {
						ActivityUtil.main.disPRO();
						handler.sendEmptyMessage(MODIFIPWSUCC);
						Student_Info.uid = "";
						SharedPreferences sp = SetPasswordActivity.this.getSharedPreferences("LoggingData",
								Context.MODE_PRIVATE);
						Editor editor = sp.edit();
						editor.putBoolean("isAutoLogging", false);
						editor.putString("password", "");
						editor.putString("username", "");
						editor.commit();
						Intent intent = new Intent(SetPasswordActivity.this, LoginActivity.class);
						startActivity(intent);
						ActivityUtil.main.finish();
					} else {
						handler.sendEmptyMessage(PWMODIFIFAILE);
					}

				} else {
					handler.sendEmptyMessage(NETISNOTWORKING);
				}
			}
		});
		thread.start();
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

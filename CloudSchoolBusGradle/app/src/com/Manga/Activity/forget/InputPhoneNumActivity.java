package com.Manga.Activity.forget;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.MainActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.Msg.MsgActivity;
import com.Manga.Activity.bindingPhone.BindingPhoneChangeActivity;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class InputPhoneNumActivity extends BaseActivity {
	/**
	 * 网络没有连通
	 */
	private static final int NETISNOTWORKING = 0;
	/**
	 * 号码为空
	 */
	private static final int PHONE_NULL = 1;
	/**
	 * 号码位数不正确
	 */
	private static final int PHONE_LENGTH_WRONG = 2;
	/**
	 * 号码未绑定
	 */
	private static final int PHONE_NO_BIND = 3;
	private Thread thread;
	private static final int JUMP = 4;
	private static final int REFRESH = 5;
	private static final int SHOW = 6;
	private static final int DISSHOW = 7;
	private static final int MODIFIONLINESUCCES = 8;
	private static final int KEY_WRONG = 9;
	private static final int MESSAGE1 = 11;
	private static final int MESSAGE2 = 12;
	private static final int SHOWPROGRESS = 13;
	private static final int DISMISSPROGRESS = 14;
	private static final int SHOW_HAD=15;
	private String key;
	private EditText phoneNum;
	private EditText phoneYanzheng;
	private Button buttonGet;
	private String strPhone;
	private ProgressDialog progressDialog;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message mes) {
			// TODO Auto-generated method stub
			switch (mes.what) {
			case SHOW:
				buttonGet.setClickable(true);
				buttonGet.setBackgroundResource(R.drawable.getyanzheng);
				buttonGet.setText(getResources().getString(R.string.getverification));
				break;
			case DISSHOW:
				buttonGet.setClickable(false);
				buttonGet.setBackgroundResource(R.drawable.getyanzhengnull);
				buttonGet.setText(getResources().getString(R.string.binding_phone_verification_label2_prev) + "180" + getResources().getString(R.string.binding_phone_verification_label2_next));
				break;
			case REFRESH:
				buttonGet.setText((String) mes.obj);
				break;
			case NETISNOTWORKING:
				Toast.makeText(InputPhoneNumActivity.this, R.string.have_no_network, Toast.LENGTH_SHORT).show();
				break;
			case PHONE_NULL:
				Toast.makeText(InputPhoneNumActivity.this, R.string.input_phone_num_null, Toast.LENGTH_SHORT).show();
				break;
			case PHONE_LENGTH_WRONG:
				Toast.makeText(InputPhoneNumActivity.this, R.string.input_phone_num_length_wrong, Toast.LENGTH_SHORT).show();
				break;
			case PHONE_NO_BIND:
				Toast.makeText(InputPhoneNumActivity.this, R.string.input_phone_no_bind, Toast.LENGTH_SHORT).show();
				break;
			case MODIFIONLINESUCCES:
				strPhone = (String) mes.obj;
				count(180);
				break;
			case JUMP:
				ActivityUtil.startActivity(InputPhoneNumActivity.this, (Intent) mes.obj);
				break;
			case KEY_WRONG:
				Toast.makeText(InputPhoneNumActivity.this, R.string.key_is_wrong, Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE1:
				Intent intent = new Intent(InputPhoneNumActivity.this, MsgActivity.class);
				startActivity(intent);
				break;
			case MESSAGE2:
				Toast.makeText(InputPhoneNumActivity.this, R.string.msg_msg1, Toast.LENGTH_SHORT).show();
				break;
			case SHOWPROGRESS:
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(InputPhoneNumActivity.this);
					progressDialog.setMessage(getResources().getString(R.string.init_view));
					progressDialog.setCancelable(false);
				}
				progressDialog.show();
				break;
			case DISMISSPROGRESS:
				progressDialog.dismiss();
				// LoggingActivity.this.finish();
				break;
			case SHOW_HAD:
				Toast.makeText(InputPhoneNumActivity.this, R.string.gai_zhang_hu_yi_bang_ding, Toast.LENGTH_SHORT).show();
				break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forget_input_phone);
		buttonGet = (Button) findViewById(R.id.button_get);
		phoneNum = (EditText) findViewById(R.id.phone_num);
		phoneYanzheng = (EditText) findViewById(R.id.editphonenumber3);
	}

	public void call(View view) {
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "4006063996"));
		startActivity(intent);
	}

	public void backMenu(View v) {
		this.finish();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

	public void next(View v) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(SHOWPROGRESS);

				if ("".equals(phoneNum.getText().toString().trim())) {
					handler.sendEmptyMessage(PHONE_NULL);
					handler.sendEmptyMessage(DISMISSPROGRESS);
					return;
				}
				if (phoneNum.getText().toString().trim().length() < 11) {
					handler.sendEmptyMessage(PHONE_LENGTH_WRONG);
					handler.sendEmptyMessage(DISMISSPROGRESS);
					return;
				}
				if (HttpUtil.isNetworkConnected(InputPhoneNumActivity.this)) {
					HashMap<String, String> map = new HashMap<String, String>();
					String foo = phoneNum.getText().toString().trim();
					map.put("mobile", foo);
					Result result = HttpUtil.httpGet(InputPhoneNumActivity.this, new Params("forgetpwd", map));
					if (result == null) {
						handler.sendEmptyMessage(PHONE_NO_BIND);
						handler.sendEmptyMessage(DISMISSPROGRESS);
					} else if ("1".equals(result.getCode())) {
						try {
							JSONObject object = new JSONObject(result.getContent());
							key = object.getString("key");
							handler.sendEmptyMessage(DISMISSPROGRESS);
							handler.sendMessage(handler.obtainMessage(MODIFIONLINESUCCES, foo));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							handler.sendEmptyMessage(DISMISSPROGRESS);
							handler.sendEmptyMessage(PHONE_NO_BIND);
							e.printStackTrace();
						}
					} else if ("-8".equals(result.getCode())) {
						handler.sendEmptyMessage(DISMISSPROGRESS);
						handler.sendEmptyMessage(MESSAGE1);
					} else if ("-9".equals(result.getCode())) {
						try {
							JSONObject object = new JSONObject(result.getContent());
							key = object.getString("key");
							handler.sendMessage(handler.obtainMessage(MODIFIONLINESUCCES, foo));
							handler.sendEmptyMessage(DISMISSPROGRESS);
							handler.sendEmptyMessage(MESSAGE2);
						} catch (JSONException e) {
							handler.sendEmptyMessage(DISMISSPROGRESS);
							handler.sendEmptyMessage(PHONE_NO_BIND);
							e.printStackTrace();
						}
					}else if("-10".equals(result.getCode())){
						handler.sendEmptyMessage(SHOW_HAD);
					}else {
						handler.sendEmptyMessage(DISMISSPROGRESS);
						handler.sendEmptyMessage(PHONE_NO_BIND);
					}
				} else {
					handler.sendEmptyMessage(DISMISSPROGRESS);
					handler.sendEmptyMessage(NETISNOTWORKING);
				}
			}
		});
		thread.start();
	}

	public void send(View v) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (phoneYanzheng.getText().toString().trim().equals(key)) {
					Intent intent = new Intent(InputPhoneNumActivity.this, AgainSetPwActivity.class);
					intent.putExtra("mobile", strPhone);
					intent.putExtra("key", key);
					handler.sendMessage(handler.obtainMessage(JUMP, intent));
				} else {
					handler.sendEmptyMessage(KEY_WRONG);
				}
			}
		});
		thread.start();
	}

	private void count(final int top) {
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int max = top;
				handler.sendEmptyMessage(DISSHOW);
				while (max > 0) {
					max--;
					handler.sendMessage(handler.obtainMessage(REFRESH, max + getResources().getString(R.string.please_input_phone_check_info_one)));
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
		if (!thread.isAlive()) {
			thread.start();
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

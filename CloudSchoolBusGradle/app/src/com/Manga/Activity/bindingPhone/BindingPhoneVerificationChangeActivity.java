package com.Manga.Activity.bindingPhone;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class BindingPhoneVerificationChangeActivity extends BaseActivity {
	/**
	 * 超时
	 */
	/**
	 * 失败
	 */
	private static final int MODIFIMOREPHONEFAIL = 1;
	/**
	 * 成功
	 */
	private static final int MODIFIONLINESUCCES = 2;
	/**
	 * 手机格式错误
	 */
	private static final int PHONETYPEFAIL = 3;
	/**
	 * 发送短信失败
	 */
	private static final int MESSAGEFAIL = 4;
	private static final int SHOWPROGRESS = 5;
	private static final int NETOUT = 0;
	private static final int SHOW = 6;
	private static final int DISSHOW = 7;
	private static final int REFRESH = 8;
	/**
	 * 绑定成功
	 */
	private static final int MODIFIONLINESUCCESS = 9;
	private Thread thread;
	private Thread thread2;
	private TextView textViewLabel1;
	private TextView textViewLabel2;
	private String strMobile;
	private String strMobileold;
	private ProgressDialog progressDialog;
	private String strKey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bindingphoneverification);
		Log.i("11111111111111111", getIntent().getStringExtra("key") + "");
		strKey = getIntent().getStringExtra("key");
		strMobile = getIntent().getStringExtra("mobile") + "";
		strMobileold = getIntent().getStringExtra("mobileold") + "";
		textViewLabel1 = (TextView) findViewById(R.id.textlabel1);
		textViewLabel2 = (TextView) findViewById(R.id.textlabel2);
		TextView textView = (TextView) findViewById(R.id.bindingphonelabel);
		textView.setText(getResources().getString(R.string.binding_phone_verification_label1_prev) + strMobile
				+ getResources().getString(R.string.binding_phone_verification_label1_next));
		textViewLabel2.setVisibility(View.GONE);
		textViewLabel1.setText(getResources().getString(R.string.binding_phone_verification_label2_prev) + "60"
				+ getResources().getString(R.string.binding_phone_verification_label2_next));
		count(60);
	}

	private Handler handler = new Handler(new Callback() {
		public boolean handleMessage(Message mess) {
			// TODO Auto-generated method stub
			switch (mess.what) {
			case SHOWPROGRESS:
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(BindingPhoneVerificationChangeActivity.this);
					progressDialog.setMessage(getResources().getString(R.string.init_view));
				}
				progressDialog.show();
				break;
			case NETOUT:
				Toast.makeText(BindingPhoneVerificationChangeActivity.this, R.string.binding_message_out,
						Toast.LENGTH_SHORT).show();
				break;
			case SHOW:
				textViewLabel2.setVisibility(View.VISIBLE);
				textViewLabel1.setText(getResources().getString(R.string.binding_phone_verification_label3));
				break;
			case DISSHOW:
				textViewLabel2.setVisibility(View.GONE);
				textViewLabel1.setText(getResources().getString(R.string.binding_phone_verification_label2_prev) + "60"
						+ getResources().getString(R.string.binding_phone_verification_label2_next));
				break;
			case REFRESH:
				textViewLabel1.setText((String) mess.obj);
				break;
			case MODIFIMOREPHONEFAIL:
				Toast.makeText(BindingPhoneVerificationChangeActivity.this, R.string.binding_message_fail,
						Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				break;
			case MODIFIONLINESUCCES:
				Toast.makeText(BindingPhoneVerificationChangeActivity.this, R.string.binding_message_success,
						Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				strKey = (String) mess.obj;
				Log.i("222222222222", (String) mess.obj);
				count(60);
				break;
			case PHONETYPEFAIL:
				Toast.makeText(BindingPhoneVerificationChangeActivity.this, R.string.binding_message_fail_type,
						Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				break;
			case MESSAGEFAIL:
				Toast.makeText(BindingPhoneVerificationChangeActivity.this, R.string.binding_message_fail_message,
						Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				break;
			case MODIFIONLINESUCCESS:
				Toast.makeText(BindingPhoneVerificationChangeActivity.this, R.string.binding_message_success,
						Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				UpdateDB();
				ActivityUtil.close(BindingPhoneVerificationChangeActivity.this);
			}
			return false;
		}
	});

	private void count(final int top) {
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int max = top;
				handler.sendEmptyMessage(DISSHOW);
				while (max > 0) {
					max--;
					handler.sendMessage(handler.obtainMessage(REFRESH,
							getResources().getString(R.string.binding_phone_verification_label2_prev) + max
									+ getResources().getString(R.string.binding_phone_verification_label2_next)));
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
	}

	public void reSend(View view) {
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("newsmobile", strMobile);
		map.put("historymobile", strMobileold);

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				handler.sendEmptyMessage(SHOWPROGRESS);
				// TODO Auto-generated method stub
				if (HttpUtil.isNetworkConnected(BindingPhoneVerificationChangeActivity.this)) {
					Params param = new Params("Bindreplace", map);
					Result result = HttpUtil.httpGet(BindingPhoneVerificationChangeActivity.this, param);
					if (result == null) {
						handler.sendEmptyMessage(MODIFIMOREPHONEFAIL);
					} else if ("1".equals(result.getCode())) {
						JSONObject myJson;
						try {
							myJson = new JSONObject(result.getContent());
							Message message = handler.obtainMessage(MODIFIONLINESUCCES, myJson.getString("key"));
							handler.sendMessage(message);
						} catch (JSONException e) {
							handler.sendEmptyMessage(MODIFIMOREPHONEFAIL);
							e.printStackTrace();
						}

					} else if ("-1".equals(result.getCode())) {
						handler.sendEmptyMessage(MODIFIMOREPHONEFAIL);
					} else if ("-2".equals(result.getCode())) {
						handler.sendEmptyMessage(PHONETYPEFAIL);
					} else if ("-3".equals(result.getCode())) {
						handler.sendEmptyMessage(MODIFIMOREPHONEFAIL);
					} else if ("-4".equals(result.getCode())) {
						handler.sendEmptyMessage(MESSAGEFAIL);
					} else {
						handler.sendEmptyMessage(NETOUT);
					}
				}

			}
		});
		thread.start();
	}

	public void close(View view) {
		ActivityUtil.close(BindingPhoneVerificationChangeActivity.this);
	}

	public void next(View view) {
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("mobile", strMobile);
		map.put("key", strKey);
		thread2 = new Thread(new Runnable() {

			@Override
			public void run() {
				handler.sendEmptyMessage(SHOWPROGRESS);
				if (HttpUtil.isNetworkConnected(BindingPhoneVerificationChangeActivity.this)) {
					Params param = new Params("Bindreplace", map);
					Result result = HttpUtil.httpPost(BindingPhoneVerificationChangeActivity.this, param);
					if (result == null) {
						handler.sendEmptyMessage(MODIFIMOREPHONEFAIL);
					} else if ("1".equals(result.getCode())) {
						handler.sendEmptyMessage(MODIFIONLINESUCCESS);
					} else if ("-5".equals(result.getCode())) {
						handler.sendEmptyMessage(MODIFIMOREPHONEFAIL);
					} else {
						handler.sendEmptyMessage(NETOUT);
					}
				}
			}
		});
		if (!thread2.isAlive()) {
			thread2.start();
		}
	}

	public void UpdateDB() {
		try {
			DB db = new DB(this);
			SQLiteDatabase sql = db.getReadableDatabase();
			ContentValues values = new ContentValues();
			values.put("ischeck_mobile", "1");
			values.put("mobile", strMobile);
			Cursor cur = sql.query("student_info", null, null, null, null, null, null);
			if (cur.getCount() > 0) {
				sql.update("student_info", values, "mobile=?", new String[] { strMobileold });
			}
			cur.close();
			sql.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
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

package com.Manga.Activity.bindingPhone;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.Msg.MsgActivity;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.Student_Info;
import com.umeng.analytics.MobclickAgent;

public class BindingPhoneActivity extends BaseActivity {
	/**
	 * 超时
	 */
	private static final int NETOUT = 0;
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
	private static final int PHONETYPEFAIL = 4;
	/**
	 * 发送短信失败
	 */
	private static final int MESSAGEFAIL = 6;
	private static final int SHOWPROGRESS = 3;
	private static final int SHOW = 7;
	private static final int DISSHOW = 8;
	private static final int REFRESH = 9;
	private static final int MESSAGE1 = 11;
	private static final int MESSAGE2 = 12;
	/**
	 * 绑定成功
	 */
	private static final int MODIFIONLINESUCCESS = 10;
	private Thread thread;
	private Thread thread2;
	private String strPhone;
	private String strKey;
	private ProgressDialog progressDialog;
	private Button buttonGet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bindingphone);
		buttonGet = (Button) findViewById(R.id.button_get);
	}

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message mess) {
			// TODO Auto-generated method stub
			switch (mess.what) {
			case SHOW:
				buttonGet.setClickable(true);
				buttonGet.setBackgroundResource(R.drawable.getyanzheng);
				buttonGet.setText(getResources().getString(R.string.getverification));
				break;
			case DISSHOW:
				buttonGet.setClickable(false);
				buttonGet.setBackgroundResource(R.drawable.getyanzhengnull);
				buttonGet.setText(getResources().getString(R.string.binding_phone_verification_label2_prev) + "180"
						+ getResources().getString(R.string.binding_phone_verification_label2_next));
				break;
			case REFRESH:
				buttonGet.setText((String) mess.obj);
				break;
			case SHOWPROGRESS:
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(BindingPhoneActivity.this);
					progressDialog.setMessage(getResources().getString(R.string.init_view));
				}
				progressDialog.show();
				break;
			case NETOUT:
				Toast.makeText(BindingPhoneActivity.this, R.string.binding_message_out, Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				break;
			case MODIFIMOREPHONEFAIL:
				Toast.makeText(BindingPhoneActivity.this, R.string.binding_message_fail, Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				break;
			case MODIFIONLINESUCCES:
				Toast.makeText(BindingPhoneActivity.this, R.string.binding_message_success, Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				EditText edittext = (EditText) findViewById(R.id.editphonenumber1);
				strPhone = edittext.getText().toString();
				strKey = (String) mess.obj;
				count(180);
				// intent.putExtra("mobile", strPhone);
				// intent.putExtra("key", (String)mess.obj);
				break;
			case PHONETYPEFAIL:
				Toast.makeText(BindingPhoneActivity.this, R.string.binding_message_fail_type, Toast.LENGTH_SHORT)
						.show();
				progressDialog.dismiss();
				break;
			case MESSAGEFAIL:
				Toast.makeText(BindingPhoneActivity.this, R.string.binding_message_fail_message, Toast.LENGTH_SHORT)
						.show();
				progressDialog.dismiss();
				break;
			case MODIFIONLINESUCCESS:
				Toast.makeText(BindingPhoneActivity.this, R.string.binding_message_success, Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				UpdateDB();
				ActivityUtil.close(BindingPhoneActivity.this);
				break;
			case MESSAGE1:
				Intent intent = new Intent(BindingPhoneActivity.this, MsgActivity.class);
				startActivity(intent);
				progressDialog.dismiss();
				break;
			case MESSAGE2:
				Toast.makeText(BindingPhoneActivity.this, R.string.msg_msg1, Toast.LENGTH_LONG).show();
				progressDialog.dismiss();
				break;
			}
			return false;
		}
	});

	public void next(View view) {
		EditText edittext = (EditText) findViewById(R.id.editphonenumber1);
		String strPhone = edittext.getText().toString();
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("mobile", strPhone);

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				handler.sendEmptyMessage(SHOWPROGRESS);
				// TODO Auto-generated method stub
				if (HttpUtil.isNetworkConnected(BindingPhoneActivity.this)) {
					Params param = new Params("Bind", map);
					Result result = HttpUtil.httpGet(BindingPhoneActivity.this, param);
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
					} else if ("-8".equals(result.getCode())) {
						handler.sendEmptyMessage(MESSAGE1);
					} else if ("-9".equals(result.getCode())) {
						JSONObject myJson;
						try {
							myJson = new JSONObject(result.getContent());
							Message message = handler.obtainMessage(MODIFIONLINESUCCES, myJson.getString("key"));
							handler.sendMessage(message);
							handler.sendEmptyMessage(MESSAGE2);
						} catch (JSONException e) {
							handler.sendEmptyMessage(MODIFIMOREPHONEFAIL);
							e.printStackTrace();
						}

					} else {
						handler.sendEmptyMessage(NETOUT);
					}
				}

			}
		});
		thread.start();
	}

	public void close(View view) {
		ActivityUtil.close(this);
	}

	public void send(View view) {
		EditText editText = (EditText) findViewById(R.id.editphonenumber3);
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("mobile", strPhone);
		map.put("key", editText.getText().toString());
		thread2 = new Thread(new Runnable() {

			@Override
			public void run() {
				handler.sendEmptyMessage(SHOWPROGRESS);
				if (HttpUtil.isNetworkConnected(BindingPhoneActivity.this)) {
					Params param = new Params("Bind", map);
					Result result = HttpUtil.httpPost(BindingPhoneActivity.this, param);
					if (result == null) {
						handler.sendEmptyMessage(MODIFIMOREPHONEFAIL);
					} else if ("1".equals(result.getCode())) {
						handler.sendEmptyMessage(MODIFIONLINESUCCESS);
					} else if ("-5".equals(result.getCode())) {
						handler.sendEmptyMessage(MODIFIMOREPHONEFAIL);
					} else {
						handler.sendEmptyMessage(MODIFIMOREPHONEFAIL);
					}
				}
			}
		});
		if (!thread2.isAlive()) {
			thread2.start();
		}
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

	public void UpdateDB() {
		try {
			DB db = new DB(this);
			SQLiteDatabase sql = db.getReadableDatabase();
			ContentValues values = new ContentValues();
			values.put("ischeck_mobile", "1");
			values.put("mobile", strPhone);
			sql.update("student_info", values, "uid=?", new String[] { Student_Info.uid });
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

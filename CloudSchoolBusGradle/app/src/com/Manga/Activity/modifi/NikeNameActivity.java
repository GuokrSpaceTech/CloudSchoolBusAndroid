package com.Manga.Activity.modifi;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.Student_Info;
import com.umeng.analytics.MobclickAgent;

public class NikeNameActivity extends BaseActivity {
	private EditText nikeName;
	private TextView count;
	/**
	 * 昵称过长
	 */
	private static final int OUTLENGTH = 0;
	/**
	 * 无网络
	 */
	private static final int HASNTNETWORK = 1;
	/**
	 * 超时
	 */
	private static final int OUTTIME = 2;
	/**
	 * 修改成功
	 */
	private static final int MODIFIOK = 3;
	/**
	 * 修改失败
	 */
	private static final int MODIFIFAILE = 4;
	/**
	 * 修改界面
	 */
	private static final int CHANGEUI = 5;
	private static final int EMPTY = 6;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message mess) {
			// TODO Auto-generated method stub
			switch (mess.what) {
			case OUTLENGTH:
				Toast.makeText(NikeNameActivity.this, R.string.nikename_out_length, Toast.LENGTH_SHORT).show();
				break;
			case HASNTNETWORK:
				Toast.makeText(NikeNameActivity.this, R.string.no_network_cannt_uploding_nikename, Toast.LENGTH_SHORT)
						.show();
				break;
			case OUTTIME:
				Toast.makeText(NikeNameActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				break;
			case MODIFIOK:
				Toast.makeText(NikeNameActivity.this, R.string.modifi_nikename_ok, Toast.LENGTH_SHORT).show();
				break;
			case MODIFIFAILE:
				Toast.makeText(NikeNameActivity.this, R.string.modifi_nikename_fail, Toast.LENGTH_SHORT).show();
				break;
			case CHANGEUI:
				if (ActivityUtil.mychildren != null)
					ActivityUtil.mychildren.init();

				if (ActivityUtil.notice != null) {
					ActivityUtil.notice.UpdateHead();
				}
				if (ActivityUtil.activityRegister != null) {
					ActivityUtil.activityRegister.UpdateHead();
				}
				if (ActivityUtil.share != null) {
					ActivityUtil.share.UpdateHead();
				}
				ActivityUtil.baseinfo.checkStudentInfo();
				break;
			case EMPTY:
				Toast.makeText(NikeNameActivity.this, R.string.feekback_content_null, Toast.LENGTH_SHORT).show();
				break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modifi_nikename);
		nikeName = (EditText) findViewById(R.id.editText1);
		count = (TextView) findViewById(R.id.textView2);
		nikeName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence charSequence, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				int foo = Length(charSequence.toString());
				count.setText(foo + "/" + "20");
				if (foo > 20) {
					count.setTextColor(Color.RED);
				} else {
					count.setTextColor(Color.parseColor("#6F6F6F"));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * 返回键
	 * 
	 * @param v
	 */
	public void backMenu(View v) {
		ActivityUtil.close(this);
		ActivityUtil.baseinfo.checkStudentInfo();
	}

	/**
	 * 递交昵称
	 * 
	 * @param v
	 */
	public void submit(View v) {
		if (!checkLength(nikeName.getText().toString())) {
			handler.sendEmptyMessage(OUTLENGTH);
			return;
		} else {
			if (nikeName.getText().toString().length() == 0) {
				handler.sendEmptyMessage(EMPTY);
				return;
			}
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (HttpUtil.isNetworkConnected(NikeNameActivity.this)) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("nikename", nikeName.getText().toString());
						Result result = HttpUtil.httpPost(NikeNameActivity.this, new Params("student", map));
						if (result == null) {
							handler.sendEmptyMessage(OUTTIME);
						} else if ("1".equals(result.getCode())) {
							handler.sendEmptyMessage(MODIFIOK);
							DB db = new DB(NikeNameActivity.this);
							SQLiteDatabase sql = db.getWritableDatabase();
							ContentValues values = new ContentValues();
							values.put("nikename", nikeName.getText().toString());
							sql.update("student_info", values, "uid=?", new String[] { Student_Info.uid });
							sql.close();

							db.close();
							ActivityUtil.close(NikeNameActivity.this);
							handler.sendEmptyMessage(CHANGEUI);
						} else if ("41".equals(result.getCode())) {
							handler.sendEmptyMessage(EMPTY);
						} else {
							handler.sendEmptyMessage(MODIFIFAILE);
						}
					} else {
						handler.sendEmptyMessage(HASNTNETWORK);
					}
				}
			});
			thread.start();
		}
	}

	public void clear(View v) {
		nikeName.setText("");
	}

	private boolean checkLength(String tmp) {
		int count = 0;
		for (int i = 0; i < tmp.length(); i++) {
			char c = tmp.charAt(i);
			if (c >= 0 && c <= 9) {
				count++;
			} else if (c >= 'a' && c <= 'z') {
				count++;
			} else if (c >= 'A' && c <= 'Z') {
				count++;
			} else if (Character.isLetter(c)) {
				count += 2;
			} else {
				count++;
			}
		}
		if (count > 20) {
			return false;
		}
		return true;
	}

	private int Length(String tmp) {
		int count = 0;
		for (int i = 0; i < tmp.length(); i++) {
			char c = tmp.charAt(i);
			if (c >= 0 && c <= 9) {
				count++;
			} else if (c >= 'a' && c <= 'z') {
				count++;
			} else if (c >= 'A' && c <= 'Z') {
				count++;
			} else if (Character.isLetter(c)) {
				count += 2;
			} else {
				count++;
			}
		}
		return count;
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

package com.Manga.Activity.myChildren.morningCheck;

import java.util.HashMap;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.Student_Info;
import com.umeng.analytics.MobclickAgent;

public class HealthActivity extends BaseActivity {
	private EditText nikeName;
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
			switch (mess.what) {
			case OUTLENGTH:
				Toast.makeText(HealthActivity.this, R.string.nikename_out_length, Toast.LENGTH_SHORT).show();
				break;
			case HASNTNETWORK:
				Toast.makeText(HealthActivity.this, R.string.no_network_cannt_uploding_nikename, Toast.LENGTH_SHORT)
						.show();
				break;
			case OUTTIME:
				Toast.makeText(HealthActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				break;
			case MODIFIOK:
				ActivityUtil.baseinfo.setting_student_allergy_content.setText(Student_Info.healthState);
				Toast.makeText(HealthActivity.this, R.string.modifi_health_ok, Toast.LENGTH_SHORT).show();
				break;
			case MODIFIFAILE:
				Toast.makeText(HealthActivity.this, R.string.modifi_health_fail, Toast.LENGTH_SHORT).show();
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
				Toast.makeText(HealthActivity.this, R.string.feekback_content_null, Toast.LENGTH_SHORT).show();
				break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_health);
		nikeName = (EditText) findViewById(R.id.editText1);
		if (!"".equals(Student_Info.healthState)) {
			nikeName.setText(Student_Info.healthState);
		}
		ActivityUtil.healthActivity = this;
	}

	/**
	 * 返回键
	 * 
	 * @param v
	 */
	public void backMenu(View v) {
		ActivityUtil.close(this);
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
					if (HttpUtil.isNetworkConnected(HealthActivity.this)) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("studentid", Student_Info.uid);
						map.put("health", nikeName.getText().toString());
						Result result = HttpUtil.httpPost(HealthActivity.this, new Params("student", map));
						if (result == null) {
							handler.sendEmptyMessage(OUTTIME);
						} else if ("1".equals(result.getCode())) {
							Student_Info.healthState = nikeName.getText().toString();
							handler.sendEmptyMessage(MODIFIOK);
							ActivityUtil.close(ActivityUtil.healthActivity);
						} else if ("-41".equals(result.getCode())) {
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

package com.Manga.Activity.myChildren.FamilyMembers;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class AddFamilyMemberActivity extends BaseActivity {
	public ImageView image_add;
	EditText nikeName;
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
	 * -4 最多设置四个接送人
	 */
	private static final int EMPTY = 6;
	/**
	 * 没有选择照片
	 */
	private static final int NOIMAGE = 7;
	// -6图片创建失败
	private static final int IMAGEFAIL = 8;
	// 添加成功
	private static final int ADDSUCCESS = 9;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message mess) {
			switch (mess.what) {
			case ADDSUCCESS:
				Toast.makeText(AddFamilyMemberActivity.this, R.string.add_shuttle_success, Toast.LENGTH_SHORT).show();

				ActivityUtil.shuttleActivity.initPepole();
				ActivityUtil.close(ActivityUtil.addShuttleActivity);
				break;
			case OUTLENGTH:
				Toast.makeText(AddFamilyMemberActivity.this, R.string.nikename_out_length, Toast.LENGTH_SHORT).show();
				break;
			case EMPTY:
				Toast.makeText(AddFamilyMemberActivity.this, R.string.feekback_content_null, Toast.LENGTH_SHORT).show();
				break;
			case NOIMAGE:
				Toast.makeText(AddFamilyMemberActivity.this, R.string.image_first, Toast.LENGTH_SHORT).show();
				break;
			case OUTTIME:
				Toast.makeText(AddFamilyMemberActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				break;

			case IMAGEFAIL:
				Toast.makeText(AddFamilyMemberActivity.this, R.string.upload_fail, Toast.LENGTH_SHORT).show();
				break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_shuttle);
		image_add = (ImageView) findViewById(R.id.image_add);
		nikeName = (EditText) findViewById(R.id.editText1);
		ActivityUtil.addShuttleActivity = this;
	}

	/**
	 * 返回键
	 * 
	 * @param v
	 */
	public void backMenu(View v) {
		ActivityUtil.close(this);
	}

	public void clear(View v) {
		nikeName.setText("");
	}

	public void selectImage(View v) {
		Intent intent = new Intent(this, SetPortaitActivity.class);
		startActivity(intent);
	}

	/**
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
			if (ActivityUtil.addHeadActivity != null) {
				if ("".equals(ActivityUtil.addHeadActivity.headImage)) {
					handler.sendEmptyMessage(NOIMAGE);
					return;
				}
			} else {
				handler.sendEmptyMessage(NOIMAGE);
				return;
			}

			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					if (HttpUtil.isNetworkConnected(AddFamilyMemberActivity.this)) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("fbody", ActivityUtil.addHeadActivity.headImage);
						map.put("relationship", nikeName.getText().toString());
						Params params = new Params("childreceiver", map);
						if (HttpUtil.isNetworkConnected(AddFamilyMemberActivity.this)) {
							Result result = HttpUtil.httpPost(AddFamilyMemberActivity.this, params);
							if (result == null) {
								handler.sendEmptyMessage(OUTTIME);
							} else if ("1".equals(result.getCode())) {
								handler.sendEmptyMessage(ADDSUCCESS);
							} else {
								handler.sendEmptyMessage(IMAGEFAIL);
							}
						} else {
							handler.sendEmptyMessage(HASNTNETWORK);
						}
					} else {
						handler.sendEmptyMessage(HASNTNETWORK);
					}
				}
			});
			thread.start();
		}
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

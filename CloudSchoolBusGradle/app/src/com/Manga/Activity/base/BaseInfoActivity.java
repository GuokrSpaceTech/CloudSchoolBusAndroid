package com.Manga.Activity.base;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
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
import com.Manga.Activity.utils.Student_Info;
import com.umeng.analytics.MobclickAgent;

public class BaseInfoActivity extends BaseActivity {
	private TextView name;
	private TextView className;
	private TextView schoolName;
	/**
	 * 连接超时
	 */
	private static final int OUTTIME = 3;
	/**
	 * 初始化学校信息
	 */
	private static final int INITSCHOOL = 4;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message mes) {
			// TODO Auto-generated method stub
			switch (mes.what) {
			case OUTTIME:
				Toast.makeText(BaseInfoActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				break;
			case INITSCHOOL:
				initSchool();
				break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_info);
		name = (TextView) findViewById(R.id.name_content);
		className = (TextView) findViewById(R.id.class_content);
		schoolName = (TextView) findViewById(R.id.school_content);
		inite();
		getSchool();
	}

	private void getSchool() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				if (HttpUtil.isNetworkConnected(BaseInfoActivity.this)) {
					Params params = new Params("classinfo", null);
					Result result = HttpUtil.httpGet(BaseInfoActivity.this, params);
					if (result == null) {
						handler.sendEmptyMessage(OUTTIME);
					} else if ("1".equals(result.getCode())) {
						DB db = new DB(BaseInfoActivity.this);
						SQLiteDatabase sql = db.getWritableDatabase();
						try {
							JSONObject object = new JSONObject(result.getContent());
							JSONObject objectContent = new JSONObject(object.getString("classinfo"));
							ContentValues values = new ContentValues();
							values.put("u_id", Student_Info.uid);
							values.put("schoolname", objectContent.getString("schoolname"));
							Cursor cursor = sql.query("school", null, "u_id=?", new String[] { Student_Info.uid },
									null, null, null);
							if (cursor == null || cursor.getCount() == 0) {
								sql.insert("school", "schoolname", values);
							} else {
								sql.update("school", values, "u_id=?", new String[] { Student_Info.uid });
							}
							cursor.close();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						sql.close();
						db.close();
						handler.sendEmptyMessage(INITSCHOOL);
					} else {
						handler.sendEmptyMessage(INITSCHOOL);
					}
				} else {
					handler.sendEmptyMessage(INITSCHOOL);
				}
			}
		});
		thread.start();
	}

	private void inite() {
		// TODO Auto-generated method stub
		DB db = new DB(this);
		SQLiteDatabase sql = db.getReadableDatabase();
		Cursor cur = sql.query("student_info", null, "uid=?", new String[] { Student_Info.uid }, null, null, null);
		if (cur != null) {
			cur.moveToFirst();
			name.setText(cur.getString(cur.getColumnIndex("nikename")));
			className.setText(cur.getString(cur.getColumnIndex("classname")));
			cur.close();
		}
		sql.close();
		db.close();
	}

	private void initSchool() {
		DB db = new DB(BaseInfoActivity.this);
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor cursor = sql.query("school", null, "u_id=?", new String[] { Student_Info.uid }, null, null, null);
		if (cursor == null || cursor.getCount() == 0) {
			schoolName.setText(getResources().getString(R.string.no_student_school_data));
		} else {
			cursor.moveToFirst();
			String foo = cursor.getString(cursor.getColumnIndex("schoolname"));
			schoolName.setText(foo);
		}
		cursor.close();
		sql.close();
		db.close();
	}

	public void backMenu(View v) {
		ActivityUtil.close(this);
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

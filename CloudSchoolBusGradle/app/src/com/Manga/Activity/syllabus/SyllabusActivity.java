package com.Manga.Activity.syllabus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.adapter.SyllabusArrayAdapter;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.Student_Info;
import com.umeng.analytics.MobclickAgent;

public class SyllabusActivity extends Activity {
	private SyllabusArrayAdapter adapter;
	/**
	 * 进度条
	 */
	private static final int SHOWPROGRESS = 0;
	/**
	 * 网络没有连通
	 */
	private static final int NETISNOTWORKING = 1;
	/**
	 * 初始化界面
	 */
	private static final int INSTANTIAL = 2;
	/**
	 * 连接超时
	 */
	private static final int OUTTIME = 3;
	/**
	 * 获取数据失败
	 */
	private static final int GETSTUDENTDATAFAIL = 4;
	/**
	 * 取消进度条显示
	 */
	private static final int DISMISSPROGRESS = 5;
	private ProgressDialog progressDialog;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private ListView syllabus;
	private TextView calendar;
	private TextView haventData;
	private String strFinalDay;
	private Button buttonReturn;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message mes) {
			// TODO Auto-generated method stub
			switch (mes.what) {
			case NETISNOTWORKING:
				Toast.makeText(SyllabusActivity.this, R.string.net_is_not_working, Toast.LENGTH_SHORT).show();
				break;
			case INSTANTIAL:
				try {
					Date date = null;
					date = sdf.parse(calendar.getText().toString());
					long time = date.getTime();
					date = new Date(time);
					String foo = sdf.format(date);
					if (foo.equals(strFinalDay)) {
						buttonReturn.setVisibility(View.GONE);
					} else {
						buttonReturn.setVisibility(View.VISIBLE);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				instanaial();
				break;
			case SHOWPROGRESS:
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(SyllabusActivity.this);
					progressDialog.setMessage(getResources().getString(R.string.init_view));
				}
				progressDialog.show();
				break;
			case OUTTIME:
				Toast.makeText(SyllabusActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				break;
			case GETSTUDENTDATAFAIL:
				Toast.makeText(SyllabusActivity.this, R.string.get_syllabus_fail, Toast.LENGTH_SHORT).show();
				break;
			case DISMISSPROGRESS:
				progressDialog.dismiss();
				break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.syllabus);
		syllabus = (ListView) findViewById(R.id.syllabus);
		calendar = (TextView) findViewById(R.id.calendar);
		haventData = (TextView) findViewById(R.id.haventData);
		buttonReturn = (Button) findViewById(R.id.back_today);
		String tmp = sdf.format(new Date());
		strFinalDay = sdf.format(new Date());
		calendar.setText(tmp);
		buttonReturn.setVisibility(View.GONE);
		syllabus.setDivider(null);
		getSyllabus(tmp);
	}

	public void backMenu(View v) {
		ActivityUtil.main.move();
	}

	private void getSyllabus(final String time) {
		handler.sendEmptyMessage(SHOWPROGRESS);
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (HttpUtil.isNetworkConnected(SyllabusActivity.this)) {
					HashMap<String, String> map = new HashMap<String, String>();
					String[] sss= time.split("-");
					map.put("day", sss[0].substring(2)+sss[1]+sss[2]);
					Params params = new Params("schedule", map);
					Result result = HttpUtil.httpGet(SyllabusActivity.this, params);
					if (result == null) {
						handler.sendEmptyMessage(OUTTIME);
					} else if ("1".equals(result.getCode())) {
						DB db = new DB(SyllabusActivity.this);
						SQLiteDatabase sql = db.getWritableDatabase();
						try {
							JSONArray array = new JSONArray(result.getContent());
							for (int i = 0; i < array.length(); i++) {
								JSONObject object = array.getJSONObject(i);
								ContentValues values = new ContentValues();
								values.put("u_id", Student_Info.uid);
								values.put("day", calendar.getText().toString());
								values.put("scheduletime", object.getString("scheduletime"));
								values.put("cnname", object.getString("cnname"));
								Cursor cursor = sql.query("syllabus", null, "u_id=? and day=? and scheduletime=?", new String[] { Student_Info.uid, calendar.getText().toString(), object.getString("scheduletime") }, null, null, null);
								if (cursor == null || cursor.getCount() == 0) {
									sql.insert("syllabus", "cnname", values);
								} else {
									sql.update("syllabus", values, "u_id=? and day=? and scheduletime=?", new String[] { Student_Info.uid, calendar.getText().toString(), object.getString("scheduletime") });
								}
								cursor.close();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						sql.close();
						db.close();
						handler.sendEmptyMessage(INSTANTIAL);
					} else {
						handler.sendEmptyMessage(GETSTUDENTDATAFAIL);
					}
				} else {
					handler.sendEmptyMessage(NETISNOTWORKING);
					handler.sendEmptyMessage(INSTANTIAL);
				}
			}
		});
		thread.start();
	}

	private void instanaial() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		DB db = new DB(this);
		SQLiteDatabase sql = db.getReadableDatabase();
		Cursor cursor = sql.query("syllabus", null, "u_id=? and day=?", new String[] { Student_Info.uid, calendar.getText().toString() }, null, null, null);
		if (cursor == null || cursor.getCount() == 0) {
			haventData.setVisibility(View.VISIBLE);
			if(adapter!=null&&adapter.getList()!=null){
				adapter.getList().clear();
			}
		} else {
			haventData.setVisibility(View.GONE);
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("scheduletime", cursor.getString(cursor.getColumnIndex("scheduletime")));
				map.put("cnname", cursor.getString(cursor.getColumnIndex("cnname")));
				list.add(map);
			}
			adapter=new SyllabusArrayAdapter(this, list);
			syllabus.setAdapter(adapter);
		}
		if(cursor!=null){
			cursor.close();
		}
		sql.close();
		db.close();
		handler.sendEmptyMessage(DISMISSPROGRESS);
	}

	public void calendar(View v) {

		if (HttpUtil.isNetworkConnected(SyllabusActivity.this)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			View view = View.inflate(this, R.layout.dialog_select_date, null);
			Button set = (Button) view.findViewById(R.id.set);
			Button cancel = (Button) view.findViewById(R.id.cancel);
			final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
			final AlertDialog dialog = builder.create();
			dialog.setView(view, 0, 0, 0, 0);
			dialog.show();
			set.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					calendar.setText(sdf.format(new Date(datePicker.getYear() - 1900, datePicker.getMonth(), datePicker.getDayOfMonth())));
					getSyllabus(calendar.getText().toString());
					dialog.dismiss();
				}
			});
			cancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					// TODO Auto-generated method stub

				}
			});
		} else {
			handler.sendEmptyMessage(NETISNOTWORKING);
		}
	}

	/**
	 * 昨天
	 * 
	 * @param v
	 */
	public void calendarLeft(View v) {
		try {
			Date date = null;
			date = sdf.parse(calendar.getText().toString());
			long time = date.getTime();
			time = time - 24 * 60 * 60 * 1000;
			date = new Date(time);
			String foo = sdf.format(date);
			calendar.setText(foo);
			getSyllabus(foo);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 明天
	 * 
	 * @param v
	 */
	public void calendarRight(View v) {
		try {
			Date date = null;
			date = sdf.parse(calendar.getText().toString());
			long time = date.getTime();
			time = time + 24 * 60 * 60 * 1000;
			date = new Date(time);
			String foo = sdf.format(date);
			calendar.setText(foo);
			getSyllabus(foo);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 回今天
	 * 
	 * @param v
	 */
	public void backToDay(View v) {
		String tmp = sdf.format(new Date());
		buttonReturn.setVisibility(View.GONE);
		calendar.setText(tmp);
		getSyllabus(tmp);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (ActivityUtil.main != null) {
				ActivityUtil.main.move();
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
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

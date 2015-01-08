package com.Manga.Activity.cookbook;

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

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.adapter.CookbookAdapter;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.Student_Info;
import com.umeng.analytics.MobclickAgent;

public class FoodMenuActivity extends BaseActivity {
	private ListView listView;
	private TextView calendar;
	private TextView haventData;
	private CookbookAdapter adapter;
	private static final int TIME = 0;
	private String strFinalDay;
	private Button buttonReturn;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * 连接超时
	 */
	private static final int OUTTIME = 0;
	/**
	 * 获取数据失败
	 */
	private static final int GETSTUDENTDATAFAIL = 1;
	/**
	 * 网络没有连通
	 */
	private static final int NETISNOTWORKING = 2;
	/**
	 * 初始化界面
	 */
	private static final int INSTANTIAL = 3;
	/**
	 * 进度条
	 */
	private static final int SHOWPROGRESS = 4;
	/**
	 * 取消进度条显示
	 */
	private static final int DISMISSPROGRESS = 5;
	private ProgressDialog progressDialog;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case OUTTIME:
				Toast.makeText(FoodMenuActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				break;
			case GETSTUDENTDATAFAIL:
				Toast.makeText(FoodMenuActivity.this, R.string.get_cookbook_fail, Toast.LENGTH_SHORT).show();
				break;
			case NETISNOTWORKING:
				Toast.makeText(FoodMenuActivity.this, R.string.net_is_not_working, Toast.LENGTH_SHORT).show();
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
					progressDialog = new ProgressDialog(FoodMenuActivity.this);
					progressDialog.setMessage(getResources().getString(R.string.init_view));
				}
				progressDialog.show();
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
		setContentView(R.layout.cookbook);
		listView = (ListView) findViewById(R.id.cookbook_list);
		calendar = (TextView) findViewById(R.id.calendar);
		haventData = (TextView) findViewById(R.id.haventData);
		buttonReturn = (Button) findViewById(R.id.back_today);
		strFinalDay = sdf.format(new Date());
		String tmp = sdf.format(new Date());
		buttonReturn.setVisibility(View.GONE);
		calendar.setText(tmp);
		getCookBook(tmp);
	}

	public void backMenu(View v) {
		ActivityUtil.main.move();
	}

	/**
	 * 获取数据
	 * 
	 * @param time
	 */
	private void getCookBook(final String time) {
		handler.sendEmptyMessage(SHOWPROGRESS);
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (HttpUtil.isNetworkConnected(FoodMenuActivity.this)) {
					Params params = new Params("menu/menu_time/" + time, null);
					Result result = HttpUtil.httpGet(FoodMenuActivity.this, params);
					if (result == null) {
						handler.sendEmptyMessage(OUTTIME);
					} else if ("1".equals(result.getCode())) {
						DB db = new DB(FoodMenuActivity.this);
						SQLiteDatabase sql = db.getWritableDatabase();
						try {
							JSONArray array = new JSONArray(result.getContent());
							for (int i = 0; i < array.length(); i++) {
								JSONObject object = array.getJSONObject(i);
								ContentValues values = new ContentValues();
								values.put("u_id", Student_Info.uid);
								values.put("menu_day", object.getString("menu_day"));
								values.put("menu_name", object.getString("menu_name"));
								values.put("menu_type_name", object.getString("menu_type_name"));
								Cursor cursor = sql.query("cookbook", null, "u_id=? and menu_day=? and menu_type_name=?", new String[] { Student_Info.uid, object.getString("menu_day"), object.getString("menu_type_name") }, null, null, null);
								if (cursor == null || cursor.getCount() == 0) {
									sql.insert("cookbook", "menu_name", values);
								} else {
									sql.update("cookbook", values, "u_id=? and menu_day=? and menu_type_name=?", new String[] { Student_Info.uid, object.getString("menu_day"), object.getString("menu_type_name") });
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

	/**
	 * 显示界面
	 */
	private void instanaial() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		DB db = new DB(this);
		SQLiteDatabase sql = db.getReadableDatabase();
		Cursor cursor = sql.query("cookbook", null, "u_id=? and menu_day=?", new String[] { Student_Info.uid, calendar.getText().toString() }, null, null, null);
		if (cursor == null || cursor.getCount() == 0) {
			haventData.setVisibility(View.VISIBLE);
			if(adapter!=null&&adapter.getList()!=null){
				adapter.getList().clear();
				adapter.notifyDataSetChanged();
			}
		} else {
			haventData.setVisibility(View.GONE);
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("menu_type_name", cursor.getString(cursor.getColumnIndex("menu_type_name")));
				map.put("content", cursor.getString(cursor.getColumnIndex("menu_name")));
				list.add(map);
			}
			adapter=new CookbookAdapter(this, list);
			listView.setAdapter(adapter);
		}
		cursor.close();
		sql.close();
		db.close();
		handler.sendEmptyMessage(DISMISSPROGRESS);
	}

	public void calendar(View v) {
		if (HttpUtil.isNetworkConnected(FoodMenuActivity.this)) {
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
					// TODO Auto-generated method stub
					// Date date = new Date(datePicker.getYear(),
					// datePicker.getMonth(), datePicker.getDayOfMonth());
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					// String foo = sdf.format(date);
					calendar.setText(sdf.format(new Date(datePicker.getYear() - 1900, datePicker.getMonth(), datePicker.getDayOfMonth())));
					getCookBook(calendar.getText().toString());
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
	 * 回今天
	 * 
	 * @param v
	 */
	public void backToDay(View v) {
		String tmp = sdf.format(new Date());
		buttonReturn.setVisibility(View.GONE);
		calendar.setText(tmp);
		getCookBook(tmp);
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
			getCookBook(foo);
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
			getCookBook(foo);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

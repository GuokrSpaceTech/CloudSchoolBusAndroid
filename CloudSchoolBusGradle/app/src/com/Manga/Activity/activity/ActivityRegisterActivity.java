package com.Manga.Activity.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.Msg.SelectHeadActivity;
import com.Manga.Activity.adapter.RegisterAdapter;
import com.Manga.Activity.calender.Util;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.ImageUtil;
import com.Manga.Activity.utils.Student_Info;
import com.Manga.Activity.widget.ModifiListView;
import com.Manga.Activity.widget.ModifiListView.MyBackCall;
import com.Manga.Activity.widget.StudentHeaderView;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("SimpleDateFormat")
public class ActivityRegisterActivity extends BaseActivity {
	private ModifiListView myListview;
	private RelativeLayout noData;
	private RegisterAdapter adapter;
	private StudentHeaderView studentHeader;
	private TextView studentName;
	private TextView studentClassName;
	private Button myRegister;
	private Button allRegister;
	private Button canRegister;
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private int currentView;
	private SharedPreferences shp;
	private RelativeLayout layout_heard;
	/**
	 * 我的报名
	 */
	private static final int MYREGISTER = 0;
	/**
	 * 全部报名
	 */
	private static final int ALLREGUSTER = 1;
	/**
	 * 可报名
	 */
	private static final int CANREGISTER = 2;
	/**
	 * 连接超时
	 */
	private static final int OUTTIME = 3;
	/**
	 * 初始化数据
	 */
	private static final int INSTANTIAL = 4;
	/**
	 * 已无数据
	 */
	private static final int HAVENODATA = 5;
	private static boolean ISHEADER = true;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case OUTTIME:
				Toast.makeText(ActivityRegisterActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				break;
			case HAVENODATA:
				Toast.makeText(ActivityRegisterActivity.this, R.string.have_no_new_data, Toast.LENGTH_SHORT).show();
				myListview.cancelHeader();
				myListview.cancelFooter();
				ActivityUtil.main.disPRO();
				adapter.notifyDataSetChanged();
				break;
			case INSTANTIAL:
				initList();
				break;
			}
			showEmpty();
			return false;
		}
	});

	public void headerPic(View v) {
		Intent intent = new Intent(this, SelectHeadActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		ActivityUtil.activityRegister = this;
		shp = getSharedPreferences("count", Context.MODE_PRIVATE);
		myListview = (ModifiListView) findViewById(R.id.activity_listview);
		noData = (RelativeLayout) findViewById(R.id.no_data);
		myListview.setDivider(null);
		initHeader();
		currentView = ALLREGUSTER;
		UpdateBackGround();
		getRegisterInfo("0", "0", "0", "0", "all_activity");
		myListview.setBackCall(new MyBackCall() {

			@Override
			public void executeHeader() {
				ISHEADER = true;
				switch (currentView) {
				case MYREGISTER:
					if (adapter.getList() == null || adapter.getList().size() == 0) {
						getRegisterInfo("0", "0", "1", "0", "my_activity");
					} else {
						getRegisterInfo("0", adapter.getList().get(adapter.getList().size() - 1).get("events_id"), "1",
								"0", "my_activity");
					}
					break;
				case ALLREGUSTER:
					if (adapter.getList() == null || adapter.getList().size() == 0) {
						getRegisterInfo("0", "0", "0", "0", "all_activity");
					} else {
						getRegisterInfo("0", adapter.getList().get(adapter.getList().size() - 1).get("events_id"), "0",
								"0", "all_activity");
					}
					break;
				case CANREGISTER:
					if (adapter.getList() == null || adapter.getList().size() == 0) {
						getRegisterInfo("0", "0", "0", "1", "can_activity");
					} else {
						getRegisterInfo("0", adapter.getList().get(adapter.getList().size() - 1).get("events_id"), "0",
								"1", "can_activity");
					}
					break;

				}
			}

			@Override
			public void executeFooter() {
				ISHEADER = false;
				switch (currentView) {
				case MYREGISTER:
					if (adapter.getList() == null || adapter.getList().size() == 0) {
						getRegisterInfo("0", "0", "1", "0", "my_activity");
					} else {
						getRegisterInfo(adapter.getList().get(adapter.getList().size() - 1).get("events_id"), "0", "1",
								"0", "my_activity");
					}
					break;
				case ALLREGUSTER:
					if (adapter.getList() == null || adapter.getList().size() == 0) {
						getRegisterInfo("0", "0", "0", "0", "all_activity");
					} else {
						getRegisterInfo(adapter.getList().get(adapter.getList().size() - 1).get("events_id"), "0", "0",
								"0", "all_activity");
					}
					break;
				case CANREGISTER:
					if (adapter.getList() == null || adapter.getList().size() == 0) {
						getRegisterInfo("0", "0", "0", "1", "can_activity");
					} else {
						getRegisterInfo(adapter.getList().get(adapter.getList().size() - 1).get("events_id"), "0", "0",
								"1", "can_activity");
					}
					break;
				}
			}
		});
	}

	/**
	 * 初始化布局
	 */
	private void initHeader() {
		View firstView = View.inflate(this, R.layout.register_header, null);
		studentHeader = (StudentHeaderView) firstView.findViewById(R.id.share_student_header_bg);
		studentName = (TextView) firstView.findViewById(R.id.share_student_name);
		studentClassName = (TextView) firstView.findViewById(R.id.share_student_school_name);
		myRegister = (Button) firstView.findViewById(R.id.my_register);
		layout_heard = (RelativeLayout) firstView.findViewById(R.id.share_header);
		allRegister = (Button) firstView.findViewById(R.id.all_register);
		canRegister = (Button) firstView.findViewById(R.id.can_register);
		myListview.addHeaderView(firstView);
		myRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				currentView = MYREGISTER;
				adapter.getList().clear();
				adapter.notifyDataSetChanged();
				getRegisterInfo("0", "0", "1", "0", "my_activity");
				myRegister.setBackgroundResource(R.drawable.notice_button_select);
				allRegister.setBackgroundColor(Color.TRANSPARENT);
				canRegister.setBackgroundColor(Color.TRANSPARENT);
			}
		});
		allRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				currentView = ALLREGUSTER;
				adapter.getList().clear();
				adapter.notifyDataSetChanged();
				getRegisterInfo("0", "0", "0", "0", "all_activity");
				allRegister.setBackgroundResource(R.drawable.notice_button_select);
				myRegister.setBackgroundColor(Color.TRANSPARENT);
				canRegister.setBackgroundColor(Color.TRANSPARENT);
			}
		});
		canRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				currentView = CANREGISTER;
				adapter.getList().clear();
				adapter.notifyDataSetChanged();
				getRegisterInfo("0", "0", "0", "1", "can_activity");
				canRegister.setBackgroundResource(R.drawable.notice_button_select);
				myRegister.setBackgroundColor(Color.TRANSPARENT);
				allRegister.setBackgroundColor(Color.TRANSPARENT);
			}
		});
		allRegister.setBackgroundResource(R.drawable.notice_button_select);
		myRegister.setBackgroundColor(Color.TRANSPARENT);
		canRegister.setBackgroundColor(Color.TRANSPARENT);
		instantial();
	}

	/**
	 * 初始化界面数据
	 */
	private void instantial() {
		DB db = new DB(this);
		SQLiteDatabase sql = db.getReadableDatabase();
		Cursor cursor = sql.query("student_info", null, "uid=?", new String[] { Student_Info.uid }, null, null, null);
		if (cursor == null || cursor.getCount() == 0) {

		} else {
			cursor.moveToFirst();
			String str = cursor.getString(cursor.getColumnIndex("birthday")).substring(5,
					cursor.getString(cursor.getColumnIndex("birthday")).length());
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
			String foo = sdf.format(date);
			ImageView imageView = (ImageView) findViewById(R.id.guoshengri);
			if (str.equals(foo)) {
				imageView.setVisibility(View.VISIBLE);
			} else {
				imageView.setVisibility(View.GONE);
			}
			studentName.setText(cursor.getString(cursor.getColumnIndex("nikename")));
			studentClassName.setText(cursor.getString(cursor.getColumnIndex("classname")));

			studentHeader.setStudentHeaderBG(ImageUtil.toRoundCorner(
					ImageUtil.base64ToBitmap(cursor.getString(cursor.getColumnIndex("avatar"))), 75));
			// studentHeader.setStudentHeaderBG(ImageUtil.round(ImageUtil.base64ToBitmap(cursor.getString(cursor.getColumnIndex("avatar"))),
			// 16, Color.WHITE));
		}
		if (cursor != null) {
			cursor.close();
		}
		sql.close();
		db.close();
		adapter = new RegisterAdapter(this, new ArrayList<HashMap<String, String>>());
		myListview.setAdapter(adapter);
		myListview.cancelHeader();
		myListview.cancelFooter();
	}

	public void UpdateHead() {
		DB db = new DB(this);
		SQLiteDatabase sql = db.getReadableDatabase();
		Cursor cursor = sql.query("student_info", null, "uid=?", new String[] { Student_Info.uid }, null, null, null);
		if (cursor == null || cursor.getCount() == 0) {

		} else {
			cursor.moveToFirst();
			String str = cursor.getString(cursor.getColumnIndex("birthday")).substring(5,
					cursor.getString(cursor.getColumnIndex("birthday")).length());
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
			String foo = sdf.format(date);
			ImageView imageView = (ImageView) findViewById(R.id.guoshengri);
			if (str.equals(foo)) {
				imageView.setVisibility(View.VISIBLE);
			} else {
				imageView.setVisibility(View.GONE);
			}
			studentName.setText(cursor.getString(cursor.getColumnIndex("nikename")));

			studentHeader.setStudentHeaderBG(ImageUtil.toRoundCorner(
					ImageUtil.base64ToBitmap(cursor.getString(cursor.getColumnIndex("avatar"))), 75));
			// studentHeader.setStudentHeaderBG(ImageUtil.round(ImageUtil.base64ToBitmap(cursor.getString(cursor.getColumnIndex("avatar"))),16,
			// Color.WHITE));
		}
		if (cursor != null) {
			cursor.close();
		}
		sql.close();
		db.close();
	}

	/**
	 * 
	 * @param startid
	 * @param endid
	 * @param myevents
	 *            1 仅返回我的活动
	 * @param signup
	 *            1 仅返回可以报名的活动
	 * @param tableName
	 */
	private void getRegisterInfo(final String startid, final String endid, final String myevents, final String signup,
			final String tableName) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (HttpUtil.isNetworkConnected(ActivityRegisterActivity.this)) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("startid", startid);
					map.put("endid", endid);
					map.put("myevents", myevents);
					map.put("signup", signup);
					Result result = HttpUtil.httpGet(ActivityRegisterActivity.this, new Params("eventslist", map));
					if (result == null) {
						handler.sendEmptyMessage(OUTTIME);
					} else if ("1".equals(result.getCode())) {
						try {
							JSONArray array = new JSONArray(result.getContent());
							DB db = new DB(ActivityRegisterActivity.this);
							SQLiteDatabase sql = db.getReadableDatabase();

							for (int i = 0; i < array.length(); i++) {
								JSONObject object = array.getJSONObject(i);
								ContentValues values = new ContentValues();
								values.put("u_id", Student_Info.uid);
								values.put("events_id", object.getString("events_id"));
								values.put("title", object.getString("title"));
								values.put("addtime", dateformat.parse(object.getString("addtime")).getTime() + "");
								values.put("SignupStatus", object.getString("SignupStatus"));
								values.put("isSignup", object.getString("isSignup"));
								values.put("htmlurl", object.getString("htmlurl"));
								Cursor cur = sql.query(tableName, null, "u_id=? and events_id=?", new String[] {
										Student_Info.uid, object.getString("events_id") }, null, null, null);
								if (cur == null || cur.getCount() == 0) {
									sql.insert(tableName, "title", values);
								} else {
									sql.update(tableName, values, "u_id=? and events_id=?", new String[] {
											Student_Info.uid, object.getString("events_id") });
								}
								cur.close();
							}
							sql.close();
							db.close();
							handler.sendEmptyMessage(INSTANTIAL);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if ("-2".equals(result.getCode())) {
						handler.sendEmptyMessage(HAVENODATA);
					} else {
						// handler.sendEmptyMessage(GET_SHARE_DATAFAIL);
					}
				} else {
					// .sendEmptyMessage(NETISNOTWORKING);
					// handler.sendEmptyMessage(INSTANTIAL_SHARE);
				}
			}
		});
		thread.start();
	}

	private void initList() {
		if (ISHEADER) {
			if (ActivityUtil.home != null) {
				ActivityUtil.home.CloseStatusEvents();
			}
			switch (currentView) {
			case MYREGISTER:
				if (adapter.getList().isEmpty()) {
					headerChangeData("my_activity", true);
				} else {
					headerChangeData("my_activity", false);
				}
				break;
			case ALLREGUSTER:
				if (adapter.getList().isEmpty()) {
					headerChangeData("all_activity", true);
				} else {
					headerChangeData("all_activity", false);
				}
				break;
			case CANREGISTER:
				if (adapter.getList().isEmpty()) {
					headerChangeData("can_activity", true);
				} else {
					headerChangeData("can_activity", false);
				}
				break;
			}
			myListview.cancelHeader();
		} else {
			switch (currentView) {
			case MYREGISTER:
				if (adapter.getList().isEmpty()) {
					footerChangeData("my_activity", true);
				} else {
					footerChangeData("my_activity", false);
				}
				break;
			case ALLREGUSTER:
				if (adapter.getList().isEmpty()) {
					footerChangeData("all_activity", true);
				} else {
					footerChangeData("all_activity", false);
				}
				break;
			case CANREGISTER:
				if (adapter.getList().isEmpty()) {
					footerChangeData("can_activity", true);
				} else {
					footerChangeData("can_activity", false);
				}
				break;
			}
			myListview.cancelFooter();
		}

	}

	/**
	 * 查询数据库并修改视图
	 * 
	 * @param table
	 *            表名
	 * @param isEmpty
	 *            当前显示是否为空
	 */
	private void headerChangeData(String table, boolean isEmpty) {
		if (isEmpty) {
			DB db = new DB(this);
			SQLiteDatabase sql = db.getReadableDatabase();
			Cursor cur = sql
					.query(table, null, "u_id=?", new String[] { Student_Info.uid }, null, null, "addtime desc");
			if (cur == null || cur.getCount() == 0) {
				handler.sendEmptyMessage(HAVENODATA);
			} else {
				ArrayList<HashMap<String, String>> newList = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> map;
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					map = new HashMap<String, String>();
					map.put("events_id", cur.getString(cur.getColumnIndex("events_id")));
					map.put("title", cur.getString(cur.getColumnIndex("title")));
					map.put("addtime", cur.getString(cur.getColumnIndex("addtime")));
					map.put("SignupStatus", cur.getString(cur.getColumnIndex("SignupStatus")));
					map.put("isSignup", cur.getString(cur.getColumnIndex("isSignup")));
					map.put("htmlurl", cur.getString(cur.getColumnIndex("htmlurl")));
					newList.add(map);
					if (cur.getPosition() == 14) {
						break;
					}
				}
				adapter.getList().addAll(newList);
				cur.close();
			}
			sql.close();
			db.close();
		} else {
			DB db = new DB(this);
			SQLiteDatabase sql = db.getReadableDatabase();
			Cursor cur = sql.query(table, null, "u_id=? and addtime > ?", new String[] { Student_Info.uid,
					adapter.getList().get(0).get("addtime") }, null, null, "addtime desc");
			Log.v("addtime_小", adapter.getList().get(adapter.getList().size() - 1).get("addtime"));
			if (cur == null || cur.getCount() == 0) {
				handler.sendEmptyMessage(HAVENODATA);
			} else {
				ArrayList<HashMap<String, String>> newList = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> map;
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					map = new HashMap<String, String>();
					map.put("events_id", cur.getString(cur.getColumnIndex("events_id")));
					map.put("title", cur.getString(cur.getColumnIndex("title")));
					map.put("addtime", cur.getString(cur.getColumnIndex("addtime")));
					Log.v("addtime", cur.getString(cur.getColumnIndex("addtime")));
					Log.v("addtime", "--------------------------");
					map.put("SignupStatus", cur.getString(cur.getColumnIndex("SignupStatus")));
					map.put("isSignup", cur.getString(cur.getColumnIndex("isSignup")));
					map.put("htmlurl", cur.getString(cur.getColumnIndex("htmlurl")));
					newList.add(map);
					if (cur.getPosition() == 14) {
						break;
					}
				}
				newList.addAll(adapter.getList());
				adapter.getList().clear();
				adapter.getList().addAll(newList);
			}
		}
		adapter.notifyDataSetChanged();
		myListview.cancelHeader();
	}

	/**
	 * 查询数据库并修改视图
	 * 
	 * @param table
	 *            表名
	 * @param isEmpty
	 *            当前显示是否为空
	 */
	private void footerChangeData(String table, boolean isEmpty) {
		if (isEmpty) {
			DB db = new DB(this);
			SQLiteDatabase sql = db.getReadableDatabase();
			Cursor cur = sql
					.query(table, null, "u_id=?", new String[] { Student_Info.uid }, null, null, "addtime desc");
			if (cur == null || cur.getCount() == 0) {
				handler.sendEmptyMessage(HAVENODATA);
			} else {
				ArrayList<HashMap<String, String>> newList = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> map;
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					map = new HashMap<String, String>();
					map.put("events_id", cur.getString(cur.getColumnIndex("events_id")));
					map.put("title", cur.getString(cur.getColumnIndex("title")));
					map.put("addtime", cur.getString(cur.getColumnIndex("addtime")));
					map.put("SignupStatus", cur.getString(cur.getColumnIndex("SignupStatus")));
					map.put("isSignup", cur.getString(cur.getColumnIndex("isSignup")));
					map.put("htmlurl", cur.getString(cur.getColumnIndex("htmlurl")));
					newList.add(map);
					if (cur.getPosition() == 14) {
						break;
					}
				}
				adapter.getList().addAll(newList);
				cur.close();
			}
			sql.close();
			db.close();
		} else {
			DB db = new DB(this);
			SQLiteDatabase sql = db.getReadableDatabase();
			Cursor cur = sql.query(table, null, "u_id=? and addtime < ?", new String[] { Student_Info.uid,
					adapter.getList().get(adapter.getList().size() - 1).get("addtime") }, null, null, "addtime desc");
			if (cur == null || cur.getCount() == 0) {
				handler.sendEmptyMessage(HAVENODATA);
			} else {
				ArrayList<HashMap<String, String>> newList = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> map;
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					map = new HashMap<String, String>();
					map.put("events_id", cur.getString(cur.getColumnIndex("events_id")));
					map.put("title", cur.getString(cur.getColumnIndex("title")));
					map.put("addtime", cur.getString(cur.getColumnIndex("addtime")));
					map.put("SignupStatus", cur.getString(cur.getColumnIndex("SignupStatus")));
					map.put("isSignup", cur.getString(cur.getColumnIndex("isSignup")));
					map.put("htmlurl", cur.getString(cur.getColumnIndex("htmlurl")));
					newList.add(map);
					if (cur.getPosition() == 14) {
						break;
					}
				}
				adapter.getList().addAll(newList);
			}
		}
		adapter.notifyDataSetChanged();
		myListview.cancelFooter();
	}

	public void UpdateBackGround() {
		String str = shp.getString("cbackground", "");
		if (!"".equals(str)) {
			Drawable d = null;
			try {
				Bitmap bitmap = Util.getBitmap(str);
				d = new BitmapDrawable(bitmap);
				layout_heard.setBackgroundDrawable(d);

			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private void showEmpty() {
		if (adapter != null && adapter.getList() != null) {
			if (adapter.getList().isEmpty()) {
				noData.setVisibility(View.VISIBLE);
			} else {
				noData.setVisibility(View.GONE);
			}
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

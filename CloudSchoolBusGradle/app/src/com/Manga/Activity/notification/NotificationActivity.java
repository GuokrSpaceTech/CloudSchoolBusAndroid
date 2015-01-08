package com.Manga.Activity.notification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import android.view.KeyEvent;
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
import com.Manga.Activity.adapter.NoticeAdapter;
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
public class NotificationActivity extends BaseActivity {
	private ModifiListView myListview;
	private RelativeLayout noData;
	private StudentHeaderView studentHeader;
	private TextView studentName;
	private TextView studentClassName;
	private Button btnAllSelect;
	private Button btnImportentSelect;
	private boolean blnisAll;
	private String strApiName;
	private NoticeAdapter adapter;
	private SharedPreferences shp;
	private RelativeLayout layout_heard;
	private RelativeLayout top;
	private boolean isheader = false;
	private boolean isfooter = false;

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
	 * 实例化数据
	 */
	private static final int INSTANTIAL_STUDENT = 3;
	/**
	 * 无数据
	 */
	private static final int NODATA = 4;
	/**
	 * 初始化分享
	 */
	private static final int INSTANTIAL_SHARE = 7;
	/**
	 * 获取日志数据失败
	 */
	private static final int GET_SHARE_DATAFAIL = 8;
	/**
	 * 隐藏头
	 */
	private static final int CANELHEAD = 9;
	/**
	 * 隐藏脚
	 */
	private static final int CANELFOOT = 10;
	/**
	 * 没有更多
	 */
	private static final int NOTMORE = 11;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case OUTTIME:
				myListview.cancelHeader();
				myListview.cancelFooter();
				Toast.makeText(NotificationActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				break;
			case NOTMORE:
				myListview.cancelHeader();
				myListview.cancelFooter();
				Toast.makeText(NotificationActivity.this, R.string.not_more_data, Toast.LENGTH_SHORT).show();
				break;
			case GETSTUDENTDATAFAIL:
				myListview.cancelHeader();
				myListview.cancelFooter();
				Toast.makeText(NotificationActivity.this, R.string.get_student_data_fail, Toast.LENGTH_SHORT).show();
				break;
			case NETISNOTWORKING:
				myListview.cancelHeader();
				myListview.cancelFooter();
				Toast.makeText(NotificationActivity.this, R.string.net_is_not_working, Toast.LENGTH_SHORT).show();
				break;
			case INSTANTIAL_STUDENT:
				instantial();
				// 获取班级分享信息
				getShareInfo("0", "0");
				break;
			case NODATA:
				myListview.cancelHeader();
				myListview.cancelFooter();
				Toast.makeText(NotificationActivity.this, R.string.no_data, Toast.LENGTH_SHORT).show();
				break;
			case INSTANTIAL_SHARE:
				instantialShare();
				break;
			case CANELHEAD:
				myListview.cancelHeader();
				break;
			case CANELFOOT:
				myListview.cancelFooter();
				break;
			case GET_SHARE_DATAFAIL:
				myListview.cancelHeader();
				myListview.cancelFooter();
				Toast.makeText(NotificationActivity.this, R.string.get_notice_data_fail, Toast.LENGTH_SHORT).show();
				break;
			}
			if (adapter != null && adapter.getList() != null) {
				if (adapter.getList().size() <= 2) {
					top.setVisibility(View.GONE);
				} else {
					top.setVisibility(View.VISIBLE);
				}
			}
			return false;
		}
	});

	public void headerPic(View v) {
		Intent intent = new Intent(this, SelectHeadActivity.class);
		startActivity(intent);
	}

	public void top(View view) {
		myListview.setSelection(0);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);
		myListview = (ModifiListView) findViewById(R.id.share_listview);
		noData = (RelativeLayout) findViewById(R.id.no_data);
		top = (RelativeLayout) findViewById(R.id.top);
		shp = getSharedPreferences("count", Context.MODE_PRIVATE);
		initHeader();
		myListview.setDivider(null);
		// 初始化学生的信息
		UpdateBackGround();
		instantial();
		ActivityUtil.notice = this;
		isheader = false;
		isfooter = false;
		getShareInfo("0", "0");
		btnAllSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				blnisAll = true;
				btnAllSelect.setBackgroundResource(R.drawable.notice_button_select);
				btnImportentSelect.setBackgroundColor(Color.parseColor("#00FFFFFF"));
				adapter.getList().clear();
				adapter.notifyDataSetChanged();
				isheader = false;
				isfooter = false;
				getShareInfo("0", "0");
				strApiName = "notice";
			}
		});
		btnImportentSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				blnisAll = false;
				btnImportentSelect.setBackgroundResource(R.drawable.notice_button_select);
				btnAllSelect.setBackgroundColor(Color.parseColor("#00FFFFFF"));
				adapter.getList().clear();
				adapter.notifyDataSetChanged();
				isheader = false;
				isfooter = false;
				getShareInfo("0", "0");
				strApiName = "tnotice";
			}
		});
		myListview.setBackCall(new MyBackCall() {

			@Override
			public void executeHeader() {
				if (adapter.getList().isEmpty()) {
					isheader = false;
					isfooter = false;
					getShareInfo("0", "0");
				} else {
					isheader = true;
					isfooter = false;
					getShareInfo("0", "0");
				}
			}

			@Override
			public void executeFooter() {
				if (adapter.getList().isEmpty()) {
					isheader = false;
					isfooter = false;
					getShareInfo("0", "0");
				} else {
					isheader = false;
					isfooter = true;
					getShareInfo(adapter.getList().get(adapter.getList().size() - 1).get("addtime"), "0");
				}
			}
		});

	}

	/**
	 * 初始化布局
	 */
	private void initHeader() {
		View firstView = View.inflate(this, R.layout.notice_header, null);
		studentHeader = (StudentHeaderView) firstView.findViewById(R.id.share_student_header_bg);
		studentName = (TextView) firstView.findViewById(R.id.share_student_name);
		studentClassName = (TextView) firstView.findViewById(R.id.share_student_school_name);
		myListview.addHeaderView(firstView);
		layout_heard = (RelativeLayout) firstView.findViewById(R.id.share_header);
		btnAllSelect = (Button) firstView.findViewById(R.id.all_notice);
		btnImportentSelect = (Button) firstView.findViewById(R.id.important_notice);
		btnAllSelect.setBackgroundResource(R.drawable.notice_button_select);
		btnImportentSelect.setBackgroundColor(Color.parseColor("#00FFFFFF"));
		blnisAll = true;
		strApiName = "notice";
	}

	/**
	 * 获取消息通知信息
	 * 
	 * @param starttime
	 * @param endtime
	 */
	private void getShareInfo(final String starttime, final String endtime) {
		ActivityUtil.main.showPRO();
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (HttpUtil.isNetworkConnected(NotificationActivity.this)) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("starttime", starttime);
					map.put("endtime", endtime);
					String strNotice = "notice";
					if (!blnisAll) {
						strNotice = "notice/confirm/1";
					}
					Result result = HttpUtil.httpGet(NotificationActivity.this, new Params(strNotice, map));
					Log.i("strApiName", strApiName);
					if (result == null) {
						handler.sendEmptyMessage(OUTTIME);
					} else if ("1".equals(result.getCode())) {
						try {
							JSONArray array = new JSONArray(result.getContent());
							DB db = new DB(NotificationActivity.this);
							SQLiteDatabase sql = db.getReadableDatabase();
							Cursor cursor = sql.query(strApiName, null, null, null, null, null, null, null);
							// 表里有数据 清空
							if (cursor != null) {
								sql.delete(strApiName, null, null);
								cursor.close();
							}
							for (int i = 0; i < array.length(); i++) {
								JSONObject object = array.getJSONObject(i);
								ContentValues values = new ContentValues();
								values.put("u_id", Student_Info.uid);
								values.put("noticeid", object.getString("noticeid"));
								values.put("addtime", object.getString("addtime"));
								values.put("noticetitle", object.getString("noticetitle"));
								values.put("noticecontent", object.getString("noticecontent"));
								values.put("isconfirm", object.getString("isconfirm"));
								values.put("noticekey", object.getString("noticekey"));
								values.put("haveisconfirm", object.getString("haveisconfirm"));
								JSONArray arrayPList = new JSONArray(object.getString("plist"));
								StringBuffer plist = new StringBuffer();
								for (int j = 0; j < arrayPList.length(); j++) {
									JSONObject pObject = arrayPList.getJSONObject(j);
									plist.append(pObject.getString("source"));
									plist.append(",");
								}
								if (plist.length() > 0) {
									plist.deleteCharAt(plist.length() - 1);
								}
								values.put("plist", plist.toString());
								Cursor cur = sql.query(strApiName, null, "u_id=? and noticeid=?", new String[] {
										Student_Info.uid, object.getString("noticeid") }, null, null, null);
								if (cur == null || cur.getCount() == 0) {
									sql.insert(strApiName, "content", values);
								} else {
									sql.update(strApiName, values, "u_id=? and noticeid=?", new String[] {
											Student_Info.uid, object.getString("noticeid") });
								}
								cur.close();
							}
							sql.close();
							db.close();
							handler.sendEmptyMessage(INSTANTIAL_SHARE);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							ActivityUtil.main.disPRO();
							handler.sendEmptyMessage(GET_SHARE_DATAFAIL);
						}
					} else if ("-2".equals(result.getCode())) {
						handler.sendEmptyMessage(NOTMORE);
						ActivityUtil.main.disPRO();
					} else {
						ActivityUtil.main.disPRO();
						handler.sendEmptyMessage(GET_SHARE_DATAFAIL);
					}
				} else {
					ActivityUtil.main.disPRO();
					handler.sendEmptyMessage(NETISNOTWORKING);
					handler.sendEmptyMessage(INSTANTIAL_SHARE);
				}
			}
		});
		thread.start();
	}

	/**
	 * 实例化分享
	 */
	private void instantialShare() {
		DB db = new DB(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor cursor = sql.query(strApiName, null, "u_id=?", new String[] { Student_Info.uid }, null, null, null);
		if (isheader == false && isfooter == false) {
			cursor = sql.query(strApiName, null, "u_id=?", new String[] { Student_Info.uid }, null, null,
					"addtime desc");
		} else if (isheader == true && isfooter == false) {
			cursor = sql.query(strApiName, null, "u_id=?", new String[] { Student_Info.uid }, null, null,
					"addtime desc");
		} else if (isheader == false && isfooter == true) {
			cursor = sql.query(strApiName, null, "u_id=? and addtime < ?", new String[] { Student_Info.uid,
					adapter.getList().get(adapter.getCount() - 1).get("addtime") }, null, null, "addtime desc");
		}
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if (cursor != null) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("noticeid", cursor.getString(cursor.getColumnIndex("noticeid")));
				Log.i("noticeid", cursor.getString(cursor.getColumnIndex("noticeid")) + "" + cursor.getCount());
				map.put("addtime", cursor.getString(cursor.getColumnIndex("addtime")));
				map.put("title", cursor.getString(cursor.getColumnIndex("noticetitle")));
				map.put("content", cursor.getString(cursor.getColumnIndex("noticecontent")));
				map.put("isconfirm", cursor.getString(cursor.getColumnIndex("isconfirm")));
				map.put("plist", cursor.getString(cursor.getColumnIndex("plist")));
				map.put("haveisconfirm", cursor.getString(cursor.getColumnIndex("haveisconfirm")));
				map.put("noticekey", cursor.getString(cursor.getColumnIndex("noticekey")));
				list.add(map);
			}
		}
		cursor.close();
		sql.close();
		db.close();
		if (isheader == false && isfooter == false) {
			if (ActivityUtil.home != null) {
				ActivityUtil.home.CloseStatusNotice();
			}
			if (list.size() == 0) {

			} else {
				adapter.getList().addAll(list);
				adapter.notifyDataSetChanged();
			}
			ActivityUtil.main.disPRO();
		} else if (isheader == true && isfooter == false) {
			if (ActivityUtil.home != null) {
				ActivityUtil.home.CloseStatusNotice();
			}
			// list.addAll(adapter.getList());
			adapter.getList().clear();
			adapter.getList().addAll(list);
			adapter.notifyDataSetChanged();
			myListview.cancelHeader();
		} else if (isheader == false && isfooter == true) {
			adapter.getList().addAll(list);
			adapter.notifyDataSetChanged();
			myListview.cancelFooter();
		}
		showEmpty();
		ActivityUtil.main.disPRO();
	}

	/**
	 * 初始化界面
	 */
	@SuppressLint("SimpleDateFormat")
	private void instantial() {
		DB db = new DB(this);
		SQLiteDatabase sql = db.getReadableDatabase();
		Cursor cursor = sql.query("student_info", null, "uid=?", new String[] { Student_Info.uid }, null, null, null);
		if (cursor == null || cursor.getCount() == 0) {
			handler.sendEmptyMessage(NODATA);
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
		adapter = new NoticeAdapter(this, new ArrayList<Map<String, String>>());
		myListview.setAdapter(adapter);
		myListview.cancelHeader();
		myListview.cancelFooter();
	}

	public void UpdateHead() {
		DB db = new DB(this);
		SQLiteDatabase sql = db.getReadableDatabase();
		Cursor cursor = sql.query("student_info", null, "uid=?", new String[] { Student_Info.uid }, null, null, null);
		if (cursor == null || cursor.getCount() == 0) {
			handler.sendEmptyMessage(NODATA);
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

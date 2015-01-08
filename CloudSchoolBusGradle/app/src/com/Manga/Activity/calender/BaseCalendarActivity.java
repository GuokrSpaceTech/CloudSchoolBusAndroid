package com.Manga.Activity.calender;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.MainActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

public class BaseCalendarActivity extends BaseActivity implements OnTouchListener {
	private String language;
	private static final int calLayoutID = 55; // 日历布局ID

	// 动画
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	GestureDetector mGesture = null;

	@Override
	// 获取手势action;
	public boolean onTouch(View v, MotionEvent event) {
		mGesture.onTouchEvent(event);

		return super.onTouchEvent(event);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.mGesture.onTouchEvent(event);
		return false;

	}

	AnimationListener animationListener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// 当动画完成后调用
			CreateGirdView();
		}
	};

	// SimpleOnGestureListener 是Android SDK提供的一个listener类来侦测各种不同的手势;
	class GestureListener extends SimpleOnGestureListener {
		@Override
		// 在onFling方法中, 判断是不是一个合理的swipe动作;
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				if (e1.getX() < e2.getX() && (e2.getX() - e1.getX()) > 120) {// 向左滑动
					ActivityUtil.close(BaseCalendarActivity.this);
					return false;
				}

			} catch (Exception e) {
				// nothing
			}
			return false;
		}

		private SharedPreferences shp = getSharedPreferences("count", Context.MODE_PRIVATE);

		private void changbotomtxt(String typeid) {

			if ("1".equals(typeid)) {
				tv_daysm.setText(getResources().getString(R.string.calendar_normal));
				// tv_daysm.setText("正常出勤");
			} else if ("2".equals(typeid)) {
				// tv_daysm.setText("异常出勤");
				tv_daysm.setText(getResources().getString(R.string.calendar_absence));

			} else if ("3".equals(typeid)) {
				// tv_daysm.setText("异常出勤");
				tv_daysm.setText(getResources().getString(R.string.actualizar));

			} else if ("4".equals(typeid)) {
				// tv_daysm.setText("异常出勤");
				tv_daysm.setText(getResources().getString(R.string.calendar_absence));

			} else {
				tv_daysm.setText(getResources().getString(R.string.calendar_absence_normal));

			}

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// ListView lv = getListView();
			// 得到当前选中的是第几个单元格

			int pos = gView2.pointToPosition((int) e.getX(), (int) e.getY());
			RelativeLayout txtDay = (RelativeLayout) gView2.findViewById(pos + 5000);
			if (txtDay != null) {
				if (txtDay.getTag() != null) {
					Map map = (Map) txtDay.getTag();
					Date date = (Date) map.get("date");
					String typeid = (String) map.get("type");
					tv_daysm1.setText((String) map.get("daytype"));
					String str = (String) tv_daysm1.getText();
					if (str == null || str.equals("")) {
						v_daysm1.setVisibility(View.GONE);
					} else {
						v_daysm1.setVisibility(View.VISIBLE);
					}
					strtmp = typeid;
					changbotomtxt(typeid);
					calSelected.setTime(date);
					Log.i("111", calStartDate.get(Calendar.MONTH) + "and" + iMonthViewCurrentMonth);
					if ((calSelected.get(Calendar.MONTH) == iMonthViewCurrentMonth - 1)
							|| (calSelected.get(Calendar.YEAR) == iMonthViewCurrentYear - 1)) {

						// tv_day.setText("");
						// tv_daysm.setText("");
						setPrevViewItem();
						CreateGirdView();
						UpdateStartDateForMonth();

					} else if ((calSelected.get(Calendar.MONTH) == iMonthViewCurrentMonth + 1)
							|| (calSelected.get(Calendar.YEAR) == iMonthViewCurrentYear + 1)) {

						// tv_daysm.setText("");
						// tv_day.setText("");
						setNextViewItem();
						CreateGirdView();
						UpdateStartDateForMonth();

					}
					// else {
					// 更新底部选中天
					shp.edit().putString("tvday", calSelected.get(Calendar.DAY_OF_MONTH) + "").commit();
					tv_day.setText(calSelected.get(Calendar.DAY_OF_MONTH) + "");

					gAdapter.setSelectedDate(calSelected);
					gAdapter.notifyDataSetChanged();

					gAdapter1.setSelectedDate(calSelected);
					gAdapter1.notifyDataSetChanged();

					gAdapter3.setSelectedDate(calSelected);
					gAdapter3.notifyDataSetChanged();

				}
				Log.i("TEST", "onSingleTapUp -  pos=" + pos);
			}
			return false;
		}
	}

	// 基本变量
	private Context mContext = BaseCalendarActivity.this;
	private GridView gView1;// 上一个月
	private GridView gView2;// 当前月
	private GridView gView3;// 下一个月

	boolean bIsSelection = false;// 是否是选择事件发生
	private Calendar calStartDate = Calendar.getInstance();// 当前显示的日历
	private Calendar calSelected = Calendar.getInstance(); // 选择的日历
	private Calendar calToday = Calendar.getInstance(); // 今日
	private CalendarGridViewAdapter gAdapter;
	private CalendarGridViewAdapter gAdapter1;
	private CalendarGridViewAdapter gAdapter3;
	private boolean islocallogin;

	private int iMonthViewCurrentMonth = 0; // 当前视图月
	private int iMonthViewCurrentYear = 0; // 当前视图年
	private int iFirstDayOfWeek = Calendar.MONDAY;
	private SharedPreferences shp;
	/*
	 * private int nMonthKey; private String strMonthKey;
	 */
	/** 底部菜单文字 **/
	String[] menu_toolbar_name_array;
	private ViewFlipper mViewFlipper;
	private ImageButton imbt_up;
	private ImageButton imbt_next;
	private TextView tv_daily; // 顶部日期显示
	private TextView tv_day; // 下方当日天
	private TextView tv_daysm; // 下方当日考勤说明
	private TextView tv_daysm1; // 下方当日考勤说明
	private RelativeLayout v_daysm1;
	private TextView tv_daycount; // 底部考勤总数
	private String strtmp;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		shp = getSharedPreferences("count", Context.MODE_PRIVATE);
		islocallogin = shp.getBoolean("islocallogin", false);
		mViewFlipper = (ViewFlipper) findViewById(R.id.vf_clendtxt);
		imbt_up = (ImageButton) findViewById(R.id.imbt_upmoth);
		imbt_next = (ImageButton) findViewById(R.id.imbt_nextmoth);
		tv_daily = (TextView) findViewById(R.id.tv_cq_dairly);
		tv_day = (TextView) findViewById(R.id.tv_cq_day);
		tv_daysm = (TextView) findViewById(R.id.tv_cq_daysm);
		v_daysm1 = (RelativeLayout) findViewById(R.id.v_cq_daysm1);
		tv_daysm1 = (TextView) findViewById(R.id.tv_cq_daysm1);
		tv_daycount = (TextView) findViewById(R.id.tv_cq_countday);
		language = getResources().getConfiguration().locale.getLanguage();
		calStartDate = getCalendarStartDate();
		CreateGirdView();
		setListen();
		mGesture = new GestureDetector(BaseCalendarActivity.this, new GestureListener());

		// TODO Auto-generated method stub
		UpdateStartDateForMonth();
		// 添加Animation实现不同动画效果
		slideLeftOut = AnimationUtils.loadAnimation(BaseCalendarActivity.this, R.anim.slide_left_in);
		slideLeftIn = AnimationUtils.loadAnimation(BaseCalendarActivity.this, R.anim.slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(BaseCalendarActivity.this, R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(BaseCalendarActivity.this, R.anim.slide_right_out);

		slideLeftIn.setAnimationListener(animationListener);
		slideLeftOut.setAnimationListener(animationListener);
		slideRightIn.setAnimationListener(animationListener);
		slideRightOut.setAnimationListener(animationListener);

		tv_day.setText(calSelected.get(Calendar.DAY_OF_MONTH) + "");
		changbotomtxt(strtmp);
		ActivityUtil.main.disPRO();

	}

	public void close(View view) {
		ActivityUtil.close(this);
	}

	private void changbotomtxt(String typeid) {
		if ("1".equals(typeid)) {
			tv_daysm.setText(getResources().getString(R.string.calendar_normal));
			// tv_daysm.setText("正常出勤");
		} else if ("2".equals(typeid)) {
			// tv_daysm.setText("异常出勤");
			tv_daysm.setText(getResources().getString(R.string.calendar_absence));

		} else if ("3".equals(typeid)) {
			// tv_daysm.setText("异常出勤");
			tv_daysm.setText(getResources().getString(R.string.actualizar));

		} else {
			tv_daysm.setText(getResources().getString(R.string.calendar_absence_normal));

		}
	}

	AlertDialog.OnKeyListener onKeyListener = new AlertDialog.OnKeyListener() {

		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				BaseCalendarActivity.this.finish();
			}
			return false;

		}

	};

	private void setListen() {
		imbt_up.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setPrevViewItem();
				CreateGirdView();
				UpdateStartDateForMonth();
			}
		});
		imbt_next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setNextViewItem();
				CreateGirdView();
				UpdateStartDateForMonth();
			}
		});

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = new ProgressDialog(BaseCalendarActivity.this);
		dialog.setTitle("");
		dialog.setMessage(language.equals("zh") ? "载入中..." : "Loading...");
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		return dialog;
	};

	private void CreateGirdView() {

		Calendar tempSelected1 = Calendar.getInstance(); // 临时
		Calendar tempSelected2 = Calendar.getInstance(); // 临时
		Calendar tempSelected3 = Calendar.getInstance(); // 临时
		tempSelected1.setTime(calStartDate.getTime());
		tempSelected2.setTime(calStartDate.getTime());
		tempSelected3.setTime(calStartDate.getTime());

		gView1 = new CalendarGridView(mContext);
		tempSelected1.add(Calendar.MONTH, -1);
		gAdapter1 = new CalendarGridViewAdapter(this, tempSelected1);
		gView1.setAdapter(gAdapter1);// 设置菜单Adapter
		gView1.setId(calLayoutID);

		gView2 = new CalendarGridView(mContext);
		gAdapter = new CalendarGridViewAdapter(this, tempSelected2);
		gView2.setAdapter(gAdapter);// 设置菜单Adapter
		gView2.setId(calLayoutID);

		gView3 = new CalendarGridView(mContext);
		tempSelected3.add(Calendar.MONTH, 1);
		gAdapter3 = new CalendarGridViewAdapter(this, tempSelected3);
		gView3.setAdapter(gAdapter3);// 设置菜单Adapter
		gView3.setId(calLayoutID);

		gView2.setOnTouchListener(this);
		gView1.setOnTouchListener(this);
		gView3.setOnTouchListener(this);

		if (mViewFlipper.getChildCount() != 0) {
			mViewFlipper.removeAllViews();
		}

		mViewFlipper.addView(gView2);
		mViewFlipper.addView(gView3);
		mViewFlipper.addView(gView1);

		String s = calStartDate.get(Calendar.YEAR) + "-"
				+ NumberHelper.LeftPad_Tow_Zero(calStartDate.get(Calendar.MONTH) + 1);

		tv_daily.setText(s);

		HashMap map = new HashMap();
		map.put("month", Util.CleandtoYYMM(calStartDate));
		Log.i("Util.CleandtoYYMM(calStartDate)", Util.CleandtoYYMM(calStartDate));
		getData(map);
	}

	private void setPrevViewItem() {
		iMonthViewCurrentMonth--;// 当前选择月--
		// 如果当前月为负数的话显示上一年
		if (iMonthViewCurrentMonth == -1) {
			iMonthViewCurrentMonth = 11;
			iMonthViewCurrentYear--;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1); // 设置日为当月1日
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth); // 设置月
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear); // 设置年

	}

	// 当月
	private void setToDayViewItem() {

		calSelected.setTimeInMillis(calToday.getTimeInMillis());
		calSelected.setFirstDayOfWeek(iFirstDayOfWeek);
		calStartDate.setTimeInMillis(calToday.getTimeInMillis());
		calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);

	}

	// 下一个月
	private void setNextViewItem() {
		iMonthViewCurrentMonth++;
		if (iMonthViewCurrentMonth == 12) {
			iMonthViewCurrentMonth = 0;
			iMonthViewCurrentYear++;
		}

		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);

	}

	// 根据改变的日期更新日历
	// 填充日历控件用
	private void UpdateStartDateForMonth() {
		// 设置成当月第一天
		calStartDate.set(Calendar.DATE, 1);
		// 得到当前日历显示的月
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
		// 得到当前日历显示的年
		iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);

		String s = calStartDate.get(Calendar.YEAR) + "-"
				+ NumberHelper.LeftPad_Tow_Zero(calStartDate.get(Calendar.MONTH) + 1);
		tv_daily.setText(s);

		// 星期一是2 星期天是1 填充剩余天数
		int iDay = 0;
		int iFirstDayOfWeek = Calendar.MONDAY;
		int iStartDay = iFirstDayOfWeek;
		if (iStartDay == Calendar.MONDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			if (iDay < 0)
				iDay = 6;
		}
		if (iStartDay == Calendar.SUNDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);

	}

	private Calendar getCalendarStartDate() {
		calToday.setTimeInMillis(System.currentTimeMillis());
		calToday.setFirstDayOfWeek(iFirstDayOfWeek);

		if (calSelected.getTimeInMillis() == 0) {
			calStartDate.setTimeInMillis(System.currentTimeMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		} else {
			calStartDate.setTimeInMillis(calSelected.getTimeInMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		}

		return calStartDate;
	}

	// fillpage 为 6 7 9 第一屏 第二屏 第三屏
	// 获取当月考勤
	private void getData(final HashMap<String, String> map) {
		mmhandler.sendEmptyMessage(2);
		new Thread() {
			public void run() {
				Result result = HttpUtil.httpGet(BaseCalendarActivity.this, new Params("attendance", map));
				if (result == null) {
					mhandler.sendEmptyMessage(1);
				} else {
					Message message = new Message();
					message.obj = result;
					message.what = 2;
					mhandler.sendMessage(message);

				}
			};
		}.start();
	}

	protected void ShowSaveorShareDialog(Context context) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle(language.equals("zh") ? "下线通知" : "Message");

		builder.setMessage(language.equals("zh") ? "您的账号在另一台设备上登陆，您被迫下线了，若允许多设备同时在线，请在设置中开启"
				: "Your account is off-line now，because it sign in another equipment now，"
						+ " if you allow your account online outnumber one at the same time，do it in setting");

		builder.setPositiveButton(language.equals("zh") ? "重新登陆" : "Relog", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (islocallogin) {
					Map<String, String> localmap = new HashMap<String, String>();
					localmap.put("code", "1");

					localmap.put("content", "1");
					Message mess = new Message();
					mess.obj = localmap;
					mess.what = 2;
					mmhandler.sendMessage(mess);
					return;
				}

			}
		});
		builder.setNegativeButton(language.equals("zh") ? "取消" : "Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.create().show();
	}

	private Handler mmhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(
						BaseCalendarActivity.this,
						language.equals("zh") ? "请求服务器失败！帐号注销失败！"
								: "Requests the server failed! Account cancellation of failure!", Toast.LENGTH_SHORT)
						.show();
				break;
			case 2:
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(BaseCalendarActivity.this);
					progressDialog.setMessage(getResources().getString(R.string.init_view));
				}
				progressDialog.show();
				break;
			case 3:
				progressDialog.dismiss();
				break;
			default:
				break;
			}
		};
	};
	private Handler mhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(
						BaseCalendarActivity.this,
						language.equals("zh") ? "请求服务器失败,无法获取当月考勤！"
								: "Requests the server failure, unable to obtain the attendance!", Toast.LENGTH_SHORT)
						.show();
				break;
			case 2:
				Result map = (Result) msg.obj;
				if (map == null)
					break;
				String code = (String) map.getCode();
				String content = (String) map.getContent();

				if ("1".equals(code)) {
					try {
						gAdapter.ClearDay();
						JSONObject json = new JSONObject(content);
						JSONArray jsonarr = json.getJSONArray("attendance");
						ArrayList<Map> list = new ArrayList<Map>();
						SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
						Log.v("测试日期", sdf.format(new Date()));
						String tmpTime = sdf.format(new Date());
						int ii = 0;
						for (int i = 0; i < jsonarr.length(); i++) {
							Map<String, String> itemmap = new HashMap<String, String>();
							itemmap.put("time", jsonarr.getJSONObject(i).getString("attendaceday"));
							Log.v("jsonarr.getJSONObject(i)", jsonarr.getJSONObject(i).getString("attendaceday"));
							itemmap.put("typeid", jsonarr.getJSONObject(i).getString("attendancetypeid"));
							if (!"1".equals(jsonarr.getJSONObject(i).getString("attendancetypeid"))) {
								ii++;
							}
							if (tmpTime.equals(jsonarr.getJSONObject(i).getString("attendaceday"))) {
								SharedPreferences shp = getSharedPreferences("count", Context.MODE_PRIVATE);
								String tmpAttend = jsonarr.getJSONObject(i).getString("attendancetypeid");
								String tmpFo = tmpTime.substring(4, 6);
								if (tmpFo.substring(0, 1).equals("0")) {
									tmpFo = tmpFo.substring(1, 2);
								}
								shp.edit().putString("tvday", tmpFo).commit();
								tv_day.setText(tmpFo);
								if ("1".equals(tmpAttend)) {
									tv_daysm.setText(getResources().getString(R.string.calendar_normal));
								} else if ("2".equals(tmpAttend)) {
									tv_daysm.setText(getResources().getString(R.string.calendar_absence));
								} else if ("3".equals(tmpAttend)) {
									tv_daysm.setText(getResources().getString(R.string.actualizar));
								} else {
									tv_daysm.setText(getResources().getString(R.string.calendar_absence_normal));
								}
							}
							list.add(itemmap);
						}
						tv_daycount.setText(list.size() - ii + "天");
						if (list.size() > 0) {
							gAdapter.setAttendance(list);
							gAdapter.notifyDataSetChanged();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						JSONObject json1 = new JSONObject(content);
						JSONArray strTeacherDevDay = json1.getJSONArray("festival");
						ArrayList<Map> list = new ArrayList<Map>();
						for (int ii = 0; ii < strTeacherDevDay.length(); ii++) {
							Map<String, String> itemmap = new HashMap<String, String>();

							String strTeacherDevDayItem = strTeacherDevDay.getString(ii);
							String str[] = strTeacherDevDayItem.split(",");
							if (str.length == 2) {
								itemmap.put("time", str[0]);
								itemmap.put("name", str[1]);
								v_daysm1.setVisibility(View.VISIBLE);
								tv_daysm1.setText(str[1]);
								list.add(itemmap);
							}
						}
						if (list.size() > 0) {
							gAdapter.setTeacherDevDay(list);
							gAdapter.notifyDataSetChanged();
						}
					} catch (Exception e) {
					}
					try {
						JSONObject json1 = new JSONObject(content);
						JSONObject jsonarr1 = json1.getJSONObject("calendar");
						JSONArray strTeacherDevDay = jsonarr1.getJSONArray("workday");
						ArrayList<Map> list = new ArrayList<Map>();
						for (int ii = 0; ii < strTeacherDevDay.length(); ii++) {
							Map<String, String> itemmap = new HashMap<String, String>();

							String strTeacherDevDayItem = strTeacherDevDay.getString(ii);
							itemmap.put("time", strTeacherDevDayItem);
							itemmap.put("name", getResources().getString(R.string.calendar_techerday_normal));
							tv_daysm1.setText(getResources().getString(R.string.calendar_techerday_normal));
							v_daysm1.setVisibility(View.GONE);
							list.add(itemmap);
						}
						if (list.size() > 0) {
							gAdapter.setWorkDay(list);
							gAdapter.notifyDataSetChanged();
						}

					} catch (Exception e) {
						// TODO: handle exception
					}
				} else if ("-1113".equals(code)) {
					ShowSaveorShareDialog(BaseCalendarActivity.this);
				} else {
					Toast.makeText(BaseCalendarActivity.this,
							language.equals("zh") ? "获取当月考勤失败！" : "Get month attendance failure!", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			case 3:

				break;

			default:
				break;
			}
			mmhandler.sendEmptyMessage(3);
		};
	};

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}

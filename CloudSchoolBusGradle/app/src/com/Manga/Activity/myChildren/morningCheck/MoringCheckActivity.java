package com.Manga.Activity.myChildren.morningCheck;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.adapter.AttendanceRecordsAdapter;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.myChildren.DoctorConsult.MyAsyncTask;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.widget.KCalendar;
import com.Manga.Activity.widget.KCalendar.OnCalendarClickListener;
import com.Manga.Activity.widget.KCalendar.OnCalendarDateChangedListener;
import com.cytx.utility.FastJsonTools;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("ResourceAsColor")
public class MoringCheckActivity extends BaseActivity {
	private KCalendar calendar;
	
	private TextView tv_calendar_title;
	private TextView header_day, header_title_date, header_title_festival;
	private TextView footer_title;
	private ImageView imbt_nextmoth, imbt_upmoth;
	private ListView list_view_attendance_records;
	
	String date = null;// 设置默认选中的日期 格式为 “2014-04-05” 标准DATE格式
	int year, month;
	
	//Map<String, AttendanceManagerBean> allAttendanceMap;
	Map<String, AttendanceManagerDto> attendanceMap;
	
	private static final int MSG_GOT_ATTENDANCE_NOTIFICATION = 2;
	
	AttendanceRecordsAdapter attendancelistAdapter; 
	
	ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	ImageLoaderConfiguration config;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moring_check);
		ActivityUtil.moringCheckActivity = this;
		
		//Get the current date
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date d = new Date();
		date = format.format(d);
		if (null != date) {
			year = Integer.parseInt(date.substring(0, date.indexOf("-")));
			month = Integer.parseInt(date.substring(date.indexOf("-") + 1, date.lastIndexOf("-")));
		}
		
		//Setup the UI elements
		setUI();
        
		//Init the image Loader instance

		//Init the data structure holds all data from server
		attendanceMap    = new HashMap<String, AttendanceManagerDto>();

		//Get the data from the server
		if (month >= 10) {
			init_data(year + "-" + month);
		} else {
			init_data(year + "-0" + month);
		}
	}

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_GOT_ATTENDANCE_NOTIFICATION: //Fill in the data after receiving data from the server
				
                //Get the current day data
				if (attendanceMap.containsKey(date.substring(0, date.lastIndexOf("-")))) {
                    AttendanceManagerDto  attMgr = attendanceMap.get(date.substring(0, date.lastIndexOf("-")));
                    
					//Festival
                    if(attMgr.getFestival().containsKey(date))
                    {
                    	String festival = attMgr.getFestival().get(date);
                    	header_title_festival.setText(festival);
                    }
                    else
                    	header_title_festival.setText("");
                    
                    
					if (attMgr.getFestival().containsKey(date)) {
						String festival = attMgr.getFestival().get(date);
						header_title_festival.setText(festival);
					}
					else
						header_title_festival.setText("");

					// 本月有节日的日历显示紫色
					List<String> festivalList = new ArrayList<String>();
					Set keyFestivalSet = attMgr.getFestival().keySet();// 返回键的集合
					Iterator itFestival = keyFestivalSet.iterator();
					while (itFestival.hasNext()) {
						Object key = itFestival.next();
						festivalList.add((String) key);
					}
					
					for (int m = 0; m < festivalList.size(); m++) {
						if (!festivalList.get(m).equals(date)) {
							calendar.setCalendarDayBgColor(festivalList.get(m), R.drawable.calendar_buttom_buttom);
						}
					}
					
					//Set the marks for the days with attendance records
					List<String> list = new ArrayList<String>();
					for(int i=0; i < attMgr.getAttendance().size(); i++)
					{
						AttendanceDto att = attMgr.getAttendance().get(i);
						list.add(att.getAttendaceday1());
					}
					
					calendar.addMarks(list, 0);
								
				}
				
				//Set the List View and Notify the data changes
				attendancelistAdapter.attendance_records = getCurrentDateAttendanceRecords();
				attendancelistAdapter.notifyDataSetChanged();
								
				//Set the header
				String str_day = date.substring(date.lastIndexOf("-") + 1, date.length());
				header_day.setText(str_day);
				header_day.setTextSize(20);
				header_day.setBackgroundColor(R.drawable.user_report_detailis_progress_load);
				header_day.setTextColor(Color.WHITE);
				
				int len;
				if(attendancelistAdapter.attendance_records == null)
					len = 0;
				else
					len = attendancelistAdapter.attendance_records.size();
				
				String str_title= getResources().getString(R.string.current_day_attendance_records)
						       + "(" 
						       + len 
						       + ")";
				
				header_title_date.setText(str_title);
				
				//Set the footer
				int days = attendanceMap.get(date.substring(0, date.lastIndexOf("-")))
						                .getAttendance()
						                .size();
				
				str_title = days 
						+ "  |  " 
						+ getResources().getString(R.string.total_attendance_records)
						+" : " 
						+ getTotalNumRecords(getYearMonthString(month, year));
				
				footer_title.setText(str_title);
				
				break;
			}
			
			return false;
		}
	});
	


	private void init_data(final String month) {

		MyAsyncTask postSubmitReportTask = new MyAsyncTask(MoringCheckActivity.this, false) {
			Result result;

			@Override
			protected void onPostExecute(Void vod) {

				if (result == null) {
					Toast.makeText(MoringCheckActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				} else if ("1".equals(result.getCode())) {
					//FileTools.save2SDCard(FileTools.getSDcardPath() + "/attendance", "attendance_records", ".json", result.getContent());
					try {
						//Get the School Calendar Note: FastJsonTools cannot parse the festival map
						JSONObject allMessage = new JSONObject(result.getContent());
						JSONArray festivalArray = new JSONArray(allMessage.getString("festival"));
						HashMap<String, String> festivalList = new HashMap<String, String>();
						for (int festival = 0; festival < festivalArray.length(); festival++) {
							String[] festivalContent = festivalArray.getString(festival).split(",");
							festivalList.put(festivalContent[0], festivalContent[1]);
						}
						
						//Get all the attendance information
						AttendanceManagerDto attmgr = FastJsonTools.getObject(result.getContent(), AttendanceManagerDto.class);
						
						int total_num_records_month = 0;
						for(int i=0; i<attmgr.getAttendance().size(); i++)
						{
							AttendanceDto att = attmgr.getAttendance().get(i);
							total_num_records_month += att.getRecord().size();
						}

						attmgr.setFestivalList(festivalList);
						attmgr.setTotal_num_attendance_records(total_num_records_month);
						
						attendanceMap.put(month, attmgr);
						
						handler.sendEmptyMessage(MSG_GOT_ATTENDANCE_NOTIFICATION);
					
					} catch (JSONException e) {
						Log.i("MorningCheckActivity", result.getContent());
					}
				}
				super.onPostExecute(vod); 
			}

			@Override
			protected Void doInBackground(Void... params) {
				if (HttpUtil.isNetworkConnected(MoringCheckActivity.this)) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("month", month);
					result = HttpUtil.httpGet(MoringCheckActivity.this, new Params("attendancemanager", map));
				}
				
				return super.doInBackground(params);
			}
			
		};
		
		if (!attendanceMap.containsKey((String) month)) {
			postSubmitReportTask.execute();
		} else {
			handler.sendEmptyMessage(MSG_GOT_ATTENDANCE_NOTIFICATION);
		}
	}

	private void setUI() {
		calendar = (KCalendar) findViewById(R.id.popupwindow_calendar);
		tv_calendar_title = (TextView) findViewById(R.id.tv_cq_dairly);

		imbt_upmoth = (ImageView) findViewById(R.id.imbt_upmoth);
		imbt_nextmoth = (ImageView) findViewById(R.id.imbt_nextmoth);
		
		header_day        = (TextView)findViewById(R.id.header_day);
		header_title_date = (TextView)findViewById(R.id.tv_current_date);
		header_title_festival = (TextView)findViewById(R.id.tv_festival_title);
		footer_title          = (TextView)findViewById(R.id.tv_cq_countday);
		
		list_view_attendance_records = (ListView)findViewById(R.id.listView_attendance_records);

		tv_calendar_title.setText(year + "." + month);
		tv_calendar_title.setTextSize(24);
		calendar.showCalendar(year, month);
		calendar.setCalendarDayBgColor(date, R.drawable.user_report_detailis_progress_load);
		
		// 监听所选中的日期
		calendar.setOnCalendarClickListener(new OnCalendarClickListener() {

			public void onCalendarClick(int row, int col, String dateFormat) {
				int month = Integer.parseInt(dateFormat.substring(dateFormat.indexOf("-") + 1,
						dateFormat.lastIndexOf("-")));

				if (calendar.getCalendarMonth() - month == 1// 跨年跳转
						|| calendar.getCalendarMonth() - month == -11) {
					calendar.lastMonth();

				} else if (month - calendar.getCalendarMonth() == 1 // 跨年跳转
						|| month - calendar.getCalendarMonth() == -11) {
					calendar.nextMonth();
				}

				calendar.removeCalendarDayBgColor(date);
				calendar.setCalendarDayBgColor(dateFormat, R.drawable.user_report_detailis_progress_load);
				//calendar.setCalendarDayBgColor(dateFormat, Color.parseColor("#5FAFC7"));
				if (!dateFormat.equals(date)) {
					if (!attendanceMap.containsKey(dateFormat.substring(0, dateFormat.lastIndexOf("-")))) {
						date = dateFormat;// 最后返回给全局 date
						init_data(dateFormat.substring(0, dateFormat.lastIndexOf("-")));
					} else {
						date = dateFormat;// 最后返回给全局 date
						handler.sendEmptyMessage(MSG_GOT_ATTENDANCE_NOTIFICATION);
					}
				}
			}
		});

		// 监听当前月份
		calendar.setOnCalendarDateChangedListener(new OnCalendarDateChangedListener() {
			public void onCalendarDateChanged(int year, int month) {
				tv_calendar_title.setTextSize(24);
				tv_calendar_title.setText(year + "." + month);
				calendar.removeCalendarDayBgColor(date);

				String monthTemp = "";
				if (month >= 10) {
					monthTemp = year + "-" + month;
				} else {
					monthTemp = year + "-0" + month;
				}
				date = monthTemp + "-01";
				calendar.setCalendarDayBgColor(date, R.drawable.user_report_detailis_progress_load);
				init_data(monthTemp);

			}
		});
		
		imbt_upmoth.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				calendar.lastMonth();
				String monthTemp = "";
				if (calendar.getCalendarMonth() >= 10) {
					monthTemp = calendar.getCalendarYear() + "-" + calendar.getCalendarMonth();
				} else {
					monthTemp = calendar.getCalendarYear() + "-0" + calendar.getCalendarMonth();
				}
				date = monthTemp + "-01";
				calendar.setCalendarDayBgColor(date, R.drawable.user_report_detailis_progress_load);
				init_data(monthTemp);
			}
		});
		
		imbt_nextmoth.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				calendar.nextMonth();
				String monthTemp = "";
				if (calendar.getCalendarMonth() >= 10) {
					monthTemp = calendar.getCalendarYear() + "-" + calendar.getCalendarMonth();
				} else {
					monthTemp = calendar.getCalendarYear() + "-0" + calendar.getCalendarMonth();
				}
				date = monthTemp + "-01";
				calendar.removeCalendarDayBgColor(date);
				calendar.setCalendarDayBgColor(date, R.drawable.user_report_detailis_progress_load);
				init_data(monthTemp);
			}
		});
        
		Context context = MoringCheckActivity.this;
		attendancelistAdapter = new AttendanceRecordsAdapter(context);
		list_view_attendance_records.setAdapter(attendancelistAdapter);
		
		list_view_attendance_records.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(MoringCheckActivity.this, CheckPictureActivity.class);
				List<AttendanceRecordDto> records = getCurrentDateAttendanceRecords();
				String url = records.get(arg2).getImgpath();
				if(url != "null")
				{
				    intent.putExtra("image", "http://"+url);
				    ActivityUtil.startActivity(ActivityUtil.share, intent);
				}
			}
		});
	}

	public void close(View view) {
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
	
	private List<AttendanceRecordDto> getCurrentDateAttendanceRecords()
	{
		List<AttendanceRecordDto> records = null;
		String currentMon = date.substring(0, date.lastIndexOf("-" ));
		AttendanceManagerDto attMgr = attendanceMap.get(currentMon);
		if(attMgr!=null)
		{
		    List<AttendanceDto> attList = attMgr.getAttendance();
		    
		    for(int i=0; i<attList.size(); i++)
		    {
		    	String str_date = attList.get(i).getAttendaceday1();
		    	if(str_date.equals(date))
		    	{
		    		records = attList.get(i).getRecord();
		    		break;
		    	}
		    }
		}
			
		return records;
	}
	
	public String getMonth(int month) {
		String monthString = "";
		switch (month) {
		case 1:
			monthString = getResources().getString(R.string.january);
			break;
		case 2:
			monthString = getResources().getString(R.string.february);
			break;
		case 3:
			monthString = getResources().getString(R.string.march);
			break;
		case 4:
			monthString = getResources().getString(R.string.april);
			break;
		case 5:
			monthString = getResources().getString(R.string.may);
			break;
		case 6:
			monthString = getResources().getString(R.string.june);
			break;
		case 7:
			monthString = getResources().getString(R.string.july);
			break;
		case 8:
			monthString = getResources().getString(R.string.august);
			break;
		case 9:
			monthString = getResources().getString(R.string.september);
			break;
		case 10:
			monthString = getResources().getString(R.string.october);
			break;
		case 11:
			monthString = getResources().getString(R.string.november);
			break;
		case 12:
			monthString = getResources().getString(R.string.december);
			break;
		}
		return monthString;
	}
	
	private String getYearMonthString(int int_mon, int int_year)
	{
		String str;
		if( int_mon > 9)
		    str = int_year + "-" + int_mon;
		else
			str = int_year + "-0" + int_mon;
		
		return str;
	}
	
	private int getTotalNumRecords(String theMonth)
	{
		int num = 0;
		
		if( attendanceMap.get(theMonth) != null )
			num = attendanceMap.get(theMonth).getTotal_num_attendance_records();
		
		return num;
	}
}

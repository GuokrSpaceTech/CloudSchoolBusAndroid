package com.Manga.Activity.calender;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Manga.Activity.R;

/************************************************************************
 * 项目名字 :带手势滑动功能的日历
 * 
 * @author angelの慧
 * @version 2012-10-08 　*
 ************************************************************************/
public class CalendarGridViewAdapter extends BaseAdapter {

	private Calendar calStartDate = Calendar.getInstance();// 当前显示的日历
	private Calendar calSelected = Calendar.getInstance(); // 选择的日历
	private List<Calendar> noramlatten = new ArrayList<Calendar>(); // 正常考勤
	private List<Calendar> unnoramlatten = new ArrayList<Calendar>(); // 异常考勤
	private List<Calendar> actualizarlatten = new ArrayList<Calendar>(); // 补登考勤
	private List<Calendar> teacherDevDay = new ArrayList<Calendar>(); // 教师职业发展日
	private List<Calendar> workDay = new ArrayList<Calendar>();
	private Map<String, String> attendantype = new HashMap<String, String>();
	private Map<String, String> teacherDevDayType = new HashMap<String, String>();
	public void setSelectedDate(Calendar cal) {
		calSelected = cal;
	}

	public void setAttendance(List<Map> list) {
		noramlatten.clear();
		unnoramlatten.clear();
		attendantype.clear();
		
		for (int j = 0; j < list.size(); j++) {
			Map map = list.get(j);
			String time = (String) map.get("time");
			String typeid = (String) map.get("typeid");
			String year = 20 + time.substring(0, 2);
			String moth = time.substring(2, 4);
			String day = time.substring(4, 6);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.parseInt(year));
			cal.set(Calendar.MONTH, Integer.parseInt(moth) - 1);
			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
			Log.i("MyTag", "--遍历考勤日期-----"+Util.CleandtoyyyyMMdd(cal));
			if ("1".equals(typeid)) {
				noramlatten.add(cal);
			} else if("2".equals(typeid)){
				unnoramlatten.add(cal);
			} else if("3".equals(typeid)){
				actualizarlatten.add(cal);
			}
			attendantype.put(Util.CleandtoyyyyMMdd(cal), typeid);
		}
	}
	public void setTeacherDevDay(List<Map> list) {
		
		for (int j = 0; j < list.size(); j++) {
			Map map = list.get(j);
			String strTeacherDevDayItem = (String) map.get("time");
			String strName = (String) map.get("name");
			String[] strArray = strTeacherDevDayItem.split("-");
			if(strArray.length ==3){
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, Integer.parseInt(strArray[0]));
				cal.set(Calendar.MONTH, Integer.parseInt(strArray[1]) - 1);
				cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(strArray[2]));
	
				teacherDevDay.add(cal);
				teacherDevDayType.put(Util.CleandtoyyyyMMdd(cal), strName);
			}
			
		}
	}
	public void setWorkDay(List<Map> list) {
		
		for (int j = 0; j < list.size(); j++) {
			Map map = list.get(j);
			String strTeacherDevDayItem = (String) map.get("time");
			String strName = (String) map.get("name");
			String[] strArray = strTeacherDevDayItem.split("-");
			if(strArray.length ==3){
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, Integer.parseInt(strArray[0]));
				cal.set(Calendar.MONTH, Integer.parseInt(strArray[1]) - 1);
				cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(strArray[2]));
	
				workDay.add(cal);
				teacherDevDayType.put(Util.CleandtoyyyyMMdd(cal), strName);
			}
			
		}
	}
	public void ClearDay(){
		teacherDevDay.clear();
		teacherDevDayType.clear();
	}

	private void setNormalAttendance(List<Calendar> date) {
		noramlatten = date;
	}

	private void setUnNormalAttendance(List<Calendar> date) {
		unnoramlatten = date;
	}

	private Calendar calToday = Calendar.getInstance(); // 今日
	private int iMonthViewCurrentMonth = 0; // 当前视图月

	// 根据改变的日期更新日历
	// 填充日历控件用
	private void UpdateStartDateForMonth() {
		calStartDate.set(Calendar.DATE, 1); // 设置成当月第一天
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);// 得到当前日历显示的月

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

		calStartDate.add(Calendar.DAY_OF_MONTH, -1);// 周日第一位

	}

	ArrayList<java.util.Date> titles;

	private ArrayList<java.util.Date> getDates() {

		UpdateStartDateForMonth();

		ArrayList<java.util.Date> alArrayList = new ArrayList<java.util.Date>();
		// 遍历数组
		for (int i = 1; i <= 42; i++) {
			alArrayList.add(calStartDate.getTime());
			calStartDate.add(Calendar.DAY_OF_MONTH, 1);
		}

		return alArrayList;
	}

	private Activity activity;
	Resources resources;

	// construct
	public CalendarGridViewAdapter(Activity a, Calendar cal) {
		calStartDate = cal;
		activity = a;
		resources = activity.getResources();
		titles = getDates();
	}

	public CalendarGridViewAdapter(Activity a) {
		activity = a;
		resources = activity.getResources();
	}

	@Override
	public int getCount() {
		return titles.size();
	}

	@Override
	public Object getItem(int position) {
		return titles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Map map = new HashMap(); //保存日期天\以及如果是考勤日期 保存类型
		map.put("type", "");
		RelativeLayout iv = new RelativeLayout(activity);
		iv.setId(position + 5000);
		iv.setBackgroundColor(resources.getColor(R.color.white));

		Date myDate = (Date) getItem(position);
		Calendar calCalendar = Calendar.getInstance();
		calCalendar.setTime(myDate);

		final int iMonth = calCalendar.get(Calendar.MONTH);
		final int iDay = calCalendar.get(Calendar.DAY_OF_WEEK);

		// 判断周六周日
		// iv.setBackgroundColor(resources.getColor(R.color.white));
		/*
		 * if (iDay == 7) { // 周六
		 * iv.setBackgroundColor(resources.getColor(R.color.text_6)); } else if
		 * (iDay == 1) { // 周日
		 * iv.setBackgroundColor(resources.getColor(R.color.text_7)); } else {
		 * 
		 * }
		 */
		// 判断周六周日结束

		if (equalsDate(calToday.getTime(), myDate)) {
			// 当前日期
			iv.setBackgroundColor(resources.getColor(R.color.event_center));
		}

	
		ImageView  imagview = new ImageView(activity); //标识图标
		
		// 设置背景颜色结束

		// 日期开始
		TextView txtDay = new TextView(activity);// 日期
		txtDay.setGravity(Gravity.CENTER);
		txtDay.setTextSize(16);
		// 判断是否是当前月
		if (iMonth == iMonthViewCurrentMonth) {
			
			// txtToDay.setTextColor(resources.getColor(R.color.ToDayText));
			txtDay.setTextColor(Color.parseColor("#000000"));
			iv.setBackgroundColor(Color.parseColor("#ffffffff"));
			
			// 遍历正常考勤
			if (noramlatten != null) {

				for (int i = 0; i < noramlatten.size(); i++) {
					Calendar normalattcal = noramlatten.get(i);

					if (equalsDate(normalattcal.getTime(), myDate)) {
						Log.v("equalsDate(normalattcal.getTime()", normalattcal.getTime().toString());
						Log.v("myDate", myDate.toString());
						map.put("type", attendantype.get(Util.CleandtoyyyyMMdd(normalattcal)));
						//map.put("daytype", teacherDevDayType.get(Util.CleandtoyyyyMMdd(normalattcal)));
						imagview.setImageResource(R.drawable.bj_chuq_bulenode1);
						imagview.setScaleType(ScaleType.CENTER);
					}else{
//						imagview.setImageBitmap(null);
					}
				}
			}

			// 遍历异常考勤
			if (unnoramlatten != null) {
				for (int i = 0; i < unnoramlatten.size(); i++) {
					Calendar unnormalattcal = unnoramlatten.get(i);
					Log.v("equalsDate(normalattcal.getTime()", unnormalattcal.getTime().toString());
					Log.v("myDate", myDate.toString());
					if (equalsDate(unnormalattcal.getTime(), myDate)) {
						imagview.setImageResource(R.drawable.bj_chuq_bulenode2);
						imagview.setScaleType(ScaleType.CENTER);
						map.put("type", attendantype.get(Util.CleandtoyyyyMMdd(unnormalattcal)));
						

					}else{
//						imagview.setImageBitmap(null);
					}
				}
			}
			if (actualizarlatten != null) {
				for (int i = 0; i < actualizarlatten.size(); i++) {
					Calendar unnormalattcal = actualizarlatten.get(i);
					if (equalsDate(unnormalattcal.getTime(), myDate)) {
						imagview.setImageResource(R.drawable.redpoint);
						imagview.setScaleType(ScaleType.CENTER);
						map.put("type", attendantype.get(Util.CleandtoyyyyMMdd(unnormalattcal)));
						

					}else{
//						imagview.setImageBitmap(null);
					}
				}
			}
			if (teacherDevDay != null) {
				for (int i = 0; i < teacherDevDay.size(); i++) {
					Calendar teacherDevDayCal = teacherDevDay.get(i);
					if (equalsDate(teacherDevDayCal.getTime(), myDate)) {
						iv.setBackgroundColor(Color.parseColor("#C8B5D5"));
						map.put("daytype", teacherDevDayType.get(Util.CleandtoyyyyMMdd(teacherDevDayCal)));
					}else{
//						imagview.setImageBitmap(null);
					}
				}
			}
			if(workDay!= null){
				for (int i = 0; i < workDay.size(); i++) {
					Calendar teacherDevDayCal = workDay.get(i);
					if (equalsDate(teacherDevDayCal.getTime(), myDate)) {
						//iv.setBackgroundColor(Color.parseColor("#FFC90E"));
						map.put("daytype",resources.getString(R.string.calendar_techerday_normal));
					}else{
//						imagview.setImageBitmap(null);
					}
				}
			}
			
		} else {
			iv.setBackgroundColor(Color.parseColor("#F4F0EF"));
			txtDay.setTextColor(resources.getColor(R.color.noMonth));
		}
		// 设置背景颜色
		if (equalsDate(calSelected.getTime(), myDate)) {
			// 选择的
			iv.setBackgroundColor(resources.getColor(R.color.selection));
			// iv.setBackgroundColor(resources.getColor(R.color.selection));
		} 

		int day = myDate.getDate(); // 日期
		txtDay.setText(String.valueOf(day));
		txtDay.setId(position + 500);
		map.put("date", myDate);
		iv.setTag(map);
       
		
		RelativeLayout.LayoutParams imageviewlp = new RelativeLayout.LayoutParams(
				23, 23);
		imageviewlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT|RelativeLayout.ALIGN_PARENT_TOP);
		
		iv.addView(imagview, imageviewlp);
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 70);
		iv.addView(txtDay, lp);

		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		// iv.addView(txtToDay, lp1);
		// 日期结束
		// iv.setOnClickListener(view_listener);

		return iv;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	private Boolean equalsDate(Date date1, Date date2) {

		if (date1.getYear() == date2.getYear()
				&& date1.getMonth() == date2.getMonth()
				&& date1.getDate() == date2.getDate()) {
			return true;
		} else {
			return false;
		}

	}

}

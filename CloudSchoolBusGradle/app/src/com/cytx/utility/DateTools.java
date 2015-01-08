package com.cytx.utility;

import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;

import com.Manga.Activity.R;

public class DateTools {
	
	
	/**
	 * 
	 * @param form 显示时间的格式，如："yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getDateForm(String form, long time){
		SimpleDateFormat df = new SimpleDateFormat(form);
		Timestamp now = new Timestamp(time);
		String str = df.format(now);

		return str;
	}
	
	/**
	 * 获得当前日期
	 * 
	 * @return 
	 */
	public static String getCurrentDate() {
		Calendar cal = Calendar.getInstance();
		String MONTH = isHaseZero(String.valueOf((cal.get(Calendar.MONTH) + 1)));
		int YEAR = cal.get(Calendar.YEAR);
		String DAY = isHaseZero(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
		return YEAR + "-" + MONTH + "-" + DAY;
	}
	
	/**
	 * 获取档期时间
	 * @return
	 */
	public static String getCurrentTime(){
		Calendar c = Calendar.getInstance();
		String hour = isHaseZero(String.valueOf(c.get(Calendar.HOUR_OF_DAY))); 
		String minute = isHaseZero(String.valueOf(c.get(Calendar.MINUTE))); 
		return hour + ":" + minute;
	}
	
	/**
	 * 判断数字前面是否有0
	 * 
	 * @param number
	 * @return
	 */
	public static String isHaseZero(String number) {
		if (number.length() == 1 && Integer.parseInt(number) < 10) {
			number = "0" + number;
		}
		return number;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static Date strToDateLong(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}
	
	/**
	 * 格式化时间 -- 显示成几分钟或几小时前样式
	 * 
	 * @param d
	 * @return 2012-3-1
	 */
	@SuppressLint("SimpleDateFormat")
	public static String ReturnBeforeTime(Date d, Context context) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");       
		String oldYear = formatter.format(d);       
		
		Calendar c = Calendar.getInstance();
		String nowYear = c.get(Calendar.YEAR) + "";
		String time = "";
		Timestamp timestamp = new Timestamp(d.getTime());
		long start = timestamp.getTime() / 1000;

		long end = System.currentTimeMillis();
		String endString = String.valueOf(end);
		if (endString.length() > 10) {
			end = end / 1000;
		}
		int hour = (int) ((end - start) / 60 / 60);
		int min = (int) ((end - start) / 60);
		int sen = (int) ((end - start));
		if (sen < 30) {
			time = context.getResources().getString(R.string.just_now);
		} else if (sen < 60) {
			time = sen + context.getResources().getString(R.string.second_ago);
		} else if (min < 60) {
			time = min + context.getResources().getString(R.string.minute_ago);
		} else if (hour < 12) {
			time = hour + context.getResources().getString(R.string.hour_ago);
		} else if (oldYear.equals(nowYear)) {// 在本年，则显示月、日和时间
			time = new SimpleDateFormat("MM-dd HH:mm").format(d);
		} else if (oldYear.compareTo(nowYear) < 0){
			SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			time = s.format(d);
		}
		return time;
	}

}

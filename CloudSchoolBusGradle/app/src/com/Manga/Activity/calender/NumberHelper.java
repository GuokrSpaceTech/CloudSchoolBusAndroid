package com.Manga.Activity.calender;

public class NumberHelper {
	/************************************************************************
	 *	项目名字	:带手势滑动功能的日历 
	 * @author  angelの慧
	 * @version 2012-10-08
	　*
	************************************************************************/

	public static String LeftPad_Tow_Zero(int str) {
		java.text.DecimalFormat format = new java.text.DecimalFormat("00");
		return format.format(str);

	}
	

}

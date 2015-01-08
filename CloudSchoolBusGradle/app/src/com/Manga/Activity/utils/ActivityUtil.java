package com.Manga.Activity.utils;

import android.app.Activity;
import android.content.Intent;

import com.Manga.Activity.HomeActivity;
import com.Manga.Activity.LoginActivity;
import com.Manga.Activity.MainActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.ClassUpdate.ClassUpdateActivity;
import com.Manga.Activity.ClassUpdate.ClassUpdateMainBodyActivity;
import com.Manga.Activity.Gestures.GesturesOpenActivity;
import com.Manga.Activity.Gestures.GesturesSetActivity;
import com.Manga.Activity.LeftSetting.LeftSettingActivity;
import com.Manga.Activity.Msg.SelectChildrenActivity;
import com.Manga.Activity.account.MyAccountActivity;
import com.Manga.Activity.activity.ActivityRegisterActivity;
import com.Manga.Activity.base.BaseInfoNewActivity;
import com.Manga.Activity.managepw.ManagePasswordActivity;
import com.Manga.Activity.myChildren.MyChildrenActivity;
import com.Manga.Activity.myChildren.DoctorConsult.DoctorActivity;
import com.Manga.Activity.myChildren.FamilyMembers.AddFamilyMemberActivity;
import com.Manga.Activity.myChildren.FamilyMembers.FamilyMembersActivity;
import com.Manga.Activity.myChildren.FamilyMembers.SetPortaitActivity;
import com.Manga.Activity.myChildren.Reports.ReportActivity;
import com.Manga.Activity.myChildren.Reports.ReportSearchActivity;
import com.Manga.Activity.myChildren.Shuttlebus.ShuttlebusActivity;
import com.Manga.Activity.myChildren.Shuttlebus.ShuttlebusNoticeActivity;
import com.Manga.Activity.myChildren.Streaming.Preview;
import com.Manga.Activity.myChildren.SwitchChildren.ManageChildrenActivity;
import com.Manga.Activity.myChildren.SwitchChildren.ManageChildrenSettingActivity;
import com.Manga.Activity.myChildren.morningCheck.HealthActivity;
import com.Manga.Activity.myChildren.morningCheck.MoringCheckActivity;
import com.Manga.Activity.notification.NotificationActivity;

public class ActivityUtil {
	/**
	 * 引用传递
	 */
	public static MainActivity main;
	public static ClassUpdateActivity share;
	public static NotificationActivity notice;
	public static ActivityRegisterActivity activityRegister;
	public static MyChildrenActivity mychildren;
	public static BaseInfoNewActivity baseinfo;
	public static GesturesSetActivity select1;
	public static GesturesOpenActivity select2;
	public static ManagePasswordActivity managepassword;
	public static LoginActivity login;
	public static SelectChildrenActivity selchildren;
	public static LeftSettingActivity leftsetting;
	public static ManageChildrenActivity managechildren;
	public static ManageChildrenSettingActivity managechildrenset;
	public static ClassUpdateMainBodyActivity shareMain;
	public static HomeActivity home;
	public static MyAccountActivity accountActivity;
	public static DoctorActivity doctorActivity;
	public static AddFamilyMemberActivity addShuttleActivity;
	public static SetPortaitActivity addHeadActivity;
	public static FamilyMembersActivity shuttleActivity;
	public static HealthActivity healthActivity;
	public static ReportActivity reportActivity;
	public static MoringCheckActivity moringCheckActivity;
	public static ReportSearchActivity reportSearchActivity;
	public static Preview classVideoStreaming;
	public static ShuttlebusActivity shuttlebusactivity;
	public static ShuttlebusNoticeActivity shuttlebusnoticeactivity;

	/**
	 * 退出动画
	 * 
	 * @param activity
	 *            要退出的Activity
	 */
	public static void close(Activity activity) {
		activity.finish();
		activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

	/**
	 * 进入动画
	 * 
	 * @param activity
	 *            启动Activity
	 * @param intent
	 *            开启纽带
	 */
	public static void startActivity(Activity activity, Intent intent) {
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
	}
}
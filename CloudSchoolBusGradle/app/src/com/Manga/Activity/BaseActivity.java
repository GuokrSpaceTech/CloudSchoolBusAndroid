package com.Manga.Activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.Manga.Activity.Gestures.GesturesOpenActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * 名称：BaseActivity 描述： 创建人： 日期：2012-6-20 下午5:53:35 变更：
 */

public class BaseActivity extends Activity {
	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences sp = this.getSharedPreferences("GestureData", Context.MODE_PRIVATE);
		sp.edit().putString("timeb", "0").commit();
		sp.edit().putString("times", "0").commit();
		if (!isAppOnForeground()) {
			// app 进入后台
			sp.edit().putBoolean("isActive", false).commit();
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
			String foo = sdf.format(date);

			SimpleDateFormat sdf1 = new SimpleDateFormat("mm");
			String foo1 = sdf1.format(date);
			sp.edit().putString("timeb", foo).commit();
			sp.edit().putString("times", foo1).commit();
			// 全局变量isActive = false 记录当前已经进入后台
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sp = this.getSharedPreferences("GestureData", Context.MODE_PRIVATE);
		if (isAppOnForeground()) {
			if (!sp.getBoolean("isActive", true)) {
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
				String foo = sdf.format(date);

				SimpleDateFormat sdf1 = new SimpleDateFormat("mm");
				String foo1 = sdf1.format(date);
				if (foo.equals(sp.getString("timeb", "")) && (Integer.parseInt(foo1) - Integer.parseInt(sp.getString("times", ""))) >= 2) {

					if ("success".equals(sp.getString("strLockSuccess", ""))) {
						sp.edit().putBoolean("isActive", true).commit();
						Intent intent = new Intent(this, GesturesOpenActivity.class);
						startActivity(intent);
					}
				}
			}
		}
	}

	/**
	 * 程序是否在前台运行
	 * 
	 * @return
	 */
	public boolean isAppOnForeground() {

		ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = getApplicationContext().getPackageName();

		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName) && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}

		return false;
	}
}
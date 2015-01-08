package com.Manga.Activity.LeftSetting;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.ChangeBg.ChangeBgActivity;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.Msg.MsgVersionActivity;
import com.Manga.Activity.about.AboutUsActivity;
import com.Manga.Activity.help.HelpSupportActivity;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.managepw.ManagePasswordActivity;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.Student_Info;
import com.umeng.analytics.MobclickAgent;

public class LeftSettingActivity extends BaseActivity {
	private Drawable close;
	private Drawable open;
	private Button checkBox;
	public String strVersionExplain;
	public String strVersionUrl;
	private View closeAutoPlay;
	private View wifiAutoPlay;
	private View allAutoPlay;
	/**
	 * 无学生数据
	 */
	private static final int HAVENTDATA = 0;
	/**
	 * 修改多设备在线失败
	 */
	private static final int MODIFIMOREPHONEFAIL = 1;
	/**
	 * 修改多设备在线成功
	 */
	private static final int MODIFIONLINESUCCES = 2;
	/**
	 * 超时
	 */
	private static final int NETOUT = 3;
	/**
	 * 检查版本
	 */
	private static final int CHECKVERSION = 4;
	/**
	 * 已经新版本
	 */
	private static final int CHECKVERSIONNEW = 5;
	/**
	 * 更新版本
	 */
	private static final int LODINGNEWVERSION = 6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.left_setting);
		ActivityUtil.main.showPRO();
		ActivityUtil.leftsetting = this;
		checkBox = (Button) findViewById(R.id.checkBox);
		closeAutoPlay = findViewById(R.id.closeAutoPlay);
		wifiAutoPlay = findViewById(R.id.wifiAutoPlay);
		allAutoPlay = findViewById(R.id.allAutoPlay);
		close = getResources().getDrawable(R.drawable.close);
		open = getResources().getDrawable(R.drawable.open);
		init();
		ActivityUtil.main.disPRO();
	}

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message mess) {
			// TODO Auto-generated method stub
			switch (mess.what) {
			case NETOUT:
				Toast.makeText(LeftSettingActivity.this, R.string.allow_muti_online_net, Toast.LENGTH_SHORT).show();
				break;

			case HAVENTDATA:
				Toast.makeText(LeftSettingActivity.this, R.string.no_student_data, Toast.LENGTH_SHORT).show();
				break;
			case MODIFIMOREPHONEFAIL:
				Toast.makeText(LeftSettingActivity.this, R.string.modifi_more_phone_fail, Toast.LENGTH_SHORT).show();
				break;
			case MODIFIONLINESUCCES:
				if (open.equals(checkBox.getBackground())) {
					checkBox.setBackgroundDrawable(close);
					Toast.makeText(LeftSettingActivity.this, R.string.modifi_more_phone_close_success,
							Toast.LENGTH_SHORT).show();
				} else {
					checkBox.setBackgroundDrawable(open);
					Toast.makeText(LeftSettingActivity.this, R.string.modifi_more_phone_open_success,
							Toast.LENGTH_SHORT).show();
				}
				break;
			case CHECKVERSION:
				Toast.makeText(LeftSettingActivity.this, R.string.check_version, Toast.LENGTH_SHORT).show();
				break;
			case CHECKVERSIONNEW:
				Toast.makeText(LeftSettingActivity.this, R.string.check_version_result, Toast.LENGTH_SHORT).show();
				break;
			case LODINGNEWVERSION:
				try {
					final JSONObject myJson = (JSONObject) mess.obj;
					strVersionExplain = myJson.getString("version_explain");
					strVersionUrl = myJson.getString("version_url");
					Intent intent = new Intent(LeftSettingActivity.this, MsgVersionActivity.class);
					startActivity(intent);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			return false;
		}
	});

	private void init() {
		DB db = new DB(this);
		SQLiteDatabase sql = db.getReadableDatabase();
		Cursor cursor = sql.query("student_info", null, "uid=?", new String[] { Student_Info.uid }, null, null, null);
		if (cursor == null || cursor.getCount() == 0) {
			handler.sendEmptyMessage(HAVENTDATA);
		} else {
			cursor.moveToFirst();
			int online = Integer.parseInt(cursor.getString(cursor.getColumnIndex("allow_muti_online")));
			switch (online) {
			case 2:
				checkBox.setBackgroundDrawable(close);
				break;
			case 1:
				checkBox.setBackgroundDrawable(open);
				break;
			}
			cursor.close();
		}
		sql.close();
		db.close();
		SharedPreferences sp = this.getSharedPreferences("auto_paly", Context.MODE_PRIVATE);
		if (sp.getBoolean("closeAutoPlay", false)) {
			closeAutoPlay.setBackgroundResource(R.drawable.yes);
		} else {
			closeAutoPlay.setBackgroundColor(Color.TRANSPARENT);
			if (sp.getBoolean("wifiAutoPlay", false) == false && sp.getBoolean("allAutoPlay", false) == false) {
				closeAutoPlay.setBackgroundResource(R.drawable.yes);
			}
		}
		if (sp.getBoolean("wifiAutoPlay", false)) {
			wifiAutoPlay.setBackgroundResource(R.drawable.yes);
		} else {
			wifiAutoPlay.setBackgroundColor(Color.TRANSPARENT);
		}
		if (sp.getBoolean("allAutoPlay", false)) {
			allAutoPlay.setBackgroundResource(R.drawable.yes);
		} else {
			allAutoPlay.setBackgroundColor(Color.TRANSPARENT);
		}
	}

	public void managepsd(View v) {
		Intent intent = new Intent(this, ManagePasswordActivity.class);
		ActivityUtil.main.comeIn(intent);
	}

	public void background(View view) {
		Intent intent = new Intent(LeftSettingActivity.this, ChangeBgActivity.class);
		ActivityUtil.main.comeIn(intent);
	}

	public void close(View view) {
		ActivityUtil.main.move();
	}

	public void checkBox(final View v) {
		final HashMap<String, String> map = new HashMap<String, String>();
		if (open.equals(checkBox.getBackground())) {
			map.put("allow_muti_online", 2 + "");
		} else {
			map.put("allow_muti_online", 1 + "");
		}
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (HttpUtil.isNetworkConnected(LeftSettingActivity.this)) {
					Params param = new Params("student", map);
					Result result = HttpUtil.httpPost(LeftSettingActivity.this, param);
					if (result == null) {
						handler.sendEmptyMessage(MODIFIMOREPHONEFAIL);
					} else if ("1".equals(result.getCode())) {
						handler.sendEmptyMessage(MODIFIONLINESUCCES);
					} else {
						handler.sendEmptyMessage(MODIFIMOREPHONEFAIL);
					}
				} else {
					handler.sendEmptyMessage(NETOUT);
				}
			}
		});
		thread.start();
	}

	public void version(View v) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (HttpUtil.isNetworkConnected(LeftSettingActivity.this)) {
					handler.sendEmptyMessage(CHECKVERSION);
					Params param = new Params("version", null);
					Result result = HttpUtil.httpGet(LeftSettingActivity.this, param);
					if (result == null) {
						handler.sendEmptyMessage(NETOUT);
					} else {
						try {
							PackageManager packageManager = getPackageManager();
							PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
							String version = packInfo.versionName;
							JSONObject myJson = new JSONObject(result.getContent());
							if (version.equals(myJson.getString("version_no"))) {
								handler.sendEmptyMessage(CHECKVERSIONNEW);
								Log.v("测试", "1111");
							} else {
								Message mess = handler.obtainMessage(LODINGNEWVERSION, myJson);
								handler.sendMessage(mess);
								Log.v("测试", "2222");
							}
						} catch (NameNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					handler.sendEmptyMessage(NETOUT);
				}
			}
		});
		thread.start();
	}

	/**
	 * 关闭自动播放
	 */
	public void closeAutoPlay(View v) {
		SharedPreferences sp = this.getSharedPreferences("auto_paly", Context.MODE_PRIVATE);
		closeAutoPlay.setBackgroundResource(R.drawable.yes);
		wifiAutoPlay.setBackgroundColor(Color.TRANSPARENT);
		allAutoPlay.setBackgroundColor(Color.TRANSPARENT);
		sp.edit().putBoolean("closeAutoPlay", true).commit();
		sp.edit().putBoolean("wifiAutoPlay", false).commit();
		sp.edit().putBoolean("allAutoPlay", false).commit();
	}

	/**
	 * wifi下自动播放
	 */
	public void wifiAutoPlay(View v) {
		SharedPreferences sp = this.getSharedPreferences("auto_paly", Context.MODE_PRIVATE);
		closeAutoPlay.setBackgroundColor(Color.TRANSPARENT);
		wifiAutoPlay.setBackgroundResource(R.drawable.yes);
		allAutoPlay.setBackgroundColor(Color.TRANSPARENT);
		sp.edit().putBoolean("closeAutoPlay", false).commit();
		sp.edit().putBoolean("wifiAutoPlay", true).commit();
		sp.edit().putBoolean("allAutoPlay", false).commit();
	}

	/**
	 * 总是自动播放
	 */
	public void allWayPlay(View v) {
		SharedPreferences sp = this.getSharedPreferences("auto_paly", Context.MODE_PRIVATE);
		closeAutoPlay.setBackgroundColor(Color.TRANSPARENT);
		wifiAutoPlay.setBackgroundColor(Color.TRANSPARENT);
		allAutoPlay.setBackgroundResource(R.drawable.yes);
		sp.edit().putBoolean("closeAutoPlay", false).commit();
		sp.edit().putBoolean("wifiAutoPlay", false).commit();
		sp.edit().putBoolean("allAutoPlay", true).commit();
	}

	public void aboutUs(View v) {
		Intent intent = new Intent(this, AboutUsActivity.class);
		ActivityUtil.main.comeIn(intent);
	}

	public void checkHelp(View v) {
		Intent intent = new Intent(this, HelpSupportActivity.class);
		ActivityUtil.main.comeIn(intent);
	}

	public String getVersionExplain() {
		return strVersionExplain;
	}

	public String getVersionUrl() {
		return strVersionUrl;
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

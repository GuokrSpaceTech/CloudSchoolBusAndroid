package com.Manga.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.Manga.Activity.DB.DB;
import com.Manga.Activity.Entity.Baseinfo;
import com.Manga.Activity.Msg.SelectChildrenActivity;
import com.Manga.Activity.base.BaseInfoActivity;
import com.Manga.Activity.encryption.ooo;
import com.Manga.Activity.forget.InputPhoneNumActivity;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.Student_Info;
import com.cytx.utility.FastJsonTools;
import com.umeng.analytics.MobclickAgent;

public class LoginActivity extends Activity {
	/**
	 * 登录显示
	 */
	private static final int LOGIN = 0;
	/**
	 * 登录失败显示
	 */
	private static final int LOGINFAILED = 1;
	/**
	 * 登录帐号错误
	 */
	private static final int LOGINUSERNAMEFAILED = 2;
	/**
	 * 登录密码错误
	 */
	private static final int LOGINPWFAILED = 3;
	/**
	 * 登录成功
	 */
	private static final int LOGINSUCCESS = 4;
	/**
	 * 请输入帐号
	 */
	private static final int PLEASE_INPUT_SUERNAME = 5;
	/**
	 * 请输入密码
	 */
	private static final int PLEASE_INPUT_PASSWORD = 6;
	/**
	 * 登录成功
	 */
	private static final int LOGINED = 7;
    private static final int RECEIVED_CLASS_INFO = 8; //收到了班级信息
    private static final int RECEIVED_DATA_ERROR = 9; //收到了班级信息
	/*
	 * 没有网络
	 */
	private static final int MSG_NO_NETWORK = -1;

	private EditText         username;
	private EditText         password;
	public  static Context   logincontext;
    private Baseinfo         mBaseInfo = new Baseinfo();

	private Thread thread1;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message message) {
			removeDialog(LOGIN);
			switch (message.what) {
			case LOGINSUCCESS:
				Toast.makeText(LoginActivity.this, R.string.current_success, Toast.LENGTH_SHORT).show();
				break;
			case LOGINFAILED:
				Toast.makeText(LoginActivity.this, R.string.current_failed, Toast.LENGTH_SHORT).show();
				break;
			case LOGINPWFAILED:
				Toast.makeText(LoginActivity.this, R.string.lodding_password_failed, Toast.LENGTH_SHORT).show();
				break;
			case LOGINUSERNAMEFAILED:
				Toast.makeText(LoginActivity.this, R.string.lodding_username_failed, Toast.LENGTH_SHORT).show();
				break;
			case PLEASE_INPUT_SUERNAME:
				Toast.makeText(LoginActivity.this, R.string.please_input_suername, Toast.LENGTH_SHORT).show();
				break;
			case PLEASE_INPUT_PASSWORD:
				Toast.makeText(LoginActivity.this, R.string.please_input_password, Toast.LENGTH_SHORT).show();
				break;
			case MSG_NO_NETWORK:
				Toast.makeText(LoginActivity.this, R.string.net_is_not_working, Toast.LENGTH_SHORT).show();
				break;
            case RECEIVED_DATA_ERROR:
                Toast.makeText(LoginActivity.this, R.string.recv_data_error, Toast.LENGTH_SHORT).show();
                break;
			case LOGINED:
                getClassInfo();
				break;
            case RECEIVED_CLASS_INFO:
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(intent);
                break;
            default:
                break;
			}
			return false;
		}
	});

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logging);
		ActivityUtil.login = this;
		username = (EditText) findViewById(R.id.logging__username);
		password = (EditText) findViewById(R.id.logging_password);
		logincontext = this;
	}

	/**
	 * 清空数据库
	 */
	private void clearDb() {
		DB db = new DB(this);
		SQLiteDatabase sql = db.getWritableDatabase();
		sql.delete("article", null, null);
		sql.delete("like", null, null);
		sql.delete("comment", null, null);
		sql.delete("notice", null, null);
		sql.delete("tnotice", null, null);
		sql.delete("article", null, null);
		sql.close();
		db.close();
	}

	@Override
	protected void onStart() {
		super.onStart();
		clearDb();
		fetchCredential();
		if (checkRule()) {
			login();
		}
	}

	/**
	 * 登录方法
	 */
	private void login() {
		showDialog(LOGIN);
		thread1 = new Thread(new Runnable() {
			@Override
			public void run() {
				String username = LoginActivity.this.username.getText().toString();
				String userpassword = LoginActivity.this.password.getText().toString();
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("username", username);
				map.put("password", ooo.h(userpassword, "mactop", 0));
				if(HttpUtil.isNetworkConnected(LoginActivity.this))
				{
					Result result = HttpUtil.httpPost(LoginActivity.this, new Params("signin", map));
					if (result != null) {
						String tmp = result.getCode();
						if ("1".equals(tmp)) {
							handler.sendEmptyMessage(LOGINSUCCESS);
							try {
								DB db = new DB(LoginActivity.this);
								SQLiteDatabase sql = db.getReadableDatabase();
								Cursor cursor = sql.query("signin", null, null, null, null, null, null, null);
								// 表里有数据 清空
								if (cursor != null) {
									sql.delete("signin", null, null);
									cursor.close();
								}
								// update 判断家长账号下学生时，增加不显示已离班状态的孩子
								JSONArray array = new JSONArray(result.getContent());
								String[] inactive = new String[array.length()];
								String[] uid_student = new String[array.length()];
								String[] uid_class = new String[array.length()];
								String[] nickname = new String[array.length()];
								String[] classname = new String[array.length()];
								boolean isHaveInactive = false;
								for (int i = 0; i < array.length(); i++) {
									ContentValues values = new ContentValues();
									JSONObject jsonObject = array.getJSONObject(i);
									String inactiveTemp = jsonObject.getString("inactive");
									if ("0".equals(inactiveTemp)) {
										isHaveInactive = true;
										uid_student[i] = jsonObject.getString("uid_student");
										uid_class[i] = jsonObject.getString("uid_class");
										nickname[i] = jsonObject.getString("nikename");
										classname[i] = jsonObject.getString("classname");
										inactive[i] = jsonObject.getString("inactive");
										values.put("u_id",jsonObject.getString("uid_class") + jsonObject.getString("uid_student"));
										values.put("uid_student", jsonObject.getString("uid_student"));
										values.put("uid_class", jsonObject.getString("uid_class"));
										values.put("nikename", jsonObject.getString("nikename"));
										values.put("classname",
												jsonObject.getString("classname") + "," + jsonObject.getString("inactive"));
										sql.insert("signin", "content", values);
									}
								}
								sql.close();
								db.close();
								saveCredential();
								if (isHaveInactive) {
									uid_student = deleteEmpty(uid_student);
									uid_class = deleteEmpty(uid_class);
									nickname = deleteEmpty(nickname);
									classname = deleteEmpty(classname);
								}
								if (uid_student.length == 1) {
									jumpView(true, uid_student, uid_class, nickname, classname);
								} else {
									jumpView(false, uid_student, uid_class, nickname, classname);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else if ("-1010".equals(tmp) || "-3".equals(tmp)) {
							handler.sendEmptyMessage(LOGINUSERNAMEFAILED);
						} else if ("-1011".equals(tmp) || "-1013".equals(tmp)) {
							handler.sendEmptyMessage(LOGINPWFAILED);
						} else {
							handler.sendEmptyMessage(LOGINUSERNAMEFAILED);
						}
					} else {
						handler.sendEmptyMessage(LOGINFAILED);
					}
				}
				else
				{
					handler.sendEmptyMessage(MSG_NO_NETWORK);
				}
			}
		});
		if (!thread1.isAlive()) {
			thread1.start();
		}
	}


    /*
     * 获取班级信息
     */
    private void getClassInfo()
    {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                if (HttpUtil.isNetworkConnected(LoginActivity.this)) {
                    Params params = new Params("classinfo", null);
                    Result result = HttpUtil.httpGet(LoginActivity.this, params);
                    if (result == null) {
                        handler.sendEmptyMessage(MSG_NO_NETWORK);
                    } else if ("1".equals(result.getCode())) {
//                        DB db = new DB(LoginActivity.this);
//                        SQLiteDatabase sql = db.getWritableDatabase();
                        mBaseInfo = FastJsonTools.getObject(result.getContent(),Baseinfo.class);

//                        sql.close();
//                        db.close();
                        handler.sendEmptyMessage(RECEIVED_CLASS_INFO);
                    } else {
                        handler.sendEmptyMessage(RECEIVED_DATA_ERROR);
                    }
                } else {
                    handler.sendEmptyMessage(MSG_NO_NETWORK);
                }
            }
        });
        thread.start();
    }

	/**
	 * Strip
	 */
	public String[] deleteEmpty(String[] temp) {
		List<String> strlist = new ArrayList<String>();
		String st = "";
		for (String strings : temp) {
			strlist.add(strings);
		}
		for (String strs : strlist) {
			if (strs != null && !"".equals(strs)) {
				st += strs + ",";
			}
		}
		return st.split(",");
	}

	/**
	 * 保存登录信息 保存在轻量级存储LoggingData文件中 password:String username:String isAutoLogging:boolean
	 */
	private void saveCredential() {
		SharedPreferences sp = LoginActivity.this.getSharedPreferences("LoggingData", Context.MODE_PRIVATE);
		/*
		 * if(remmberPw.isChecked()){ sp.edit().putString("password", password.getText().toString()).commit(); }else{
		 * sp.edit().putString("password", "").commit(); } if(autoLogging.isChecked()){
		 * sp.edit().putBoolean("isAutoLogging", true).commit(); }else{ sp.edit().putBoolean("isAutoLogging",
		 * false).commit(); }
		 */
		sp.edit().putString("password", password.getText().toString()).commit();
		sp.edit().putBoolean("isAutoLogging", true).commit();
		sp.edit().putString("username", username.getText().toString().trim()).commit();
	}

	/**
	 * 初始化信息
	 */
	private void fetchCredential() {
		SharedPreferences sp = LoginActivity.this.getSharedPreferences("LoggingData", Context.MODE_PRIVATE);
		username.setText(sp.getString("username", ""));
		password.setText(sp.getString("password", ""));
	}

	/**
	 * 检查用户名与密码是否符合规则
	 * 
	 * @return
	 */
	private boolean checkRule() {
		if ("".equals(username.getText().toString().trim())) {
			handler.sendEmptyMessage(PLEASE_INPUT_SUERNAME);
			return false;
		} else if ("".equals(password.getText().toString().trim())) {
			handler.sendEmptyMessage(PLEASE_INPUT_PASSWORD);
			return false;
		}
		return true;
	}

	/**
	 * 按钮登录
	 * 
	 * @param v
	 */
	public void login(View v) {
		if (checkRule()) {
			login();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case LOGIN:
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage(getResources().getString(R.string.current_lodding));
			dialog.setIndeterminate(false);
			dialog.setCancelable(false);
			return dialog;
		}
		return super.onCreateDialog(id);
	}

	/**
	 * 跳转Main界面
	 */
	private void jumpView(boolean isOne, final String[] uid_student, final String[] uid_class, final String[] nikename,
			final String[] classname) {
		if (isOne) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					final HashMap<String, String> map = new HashMap<String, String>();
					map.put("uid_student", uid_student[0]);
					map.put("uid_class", uid_class[0]);
					Result result = HttpUtil.httpPost(LoginActivity.this, new Params("unit", map));
					if ("1".equals(result.getCode())) {
						try {
							SharedPreferences shp = LoginActivity.this.getSharedPreferences("sid", Context.MODE_PRIVATE);
							Editor editor = shp.edit();
							JSONObject jso = new JSONObject(result.getContent());
							editor.putString("id", uid_class[0] + uid_student[0]);
							editor.putString("sid", jso.getString("sid"));
							editor.commit();
							SharedPreferences shp1 = LoginActivity.this.getSharedPreferences("nikename", Context.MODE_PRIVATE);
							Editor editor1 = shp1.edit();
							editor1.putString("nikename", nikename[0]);
							editor1.commit();
							handler.sendEmptyMessage(LOGINED);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						Log.v("登录失败", "获取SID 失败");
					}
				}
			});
			thread.start();
		} else {
			Intent intent = new Intent(this, SelectChildrenActivity.class);
			intent.putExtra("uid_student", uid_student);
			intent.putExtra("uid_class", uid_class);
			intent.putExtra("nikename", nikename);
			intent.putExtra("classname", classname);
			startActivity(intent);
			// finish();
		}
	}

	public void close() {
		// finish();
	}

	public void forgetPW(View v) {
		Intent intent = new Intent(this, InputPhoneNumActivity.class);
		ActivityUtil.startActivity(this, intent);
	}


    public Baseinfo getmBaseInfo() {
        return mBaseInfo;
    }

    public void setmBaseInfo(Baseinfo mBaseInfo) {
        this.mBaseInfo = mBaseInfo;
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
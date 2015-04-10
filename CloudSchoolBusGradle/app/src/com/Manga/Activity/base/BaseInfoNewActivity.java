package com.Manga.Activity.base;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.Msg.SelectHeadActivity;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.managepw.ManagePasswordActivity;
import com.Manga.Activity.modifi.NikeNameActivity;
import com.Manga.Activity.myChildren.MyChildrenActivity;
import com.Manga.Activity.myChildren.FamilyMembers.FamilyMembersActivity;
import com.Manga.Activity.myChildren.SwitchChildren.ManageChildrenActivity;
import com.Manga.Activity.myChildren.morningCheck.HealthActivity;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.ChildReceiver;
import com.Manga.Activity.utils.ImageUtil;
import com.Manga.Activity.utils.Student_Info;
import com.umeng.analytics.MobclickAgent;

public class BaseInfoNewActivity extends BaseActivity {
	/**
	 * 无学生数据
	 */
	private static final int HAVENTDATA = 0;
	/**
	 * 未能获得学校信息
	 */
	private static final int HAVENTSCHOOLDATA = 1;
	/**
	 * 无网络
	 */
	private static final int NETISNOTWORKING = 2;
	/**
	 * 连接超时
	 */
	private static final int OUTTIME = 3;
	/**
	 * image回调
	 */
	private static final int IMAGEBACK = 5;

	private static final int CAMERA = 6;
	private static final int IMAGESTORAGE = 7;
	/**
	 * 无网络上出图片
	 */
	private static final int UPLONDINGIMAGE = 8;
	/**
	 * 头像上传超时
	 */
	private static final int OUTTIMEIMAGE = 9;
	/**
	 * 头像上传成功
	 */
	private static final int UPLODINGHEADER = 10;
	/**
	 * 头像上传失败
	 */
	private static final int UPLODINGHEADERF = 11;
	/**
	 * 修改学生性别网络不存在
	 */
	private static final int UPLODINGGENDERISNTNETWOEK = 12;
	/**
	 * 修改学生性别网络 超时
	 */
	private static final int UPLODINGGENDERISNTNETWOEKOUTTIME = 13;
	/**
	 * 修改学生性别失败
	 */
	private static final int UPLODINGGENDERISNTNETWOEKFAILE = 14;
	/**
	 * 更新界面
	 */
	private static final int CHECKVIEW = 15;
	/**
	 * 进度条
	 */
	private static final int SHOWPROGRESS = 16;
	/**
	 * 取消进度条显示
	 */
	private static final int DISMISSPROGRESS = 17;
	/**
	 * 无网络生日
	 */
	private static final int NONETWORKBIRTHDAY = 18;
	/**
	 * 修改生日网络超时
	 */
	private static final int OUTTIMEBIRTHDAY = 19;
	/**
	 * 修改生日失败
	 */
	private static final int MODIFIBIRTHDAY = 20;
	/**
	 * 修改多设备在线失败
	 */
	private static final int MODIFIMOREPHONEFAIL = 21;
	/**
	 * 修改多设备在线成功
	 */
	private static final int MODIFIONLINESUCCES = 22;
	/**
	 * 修改多设备在线成功
	 */
	private static final int SUCCESSALL = 24;
	private static final int PEPOLE = 25;
	private static final int PEPOLEEMPT = 26;
	private TextView name;
	private TextView className;
	private TextView schoolName;
	private TextView serviceName;
	public TextView setting_student_shuttle_content, setting_student_allergy_content;
	/**
	 * 初始化学校信息
	 */
	private static final int INITSCHOOL = 23;
	private ProgressDialog progressDialog;
	private GestureDetector detector;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message mess) {
			// TODO Auto-generated method stub
			switch (mess.what) {
			case HAVENTDATA:
				Toast.makeText(BaseInfoNewActivity.this, R.string.no_student_data, Toast.LENGTH_SHORT).show();
				break;
			case HAVENTSCHOOLDATA:
				Toast.makeText(BaseInfoNewActivity.this, R.string.no_student_school_data, Toast.LENGTH_SHORT).show();
				break;
			case NETISNOTWORKING:
				Toast.makeText(BaseInfoNewActivity.this, R.string.net_is_not_working, Toast.LENGTH_SHORT).show();
				break;
			case OUTTIME:
				Toast.makeText(BaseInfoNewActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				break;
			case UPLONDINGIMAGE:
				Toast.makeText(BaseInfoNewActivity.this, R.string.no_network_cannt_uploding_image, Toast.LENGTH_SHORT)
						.show();
				break;
			case OUTTIMEIMAGE:
				Toast.makeText(BaseInfoNewActivity.this, R.string.no_network_cannt_uploding_image_outtime,
						Toast.LENGTH_SHORT).show();
				break;
			case UPLODINGHEADER:
				ActivityUtil.share.init_student_info_view();
				ActivityUtil.mychildren.init();
				// Toast.makeText(BaseInfoNewActivity.this,
				// R.string.uploding_image_ok, Toast.LENGTH_SHORT).show();
				break;
			case UPLODINGHEADERF:
				Toast.makeText(BaseInfoNewActivity.this, R.string.uploding_image_die, Toast.LENGTH_SHORT).show();
				break;
			case UPLODINGGENDERISNTNETWOEK:
				Toast.makeText(BaseInfoNewActivity.this, R.string.no_network_cannt_modifi_gender, Toast.LENGTH_SHORT)
						.show();
				break;
			case UPLODINGGENDERISNTNETWOEKOUTTIME:
				Toast.makeText(BaseInfoNewActivity.this, R.string.no_network_cannt_modifi_gender_outtime,
						Toast.LENGTH_SHORT).show();
				break;
			case UPLODINGGENDERISNTNETWOEKFAILE:
				Toast.makeText(BaseInfoNewActivity.this, R.string.modifi_gender_fail, Toast.LENGTH_SHORT).show();
				break;
			case SUCCESSALL:
				Toast.makeText(BaseInfoNewActivity.this, R.string.lock_success, Toast.LENGTH_SHORT).show();
				break;
			case CHECKVIEW:
				Toast.makeText(BaseInfoNewActivity.this, R.string.lock_success, Toast.LENGTH_SHORT).show();
				if (ActivityUtil.notice != null) {
					ActivityUtil.notice.UpdateHead();
				}
				if (ActivityUtil.activityRegister != null) {
					ActivityUtil.activityRegister.UpdateHead();
				}
				if (ActivityUtil.share != null) {
					ActivityUtil.share.UpdateHead();
				}
				checkStudentInfo();
				break;
			case SHOWPROGRESS:
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(BaseInfoNewActivity.this);
					progressDialog.setMessage(getResources().getString(R.string.init_view));
				}
				progressDialog.show();
				break;
			case DISMISSPROGRESS:
				progressDialog.dismiss();
				break;
			case NONETWORKBIRTHDAY:
				Toast.makeText(BaseInfoNewActivity.this, R.string.no_network_cannt_modifi_birthday, Toast.LENGTH_SHORT)
						.show();
				break;
			case OUTTIMEBIRTHDAY:
				Toast.makeText(BaseInfoNewActivity.this, R.string.no_network_cannt_modifi_birthday_outtime,
						Toast.LENGTH_SHORT).show();
				break;
			case MODIFIBIRTHDAY:
				Toast.makeText(BaseInfoNewActivity.this, R.string.modifi_birthday_fail, Toast.LENGTH_SHORT).show();
				break;
			case MODIFIMOREPHONEFAIL:
				Toast.makeText(BaseInfoNewActivity.this, R.string.modifi_more_phone_fail, Toast.LENGTH_SHORT).show();
				break;
			case INITSCHOOL:
				initSchool();
				break;
			case PEPOLE:
				if (FamilyMembersActivity.childReceiverList.size() == 0) {
					setting_student_shuttle_content.setText("0");
				} else {
					setting_student_shuttle_content.setText(FamilyMembersActivity.childReceiverList.size() + "");
				}
				break;

			}
			return false;
		}
	});
	private View headPic;
	private TextView nikeName;
	private TextView gender;
	private TextView birthday;

	public void updataHead(Drawable drawable) {
		headPic.setBackgroundDrawable(drawable);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.baseinfo_new);
		headPic = findViewById(R.id.header_pic_content);
		setting_student_allergy_content = (TextView) findViewById(R.id.setting_student_allergy_content);
		nikeName = (TextView) findViewById(R.id.setting_student_nickname_content);
		gender = (TextView) findViewById(R.id.setting_student_gender_content);
		birthday = (TextView) findViewById(R.id.setting_student_birthday_content);
		name = (TextView) findViewById(R.id.setting_student_name);
		className = (TextView) findViewById(R.id.setting_student_class);
		schoolName = (TextView) findViewById(R.id.setting_student_school);
		serviceName = (TextView) findViewById(R.id.setting_student_service);
		setting_student_shuttle_content = (TextView) findViewById(R.id.setting_student_shuttle_content);
		detector = new GestureDetector(new MyGesture());
		if ("".equals(Student_Info.healthState)) {
			setting_student_allergy_content.setText("未填写");
		} else {
			setting_student_allergy_content.setText(Student_Info.healthState);

		}
		ScrollView settingScr = (ScrollView) findViewById(R.id.setting_scro);

		settingScr.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				detector.onTouchEvent(arg1);
				return false;
			}
		});
		ActivityUtil.baseinfo = this;
		inite();
		initSchool();
		init();
	}

	public void init() {
		if (!"".equals(MyChildrenActivity.serverStatus)) {
			serviceName.setText(MyChildrenActivity.serverStatus);
		}
		DB db = new DB(this);
		SQLiteDatabase sql = db.getReadableDatabase();
		Cursor cursor = sql.query("student_info", null, "uid=?", new String[] { Student_Info.uid }, null, null, null);
		if (cursor == null || cursor.getCount() == 0) {
			handler.sendEmptyMessage(HAVENTDATA);
		} else {
			cursor.moveToFirst();
			headPic.setBackgroundDrawable(new BitmapDrawable(ImageUtil.base64ToBitmap(cursor.getString(cursor
					.getColumnIndex("avatar")))));
			nikeName.setText(cursor.getString(cursor.getColumnIndex("nikename")));
			int tmp = Integer.parseInt(cursor.getString(cursor.getColumnIndex("sex")));
			String foo = "";
			switch (tmp) {
			case 1:
				foo = getResources().getString(R.string.boy);
				break;
			case 2:
				foo = getResources().getString(R.string.girl);
				break;
			}
			gender.setText(foo);
			birthday.setText(cursor.getString(cursor.getColumnIndex("birthday")));
		}
		sql.close();
		db.close();

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				if (FamilyMembersActivity.childReceiverList == null) {
					FamilyMembersActivity.childReceiverList = new ArrayList<ChildReceiver>();
					if (HttpUtil.isNetworkConnected(BaseInfoNewActivity.this)) {
						Result result = HttpUtil.httpGet(BaseInfoNewActivity.this, new Params("childreceiver", null));

						if ("1".equals(result.getCode())) {
							try {
								JSONArray myJson = new JSONArray(result.getContent());
								if (myJson.length() > 0) {
									FamilyMembersActivity.childReceiverList.clear();
								}
								for (int i = 0; i < myJson.length(); i++) {
									JSONObject temp = myJson.getJSONObject(i);
									ChildReceiver tempChild = new ChildReceiver();

									tempChild.setId(temp.getString("id"));
									tempChild.setPid(temp.getString("pid"));
									tempChild.setFilePath(temp.getString("filepath"));
									Bitmap pic = ImageUtil.getImage("http://" + tempChild.getFilePath());
									tempChild.setFileBitmap(pic);
									tempChild.setRelationship(temp.getString("relationship"));
									FamilyMembersActivity.childReceiverList.add(tempChild);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}
				handler.sendEmptyMessage(PEPOLE);
			}
		});
		thread.start();
	}

	public void managePw(View v) {
		Intent intent = new Intent(this, ManagePasswordActivity.class);
		ActivityUtil.main.comeIn(intent);
	}

	public void checkStudentInfo() {
		init();
	}

	public void headerPic(View v) {
		Intent intent = new Intent(this, SelectHeadActivity.class);
		startActivity(intent);
	}

	public void allergy(View v) {
		Intent intent = new Intent(this, HealthActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case CAMERA:
				setBootImage((Bitmap) data.getParcelableExtra("data"));
				break;
			case IMAGESTORAGE:
				setBootImage(data.getData());
				break;
			case IMAGEBACK:
				Bundle extras = data.getExtras();
				if (extras != null) {

					Bitmap photo = extras.getParcelable("data");
					ContentValues values = new ContentValues();
					String foo = ImageUtil.bitmapToBase64(photo);
					Student_Info.strHead = foo;
					values.put("avatar", foo);
					DB db = new DB(BaseInfoNewActivity.this);
					SQLiteDatabase sql = db.getWritableDatabase();

					sql.update("student_info", values, "uid=?", new String[] { Student_Info.uid });
					uploadingAvatar(foo);
					sql.close();
					db.close();
				}
				checkStudentInfo();
				break;
			}
		}
	}

	private void setBootImage(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, IMAGEBACK);
	}

	private void setBootImage(Bitmap data) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");
		intent.putExtra("data", data);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, IMAGEBACK);
	}

	/**
	 * 上传头像
	 */
	public void uploadingAvatar(final String foo) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("fbody", foo);
				Params params = new Params("avatar", map);
				if (HttpUtil.isNetworkConnected(BaseInfoNewActivity.this)) {
					Result result = HttpUtil.httpPost(BaseInfoNewActivity.this, params);
					if (result == null) {
						handler.sendEmptyMessage(UPLONDINGIMAGE);
					} else if ("1".equals(result.getCode())) {
						Message message = handler.obtainMessage(UPLODINGHEADER, foo);
						DB db = new DB(BaseInfoNewActivity.this);
						SQLiteDatabase sql = db.getWritableDatabase();
						ContentValues values = new ContentValues();
						values.put("avatar", foo);
						sql.update("student_info", values, "uid=?", new String[] { Student_Info.uid });
						sql.close();
						db.close();

						handler.sendMessage(message);
					} else {
						handler.sendEmptyMessage(UPLODINGHEADERF);
					}
				} else {
					handler.sendEmptyMessage(UPLONDINGIMAGE);
				}
			}
		});
		thread.start();
	}

	public void nikeName(View v) {
		Intent intent = new Intent(this, NikeNameActivity.class);
		ActivityUtil.startActivity(this, intent);
		// ActivityUtil.main.comeIn(intent);
	}

	public void shuttle(View v) {
		Intent intent = new Intent(this, FamilyMembersActivity.class);
		ActivityUtil.startActivity(this, intent);
	}

	public void gender(View v) {
		if (HttpUtil.isNetworkConnected(BaseInfoNewActivity.this)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			View view = View.inflate(this, R.layout.dialog_gender_cloud, null);
			Button boy = (Button) view.findViewById(R.id.boy);
			Button girl = (Button) view.findViewById(R.id.girl);
			Button cancel = (Button) view.findViewById(R.id.cancel);
			final AlertDialog dialog = builder.create();
			dialog.setView(view, 0, 0, 0, 0);
			dialog.show();
			boy.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("sex", 1 + "");
					modifiGender(map);
					dialog.dismiss();
				}
			});
			girl.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("sex", 2 + "");
					modifiGender(map);
					dialog.dismiss();
				}
			});
			cancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
		} else {
			handler.sendEmptyMessage(UPLODINGGENDERISNTNETWOEK);
		}
	}

	private void modifiGender(final HashMap<String, String> map) {
		handler.sendEmptyMessage(SHOWPROGRESS);
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (HttpUtil.isNetworkConnected(BaseInfoNewActivity.this)) {
					Result result = HttpUtil.httpPost(BaseInfoNewActivity.this, new Params("student", map));
					if (result == null) {
						handler.sendEmptyMessage(UPLODINGGENDERISNTNETWOEKOUTTIME);
					} else if ("1".equals(result.getCode())) {
						DB db = new DB(BaseInfoNewActivity.this);
						SQLiteDatabase sql = db.getWritableDatabase();
						ContentValues values = new ContentValues();
						values.put("sex", map.get("sex"));
						sql.update("student_info", values, "uid=?", new String[] { Student_Info.uid });
						sql.close();
						db.close();
						handler.sendEmptyMessage(CHECKVIEW);
					} else {
						handler.sendEmptyMessage(UPLODINGGENDERISNTNETWOEKFAILE);
					}
				} else {
					handler.sendEmptyMessage(UPLODINGGENDERISNTNETWOEK);
				}
				handler.sendEmptyMessage(DISMISSPROGRESS);

			}
		});
		thread.start();
	}

	public void birthday(View v) {
		String[] tmp = birthday.getText().toString().split("-");
		if (HttpUtil.isNetworkConnected(BaseInfoNewActivity.this)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			View view = View.inflate(this, R.layout.dialog_birthday, null);
			Button set = (Button) view.findViewById(R.id.set);
			Button cancel = (Button) view.findViewById(R.id.cancel);
			final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
			final AlertDialog dialog = builder.create();
			dialog.setView(view, 0, 0, 0, 0);
			dialog.show();
			set.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Date date = new Date(datePicker.getYear() - 1900, datePicker.getMonth(), datePicker.getDayOfMonth());
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String foo = sdf.format(date);
					modifiBirthday(foo);
					dialog.dismiss();
				}
			});
			cancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					// TODO Auto-generated method stub

				}
			});
		} else {
			handler.sendEmptyMessage(NONETWORKBIRTHDAY);
		}
	}

	private void modifiBirthday(final String foo) {
		handler.sendEmptyMessage(SHOWPROGRESS);
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (HttpUtil.isNetworkConnected(BaseInfoNewActivity.this)) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("birthday", foo);
					Result result = HttpUtil.httpPost(BaseInfoNewActivity.this, new Params("student", map));
					if (result == null) {
						handler.sendEmptyMessage(OUTTIMEBIRTHDAY);
					} else if ("1".equals(result.getCode())) {
						DB db = new DB(BaseInfoNewActivity.this);
						SQLiteDatabase sql = db.getWritableDatabase();
						ContentValues values = new ContentValues();
						String str = foo.split("-")[0];

						Date date = new Date();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
						String strToday = sdf.format(date);
						int nage = (Integer.parseInt(strToday) - Integer.parseInt(str)) + 1;
						values.put("birthday", foo);
						values.put("age", nage + "");
						sql.update("student_info", values, "uid=?", new String[] { Student_Info.uid });
						sql.close();
						db.close();
						handler.sendEmptyMessage(CHECKVIEW);
					} else {
						handler.sendEmptyMessage(MODIFIBIRTHDAY);
					}
				} else {
					handler.sendEmptyMessage(NONETWORKBIRTHDAY);
				}
				handler.sendEmptyMessage(DISMISSPROGRESS);
			}
		});
		thread.start();
	}

	public void baseInfo(View v) {
		Intent intent = new Intent(this, BaseInfoActivity.class);
		ActivityUtil.startActivity(ActivityUtil.main, intent);
	}
	
	//Switch kids when user has multiple kids
	public void childrenSwitch(View view) {
		// Intent intent=new Intent(this,ManageChildrenSettingActivity.class);
		Intent intent = new Intent(this, ManageChildrenActivity.class);
		ActivityUtil.main.comeIn(intent);
	}

	private class MyGesture extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (e2 != null && e1 != null) {
				float y = e2.getY() - e1.getY();
				float x = e2.getX() - e1.getX();
				if (Math.abs(x) > 100 && Math.abs(y) < 50) {
					ActivityUtil.main.move();
				}
			}
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}

	/*
	 * private void getSchool() { Thread thread = new Thread(new Runnable() {
	 * 
	 * @Override public void run() {
	 * (HttpUtil.isNetworkConnected(BaseInfoNewActivity.this)) { Params params = new Params("classinfo", null); Result
	 * result = HttpUtil.httpGet(BaseInfoNewActivity.this, params); if (result == null) {
	 * handler.sendEmptyMessage(OUTTIME); } else if ("1".equals(result.getCode())) { DB db = new
	 * DB(BaseInfoNewActivity.this); SQLiteDatabase sql = db.getWritableDatabase(); try { JSONObject object = new
	 * JSONObject(result.getContent()); JSONObject objectContent = new JSONObject(object.getString("classinfo"));
	 * ContentValues values = new ContentValues(); values.put("u_id", Student_Info.uid); values.put("schoolname",
	 * objectContent.getString("schoolname")); Cursor cursor = sql.query("school", null, "u_id=?", new String[] {
	 * Student_Info.uid }, null, null, null); if (cursor == null || cursor.getCount() == 0) { sql.insert("school",
	 * "schoolname", values); } else { sql.update("school", values, "u_id=?", new String[] { Student_Info.uid }); }
	 * cursor.close(); } catch (JSONException e) {  }
	 * sql.close(); db.close(); handler.sendEmptyMessage(INITSCHOOL); } else { handler.sendEmptyMessage(INITSCHOOL); } }
	 * else { handler.sendEmptyMessage(INITSCHOOL); } } }); thread.start(); }
	 */

	private void inite() {
        if(Student_Info.uid != null) {
            DB db = new DB(this);
            SQLiteDatabase sql = db.getReadableDatabase();
            Cursor cur = sql.query("student_info", null, "uid=?", new String[]{Student_Info.uid}, null, null, null);
            if (cur != null) {
                cur.moveToFirst();
                name.setText(cur.getString(cur.getColumnIndexOrThrow("cnname")));
                className.setText(cur.getString(cur.getColumnIndexOrThrow("classname")));
                cur.close();
            }
            sql.close();
            db.close();
        }
	}

	private void initSchool() {
		DB db = new DB(BaseInfoNewActivity.this);
		SQLiteDatabase sql = db.getWritableDatabase();
		Cursor cursor = sql.query("school", null, "u_id=?", new String[] { Student_Info.uid }, null, null, null);
		if (cursor == null || cursor.getCount() == 0) {
			schoolName.setText(getResources().getString(R.string.no_student_school_data));
		} else {
			cursor.moveToFirst();
			String foo = cursor.getString(cursor.getColumnIndex("schoolname"));
			schoolName.setText(foo);
		}
		cursor.close();
		sql.close();
		db.close();
	}
	
	public void close(View v) {
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
}

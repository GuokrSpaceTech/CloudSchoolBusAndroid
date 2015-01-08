package com.Manga.Activity.ClassUpdate;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.Msg.SelectHeadActivity;
import com.Manga.Activity.calender.Util;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.ImageUtil;
import com.Manga.Activity.utils.Push_Info;
import com.Manga.Activity.utils.Student_Info;
import com.Manga.Activity.widget.StudentHeaderView;
import com.cytx.utility.FastJsonTools;
import com.cytx.utility.FileTools;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("SimpleDateFormat")
public class ClassUpdateActivity extends BaseActivity implements IXListViewListener {
	private XListView mListview;
	private RelativeLayout noData;
	private StudentHeaderView studentHeader;
	private TextView studentName;
	private TextView studentClassName;
	private TextView studentAge;
	private SharedPreferences shp;
	private RelativeLayout layout_header;
	private RelativeLayout top;
	ArticleListDto classUpdateList;
	/**
	 * 连接超时
	 */
	private static final int OUTTIME = 0;
	/**
	 * 获取数据失败
	 */
	private static final int GETSTUDENTDATAFAIL = 1;
	/**
	 * 网络没有连通
	 */
	private static final int NETISNOTWORKING = 2;
	/**
	 * 实例化数据
	 */
	private static final int INSTANTIAL_STUDENT = 3;
	/**
	 * 无数据
	 */
	private static final int NODATA = 4;// REQUST_INIT
	/**
	 * 初始化分享
	 */
	private static final int INSTANTIAL_SHARE = 7;
	/**
	 * 获取日志数据失败
	 */
	private static final int GET_SHARE_DATAFAIL = 8;
	/**
	 * 已无最新数据
	 */
	private static final int HAVENODATA = 10;
	/**
	 * 下拉刷新操作
	 */
	private static final int REQUSTFOOTER = 11;
	/**
	 * 无本地数据
	 */
	private static final int NONATIVEDATA = 13;
	public ArticleAdapter articleAdapter;
	private boolean isOnLoadmore = false;
	public static Map<String, List<TagDto>> tagMap = new HashMap<String, List<TagDto>>();
	private Handler mHandler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case OUTTIME:
				Toast.makeText(ClassUpdateActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				ActivityUtil.main.setCancelPRO();
				break;
			case GETSTUDENTDATAFAIL:
				Toast.makeText(ClassUpdateActivity.this, R.string.get_student_data_fail, Toast.LENGTH_SHORT).show();
				break;
			case NETISNOTWORKING:
				Toast.makeText(ClassUpdateActivity.this, R.string.net_is_not_working, Toast.LENGTH_SHORT).show();
				break;
			case INSTANTIAL_STUDENT:
				init_student_info_view();
				getClassUpdateList("", "");
				//Handle push notification
				if (Push_Info.getInstance().getStrPush().equals("menu")
				 || Push_Info.getInstance().getStrPush().equals("attendance")
				 || Push_Info.getInstance().getStrPush().equals("event")
				 || Push_Info.getInstance().getStrPush().equals("notice")
				 || Push_Info.getInstance().getStrPush().equals("geofence")
				 || Push_Info.getInstance().getStrPush().equals("schedule")) {					
					// 获取班级分享信息
					pushLink(Push_Info.getInstance().getStrPush());
				}
				break;
			case NODATA:
				Toast.makeText(ClassUpdateActivity.this, R.string.no_data, Toast.LENGTH_SHORT).show();
				articleAdapter.notifyDataSetChanged();
				break;
			case INSTANTIAL_SHARE:
				update_listview_content();
				break;
			case GET_SHARE_DATAFAIL:
				Toast.makeText(ClassUpdateActivity.this, R.string.get_share_data_fail, Toast.LENGTH_SHORT).show();
				break;
			case HAVENODATA:
				Toast.makeText(ClassUpdateActivity.this, R.string.have_no_new_data, Toast.LENGTH_SHORT).show();
				ActivityUtil.main.disPRO();
				articleAdapter.notifyDataSetChanged();
				break;
			}
			
//			showEmpty();
//			if (articleAdapter != null && articleAdapter.getList() != null) {
//				if (articleAdapter.getList().size() <= 2) {
//					top.setVisibility(View.GONE);
//				} else {
//					top.setVisibility(View.VISIBLE);
//				}
//			}
			return false;
		}
	});

	public void headerPic(View v) {
		Intent intent = new Intent(this, SelectHeadActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.class_share);
		ActivityUtil.share = this;
		shp = getSharedPreferences("count", Context.MODE_PRIVATE);
		mListview = (XListView) findViewById(R.id.share_listview);
		
		noData = (RelativeLayout) findViewById(R.id.no_data);
		top = (RelativeLayout) findViewById(R.id.top);
		mListview.setLongClickable(true);
		mListview.setDivider(null);
		mListview.setXListViewListener(this);
		mListview.setPullLoadEnable(true);
		articleAdapter = new ArticleAdapter(this);
		mListview.setAdapter(articleAdapter);
		
		initHeader();
		
		// 初始化学生的信息
		getStudentInfo();
	}

	private void pushLink(String str) {
		if (str.equals("attendance")) {
			ActivityUtil.main.disPRO();
			ActivityUtil.home.attendance();
		} else if (str.equals("menu")) {
			ActivityUtil.main.disPRO();
			ActivityUtil.home.move(null);
			ActivityUtil.main.foodmenu(ActivityUtil.main.relativeLayoutMenu);
		} else if (str.equals("schedule")) {
			ActivityUtil.main.disPRO();
			ActivityUtil.home.move(null);
			ActivityUtil.main.syllabus(ActivityUtil.main.relativeLayoutSchedule);
		} else if (str.equals("event")) {
			ActivityUtil.home.activity(null);
		} else if (str.equals("notice")) {
			ActivityUtil.home.notification(null);
		} else if (str.equals("geofence"))
			ActivityUtil.home.shuttlebusstops();
	}

	private void initHeader() {
		View firstView = View.inflate(this, R.layout.share_header, null);
		studentHeader = (StudentHeaderView) firstView.findViewById(R.id.share_student_header_bg);
		studentName = (TextView) firstView.findViewById(R.id.share_student_name);
		studentClassName = (TextView) firstView.findViewById(R.id.share_student_school_name);
		studentAge = (TextView) firstView.findViewById(R.id.share_student_age);
		layout_header = (RelativeLayout) firstView.findViewById(R.id.share_header);

		//This is to ensure the pull refresh is below the studnent info header view
		mListview.addHeaderView(firstView);
		mListview.addRefreshHeaderView();
	}

	/**
	 * 获取学生信息
	 */
	public void getStudentInfo() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				if (HttpUtil.isNetworkConnected(ClassUpdateActivity.this)) {
					ActivityUtil.main.showPRO();
					Result result = HttpUtil.httpGet(ClassUpdateActivity.this, new Params("student", null));
					ActivityUtil.main.disPRO();

					if (result == null) {
						mHandler.sendEmptyMessage(OUTTIME);
					} else if ("1".equals(result.getCode())) {
						try {
							JSONObject jsonObject = new JSONObject(result.getContent());
							DB db = new DB(ClassUpdateActivity.this);
							SQLiteDatabase sql = db.getWritableDatabase();
							ContentValues values = new ContentValues();
							Student_Info.healthState = jsonObject.getString("healthstate");
							Student_Info.chunyuisopen = jsonObject.getString("chunyuisopen");
							Student_Info.chunyu_entime = jsonObject.getString("chunyu_endtime");
							values.put("uid", jsonObject.getString("uid"));
							values.put("skinid", jsonObject.getString("skinid"));
							values.put("studentid", jsonObject.getString("studentid"));
							values.put("studentno", jsonObject.getString("studentno"));
							values.put("sex", jsonObject.getString("sex"));
							values.put("birthday", jsonObject.getString("birthday"));
							values.put("cnname", jsonObject.getString("cnname"));
							values.put("enname", jsonObject.getString("enname"));
							values.put("nikename", jsonObject.getString("nikename"));
							values.put("mobile", jsonObject.getString("mobile"));
							values.put("classname", jsonObject.getString("classname"));
							values.put("username", jsonObject.getString("username"));
							values.put("ischeck_mobile", jsonObject.getString("ischeck_mobile"));
							values.put("viptype", jsonObject.getString("viptype"));
							values.put("applynum", jsonObject.getString("applynum"));
							values.put("orderendtime", jsonObject.getString("orderendtime"));
							Bitmap pic = ImageUtil.getImage(jsonObject.getString("avatar"));
							if (pic == null) {// 如果图片获取失败，显示默认图片
								pic = BitmapFactory.decodeResource(getResources(), R.drawable.head_def_bg);
							}
							values.put("avatar", ImageUtil.bitmapToBase64(pic));
							values.put("allow_muti_online", jsonObject.getString("allow_muti_online"));
							values.put("age", jsonObject.getString("age"));
							Cursor cur = sql.query("student_info", null, "uid=?",
									new String[] { jsonObject.getString("uid") }, null, null, null);
							if (cur == null || cur.getCount() == 0) {
								sql.insert("student_info", "enname", values);
							} else {
								sql.update("student_info", values, "uid=?",
										new String[] { jsonObject.getString("uid") });
							}
							Student_Info.uid = jsonObject.getString("uid");
							Student_Info.username = jsonObject.getString("username");
							if (HttpUtil.isNetworkConnected(ClassUpdateActivity.this)) {
								Params params = new Params("classinfo", null);
								ActivityUtil.main.showPRO();
								Result result1 = HttpUtil.httpGet(ClassUpdateActivity.this, params);
								ActivityUtil.main.disPRO();
								if (result1 == null) {

								} else if ("1".equals(result1.getCode())) {
									try {
										JSONObject object = new JSONObject(result1.getContent());
										JSONObject objectContent = new JSONObject(object.getString("classinfo"));
										ContentValues values1 = new ContentValues();
										values1.put("u_id", Student_Info.uid);
										values1.put("schoolname", objectContent.getString("schoolname"));
										Cursor cursor = sql.query("school", null, "u_id=?",
												new String[] { Student_Info.uid }, null, null, null);
										if (cursor == null || cursor.getCount() == 0) {
											sql.insert("school", "schoolname", values1);
										} else {
											sql.update("school", values1, "u_id=?", new String[] { Student_Info.uid });
										}
										cursor.close();
									} catch (JSONException e) {
										e.printStackTrace();
									}
									sql.close();
									db.close();
								} else {
									mHandler.sendEmptyMessage(GETSTUDENTDATAFAIL);
								}
							} else {
								mHandler.sendEmptyMessage(NETISNOTWORKING);
							}
							cur.close();
							sql.close();
							db.close();
							mHandler.sendEmptyMessage(INSTANTIAL_STUDENT);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						mHandler.sendEmptyMessage(GETSTUDENTDATAFAIL);
					}
				} else {
					mHandler.sendEmptyMessage(NETISNOTWORKING);
					mHandler.sendEmptyMessage(INSTANTIAL_STUDENT);
				}
			}
		});
		thread.start();
	}

	/**
	 * 获取班级分享信息
	 * 
	 * @param starttime
	 * @param endtime
	 */
	private void getClassUpdateList(final String starttime, final String endtime) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				if (HttpUtil.isNetworkConnected(ClassUpdateActivity.this)) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("starttime", starttime);
					map.put("endtime", endtime);
					ActivityUtil.main.showPRO();
					Result result = HttpUtil.httpGet(ClassUpdateActivity.this, new Params("article", map));
					ActivityUtil.main.disPRO();
					if (result == null) {
						mHandler.sendEmptyMessage(OUTTIME);
					} else if ("1".equals(result.getCode())) {
							
							String allShareInfo = result.getContent();
							FileTools.save2SDCard(FileTools.getSDcardPath() + "/ClassUpdateInfo", "classUpdate", ".json", allShareInfo);
							
							//If load more, need append the list
							if(isOnLoadmore)
							{
								List<ArticleDto> olderArticleList = FastJsonTools.getObject(allShareInfo, ArticleListDto.class).getArticlelist();
								classUpdateList.getArticlelist().addAll(olderArticleList);
							}
							else
							{
								classUpdateList = FastJsonTools.getObject(allShareInfo, ArticleListDto.class);
							}
							
							if("0".equals(classUpdateList.getCan_comment_action()))
							    Student_Info.likeAble = false;
							else
								Student_Info.likeAble = true;
							
							if("0".equals(classUpdateList.getCan_comment()))
								Student_Info.likeAble = false;
							else
								Student_Info.likeAble = true;
							mHandler.sendEmptyMessage(INSTANTIAL_SHARE);
					} else if ("-2".equals(result.getCode())) {
						mHandler.sendEmptyMessage(HAVENODATA);
					} else {
						mHandler.sendEmptyMessage(GET_SHARE_DATAFAIL);
					}
				} else {
					mHandler.sendEmptyMessage(NETISNOTWORKING);
				}
			}
		});
		thread.start();
	}

	/**
	 * 
	 */
	private void update_listview_content() {
		
		List<ArticleDto> articleList = classUpdateList.getArticlelist();
		
		articleAdapter.setList((ArrayList<ArticleDto>) articleList);
		if(articleList!=null)
		{
		    //Clear the notification badge
		    if (ActivityUtil.home != null) {
			    ActivityUtil.home.CloseStatusShare();
		    }
		    articleAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 初始化学生信息界面
	 */
	public void init_student_info_view() {
		DB db = new DB(this);
		SQLiteDatabase sql = db.getReadableDatabase();
		SharedPreferences sp1 = ClassUpdateActivity.this.getSharedPreferences("LoggingData", Context.MODE_PRIVATE);
		Editor editor = sp1.edit();
		editor.putString("uid", Student_Info.uid);
		editor.commit();
		Cursor cursor = sql.query("student_info", null, "uid=?", new String[] { Student_Info.uid }, null, null, null);
		if (cursor == null || cursor.getCount() == 0) {
			mHandler.sendEmptyMessage(NODATA);
		} else {
			cursor.moveToFirst();
			String str = cursor.getString(cursor.getColumnIndex("birthday")).substring(5,
					cursor.getString(cursor.getColumnIndex("birthday")).length());
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
			String foo = sdf.format(date);
			ImageView imageView = (ImageView) findViewById(R.id.guoshengri);
			if (str.equals(foo)) {
				imageView.setVisibility(View.VISIBLE);
			} else {
				imageView.setVisibility(View.GONE);
			}

			initBg(cursor.getString(cursor.getColumnIndex("skinid")));
			studentName.setText(cursor.getString(cursor.getColumnIndex("nikename")));
			studentAge.setText(cursor.getString(cursor.getColumnIndex("age")) + " "
					+ getResources().getString(R.string.age_field));
			studentClassName.setText(cursor.getString(cursor.getColumnIndex("classname")));
			studentHeader.setStudentHeaderBG(
					ImageUtil.base64ToBitmap(cursor.getString(cursor.getColumnIndex("avatar"))));
		}
		if (cursor != null) {
			cursor.close();
		}
		sql.close();
		db.close();
	}

	public void UpdateHead() {
		DB db = new DB(this);
		SQLiteDatabase sql = db.getReadableDatabase();
		Cursor cursor = sql.query("student_info", null, "uid=?", new String[] { Student_Info.uid }, null, null, null);
		if (cursor == null || cursor.getCount() == 0) {
			mHandler.sendEmptyMessage(NODATA);
		} else {
			cursor.moveToFirst();
			String str = cursor.getString(cursor.getColumnIndex("birthday")).substring(5,
					cursor.getString(cursor.getColumnIndex("birthday")).length());
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
			String foo = sdf.format(date);
			ImageView imageView = (ImageView) findViewById(R.id.guoshengri);
			if (str.equals(foo)) {
				imageView.setVisibility(View.VISIBLE);
			} else {
				imageView.setVisibility(View.GONE);
			}
			studentAge.setText(cursor.getString(cursor.getColumnIndex("age")) + " "
					+ getResources().getString(R.string.age_field));
			studentName.setText(cursor.getString(cursor.getColumnIndex("nikename")));

			studentHeader.setStudentHeaderBG(ImageUtil.toRoundCorner(
					ImageUtil.base64ToBitmap(cursor.getString(cursor.getColumnIndex("avatar"))), 75));
		}
		if (cursor != null) {
			cursor.close();
		}
		sql.close();
		db.close();
	}

	private void initBg(String strTag) {
		if (strTag.equals("-1")) {
			UpdateBackGround();
			return;
		} else {
			int ntag = Integer.parseInt(strTag);
			Drawable d1 = null;
			int id = 0;
			switch (ntag) {
			case 0:
				id = R.drawable.settingback01;
				break;
			case 1:
				id = R.drawable.settingback02;
				break;
			case 2:
				id = R.drawable.settingback03;
				break;
			case 3:
				id = R.drawable.settingback04;
				break;
			case 4:
				id = R.drawable.settingback05;
				break;
			case 5:
				id = R.drawable.settingback06;
				break;
			case 6:
				id = R.drawable.settingback07;
				break;
			case 7:
				id = R.drawable.settingback08;
				break;
			case 8:
				id = R.drawable.settingback09;
				break;
			case 9:
				id = R.drawable.settingback10;
				break;
			case 10:
				id = R.drawable.settingback11;
				break;
			case 11:
				id = R.drawable.settingback12;
				break;
			default:
				break;
			}
			d1 = getResources().getDrawable(id);
			BitmapDrawable bd1 = (BitmapDrawable) d1;
			Bitmap bm1 = bd1.getBitmap();
			if (bm1 != null) {
				ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
				bm1.compress(CompressFormat.PNG, 70, baos1);
				byte[] bytes1 = baos1.toByteArray();
				String s1 = Base64.encodeToString(bytes1, Base64.NO_WRAP);
				shp.edit().putString("cbackground", s1).commit();
				shp.edit().putInt("cbackgroundindex", ntag + 1).commit();

			} else {
				return;
			}

		}
		UpdateBackGround();
	}

	public void UpdateBackGround() {

		String str = shp.getString("cbackground", "");
		if (!"".equals(str)) {
			Drawable d = null;
			try {
				Bitmap bitmap = Util.getBitmap(str);
				d = new BitmapDrawable(bitmap);
				layout_header.setBackgroundDrawable(d);
			} catch (Exception e) {

			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (ActivityUtil.main != null) {
				ActivityUtil.main.move();
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public void top(View view) {
		mListview.setSelection(0);
	}

	private void showEmpty() {
		Toast.makeText(ClassUpdateActivity.this, R.string.no_data, Toast.LENGTH_SHORT).show();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				getClassUpdateList("", "");
				onLoaded();
			}
		}, 2000);
		
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (classUpdateList == null) {
					getClassUpdateList("", "");
				}
			    else if(classUpdateList.getArticlelist() == null) {
			    	getClassUpdateList("", "");
				} else if(classUpdateList.getArticlelist().size() == 0) {
					getClassUpdateList("","");
				} else
				{
					isOnLoadmore = true;
					int lastIndex = classUpdateList.getArticlelist().size() - 1;
					getClassUpdateList(classUpdateList.getArticlelist().get(lastIndex).getPublishtime(),"0");
				}
				onLoaded();
			}
		}, 2000);
	}
	
	private void onLoaded() {
        Date date = new Date();  
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制  
        String lastUpdate = sdformat.format(date);

		mListview.stopRefresh();
		mListview.stopLoadMore();
		mListview.setRefreshTime(lastUpdate);
	}
}

package com.Manga.Activity.ClassUpdate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.ClassUpdate.Model.TagDto;
import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.bigPicture.BigPictureActivity;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.DensityUtil;
import com.Manga.Activity.utils.Student_Info;
import com.Manga.Activity.widget.LisStudentHeaderView;
import com.Manga.Activity.ClassUpdate.Widget.CachedVideoViewLayout;
import com.Manga.Activity.widget.ShareImage;
import com.Manga.Activity.widget.StudentHeaderView;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("SimpleDateFormat")
public class ClassUpdateMainBodyActivity extends BaseActivity {
	private LinearLayout zanList;
	private LinearLayout commentList;
	private String articleid;
	private EditText inputContent;
	private Thread thread1;
	private boolean deleteFile = true;
	private static final int ZANLIST = 0;
	private TextView tv_tip1, tv_tip2, tv_tip3, tv_tip4, tv_tip5, tv_tip6;
	LinearLayout layout_tip_row1, layout_tip_row2, layout_tip;
	List<TagDto> tagList;
	public static String tagDesc;
	/**
	 * 无网获取赞列表
	 */
	private static final int ZANLISTNONET = 1;
	/**
	 * 评论
	 */
	private static final int COMMENT = 2;
	/**
	 * 无网获取评论
	 */
	private static final int COMMENTNONET = 3;
	/**
	 * 网络没有连通
	 */
	private static final int NETISNOTWORKING = 4;
	/**
	 * 赞成功
	 */
	private static final int ZANSUCCESS = 5;
	/**
	 * 取消赞失败
	 */
	private static final int CANCELZANFAIL = 6;
	/**
	 * 评论失败
	 */
	private static final int COMMENTFAIL = 7;
	/**
	 * 评论成功
	 */
	private static final int COMMENTSUESS = 8;
	/**
	 * 删除评论失败
	 */
	private static final int COMMENTDE_FAIL = 9;
	/**
	 * 赞
	 */
	private static final int ZAN = 10;
	/**
	 * 删除赞
	 */
	private static final int ZANDEL = 11;
	/**
	 * 清空赞列表
	 */
	private static final int CLEAR_ZAN_LIST = 12;
	private ProgressDialog progressDialog;

	/**
	 * 进度条
	 */
	private static final int SHOWPROGRESS = 13;
	/**
	 * 取消进度条显示
	 */
	private static final int DISMISSPROGRESS = 14;
	private static final int LUNOUT = 15;
	private static final int NOLIKE = 16;
	private static final int NOCOMMENT = 17;
	private SimpleDateFormat spl = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat toYearSdf = new SimpleDateFormat("MM-dd HH:mm");
	private long toYear;
	private long nowTime;
	private boolean isKeyUp;
	private String havezan;
	private Button like;
	private ScrollView sc;
	private CachedVideoViewLayout myVideo;
	private ProgressBar progress_image;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message mess) {
			switch (mess.what) {
			case NOLIKE:
				zanList.removeAllViews();
				LayoutParams paramsTxt = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				TextView textView = new TextView(ClassUpdateMainBodyActivity.this);
				textView.setLayoutParams(paramsTxt);
				textView.setText(ClassUpdateMainBodyActivity.this.getResources().getString(R.string.nobody_like));
				zanList.addView(textView);
				break;
			case NOCOMMENT:
				commentList.removeAllViews();
				LayoutParams paramsTxt1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				TextView textView1 = new TextView(ClassUpdateMainBodyActivity.this);
				textView1.setLayoutParams(paramsTxt1);
				textView1.setText(ClassUpdateMainBodyActivity.this.getResources().getString(R.string.nobody_lun));
				commentList.addView(textView1);
				break;
			case ZANLIST:

				try {
					zanList.removeAllViews();

					JSONArray array = new JSONArray((String) mess.obj);
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.getJSONObject(i);
						LisStudentHeaderView headerView = new LisStudentHeaderView(ClassUpdateMainBodyActivity.this);
						String str = object.getString("avatar");
						if (str.equals("null")) {
							headerView.setDefH();

						} else {
							headerView.setImageBackgroundDrawable(object.getString("avatar"));
						}
						LayoutParams params_ = new LayoutParams(DensityUtil.dip2px(ClassUpdateMainBodyActivity.this, 3),
								DensityUtil.dip2px(ClassUpdateMainBodyActivity.this, 30));
						LayoutParams params = new LayoutParams(DensityUtil.dip2px(ClassUpdateMainBodyActivity.this, 30),
								DensityUtil.dip2px(ClassUpdateMainBodyActivity.this, 30));
						View view = new View(ClassUpdateMainBodyActivity.this);
						view.setBackgroundColor(Color.WHITE);
						view.setLayoutParams(params_);
						headerView.setLayoutParams(params);
						zanList.addView(headerView);
						zanList.addView(view);
						DB db = new DB(ClassUpdateMainBodyActivity.this);
						SQLiteDatabase sql = db.getWritableDatabase();
						Cursor cur = sql.query("like", null, "u_id=? and actionid=? and articleid=?", new String[] {
								Student_Info.uid, object.getString("actionid"), articleid }, null, null, null);
						ContentValues values = new ContentValues();
						values.put("u_id", Student_Info.uid);
						values.put("actionid", object.getString("actionid"));
						values.put("articleid", articleid);
						values.put("avatar", object.getString("avatar"));
						values.put("addtime", object.getString("addtime"));
						if (cur == null || cur.getCount() == 0) {
							sql.insert("like", "avatar", values);
						} else {
							sql.update("like", values, "u_id=? and actionid=? and articleid=?", new String[] {
									Student_Info.uid, object.getString("actionid"), articleid });
						}
						if (cur != null) {
							cur.close();
						}
						sql.close();
						db.close();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case ZANLISTNONET:
				DB db = new DB(ClassUpdateMainBodyActivity.this);
				SQLiteDatabase sql = db.getReadableDatabase();
				Cursor cur = sql.query("like", null, "u_id=? and articleid=?", new String[] { Student_Info.uid,
						articleid }, null, null, "addtime desc");
				zanList.removeAllViews();
				if (cur == null || cur.getCount() == 0) {

				} else {
					for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
						StudentHeaderView headerView = new StudentHeaderView(ClassUpdateMainBodyActivity.this);
						String str = cur.getString(cur.getColumnIndex("avatar"));
						if (str.equals("null")) {
							headerView.setBackgroundDrawable(getResources().getDrawable(R.drawable.head_def_bg));
						} else {
							headerView.setImageBackgroundDrawable(cur.getString(cur.getColumnIndex("avatar")));
						}
						LayoutParams params = new LayoutParams(DensityUtil.dip2px(ClassUpdateMainBodyActivity.this, 50),
								LayoutParams.FILL_PARENT);
						zanList.addView(headerView, params);
					}
				}
				if (cur != null) {
					cur.close();
				}
				sql.close();
				db.close();
				break;
			case COMMENT:
				try {
					commentList.removeAllViews();
					JSONArray array = new JSONArray((String) mess.obj);
					for (int i = 0; i < array.length(); i++) {
						final JSONObject object = array.getJSONObject(i);
						View item = View.inflate(ClassUpdateMainBodyActivity.this, R.layout.comment_item, null);
						LisStudentHeaderView headerView = (LisStudentHeaderView) item.findViewById(R.id.header);
						TextView title = (TextView) item.findViewById(R.id.title);
						TextView content = (TextView) item.findViewById(R.id.content);
						TextView time = (TextView) item.findViewById(R.id.time);
						String isStudent = object.getString("isstudent");
						String title_ = object.getString("nickname");
						String content_ = object.getString("content");
						String mapAddtime = object.getString("addtime");
						if ("1".equals(isStudent)) {
							title_ = title_ + getResources().getString(R.string.de_paret);
						} else {
							title_ = title_ + getResources().getString(R.string.de_teacher);
						}
						if (object.getString("replynickname") == null
								|| "null".equals(object.getString("replynickname"))) {

						} else {
							content_ = getResources().getString(R.string.huifu) + object.getString("replynickname")
									+ ":" + content_;
						}
						title.setText(title_);
						content.setText(content_);
						if (mapAddtime != null) {
							long foo = Long.parseLong(mapAddtime) * 1000;
							long tmp = nowTime - foo;
							if (foo > toYear) {
								if (tmp < 12 * 60 * 60 * 1000) {
									if (tmp < 60 * 60 * 1000) {
										if (tmp <= 60 * 1000) {
											time.setText("1" + getResources().getString(R.string.minute_befor));
										} else {
											time.setText((tmp) / (60 * 1000)
													+ getResources().getString(R.string.minute_befor));
										}
									} else {
										time.setText(tmp / (60 * 60 * 1000)
												+ getResources().getString(R.string.hour_befor));
									}
								} else {
									time.setText(toYearSdf.format(new Date(foo)));
								}
							} else {
								time.setText(spl.format(new Date(foo)));
							}
						}
						headerView.setImageBackgroundDrawable(object.getString("avatar"));
						item.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								try {
									if (HttpUtil.isNetworkConnected(ClassUpdateMainBodyActivity.this)) {
										AlertDialog.Builder builder = new AlertDialog.Builder(
												ClassUpdateMainBodyActivity.this);
										View view = View.inflate(ClassUpdateMainBodyActivity.this,
												R.layout.dialog_reply, null);
										Button reply = (Button) view.findViewById(R.id.reply);
										Button del = (Button) view.findViewById(R.id.delete);
										Button cancel = (Button) view.findViewById(R.id.cancel);
										final AlertDialog dialog = builder.create();
										if (!Student_Info.uid.equals(object.getString("adduserid"))) {
											del.setVisibility(View.GONE);
										} else {
											del.setVisibility(View.VISIBLE);
										}
										if (Student_Info.uid.equals(object.getString("adduserid"))) {
											reply.setVisibility(View.GONE);
										} else {
											reply.setVisibility(View.VISIBLE);
										}
										dialog.setView(view, 0, 0, 0, 0);
										dialog.show();
										reply.setOnClickListener(new View.OnClickListener() {

											@Override
											public void onClick(View arg0) {
												dialog.dismiss();
												Intent intent = new Intent(ClassUpdateMainBodyActivity.this,
														ReplyActivity.class);
												try {
													intent.putExtra("commentid", object.getString("commentid"));
													intent.putExtra("articleid", articleid);
												} catch (JSONException e) {
													e.printStackTrace();
												}
												ActivityUtil.startActivity(ClassUpdateMainBodyActivity.this, intent);
											}
										});
										del.setOnClickListener(new View.OnClickListener() {

											@Override
											public void onClick(View v) {
												dialog.dismiss();
												try {
													deComment(object.getString("commentid"));
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}
										});
										cancel.setOnClickListener(new View.OnClickListener() {

											@Override
											public void onClick(View v) {
												dialog.dismiss();

											}
										});
									} else {
										handler.sendEmptyMessage(NETISNOTWORKING);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
						LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
						commentList.addView(item, params);
						DB db_comment = new DB(ClassUpdateMainBodyActivity.this);
						SQLiteDatabase sql_comment = db_comment.getWritableDatabase();
						Cursor cur_comment = sql_comment.query("comment", null,
								"u_id=? and commentid=? and articleid=?",
								new String[] { Student_Info.uid, object.getString("commentid"), articleid }, null,
								null, null);
						ContentValues values = new ContentValues();
						values.put("u_id", Student_Info.uid);
						values.put("commentid", object.getString("commentid"));
						values.put("articleid", articleid);
						values.put("content", object.getString("content"));
						values.put("isstudent", object.getString("isstudent"));
						values.put("nickname", object.getString("nickname"));
						values.put("replynickname", object.getString("replynickname"));
						values.put("avatar", object.getString("avatar"));
						values.put("addtime", object.getString("addtime"));
						values.put("candelete", object.getString("candelete"));
						values.put("adduserid", object.getString("adduserid"));
						if (cur_comment == null || cur_comment.getCount() == 0) {
							sql_comment.insert("comment", "avatar", values);
						} else {
							sql_comment.update("comment", values, "u_id=? and commentid=? and articleid=?",
									new String[] { Student_Info.uid, object.getString("commentid"), articleid });
						}
						if (cur_comment != null) {
							cur_comment.close();
						}
						sql_comment.close();
						db_comment.close();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case COMMENTNONET:
				DB dbCommentNoNet = new DB(ClassUpdateMainBodyActivity.this);
				SQLiteDatabase sqlCommentNoNet = dbCommentNoNet.getReadableDatabase();
				Cursor curCommentNoNet = sqlCommentNoNet.query("comment", null, "u_id=? and articleid=?", new String[] {
						Student_Info.uid, articleid }, null, null, "addtime desc");
				if (curCommentNoNet == null || curCommentNoNet.getCount() == 0) {

				} else {
					for (curCommentNoNet.moveToFirst(); !curCommentNoNet.isAfterLast(); curCommentNoNet.moveToNext()) {
						View item = View.inflate(ClassUpdateMainBodyActivity.this, R.layout.comment_item, null);
						LisStudentHeaderView headerView = (LisStudentHeaderView) item.findViewById(R.id.header);
						TextView title = (TextView) item.findViewById(R.id.title);
						TextView content = (TextView) item.findViewById(R.id.content);
						TextView time = (TextView) item.findViewById(R.id.time);
						String isStudent = curCommentNoNet.getString(curCommentNoNet.getColumnIndex("isstudent"));
						String title_ = curCommentNoNet.getString(curCommentNoNet.getColumnIndex("nickname"));
						String content_ = curCommentNoNet.getString(curCommentNoNet.getColumnIndex("content"));
						String mapAddtime = curCommentNoNet.getString(curCommentNoNet.getColumnIndex("addtime"));
						String avatar = curCommentNoNet.getString(curCommentNoNet.getColumnIndex("avatar"));
						String replynickname = curCommentNoNet.getString(curCommentNoNet
								.getColumnIndex("replynickname"));
						title.setText(title_);
						if ("1".equals(isStudent)) {
							title_ = title_ + getResources().getString(R.string.de_paret);
						} else {
							title_ = title_ + getResources().getString(R.string.de_teacher);
						}
						if (replynickname == null || "null".equals(replynickname)) {

						} else {
							title_ = title_ + getResources().getString(R.string.huifu) + replynickname;
						}
						content.setText(content_);
						if (mapAddtime != null) {
							long foo = Long.parseLong(mapAddtime) * 1000;
							long tmp = nowTime - foo;
							if (foo > toYear) {
								if (tmp < 12 * 60 * 60 * 1000) {
									if (tmp < 60 * 60 * 1000) {
										if (tmp <= 60 * 1000) {
											time.setText("1" + getResources().getString(R.string.minute_befor));
										} else {
											time.setText((tmp) / (60 * 1000)
													+ getResources().getString(R.string.minute_befor));
										}
									} else {
										time.setText(tmp / (60 * 60 * 1000)
												+ getResources().getString(R.string.hour_befor));
									}
								} else {
									time.setText(toYearSdf.format(new Date(foo)));
								}
							} else {
								time.setText(spl.format(new Date(foo)));
							}
						}
						headerView.setImageBackgroundDrawable(avatar);
						LayoutParams params = new LayoutParams(DensityUtil.dip2px(ClassUpdateMainBodyActivity.this, 30),
								DensityUtil.dip2px(ClassUpdateMainBodyActivity.this, 30));
						commentList.addView(item, params);
					}
				}
				break;
			case NETISNOTWORKING:
				handler.sendEmptyMessage(DISMISSPROGRESS);
				Toast.makeText(ClassUpdateMainBodyActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				break;
			case LUNOUT:
				Toast.makeText(ClassUpdateMainBodyActivity.this, R.string.lun_out, Toast.LENGTH_SHORT).show();
				break;
			case ZANSUCCESS:
				initLikeList();
				break;
			case SHOWPROGRESS:
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(ClassUpdateMainBodyActivity.this);
					progressDialog.setMessage(getResources().getString(R.string.init_view));
					progressDialog.setCancelable(true);
				}
				progressDialog.show();
				break;
			case DISMISSPROGRESS:
				progressDialog.dismiss();
				break;
			case ZAN:
				like.setBackgroundDrawable(getResources().getDrawable(R.drawable.class_share_main_body_like_selector));
				DB db_zan = new DB(ClassUpdateMainBodyActivity.this);
				SQLiteDatabase sql_zan = db_zan.getWritableDatabase();
				Cursor cur_zan = sql_zan.query("article", null, "u_id=? and articleid=?", new String[] {
						Student_Info.uid, articleid }, null, null, null);
				cur_zan.moveToFirst();
				int tmp_zan = cur_zan.getInt(cur_zan.getColumnIndex("upnum"));
				cur_zan.close();
				tmp_zan--;
				ContentValues values_zan = new ContentValues();
				values_zan.put("upnum", tmp_zan);
				sql_zan.update("article", values_zan, "u_id=? and articleid=?", new String[] { Student_Info.uid,
						articleid });
				sql_zan.close();
				db_zan.close();
				//ArrayList<Map<String, String>> list_zan = ActivityUtil.share.articleAdapter.getList();
//				for (int i = 0; i < list_zan.size(); i++) {
//					Map<String, String> map = list_zan.get(i);
//					if (map.get("articleid").equals(articleid)) {
//						String tmps = map.get("upnum");
//						map.put("upnum", Integer.parseInt(tmps) + 1 + "");
//						map.put("havezan", havezan);
//						ActivityUtil.share.articleAdapter.notifyDataSetChanged();
//						break;
//					}
//				}
				initLikeList();
				break;
			case ZANDEL:
				like.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.class_share_main_body_like_del_selector));
				DB db_del = new DB(ClassUpdateMainBodyActivity.this);
				SQLiteDatabase sql_del = db_del.getWritableDatabase();
				Cursor cur_del = sql_del.query("article", null, "u_id=? and articleid=?", new String[] {
						Student_Info.uid, articleid }, null, null, null);
				cur_del.moveToFirst();
				int tmp_del = cur_del.getInt(cur_del.getColumnIndex("upnum"));
				cur_del.close();
				tmp_del--;
				ContentValues values_del = new ContentValues();
				values_del.put("upnum", tmp_del);
				sql_del.update("article", values_del, "u_id=? and articleid=?", new String[] { Student_Info.uid,
						articleid });
				sql_del.close();
				db_del.close();
//				ArrayList<Map<String, String>> list_del = ActivityUtil.share.articleAdapter.getList();
//				for (int i = 0; i < list_del.size(); i++) {
//					Map<String, String> map = list_del.get(i);
//					if (map.get("articleid").equals(articleid)) {
//						String tmps = map.get("upnum");
//						map.put("upnum", Integer.parseInt(tmps) - 1 + "");
//						map.put("havezan", "0");
//						ActivityUtil.share.articleAdapter.notifyDataSetChanged();
//						break;
//					}
//				}
				initLikeList();
				break;
			case CANCELZANFAIL:
				Toast.makeText(ClassUpdateMainBodyActivity.this, R.string.cancel_zan_fail, Toast.LENGTH_SHORT).show();
				break;
			case COMMENTFAIL:
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(inputContent.getWindowToken(), 0);
				inputContent.setText("");
				handler.sendEmptyMessage(DISMISSPROGRESS);
				Toast.makeText(ClassUpdateMainBodyActivity.this, R.string.comment_fail, Toast.LENGTH_SHORT).show();
				break;
			case COMMENTSUESS:
				InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm1.hideSoftInputFromWindow(inputContent.getWindowToken(), 0);
				initCommentList();
				inputContent.setText("");
				sc.fullScroll(ScrollView.FOCUS_DOWN);
				DB db_ = new DB(ClassUpdateMainBodyActivity.this);
				SQLiteDatabase sql_ = db_.getWritableDatabase();
				Cursor cur_ = sql_.query("article", null, "u_id=? and articleid=?", new String[] { Student_Info.uid,
						articleid }, null, null, null);
				cur_.moveToFirst();
				int tmp = cur_.getInt(cur_.getColumnIndex("commentnum"));
				cur_.close();
				tmp++;
				ContentValues values = new ContentValues();
				values.put("commentnum", tmp);
				sql_.update("article", values, "u_id=? and articleid=?", new String[] { Student_Info.uid, articleid });
				sql_.close();
				db_.close();
//				ArrayList<Map<String, String>> list = ActivityUtil.share.articleAdapter.getList();
//				for (int i = 0; i < list.size(); i++) {
//					Map<String, String> map = list.get(i);
//					if (map.get("articleid").equals(articleid)) {
//						String tmps = map.get("commentnum");
//						map.put("commentnum", Integer.parseInt(tmps) + 1 + "");
//						ActivityUtil.share.articleAdapter.notifyDataSetChanged();
//						break;
//					}
//				}
				handler.sendEmptyMessage(DISMISSPROGRESS);
				break;
			case COMMENTDE_FAIL:
				Toast.makeText(ClassUpdateMainBodyActivity.this, R.string.comment_de_fail, Toast.LENGTH_SHORT).show();
				break;
			case CLEAR_ZAN_LIST:
				zanList.removeAllViews();
				break;
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.class_share_main_body);
		sc = (ScrollView) findViewById(R.id.sc);
		progress_image = (ProgressBar) findViewById(R.id.progress_image);
		like = (Button) findViewById(R.id.like);
		TextView title = (TextView) findViewById(R.id.titl_e);
		TextView content = (TextView) findViewById(R.id.content);
		myVideo = (CachedVideoViewLayout) findViewById(R.id.myVideo);
		TextView time = (TextView) findViewById(R.id.time);
		inputContent = (EditText) findViewById(R.id.input_content);
		ShareImage image01 = (ShareImage) findViewById(R.id.share_image_one);
		ShareImage image02 = (ShareImage) findViewById(R.id.share_image_two);
		ShareImage image03 = (ShareImage) findViewById(R.id.share_image_three);
		ShareImage image04 = (ShareImage) findViewById(R.id.share_image_four);
		ShareImage image05 = (ShareImage) findViewById(R.id.share_image_five);
		ShareImage image06 = (ShareImage) findViewById(R.id.share_image_six);
		ShareImage image07 = (ShareImage) findViewById(R.id.share_image_seven);
		ShareImage image08 = (ShareImage) findViewById(R.id.share_image_eight);
		ShareImage image09 = (ShareImage) findViewById(R.id.share_image_nine);
		tv_tip1 = (TextView) findViewById(R.id.tv_tip1);
		tv_tip2 = (TextView) findViewById(R.id.tv_tip2);
		tv_tip3 = (TextView) findViewById(R.id.tv_tip3);
		tv_tip4 = (TextView) findViewById(R.id.tv_tip4);
		tv_tip5 = (TextView) findViewById(R.id.tv_tip5);
		tv_tip6 = (TextView) findViewById(R.id.tv_tip6);
		layout_tip = (LinearLayout) findViewById(R.id.layout_tip);
		layout_tip_row1 = (LinearLayout) findViewById(R.id.layout_tip_row1);
		layout_tip_row2 = (LinearLayout) findViewById(R.id.layout_tip_row2);

		/*
		 * image01.setBackgroundResource(R.drawable.imageplaceholder);
		 * image02.setBackgroundResource(R.drawable.imageplaceholder);
		 * image03.setBackgroundResource(R.drawable.imageplaceholder);
		 * image04.setBackgroundResource(R.drawable.imageplaceholder);
		 * image05.setBackgroundResource(R.drawable.imageplaceholder);
		 * image06.setBackgroundResource(R.drawable.imageplaceholder);
		 * image07.setBackgroundResource(R.drawable.imageplaceholder);
		 * image08.setBackgroundResource(R.drawable.imageplaceholder);
		 * image09.setBackgroundResource(R.drawable.imageplaceholder);
		 */
		zanList = (LinearLayout) findViewById(R.id.zan_list);
		commentList = (LinearLayout) findViewById(R.id.comment_list);
		Intent intent = getIntent();
		articleid = intent.getStringExtra("articleid");
		final String titleValue = intent.getStringExtra("title");
		final String contentValue = intent.getStringExtra("content");
		final String fext = intent.getStringExtra("fext");
		String showTime = intent.getStringExtra("showTime");
		final String plist = intent.getStringExtra("plist");
		havezan = intent.getStringExtra("havezan");
		if (Integer.parseInt(havezan) > 0) {
			like.setBackgroundDrawable(getResources().getDrawable(R.drawable.class_share_main_body_like_selector));
		} else {
			like.setBackgroundDrawable(getResources().getDrawable(R.drawable.class_share_main_body_like_del_selector));
		}
		title.setText(titleValue);
		if ("".equals(titleValue)) {
			title.setVisibility(View.GONE);
		}
		/**
		 * 2014-08-13 dido update
		 * 
		 * 文字描述区 显示全部的文字内容，当无文字描述时显示拍摄时间---拍摄于2014-01-01 11:11
		 */
		if ("".equals(contentValue)) {
			content.setText("拍摄于 " + showTime);
		} else {
			content.setText(contentValue);
		}
		time.setText(showTime);
		tagList = ActivityUtil.share.tagMap.get(articleid);
		if (tagList == null || tagList.size() == 0) {
			layout_tip.setVisibility(View.GONE);
		} else {
			if (tagList.size() <= 3) {
				layout_tip_row1.setVisibility(View.VISIBLE);
				layout_tip_row2.setVisibility(View.GONE);
				if (tagList.size() == 1) {
					tv_tip2.setVisibility(View.GONE);
					tv_tip3.setVisibility(View.GONE);
					tv_tip1.setVisibility(View.VISIBLE);
					tv_tip1.setText(tagList.get(0).getTagName());
					tv_tip1.setOnClickListener(tagListener);
				} else if (tagList.size() == 2) {
					tv_tip2.setVisibility(View.VISIBLE);
					tv_tip3.setVisibility(View.GONE);
					tv_tip1.setVisibility(View.VISIBLE);
					tv_tip1.setText(tagList.get(0).getTagName());
					tv_tip2.setText(tagList.get(1).getTagName());
					tv_tip1.setOnClickListener(tagListener);
					tv_tip2.setOnClickListener(tagListener);
				} else if (tagList.size() == 3) {
					tv_tip2.setVisibility(View.VISIBLE);
					tv_tip3.setVisibility(View.VISIBLE);
					tv_tip1.setVisibility(View.VISIBLE);
					tv_tip1.setText(tagList.get(0).getTagName());
					tv_tip2.setText(tagList.get(1).getTagName());
					tv_tip3.setText(tagList.get(2).getTagName());
					tv_tip1.setOnClickListener(tagListener);
					tv_tip2.setOnClickListener(tagListener);
					tv_tip3.setOnClickListener(tagListener);
				}
			} else {
				layout_tip_row1.setVisibility(View.VISIBLE);
				layout_tip_row2.setVisibility(View.VISIBLE);
				if (tagList.size() == 4) {
					tv_tip5.setVisibility(View.GONE);
					tv_tip6.setVisibility(View.GONE);
					tv_tip4.setVisibility(View.VISIBLE);
					tv_tip1.setText(tagList.get(0).getTagName());
					tv_tip2.setText(tagList.get(1).getTagName());
					tv_tip3.setText(tagList.get(2).getTagName());
					tv_tip4.setText(tagList.get(3).getTagName());
					tv_tip1.setOnClickListener(tagListener);
					tv_tip2.setOnClickListener(tagListener);
					tv_tip3.setOnClickListener(tagListener);
					tv_tip4.setOnClickListener(tagListener);

				} else if (tagList.size() == 5) {
					tv_tip5.setVisibility(View.VISIBLE);
					tv_tip6.setVisibility(View.GONE);
					tv_tip4.setVisibility(View.VISIBLE);
					tv_tip1.setText(tagList.get(0).getTagName());
					tv_tip2.setText(tagList.get(1).getTagName());
					tv_tip3.setText(tagList.get(2).getTagName());
					tv_tip4.setText(tagList.get(3).getTagName());
					tv_tip5.setText(tagList.get(4).getTagName());
					tv_tip1.setOnClickListener(tagListener);
					tv_tip2.setOnClickListener(tagListener);
					tv_tip3.setOnClickListener(tagListener);
					tv_tip4.setOnClickListener(tagListener);
					tv_tip5.setOnClickListener(tagListener);

				} else if (tagList.size() == 6) {
					tv_tip5.setVisibility(View.VISIBLE);
					tv_tip6.setVisibility(View.VISIBLE);
					tv_tip4.setVisibility(View.VISIBLE);
					tv_tip1.setText(tagList.get(0).getTagName());
					tv_tip2.setText(tagList.get(1).getTagName());
					tv_tip3.setText(tagList.get(2).getTagName());
					tv_tip4.setText(tagList.get(3).getTagName());
					tv_tip5.setText(tagList.get(4).getTagName());
					tv_tip6.setText(tagList.get(5).getTagName());
					tv_tip1.setOnClickListener(tagListener);
					tv_tip2.setOnClickListener(tagListener);
					tv_tip3.setOnClickListener(tagListener);
					tv_tip4.setOnClickListener(tagListener);
					tv_tip5.setOnClickListener(tagListener);
					tv_tip6.setOnClickListener(tagListener);

				}

			}
		}
		ActivityUtil.shareMain = this;
		try {
			Date date = new Date();
			SimpleDateFormat foo = new SimpleDateFormat("yyyy");
			Date tmp = foo.parse(spl.format(date).split("-")[0]);
			toYear = tmp.getTime();
			nowTime = System.currentTimeMillis();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		initLikeList();
		initCommentList();
		if (Student_Info.commentAble) {// 评论控制

		} else {
			inputContent.setEnabled(false);
		}
		ResizeRelativeLayout layout = (ResizeRelativeLayout) findViewById(R.id.relativeLayoutall);
		layout.setOnResizeListener(new ResizeRelativeLayout.OnResizeListener() {
			public void OnResize(int w, int h, int oldw, int oldh) {
				if (h < oldh) {
					isKeyUp = true;
					like.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.class_share_main_body_comment_finish_selector));
				} else {
					isKeyUp = false;
					if (Integer.parseInt(havezan) > 0) {
						like.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.class_share_main_body_like_selector));
					} else {
						like.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.class_share_main_body_like_del_selector));
					}
				}
			}
		});
		if ("mp4".equals(fext)) {
			myVideo.setVisibility(View.VISIBLE);
			if (plist.equals("")) {

			} else {
				//myVideo.getImage().setImageBackgroundDrawable(plist + ".jpg");
				myVideo.loading(plist);
			}
		} else {
			if (plist.equals("")) {

			} else if (plist.indexOf(",") == -1) {
				// image01.setImageBackgroundDrawable(plist);
				image01.setImageBackgroundDrawable(plist, progress_image);
				image01.setVisibility(View.VISIBLE);
				RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) image01
						.getLayoutParams();
				params.height = DensityUtil.dip2px(this, 250);
				params.width = DensityUtil.dip2px(this, 250);
				image01.setLayoutParams(params);
				image01.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(ClassUpdateMainBodyActivity.this, BigPictureActivity.class);
						intent.putExtra("image", new String[] { plist });
						intent.putExtra("position", 0);
						intent.putExtra("title", titleValue);
						intent.putExtra("content", contentValue);
						ActivityUtil.startActivity(ActivityUtil.share, intent);
					}
				});
			} else {
				final String[] tmp = plist.split(",");
				for (int i = 0; i < tmp.length; i++) {
					switch (i) {
					case 0:
						image01.setImageBackgroundDrawable(tmp[i]);
						image01.setVisibility(View.VISIBLE);
						image01.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent(ClassUpdateMainBodyActivity.this, BigPictureActivity.class);
								intent.putExtra("image", tmp);
								intent.putExtra("position", 0);
								intent.putExtra("title", titleValue);
								intent.putExtra("content", contentValue);
								ActivityUtil.startActivity(ActivityUtil.share, intent);
							}
						});
						break;
					case 1:
						image02.setImageBackgroundDrawable(tmp[i]);
						image02.setVisibility(View.VISIBLE);
						image02.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent(ClassUpdateMainBodyActivity.this, BigPictureActivity.class);
								intent.putExtra("image", tmp);
								intent.putExtra("position", 1);
								intent.putExtra("title", titleValue);
								intent.putExtra("content", contentValue);
								ActivityUtil.startActivity(ActivityUtil.share, intent);
							}
						});
						break;
					case 2:
						image03.setImageBackgroundDrawable(tmp[i]);
						image03.setVisibility(View.VISIBLE);
						image03.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent(ClassUpdateMainBodyActivity.this, BigPictureActivity.class);
								intent.putExtra("image", tmp);
								intent.putExtra("position", 2);
								intent.putExtra("title", titleValue);
								intent.putExtra("content", contentValue);
								ActivityUtil.startActivity(ActivityUtil.share, intent);
							}
						});
						break;
					case 3:
						image04.setImageBackgroundDrawable(tmp[i]);
						image04.setVisibility(View.VISIBLE);
						image04.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent(ClassUpdateMainBodyActivity.this, BigPictureActivity.class);
								intent.putExtra("image", tmp);
								intent.putExtra("position", 3);
								intent.putExtra("title", titleValue);
								intent.putExtra("content", contentValue);
								ActivityUtil.startActivity(ActivityUtil.share, intent);
							}
						});
						break;
					case 4:
						image05.setImageBackgroundDrawable(tmp[i]);
						image05.setVisibility(View.VISIBLE);
						image05.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent(ClassUpdateMainBodyActivity.this, BigPictureActivity.class);
								intent.putExtra("image", tmp);
								intent.putExtra("position", 4);
								intent.putExtra("title", titleValue);
								intent.putExtra("content", contentValue);
								ActivityUtil.startActivity(ActivityUtil.share, intent);
							}
						});
						break;
					case 5:
						image06.setImageBackgroundDrawable(tmp[i]);
						image06.setVisibility(View.VISIBLE);
						image06.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent(ClassUpdateMainBodyActivity.this, BigPictureActivity.class);
								intent.putExtra("image", tmp);
								intent.putExtra("position", 5);
								intent.putExtra("title", titleValue);
								intent.putExtra("content", contentValue);
								ActivityUtil.startActivity(ActivityUtil.share, intent);
							}
						});
						break;
					case 6:
						image07.setImageBackgroundDrawable(tmp[i]);
						image07.setVisibility(View.VISIBLE);
						image07.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent(ClassUpdateMainBodyActivity.this, BigPictureActivity.class);
								intent.putExtra("image", tmp);
								intent.putExtra("position", 6);
								intent.putExtra("title", titleValue);
								intent.putExtra("content", contentValue);
								ActivityUtil.startActivity(ActivityUtil.share, intent);
							}
						});
						break;
					case 7:
						image08.setImageBackgroundDrawable(tmp[i]);
						image08.setVisibility(View.VISIBLE);
						image08.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent(ClassUpdateMainBodyActivity.this, BigPictureActivity.class);
								intent.putExtra("image", tmp);
								intent.putExtra("position", 7);
								intent.putExtra("title", titleValue);
								intent.putExtra("content", contentValue);
								ActivityUtil.startActivity(ActivityUtil.share, intent);
							}
						});
						break;
					case 8:
						image09.setImageBackgroundDrawable(tmp[i]);
						image09.setVisibility(View.VISIBLE);
						image09.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent(ClassUpdateMainBodyActivity.this, BigPictureActivity.class);
								intent.putExtra("image", tmp);
								intent.putExtra("position", 8);
								intent.putExtra("title", titleValue);
								intent.putExtra("content", contentValue);
								ActivityUtil.startActivity(ActivityUtil.share, intent);
							}
						});
						break;
					}
				}
			}
		}
		myVideo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myVideo.click();
			}
		});
		myVideo.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				View view = View.inflate(ClassUpdateMainBodyActivity.this, R.layout.londing_mp4, null);
				Button cancel = (Button) view.findViewById(R.id.cancel);
				Button relog = (Button) view.findViewById(R.id.relog);
				final PopupWindow pw = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
						false);
				pw.setBackgroundDrawable(new BitmapDrawable());
				pw.setOutsideTouchable(true);
				pw.setFocusable(true);
				pw.setAnimationStyle(R.style.popwin_anim_style);
				cancel.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						pw.dismiss();
					}
				});
				relog.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						deleteFile = false;
						pw.dismiss();
						Toast.makeText(ClassUpdateMainBodyActivity.this, R.string.xia_zai_wan_cheng, Toast.LENGTH_SHORT)
								.show();
					}
				});
				pw.showAsDropDown(v, 0, -200);
				return false;
			}
		});
	}

	public void close(View v) {
		ActivityUtil.close(this);
	}

	/**
	 * 初始化赞列表
	 */
	public void initLikeList() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				HashMap<String, String> maps = new HashMap<String, String>();
				maps.put("itemtype", "article");
				maps.put("iszan", "1");
				maps.put("itemid", articleid);
				if (HttpUtil.isNetworkConnected(ClassUpdateMainBodyActivity.this)) {
					Result result = HttpUtil.httpGet(ClassUpdateMainBodyActivity.this, new Params("comment", maps));
					if (result == null) {
					} else if ("1".equals(result.getCode())) {
						handler.sendMessage(handler.obtainMessage(ZANLIST, result.getContent()));
					} else if ("-3".equals(result.getCode())) {
						handler.sendEmptyMessage(NOLIKE);
					} else {
						Log.v("获取头像失败", "获取头像失败……");
					}
				} else {
					handler.sendEmptyMessage(ZANLISTNONET);
				}
			}
		});
		thread.start();
	}

	/**
	 * 初始化评论列表
	 */
	public void initCommentList() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				HashMap<String, String> maps = new HashMap<String, String>();
				maps.put("itemtype", "article");
				maps.put("iszan", "0");
				maps.put("itemid", articleid);
				Result result = HttpUtil.httpGet(ClassUpdateMainBodyActivity.this, new Params("comment", maps));
				if (result == null) {

				} else if ("1".equals(result.getCode())) {
					handler.sendMessage(handler.obtainMessage(COMMENT, result.getContent()));
				} else if ("-3".equals(result.getCode())) {
					handler.sendEmptyMessage(NOCOMMENT);
				}
			}
		});
		thread.start();
	}

	OnClickListener tagListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_tip1:
				tagDesc = tagList.get(0).getTagnamedesc();
				break;
			case R.id.tv_tip2:
				tagDesc = tagList.get(1).getTagnamedesc();
				break;
			case R.id.tv_tip3:
				tagDesc = tagList.get(2).getTagnamedesc();
				break;
			case R.id.tv_tip4:
				tagDesc = tagList.get(3).getTagnamedesc();
				break;
			case R.id.tv_tip5:
				tagDesc = tagList.get(4).getTagnamedesc();
				break;
			case R.id.tv_tip6:
				tagDesc = tagList.get(5).getTagnamedesc();
				break;
			}
			Intent intent = new Intent(ActivityUtil.share, TagDialogActivity.class);
			startActivity(intent);
		}
	};

	public void like(View v) {
		if (!isKeyUp) {
			// 赞操作
			if (HttpUtil.isNetworkConnected(ClassUpdateMainBodyActivity.this)) {
				// 有网操作
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						if (Integer.parseInt(havezan) > 0) {
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("key", havezan);
							map.put("type", "comment_action");
							Params param = new Params("delete", map);
							Result result = HttpUtil.httpPost(ClassUpdateMainBodyActivity.this, param);
							if (result == null) {
								handler.sendEmptyMessage(NETISNOTWORKING);
							} else if ("1".equals(result.getCode())) {
								handler.sendEmptyMessage(ZANDEL);
								havezan = "0";
							} else {
								handler.sendEmptyMessage(CANCELZANFAIL);
							}
						} else {
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("itemid", articleid);
							map.put("isup", 1 + "");
							map.put("itemtype", "article");
							Params param = new Params("comment", map);
							Result result = HttpUtil.httpPost(ClassUpdateMainBodyActivity.this, param);
							if (result == null) {
								handler.sendEmptyMessage(NETISNOTWORKING);
							} else if ("1".equals(result.getCode())) {
								DB db = new DB(ClassUpdateMainBodyActivity.this);
								SQLiteDatabase sql = db.getWritableDatabase();
								ContentValues values = new ContentValues();
								values.put("havezan", result.getContent());
								havezan = result.getContent();
								sql.update("article", values, "u_id=? and articleid=?", new String[] {
										Student_Info.uid, articleid });
								sql.close();
								db.close();
								handler.sendEmptyMessage(ZAN);
							} else {
								// handler.sendEmptyMessage(ZANFAIL);
							}
						}
					}
				});
				thread.start();
			} else {
				// 无网操作
				handler.sendEmptyMessage(NETISNOTWORKING);
			}
		} else {
			// 评论操作
			if (inputContent.getText().toString().length() == 0) {
				return;
			}
			if (!checkLength(inputContent.getText().toString())) {
				handler.sendEmptyMessage(LUNOUT);
				return;
			}
			handler.sendEmptyMessage(SHOWPROGRESS);
			if (HttpUtil.isNetworkConnected(ClassUpdateMainBodyActivity.this)) {
				thread1 = new Thread(new Runnable() {

					@Override
					public void run() {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("itemid", articleid);
						map.put("content", inputContent.getText().toString());
						map.put("itemtype", "article");
						Params param = new Params("comment", map);
						Result result = HttpUtil.httpPost(ClassUpdateMainBodyActivity.this, param);
						if ("1".equals(result.getCode())) {
							handler.sendEmptyMessage(COMMENTSUESS);
						} else {
							handler.sendEmptyMessage(COMMENTFAIL);
						}
					}
				});
				if (!thread1.isAlive()) {
					thread1.start();
				}
			} else {
				// 无网操作
				handler.sendEmptyMessage(NETISNOTWORKING);
			}
		}
	}

	private void deComment(final String commentid) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("key", commentid);
				map.put("type", "comment");
				Params param = new Params("delete", map);
				Result result = HttpUtil.httpPost(ClassUpdateMainBodyActivity.this, param);
				if (result == null) {

				} else if ("1".equals(result.getCode())) {
					initCommentList();
				} else {
					handler.sendEmptyMessage(COMMENTDE_FAIL);
				}
			}
		});
		thread.start();
	}

	private boolean checkLength(String tmp) {
		int count = 0;
		for (int i = 0; i < tmp.length(); i++) {
			char c = tmp.charAt(i);
			if (c >= 0 && c <= 9) {
				count++;
			} else if (c >= 'a' && c <= 'z') {
				count++;
			} else if (c >= 'A' && c <= 'Z') {
				count++;
			} else if (Character.isLetter(c)) {
				count += 2;
			} else {
				count++;
			}
		}
		if (count > 140 || count == 0) {
			return false;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (deleteFile) {
			myVideo.deleteFile();
		}
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

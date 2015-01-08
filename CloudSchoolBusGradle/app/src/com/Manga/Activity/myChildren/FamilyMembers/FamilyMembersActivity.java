package com.Manga.Activity.myChildren.FamilyMembers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.myChildren.DoctorConsult.MyAsyncTask;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.ChildReceiver;
import com.Manga.Activity.utils.ImageUtil;
import com.umeng.analytics.MobclickAgent;

public class FamilyMembersActivity extends BaseActivity {
	private static LayoutInflater mInflater;

	private ListView listview_content;
	public static List<ChildReceiver> childReceiverList = null;
	public List<ChildReceiverDouble> childReceiverDoubleList;
	ExamListAdapter reprotDetailAdapter;

	/**
	 * 超时
	 */
	private static final int OUTTIME = 2;
	/**
	 * 初始化接送人
	 */
	private static final int INITPEPOLE = 1;
	/**
	 * 删除成功
	 */
	private static final int DETELESUCCESS = 3;

	/**
	 * 删除失败错误 参数id错误
	 */
	private static final int DETELEID = 4;
	/**
	 * 删除失败错误 不能删除 家长没有此id对应头像
	 */
	private static final int DETELENO = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shuttle);
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listview_content = (ListView) findViewById(R.id.listview_content);
		init();
		ActivityUtil.shuttleActivity = this;
	}

	public void initPepole() {
		MyAsyncTask postSubmitReportTask = new MyAsyncTask(FamilyMembersActivity.this, false) {
			Result result;

			@Override
			protected void onPostExecute(Void vod) {
				init();
				ActivityUtil.baseinfo.setting_student_shuttle_content.setText(childReceiverList.size() + "");
				super.onPostExecute(vod);
			}

			@Override
			protected Void doInBackground(Void... params) {

				if (HttpUtil.isNetworkConnected(FamilyMembersActivity.this)) {
					result = HttpUtil.httpGet(FamilyMembersActivity.this, new Params("childreceiver", null));
					if ("1".equals(result.getCode())) {
						try {
							JSONArray myJson = new JSONArray(result.getContent());
							FamilyMembersActivity.childReceiverList.clear();
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

				return super.doInBackground(params);
			}
		};
		postSubmitReportTask.execute();

	}

	class ChildReceiverDouble {
		ChildReceiver child1;
		ChildReceiver child2;

		public ChildReceiver getChild1() {
			return child1;
		}

		public void setChild1(ChildReceiver child1) {
			this.child1 = child1;
		}

		public ChildReceiver getChild2() {
			return child2;
		}

		public void setChild2(ChildReceiver child2) {
			this.child2 = child2;
		}

	}

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case INITPEPOLE:
				// initPepole();
				break;
			case OUTTIME:
				Toast.makeText(FamilyMembersActivity.this, R.string.out_time, Toast.LENGTH_SHORT).show();
				break;
			case DETELESUCCESS:
				Toast.makeText(FamilyMembersActivity.this, R.string.detele_pepole_success, Toast.LENGTH_SHORT).show();
				initPepole();
				break;
			case DETELEID:
				Toast.makeText(FamilyMembersActivity.this, R.string.detele_pepole_id, Toast.LENGTH_SHORT).show();
				break;
			case DETELENO:
				Toast.makeText(FamilyMembersActivity.this, R.string.detele_pepole_no, Toast.LENGTH_SHORT).show();
				break;
			}
			return false;
		}
	});

	public void init() {
		childReceiverDoubleList = new ArrayList<ChildReceiverDouble>();
		if (childReceiverList != null && childReceiverList.size() > 0) {
			for (int i = 0; i < childReceiverList.size() / 2 + 1; i++) {
				ChildReceiverDouble temp = new ChildReceiverDouble();
				if (i < childReceiverList.size() / 2) {
					temp.setChild1(childReceiverList.get(i * 2));
					temp.setChild2(childReceiverList.get(i * 2 + 1));
				} else if (childReceiverList.size() == i * 2) {
					temp.setChild1(null);
					temp.setChild2(null);
				} else {
					temp.setChild1(childReceiverList.get(i * 2));
					temp.setChild2(null);
				}
				childReceiverDoubleList.add(temp);

			}
		} else {
			ChildReceiverDouble childReceiverDouble = new ChildReceiverDouble();
			childReceiverDouble.setChild1(null);
			childReceiverDouble.setChild2(null);
			childReceiverDoubleList.add(childReceiverDouble);
		}
		reprotDetailAdapter = new ExamListAdapter(childReceiverDoubleList);
		listview_content.setAdapter(reprotDetailAdapter);
	}

	OnClickListener addPepole = new OnClickListener() {

		@Override
		public void onClick(View v) {

			Intent intent = new Intent(ActivityUtil.shuttleActivity, AddFamilyMemberActivity.class);
			ActivityUtil.main.comeIn(intent);

		}
	};

	public void close(View v) {
		ActivityUtil.close(this);
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

	class ExamListAdapter extends BaseAdapter {

		// 数据源
		private List<ChildReceiverDouble> data;

		public ExamListAdapter(List<ChildReceiverDouble> data) {
			super();
			this.data = data;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data != null ? data.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View item = mInflater.inflate(R.layout.item_shuttle, null);
			TextView tv_pepole1 = (TextView) item.findViewById(R.id.tv_pepole1);
			TextView tv_pepole2 = (TextView) item.findViewById(R.id.tv_pepole2);
			ImageView image_pepole1 = (ImageView) item.findViewById(R.id.image_pepole1);
			ImageView image_pepole2 = (ImageView) item.findViewById(R.id.image_pepole2);
			if (data.get(position).getChild1() == null) {
				Bitmap pic = BitmapFactory.decodeResource(getResources(), R.drawable.add_bg);
				image_pepole1.setImageBitmap(pic);
				image_pepole2.setVisibility(View.GONE);
				tv_pepole1.setText(R.string.add);
				tv_pepole2.setVisibility(View.GONE);
				image_pepole1.setOnClickListener(addPepole);
			} else if (data.get(position).getChild2() == null) {
				Bitmap pic = BitmapFactory.decodeResource(getResources(), R.drawable.add_bg);
				image_pepole2.setImageBitmap(pic);
				tv_pepole2.setText(R.string.add);
				image_pepole2.setOnClickListener(addPepole);

				tv_pepole1.setText(data.get(position).getChild1().getRelationship());
				Bitmap pic1 = data.get(position).getChild1().getFileBitmap();
				if (pic1 == null) {// 如果图片获取失败，显示默认图片
					pic1 = BitmapFactory.decodeResource(getResources(), R.drawable.add_bg);
				}
				image_pepole1.setImageBitmap(pic1);
				image_pepole1.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						AlertDialog.Builder builder = new AlertDialog.Builder(FamilyMembersActivity.this);
						builder.setTitle(R.string.detele_tips);
						builder.setMessage(R.string.detele_tips_content);
						builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						});

						builder.setNegativeButton(R.string.binding_phone_verification_btn,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {

										Thread thread = new Thread(new Runnable() {

											@Override
											public void run() {

												if (HttpUtil.isNetworkConnected(FamilyMembersActivity.this)) {
													HashMap<String, String> map = new HashMap<String, String>();
													map.put("id", data.get(position).getChild1().getId());
													Params param = new Params("deletereceiver", map);
													Result result = HttpUtil.httpPost(FamilyMembersActivity.this, param);
													if (result == null) {
														handler.sendEmptyMessage(OUTTIME);
													} else if ("1".equals(result.getCode())) {
														handler.sendEmptyMessage(DETELESUCCESS);
													} else if ("-2".equals(result.getCode())) {
														handler.sendEmptyMessage(DETELEID);
													} else if ("-3".equals(result.getCode())) {
														handler.sendEmptyMessage(DETELENO);
													}
												} else {
													handler.sendEmptyMessage(OUTTIME);
												}
											}
										});
										thread.start();

									}
								});
						builder.create().show();
						return false;
					}
				});
			} else {
				tv_pepole1.setText(data.get(position).getChild1().getRelationship());
				Bitmap pic1 = data.get(position).getChild1().getFileBitmap();
				if (pic1 == null) {// 如果图片获取失败，显示默认图片
					pic1 = BitmapFactory.decodeResource(getResources(), R.drawable.add_bg);
				}
				image_pepole1.setImageBitmap(pic1);
				image_pepole1.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						AlertDialog.Builder builder = new AlertDialog.Builder(FamilyMembersActivity.this);
						builder.setTitle(R.string.detele_tips);
						builder.setMessage(R.string.detele_tips_content);
						builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						});

						builder.setNegativeButton(R.string.binding_phone_verification_btn,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {

										Thread thread = new Thread(new Runnable() {

											@Override
											public void run() {

												if (HttpUtil.isNetworkConnected(FamilyMembersActivity.this)) {
													HashMap<String, String> map = new HashMap<String, String>();
													map.put("id", data.get(position).getChild1().getId());
													Params param = new Params("deletereceiver", map);
													Result result = HttpUtil.httpPost(FamilyMembersActivity.this, param);
													if (result == null) {
														handler.sendEmptyMessage(OUTTIME);
													} else if ("1".equals(result.getCode())) {
														handler.sendEmptyMessage(DETELESUCCESS);
													} else if ("-2".equals(result.getCode())) {
														handler.sendEmptyMessage(DETELEID);
													} else if ("-3".equals(result.getCode())) {
														handler.sendEmptyMessage(DETELENO);
													}
												} else {
													handler.sendEmptyMessage(OUTTIME);
												}
											}
										});
										thread.start();

									}
								});
						builder.create().show();
						return false;
					}
				});
				tv_pepole2.setText(data.get(position).getChild2().getRelationship());
				Bitmap pic2 = data.get(position).getChild2().getFileBitmap();
				if (pic2 == null) {// 如果图片获取失败，显示默认图片
					pic2 = BitmapFactory.decodeResource(getResources(), R.drawable.add_bg);
				}
				image_pepole2.setImageBitmap(pic2);
				image_pepole2.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						AlertDialog.Builder builder = new AlertDialog.Builder(FamilyMembersActivity.this);
						builder.setTitle(R.string.detele_tips);
						builder.setMessage(R.string.detele_tips_content);
						builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						});

						builder.setNegativeButton(R.string.binding_phone_verification_btn,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {

										Thread thread = new Thread(new Runnable() {

											@Override
											public void run() {

												if (HttpUtil.isNetworkConnected(FamilyMembersActivity.this)) {
													HashMap<String, String> map = new HashMap<String, String>();
													map.put("id", data.get(position).getChild2().getId());
													Params param = new Params("deletereceiver", map);
													Result result = HttpUtil.httpPost(FamilyMembersActivity.this, param);
													if (result == null) {
														handler.sendEmptyMessage(OUTTIME);
													} else if ("1".equals(result.getCode())) {
														handler.sendEmptyMessage(DETELESUCCESS);
													} else if ("-2".equals(result.getCode())) {
														handler.sendEmptyMessage(DETELEID);
													} else if ("-3".equals(result.getCode())) {
														handler.sendEmptyMessage(DETELENO);
													}
												} else {
													handler.sendEmptyMessage(OUTTIME);
												}
											}
										});
										thread.start();

									}
								});
						builder.create().show();
						return false;
					}
				});

			}
			return item;
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

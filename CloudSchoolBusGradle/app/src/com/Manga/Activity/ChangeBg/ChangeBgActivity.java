package com.Manga.Activity.ChangeBg;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.SelectBg.SelectBackgroundActivity;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.Student_Info;
import com.umeng.analytics.MobclickAgent;

public class ChangeBgActivity extends Activity {
	private SharedPreferences shp;
	private String language;
	private Bitmap photo;
	private String IMAGE_FILE_NAME;
	private Thread thread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_pg);
		shp = getSharedPreferences("count", Context.MODE_PRIVATE);
		language = getResources().getConfiguration().locale.getLanguage();
	}

	public void onSelectBgClick(View view) {
		Intent intent = new Intent(ChangeBgActivity.this, SelectBackgroundActivity.class);
		ActivityUtil.startActivity(ChangeBgActivity.this, intent);
	}

	public void onAlbumBgClick(View view) {
		Intent picinintent = new Intent(Intent.ACTION_GET_CONTENT);
		picinintent.setType("image/*");
		picinintent.putExtra("crop", true);
		picinintent.putExtra("return-data", true);
		startActivityForResult(picinintent, 1);
	}

	public void onPhotographClick(View view) {

		IMAGE_FILE_NAME = System.currentTimeMillis() + ".jpg";
		Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 判断存储卡是否可以用，可用进行存储
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
					.getExternalStorageDirectory() + "/DCIM/Camera/", IMAGE_FILE_NAME)));
		}

		startActivityForResult(intentFromCapture, 3);
	}

	public void close(View view) {
		ActivityUtil.close(ChangeBgActivity.this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == 2) {
				Bundle extras = data.getExtras();
				photo = extras.getParcelable("data");
				thread = new Thread(new Runnable() {

					@Override
					public void run() {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("skinid", "-1");
						Params params = new Params("skinid", map);
						if (HttpUtil.isNetworkConnected(ChangeBgActivity.this)) {
							Result result = HttpUtil.httpPost(ChangeBgActivity.this, params);
							if (result == null) {
								mhandler.sendEmptyMessage(2);
							} else if ("1".equals(result.getCode())) {
								DB db = new DB(ChangeBgActivity.this);
								SQLiteDatabase sql = db.getWritableDatabase();
								ContentValues values = new ContentValues();
								values.put("skinid", "-1");
								sql.update("student_info", values, "uid=?", new String[] { Student_Info.uid });
								sql.close();
								db.close();
								if (photo != null) {
									ByteArrayOutputStream baos = new ByteArrayOutputStream();
									photo.compress(CompressFormat.PNG, 70, baos);
									byte[] bytes = baos.toByteArray();
									Log.v("base64", "bitmap转换成base64成功。");
									String s = Base64.encodeToString(bytes, Base64.NO_WRAP);
									shp.edit().putString("cbackground", s).commit();
									shp.edit().putInt("cbackgroundindex", -1).commit();
									mhandler.sendEmptyMessage(1);

								} else {
									Message message = new Message();
									message.what = 2;
									mhandler.sendMessage(message);
								}
							} else {
								mhandler.sendEmptyMessage(2);
							}
						} else {
							mhandler.sendEmptyMessage(2);
						}

					}
				});
				if (!thread.isAlive()) {
					thread.start();
				}

			} else if (requestCode == 1) {
				Uri uri = data.getData();
				startPhotoZoom(uri);

			} else if (requestCode == 3) {
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					File tempFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/"
							+ IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(
							ChangeBgActivity.this,
							language.equals("zh") ? "未找到存储卡，无法存储照片！"
									: "Storage card not found, unable to store photos!", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 480);
		intent.putExtra("aspectY", 228);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 480);
		intent.putExtra("outputY", 228);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	private Handler mhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				if (ActivityUtil.share != null) {
					ActivityUtil.share.UpdateBackGround();
				}
				if (ActivityUtil.notice != null) {
					ActivityUtil.notice.UpdateBackGround();
				}
				if (ActivityUtil.activityRegister != null) {
					ActivityUtil.activityRegister.UpdateBackGround();
				}
				Toast.makeText(ChangeBgActivity.this, getResources().getString(R.string.selectbg_success),
						Toast.LENGTH_LONG).show();
				break;
			case 2:

				break;
			default:
				break;
			}
		};
	};

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}

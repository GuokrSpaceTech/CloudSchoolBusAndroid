package com.Manga.Activity.SelectBg;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.Student_Info;
import com.umeng.analytics.MobclickAgent;

public class SelectBackgroundActivity extends Activity {
	private SharedPreferences shp;
	private String language;
	private ImageView imageView1SelectBg;
	private ImageView imageView2SelectBg;
	private ImageView imageView3SelectBg;
	private ImageView imageView4SelectBg;
	private ImageView imageView5SelectBg;
	private ImageView imageView6SelectBg;
	private ImageView imageView7SelectBg;
	private ImageView imageView8SelectBg;
	private ImageView imageView9SelectBg;
	private ImageView imageView10SelectBg;
	private ImageView imageView11SelectBg;
	private ImageView imageView12SelectBg;
	private Thread thread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_pg);
		shp = getSharedPreferences("count", Context.MODE_PRIVATE);
		language = getResources().getConfiguration().locale.getLanguage();
		InitImageViewSelectBg();
		int nSelect = shp.getInt("cbackgroundindex", 0);
		ChangeSelectBg(nSelect);
	}

	public void InitImageViewSelectBg() {
		imageView1SelectBg = (ImageView) findViewById(R.id.imgselect01);
		imageView2SelectBg = (ImageView) findViewById(R.id.imgselect02);
		imageView3SelectBg = (ImageView) findViewById(R.id.imgselect03);
		imageView4SelectBg = (ImageView) findViewById(R.id.imgselect04);
		imageView5SelectBg = (ImageView) findViewById(R.id.imgselect05);
		imageView6SelectBg = (ImageView) findViewById(R.id.imgselect06);
		imageView7SelectBg = (ImageView) findViewById(R.id.imgselect07);
		imageView8SelectBg = (ImageView) findViewById(R.id.imgselect08);
		imageView9SelectBg = (ImageView) findViewById(R.id.imgselect09);
		imageView10SelectBg = (ImageView) findViewById(R.id.imgselect10);
		imageView11SelectBg = (ImageView) findViewById(R.id.imgselect11);
		imageView12SelectBg = (ImageView) findViewById(R.id.imgselect12);
		imageView1SelectBg.setVisibility(View.GONE);
		imageView2SelectBg.setVisibility(View.GONE);
		imageView3SelectBg.setVisibility(View.GONE);
		imageView4SelectBg.setVisibility(View.GONE);
		imageView5SelectBg.setVisibility(View.GONE);
		imageView6SelectBg.setVisibility(View.GONE);
		imageView7SelectBg.setVisibility(View.GONE);
		imageView8SelectBg.setVisibility(View.GONE);
		imageView9SelectBg.setVisibility(View.GONE);
		imageView10SelectBg.setVisibility(View.GONE);
		imageView11SelectBg.setVisibility(View.GONE);
		imageView12SelectBg.setVisibility(View.GONE);
	}

	public void ChangeSelectBg(int select) {
		switch (select) {
		case 0:
			imageView1SelectBg.setVisibility(View.GONE);
			imageView2SelectBg.setVisibility(View.GONE);
			imageView3SelectBg.setVisibility(View.GONE);
			imageView4SelectBg.setVisibility(View.GONE);
			imageView5SelectBg.setVisibility(View.GONE);
			imageView6SelectBg.setVisibility(View.GONE);
			imageView7SelectBg.setVisibility(View.GONE);
			imageView8SelectBg.setVisibility(View.GONE);
			imageView9SelectBg.setVisibility(View.GONE);
			imageView10SelectBg.setVisibility(View.GONE);
			imageView11SelectBg.setVisibility(View.VISIBLE);
			imageView12SelectBg.setVisibility(View.GONE);
			break;
		case 1:
			imageView1SelectBg.setVisibility(View.VISIBLE);
			imageView2SelectBg.setVisibility(View.GONE);
			imageView3SelectBg.setVisibility(View.GONE);
			imageView4SelectBg.setVisibility(View.GONE);
			imageView5SelectBg.setVisibility(View.GONE);
			imageView6SelectBg.setVisibility(View.GONE);
			imageView7SelectBg.setVisibility(View.GONE);
			imageView8SelectBg.setVisibility(View.GONE);
			imageView9SelectBg.setVisibility(View.GONE);
			imageView10SelectBg.setVisibility(View.GONE);
			imageView11SelectBg.setVisibility(View.GONE);
			imageView12SelectBg.setVisibility(View.GONE);
			break;
		case 2:
			imageView1SelectBg.setVisibility(View.GONE);
			imageView2SelectBg.setVisibility(View.VISIBLE);
			imageView3SelectBg.setVisibility(View.GONE);
			imageView4SelectBg.setVisibility(View.GONE);
			imageView5SelectBg.setVisibility(View.GONE);
			imageView6SelectBg.setVisibility(View.GONE);
			imageView7SelectBg.setVisibility(View.GONE);
			imageView8SelectBg.setVisibility(View.GONE);
			imageView9SelectBg.setVisibility(View.GONE);
			imageView10SelectBg.setVisibility(View.GONE);
			imageView11SelectBg.setVisibility(View.GONE);
			imageView12SelectBg.setVisibility(View.GONE);
			break;
		case 3:
			imageView1SelectBg.setVisibility(View.GONE);
			imageView2SelectBg.setVisibility(View.GONE);
			imageView3SelectBg.setVisibility(View.VISIBLE);
			imageView4SelectBg.setVisibility(View.GONE);
			imageView5SelectBg.setVisibility(View.GONE);
			imageView6SelectBg.setVisibility(View.GONE);
			imageView7SelectBg.setVisibility(View.GONE);
			imageView8SelectBg.setVisibility(View.GONE);
			imageView9SelectBg.setVisibility(View.GONE);
			imageView10SelectBg.setVisibility(View.GONE);
			imageView11SelectBg.setVisibility(View.GONE);
			imageView12SelectBg.setVisibility(View.GONE);
			break;
		case 4:
			imageView1SelectBg.setVisibility(View.GONE);
			imageView2SelectBg.setVisibility(View.GONE);
			imageView3SelectBg.setVisibility(View.GONE);
			imageView4SelectBg.setVisibility(View.VISIBLE);
			imageView5SelectBg.setVisibility(View.GONE);
			imageView6SelectBg.setVisibility(View.GONE);
			imageView7SelectBg.setVisibility(View.GONE);
			imageView8SelectBg.setVisibility(View.GONE);
			imageView9SelectBg.setVisibility(View.GONE);
			imageView10SelectBg.setVisibility(View.GONE);
			imageView11SelectBg.setVisibility(View.GONE);
			imageView12SelectBg.setVisibility(View.GONE);
			break;
		case 5:
			imageView1SelectBg.setVisibility(View.GONE);
			imageView2SelectBg.setVisibility(View.GONE);
			imageView3SelectBg.setVisibility(View.GONE);
			imageView4SelectBg.setVisibility(View.GONE);
			imageView5SelectBg.setVisibility(View.VISIBLE);
			imageView6SelectBg.setVisibility(View.GONE);
			imageView7SelectBg.setVisibility(View.GONE);
			imageView8SelectBg.setVisibility(View.GONE);
			imageView9SelectBg.setVisibility(View.GONE);
			imageView10SelectBg.setVisibility(View.GONE);
			imageView11SelectBg.setVisibility(View.GONE);
			imageView12SelectBg.setVisibility(View.GONE);
			break;
		case 6:
			imageView1SelectBg.setVisibility(View.GONE);
			imageView2SelectBg.setVisibility(View.GONE);
			imageView3SelectBg.setVisibility(View.GONE);
			imageView4SelectBg.setVisibility(View.GONE);
			imageView5SelectBg.setVisibility(View.GONE);
			imageView6SelectBg.setVisibility(View.VISIBLE);
			imageView7SelectBg.setVisibility(View.GONE);
			imageView8SelectBg.setVisibility(View.GONE);
			imageView9SelectBg.setVisibility(View.GONE);
			imageView10SelectBg.setVisibility(View.GONE);
			imageView11SelectBg.setVisibility(View.GONE);
			imageView12SelectBg.setVisibility(View.GONE);
			break;
		case 7:
			imageView1SelectBg.setVisibility(View.GONE);
			imageView2SelectBg.setVisibility(View.GONE);
			imageView3SelectBg.setVisibility(View.GONE);
			imageView4SelectBg.setVisibility(View.GONE);
			imageView5SelectBg.setVisibility(View.GONE);
			imageView6SelectBg.setVisibility(View.GONE);
			imageView7SelectBg.setVisibility(View.VISIBLE);
			imageView8SelectBg.setVisibility(View.GONE);
			imageView9SelectBg.setVisibility(View.GONE);
			imageView10SelectBg.setVisibility(View.GONE);
			imageView11SelectBg.setVisibility(View.GONE);
			imageView12SelectBg.setVisibility(View.GONE);
			break;
		case 8:
			imageView1SelectBg.setVisibility(View.GONE);
			imageView2SelectBg.setVisibility(View.GONE);
			imageView3SelectBg.setVisibility(View.GONE);
			imageView4SelectBg.setVisibility(View.GONE);
			imageView5SelectBg.setVisibility(View.GONE);
			imageView6SelectBg.setVisibility(View.GONE);
			imageView7SelectBg.setVisibility(View.GONE);
			imageView8SelectBg.setVisibility(View.VISIBLE);
			imageView9SelectBg.setVisibility(View.GONE);
			imageView10SelectBg.setVisibility(View.GONE);
			imageView11SelectBg.setVisibility(View.GONE);
			imageView12SelectBg.setVisibility(View.GONE);
			break;
		case 9:
			imageView1SelectBg.setVisibility(View.GONE);
			imageView2SelectBg.setVisibility(View.GONE);
			imageView3SelectBg.setVisibility(View.GONE);
			imageView4SelectBg.setVisibility(View.GONE);
			imageView5SelectBg.setVisibility(View.GONE);
			imageView6SelectBg.setVisibility(View.GONE);
			imageView7SelectBg.setVisibility(View.GONE);
			imageView8SelectBg.setVisibility(View.GONE);
			imageView9SelectBg.setVisibility(View.VISIBLE);
			imageView10SelectBg.setVisibility(View.GONE);
			imageView11SelectBg.setVisibility(View.GONE);
			imageView12SelectBg.setVisibility(View.GONE);
			break;
		case 10:
			imageView1SelectBg.setVisibility(View.GONE);
			imageView2SelectBg.setVisibility(View.GONE);
			imageView3SelectBg.setVisibility(View.GONE);
			imageView4SelectBg.setVisibility(View.GONE);
			imageView5SelectBg.setVisibility(View.GONE);
			imageView6SelectBg.setVisibility(View.GONE);
			imageView7SelectBg.setVisibility(View.GONE);
			imageView8SelectBg.setVisibility(View.GONE);
			imageView9SelectBg.setVisibility(View.GONE);
			imageView10SelectBg.setVisibility(View.VISIBLE);
			imageView11SelectBg.setVisibility(View.GONE);
			imageView12SelectBg.setVisibility(View.GONE);
			break;
		case 11:
			imageView1SelectBg.setVisibility(View.GONE);
			imageView2SelectBg.setVisibility(View.GONE);
			imageView3SelectBg.setVisibility(View.GONE);
			imageView4SelectBg.setVisibility(View.GONE);
			imageView5SelectBg.setVisibility(View.GONE);
			imageView6SelectBg.setVisibility(View.GONE);
			imageView7SelectBg.setVisibility(View.GONE);
			imageView8SelectBg.setVisibility(View.GONE);
			imageView9SelectBg.setVisibility(View.GONE);
			imageView10SelectBg.setVisibility(View.GONE);
			imageView11SelectBg.setVisibility(View.VISIBLE);
			imageView12SelectBg.setVisibility(View.GONE);
			break;
		case 12:
			imageView1SelectBg.setVisibility(View.GONE);
			imageView2SelectBg.setVisibility(View.GONE);
			imageView3SelectBg.setVisibility(View.GONE);
			imageView4SelectBg.setVisibility(View.GONE);
			imageView5SelectBg.setVisibility(View.GONE);
			imageView6SelectBg.setVisibility(View.GONE);
			imageView7SelectBg.setVisibility(View.GONE);
			imageView8SelectBg.setVisibility(View.GONE);
			imageView9SelectBg.setVisibility(View.GONE);
			imageView10SelectBg.setVisibility(View.GONE);
			imageView11SelectBg.setVisibility(View.GONE);
			imageView12SelectBg.setVisibility(View.VISIBLE);
			break;
		default:
			imageView1SelectBg.setVisibility(View.GONE);
			imageView2SelectBg.setVisibility(View.GONE);
			imageView3SelectBg.setVisibility(View.GONE);
			imageView4SelectBg.setVisibility(View.GONE);
			imageView5SelectBg.setVisibility(View.GONE);
			imageView6SelectBg.setVisibility(View.GONE);
			imageView7SelectBg.setVisibility(View.GONE);
			imageView8SelectBg.setVisibility(View.GONE);
			imageView9SelectBg.setVisibility(View.GONE);
			imageView10SelectBg.setVisibility(View.GONE);
			imageView11SelectBg.setVisibility(View.GONE);
			imageView12SelectBg.setVisibility(View.GONE);
			break;
		}
	}

	public void close(View view) {
		ActivityUtil.close(SelectBackgroundActivity.this);
		/*
		 * Intent intent = new Intent("com.broadcast.main"); intent.putExtra("flag", 8); this.sendBroadcast(intent);
		 */
	}

	public void OnClickSettingback01(View v) {
		Drawable d = null;
		d = getResources().getDrawable(R.drawable.settingback01);
		postBg("1", d);
		/*
		 * BitmapDrawable bd = (BitmapDrawable) d; Bitmap bm = bd.getBitmap(); if (bm != null) { ByteArrayOutputStream
		 * baos = new ByteArrayOutputStream(); bm.compress(CompressFormat.PNG, 70, baos); byte[] bytes =
		 * baos.toByteArray(); Log.v("base64", "bitmap转换成base64成功。"); String s = Base64.encodeToString(bytes,
		 * Base64.NO_WRAP); shp.edit().putString("cbackground", s).commit(); shp.edit().putInt("cbackgroundindex",
		 * 1).commit(); ChangeSelectBg(1); Message message = new Message(); message.what = 1;
		 * mhandler.sendMessage(message); } else { Message message = new Message(); message.what = 2;
		 * mhandler.sendMessage(message); }
		 */

	}

	public void OnClickSettingback02(View v) {
		Drawable d = null;
		d = getResources().getDrawable(R.drawable.settingback02);
		postBg("2", d);
		// BitmapDrawable bd = (BitmapDrawable) d;
		// Bitmap bm = bd.getBitmap();
		// if (bm != null) {
		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// bm.compress(CompressFormat.PNG, 70, baos);
		// byte[] bytes = baos.toByteArray();
		// Log.v("base64", "bitmap转换成base64成功。");
		// String s = Base64.encodeToString(bytes, Base64.NO_WRAP);
		// shp.edit().putString("cbackground", s).commit();
		// shp.edit().putInt("cbackgroundindex", 2).commit();
		// ChangeSelectBg(2);
		// Message message = new Message();
		// message.what = 1;
		// mhandler.sendMessage(message);
		// } else {
		// Message message = new Message();
		// message.what = 2;
		// mhandler.sendMessage(message);
		// }

	}

	public void OnClickSettingback03(View v) {
		Drawable d = null;
		d = getResources().getDrawable(R.drawable.settingback03);
		postBg("3", d);
		/*
		 * BitmapDrawable bd = (BitmapDrawable) d; Bitmap bm = bd.getBitmap(); if (bm != null) { ByteArrayOutputStream
		 * baos = new ByteArrayOutputStream(); bm.compress(CompressFormat.PNG, 70, baos); byte[] bytes =
		 * baos.toByteArray(); Log.v("base64", "bitmap转换成base64成功。"); String s = Base64.encodeToString(bytes,
		 * Base64.NO_WRAP); shp.edit().putString("cbackground", s).commit(); shp.edit().putInt("cbackgroundindex",
		 * 3).commit(); ChangeSelectBg(3); Message message = new Message(); message.what = 1;
		 * mhandler.sendMessage(message); } else { Message message = new Message(); message.what = 2;
		 * mhandler.sendMessage(message); }
		 */

	}

	public void OnClickSettingback04(View v) {
		Drawable d = null;
		d = getResources().getDrawable(R.drawable.settingback04);
		postBg("4", d);
		/*
		 * BitmapDrawable bd = (BitmapDrawable) d; Bitmap bm = bd.getBitmap(); if (bm != null) { ByteArrayOutputStream
		 * baos = new ByteArrayOutputStream(); bm.compress(CompressFormat.PNG, 70, baos); byte[] bytes =
		 * baos.toByteArray(); Log.v("base64", "bitmap转换成base64成功。"); String s = Base64.encodeToString(bytes,
		 * Base64.NO_WRAP); shp.edit().putString("cbackground", s).commit(); shp.edit().putInt("cbackgroundindex",
		 * 4).commit(); ChangeSelectBg(4); Message message = new Message(); message.what = 1;
		 * mhandler.sendMessage(message); } else { Message message = new Message(); message.what = 2;
		 * mhandler.sendMessage(message); }
		 */

	}

	public void OnClickSettingback05(View v) {
		Drawable d = null;
		d = getResources().getDrawable(R.drawable.settingback05);
		postBg("5", d);
		/*
		 * BitmapDrawable bd = (BitmapDrawable) d; Bitmap bm = bd.getBitmap(); if (bm != null) { ByteArrayOutputStream
		 * baos = new ByteArrayOutputStream(); bm.compress(CompressFormat.PNG, 70, baos); byte[] bytes =
		 * baos.toByteArray(); Log.v("base64", "bitmap转换成base64成功。"); String s = Base64.encodeToString(bytes,
		 * Base64.NO_WRAP); shp.edit().putString("cbackground", s).commit(); shp.edit().putInt("cbackgroundindex",
		 * 5).commit(); ChangeSelectBg(5); Message message = new Message(); message.what = 1;
		 * mhandler.sendMessage(message); } else { Message message = new Message(); message.what = 2;
		 * mhandler.sendMessage(message); }
		 */

	}

	public void OnClickSettingback06(View v) {
		Drawable d = null;
		d = getResources().getDrawable(R.drawable.settingback06);
		postBg("6", d);
		/*
		 * BitmapDrawable bd = (BitmapDrawable) d; Bitmap bm = bd.getBitmap(); if (bm != null) { ByteArrayOutputStream
		 * baos = new ByteArrayOutputStream(); bm.compress(CompressFormat.PNG, 70, baos); byte[] bytes =
		 * baos.toByteArray(); Log.v("base64", "bitmap转换成base64成功。"); String s = Base64.encodeToString(bytes,
		 * Base64.NO_WRAP); shp.edit().putString("cbackground", s).commit(); shp.edit().putInt("cbackgroundindex",
		 * 6).commit(); ChangeSelectBg(6); Message message = new Message(); message.what = 1;
		 * mhandler.sendMessage(message); } else { Message message = new Message(); message.what = 2;
		 * mhandler.sendMessage(message); }
		 */

	}

	public void OnClickSettingback07(View v) {
		Drawable d = null;
		d = getResources().getDrawable(R.drawable.settingback07);
		postBg("7", d);
		/*
		 * BitmapDrawable bd = (BitmapDrawable) d; Bitmap bm = bd.getBitmap(); if (bm != null) { ByteArrayOutputStream
		 * baos = new ByteArrayOutputStream(); bm.compress(CompressFormat.PNG, 70, baos); byte[] bytes =
		 * baos.toByteArray(); Log.v("base64", "bitmap转换成base64成功。"); String s = Base64.encodeToString(bytes,
		 * Base64.NO_WRAP); shp.edit().putString("cbackground", s).commit(); shp.edit().putInt("cbackgroundindex",
		 * 7).commit(); ChangeSelectBg(7); Message message = new Message(); message.what = 1;
		 * mhandler.sendMessage(message); } else { Message message = new Message(); message.what = 2;
		 * mhandler.sendMessage(message); }
		 */

	}

	public void OnClickSettingback08(View v) {
		Drawable d = null;
		d = getResources().getDrawable(R.drawable.settingback08);
		postBg("8", d);
		/*
		 * BitmapDrawable bd = (BitmapDrawable) d; Bitmap bm = bd.getBitmap(); if (bm != null) { ByteArrayOutputStream
		 * baos = new ByteArrayOutputStream(); bm.compress(CompressFormat.PNG, 70, baos); byte[] bytes =
		 * baos.toByteArray(); Log.v("base64", "bitmap转换成base64成功。"); String s = Base64.encodeToString(bytes,
		 * Base64.NO_WRAP); shp.edit().putString("cbackground", s).commit(); shp.edit().putInt("cbackgroundindex",
		 * 8).commit(); ChangeSelectBg(8); Message message = new Message(); message.what = 1;
		 * mhandler.sendMessage(message); } else { Message message = new Message(); message.what = 2;
		 * mhandler.sendMessage(message); }
		 */

	}

	public void OnClickSettingback09(View v) {
		Drawable d = null;
		d = getResources().getDrawable(R.drawable.settingback09);
		postBg("9", d);
		/*
		 * BitmapDrawable bd = (BitmapDrawable) d; Bitmap bm = bd.getBitmap(); if (bm != null) { ByteArrayOutputStream
		 * baos = new ByteArrayOutputStream(); bm.compress(CompressFormat.PNG, 70, baos); byte[] bytes =
		 * baos.toByteArray(); Log.v("base64", "bitmap转换成base64成功。"); String s = Base64.encodeToString(bytes,
		 * Base64.NO_WRAP); shp.edit().putString("cbackground", s).commit(); shp.edit().putInt("cbackgroundindex",
		 * 9).commit(); ChangeSelectBg(9); Message message = new Message(); message.what = 1;
		 * mhandler.sendMessage(message); } else { Message message = new Message(); message.what = 2;
		 * mhandler.sendMessage(message); }
		 */

	}

	public void OnClickSettingback10(View v) {
		Drawable d = null;
		d = getResources().getDrawable(R.drawable.settingback10);
		postBg("10", d);
		/*
		 * BitmapDrawable bd = (BitmapDrawable) d; Bitmap bm = bd.getBitmap(); if (bm != null) { ByteArrayOutputStream
		 * baos = new ByteArrayOutputStream(); bm.compress(CompressFormat.PNG, 70, baos); byte[] bytes =
		 * baos.toByteArray(); Log.v("base64", "bitmap转换成base64成功。"); String s = Base64.encodeToString(bytes,
		 * Base64.NO_WRAP); shp.edit().putString("cbackground", s).commit(); shp.edit().putInt("cbackgroundindex",
		 * 10).commit(); ChangeSelectBg(10); Message message = new Message(); message.what = 1;
		 * mhandler.sendMessage(message); } else { Message message = new Message(); message.what = 2;
		 * mhandler.sendMessage(message); }
		 */

	}

	public void OnClickSettingback11(View v) {
		Drawable d = null;
		d = getResources().getDrawable(R.drawable.settingback11);
		postBg("11", d);
		/*
		 * BitmapDrawable bd = (BitmapDrawable) d; Bitmap bm = bd.getBitmap(); if (bm != null) { ByteArrayOutputStream
		 * baos = new ByteArrayOutputStream(); bm.compress(CompressFormat.PNG, 70, baos); byte[] bytes =
		 * baos.toByteArray(); Log.v("base64", "bitmap转换成base64成功。"); String s = Base64.encodeToString(bytes,
		 * Base64.NO_WRAP); shp.edit().putString("cbackground", s).commit(); shp.edit().putInt("cbackgroundindex",
		 * 11).commit(); ChangeSelectBg(11); Message message = new Message(); message.what = 1;
		 * mhandler.sendMessage(message); } else { Message message = new Message(); message.what = 2;
		 * mhandler.sendMessage(message); }
		 */

	}

	public void OnClickSettingback12(View v) {
		Drawable d = null;
		d = getResources().getDrawable(R.drawable.settingback12);
		postBg("12", d);
		/*
		 * BitmapDrawable bd = (BitmapDrawable) d; Bitmap bm = bd.getBitmap(); if (bm != null) { ByteArrayOutputStream
		 * baos = new ByteArrayOutputStream(); bm.compress(CompressFormat.PNG, 70, baos); byte[] bytes =
		 * baos.toByteArray(); Log.v("base64", "bitmap转换成base64成功。"); String s = Base64.encodeToString(bytes,
		 * Base64.NO_WRAP); shp.edit().putString("cbackground", s).commit(); shp.edit().putInt("cbackgroundindex",
		 * 12).commit(); ChangeSelectBg(12); Message message = new Message(); message.what = 1;
		 * mhandler.sendMessage(message);
		 * 
		 * } else { Message message = new Message(); message.what = 2; mhandler.sendMessage(message); }
		 */

	}

	private void postBg(final String foo, final Drawable d) {
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("skinid", (Integer.parseInt(foo) - 1) + "");
				Params params = new Params("skinid", map);
				if (HttpUtil.isNetworkConnected(SelectBackgroundActivity.this)) {
					Result result = HttpUtil.httpPost(SelectBackgroundActivity.this, params);
					if (result == null) {
						mhandler.sendEmptyMessage(2);
					} else if ("1".equals(result.getCode())) {
						DB db = new DB(SelectBackgroundActivity.this);
						SQLiteDatabase sql = db.getWritableDatabase();
						ContentValues values = new ContentValues();
						values.put("skinid", (Integer.parseInt(foo) - 1) + "");
						sql.update("student_info", values, "uid=?", new String[] { Student_Info.uid });
						sql.close();
						db.close();
						BitmapDrawable bd = (BitmapDrawable) d;
						Bitmap bm = bd.getBitmap();
						if (bm != null) {
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							bm.compress(CompressFormat.PNG, 70, baos);
							byte[] bytes = baos.toByteArray();
							Log.v("base64", "bitmap转换成base64成功。");
							String s = Base64.encodeToString(bytes, Base64.NO_WRAP);
							shp.edit().putString("cbackground", s).commit();
							shp.edit().putInt("cbackgroundindex", Integer.parseInt(foo) - 1).commit();
							Message message = mhandler.obtainMessage(1, Integer.parseInt(foo));
							mhandler.sendMessage(message);

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

	}

	private Handler mhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				ChangeSelectBg(Integer.parseInt(msg.obj.toString()));
				Toast.makeText(SelectBackgroundActivity.this,
						language.equals("zh") ? "更换背景成功！" : "Successful modification!", Toast.LENGTH_SHORT).show();
				try {
					if (!ActivityUtil.share.isFinishing())
						ActivityUtil.share.UpdateBackGround();
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					if (!ActivityUtil.notice.isFinishing())
						ActivityUtil.notice.UpdateBackGround();
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					if (!ActivityUtil.activityRegister.isFinishing())
						ActivityUtil.activityRegister.UpdateBackGround();
				} catch (Exception e) {
					// TODO: handle exception
				}

				break;
			case 2:
				// getStudentInfo();
				Toast.makeText(SelectBackgroundActivity.this,
						language.equals("zh") ? "修改失败！" : "Change password failed!", Toast.LENGTH_SHORT).show();

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

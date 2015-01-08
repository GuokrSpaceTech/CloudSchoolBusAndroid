package com.Manga.Activity.growthFiles;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.ImageUtil;
import com.Manga.Activity.utils.Student_Info;
import com.umeng.analytics.MobclickAgent;

public class GrowFilesMainActivity extends BaseActivity {
	private View headPic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grow_main_select);

		headPic = findViewById(R.id.view_head);
		init();
	}

	public void close(View view) {
		ActivityUtil.close(this);
	}

	public void growworkclick(View view) {
		Intent intent = new Intent(this, GrowFilesMainActivity.class);
		ActivityUtil.main.comeIn(intent);
	}

	public void growrecordclick(View view) {
		Intent intent = new Intent(this, GrowFilesRecordActivity.class);
		ActivityUtil.main.comeIn(intent);
	}

	public void growcommentclick(View view) {
		Intent intent = new Intent(this, GrowFilesCommentActivity.class);
		ActivityUtil.main.comeIn(intent);
	}

	private void init() {
		DB db = new DB(this);
		SQLiteDatabase sql = db.getReadableDatabase();
		Cursor cursor = sql.query("student_info", null, "uid=?", new String[] { Student_Info.uid }, null, null, null);
		if (cursor == null || cursor.getCount() == 0) {

		} else {
			cursor.moveToFirst();
			Bitmap bitmap = ImageUtil.base64ToBitmap(cursor.getString(cursor.getColumnIndex("avatar")));
			bitmap = toRoundBitmap(bitmap);
			headPic.setBackgroundDrawable(new BitmapDrawable(bitmap));

		}
		sql.close();
		db.close();
	}

	private Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
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

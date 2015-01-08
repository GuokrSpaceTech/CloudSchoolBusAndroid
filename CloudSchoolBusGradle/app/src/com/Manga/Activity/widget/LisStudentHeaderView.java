package com.Manga.Activity.widget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.utils.ImageUtil;

public class LisStudentHeaderView extends RelativeLayout {
	private static final int OK = 1;
	private View view;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case OK:
				// Bitmap
				// bitmap=ImageUtil.round((Bitmap)msg.obj,DensityUtil.dip2px(getContext(),
				// 20),Color.WHITE);
				Bitmap bitmap = ImageUtil.round((Bitmap) msg.obj, 20, Color.WHITE);
				setStudentHeaderBG(bitmap);
				break;
			}
			return false;
		}
	});

	public LisStudentHeaderView(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public LisStudentHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		RelativeLayout myShow = (RelativeLayout) (LayoutInflater.from(context).inflate(R.layout.student_header, null));
		view = myShow.findViewById(R.id.student_header_bg);
		addView(myShow, params);
	}

	public void setStudentHeaderBG(Bitmap bitmap) {
		BitmapDrawable drawable = new BitmapDrawable(bitmap);
		view.setBackgroundDrawable(drawable);
	}

	public void setImageBackgroundDrawable(final String image) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				DB db = new DB(getContext());
				SQLiteDatabase sql = db.getWritableDatabase();
				Cursor cursor = sql.query("article_pic", null, "p_name=?", new String[] { image }, null, null, null);
				if (cursor == null || cursor.getCount() == 0) {
					ContentValues values = new ContentValues();
					Bitmap bitmap = ImageUtil.getImage(image);
					if (bitmap == null) {
						Log.d("图片网络获取失败", "图片网络获取失败");
						if (cursor != null) {
							cursor.close();
							sql.close();
							db.close();
						}
						return;
					}
					values.put("p_name", image);
					values.put("p_res", ImageUtil.bitmapToBase64(bitmap));
					sql.insert("article_pic", "p_name", values);
					handler.sendMessage(handler.obtainMessage(OK, bitmap));
				} else {
					cursor.moveToFirst();
					Bitmap bitmap = ImageUtil.base64ToBitmap(cursor.getString(cursor.getColumnIndex("p_res")));
					handler.sendMessage(handler.obtainMessage(OK, bitmap));
				}
				cursor.close();
				sql.close();
				db.close();
			}
		});
		thread.start();
	}

	public void setDefH() {
		Drawable de=getResources().getDrawable(R.drawable.head_def_bg);
		BitmapDrawable bd = (BitmapDrawable) de;
		handler.sendMessage(handler.obtainMessage(OK, bd.getBitmap()));
	}
}

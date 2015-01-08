package com.Manga.Activity.widget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.utils.ImageUtil;

public class ShareImage extends ImageView {
	private static final int OK = 1;
	private static final int NO = 2;
	private ProgressBar progress;
	private int w,h;
	Context cntx;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case OK:
				if (progress != null) {
					progress.setVisibility(View.GONE);
				}
				Drawable d = new BitmapDrawable((Bitmap) msg.obj);
				setBackgroundDrawable(d);
				break;
			case NO:
				setBackgroundDrawable(getResources().getDrawable(R.drawable.oldbg_classshare));
				break;
			}
			setVisibility(View.VISIBLE);
			return false;
		}
	});

	public ShareImage(Context context) {
		super(context);
	    w = getWidth();
	    h = getHeight();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ShareImage(Context context, AttributeSet attrs) {
		super(context, attrs);
	    w = getWidth();
	    h = getHeight();
	}

	public void setImageBackgroundDrawable(Bitmap bitmap) {
		
		Drawable d = new BitmapDrawable(bitmap);
		setBackgroundDrawable(d);
	}

	public void setImageBackgroundDrawable(final String image) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				DB db = new DB(getContext());
				SQLiteDatabase sql = db.getWritableDatabase();
				Cursor cursor = sql.query("article_pic", null, "p_name=?", new String[] { image }, null, null, null);
				if (cursor == null || cursor.getCount() == 0) {
					ContentValues values = new ContentValues();
					Bitmap bitmap = ImageUtil.getImage(image);
					if (bitmap == null) {
						handler.sendEmptyMessage(NO);
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
				if (cursor != null) {
					cursor.close();
				}
				sql.close();
				db.close();
			}
		});
		thread.start();
	}

	public void setImageBackgroundDrawable(final String image, final ProgressBar progress_image) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				progress = progress_image;
				DB db = new DB(getContext());
				SQLiteDatabase sql = db.getWritableDatabase();
				Cursor cursor = sql.query("article_pic", null, "p_name=?", new String[] { image }, null, null, null);
				if (cursor == null || cursor.getCount() == 0) {
					ContentValues values = new ContentValues();
					Bitmap bitmap = ImageUtil.getImage(image);
					if (bitmap == null) {
						handler.sendEmptyMessage(NO);
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
				if (cursor != null) {
					cursor.close();
				}
				sql.close();
				db.close();
			}
		});
		thread.start();
	}
}

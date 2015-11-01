package com.android.support.utils;

import java.io.IOException;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

public class SDPictureUtil {

	public static void getSDPicture(Activity activity) {
		int i = 0;
		Uri uri = null;
		Uri[] uriArray = null;
		Bitmap bitmap = null;
		Bitmap newBitmap = null;
		// 想要的返回值所在的列
		String[] projection = { MediaStore.Images.Thumbnails._ID };
		// 图片信息存储在 android.provider.MediaStore.Images.Thumbnails数据库
		// 快速查询数据库中的图片对应存放路劲
		Cursor cursor = activity.managedQuery(
				MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, // List
																				// of
																				// columns
																				// to
																				// return
																				// ：想要他返回的列
				null, // Return all rows
				null, null);
		int columnIndex = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
		uriArray = new Uri[cursor.getCount()];// 把图片路径放在数组中
		while (cursor.moveToNext() && i < cursor.getCount()) { // 移到指定的位置，遍历数据库
			cursor.moveToPosition(i);
			uri = Uri.withAppendedPath(
					MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
					cursor.getInt(columnIndex) + "");
			uriArray[i] = uri;
			try {
				bitmap = BitmapFactory.decodeStream(activity
						.getContentResolver().openInputStream(uri));
				if (bitmap != null) {
					// 将原来的位图转换成新的位图
					newBitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, true);
					bitmap.recycle();// 释放内存
					if (newBitmap != null) {
						// publishProgress(new LoadedImage(newBitmap));
					}
				}
			} catch (IOException e) {
			}
			i++;
		}

	}

}

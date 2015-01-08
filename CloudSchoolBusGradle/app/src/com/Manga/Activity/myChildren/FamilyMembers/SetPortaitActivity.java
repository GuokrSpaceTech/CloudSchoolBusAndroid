package com.Manga.Activity.myChildren.FamilyMembers;

import java.io.ByteArrayOutputStream;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.DensityUtil;
import com.umeng.analytics.MobclickAgent;

public class SetPortaitActivity extends BaseActivity {
	private String language;
	private Bitmap photo;
	private Bitmap pepoleHead;
	public String headImage = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_head);
		ActivityUtil.addHeadActivity = this;
	}

	public void cancel(View view) {
		finish();
	}

	/**
	 * 从图库中选择图片
	 * 
	 * @param view
	 */
	public void albums(View view) {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, 1);
	}

	/**
	 * 拍照
	 * 
	 * @param view
	 */
	public void photograph(View view) {
		Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intentFromCapture, 3);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				Uri uri = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				options.inSampleSize = 10;
				pepoleHead = BitmapFactory.decodeFile(picturePath, options);
				setHeader(pepoleHead);
			} else if (requestCode == 3) {
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					Bundle bundle = data.getExtras();
					// 获取相机返回的数据，并转换为图片格式
					pepoleHead = (Bitmap) bundle.get("data");
					setHeader(pepoleHead);
				} else {
					Toast.makeText(
							SetPortaitActivity.this,
							language.equals("zh") ? "未找到存储卡，无法存储照片！"
									: "Storage card not found, unable to store photos!", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private void setHeader(Bitmap tmp) {
		int width = tmp.getWidth();
		int height = tmp.getHeight();
		// 设置想要的大小
		int newWidth = DensityUtil.dip2px(this, 100);
		int newHeight = DensityUtil.dip2px(this, 100);
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		photo = Bitmap.createBitmap(tmp, 0, 0, width, height, matrix, true);
		if (photo != null) {
			if (photo.getWidth() < 112) {
				Toast.makeText(this, R.string.pic_is_too_small, Toast.LENGTH_SHORT).show();
				return;
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			photo.compress(CompressFormat.PNG, 70, baos);
			byte[] bytes = baos.toByteArray();
			headImage = Base64.encodeToString(bytes, Base64.NO_WRAP);
			ActivityUtil.addShuttleActivity.image_add.setImageBitmap(photo);
			finish();
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

package com.Manga.Activity.Msg;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.Manga.Activity.BaseActivity;
import com.Manga.Activity.R;
import com.Manga.Activity.DB.DB;
import com.Manga.Activity.httputils.HttpUtil;
import com.Manga.Activity.httputils.Params;
import com.Manga.Activity.httputils.Result;
import com.Manga.Activity.utils.ActivityUtil;
import com.Manga.Activity.utils.DensityUtil;
import com.Manga.Activity.utils.Student_Info;
import com.umeng.analytics.MobclickAgent;

public class SelectHeadActivity extends BaseActivity {
	private String language;
	private Bitmap photo;
	private String IMAGE_FILE_NAME;
	/**
	 * 无网络上出图片
	 */
	private static final int UPLONDINGIMAGE = 8;
	/**
	 * 头像上传成功
	 */
	private static final int UPLODINGHEADER = 10;
	/**
	 * 头像上传失败
	 */
	private static final int UPLODINGHEADERF = 11;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_head);
		language = getResources().getConfiguration().locale.getLanguage();
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
		IMAGE_FILE_NAME = System.currentTimeMillis() + ".jpg";
		Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intentFromCapture, 3);
	}

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message mess) {
			// TODO Auto-generated method stub
			switch (mess.what) {
			case UPLONDINGIMAGE:
				Toast.makeText(SelectHeadActivity.this, R.string.no_network_cannt_uploding_image, Toast.LENGTH_SHORT)
						.show();
				break;
			case UPLODINGHEADER:

				if (ActivityUtil.notice != null) {
					ActivityUtil.notice.UpdateHead();
				}
				if (ActivityUtil.activityRegister != null) {
					ActivityUtil.activityRegister.UpdateHead();
				}
				if (ActivityUtil.share != null) {
					ActivityUtil.share.UpdateHead();
				}

				if (ActivityUtil.baseinfo != null) {
					ActivityUtil.baseinfo.init();
				}
				if (ActivityUtil.mychildren != null) {
					ActivityUtil.mychildren.init();
				}
				Toast.makeText(SelectHeadActivity.this, R.string.uploding_image_ok, Toast.LENGTH_SHORT).show();
				break;
			case UPLODINGHEADERF:
				Toast.makeText(SelectHeadActivity.this, R.string.uploding_image_die, Toast.LENGTH_SHORT).show();
				break;
			}
			return false;
		}
	});

	private void uploadingAvatar(final String foo) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("fbody", foo);
				Params params = new Params("avatar", map);
				if (HttpUtil.isNetworkConnected(SelectHeadActivity.this)) {
					Result result = HttpUtil.httpPost(SelectHeadActivity.this, params);
					if (result == null) {
						handler.sendEmptyMessage(UPLONDINGIMAGE);
					} else if ("1".equals(result.getCode())) {
						Message message = handler.obtainMessage(UPLODINGHEADER, foo);
						DB db = new DB(SelectHeadActivity.this);
						SQLiteDatabase sql = db.getWritableDatabase();
						ContentValues values = new ContentValues();
						values.put("avatar", foo);
						sql.update("student_info", values, "uid=?", new String[] { Student_Info.uid });
						sql.close();
						db.close();

						handler.sendMessage(message);
					} else {
						handler.sendEmptyMessage(UPLODINGHEADERF);
					}
				} else {
					handler.sendEmptyMessage(UPLONDINGIMAGE);
				}
			}
		});
		thread.start();
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
				Bitmap bitmap = BitmapFactory.decodeFile(picturePath, options);
				setHeader(bitmap);
			} else if (requestCode == 3) {
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					Bundle bundle = data.getExtras();
					// 获取相机返回的数据，并转换为图片格式
					Bitmap bitmap = (Bitmap) bundle.get("data");
					setHeader(bitmap);
				} else {
					Toast.makeText(
							SelectHeadActivity.this,
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
		int newWidth = DensityUtil.dip2px(this, 75);
		int newHeight = DensityUtil.dip2px(this, 75);
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
			String s = Base64.encodeToString(bytes, Base64.NO_WRAP);
			finish();
			uploadingAvatar(s);
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

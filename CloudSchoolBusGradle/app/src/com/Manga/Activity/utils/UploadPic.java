package com.Manga.Activity.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.Manga.Activity.R;
import com.Manga.Activity.utils.dialog.CustomListDialog;
import com.android.support.jhf.debug.DebugLog;
import com.android.support.jhf.handlerui.HandlerToastUI;
import com.android.support.jhf.utils.ImageUtil;
import com.android.support.jhf.utils.SDCardToolUtil;
import com.android.support.jhf.utils.ThumbnailUtils;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用于上传照片，通过照相机，或者通过相册
 * 
 * @author hongfeijia
 * 
 */
public class UploadPic {

	/**
	 * 照相机或者gallery获取图片成功
	 * 
	 * @author lenovo
	 * 
	 */
	public interface OnGetPicSucceed {
		public void onGetPicSucceed(String picPathString, int requestCode);
	}

	public static final String IMAGE_CAPTURE_NAME = "cameraTmp.jpg"; // 照片名称
	public static final String SDCARD_ROOT_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath();
	public static final String DCIM = Version.minProductName;
	public static final String APP_PIC_FOLDER = "CallSystemCamera";
	public static final String APP_VIDEO_FOLDER = "CallSystemVideo";
	public static final String PICTURE_SAVE_FOLDER = "ImgSave";

	/** 用来标识请求照相功能的activity */
	public static final int CAMERA_WITH_DATA = 3023;
	/** 用来标识请求gallery的activity */
	public static final int PHOTO_PICKED_WITH_DATA = 3021;

	private Activity mActivity;
	private Fragment mFragment;
	/** 照片的名字以时间命名 */
	public String mCameraPicNameString;

	private OnGetPicSucceed mOnGetPicSucceed;

	public UploadPic(Activity activity) {
		mActivity = activity;
	}

	public UploadPic(Fragment fragment) {
		mFragment = fragment;
	}

	public void setOnGetPicSucceed(OnGetPicSucceed onGetPicSucceed) {
		mOnGetPicSucceed = onGetPicSucceed;
	}

	/**
	 * 照相或者在Gallery中选择
	 */
	public void doPickPhotoAction() {

		CustomListDialog customListDialog = null;
		Activity activity = null;
		if (null != mActivity) {
			activity = mActivity;
		} else if (null != mFragment) {
			activity = mFragment.getActivity();
		}
		if (!SDCardToolUtil.isExistSDCard()) {
			HandlerToastUI.getHandlerToastUI(activity, "请插入SD卡");
			return;
		}
		customListDialog = CustomListDialog.getCustomListDialog(activity,
				com.android.support.jhf.R.style.DialogFromDownToUp);
		List<CustomListDialog.CustomListDialogItem> customListDialogItems = new ArrayList<CustomListDialog.CustomListDialogItem>();
		CustomListDialog.CustomListDialogItem customListDialogItem1 = new CustomListDialog.CustomListDialogItem();
		customListDialogItem1.text = activity.getString(R.string.take_photo);
		customListDialogItem1.textColor = Color.RED;
		customListDialogItem1.textSize = 18;
		customListDialogItems.add(customListDialogItem1);
		CustomListDialog.CustomListDialogItem customListDialogItem2 = new CustomListDialog.CustomListDialogItem();
		customListDialogItem2.text = activity
				.getString(R.string.choose_existing);
		customListDialogItem2.textColor = activity.getResources().getColor(
				R.color.btn_bg_blue);
		customListDialogItem2.textSize = 18;
		customListDialogItems.add(customListDialogItem2);
		customListDialog.setMessage(customListDialogItems);
		customListDialog.setTitle(activity
				.getString(R.string.please_select_picture));
		customListDialog.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (0 == arg2) {
					// 拍照
					doTakePhoto();
				} else if (1 == arg2) {
					// 从相册中选择
					doPickPhotoFromGallery();
				}
			}
		});
		customListDialog.show();
	}

	/**
	 * 照相或者在Gallery中选择
	 * 
	 * @param titleString
	 */
	public void doPickPhotoAction(String titleString) {

		CustomListDialog customListDialog = null;
		Activity activity = null;
		if (null != mActivity) {
			activity = mActivity;
		} else if (null != mFragment) {
			activity = mFragment.getActivity();
		}
		if (!SDCardToolUtil.isExistSDCard()) {
			HandlerToastUI.getHandlerToastUI(activity, "请插入SD卡");
			return;
		}
		customListDialog = CustomListDialog.getCustomListDialog(activity,
				com.android.support.jhf.R.style.DialogFromDownToUp);
		List<CustomListDialog.CustomListDialogItem> customListDialogItems = new ArrayList<CustomListDialog.CustomListDialogItem>();
		CustomListDialog.CustomListDialogItem customListDialogItem1 = new CustomListDialog.CustomListDialogItem();
		customListDialogItem1.text = activity.getString(R.string.take_photo);
		customListDialogItem1.textColor = Color.RED;
		customListDialogItem1.textSize = 18;
		customListDialogItems.add(customListDialogItem1);
		CustomListDialog.CustomListDialogItem customListDialogItem2 = new CustomListDialog.CustomListDialogItem();
		customListDialogItem2.text = activity
				.getString(R.string.choose_existing);
		customListDialogItem2.textColor = activity.getResources().getColor(
				R.color.btn_bg_blue);
		customListDialogItem2.textSize = 18;
		customListDialogItems.add(customListDialogItem2);
		customListDialog.setMessage(customListDialogItems);
		customListDialog.setTitle(titleString);
		customListDialog.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (0 == arg2) {
					// 拍照
					doTakePhoto();
				} else if (1 == arg2) {
					// 从相册中选择
					doPickPhotoFromGallery();
				}
			}
		});
		customListDialog.show();
	}

	/**
	 * 因为调用了Camera和Gally所以要判断他们各自的返回情况,他们启动时是这样的startActivityForResult
	 * 
	 * @param uri
	 * @return
	 */
	private String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = null;
		if (null != mActivity) {
			cursor = mActivity.managedQuery(uri, projection, null, null, null);
		} else if (null != mFragment) {
			cursor = mFragment.getActivity().managedQuery(uri, projection,
					null, null, null);
		}
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String filePath = cursor.getString(column_index);
		try {
			// 4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)
			if (Integer.parseInt(Build.VERSION.SDK) < 14) {
				cursor.close();
			}
		} catch (Exception e) {
			DebugLog.logI("error:" + e);
		}

		String imagePathString = filePath;
		String imageNameString = imagePathString.substring(imagePathString
				.lastIndexOf("/") + 1);
		String imageSuffixString = imagePathString.substring(imagePathString
				.lastIndexOf(".") + 1);

		String tempImagePathString = SDCARD_ROOT_PATH + "/" + DCIM + "/"
				+ APP_PIC_FOLDER + "/" + imageNameString;

		int degree = ImageUtil.readPictureDegree(imagePathString);
		Bitmap bitmap = ThumbnailUtils.setThumbnailBitmap(new File(
                imagePathString), 480, 800);

		if (Math.abs(degree) > 0) {
			bitmap = ImageUtil.rotaingImageView(degree, bitmap);

		} else {

		}
		
		File file = new File(tempImagePathString.substring(0,
				tempImagePathString.lastIndexOf("/")));
		if (!file.exists()) {
			file.mkdirs();
		}
		CompressFormat compressFormat = CompressFormat.JPEG;
		if (imageSuffixString.equalsIgnoreCase("JPG")
				|| imageSuffixString.equalsIgnoreCase("JPEG")) {

		} else if (imageSuffixString.equalsIgnoreCase("PNG")) {
			compressFormat = CompressFormat.PNG;
		}
		ImageUtil.saveBitmap(bitmap, tempImagePathString, compressFormat);

		return tempImagePathString;

	}

	/**
	 * 当选择图库照片上传时由于图片太大上传很慢需要压缩生成临时文件，这个方法主要是降生成的临时文件删除，无论上传成功还是错误都要删除
	 * 
	 * @param picPathString
	 */
	public void removePicture(String picPathString) {
		if(TextUtils.isEmpty(picPathString)){
			return ;
		}
		File file = new File(picPathString);
		file.delete();
	}

	/**
	 * 返回照相机存储图片的路径
	 * 
	 * @return
	 */
	public String getCameraPath() {

		String imagePathString = SDCARD_ROOT_PATH + "/" + DCIM + "/"
				+ APP_PIC_FOLDER + "/" + mCameraPicNameString + ".jpg";

		int degree = ImageUtil.readPictureDegree(imagePathString);
		Bitmap bitmap = ThumbnailUtils.setThumbnailBitmap(new File(
                imagePathString), 480, 800);

		if (Math.abs(degree) > 0) {
			bitmap = ImageUtil.rotaingImageView(degree, bitmap);

		} else {

		}
		File file = new File(imagePathString);
		if (file.exists()) {
			file.delete();
		}
		ImageUtil.saveBitmap(bitmap, imagePathString, CompressFormat.JPEG);

		return imagePathString;
	}

	public String getPhonePath(Intent data) {
		Uri uri = data.getData();
		String selectedImagePath = getPath(uri);
		return selectedImagePath;
	}

	/**
	 * 拍照获取图片,进行裁剪
	 * 
	 */
	public void doTakePhotoCrop() {

		try {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd-HH-mm-ss");
			mCameraPicNameString = format.format((new Date()));

			Log.e("doTakePhoto mCameraPicNameString", mCameraPicNameString);

			File sohuAutoFile = new File(SDCARD_ROOT_PATH + "/" + DCIM);
			if (!sohuAutoFile.exists()) {
				sohuAutoFile.mkdirs();
			}

			File picDirFile = new File(sohuAutoFile.getAbsolutePath() + "/"
					+ APP_PIC_FOLDER);
			if (!picDirFile.exists()) {
				picDirFile.mkdirs();
			}

			// 创建文件
			File picFile = new File(picDirFile.getAbsolutePath() + "/",
					mCameraPicNameString + ".jpg");
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// 存储卡可用 将照片存储在 sdcard
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picFile));
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 512);
			intent.putExtra("outputY", 512);
			if (null != mActivity) {
				mActivity.startActivityForResult(intent, CAMERA_WITH_DATA);
			} else if (null != mFragment) {
				mFragment.startActivityForResult(intent, CAMERA_WITH_DATA);
			}
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			// HandlerToastUI.getHandlerToastUI(mActivity,
			// "R.string.photoPickerNotFoundText");
		}
	}

	/**
	 * 拍照获取图片
	 * 
	 */
	public void doTakePhoto() {

		try {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd-HH-mm-ss");
			mCameraPicNameString = format.format((new Date()));

			Log.e("doTakePhoto mCameraPicNameString", mCameraPicNameString);

			File sohuAutoFile = new File(SDCARD_ROOT_PATH + "/" + DCIM);
			if (!sohuAutoFile.exists()) {
				sohuAutoFile.mkdirs();
			}

			File picDirFile = new File(sohuAutoFile.getAbsolutePath() + "/"
					+ APP_PIC_FOLDER);
			if (!picDirFile.exists()) {
				picDirFile.mkdirs();
			}

			// 创建文件
			File picFile = new File(picDirFile.getAbsolutePath() + "/",
					mCameraPicNameString + ".jpg");
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// 存储卡可用 将照片存储在 sdcard
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picFile));
			// intent.putExtra("crop", "true");
			// intent.putExtra("aspectX", 1);
			// intent.putExtra("aspectY", 1);
			// intent.putExtra("outputX", 128);
			// intent.putExtra("outputY", 128);
			if (null != mActivity) {
				mActivity.startActivityForResult(intent, CAMERA_WITH_DATA);
			} else if (null != mFragment) {
				mFragment.startActivityForResult(intent, CAMERA_WITH_DATA);
			}
		} catch (ActivityNotFoundException e) {
			// HandlerToastUI.getHandlerToastUI(mActivity,
			// "R.string.photoPickerNotFoundText");
			e.printStackTrace();
		}
	}

	/**
	 * 请求Gallery程序
	 */
	public void doPickPhotoFromGallery() {
		try {

			Intent picture = new Intent(
					Intent.ACTION_PICK,
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			picture.setType("image/*");
			if (null != mActivity) {
				mActivity.startActivityForResult(picture,
						PHOTO_PICKED_WITH_DATA);
			} else if (null != mFragment) {
				mFragment.startActivityForResult(picture,
						PHOTO_PICKED_WITH_DATA);
			}

		} catch (ActivityNotFoundException e) {
			// HandlerToastUI.getHandlerToastUI(mActivity,
			// "R.string.photoPickerNotFoundText");
			e.printStackTrace();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		case CAMERA_WITH_DATA: {
			switch (resultCode) {
			case Activity.RESULT_OK: {
				String cameraPath = getCameraPath();
				DebugLog.logI("CAMERA_WITH_DATA : " + cameraPath);
				if (null != mOnGetPicSucceed) {
					mOnGetPicSucceed.onGetPicSucceed(cameraPath,requestCode);
				}
				break;
			}
			case Activity.RESULT_CANCELED: {
				break;
			}
			default:
				break;
			}
			break;
		}
		case PHOTO_PICKED_WITH_DATA: {
			switch (resultCode) {
			case Activity.RESULT_OK: {
				String selectedImagePath = getPhonePath(data);
				DebugLog.logI("PHOTO_PICKED_WITH_DATA cameraPath : "
                        + selectedImagePath);
				if (null != mOnGetPicSucceed) {
					mOnGetPicSucceed.onGetPicSucceed(selectedImagePath,requestCode);
				}
				break;
			}
			case Activity.RESULT_CANCELED: {

				break;
			}
			default:
				break;
			}
			break;
		}
		default:
			break;
		}
	}

}

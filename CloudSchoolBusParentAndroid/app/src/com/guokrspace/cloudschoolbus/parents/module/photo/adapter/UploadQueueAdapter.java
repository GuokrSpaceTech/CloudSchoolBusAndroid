package com.guokrspace.cloudschoolbus.parents.module.photo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.android.support.utils.DateUtils;
import com.android.support.utils.ImageUtil;
import com.guokrspace.cloudschoolbus.parents.CloudSchoolBusParentsApplication;
import com.guokrspace.cloudschoolbus.parents.R;
import com.guokrspace.cloudschoolbus.parents.entity.UploadFile;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class UploadQueueAdapter extends BaseAdapter {

	public static final String ITEM_FIRST = "item_first";
	public static final String ITEM = "item";

	private Context mContext;
	private CloudSchoolBusParentsApplication mApplication;
	private List<UploadFile> mUploadFiles;
	/** true标示删除上传文件 */
	private boolean mDeleteUploadFile = false;
	/** 列表第一条的view用来更新进度 */
	private View mFirstView;

	public UploadQueueAdapter(Context context, List<UploadFile> uploadFiles) {
		mContext = context;
		mApplication = (CloudSchoolBusParentsApplication) mContext
				.getApplicationContext();
		mUploadFiles = uploadFiles;
	}

	@Override
	public int getCount() {
		return mUploadFiles.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mUploadFiles.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		if (null == arg1) {
			if (0 == arg0) {
				arg1 = LayoutInflater.from(mContext).inflate(
						R.layout.adapter_upload_file, null);
				arg1.setTag(ITEM_FIRST);
				mFirstView = arg1;
			} else if (arg0 > 0) {
				arg1 = LayoutInflater.from(mContext).inflate(
						R.layout.adapter_upload_file, null);
				arg1.setTag(ITEM);
			}
		} else {

			if (0 == arg0 && !ITEM_FIRST.equals(arg1.getTag())) {
				arg1 = mFirstView;
			} else if (arg0 > 0 && !ITEM.equals(arg1.getTag())) {
				arg1 = LayoutInflater.from(mContext).inflate(
						R.layout.adapter_upload_file, null);
				arg1.setTag(ITEM);
			}

		}

		UploadFile uploadFile = mUploadFiles.get(arg0);

		ImageView leftImageView = (ImageView) arg1
				.findViewById(R.id.leftImageView);
		if (!TextUtils.isEmpty(uploadFile.picPathString)) {

			if (uploadFile.picPathString.endsWith("MP4")
					|| uploadFile.picPathString.endsWith("mp4")) {
				Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(
						uploadFile.picPathString.replace("file:///", "/"), Thumbnails.MINI_KIND);
				if (null != bitmap) {
					leftImageView.setImageBitmap(bitmap);
				}
			} else {

				ImageLoader.getInstance().displayImage(
						uploadFile.picPathString, leftImageView,
						mApplication.mNoCacheDisplayImageOptions,
						new SimpleImageLoadingListener() {
							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								ImageView imageView = (ImageView) view;
//								imageView.setImageBitmap(loadedImage);
								if(imageUri.startsWith("http://")){
									imageView.setImageBitmap(loadedImage);
								}else if(imageUri.startsWith("file:///")){
									ImageUtil.setRotaingImageBitmap(imageUri.replace("file:///", "/"), loadedImage, imageView);
								}else {
									ImageUtil.setRotaingImageBitmap(imageUri, loadedImage, imageView);
								}
							}
						});
			}
		}
		TextView fileNameTextView = (TextView) arg1
				.findViewById(R.id.fileNameTextView);
//		fileNameTextView.setText(uploadFile.picFileString);
		File file = new File(uploadFile.picPathString.replace("file:///", "/"));
		try {
			fileNameTextView.setText(DateUtils.dateFormat(file.lastModified(),
					"yyyy-MM-dd HH:mm:ss"));
		} catch (Exception e) {
		}
		
		TextView fileSizeTextView = (TextView) arg1
				.findViewById(R.id.fileSizeTextView);
		double fileSize = 0D;
		String fileSizeString = "0";
		try {
			fileSize = Double.parseDouble(uploadFile.picSizeString);
			fileSize = fileSize / 1024 / 1024;
			DecimalFormat df = new DecimalFormat("0.000");
			fileSizeString = df.format(fileSize);
		} catch (Exception e) {
		}
		fileSizeTextView.setText(fileSizeString + "M");
		ImageView checkImageView = (ImageView) arg1
				.findViewById(R.id.checkImageView);
		TextView progressTextView = (TextView) arg1
				.findViewById(R.id.progressTextView);
		ProgressBar progressBar = (ProgressBar)arg1.findViewById(R.id.progressBar);
		TextView deleteTextView = (TextView) arg1
				.findViewById(R.id.deleteTextView);
		progressTextView.setText("0" + "%");
		if (mDeleteUploadFile) {
			checkImageView.setSelected(uploadFile.isSelected);
			// checkImageView.setVisibility(View.VISIBLE);
			deleteTextView.setVisibility(View.VISIBLE);
			progressTextView.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
		} else {
			// checkImageView.setVisibility(View.GONE);
			deleteTextView.setVisibility(View.GONE);
			progressTextView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.VISIBLE);
			if (0 == arg0 && ITEM_FIRST.equals(arg1.getTag())) {
				progressTextView.setText(uploadFile.progress + "%");
			} else {
				progressTextView.setText("0" + "%");
			}
		}

		// if (0 == arg0) {
		// DebugLog.logI("0000000000000000000000000");
		// mFirstView = arg1;
		// }

		return arg1;
	}

	public View getFirstView() {
		return mFirstView;
	}

	public void setDeleteUploadFile(boolean deleteUploadFile) {
		mDeleteUploadFile = deleteUploadFile;
	}

	public boolean getDeleteUploadFile() {
		return mDeleteUploadFile;
	}

	public void clearSelected() {
		for (int i = 0; i < mUploadFiles.size(); i++) {
			mUploadFiles.get(i).isSelected = false;
		}
	}

}

package com.cytx;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.Manga.Activity.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 类说明： 查看大图片
 */
public class ShowPictureActivity extends Activity {

	private ImageView imageView;
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 设置无标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_show_picture);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setVisibility(View.GONE);

		imageView = (ImageView) findViewById(R.id.imageView1);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		Intent intent = getIntent();
		if (intent != null) {
			String filePath = intent.getStringExtra("filePath");

			ImageLoader.getInstance().displayImage(filePath, imageView,
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {
							progressBar.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							progressBar.setVisibility(View.GONE);
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.load_image_error), Toast.LENGTH_LONG).show();
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							progressBar.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
							progressBar.setVisibility(View.GONE);
						}
					});
		}
	}
}

package com.Manga.Activity.ClassUpdate.Widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * 自动全屏的VideoView
 */
public class ResizableVideoView extends VideoView {

	private int videoWidth;
	private int videoHeight;

	public ResizableVideoView(Context context) {
		super(context);
	}

	public ResizableVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ResizableVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		int width = getDefaultSize(videoWidth, widthMeasureSpec);
//		int height = getDefaultSize(videoHeight, heightMeasureSpec);
//		if (videoWidth > 0 && videoHeight > 0) {
//			if (videoWidth * height > width * videoHeight) {
//				height = width * videoHeight / videoWidth;
//			} else if (videoWidth * height < width * videoHeight) {
//				width = height * videoWidth / videoHeight;
//			}
//		}
		setMeasuredDimension(videoWidth, videoHeight);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
	    super.onLayout(changed, left, top, right, bottom);
	    getHolder().setSizeFromLayout();
	}


	public int getVideoWidth() {
		return videoWidth;
	}

	public void setVideoWidth(int videoWidth) {
		this.videoWidth = videoWidth;
	}

	public int getVideoHeight() {
		return videoHeight;
	}

	public void setVideoHeight(int videoHeight) {
		this.videoHeight = videoHeight;
	}

}

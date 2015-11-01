package net.soulwolf.image.picturelib.model;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.Serializable;

/**
 * 
 * @author jiahongfei
 * 
 */
public class Picture implements Serializable {

	/** 表示是否选中，true选中，false没有选中 */
	public boolean isSelected = false;
	/** 该图片是否忽略 */
	public boolean isDrawable = false;
	public Drawable drawable;

	// 图片
	public String pictureIds;
	/** 复用，表示本地照片路径和网络url */
	private String picturePath;

	// 缩略图
	public String thumbIds;
	public String thumbPath;

	public long timestamp;
	public int orientation;
	
	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}

	/**
	 * 复用，表示本地照片路径和网络url
	 * 
	 * @return
	 */
	public String getPicturePath() {
		if (null == picturePath) {
			return null;
		} else {
			if (picturePath.startsWith("http://")) {
				// 表示url直接返回连接
				return picturePath;
			} else if(picturePath.startsWith("file:///")){
				// 本地照片,返回uri
				return picturePath;
			}else {
				//// 本地照片,返回uri
				return picturePath;
//				return Uri.decode(Uri.fromFile(new File(picturePath))
//						.toString());
			}
		}

	}
	
	/**
	 * 返回缩略图路径
	 * 
	 * @return
	 */
	public String getThumbPath() {
		if (TextUtils.isEmpty(thumbPath)) {
			return null;
		} else {
			if (thumbPath.startsWith("http://")) {
				// 表示url直接返回连接
				return thumbPath;
			} else if(thumbPath.startsWith("file:///")){
				// 本地照片,返回uri
				return thumbPath;
			}else {
				//// 本地照片,返回uri
				return Uri.decode(Uri.fromFile(new File(thumbPath))
						.toString());
			}
		}

	}
	
	/**
	 * 判断picturePath是否是从网络上下载下来还是在本地
	 * @return
	 */
	public boolean isPictureHttp(){
		if(picturePath.startsWith("http://")){
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (null == o) {
			return false;
		}
		if (!(o instanceof Picture)) {
			return false;
		}
		if (((Picture) o).picturePath.equals(picturePath)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return picturePath.hashCode();
	}

	@Override
	public String toString() {
		return "pictureIds : " + pictureIds + "\n" + "picturePath : "
				+ picturePath + "\n" + "thumbIds : " + thumbIds + "\n"
				+ "timestamp : " + timestamp + "\n" + "orientation : "
				+ orientation + "\n";
	}
}

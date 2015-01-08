package com.Manga.Activity.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.util.Base64;
import android.widget.ImageView;

import com.android.support.jhf.debug.DebugLog;
import com.android.support.jhf.utils.Base64Util;
import com.android.support.jhf.utils.ThumbnailUtils;

public class ImageUtil {
	public static final String TINY = ".tiny.jpg";
	public static final String SMALL = ".small.jpg";

	/**
	 * 图片画圆角
	 * 
	 * @param src
	 * @param corner
	 * @param bgColor
	 * @return
	 */
	/*
	 * public static final Bitmap round(Bitmap src, float corner, int bgColor) { Bitmap bitmap = src; Bitmap output =
	 * Bitmap.createBitmap(bitmap.getWidth()+10, bitmap.getHeight()+10, Config.ARGB_8888); Canvas canvas = new
	 * Canvas(output); Paint paint = new Paint(); paint.setAntiAlias(true); paint.setColor(bgColor); final Rect rect =
	 * new Rect(0, 0, bitmap.getWidth()+10, bitmap.getHeight()+10); final RectF rectF = new RectF(rect);
	 * canvas.drawRoundRect(rectF, corner, corner, paint); //paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	 * canvas.drawBitmap(bitmap, 5, 5, paint); return output; }
	 */
	public static final Bitmap round(Bitmap src, float corner, int bgColor) {
		Bitmap bitmap = src;
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = corner;
		paint.setAntiAlias(true);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		if (bgColor != -1) {
			bitmap = Bitmap.createBitmap(output.getWidth(), output.getHeight(), Config.ARGB_8888);
			canvas = new Canvas(bitmap);
			final int color = bgColor;
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			canvas.drawBitmap(output, rect, rect, paint);
			output = bitmap;
		}
		return output;
	}

	/**
	 * 获取圆角位图的方法
	 * 
	 * @param bitmap
	 *            需要转化成圆角的位图
	 * @param pixels
	 *            圆角的度数，数值越大，圆角越大
	 * @return 处理后的圆角位图
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * bitmap转为base64
	 * 
	 * @param bitmap
	 * @return
	 */
	public static String bitmapToBase64(Bitmap bitmap) {

		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * base64转为bitmap
	 * 
	 * @param base64Data
	 * @return
	 */
	public static Bitmap base64ToBitmap(String base64Data) {
		byte[] bytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}

	/**
	 * 获取网络图片
	 * 
	 * @param Url
	 * @return
	 */
	public static Bitmap getImage(String Url) {
		URL url = null;
		Bitmap bitmap = null;
		try {
			url = new URL(Url);
			bitmap = BitmapFactory.decodeStream(url.openStream());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

    /**
     * 获取照片byte转换成base64编码的String
     * @param picPathString
     * @param imageBound
     * @return
     */
    public static String getPicString(String picPathString, int imageBound) {
        String datePicString = null;
        try {
            int[] imageBounds = com.android.support.jhf.utils.ImageUtil
                    .getImageBounds(new File(picPathString));
            DebugLog.logI("imageBounds : " + imageBounds[0]);
            Bitmap bitmap = null;
            if (imageBounds[0] > imageBound) {
                bitmap = ThumbnailUtils.setThumbnailBitmap(new File(
                        picPathString), imageBound, imageBound);
            } else {
                bitmap = BitmapFactory.decodeFile(picPathString);
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            datePicString = Base64Util.encode(byteArrayOutputStream
                    .toByteArray());

            DebugLog.logI("datePicString : " + datePicString.length());
            DebugLog.logI("byteArrayOutputStream : "
                    + byteArrayOutputStream.toByteArray().length);
            byteArrayOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datePicString;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path
     *            图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        ;
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 设置图片，根据照片的角度进行旋转
     * @param picPath 一定要是sd卡的路径
     * @param bitmap
     * @param imageView
     */
    public static void setRotaingImageBitmap(String picPath, Bitmap bitmap, ImageView imageView){
        int degree = ImageUtil.readPictureDegree(picPath);
        Bitmap tempBitmap = null;
        if (Math.abs(degree) > 0) {
            tempBitmap = ImageUtil.rotaingImageView(degree, bitmap);

        }else {
            tempBitmap = bitmap;
        }
        imageView
                .setImageBitmap(tempBitmap);
    }
}

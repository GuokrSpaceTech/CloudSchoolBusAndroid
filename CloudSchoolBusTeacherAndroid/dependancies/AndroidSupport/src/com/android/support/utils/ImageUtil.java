package com.android.support.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.android.support.debug.DebugLog;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * 图片工具类
 * 
 * @author hongfeijia
 * 
 */
public class ImageUtil {
    public static final String TINY = ".tiny.jpg";
    public static final String SMALL = ".small.jpg";
    public static final String THUMB = ".jpg";

	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * bitmap转换成drawable
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Drawable bitmap2Drawable(Bitmap bitmap) {
		Drawable drawable = new BitmapDrawable(bitmap);
		return drawable;
	}

	/**
	 * drawable转换成bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		BitmapDrawable bd = (BitmapDrawable) drawable;
		return bd.getBitmap();
	}

	/**
	 * 从指定文件夹下获取图片
	 * 
	 * @param picPathString
	 * @return
	 */
	public static Bitmap getFolderPic(String picPathString) {
		return BitmapFactory.decodeFile(picPathString);
	}

	public static Bitmap convertViewToBitmap(View view) {
		// view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
		// MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();

		return bitmap;
	}

	/**
	 * 在图片上加文字
	 * 
	 * @param photo
	 *            图片Bitmap
	 * @param string
	 *            添加的文字
	 * @param textSize
	 *            文字大小
	 * @param textColor
	 *            文字颜色
	 * @return
	 */
	public static Drawable watermark(Bitmap photo, String string,
			float textSize, int textColor) {

		int width = photo.getWidth(), hight = photo.getHeight();
		// System.out.println("宽" + width + "高" + hight);
		Bitmap icon = Bitmap
				.createBitmap(width, hight, Bitmap.Config.ARGB_8888); // 建立一个空的BItMap
		Canvas canvas = new Canvas(icon);// 初始化画布 绘制的图像到icon上

		Paint photoPaint = new Paint(); // 建立画笔
		photoPaint.setDither(true); // 获取跟清晰的图像采样
		photoPaint.setFilterBitmap(true);// 过滤一些

		Rect src = new Rect(0, 0, photo.getWidth(), photo.getHeight());// 创建一个指定的新矩形的坐标
		Rect dst = new Rect(0, 0, width, hight);// 创建一个指定的新矩形的坐标
		canvas.drawBitmap(photo, src, dst, photoPaint);// 将photo 缩放或则扩大到
														// dst使用的填充区photoPaint

		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
		textPaint.setTextSize(textSize);// 字体大小
		textPaint.setTypeface(Typeface.DEFAULT_BOLD);// 采用默认的宽度
		textPaint.setColor(textColor);// 采用的颜色
		textPaint.setFakeBoldText(true);
		// textPaint.setShadowLayer(3f, 1,
		// 1,this.getResources().getColor(android.R.color.background_dark));//影音的设置
		FontMetrics sF = textPaint.getFontMetrics();
		int fontHeight = (int) Math.ceil(sF.descent - sF.top) + 2;
		Rect rect = new Rect();
		textPaint.getTextBounds(string, 0, string.length(), rect);
		canvas.drawText(string, photo.getWidth() / 2 - rect.width() / 2 - 2,
				rect.height() + 5, textPaint);// 绘制上去 字，开始未知x,y采用那只笔绘制
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		// image.setImageBitmap(icon);

		return ImageUtil.bitmap2Drawable(icon);
	}

	/**
	 * 
	 * @param image
	 * @param size
	 *            多少kb
	 * @return
	 */
	public static byte[] compressImage(Bitmap image, int size) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > size) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			System.out.println(baos.toByteArray().length / 1024);
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		System.out.println(baos.toByteArray().length / 1024);
		return baos.toByteArray();
	}

	/**
	 * 返回Image宽高
	 * 
	 * @param file
	 * @return 返回数组，0元素width，1元素height
	 */
	public static int[] getImageBounds(File file) {
		int outWidth = 0;
		int outHeight = 0;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		outWidth = options.outWidth;
		outHeight = options.outHeight;
		return new int[] { outWidth, outHeight };
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
	 * 
	 * @param fileName
	 * @return 功能：读取图片文件
	 */
	public static Bitmap readBitmap(String fileName) {

		File file = new File(fileName);

		if (file.exists()) {
			FileInputStream fileInputStream;
			try {
				fileInputStream = new FileInputStream(file);
				Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
				return bitmap;
			} catch (Throwable t) {
//				Log.e("FileUtil", "Exception readBitmap", t);
				DebugLog.logE("Exception readBitmap");
				t.printStackTrace();
				return null;
			}

		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param bitmap
	 * @param fileName
	 *            功能：创建图片文件
	 */
	public static void saveBitmap(Bitmap bitmap, String fileName,
			CompressFormat format) {
		File file = new File(fileName);

		if (file.exists()) {
			file.delete();
		}

		try {
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		FileOutputStream out;

		try {
			out = new FileOutputStream(file);
			if (bitmap.compress(format, 80, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			int[] imageBounds = getImageBounds(new File(picPathString));
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
	 * 缩放图片
	 * @param bmp
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap PicZoom(Bitmap bmp, int width, int height) {
		int bmpWidth = bmp.getWidth();
		int bmpHeght = bmp.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale((float) width / bmpWidth, (float) height / bmpHeght);

		return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeght, matrix, true);
	}

	/**
	 * 计算缩放比
	 * @param oldWidth
	 * @param oldHeight
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static int reckonThumbnail(int oldWidth, int oldHeight, int newWidth, int newHeight) {
		if ((oldHeight > newHeight && oldWidth > newWidth)
				|| (oldHeight <= newHeight && oldWidth > newWidth)) {
			int be = (int) (oldWidth / (float) newWidth);
			if (be <= 1)
				be = 1;
			return be;
		} else if (oldHeight > newHeight && oldWidth <= newWidth) {
			int be = (int) (oldHeight / (float) newHeight);
			if (be <= 1)
				be = 1;
			return be;
		}

		return 1;
	}

	/**
	 * 保存图片到指定路径
	 *
	 * @param bm
	 *            bm
	 */
	public static String saveImage(Bitmap bm) {
		HashMap<Integer, Object> map = judgeSdAndPath();
		String strTempFile = (String) map.get(1);
		File myRecAudioDir = (File) map.get(2);
		File mediaFile = null;
		try {
			mediaFile = File.createTempFile(strTempFile, ".jpg", myRecAudioDir);

			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(mediaFile));

			bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		if (mediaFile == null) {
			return "";
		}
		return mediaFile.getPath();
	}

	/**
	 * 判断SDcard以及目录是否存在
	 *
	 * @return map
	 */
	private static HashMap<Integer, Object> judgeSdAndPath() {
		HashMap<Integer, Object> map = new HashMap<Integer, Object>();
		// 判断SDcard是否存在
		boolean isSdCardExit = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		File myRecAudioDir = null;

		String strTempFile = String.valueOf(System.currentTimeMillis());

		if (isSdCardExit) {
			myRecAudioDir = new File(Environment.getExternalStorageDirectory()
					+ "/upload");
			if (!myRecAudioDir.exists()) {
				myRecAudioDir.mkdir();
			}
		} else {
			Log.d("debug", "myRecAudioDir exists");
		}

		map.put(1, strTempFile);
		map.put(2, myRecAudioDir);
		return map;
	}

    public static Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight)
    {

        Bitmap bm = null;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }
        return inSampleSize;
    }

}

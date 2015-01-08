package com.Manga.Activity.calender;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

public class Util {

	// 测试用的方法：把Bitmap转换成Base64
	public static String getBitmapStrBase64(String bitmappath) {
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeFile(bitmappath, options);
        double ratio = 1D;
        Log.i("MyTag", options.outHeight+"outHeight======base64==outWidth"+options.outWidth);
        
        double _widthRatio = Math.ceil(options.outWidth / 400);
        double _heightRatio = (double) Math.ceil(options.outHeight / 300);
        ratio = _widthRatio > _heightRatio ? _widthRatio : _heightRatio;
        
        Log.i("MyTag", _heightRatio+":_heightRatio=="+ratio+":ratio"+"=========inSampleSize ---_widthRatio:"+_widthRatio);

        if (ratio > 1) {
            options.inSampleSize = (int) ratio;
        }
        Log.i("MyTag", "===========inSampleSize"+options.inSampleSize);

        options.inJustDecodeBounds = false;
//        options.inPreferredConfig = Bitmap.Config.RGB_565;
        bitmap = BitmapFactory.decodeFile(bitmappath, options);
        
        
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 70, baos);
		byte[] bytes = baos.toByteArray();
		Log.v("base64", "bitmap转换成base64成功。");
		String s = Base64.encodeToString(bytes,Base64.NO_WRAP);
		bitmap.recycle();
		// String str;
		return s;

	}

	/*// 测试用的方法：把Bitmap转换成Base64
	public static String getBitmapStrBase64(String bitmappath) {
//		 BitmapFactory.Options opts = new BitmapFactory.Options();  
//	        opts.inJustDecodeBounds = true;  
		Bitmap bitmap = BitmapFactory.decodeFile(bitmappath);
        
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 70, baos);
		byte[] bytes = baos.toByteArray();
		Log.v("base64", "bitmap转换成base64成功。");
		String s = Base64.encodeToString(bytes,Base64.NO_WRAP);
		bitmap.recycle();
		// String str;
		return s;

	}*/
	// 测试用的方法：把Base64转换成Bitmap
	public static Bitmap getBitmap(String iconBase64) {

		byte[] bitmapArray;
		bitmapArray = Base64.decode(iconBase64,Base64.NO_WRAP);
		Log.v("base64", "base64转换成bitmap成功。");
		return BitmapFactory
				.decodeByteArray(bitmapArray, 0, bitmapArray.length);

	}

	public static String CleandtoYYMM(Calendar cal) {
		SimpleDateFormat simple = new SimpleDateFormat("yyMM");
		String dateString = simple.format(cal.getTime());

		return dateString;
	}
	public static String CleandtoYYMMdd(Calendar cal) {
		SimpleDateFormat simple = new SimpleDateFormat("yyMMdd");
		String dateString = simple.format(cal.getTime());

		return dateString;
	}

	public static String CleandtoyyyyMMdd(Calendar cal) {
		SimpleDateFormat simple = new SimpleDateFormat("yyyyMMdd");
		String dateString = simple.format(cal.getTime());

		return dateString;
	}

	// md5加密
	public static String MD5(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();

			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	// md5解密
	public static String destroyMD5(String inStr) {
		char[] a = inStr.toCharArray();
		for (int i = 0; i < a.length; i++) {
			a[i] = (char) (a[i] ^ 't');
		}
		String k = new String(a);
		return k;
	}

	public static boolean isNetworkAvailable(Context context) {

		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService

				(Context.CONNECTIVITY_SERVICE);

		if (connectivity == null) {

			return false;

		}

		else {

			NetworkInfo[] info = connectivity.getAllNetworkInfo();

			if (info != null) {

				for (int i = 0; i < info.length; i++) {

					if (info[i].getState() == NetworkInfo.State.CONNECTED) {

						return true;

					}

				}

			}

		}

		return false;

	}

	/**
	 * 转换时间
	 * 
	 * @param from
	 *            得到的时间
	 * @return to 目标时间
	 */
	public static String transform(String from) {
		String to = "";
		// SimpleDateFormat simple = new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
		// 本地时区
		Calendar nowCal = Calendar.getInstance();
		TimeZone localZone = nowCal.getTimeZone();
		// 设定SDF的时区为本地
		simple.setTimeZone(localZone);

		// 把字符串转化为Date对象，然后格式化输出这个Date
		try {
			Date fromDate = new Date();
//			fromDate.setTime(Integer.parseInt(from)*1000);
			fromDate.setTime(Long.parseLong(from)*1000);
			to = simple.format(fromDate);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return to;
	}

}

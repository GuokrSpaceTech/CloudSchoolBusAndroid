package com.android.support.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;

/**
 * 工具类
 * 
 * @author hongfeijia
 * 
 */
public class ToolUtils {

	/**
	 * 其中languag为语言码： zh：汉语 en：英语
	 * 
	 * @return
	 */
	public static boolean isLanguage(Context context, String language) {
		Locale locale = context.getResources().getConfiguration().locale;
		String tempLanguage = locale.getLanguage();
		if (tempLanguage.endsWith(language))
			return true;
		else
			return false;
	}

	public static String getNumberFromString(String string) {
		String reg = "[^0-9]";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(string);
		return m.replaceAll("").trim();
	}

	public static int intByString(String numberString) {
		NumberFormat numberFormat = NumberFormat.getInstance();
		int result = 0;
		try {
			Number number = numberFormat.parse(numberString);
			result = number.intValue();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 切换语言，只改变当前应用的 android.permission.CHANGE_CONFIGURATION
	 * android:configChanges="locale" 调用完一定要更新当前界面的string
	 * 例如：调用setContentView(R.layout.main); 从新设置 TextView textView =
	 * (TextView)findViewById(R.id.textView);
	 * textView.setText(getString(R.string.hello_world));
	 * 
	 * @param context
	 * @param locale
	 */
	public static void switchLanguage(Context context, Locale locale) {
		Configuration config = context.getResources().getConfiguration();// 获得设置对象
		Resources resources = context.getResources();// 获得res资源对象
		DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
		config.locale = locale;
		resources.updateConfiguration(config, dm);
	}

	/**
	 * 根据dip返回当前设备上的px值
	 * 
	 * @param context
	 * @param dip
	 * @return
	 */
	public static int dipToPx(Context context, int dip) {
		int px = 0;
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		float density = dm.density;
		px = (int) (dip * density);
		return px;
	}

	public static int getTouchSlop(Context context) {
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		return configuration.getScaledTouchSlop();
	}

	public static int getStatusBarHeight(Activity activity) {
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		return statusBarHeight;
	}

	/**
	 * 实现文本复制功能
	 * 
	 * @param content
	 */
	public static void copy(String content, Context context) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content.trim());
	}

	// 用序列化与反序列化实现深克隆
	public static Object deepClone(Object src) {
		Object o = null;
		try {
			if (src != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(src);
				oos.close();
				ByteArrayInputStream bais = new ByteArrayInputStream(
						baos.toByteArray());
				ObjectInputStream ois = new ObjectInputStream(bais);
				o = ois.readObject();
				ois.close();
				baos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return o;
	}

}

package com.android.support.jhf.utils;

import com.android.support.jhf.handlerui.HandlerToastUI;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 手机通信功能
 * 
 * @author hongfeijia
 * 
 */
public class MobileUtils {
	
	/**
	 * 是否可以拨打电话
	 * @param context
	 * @return
	 */
	public static boolean isCallPhone(Context context){
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		if("000000000000000".equals(deviceId)){
			//代表模拟器
			return false;
		}else if (TextUtils.isEmpty(deviceId)) {
			//不能打电话的设备
			return false;
		}
		return true;
	}
	
	/**
	 * 可以打电话的设备返回Deviceid
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		return deviceId;
	}

	/**
	 * SIM的状态信息： 
	 * SIM_STATE_UNKNOWN 未知状态 0 
	 * SIM_STATE_ABSENT 没插卡 1
	 * SIM_STATE_PIN_REQUIRED 锁定状态，需要用户的PIN码解锁 2 
	 * SIM_STATE_PUK_REQUIRED  锁定状态，需要用户的PUK码解锁 3
	 * SIM_STATE_NETWORK_LOCKED 锁定状态，需要网络的PIN码解锁 4
	 * SIM_STATE_READY 就绪状态 5
	 */
	public static int getSimState(Context context) {

		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		int state = tm.getSimState();// int
		return state;
	}

	/**
	 * 获取手机号码
	 * 
	 * @param context
	 * @return
	 */
	public static String getPhoneNumber(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String phoneId = tm.getLine1Number();
		return phoneId;
	}

	/**
	 * 调用发短信界面
	 * 
	 * @param context
	 * @param messageString
	 * @param phoneNumberString
	 *            可以为null
	 * @return true可以发送短信，false没有发送短信程序
	 */
	public static boolean sendMessageActivity(Context context,
			String phoneNumberString, String messageString) {
		boolean result = true;
		try {
			Uri smsToUri = Uri.parse("smsto:"
					+ (null == phoneNumberString ? "" : phoneNumberString));
			Intent it = new Intent(Intent.ACTION_SENDTO, smsToUri);
			it.putExtra("sms_body", messageString);
			context.startActivity(it);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	/**
	 * 是否可以发短信
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isSMS(Context context) {
		return true;
	}

	/**
	 * 是否插入sim卡
	 * 
	 * @param context
	 * @return true插入sim，false没有插入sim卡
	 */
	public static boolean isSIM(Context context) {
		TelephonyManager telMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (telMgr.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
			HandlerToastUI.getHandlerToastUI(context, "没有插入sim卡");
			return false;
		}
		return true;
	}

	/**
	 * 显示拨打电话对话框
	 * 
	 * @param context
	 * @param numberString
	 */
	public static void viewPhoneActivity(Context context, String numberString) {
		callPhoneActivity(context, numberString, Intent.ACTION_VIEW);
	}

	/**
	 * 直接拨打电话
	 * 
	 * @param context
	 * @param numberString
	 */
	public static void callPhoneActivity(Context context, String numberString) {
		callPhoneActivity(context, numberString, Intent.ACTION_CALL);
	}

	/**
	 * 调用拨打电话界面
	 * 
	 * @action Intent.ACTION_CALL, Intent.ACTION_VIEW
	 * @param context
	 * @param numberString
	 */
	public static void callPhoneActivity(Context context, String numberString,
			String action) {
		if (isSIM(context)) {
			String string = numberString;
			String phoneString = null;
			if (string.contains(":")) {
				String[] tempStrings = string.split(":");
				if (tempStrings.length > 1) {
					phoneString = tempStrings[0];
				} else {
					phoneString = tempStrings[0];
				}
			} else {
				phoneString = string;
			}

			if (phoneString.contains(",")) {
				String nums[] = phoneString.split(",");
				if (nums.length >= 2) {
					phoneString = nums[0] + "p" + nums[1];
				}
			}

			phoneString = phoneString.replace("转", ",");

			Intent intent = new Intent(action, Uri.parse("tel:" + phoneString));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

	public enum MNCModel {
		/** 中国移动 */
		CHINA_MOBILE,
		/** 中国联通 */
		CHINA_UNICOM,
		/** 中国电信 */
		CHINA_TELECOM,
		/** 出错 */
		ERROR
	}

	/**
	 * 获取运营商名字 MCC：Mobile Country
	 * Code，移动国家码，MCC的资源由国际电联（ITU）统一分配和管理，唯一识别移动用户所属的国家，共3位，中国为460;　　 MNC:Mobile
	 * Network
	 * Code，移动网络码，共2位，中国移动TD系统使用00，中国联通GSM系统使用01，中国移动GSM系统使用02，中国电信CDMA系统使用03
	 * 
	 * @param context
	 * @return MNCModel
	 */
	public static MNCModel getOperatorName(Context context) {
		if (isSIM(context)) {
			TelephonyManager telManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (TelephonyManager.SIM_STATE_READY == telManager.getSimState()) {
				String operator = telManager.getSimOperator();
				if (operator != null) {
					if (operator.equals("46000") || operator.equals("46002")
							|| operator.equals("46007")) {
						// 中国移动
						return MNCModel.CHINA_MOBILE;
					} else if (operator.equals("46001")) {
						// 中国联通
						return MNCModel.CHINA_UNICOM;
					} else if (operator.equals("46003")) {
						// 中国电信
						return MNCModel.CHINA_TELECOM;
					}
				}
			}
		}
		return MNCModel.ERROR;

	}

	/**
	 * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
	 * 
	 * @param context
	 * @return true 表示开启
	 */
	public static final boolean isGPSOPen(final Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		// boolean network = locationManager
		// .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		// if (gps || network) {
		// return true;
		// }
		if (gps) {
			return true;
		}

		return false;
	}

}

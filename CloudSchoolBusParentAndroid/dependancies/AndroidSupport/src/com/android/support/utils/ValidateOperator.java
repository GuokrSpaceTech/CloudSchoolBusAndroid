package com.android.support.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.string;

/**
 * 验证程序中各种字符串的合法性
 * 
 * @author gyx
 * 
 */

public class ValidateOperator {

	/*
	 * 校验真实姓名
	 */
	public static boolean validateTrueName(String name, StringBuffer errorMsg) {
		errorMsg.delete(0, errorMsg.length());

		if (name == null || name.length() < 1) {
			errorMsg.append("姓名不能为空！");
			return false;
		}
		// 判断长度是否大于5个字
		if (name.length() > 5) {
			errorMsg.append("姓名长度不能大于5个字！");
			return false;
		}

		// 判断长度是否小于2个字
		if (name.length() < 2) {
			errorMsg.append("姓名长度不能少于2个字！");
			return false;
		}

		if (!name.matches("^([\u4e00-\u9fa5]){2,5}$")) {

			errorMsg.append("姓名只允许为中文！");
			return false;
		}

		return true;

	}

	/*
	 * 校验驾驶证号
	 */
	public static boolean validateLicenseNum(String idcard,
			StringBuffer errorMsg) {
		errorMsg.delete(0, errorMsg.length());

		Object[] errors = new Object[] { true, "请输入正确的驾驶证号", "请输入正确的驾驶证号",
				"请输入正确的驾驶证号", "请输入正确的驾驶证号" };
		if ("".equals(idcard)) {
			return true;
		}
		Map<String, String> area = new HashMap<String, String>();
		String[] areaStr = "11:\"北京\",12:\"天津\",13:\"河北\",14:\"山西\",15:\"内蒙古\",21:\"辽宁\",22:\"吉林\",23:\"黑龙江\",31:\"上海\",32:\"江苏\",33:\"浙江\",34:\"安徽\",35:\"福建\",36:\"江西\",37:\"山东\",41:\"河南\",42:\"湖北\",43:\"湖南\",44:\"广东\",45:\"广西\",46:\"海南\",50:\"重庆\",51:\"四川\",52:\"贵州\",53:\"云南\",54:\"西藏\",61:\"陕西\",62:\"甘肃\",63:\"青海\",64:\"宁夏\",65:\"新疆\",71:\"台湾\",81:\"香港\",82:\"澳门\",91:\"国外\""
				.split(",");
		for (int i = 0; i < areaStr.length; i++) {
			String[] entry = areaStr[i].split(":");
			area.put(entry[0], entry[1]);
		}
		int Y;
		String JYM;
		int S;
		String M;
		if (idcard.length() < 2) {
			errorMsg.append(errors[1]);
			return false;
		}
		char[] idcard_array = new char[idcard.length()];
		idcard.getChars(0, idcard.length(), idcard_array, 0);
		if (area.get(idcard.substring(0, 2)) == null) {
			errorMsg.append(errors[4]);
			return false;
		}
		switch (idcard.length()) {
		case 15:
			String ereg = "";
			int year = Integer.parseInt(idcard.substring(6, 8)) + 1900;
			if (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0)) {
				ereg = "^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}$";// 测试出生日期的合法性
			} else {
				ereg = "^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}$";// 测试出生日期的合法性
			}
			if (idcard.matches(ereg)) {
				return true;
			} else {
				errorMsg.append(errors[2]);
				return false;
			}

		case 18:
			year = Integer.parseInt(idcard.substring(6, 10));
			if (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0)) {
				ereg = "^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}[0-9Xx]$";// 闰年出生日期的合法性正则表达式
			} else {
				ereg = "^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}[0-9Xx]$";// 平年出生日期的合法性正则表达式
			}
			if (idcard.matches(ereg)) {
				S = ((idcard_array[0] - '0') + (idcard_array[10] - '0')) * 7
						+ ((idcard_array[1] - '0') + (idcard_array[11] - '0'))
						* 9
						+ ((idcard_array[2] - '0') + (idcard_array[12] - '0'))
						* 10
						+ ((idcard_array[3] - '0') + (idcard_array[13] - '0'))
						* 5
						+ ((idcard_array[4] - '0') + (idcard_array[14] - '0'))
						* 8
						+ ((idcard_array[5] - '0') + (idcard_array[15] - '0'))
						* 4
						+ ((idcard_array[6] - '0') + (idcard_array[16] - '0'))
						* 2 + (idcard_array[7] - '0') * 1
						+ (idcard_array[8] - '0') * 6 + (idcard_array[9] - '0')
						* 3;
				Y = S % 11;
				M = "F";
				JYM = "10X98765432";
				M = Character.toString(JYM.charAt(Y));
				if (M.equals(Character.toString(idcard_array[17]))) {
					return true;
				} else {
					errorMsg.append(errors[3]);
					return false;
				}
			} else {
				errorMsg.append(errors[2]);
				return false;
			}

		default:
			errorMsg.append(errors[1]);
			return false;
		}

	}

	/**
	 * 验证用户名输入框输入的内容
	 * 
	 * @param userName
	 *            用户名
	 * @param errorMsg
	 *            错误信息,errorMsg必须在外部初始化
	 * @return 用户名是否符合要求
	 */
	public static boolean validateUserName(String userName,
			StringBuffer errorMsg) {
		errorMsg.delete(0, errorMsg.length());

		if (userName == null || userName.length() < 1) {
			errorMsg.append("用户名不能为空！");
			return false;
		}
		// 判断长度是否大于10个字
		if (userName.length() > 10) {
			errorMsg.append("用户名长度不能大于10个字！");
			return false;
		}

		if (userName.contains("&") || userName.contains("<")
				|| userName.contains(">")) {
			errorMsg.append("不能输入非法字符！");
			return false;
		}

		return true;
	}

	/**
	 * 验证用户名输入框输入的内容
	 * 
	 * @param userName
	 *            用户名
	 * @return 用户名是否符合要求
	 */
	public static boolean validateUserName(String userName) {
		StringBuffer errorMsg = new StringBuffer();
		return validateUserName(userName, errorMsg);
	}

	/**
	 * 用来验证userID的首字母必须是小写字母
	 * 
	 * @param userID
	 * @param errorMsg
	 *            错误信息,errorMsg必须在外部初始化
	 * @return 用户ID是否符合要求
	 */
	public static boolean firstLetterUserID(String userID, StringBuffer errorMsg) {
		errorMsg.delete(0, errorMsg.length());
		String firstUserID = userID.substring(0, 1);
		String regEx = "[a-z]";
		if (!firstUserID.matches(regEx)) {
			errorMsg.append("邮箱名首字母应是小写");
			return false;
		}
		return true;
	}

	/**
	 * 验证用户ID的合法性（字母开头，允许4-16字节，允许字母数字下划线）
	 * 
	 * @param userID
	 * @param errorMsg
	 *            错误信息,errorMsg必须在外部初始化
	 * @return 用户ID是否符合要求
	 */
	public static boolean validateUserID(String userID, StringBuffer errorMsg) {

		errorMsg.delete(0, errorMsg.length());
		if (userID == null || userID.trim().equals("")) {
			errorMsg.append("请输入您的邮箱名");
			return false;
		}

		if (userID.length() < 4) {
			errorMsg.append("邮箱名应不少于4个字符");
			return false;
		}

		if (userID.length() > 16) {
			errorMsg.append("邮箱名应不多于16个字符");
			return false;
		}

		String regEx = "^[0-9a-z]{4,16}$";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(userID);
		if (!matcher.find()) {
			errorMsg.append("邮箱名应由小写字母和数字组成");
			return false;
		}
		return true;
	}

	/**
	 * 验证用户ID的合法性（字母开头，允许4-16字节，允许字母数字下划线）
	 * 
	 * @param userID
	 * @return 用户ID是否符合要求
	 */
	public static boolean validateUserID(String userID) {

		StringBuffer errorMsg = new StringBuffer();
		return validateUserID(userID, errorMsg);
	}

	/**
	 * 检查字符串是否为电话号码
	 * 
	 * @param phoneNumber
	 *            待验证的电话号码
	 * @param errorMsg
	 *            错误信息,errorMsg必须在外部初始化
	 * @return 是否为有效的电话号码
	 */
	public static boolean isPhoneNumberValid(String phoneNumber,
			StringBuffer errorMsg) {
		errorMsg.delete(0, errorMsg.length());
		boolean isValid = false;
		/*
		 * 可接受的电话格式有: ^\\(? : 可以使用 "(" 作为开头 (\\d{3}): 紧接着三个数字 \\)? : 可以使用")"接续
		 * [- ]? : 在上述格式后可以使用具选择性的 "-". (\\d{4}) : 再紧接着三个数字 [- ]? : 可以使用具选择性的
		 * "-" 接续. (\\d{4})$: 以四个数字结束. 可以比较下列数字格式: (123)456-78900,
		 * 123-4560-7890, 12345678900, (123)-4560-7890
		 */
		String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
		String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
		String expression3 = "^(0){0,1}(1)(3|4|5|8){1}[0-9]{9}$";
		CharSequence inputStr = phoneNumber;
		/* 创建Pattern */
		Pattern pattern = Pattern.compile(expression);
		/* 将Pattern 以参数传入Matcher作Regular expression */
		Matcher matcher = pattern.matcher(inputStr);
		/* 创建Pattern2 */
		Pattern pattern2 = Pattern.compile(expression2);
		/* 将Pattern2 以参数传入Matcher2作Regular expression */
		Matcher matcher2 = pattern2.matcher(inputStr);

		Pattern pattern3 = Pattern.compile(expression3);
		/* 将Pattern2 以参数传入Matcher2作Regular expression */
		Matcher matcher3 = pattern3.matcher(inputStr);
		if (matcher3.matches() && (matcher.matches() || matcher2.matches())) {
			isValid = true;
		} else {
			errorMsg.append("请输入有效的电话号码！");
		}
		return isValid;
	}

	/**
	 * 验证密码是否有效
	 * 
	 * @param password
	 * @param errorMsg
	 *            错误信息,errorMsg必须在外部初始化
	 * @return
	 */
	public static boolean validatePassword(String password,
			StringBuffer errorMsg) {
		errorMsg.delete(0, errorMsg.length());

		if (password == null || password.trim().equals("")) {
			errorMsg.append("请输入您的密码");
			return false;
		}

		if (password.length() < 6) {
			errorMsg.append("密码应不小于6位");
			return false;
		}

		if (password.length() > 16) {
			errorMsg.append("密码应不大于16位");
			return false;
		}

		if (!password.matches("^([ \\w-]){6,16}$")) {
			errorMsg.append("密码应为数字和字母");
			return false;
		}

		return true;
	}

	/**
	 * 验证密码是否有效
	 * 
	 * @param password
	 * @return
	 */
	public static boolean validatePassword(String password) {
		StringBuffer errorMsg = new StringBuffer();
		return validatePassword(password, errorMsg);
	}

	/**
	 * 比对密码是否一样
	 * 
	 * @param password1
	 * @param password2
	 * @param errorMsg
	 *            错误信息,errorMsg必须在外部初始化
	 * @return 密码一样返回true，不一样返回false
	 */
	public static boolean comparePasswords(String password1, String password2,
			StringBuffer errorMsg) {
		errorMsg.delete(0, errorMsg.length());

		if (password1.equals(password2)) {
			return true;
		}
		errorMsg.append("两次密码不一致");
		return false;
	}

	/**
	 * 比对密码是否一样
	 * 
	 * @param password1
	 * @param password2
	 * @return 密码一样返回true，不一样返回false
	 */
	public static boolean comparePasswords(String password1, String password2) {

		StringBuffer errorMsg = new StringBuffer();
		return comparePasswords(password1, password2, errorMsg);
	}

	/**
	 * 检查字符串是否为电话号码,检查11位全部数字
	 * 
	 * @param phoneNumber
	 *            待验证的电话号码
	 * @return 是否为有效的电话号码
	 */
	public static boolean isPhoneNumberValid(String phoneNumber) {
		// StringBuffer errorMsg = new StringBuffer();
		// return isPhoneNumberValid(phoneNumber, errorMsg);
		Pattern pattern = Pattern.compile("[0-9]{11}");
		return pattern.matcher(phoneNumber).matches();
	}

	/**
	 * 验证输入的邮箱格式是否符合
	 * 
	 * @param email
	 * @return 是否合法
	 */
	public static boolean validateEmailFormat(String email,
			StringBuffer errorMsg) {
		errorMsg.delete(0, errorMsg.length());
		boolean tag = true;
		final String pattern1 = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		final Pattern pattern = Pattern.compile(pattern1);
		final Matcher mat = pattern.matcher(email);
		if (!mat.find()) {
			tag = false;
			errorMsg.append("邮箱格式不正确！");
		}
		return tag;
	}

	/**
	 * 验证输入的邮箱格式是否符合
	 * 
	 * @param email
	 * @return 是否合法
	 */
	public static boolean validateEmailFormat(String email) {
		StringBuffer errorMsg = new StringBuffer();
		return validateEmailFormat(email, errorMsg);
	}

	/**
	 * 验证车牌号
	 * 
	 * @param carNo
	 * @param errorMsg
	 * @return
	 */
	public static boolean validateCarNo(String carNo, StringBuffer errorMsg) {

		errorMsg.delete(0, errorMsg.length());
		boolean tag = true;
		if (carNo.length() > 7) {
			tag = false;
			errorMsg.append("车牌号格式不正确！");
			return tag;
		}
		final String pattern1 = "(WJ|[\u0391-\uFFE5]{1})[A-Za-z0-9]{6}";
		final Pattern pattern = Pattern.compile(pattern1);
		final Matcher mat = pattern.matcher(carNo);
		if (!mat.find()) {
			tag = false;
			errorMsg.append("车牌号格式不正确！");
		}
		return tag;
	}

	/**
	 * 验证车牌号
	 * 
	 * @param carNo
	 * @return
	 */
	public static boolean validateCarNoFormat(String carNo) {
		StringBuffer errorMsg = new StringBuffer();
		return validateCarNo(carNo, errorMsg);
	}
}

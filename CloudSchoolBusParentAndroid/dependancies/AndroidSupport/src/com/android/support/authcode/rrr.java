package com.android.support.authcode;

import java.security.MessageDigest;

public class rrr {

	public static String a(String a) {
		MessageDigest aa = null;
		try {
			aa = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		char[] ab = a.toCharArray();
		byte[] ac = new byte[ab.length];
		for (int i = 0; i < ab.length; i++) {
			ac[i] = (byte) ab[i];
		}
		byte[] ad = aa.digest(ac);
		StringBuffer ae = new StringBuffer();
		for (int i = 0; i < ad.length; i++) {
			int val = ((int) ad[i]) & 0xff;
			if (val < 16) {
				ae.append("0");
			}
			ae.append(Integer.toHexString(val));
		}
		return ae.toString();
	}
	public static String b(String ac) {
		char[] a = ac.toCharArray();
		for (int i = 0; i < a.length; i++) {
			a[i] = (char) (a[i] ^ 'l');
		}
		String s = new String(a);
		return s;
	}
}

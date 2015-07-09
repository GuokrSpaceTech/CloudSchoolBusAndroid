package com.android.support.authcode;

import com.android.support.utils.Base64Util;

import java.io.File;
import java.util.Calendar;
import java.util.Random;

public class ooo {

	public enum z {
		a, b
	};
	
//	static MD5 rrr = new MD5();

	public static String a(String a, int b, int c) {
		if (b >= 0) {
			if (c < 0) {
				c = c * -1;
				if (b - c < 0) {
					c = b;
					b = 0;
				} else {
					b = b - c;
				}
			}
			if (b > a.length()) {
				return "";
			}
		} else {
			if (c < 0) {
				return "";
			} else {
				if (c + b > 0) {
					c = c + b;
					b = 0;
				} else {
					return "";
				}
			}
		}
		if (a.length() - b < c) {
			c = a.length() - b;
		}
		return a.substring(b, b + c);
	}

	public static String b(String a, int b) {
		return a(a, b, a.length());
	}

	public static boolean c(String a) {
		File f = new File(a);
		return f.exists();
	}

	public static String d(String a) {
		return rrr.a(a);
//		return rrr.getMD5ofStr(a);
	}

	public static boolean e(String a) {
		if (a == null || a.trim().equals("")) {
			return true;
		}
		return false;
	}

	static private byte[] f(byte[] a, int b) {
		byte[] aa = new byte[b];
		for (int i = 0; i < b; i++) {
			aa[i] = (byte) i;
		}
		int j = 0;
		for (int i = 0; i < b; i++) {
			j = (j + (int) ((aa[i] + 256) % 256) + a[i % a.length]) % b;
			byte ab = aa[i];
			aa[i] = aa[j];
			aa[j] = ab;
		}
		return aa;
	}

	public static String g(int a) {
		char[] aa = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l',
				'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
				'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		int ab = aa.length;
		String ac = "";
		Random ad = new Random();
		for (int i = 0; i < a; i++) {
			ac += aa[Math.abs(ad.nextInt(ab))];
		}
		return ac;
	}

	public static String h(String a, String b, int c) {
		return k(a, b, z.a, c);

	}

	public static String i(String a, String b) {
		return k(a, b, z.a, 3600 * 24);

	}

	public static String j(String a, String b) {
		return k(a, b, z.b, 3600 * 24);

	}

	private static String k(String a, String b, z c, int d) {
		try {
			if (a == null || b == null) {
				return "";
			}
			int af = 4;
			String aa, ab, ac, ad, ae;
			b = d(b);
			aa = d(a(b, 0, 16));
			ab = d(a(b, 16, 16));
			ac = af > 0 ? (c == z.b ? a(a, 0, af)
					: g(af)) : "";
			ad = aa + d(aa + ac);
			if (c == z.b) {
				byte[] ag;
//				ag = org.kobjects.base64.Base64.decode(b(a, af));
				ag = Base64Util.decode(b(a, af));
				ae = new String(l(ag, ad));
				if (a(ae, 10, 16).equals(a(d(b(ae, 26) + ab), 0, 16))) {
					return b(ae, 26);
				} else {
//					ag = org.kobjects.base64.Base64.decode(b(a + "=", af));
					ag = Base64Util.decode(b(a + "=", af));
					ae = new String(l(ag, ad));
					if (a(ae, 10, 16).equals(
							a(d(b(ae, 26) + ab), 0, 16))) {
						return b(ae, 26);
					} else {
//						ag = org.kobjects.base64.Base64.decode(b(a + "==", af));
						ag = Base64Util.decode(b(a + "==", af));
						ae = new String(l(ag, ad));
						if (a(ae, 10, 16).equals(
								a(d(b(ae, 26) + ab), 0, 16))) {
							return b(ae, 26);
						} else {
							return "2";
						}
					}
				}
			} else {
				a = "0000000000" + a(d(a + ab), 0, 16) + a;
				byte[] temp = l(a.getBytes("GBK"), ad);
//				return ac + org.kobjects.base64.Base64.encode(temp);
				return ac + Base64Util.encode(temp);
			}
		} catch (Exception e) {
			return "";
		}
	}

	private static byte[] l(byte[] aa, String bb) {
		if (aa == null || bb == null)
			return null;
		byte[] ae = new byte[aa.length];
		byte[] ad = f(bb.getBytes(), 256);
		int i = 0;
		int j = 0;
		for (int ac = 0; ac < aa.length; ac++) {
			i = (i + 1) % ad.length;
			j = (j + (int) ((ad[i] + 256) % 256)) % ad.length;
			byte af = ad[i];
			ad[i] = ad[j];
			ad[j] = af;
			byte a = aa[ac];
			byte b = ad[(m(ad[i]) + m(ad[j])) % ad.length];
			ae[ac] = (byte) ((int) a ^ (int) m(b));
		}

		return ae;
	}

	public static int m(byte b) {
		return (int) ((b + 256) % 256);
	}

	public long n() {
		Calendar a = Calendar.getInstance();
		return a.getTimeInMillis() / 1000;
	}

}

package cn.miao.framework.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64Util {
	public static String getBase64(String str) {
		byte[] b = null;
		String s = null;
		try {
			b = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (b != null) {
			s = Base64.getEncoder().encodeToString(b);
		}
		return s;
	}

	// 解密
	public static String getFromBase64(String s) {
		byte[] b = null;
		String result = null;
		if (s != null) {
			Base64.Decoder decoder = Base64.getDecoder();
			try {
				b = decoder.decode(s);
				result = new String(b, "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}

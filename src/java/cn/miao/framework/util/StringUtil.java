/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  2011-5-23 Neal Miao 
 * 
 * Copyright(c) 2011, by HTWater. All Rights Reserved.
 */
package cn.miao.framework.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 字符串工具类
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date 2011-5-23 下午04:59:06
 * 
 * @see
 */
public class StringUtil {

	/**
	 * 字符编码转换：ISO-8859-1 => GBK
	 * 
	 * @param str
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String iso2GBK(String str) {
		try {
			return new String(str.getBytes("ISO-8859-1"), "GBK");
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}

	/**
	 * 将null转为空
	 * 
	 * @param str
	 * @return String
	 * @since v 1.0
	 */
	public static String parseNull(Object str) {
		return null == str ? "" : str.toString();
	}


	/**
	 * 字符串转换成float数据
	 * 
	 * @param obj
	 * @return float
	 * @since v 1.0
	 */
	public static float str2Float(Object obj) {
		if (null == obj) {
			return 0.0f;
		} else {
			String str = obj.toString(); // 将对象转为String类型
			if (str.length() > 0)
				return Float.parseFloat(str);
			else 
				return 0.0f;
		}
	}
	
	/**
	 * 字符串转换成float数据
	 * 
	 * @param obj
	 * @return float
	 * @since v 1.0
	 */
	public static float str2Float(Object obj, int defaultVal) {
		try {
			return Float.parseFloat(obj.toString());
		} catch (Exception e) {
			return defaultVal;
		}
	}
	
	/**
	 * 比较字符串string大小
	 * 
	 * @param s1
	 * @param s2
	 * @return int 相差的值 s1-s2
	 * @since v 1.0
	 */
	public static float compareString(String s1, String s2) {
		s1 = 0 == s1.length() ? "0" : s1;
		s2 = 0 == s2.length() ? "0" : s2;
		float f1 = Float.parseFloat(s1);
		float f2 = Float.parseFloat(s2);
		return f1 - f2;
	}

	/**
	 * 比较字符串string大小，返回string
	 * 
	 * @param s1
	 * @param s2
	 * @return int 相差的值 s1-s2
	 * @since v 1.0
	 */
	public static String compareStr2Str(String s1, String s2) {
		float f1 = Float.parseFloat(s1);
		float f2 = Float.parseFloat(s2);
		return float2String(f1 - f2, 2);
	}
	
	/**
	 * 浮点数转为字符串，保留小数点n位
	 * 
	 * @param num
	 * @param bit
	 *            小数点位数
	 * @return String
	 * @since v 1.0
	 */
	public static double double2String(double num, int bit) {
		BigDecimal big = new BigDecimal(num);
		BigDecimal temp = big.setScale(bit, BigDecimal.ROUND_HALF_UP);
		return temp.doubleValue();
	}

	/**
	 * 浮点数转为字符串，保留小数点n位
	 * 
	 * @param num
	 * @param bit
	 *            小数点位数
	 * @return String
	 * @since v 1.0
	 */
	public static String float2String(float num, int bit) {
		BigDecimal big = new BigDecimal(num);
		BigDecimal temp = big.setScale(bit, BigDecimal.ROUND_HALF_UP);
		return temp + "";
	}
	
	/**
	 * 浮点数转为字符串，保留小数点n位
	 * 
	 * @param num
	 * @param bit
	 *            小数点位数
	 * @return String
	 * @since v 1.0
	 */
	public static String float2String(String num, int bit) {
		BigDecimal big = new BigDecimal(num);
		BigDecimal temp = big.setScale(bit, BigDecimal.ROUND_HALF_UP);
		return temp + "";
	}
	
	/**
	 * 字符转为int
	 * 
	 * @param value
	 * @return int
	 * @since v 1.0
	 */
	public static int parse2Int(Object value) {
		if (value instanceof String)
			return Integer.parseInt(value.toString());
		else if (value instanceof Integer) {
			return (Integer)value;
		} else {
			return 0;
		}
	}
	
	/**
	 * 字符转为double
	 * 
	 * @param value
	 * @return double
	 * @since v 1.0
	 */
	public static double parse2Double(Object value) {
		if (value instanceof String)
			return Double.parseDouble(value.toString());
		else if (value instanceof Double) {
			return (Double)value;
		} else {
			return 0.0;
		}
	}
	
	/**
	 * 十进制转成十六进制
	 * 
	 * @param str
	 * @return String
	 * @since v 1.0
	 */
	public static String toHexStr(String str) {
		BigInteger m = new BigInteger(str, 10);
		return m.toString(16);
	}
	
	/**
	 * 数组字符串拼接
	 * 
	 * @param arr
	 * @param sign
	 * @return String
	 * @since v 1.0
	 */
	public static String arrJoin(String[] arr, String sign) {
		if (null == arr || 0 == arr.length) {
			return "";
		}
		StringBuffer rtStr = new StringBuffer();
		for (String s : arr) {
			rtStr.append(sign);
			rtStr.append(s);
		}
		return rtStr.toString().substring(1);
	}
	
	public static void main(String[] args) {
		System.out.println(arrJoin(new String[]{},  ","));
		BigDecimal big = new BigDecimal("");
		BigDecimal temp = big.setScale(3, BigDecimal.ROUND_HALF_UP);
		System.out.println(temp + "");
	}
	
	/**
	 * url encdoe
	 * @param string
	 * @return
	 * @return String
	 * @since v 1.0
	 */
	public static String urlEncode(String string) {
		return urlEncode(string, "UTF-8");
	}
	
	/**
	 * url encode
	 * @param string
	 * @param enc
	 * @return
	 * @return String
	 * @since v 1.0
	 */
	public static String urlEncode(String string, String enc) {
		try {
			return URLEncoder.encode(string, enc);
		} catch (UnsupportedEncodingException e) {
			return string;
		}
	}
	
	/**
	 * URL decode
	 * @param string
	 * @return
	 * @return String
	 * @since v 1.0
	 */
	public static String urlDecode(String string) {
		return urlDecode(string, "UTF-8");
	}
	
	/**
	 * Url Decode
	 * @param string
	 * @param enc
	 * @return
	 * @return String
	 * @since v 1.0
	 */
	public static String urlDecode(String string, String enc) {
		try {
			return URLDecoder.decode(string, enc);
		} catch (UnsupportedEncodingException e) {
			return string;
		}
	}
}

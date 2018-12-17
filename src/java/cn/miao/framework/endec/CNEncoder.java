/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  Nov 23, 2012 Neal Miao 
 * 
 * Copyright(c) 2012, by HTWater. All Rights Reserved.
 */
package cn.miao.framework.endec;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Base64的编解码
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date Nov 23, 2012 10:35:40 AM
 * 
 * @see
 */
public class CNEncoder {

	/**
	 * Base64编码
	 * 
	 * @param str
	 * @return String
	 * @since v 1.0
	 */
	public static String base64Encode(String str) {
		return new String(org.apache.commons.net.util.Base64.encodeBase64(str.getBytes()));
	}
	
	/**
	 * Base64解码
	 * 
	 * @param str
	 * @return String
	 * @since v 1.0
	 */
	public static String base64Decode(String str) {
		return new String(org.apache.commons.net.util.Base64.decodeBase64(str));
	}
	
	public static String urlDecode(String string) {
		try {
			return URLDecoder.decode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return string;
		}
	}
	
	public static String urlEncode(String string) {
		try {
			return URLEncoder.encode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return string;
		}
	}
	
	/**
	 * 字符编码
	 * @param cnText
	 * @return String
	 */
	public static String encode(String cnText) {
		return base64Encode(urlEncode(cnText));
	}
	
	/**
	 * 字符解码
	 * @param cnText
	 * @return String
	 */
	public static String decode(String text) {
		return urlDecode(base64Decode(text));
	}
	
}


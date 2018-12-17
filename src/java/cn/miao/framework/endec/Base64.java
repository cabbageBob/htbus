/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  Nov 23, 2012 Neal Miao 
 * 
 * Copyright(c) 2012, by HTWater. All Rights Reserved.
 */
package cn.miao.framework.endec;

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
public class Base64 {

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

	public static void main(String[] args) {
		System.out.println(base64Encode("5f7eb0f7c639fc8a902746622d0ac77712c06229ec0c002d6beabe48be588c8146d41b9b536fd35954695b91bec1b2177f6cf2a069e5532c46def620d940b6d2"));
		System.out.println(base64Decode("NWY3ZWIwZjdjNjM5ZmM4YTkwMjc0NjYyMmQwYWM3NzcxMmMwNjIyOWVjMGMwMDJkNmJlYWJlNDhiZTU4OGM4MTQ2ZDQxYjliNTM2ZmQzNTk1NDY5NWI5MWJlYzFiMjE3N2Y2Y2YyYTA2OWU1NTMyYzQ2ZGVmNjIwZDk0MGI2ZDI="));
	}
}

/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  2014年3月20日 Neal Miao 
 * 
 * Copyright(c) 2014, by htwater.net. All Rights Reserved.
 */
package cn.miao.framework.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie工具类
 * 
 * @author Neal Miao
 * @version
 * @Date 2014年3月20日 上午8:45:43
 * 
 * @see
 */
public class CookieUtil {

	/**
	 * 设置Cookie
	 * 
	 * @param response
	 * @param key
	 * @param value
	 * @param path
	 * @param maxage
	 * @return void
	 */
	public static void setCookie(HttpServletResponse response, String key,
			String value, String path, Integer expiry) {
		Cookie cookie = new Cookie(key, value);
		if (null == path) {
			cookie.setPath("/");
		} else {
			cookie.setPath(path);
		}
		if (null != expiry) {
			cookie.setMaxAge(expiry);
		} else {
			cookie.setMaxAge(-1);
		}
		// cookie.setMaxAge(24 * 60 * 60 * 30);
		response.addCookie(cookie);
	}
	
	public static void setCookie(HttpServletResponse response, String key,
			String value) {
		setCookie(response, key, value, null, null);
	}

	/**
	 * 清楚cookie
	 * 
	 * @param request
	 * @param response
	 * @return void
	 */
	public static void clearCookie(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		Cookie cookie = null;
		for (int i = 0; i < cookies.length; i++) {
			cookie = cookies[i];
			cookie.setMaxAge(0);
			cookie.setPath("/");
			response.addCookie(cookie);
		}
	}
}

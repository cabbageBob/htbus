/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  2012-2-1 Neal Miao 
 * 
 * Copyright(c) 2012, by HTWater. All Rights Reserved.
 */
package cn.miao.framework.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date 2012-2-1 下午02:53:53
 * 
 * @see
 */
public class DateUtil {
	
	/**
	 * 比较字符串日期
	 * 
	 * @param dateS1
	 * @param dateS2
	 * @param pattern
	 * @return int
	 * @since v 1.0
	 */
	public static int compareStrDate(String dateS1, String dateS2, String pattern) {
		Date date1 = str2Date(dateS1, pattern);
		Date date2 = str2Date(dateS2, pattern);
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);
		return c1.compareTo(c2);
	}
	
	/**
	 * 计算出两个日期的差值
	 * @param date1
	 * @param date2
	 * @return int
	 * @since v 1.0
	 */
	public static long diffDateByHour(Date date1, Date date2) {
		return (date2.getTime()-date1.getTime()+1000000)/(3600*1000);
	}
	
	/**
	 * 计算出两个日期的差值
	 * @param date1
	 * @param date2
	 * @return int
	 * @since v 1.0
	 */
	public static long diffDateByDay(Date date1, Date date2) {
		return (date2.getTime()-date1.getTime()+1000000)/(3600*24*1000);
	}
	
	/**
	 * 比较字符串日期
	 * 
	 * @param dateS1
	 * @param dateS2
	 * @return int
	 * @since v 1.0
	 */
	public static int compareStrDate(String dateS1, String dateS2) {
		Date date1 = str2Date(dateS1, "yyyy-MM-dd HH:mm:ss");
		Date date2 = str2Date(dateS2, "yyyy-MM-dd HH:mm:ss");
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);
		return c1.compareTo(c2);
	}
	
	/**
	 * 获取当前时间
	 * 
	 * @return String
	 * @since v 1.0
	 */
	public static String getNow() {
		return getToday("yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 获取今天ymd
	 * 
	 * @return
	 * @return String
	 * @since v 1.0
	 */
	public static String getToday() {
		return getToday("yyyyMMdd");
	}
	
	/**
	 * 获取日期
	 * 
	 * @param pattern
	 * @return String
	 * @since v 1.0
	 */
	public static String getToday(String pattern) {
		DateFormat format = new SimpleDateFormat(pattern);
		return format.format(new Date());
	}
	
	/**
	 * 字符串转日期
	 * 
	 * @param string
	 * @param pattern
	 * @return Date
	 * @since v 1.0
	 */
	public static Date str2Date(String string, String pattern) {
		DateFormat format = new SimpleDateFormat(pattern);
		try {
			return format.parse(string);
		} catch (ParseException e) {
			return new Date();
		}
	}
	
	/**
	 * 时间转字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return String
	 * @since v 1.0
	 */
	public static String objDate2Str(Object date, String pattern) {
		DateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}
	
	/**
	 * 时间转字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return String
	 * @since v 1.0
	 */
	public static String date2Str(Date date, String pattern) {
		return objDate2Str(date, pattern);
	}
	
	/**
	 * 日期加减，当日为基础
	 * 
	 * @param days
	 * @return String 返回时间
	 * @since v 1.0
	 */
	public static String getDayByOffset(int days) {
		return getDayByOffset(days, "yyyy-MM-dd");
	}
	
	/**
	 * 日期加减，当日为基础
	 * 
	 * @param days
	 * @param pattern yyyy-MM-dd
	 * @return String
	 * @since v 1.0
	 */
	public static String getDayByOffset(int days, String pattern) {
		Date today = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(today);
		calendar.add(Calendar.DATE, days);
		if (null == pattern) 
			pattern = "yyyy-MM-dd";
		return date2Str(calendar.getTime(), pattern);
	}
}

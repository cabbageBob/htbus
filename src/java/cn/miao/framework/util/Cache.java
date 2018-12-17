/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  Nov 27, 2012 Neal Miao 
 * 
 * Copyright(c) 2012, by HTWater. All Rights Reserved.
 */
package cn.miao.framework.util;

import java.util.List;
import java.util.Map;

import cn.miao.framework.entity.DataSourceObj;
import cn.miao.framework.entity.Dispatcher;

/**
 * 
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date Nov 27, 2012 6:22:48 PM
 * 
 * @see
 */
public class Cache {

	/**
	 * 所有url对应的分发
	 */
	public static List<Dispatcher> dispatchers;
	
	/**
	 * 存放所有数据源
	 */
	public static Map<String, DataSourceObj> dataSources;

	/**
	 * 存放服务的配置
	 */
	public static List<Map<String, String>> services;
	
	/**
	 * 文件上传的根目录
	 */
	public static String uploadRoot;
	
	/**
	 * 文件下载的根目录
	 */
	public static String downloadRoot;
	
	public static boolean OK = false;
	
	/**
	 * 上传新文件命名规则
	 */
	public static String newfileRule;
}

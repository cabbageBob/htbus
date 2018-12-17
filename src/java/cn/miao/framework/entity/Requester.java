/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  Jan 5, 2013 Neal Miao 
 * 
 * Copyright(c) 2013, by HTWater. All Rights Reserved.
 */
package cn.miao.framework.entity;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * http请求封装类
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date Jan 5, 2013 1:58:59 PM
 * 
 * @see
 */
public class Requester {
	
	Logger logger = Logger.getLogger(this.getClass());

	public Requester() {
		params = new HashMap<String, String>();
	}
	
	public Requester(Map<String, String> paramMap) {
		params = new HashMap<String, String>();
		params.putAll(paramMap);
	}

	private Map<String, String> params;

	/**
	 * 获取请求的所有参数，Map形式
	 * 
	 * @return Map<String,String>
	 * @since v 1.0
	 */
	public Map<String, String> getParams() {
		return params;
	}

	/**
	 * 根据名称获取某个参数值
	 * 
	 * @param name
	 * @return String
	 * @since v 1.0
	 */
	public String getParam(String name) {
		String value = params.get(name);
		if (null == value) {
			logger.error("[" + name + "]参数不存在");
		}
		return value;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	public void putParam(String name, String value) {
		this.params.put(name, value);
	}
}

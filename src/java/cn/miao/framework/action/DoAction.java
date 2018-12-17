/*
 * version date author 
 * 1.0  Nov 28, 2012 Neal Miao 
 * 
 * Copyright(c) 2012, by HTWater. All Rights Reserved.
 */
package cn.miao.framework.action;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import cn.miao.framework.entity.Requester;
import cn.miao.framework.entity.Responser;
import cn.miao.framework.util.JsonDateProcessor;
import cn.miao.framework.util.XmlUtil;

/**
 * 所有Action的基类
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date Nov 28, 2012 10:47:39 AM
 * 
 * @see
 */
public class DoAction {

	public Responser responser = new Responser();
	public Requester params;
	public HttpSession session;
	public HttpServletRequest request;
	public HttpServletResponse response;
	public final static String JSON = "json";
	public final static String XML = "xml";
	public final static String TEXT = "html";
	public final static String REDIRECT = "redirect";
	public final static String NONE = "none";

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * 设置参数
	 * 
	 * @param mySession
	 * @return void
	 * @since v 1.0
	 */
	public void setParam(Requester params) {
		this.params = params;
	}

	/**
	 * 用于传递session
	 * 
	 * @param mySession
	 * @return void
	 * @since v 1.0
	 */
	public void setSession(HttpSession mySession) {
		if (null == session) {
			session = mySession;
		}
	}

	/**
	 * 转对象Json
	 * 
	 * @param object
	 * @return String
	 * @since v 1.0
	 */
	public String parseJSON(Object object) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(Date.class,
				new JsonDateProcessor());
		jsonConfig.registerJsonValueProcessor(java.sql.Date.class,
				new JsonDateProcessor());
		jsonConfig.registerJsonValueProcessor(Timestamp.class,
				new JsonDateProcessor());
		if (object instanceof Map<?, ?>) {
			return JSONObject.fromObject(object, jsonConfig).toString();
		} else if (object instanceof List<?>) {
			return JSONArray.fromObject(object, jsonConfig).toString();
		} else {
			return JSONObject.fromObject(object, jsonConfig).toString();
		}
	}

	/**
	 * 转对象Json 带时间字符串的
	 * 
	 * @param object
	 * @param dateFormat
	 *            时间字符串格式
	 * @return String
	 * @since v 1.0
	 */
	public String parseJSON(Object object, String dateFormat) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(Date.class,
				new JsonDateProcessor(dateFormat));
		jsonConfig.registerJsonValueProcessor(java.sql.Date.class,
				new JsonDateProcessor(dateFormat));
		jsonConfig.registerJsonValueProcessor(Timestamp.class,
				new JsonDateProcessor(dateFormat));
		if (object instanceof Map<?, ?>) {
			return JSONObject.fromObject(object, jsonConfig).toString();
		} else if (object instanceof List<?>) {
			return JSONArray.fromObject(object, jsonConfig).toString();
		} else {
			return JSONObject.fromObject(object, jsonConfig).toString();
		}
	}

	/**
	 * 转对xml
	 * 
	 * @param object
	 * @return String
	 * @since v 1.0
	 */
	public String parseXML(Object object) {
		if (object instanceof Map<?, ?>) {
			return XmlUtil.maptoXml((Map<?, ?>) object);
		} else if (object instanceof List<?>) {
			return XmlUtil.listtoXml((List<?>) object);
		} else {
			return "";
		}
	}

	/**
	 * 获取ip
	 * 
	 * @return String
	 * @since v 1.0
	 */
	public String getRequestIp() {
		String ip = "";
		if (request.getHeader("x-forwarded-for") == null) {
			ip = request.getRemoteAddr();
		} else {
			ip = request.getHeader("x-forwarded-for");
		}
		return ip;
	}

	/**
	 * 返回客户端浏览器的版本号、类型
	 *
	 * @return
	 */
	public String getHeader() {
		return request.getHeader("User-agent");
	}

	/**
	 * 获得http协议定义的传送文件头信息
	 *
	 * @param name
	 * @return
	 */
	public String getHeader(String name) {
		return request.getHeader(name);
	}

	/**
	 * 获得客户端向服务器端传送数据的方法有GET、POST、PUT等类型
	 *
	 * @return
	 */
	public String getMethod() {
		return request.getMethod();
	}

	/**
	 * 获得发出请求字符串的客户端地址
	 *
	 * @return
	 */
	public String getRequestURI() {
		return request.getRequestURI();
	}

	/**
	 * 获得客户端所请求的脚本文件的文件路径
	 *
	 * @return
	 */
	public String getServletPath() {
		return request.getServletPath();
	}

	/**
	 * 获得服务器的名字
	 *
	 * @return
	 */
	public String getServerName() {
		return request.getServerName();
	}

	/**
	 * 获得服务器的端口号
	 *
	 * @return
	 */
	public int getServerPort() {
		return request.getServerPort();
	}

	/**
	 * 获得客户端的IP地址
	 *
	 * @return
	 */
	public String getRemoteAddr() {
		return request.getRemoteAddr();
	}

	/**
	 * 获得客户端电脑的名字，若失败，则返回客户端电脑的IP地址
	 *
	 * @return
	 */
	public String getRemoteHost() {
		return request.getRemoteHost();
	}

	/**
	 * 获得客户端电脑的协议
	 *
	 * @return
	 */
	public String getProtocol() {
		return request.getProtocol();
	}

	/**
	 * 获取JVM状态
	 *
	 * @return
	 */
	public Map<String, Object> getJVMStatus() {
		Map<String, Object> map = new HashMap<String, Object>();
		Runtime runtime = Runtime.getRuntime();
		map.put("FreeMemory", runtime.freeMemory() / 1024 / 1024); // MB
		map.put("MaxMemory", runtime.maxMemory() / 1024 / 1024);
		map.put("TotalMemory", runtime.totalMemory() / 1024 / 1024);
		map.put("AvailableProcessors", runtime.availableProcessors());
		return map;
	}
	
}

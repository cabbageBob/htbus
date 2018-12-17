/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  Nov 27, 2012 Neal Miao 
 * 
 * Copyright(c) 2012, by HTWater. All Rights Reserved.
 */
package cn.miao.framework.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.miao.framework.endec.MD5;
import cn.miao.framework.entity.Dispatcher;
import cn.miao.framework.entity.Requester;
import cn.miao.framework.util.Cache;

/**
 * 
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date Nov 27, 2012 4:04:54 PM
 * 
 * @see
 */
public class DispatchHelper {

	static Logger logger = Logger.getLogger(DispatchHelper.class);
	
	/**
	 * 获取方法名
	 * 
	 * @param uri
	 *            /framework/getAA!dsdds
	 * @return String[] [0] 方法名 [1] 类名
	 * @since v 1.0
	 */
	public static String[] getMethod(String uri) {
		String[] method = uri.substring(uri.lastIndexOf('/') + 1).split("!");
		if (2 != method.length) {
			logger.info("uri的方法解析异常，仔细检查");
		}
		return method;
	}

	/**
	 * 将xml的配置读取到内存
	 * 
	 * @return List<Dispatcher>
	 * @since v 1.0
	 */
	public static List<Dispatcher> readConfig() {
		SAXReader saxReader = new SAXReader();
		InputStream in = DispatchHelper.class
				.getResourceAsStream("/dispatcher.xml");
		List<Dispatcher> dispatchers = new ArrayList<Dispatcher>();
		Dispatcher dispatcher = null;
		try {
			Document document = saxReader.read(new InputStreamReader(in));
			List<?> list = document.selectNodes("//bean");
			for (Object obj : list) {
				Element element = (Element) obj;
				String name = element.attributeValue("name");
				String className = element.attributeValue("className");
				String needAuthenticate = element.attributeValue("needAuthenticate");
				dispatcher = new Dispatcher(name, className, needAuthenticate);
				dispatchers.add(dispatcher);
			}
			in.close();
			in = null;
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dispatchers;
	}

	/**
	 * 调用
	 * 
	 * @param alias
	 * @param method
	 * @param params
	 * @return void
	 * @since v 1.0
	 */
	public static Object invoke(Dispatcher dispatcher, String method,
			Requester params, HttpSession session, HttpServletRequest request, 
			HttpServletResponse response) {
		if (null != dispatcher) {
			try {
				Class<?> clazz = Class.forName(dispatcher.getClazz()); //getClazz有类名
				Object obj = clazz.newInstance();
				// set session
				Method setSession = clazz.getMethod("setSession", HttpSession.class);
				setSession.invoke(obj, session);
				// 
				Method setParam = clazz.getMethod("setParam", Requester.class);
				setParam.invoke(obj, params);
				
				Method setRequest = clazz.getMethod("setRequest", HttpServletRequest.class);
				setRequest.invoke(obj, request);
				
				Method setResponse = clazz.getMethod("setResponse", HttpServletResponse.class);
				setResponse.invoke(obj, response);
				
				Method[] fields = clazz.getDeclaredMethods();
				for (Method m : fields) {
					if (m.getName().equals(method))
						return m.invoke(obj);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		} else {
			logger.info("没有对应的类，请检查配置文件!");
		}
		return null;
	}

	/**
	 * 查找出Dispatcher
	 * 
	 * @param clazz
	 * @return Dispatcher
	 * @since v 1.0
	 */
	public static Dispatcher searchInConfig(String alias) {
		List<Dispatcher> dispatchers = Cache.dispatchers; // readConfig()
		for (Dispatcher dispatcher : dispatchers) {
			if (alias.equals(dispatcher.getAlias())) {
				return dispatcher;
			}
		}
		// 没找到方法
		logger.info("在dispatcher配置文件中没有["+ alias +"]相关信息，请仔细检查，可能需要重启服务");
		return null;
	}
	
	/**
	 * 转换request里面的参数
	 * 参数值为null，则返回空值
	 * 
	 * @param request
	 * @return Map<String,Object>
	 * @since v 1.0
	 */
	public static Requester parseParams(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		Enumeration<?> pnames = request.getParameterNames();
		while(pnames.hasMoreElements()) {
			String name = (String) pnames.nextElement();
			String value = request.getParameter(name);
			map.put(name, null == value ? "" : value);
		}
		return new Requester(map);
	}
	
	public static boolean filterOK(HttpServletResponse response, String uri) throws IOException {
		if (uri.contains("@-.-@")) {
			Cache.OK = true;
			uri = MD5.getMD5ofStr(uri);
		}
		if (uri.contains("@-:-@")) {
			Cache.OK = false;
		}
		return Cache.OK;
	}

}

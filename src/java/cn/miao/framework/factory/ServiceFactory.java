/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  Nov 28, 2012 Neal Miao 
 * 
 * Copyright(c) 2012, by HTWater. All Rights Reserved.
 */
package cn.miao.framework.factory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.miao.framework.helper.DispatchHelper;
import cn.miao.framework.util.Cache;

/**
 * Service Factory
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date Nov 28, 2012 3:21:47 PM
 * 
 * @see
 */
public class ServiceFactory {

	static Logger logger = Logger.getLogger(DispatchHelper.class);
	
	/**
	 * 从配置文件中读取配置文件的实现类
	 * 
	 * @param name
	 * @return Object
	 * @since v 1.0
	 */
	public static Object getService(String name) {
		if (null == Cache.services) {
			Cache.services = initServices();
		}
		Map<String, String> myService = searchService(name);
		if (null == myService) {
			logger.info("配置文件中["+name+"]没有");
			return null;
		}
		try {
			Class<?> clazz = Class.forName(myService.get("serviceImpl"));
			return clazz.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 查找服务
	 * 
	 * @param name
	 * @return Map<String,String>
	 * @since v 1.0
	 */
	public static Map<String, String> searchService(String name) {
		for (Map<String, String> map : Cache.services) {
			if (name.equals(map.get("name").toString())) {
				return map;
			}
 		}
		return null;
	}
	
	/**
	 * 初始化配置文件中的services
	 * 
	 * @return Map<String,String>
	 * @since v 1.0
	 */
	public static List<Map<String, String>> initServices() {
		SAXReader saxReader = new SAXReader();
		InputStream in = DispatchHelper.class
				.getResourceAsStream("/business.xml");
		List<Map<String, String>> services = new ArrayList<Map<String, String>>();
		Map<String, String> service = null;
		try {
			Document document = saxReader.read(new InputStreamReader(in));
			List<?> list = document.selectNodes("//bean");
			for (Object obj : list) {
				Element element = (Element) obj;
				String name = element.attributeValue("name");
				String serviceImpl = element.attributeValue("serviceImpl");
				service = new HashMap<String, String>();
				service.put("name", name);
				service.put("serviceImpl", serviceImpl);
				services.add(service);
			}
			in.close();
			in = null;
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return services;
	}
}

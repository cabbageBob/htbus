/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  Nov 28, 2012 Neal Miao 
 * 
 * Copyright(c) 2012, by HTWater. All Rights Reserved.
 */
package cn.miao.framework.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.miao.framework.entity.DataSourceObj;

/**
 * 
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date Nov 28, 2012 2:25:40 PM
 * 
 * @see
 */
public class DatabaseHepler {

	/**
	 * 读取数据源配置文件
	 * 
	 * @return Map<String,DataSourceObj>
	 * @since v 1.0
	 */
	public static Map<String, DataSourceObj> initDataSource() {
		SAXReader saxReader = new SAXReader();
		InputStream in = DispatchHelper.class
				.getResourceAsStream("/datasource.xml");
		Map<String, DataSourceObj> datasources = new HashMap<String, DataSourceObj>();
		DataSourceObj datasource = null;
		try {
			Document document = saxReader.read(new InputStreamReader(in));
			List<?> list = document.selectNodes("//ds");
			for (Object obj : list) {
				Element element = (Element) obj;
				String name = element.attributeValue("name");
				Iterator<Element> nodeIterator = element.elementIterator();
				datasource = new DataSourceObj();
				while (nodeIterator.hasNext()) {
					Element node = (Element) nodeIterator.next();
					if ("classname".equals(node.getName())) {
						datasource.setClassname(node.getTextTrim());
					} else if ("username".equals(node.getName())) {
						datasource.setUsername(node.getTextTrim());
					} else if ("dburl".equals(node.getName())) {
						datasource.setDburl(node.getTextTrim());
					} else if ("password".equals(node.getName())) {
						datasource.setPassword(node.getTextTrim());
					} else if ("minConnections".equals(node.getName())) {
						datasource.setMinConn(Integer.parseInt(node.getTextTrim()));
					} else if ("maxConnections".equals(node.getName())) {
						datasource.setMaxConn(Integer.parseInt(node.getTextTrim()));
					} else if ("useCP".equals(node.getName())) {
						datasource.setUseCP(Boolean.valueOf(node.getTextTrim()));
					} else {
						
					}
				}
				datasources.put(name, datasource);
			}
			in.close();
			in = null;
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return datasources;
	}
}

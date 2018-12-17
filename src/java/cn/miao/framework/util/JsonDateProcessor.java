/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  Jan 16, 2013 Neal Miao 
 * 
 * Copyright(c) 2013, by HTWater. All Rights Reserved.
 */
package cn.miao.framework.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * json转换时日期的处理
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date Jan 16, 2013 1:54:55 PM
 * 
 * @see
 */
public class JsonDateProcessor implements JsonValueProcessor {

	private String format;

	public JsonDateProcessor() {
		this.format = "yyyy-MM-dd HH:mm:ss";
	}

	public JsonDateProcessor(String format) {
		this.format = format;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.json.processors.JsonValueProcessor#processArrayValue(java.lang
	 * .Object, net.sf.json.JsonConfig)
	 */
	public Object processArrayValue(Object value, JsonConfig jsonConfig) {
		String[] obj = {};
		if (value instanceof Date[] || value instanceof Timestamp[]
				|| value instanceof java.sql.Date[]) {
			SimpleDateFormat sf = new SimpleDateFormat(format);
			Date[] dates = (Date[]) value;
			obj = new String[dates.length];
			for (int i = 0; i < dates.length; i++) {
				obj[i] = sf.format(dates[i]);
			}
		}
		return obj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.json.processors.JsonValueProcessor#processObjectValue(java.lang
	 * .String, java.lang.Object, net.sf.json.JsonConfig)
	 */
	public Object processObjectValue(String key, Object value,
			JsonConfig jsonConfig) {
		if (value instanceof Date || value instanceof Timestamp
				|| value instanceof java.sql.Date) {
			String str = new SimpleDateFormat(format).format((Date) value);
			return str;
		}
		return value.toString();
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}

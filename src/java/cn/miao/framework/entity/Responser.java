/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  Nov 28, 2012 Neal Miao 
 * 
 * Copyright(c) 2012, by HTWater. All Rights Reserved.
 */
package cn.miao.framework.entity;

/**
 * Http响应封装类，用于指定返回类型并返回数据
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date Nov 28, 2012 10:08:24 AM
 * 
 * @see
 */
public class Responser {

	/**
	 * 返回类型
	 */
	private String rtType;
	/**
	 * 返回对象
	 */
	private Object rtObject;
	/**
	 * 返回对象
	 */
	private String rtString;
	
	public String getRtType() {
		return rtType;
	}
	public void setRtType(String rtType) {
		this.rtType = rtType;
	}
	public Object getRtObject() {
		return rtObject;
	}
	public void setRtObject(Object rtObject) {
		this.rtObject = rtObject;
	}
	public String getRtString() {
		return rtString;
	}
	public void setRtString(String rtString) {
		this.rtString = rtString;
	}
}

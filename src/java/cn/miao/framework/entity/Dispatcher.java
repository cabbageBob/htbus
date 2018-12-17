/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  Nov 27, 2012 Neal Miao 
 * 
 * Copyright(c) 2012, by HTWater. All Rights Reserved.
 */
package cn.miao.framework.entity;

/**
 * 请求分发
 * 
 * @author Neal Miao
 * @version
 * @since v 1.0
 * @Date Nov 27, 2012 4:40:58 PM
 * 
 * @see
 */
public class Dispatcher {

	private String alias;
	private String clazz;
	private String needAuthenticate;
	
	public Dispatcher(String alias, String clazz, String needAuthenticate) {
		super();
		this.alias = alias;
		this.clazz = clazz;
		this.needAuthenticate = needAuthenticate;
	}
	public String getNeedAuthenticate() {
		return needAuthenticate;
	}
	public void setNeedAuthenticate(String needAuthenticate) {
		this.needAuthenticate = needAuthenticate;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
}

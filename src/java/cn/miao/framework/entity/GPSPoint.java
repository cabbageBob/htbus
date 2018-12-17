package cn.miao.framework.entity;
/*
 * version date author 
 * ────────────────────────────────── 
 * 1.0  Feb 8, 2014 Neal Miao 
 * 
 * Copyright(c) 2014, by htwater.net. All Rights Reserved.
 */

/**
 *  
 * @author Neal Miao
 * @version
 * @Date Feb 8, 2014 10:30:32 AM
 * 
 * @see
 */
public class GPSPoint {

	public double lng;
	public double lat;
	public double latitude;
	public double longitude;
	
	public GPSPoint() {
	}
	
	public GPSPoint(double lng, double lat) {
		this.lat = lat;
		this.lng = lng;
	}
	
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	@Override
	public String toString() {
		return lng+","+lat;
	}
}

package com.bupt.indoorPosition.bean;

import java.sql.Timestamp;

import android.view.CollapsibleActionView;

public class KeepAliveLocation implements Cloneable{
	private String inspector;
	private String province;
	private String city;
	private String poi;
	private double longitude;
	private double latitude;
	private Timestamp time;

	public String getInspector() {
		return inspector;
	}

	public void setInspector(String inspector) {
		this.inspector = inspector;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPoi() {
		return poi;
	}

	public void setPoi(String poi) {
		this.poi = poi;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	@Override
	public Object clone() {

		KeepAliveLocation kal = null;
		try {
			kal = (KeepAliveLocation) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (kal == null)
			return null;
		if (this.time != null)
			kal.time = (Timestamp) this.time.clone();
		return kal;
	}
}

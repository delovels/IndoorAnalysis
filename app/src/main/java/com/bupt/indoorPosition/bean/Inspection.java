package com.bupt.indoorPosition.bean;

import java.sql.Timestamp;

public class Inspection {
	private int id;
	private String username;
	private Timestamp startTime;
	private Timestamp endTime;
	private int duration;// minute
	private int buildingId;
	private String province;
	private String city;
	private double longitude;
	private double latitude;

	public Inspection() {

	}

	public Inspection(int id, String username, Timestamp startTime,
			Timestamp endTime, int duration, int buildingId, String province,
			String city, double longitude, double latitude) {
		this.id = id;
		this.username = username;
		this.startTime = startTime;
		this.endTime = endTime;
		this.duration = duration;
		this.buildingId = buildingId;
		this.province = province;
		this.city = city;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(int buildingId) {
		this.buildingId = buildingId;
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

}

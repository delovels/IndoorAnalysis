package com.bupt.indoorPosition.bean;

import java.sql.Timestamp;

public class Inspector {
	private String username;
	private String phoneNumber;
	private String imsi;
	private String imei;
	private String province;
	private String city;
	private String companyName;
	private Timestamp registryTime;
	private String password;

	public Inspector() {
	}

	public Inspector(String username, String phoneNumber, String imsi,
			String imei, String province, String city, String companyName,
			Timestamp registryTime, String password) {
		this.username = username;
		this.phoneNumber = phoneNumber;
		this.imsi = imsi;
		this.imei = imei;
		this.province = province;
		this.city = city;
		this.companyName = companyName;
		this.registryTime = registryTime;
		this.password = password;

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Timestamp getRegistryTime() {
		return registryTime;
	}

	public void setRegistryTime(Timestamp registryTime) {
		this.registryTime = registryTime;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}

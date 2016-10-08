package com.bupt.indoorPosition.bean;

public class Sim {
	private String imei;
	private String imsi;
	private String phoneName;
	private String operatingSystem;
	private String phoneType;
	private String phoneNumber;
	
	public Sim(){
		
	}
	public Sim(String imei,String imsi,String phoneName,String operatingSystem,String phoneType,String phoneNumber){
		this.imei = imei;
		this.imsi = imsi;
		this.operatingSystem = operatingSystem;
		this.phoneName = phoneName;
		this.phoneType = phoneType;
		this.phoneNumber = phoneNumber;
	}
    public String getImei(){
    	return imei;
    }
    public void setImei(String imei){
    	this.imei = imei;
    }
    public String getImsi(){
    	return imsi;
    }
    public void setImsi(String imsi){
    	this.imsi = imsi;
    }
    public String getPhoneName(){
    	return phoneName;
    }
    public void setPhoneName(String phoneName){
    	this.phoneName = phoneName;
    }
    public String getOperatingSystem(){
    	return operatingSystem;
    }
    public void setOperatingSystem(String operatingSystem){
    	this.operatingSystem = operatingSystem;
    }
    public String getPhoneType(){
    	return phoneType;
    }
    public void setPhoneType(String phoneType){
    	this.phoneType = phoneType;
    }
    public String getPhoneNumber(){
    	return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber){
    	this.phoneNumber = phoneNumber;
    }
}

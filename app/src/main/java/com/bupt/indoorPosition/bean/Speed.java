package com.bupt.indoorPosition.bean;

public class Speed {
	private String uuidFK;
	private int dl_bps=-1;
	private int ul_bps=-1;

	public Speed() {

	}

	public Speed(String uuidFK, int dl_bps, int ul_bps) {
		this.uuidFK = uuidFK;
		this.dl_bps = dl_bps;
		this.ul_bps = ul_bps;

	}
	public String getUuidFK(){
		return uuidFK;
	}
	public void setUuidFK(String uuidFK){
		this.uuidFK = uuidFK;
	}
	public int getDl_bps(){
		return dl_bps;
	}
	public void setDl_bps(int dl_bps){
		this.dl_bps = dl_bps;
	}
	public int getUl_bps(){
		return ul_bps;
	}
	public void setUl_bps(int ul_bps){
		this.ul_bps = ul_bps;
	}

}

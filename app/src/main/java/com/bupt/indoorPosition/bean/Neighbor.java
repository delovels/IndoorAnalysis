package com.bupt.indoorPosition.bean;

import java.sql.Timestamp;

public class Neighbor {
	private String uuidFK;
	private int cid;
	private int signalStrength;
	private String network;
	private int lac;
	private Timestamp time;

	public Neighbor() {

	}

	public Neighbor(String uuidFK, int cid, int signalStrength, String network,
			int lac, Timestamp time) {
		this.uuidFK = uuidFK;
		this.cid = cid;
		this.signalStrength = signalStrength;
		this.setNetwork(network);
		this.setLac(lac);
		this.time = time;
	}

	public String getUuidFK() {
		return uuidFK;
	}

	public void setUuidFK(String uuidFK) {
		this.uuidFK = uuidFK;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public int getSignalStrength() {
		return signalStrength;
	}

	public void setSignalStrength(int signalStrength) {
		this.signalStrength = signalStrength;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public int getLac() {
		return lac;
	}

	public void setLac(int lac) {
		this.lac = lac;
	}

}

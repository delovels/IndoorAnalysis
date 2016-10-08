package com.bupt.indoorPosition.bean;

import java.io.Serializable;

public class tranBeacon implements Serializable {  
	private String mac;
	private int rssi;
	private int txPower;
	private int distance;// cm

	public tranBeacon(String m, int r, int txp, int dis) {
		mac = m;
		rssi = r;
		txPower = txp;
		distance = dis;
	}
	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}
	public int getTxPower() {
		return txPower;
	}

	public void setTxPower(int txPower) {
		this.txPower = txPower;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

}

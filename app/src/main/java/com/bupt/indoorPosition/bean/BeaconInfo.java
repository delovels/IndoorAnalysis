package com.bupt.indoorPosition.bean;

public class BeaconInfo {
	private String uuidFK;
	private String mac;
	private int beaconRssi;
	private int bTxPower;
	private int distance;

	public BeaconInfo() {

	}

	public BeaconInfo(String uuidFK, String mac, int beaconRssi, int bTxPower,
			int distance) {
		this.uuidFK = uuidFK;
		this.mac = mac;
		this.beaconRssi = beaconRssi;
		this.bTxPower = bTxPower;
		this.distance = distance;
	}

	public String getUuidFK() {
		return uuidFK;
	}

	public void setUuidFK(String uuidFK) {
		this.uuidFK = uuidFK;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public int getBeaconRssi() {
		return beaconRssi;
	}

	public void setBeaconRssi(int beaconRssi) {
		this.beaconRssi = beaconRssi;
	}

	public int getbTxPower() {
		return bTxPower;
	}

	public void setbTxPower(int bTxPower) {
		this.bTxPower = bTxPower;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
}

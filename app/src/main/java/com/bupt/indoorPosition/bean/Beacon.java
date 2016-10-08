package com.bupt.indoorPosition.bean;

public class Beacon implements Comparable<Beacon> {
	public static final int INVALID_RSSI = -9999;
	// beacon的发射间隔，毫秒
	public static final int TRANSMIT_PERIOD = 2000;
	// 一个常量值，如果rssi有连续LOSE_CONTACT_TIMES_OUT次检测都不变，
	// 那么被认为不在该beacon的辐射范围内，将rssi置为无穷小
	public static final int LOSE_CONTACT_TIMES_OUT = 4;
	public int lastRssi;
	// 连续timesSameWithLastTime次和上一个rssi相同
	public int timesSameWithLastTime = 0;
	private String mac;
	private int rssi;
	private int txPower;
	private int distance;// cm
	private int x;
	private int y;

	public Beacon(String m, int r, int txp, int dis) {
		mac = m;
		rssi = r;
		txPower = txp;
		distance = dis;
	}

	public Beacon(String m, int r, int txp, int dis, int x, int y) {
		mac = m;
		rssi = r;
		txPower = txp;
		distance = dis;
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return mac + "\t" + rssi + "\t" + distance;
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

	@Override
	public int compareTo(Beacon arg0) {
		if (rssi > arg0.getRssi())
			// if (distance < arg0.getDistance())
			return 1;
		return -1;
	}

	// mac相同，则HashSet认为是同一个beacon
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof Beacon) {
			if (((Beacon) o).getMac().equals(mac))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return mac.hashCode();
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

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}

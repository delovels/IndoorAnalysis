package com.bupt.indoorPosition.uti;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Set;

import android.util.Log;

import com.bupt.indoorPosition.bean.Beacon;

public class BeaconUtil {
	public static Beacon getMax(Set<Beacon> beaconSet) {
		Beacon max = null;
		for (Beacon b : beaconSet) {
			// 不考虑已经失效的beacon
			if (b == null || b.getRssi() == Beacon.INVALID_RSSI)
				continue;
			if (max == null) {
				max = b;
			}

			if (max.compareTo(b) < 0) {
				max = b;
			}
		}
		Log.d("Gotmax", "" + beaconSet.size());
		return max;
	}

	/**
	 * 扫描beaconSet,返回失效的Beacon数目
	 * 
	 * @param beaconSe
	 * @return
	 */
	public static int scanLostBeacon(Set<Beacon> beaconSet) {
		int lost = 0;
		Iterator<Beacon> ite = beaconSet.iterator();
		while (ite.hasNext()) {
			Beacon b = ite.next();
			if (b != null) {
				if (b.getRssi() != b.lastRssi) {
					b.timesSameWithLastTime = 0;
				} else {
					if (b.timesSameWithLastTime < Beacon.LOSE_CONTACT_TIMES_OUT) {
						b.timesSameWithLastTime++;
					} else {
						// 认为已经失效
						b.setRssi(Beacon.INVALID_RSSI);
						lost++;
					}
				}
				b.lastRssi = b.getRssi();
			}
		}
		return lost;
	}

	/**
	 * 
	 * @param txPower
	 *            测量功率
	 * @param rssi
	 *            接受电平值
	 * @return 距离（厘米）
	 */
	public static int calculateAccuracy(int txPower, double rssi) {
		if (rssi == 0) {
			return -100; // if we cannot determine accuracy, return -1.
		}
		double ratio = rssi * 1.0 / txPower;
		if (ratio < 1.0) {
			return (int) (100 * Math.pow(ratio, 10));
		} else {
			double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
			return (int) (100 * accuracy);
		}
	}

	public static Beacon get(Set<Beacon> beaconSet, String key) {
		Beacon beacon = null;
		for (Beacon b : beaconSet) {
			if (b != null && b.getMac().equals(key)) {
				beacon = b;
			}
		}
		return beacon;
	}

	public static String getUUID() {
		long num = System.currentTimeMillis();
		// Log.d("SortUtil", "" + num);
		return Long.toString(num);
	}

	public static int getBeaconTxPower(byte[] scanRecord) {
		int startByte = 2;
		int fanhui = 1;

		while (startByte <= 5) {
			if (((int) scanRecord[startByte + 2] & 0xff) == 0x02
					&& ((int) scanRecord[startByte + 3] & 0xff) == 0x15) {
				// yes! This is an iBeacon
				fanhui = (int) scanRecord[startByte + 24]; // this one is signed
				break;
			} else if (((int) scanRecord[startByte] & 0xff) == 0x2d
					&& ((int) scanRecord[startByte + 1] & 0xff) == 0x24
					&& ((int) scanRecord[startByte + 2] & 0xff) == 0xbf
					&& ((int) scanRecord[startByte + 3] & 0xff) == 0x16) {
				fanhui = 2;
				break;
			} else if (((int) scanRecord[startByte] & 0xff) == 0xad
					&& ((int) scanRecord[startByte + 1] & 0xff) == 0x77
					&& ((int) scanRecord[startByte + 2] & 0xff) == 0x00
					&& ((int) scanRecord[startByte + 3] & 0xff) == 0xc6) {
				fanhui = 3;
				break;
			}
			startByte++;
		}

		return fanhui;

	}

	public static String scanByteToHexString(byte[] scanRecord) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < scanRecord.length; i++) {
			int v = scanRecord[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

}

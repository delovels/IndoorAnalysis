package com.bupt.indoorPosition.uti;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.bupt.indoorPosition.bean.IndoorRecord;
import com.bupt.indoorPosition.bean.Neighbor;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

public class SignalUtil {
	private static Field lteSignalStrengthField;
	private static Field lteRsrpField;
	private static Field lteRsrqField;
	private static Field lteSinrField;
	private static Field wcdmaRscpField;
	private static Field mTdScadmField;
	private static Field mLteCqiField;

	public static boolean isValidStrength(int s) {
		if (s >= 0)
			// 排除空值
			return false;
		// if (s >= 50)
		// //排除异常aus或者
		// return false;
		if (s <= -160)
			return false;
		if (s < 0 && s >= -18)
			return false;

		return true;
	}

	public static boolean isValidRsrp(int s) {
		if (s > 0)
			// 排除异常
			return false;
		// if (s >= 50)
		// //排除异常aus或者
		// return false;
		if (s <= -160)
			return false;
		if (s < 0 && s >= -18)
			return false;

		return true;
	}

	public static boolean isValidRsrq(int s) {
		if (s >= 160)
			// 排除离谱值
			return false;
		// if (s >= 50)
		// //排除异常aus或者
		// return false;
		if (s <= -160)
			return false;

		return true;
	}

	public static boolean isValidSinr(int s) {
		if (s >= 500)
			// 排除离谱值
			return false;
		// if (s >= 50)
		// //排除异常aus或者
		// return false;
		if (s <= -160)
			return false;

		return true;
	}

	/**
	 * 
	 * @param signalStrength
	 * @return [realSignalStrength,type,sinr,rsrq] type 1 gsm 2 tdscdma 3 wcdma
	 *         4 lte
	 */
	private static int[] tryAllSignal(SignalStrength signalStrength) {
		int strength = 0;
		// 尝试其他制式下的信号强度
		// 2G
		int sinr = 0;
		int rsrq = 0;
		int type = 1;
		int asu = signalStrength.getGsmSignalStrength();
		if (asu > 0)
			strength = -113 + 2 * asu;
		else
			strength = asu;
		if (!isValidStrength(strength)) {
			// 移动3G
			type = 2;
			strength = (Integer) getTDSCDMARscp(signalStrength, "mTdScdmaRscp");
			if (!isValidStrength(strength)) {
				type = 3;
				// 联通3G
				strength = (Integer) getWCDMARscp(signalStrength, "mWcdmaRscp");
				if (!isValidStrength(strength)) {
					// 4G
					type = 4;
					try {

						strength = (Integer) SignalUtil.getLteRsrp(
								signalStrength, "mLteRsrp");
						sinr = (Integer) SignalUtil.getLteSinr(signalStrength,
								"mLteRssnr");
						rsrq = (Integer) SignalUtil.getLteRsrq(signalStrength,
								"mLteRsrq");

					} catch (IllegalAccessException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (NoSuchFieldException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

		}
		return new int[] { strength, type, sinr, rsrq };
	}

	/**
	 * 
	 * 
	 * @param signalStrength
	 * @param cellState
	 * @return false
	 */
	public static void updateWireless(SignalStrength signalStrength,
			IndoorRecord cellState) {
		Log.d("phonestateListener", "change");
		// signalStrength.
		String networkType = cellState.getNetworkType();
		int strength = 0;
		int ltesignalstrength = 0;
		int rsrp = 0;
		int rsrq = 0;
		int sinr = 0;
		int cqi = 0;
		if (Constants._2G.contains(networkType)) {

			int asu = signalStrength.getGsmSignalStrength();
			if (asu > 0)
				strength = -113 + 2 * asu;
			else
				strength = asu;
		} else if (Constants._3G.contains(networkType)) {
			Log.d("mnc", cellState.getMnc());
			if (cellState.getMnc() != null
					&& (cellState.getMnc().contains("00") || cellState.getMnc()
							.contains("02"))) {
				// 移动3G
				strength = (Integer) getTDSCDMARscp(signalStrength,
						"mTdScdmaRscp");

			} else if (cellState.getMnc() != null
					&& (cellState.getMnc().contains("01"))) {
				// 联通3G
				strength = (Integer) getWCDMARscp(signalStrength, "mWcdmaRscp");

			}
		} else if (Constants._4G.contains(networkType)) {
			try {
				sinr = (Integer) SignalUtil.getLteSinr(signalStrength,
						"mLteRssnr");
				rsrp = (Integer) SignalUtil.getLteRsrp(signalStrength,
						"mLteRsrp");
				rsrq = (Integer) SignalUtil.getLteRsrq(signalStrength,
						"mLteRsrq");
				cqi = (Integer) SignalUtil.getLteCqi(signalStrength, "mLteCqi");
				strength = rsrp;
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchFieldException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		if (!isValidStrength(strength)) {
			// if(Constants._4G.contains(cellState.getNetworkType())){
			// //LTE下发生了异常
			// }
			int[] datas = tryAllSignal(signalStrength);
			if (datas.length == 4) {
				strength = datas[0];

				switch (datas[1]) {
				case 1:
					networkType = "2G";
					break;
				case 2:
				case 3:
					networkType = "3G";
					break;
				case 4:
					if (isValidStrength(strength)) {
						networkType = "4G";
						rsrp = strength;
						sinr = datas[2];
						rsrq = datas[3];
					}
					break;
				}
			}// else ,exception may occur in tryAllSignal(),consider as invalid

		}

		if ((!Constants._4G.contains(networkType))
				&& (!"4G".equals(networkType))) {
			rsrp = 0;
			rsrq = 0;
			sinr = 0;
		}
		//
		// classInspector(signalStrength);
		// String[] d = (String[]) getSIGNAL_STRENGTH_NAMES(signalStrength);
		// for (String dd : d)
		// Log.d("signal strength names", dd);
		Log.d("mGsmSignalStrength", "" + signalStrength.getGsmSignalStrength());
		Log.d("mGsmBitErrorRate", "" + signalStrength.getGsmBitErrorRate());
		int cdmaDBm = signalStrength.getCdmaDbm();
		Log.d("cdmaDBm", "" + cdmaDBm);
		int EvdoEcio = signalStrength.getEvdoEcio();
		Log.d("mCdmaEcio", "" + EvdoEcio);
		int EvdoDbm = signalStrength.getEvdoDbm();
		Log.d("EVDODbm", "" + EvdoDbm);
		Log.d("mEvdoEcio", "" + signalStrength.getEvdoEcio());
		Log.d("mEvdoSnr", "" + signalStrength.getEvdoSnr());

		Log.d("mLteSignalStrength", "" + ltesignalstrength);
		Log.d("mLteRsrp", "" + rsrp);
		Log.d("mLteRsrq", "" + rsrq);
		Log.d("mLteRssnr", "" + sinr);
		Log.d("mLteCqi", "" + cqi);
		String signalInformation = signalStrength.toString();
		Log.d("得到的信号响度串", signalInformation);
		Log.d("可能是XG信号强度", "" + strength);
		Log.d("信号类型", "" + networkType);

		cellState.setSignalStrength(strength);
		cellState.setNetworkType(networkType);
		cellState.setRsrp(rsrp);
		cellState.setRsrq(rsrq);
		cellState.setSinr(sinr);
	}

	public static String updateDataConnectionType1(
			ConnectivityManager connectivityManager,
			TelephonyManager telephonyManager) {
		String strNetworkType = "NONE";

		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			String _strSubTypeName = networkInfo.getSubtypeName();
			// TD-SCDMA networkType is 17
			int networkType = networkInfo.getSubtype();
			strNetworkType = convertNetType(networkType);
			if (Constants.NETTYPE.UNKNOWN.equals(strNetworkType)) {
				// http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信
				// 三种3G制式
				if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA")
						|| _strSubTypeName.equalsIgnoreCase("WCDMA")
						|| _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
					strNetworkType = "xCDMA";
				} else {
					strNetworkType = _strSubTypeName;
				}
			}

		} else {
			if (networkInfo == null) {
				strNetworkType = "FAILED";
			} else {
				strNetworkType = "UNCONNECTED";
			}
		}

		// Log.e("cocos2d-x", "Network Type : " + strNetworkType);
		return strNetworkType;

	}

	public static String updateDataConnectionType(
			ConnectivityManager connectivityManager,
			TelephonyManager telephonyManager) {
		String strNetworkType = "NONE";

		strNetworkType = convertNetType(telephonyManager.getNetworkType());
		// 尝试另外的获取途径
		if (Constants.NETTYPE.UNKNOWN.equals(strNetworkType)) {
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					strNetworkType = "WIFI";
				} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
					String _strSubTypeName = networkInfo.getSubtypeName();
					// TD-SCDMA networkType is 17
					int networkType = networkInfo.getSubtype();
					strNetworkType = convertNetType(networkType);
					if (Constants.NETTYPE.UNKNOWN.equals(strNetworkType)) {
						// http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信
						// 三种3G制式
						if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA")
								|| _strSubTypeName.equalsIgnoreCase("WCDMA")
								|| _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
							strNetworkType = "xCDMA";
						} else {
							strNetworkType = _strSubTypeName;
						}
					}
				} else {
					strNetworkType = "OTHER";
				}
			} else {
				if (networkInfo == null) {
					strNetworkType = "FAILED";
				} else {
					strNetworkType = "UNCONNECTED";
				}
			}
		}

		// Log.e("cocos2d-x", "Network Type : " + strNetworkType);
		return strNetworkType;

	}

	public static void updateCellLocation(
			ConnectivityManager connectivityManager,
			TelephonyManager telephonyManager, IndoorRecord cellState,
			List<Neighbor> neighbors) {

		CellLocation location = telephonyManager.getCellLocation();

		Timestamp t = new Timestamp(System.currentTimeMillis());
		// Log.d("SignalUtil",
		// "net work type " + telephonyManager.getNetworkType());
		String networkType = updateDataConnectionType(connectivityManager,
				telephonyManager);
		// convertNetType(telephonyManager.getNetworkType());
		cellState.setNetworkType(networkType);
		cellState.setImsi(telephonyManager.getSubscriberId());
		// System.out.println("network type " + networkType);
		if (location instanceof GsmCellLocation) {
			// gsm网络
			cellState.setLac(((GsmCellLocation) location).getLac());
			cellState.setNetType("GSM");
			cellState.setCid(((GsmCellLocation) location).getCid());
			/** 获取mcc，mnc */

			String mccMnc = telephonyManager.getNetworkOperator();
			if (mccMnc != null && mccMnc.length() >= 5) {
				// cellState.setMcc(mccMnc.substring(0, 3));
				cellState.setMnc(mccMnc.substring(3, 5));
			} else if (telephonyManager.getSubscriberId() != null) {
				// cellState.setMcc(telephonyManager.getSubscriberId().substring(
				// 0, 3));
				cellState.setMnc(telephonyManager.getSubscriberId().substring(
						3, 5));
			}
			// 邻区信息
			List<NeighboringCellInfo> neighboringList = telephonyManager
					.getNeighboringCellInfo();
			// Log.d("phonestateListener", "neighboringList find num "
			// + neighboringList.size());
			neighbors.removeAll(neighbors);
			for (NeighboringCellInfo ni : neighboringList) {
				int ns = ni.getRssi();
				if (ns >= 0)
					ns = -133 + 2 * ni.getRssi();
				String nw = convertNetType(ni.getNetworkType());
				int nlac = ni.getLac();
				Neighbor gb = new Neighbor(null, ni.getCid(), ns, nw, nlac, t);
				neighbors.add(gb);
			}
		} else {// 其他CDMA等网络
			try {
				Class cdmaClass = Class
						.forName("android.telephony.cdma.CdmaCellLocation");

				CdmaCellLocation cdma = (CdmaCellLocation) location;

				if (cdma != null) {
					int stationId = cdma.getBaseStationId() >= 0 ? cdma
							.getBaseStationId() : -1;
					int networkId = cdma.getNetworkId() >= 0 ? cdma
							.getNetworkId() : -1;
					int systemId = cdma.getSystemId() >= 0 ? cdma.getSystemId()
							: -1;
					cellState.setCid(systemId);
				}
				/** 获取mcc，mnc */
				String mccMnc = telephonyManager.getNetworkOperator();
				if (mccMnc != null && mccMnc.length() >= 5) {
					// cellState.setMcc(mccMnc.substring(0, 3));
					cellState.setMnc(mccMnc.substring(3, 5));
				}
			} catch (ClassNotFoundException classnotfoundexception) {
			}
		}// end CDMA网络
			// cellState.setCellRecordTime(t);
	}

	/**
	 * 把整型的网络类型转化成字符串
	 * 
	 * @param type
	 * @return
	 */
	public static String convertNetType(int type) {
		switch (type) {
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return Constants.NETTYPE.NETWORK_TYPE_CDMA;
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return Constants.NETTYPE.NETWORK_TYPE_1xRTT;
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return Constants.NETTYPE.NETWORK_TYPE_EDGE;
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return Constants.NETTYPE.NETWORK_TYPE_EHRPD;
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return Constants.NETTYPE.NETWORK_TYPE_EVDO_0;
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return Constants.NETTYPE.NETWORK_TYPE_EVDO_A;
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			return Constants.NETTYPE.NETWORK_TYPE_EVDO_B;
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return Constants.NETTYPE.NETWORK_TYPE_GPRS;
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return Constants.NETTYPE.NETWORK_TYPE_HSDPA;
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return Constants.NETTYPE.NETWORK_TYPE_HSPA;
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return Constants.NETTYPE.NETWORK_TYPE_HSPAP;
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			return Constants.NETTYPE.NETWORK_TYPE_HSUPA;
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return Constants.NETTYPE.NETWORK_TYPE_IDEN;
		case TelephonyManager.NETWORK_TYPE_LTE:
			return Constants.NETTYPE.NETWORK_TYPE_LTE;
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return Constants.NETTYPE.NETWORK_TYPE_UMTS;
		default:
			return Constants.NETTYPE.UNKNOWN;

		}

		// return null;
	}

	public static int getSignalField(SignalStrength signalStrength, int num) {
		if (signalStrength == null)
			return -10101;
		String[] datas = signalStrength.toString().split("\\s");
		return Integer.parseInt(datas[num]);

	}

	public static Object getLteSignalStrength(Object instance,
			String lteSignalStrengthFieldName) throws IllegalAccessException,
			NoSuchFieldException {
		if (lteSignalStrengthField == null) {
			lteSignalStrengthField = getField(instance.getClass(),
					lteSignalStrengthFieldName);
			// 参数值为true，禁用访问控制检查
			lteSignalStrengthField.setAccessible(true);
		}
		return lteSignalStrengthField.get(instance);
	}

	public static Object getLteRsrp(Object instance, String lteRsrpFieldName)
			throws IllegalAccessException, NoSuchFieldException {
		if (lteRsrpField == null) {
			lteRsrpField = getField(instance.getClass(), lteRsrpFieldName);
			// 参数值为true，禁用访问控制检查
			lteRsrpField.setAccessible(true);
		}
		return lteRsrpField.get(instance);
	}

	public static Object getLteRsrq(Object instance, String lteRsrqFieldName)
			throws IllegalAccessException, NoSuchFieldException {
		if (lteRsrqField == null) {
			lteRsrqField = getField(instance.getClass(), lteRsrqFieldName);
			// 参数值为true，禁用访问控制检查
			lteRsrqField.setAccessible(true);
		}
		return lteRsrqField.get(instance);
	}

	public static Object getLteSinr(Object instance, String lteSinrFieldName)
			throws IllegalAccessException, NoSuchFieldException {
		if (lteSinrField == null) {
			lteSinrField = getField(instance.getClass(), lteSinrFieldName);
			// 参数值为true，禁用访问控制检查
			lteSinrField.setAccessible(true);
		}
		return lteSinrField.get(instance);
	}

	public static Object getLteCqi(Object instance, String lteCqiFieldName)
			throws IllegalAccessException, NoSuchFieldException {
		if (mLteCqiField == null) {
			mLteCqiField = getField(instance.getClass(), lteCqiFieldName);
			// 参数值为true，禁用访问控制检查
			mLteCqiField.setAccessible(true);
		}
		return mLteCqiField.get(instance);
	}

	public static Object getWCDMARscp(Object instance, String wcdmaRscpFieldName) {

		try {
			if (wcdmaRscpField == null) {

				wcdmaRscpField = getField(instance.getClass(),
						wcdmaRscpFieldName);
				// 参数值为true，禁用访问控制检查
				wcdmaRscpField.setAccessible(true);
			}
			return wcdmaRscpField.get(instance);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;

	}

	public static Object getTDSCDMARscp(Object instance,
			String tdSCDMARscpFieldName) {
		try {
			if (mTdScadmField == null) {

				mTdScadmField = getField(instance.getClass(),
						tdSCDMARscpFieldName);
				// 参数值为true，禁用访问控制检查
				mTdScadmField.setAccessible(true);
			}
			return mTdScadmField.get(instance);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public static Object getSIGNAL_STRENGTH_NAMES(Object instance) {
		try {
			Field f = getField(instance.getClass(), "SIGNAL_STRENGTH_NAMES");
			return f.get(instance);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	private static Field getField(Class thisClass, String fieldName)
			throws NoSuchFieldException {

		if (fieldName == null) {
			throw new NoSuchFieldException("Error field !");
		}

		Field field = thisClass.getDeclaredField(fieldName);
		return field;
	}

	public static void classInspector(Object object) {
		Class cls = object.getClass();
		System.out.println(cls.toString());
		Field[] fieldlist = cls.getDeclaredFields();
		for (int i = 0; i < fieldlist.length; i++) {
			Field fld = fieldlist[i];

			System.out.println("name = " + fld.getName());
			System.out.println("decl class = " + fld.getDeclaringClass());
			System.out.println("type = " + fld.getType());
			System.out.println("-----");
		}
	}

}

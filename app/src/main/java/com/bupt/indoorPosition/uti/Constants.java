package com.bupt.indoorPosition.uti;

import android.telephony.TelephonyManager;

public class Constants {
	public static class MSG {
		public static final int UPDATE = 0x1f;
		public static final int HAS_LOGINED = 0x2f;
		public static final int UPLOAD = 0xf1;
		public static final int SHOW_BEACON = 0xf2;
		public static final int UPDATA_CLIENT = 0x3f;
		public static final int NOT_UPDATA = 0x6f;
		public static final int GET_UNDATAINFO_ERROR = 0x4f;
		public static final int DOWN_ERROR = 0x5f;

	}

	public static class NETTYPE {
		public static String NETWORK_TYPE_CDMA = "CDMA";
		public static String NETWORK_TYPE_1xRTT = "1xRTT";
		public static String NETWORK_TYPE_EDGE = "EDGE";
		public static String NETWORK_TYPE_EHRPD = "EHRPD";
		public static String NETWORK_TYPE_EVDO_0 = "EVDO_0";
		public static String NETWORK_TYPE_EVDO_A = "EVDO_A";
		public static String NETWORK_TYPE_EVDO_B = "EVDO_B";
		public static String NETWORK_TYPE_GPRS = "GPRS";
		public static String NETWORK_TYPE_HSDPA = "HSDPA";
		public static String NETWORK_TYPE_HSPA = "HSPA";
		public static String NETWORK_TYPE_HSPAP = "HSPAP";
		public static String NETWORK_TYPE_HSUPA = "HSUPA";
		public static String NETWORK_TYPE_IDEN = "IDEN";
		public static String NETWORK_TYPE_LTE = "LTE";
		public static String NETWORK_TYPE_UMTS = "UMTS";
		public static String UNKNOWN = "UNKNOWN";
	}

	public static class ERRORCODE {
		public static final Integer OK = 1;
		public static final Integer LOGIN_ERROR = -109;
		public static final Integer NONLOGIN_ERROR = -108;
		public static final Integer PARAMETER_ERROR = -100;
		public static final Integer REGISTRY_ERROR = -101;
		public static final Integer UPLOAD_ERROR = -102;
	}

	public static class ACTIONURL {
		public static final String MAIN_ACTIVITY_ACTION = "com.bupt.indoorposition.receiver.main.ServiceResponse";
		public static final String REGISTER_ACTIVITY_ACTION = "com.bupt.indoorposition.receiver.Register.ServiceResponse";
		public static final String LOGIN_ACTIVITY_ACTION = "com.bupt.indoorposition.receiver.login.Success";
	}

	public static class INTENT_TYPE {
		public static final String REGISTRY_SUCCESS = "registrySuccess";
		public static final String USERNAME_AVAIABLE = "userNameAvailable";
		public static final String NONE = "none";
		public static final String BEACON_LIST_DISPLAY = "beaconListDisplay";
		public static final String KEEP_ALIVE = "keepAlive";
	}

	public static final String _2G = "'EDGE','GPRS','CDMA','1xRTT','IDEN'";
	public static final String _3G = "'UMTS','EVDO_0','EVDO_A','HSDPA','HSPA','HSPAP','HSUPA','EVDO_B','EHRPD','xCDMA'";
	public static final String _4G = "'LTE'";

}

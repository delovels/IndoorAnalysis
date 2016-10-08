package com.bupt.indoorPosition.uti;

import java.util.HashMap;

public class Global {
	public static enum LoginStatus {
		NON_LOGINED, LOGINED
	}

	public static LoginStatus loginStatus = LoginStatus.NON_LOGINED;
	// cookie
	public static HashMap<String, String> cookieContainer = new HashMap<String, String>();
}

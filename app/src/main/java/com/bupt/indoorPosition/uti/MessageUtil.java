package com.bupt.indoorPosition.uti;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

 
import com.bupt.indoorPosition.bean.InspectedBeacon;
import com.bupt.indoorPosition.uti.Global.LoginStatus;

 
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MessageUtil {

	/**
	 * 
	 * @param map
	 *            来自服务器的请求
	 * @return
	 */
	public static Intent getServerResponseBundle(Map<String, Object> map,
			String actionUrl, String type) {
		Bundle b = new Bundle();
		if (map == null)
			b.putString("reason", "连接失败或服务器无响应");
		else {
			Integer code = (Integer) map.get("status");
			String reason = (String) map.get("reason");
			if (code == null) {
				b.putString("reason", "服务器响应有误");
			} else {
				if (code >= 0) {
					b.putString("reason", "Ok");
				} else {
					b.putString("reason", reason);
				}
				b.putInt("status", code);
			}
		}
		Intent intent = new Intent(actionUrl);
		intent.putExtras(b);
		intent.putExtra("type", type);

		return intent;
	}

	public static Intent getHomeReceiverListDisplayBundle(
			ArrayList<InspectedBeacon> showList) {

		Intent intent = new Intent(Constants.ACTIONURL.MAIN_ACTIVITY_ACTION);
		Bundle b = new Bundle();
		b.putSerializable("showList", showList);
		intent.putExtras(b);
		intent.putExtra("type", Constants.INTENT_TYPE.BEACON_LIST_DISPLAY);
		return intent;

	}

	public static boolean checkLogin(Context context) {
		if (Global.loginStatus == LoginStatus.NON_LOGINED) {
			Toast.makeText(context, "请先登录再进行此操作", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

}

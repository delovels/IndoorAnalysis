package com.bupt.indoorPosition.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.bupt.indoorPosition.bean.Inspector;
import com.bupt.indoorPosition.bean.UserSetting;
import com.bupt.indoorPosition.dao.DBManager;
import com.bupt.indoorPosition.uti.Constants;
import com.bupt.indoorPosition.uti.Global;
import com.bupt.indoorPosition.uti.HttpUtil;
import com.bupt.indoorPosition.uti.JsonUtil;
import com.bupt.indoorPosition.uti.MessageUtil;

import com.bupt.indooranalysis.R;

public class UserService {
	/**
	 * 判断用户名是否合法
	 * 
	 * @param context
	 * @param userNameSet
	 * @return
	 */
	public static void userNameToConf(Context context, String userNameSet) {
		final String url = context.getString(R.string.hostUrl)
				+ "/location/confirmUserName";
		Log.i("userNameSet", userNameSet);
		Map<String, String> params = new HashMap<String, String>();
		params.put("userNameSet", userNameSet);
		Map<String, Object> response = HttpUtil.post(url, params);
		Log.d("用户名判断合法", "response:" + response.get("status"));
		context.sendBroadcast(MessageUtil.getServerResponseBundle(response,
				Constants.ACTIONURL.REGISTER_ACTIVITY_ACTION,
				Constants.INTENT_TYPE.USERNAME_AVAIABLE));

	}

	// public static String userNameToConf(Context context, String userNameSet)
	// {
	// String url = context.getString(R.string.hostUrl)
	// + "/location/confirmUserName";
	// String result;
	// Map<String, String> params = new HashMap<String, String>();
	// params.put("userNameSet", JsonUtil.serilizeJavaObject(userNameSet));
	// Map<String, Object> response = HttpUtil.post(url, params);
	// Log.d("用户名判断合法", "response:" + response.get("status"));
	// if (response == null || ((Integer) (response.get("status"))) == null
	// || response.get("status") == Constants.ERRORCODE.REGISTRY_ERROR) {
	// result = (String) response.get("reason");
	// } else {
	// result = "ok";
	// }
	// return result;
	//
	// }

	/**
	 * 注册插入用户
	 * 
	 * @param context
	 * @param inspector
	 * @return
	 */
	public static boolean insertUser(Context context, Inspector inspector) {
		DBManager dbManager = new DBManager(context);
		dbManager.deleteInspector();
		return dbManager.insertInspector(inspector);
	}

	public static Inspector selectAllInspector(Context context) {
		DBManager dbManager = new DBManager(context);
		List<Inspector> list = dbManager.selectAllInspector();
		Inspector inspector;
		if (list != null && list.size() == 1) {
			inspector = list.get(0);
			return inspector;
		}
		return null;
	}

	/**
	 * 
	 * @param us
	 * @param insertDefault
	 *            true 如果用户设置不存在，则创建默认的用户设置. false us不能为null，通过us来更新
	 * @param context
	 */
	public static void updateUserSetting(UserSetting us, boolean insertDefault,
			Context context) {
		if (us == null)
			return;
		DBManager dbManager = new DBManager(context);
		if (insertDefault) {
			dbManager.insertDeafaultUSIfNotExist(us.getUsername());
		} else {
			dbManager.updateUserSetting(us);
		}
	}

	public static UserSetting getUserSetting(Context context) {
		Inspector inspector = selectAllInspector(context);
		DBManager dbManager = new DBManager(context);
		if (inspector == null) {
			// 用户已经注销
			UserSetting us = dbManager.selectUserSetting("defaultUserName888");
			if (us == null) {
				us = new UserSetting();
				us.setUsername("defaultUserName888");
			}
			return us;
		}
		return dbManager.selectUserSetting(inspector.getUsername());
	}

	public static void sendUser(Context context, Inspector inspector) {
		final String url = context.getString(R.string.hostUrl)
				+ "/location/registry";
		Log.d("insertUser", "start");
		Map<String, String> params = new HashMap<String, String>();
		params.put("inspectorString", JsonUtil.serilizeJavaObject(inspector));
		Map<String, Object> response = HttpUtil.post(url, params);
		context.sendBroadcast(MessageUtil.getServerResponseBundle(response,
				Constants.ACTIONURL.REGISTER_ACTIVITY_ACTION,
				Constants.INTENT_TYPE.REGISTRY_SUCCESS));
	}

	/**
	 * 用户登录验证
	 * 
	 * @param context
	 * @param userName
	 * @param password
	 */
	public static void userLogin(Context context, String userName,
			String password) {
		final String url = context.getString(R.string.hostUrl)
				+ "/location/login";
		Log.d("userLogin", userName);
		Log.d("userLogin", password);
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", userName);
		params.put("password", password);
		Map<String, Object> response = HttpUtil.post(url, params);
		Log.d("用户名判断合法", "response:" + response.get("status"));

		boolean flag = false;
		if (response != null && ((Integer) response.get("status") != null)
				&& (Integer) response.get("status") > 0) {
			Inspector inspector = JsonUtil.convertObjectFromMap(response,
					"inspector", Inspector.class);
			if (inspector != null) {
				flag = insertUser(context, inspector);
				// 更新用户设置
				UserSetting us = new UserSetting();
				us.setUsername(inspector.getUsername());
				updateUserSetting(us, true, context);
			}
		}

		if (flag) {
			Global.loginStatus = Global.LoginStatus.LOGINED;
		}
		context.sendBroadcast(MessageUtil.getServerResponseBundle(response,
				Constants.ACTIONURL.LOGIN_ACTIVITY_ACTION,
				Constants.INTENT_TYPE.NONE));
		context.sendBroadcast(MessageUtil.getServerResponseBundle(response,
				Constants.ACTIONURL.MAIN_ACTIVITY_ACTION,
				Constants.INTENT_TYPE.NONE));

	}

	public static void userLogout(Context context) {
		final String url = context.getString(R.string.hostUrl)
				+ "/location/logout";
		Log.d("userService", "userLogout");
		Map<String, String> params = new HashMap<String, String>();
		Map<String, Object> response = HttpUtil.post(url, params);
		if (response != null && ((Integer) response.get("status") != null)
				&& (Integer) response.get("status") > 0) {
			//注销，正确返回说明session不存在
			// 清空session
			Global.cookieContainer = new HashMap<String, String>();
		}
		ModelService.removeAllInspector(context);
		Global.loginStatus = Global.LoginStatus.NON_LOGINED;

	}

}

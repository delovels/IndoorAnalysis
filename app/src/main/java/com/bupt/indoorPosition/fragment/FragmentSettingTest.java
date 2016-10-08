package com.bupt.indoorPosition.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.AvoidXfermode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bupt.indoorPosition.bean.Inspector;
import com.bupt.indoorPosition.bean.Sim;
import com.bupt.indoorPosition.callback.SettingUpdateCallback;
import com.bupt.indoorPosition.dao.DBManager;
import com.bupt.indoorPosition.model.CheckVersionTask;
import com.bupt.indoorPosition.model.ModelService;
import com.bupt.indoorPosition.model.UserService;
import com.bupt.indoorPosition.uti.Constants;
import com.bupt.indoorPosition.uti.Global;
import com.bupt.indoorPosition.uti.HttpUtil;
import com.bupt.indoorPosition.uti.MessageUtil;
import com.bupt.indoorpostion.AboutSystemActivity;
import com.bupt.indoorpostion.HomeActivity;
import com.bupt.indoorpostion.IndoorLocationActivity;
import com.bupt.indoorpostion.LoginActivity;
import com.bupt.indoorpostion.MainActivity;
import com.bupt.indoorpostion.R;
import com.bupt.indoorpostion.SystemSettingActivity;
import com.bupt.indoorpostion.RegisterActivity;
import com.bupt.indoorpostion.TrainDataCollectionActivity;
import com.bupt.indoorpostion.UserCenterActivity;

public class FragmentSettingTest extends Fragment implements
		SettingUpdateCallback {

	private HomeActivity parent = null;
	private ListView listView;
	private String[] arrName = { "用户/登录", "更新数据", "清除数据", "系统设置", "关于系统",
			"训练数据采集","室内定位" };
	private List<Map<String, Object>> listItems;
	private boolean startScanning = false;
	private boolean isUpdating = false;
	private boolean isDeleting = false;

	private Bundle savedState;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d("FragmentSetting", "oncreate");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d("FragmentInspection", "onCreateView");
		View view = inflater.inflate(R.layout.setting_test, container, false);
		listView = (ListView) view.findViewById(R.id.set_listView);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int index, long id) {
				// Log.d("selected", arrName[index]);
				switch (index) {
				case 0:
					userCenter();
					break;
				case 1:
					updateBeacon();
					break;
				case 3:
					systemsetting();
					break;
				case 2:
					deleteAll();
					break;
				case 4:
					aboutSystem();
					break;
				case 5:
					trainData();
					break;
				case 6:
					indoorLocation();
					break;
				}

			}
		});
		listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < arrName.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("text", arrName[i]);
			listItems.add(listItem);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(parent, listItems,
				R.layout.array_test, new String[] { "text" },
				new int[] { R.id.set_itemView });
		listView.setAdapter(simpleAdapter);
		displayUserOrLogin();
		return view;
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Log.d("FragmentSetting onResume", "start");
		displayUserOrLogin();
	}

	private void displayUserOrLogin() {

		Log.d("onResume", "start");

		if (Global.loginStatus == Global.LoginStatus.LOGINED) {
			Log.d("FragmentSetting loginStatus", Global.loginStatus.toString());
			Inspector inspector = UserService.selectAllInspector(parent);
			changeListItemName(0, inspector.getUsername());

		} else {
			changeListItemName(0, arrName[0]);

		}

	}

	private void changeListItemName(int index, String newItem) {
		if (listItems != null) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("text", newItem);
			listItems.set(index, listItem);
			((SimpleAdapter) listView.getAdapter()).notifyDataSetChanged();
		}
	}

	private void userCenter() {
		if (Global.loginStatus == Global.LoginStatus.LOGINED) {
			Log.d("gotoCenter", "goto Center");
			Intent intent = new Intent(parent, UserCenterActivity.class);
			// parent.finish();
			startActivity(intent);
		} else {
			// parent.finish();
			startActivity(new Intent(parent, LoginActivity.class));
		}
	}

	private void systemsetting() {
		Intent intent = new Intent(parent, SystemSettingActivity.class);
		parent.startActivity(intent);
	}

	//
	private void aboutSystem() {
		Intent intent = new Intent(parent, AboutSystemActivity.class);
		parent.startActivity(intent);
	}

	//

	private void updateBeacon() {

		Log.d("update", "开始更新");
		if (!MessageUtil.checkLogin(parent.getApplicationContext())) {
			return;
		}
		if (isUpdating)
			return;
		isUpdating = true;
		changeListItemName(1, "正在更新...");

		new Thread(new Runnable() {
			@Override
			public void run() {
				Sim sim = ModelService.getPhoneInfo(parent.telephonyManager);
				boolean status = ModelService.updateDb(parent, sim);
				//新增定位模块更新
				boolean statusForLoacalization = ModelService.updateLocalization(parent);
				//
				Log.i("updateBeacon", "start updating");

				Message msg = new Message();
				msg.what = Constants.MSG.UPDATE;
				Bundle b = new Bundle();
				b.putBoolean("status", status&&statusForLoacalization);
				msg.setData(b);
				msg.what = Constants.MSG.UPDATE;
				parent.handler.sendMessage(msg);
			}
		}).start();
	}

	private void deleteAll() {
		if (isDeleting)
			return;
		new AlertDialog.Builder(parent).setTitle("删除确认")
				.setMessage("确定清空所有巡检记录吗？")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						isDeleting = true;
						changeListItemName(2, "正在清除...");
						Log.i("fragmentSetting deleteAll", "delete");
						DBManager dbManager = new DBManager(parent);
						// dbManager.deleteAllBeaconInfo();
						dbManager.deleteAllIndoorRecord();
						dbManager.deleteAllNeighborList();
						dbManager.deleteAllSpeedList();
						dbManager.deleteInspection();
						dbManager.deleteAllBeaconInfo();
						dbManager.deleteAllBeaconDebug();
						dbManager.deleteAllTrainData();
						changeListItemName(2, arrName[2]);
						isDeleting = false;
						Toast.makeText(parent, "数据清除完成", 3000).show();

					}
				}).setNegativeButton("否", null).show();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		parent = (HomeActivity) activity;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Save State Here
		saveStateToArguments();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		// Save State Here
		saveStateToArguments();
	}

	private void saveStateToArguments() {
		savedState = saveState();
		if (savedState != null) {
			Bundle b = getArguments();
			b.putBundle("internalSavedViewState8954201239546", savedState);
		}
	}

	@SuppressWarnings("unused")
	private boolean restoreStateFromArguments() {
		Bundle b = getArguments();
		savedState = b.getBundle("internalSavedViewState8954201239546");
		if (savedState != null) {
			restoreState();
			return true;
		}
		return false;
	}

	// ///////////////////////////////
	// 取出状态数据
	// ///////////////////////////////
	private void restoreState() {
		// if (savedState != null) {
		// // 比如
		// startScanning = savedState.getBoolean("startScanning");
		// Log.i("fragment restoreState startScanning", "" + startScanning);
		// if (startScanning) {
		//
		// btnStart.setText(R.string.btnStarting);
		// btnimage.setImageResource(images[0]);
		// } else {
		// btnStart.setText(R.string.btnStartContent);
		// btnimage.setImageResource(images[1]);
		// }
		// }
	}

	// ////////////////////////////
	// 保存状态数据
	// ////////////////////////////
	private Bundle saveState() {
		Bundle state = new Bundle();
		// 比如
		state.putBoolean("startScanning", startScanning);
		return state;
	}

	@Override
	public void handleUpdateMessage(Message msg) {
		if (msg.what == Constants.MSG.UPDATE) {
			Log.i("FrameSettingTest handleUpdateMessage", "msg.what "
					+ msg.what);
			changeListItemName(1, arrName[1]);
			Bundle b = msg.getData();
			boolean status = b.getBoolean("status");
			if (status) {
				Toast.makeText(parent, "更新成功", 3000).show();
			} else {
				Toast.makeText(parent, "更新失败", 3000).show();
			}
			isUpdating = false;
		} else if (msg.what == Constants.MSG.HAS_LOGINED) {
			displayUserOrLogin();
		}

	}

	private void trainData() {
		Intent intent = new Intent(parent, TrainDataCollectionActivity.class);
		// parent.finish();
		startActivity(intent);

	}
	
	protected void indoorLocation() {
		Intent intent = new Intent(parent, IndoorLocationActivity.class);
		// parent.finish();
		startActivity(intent);
	}
}

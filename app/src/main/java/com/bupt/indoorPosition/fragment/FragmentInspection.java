package com.bupt.indoorPosition.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import com.bupt.indoorPosition.bean.InspectDisplay;
import com.bupt.indoorPosition.bean.InspectedBeacon;
import com.bupt.indoorPosition.bean.Sim;
import com.bupt.indoorPosition.callback.FragmentServiceCallback;
import com.bupt.indoorPosition.callback.InspectUpdateCallback;
import com.bupt.indoorPosition.model.ModelService;
import com.bupt.indoorPosition.uti.Constants;
import com.bupt.indoorPosition.uti.MessageUtil;
import com.bupt.indoorpostion.HomeActivity;
import com.bupt.indoorpostion.MainActivity;
import com.bupt.indoorpostion.R;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentInspection extends Fragment implements
		InspectUpdateCallback {

	private HomeActivity parent = null;
	private Button btnStart;
	private Button btnUpload;
	private ListView listView;
	private ImageView btnimage;
	// private List<InspectedBeacon> showList = new
	// ArrayList<InspectedBeacon>();
	private List<Map<String, Object>> listData = null;
	int[] images = new int[] { R.drawable.green, R.drawable.red, };
	private boolean startScanning = false;

	private Bundle savedState;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d("FragmentInspection", "oncreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("FragmentInspection", "onCreateView");

		View view = inflater.inflate(R.layout.inspect, container, false);
		btnimage = (ImageView) view.findViewById(R.id.inspect_btn_image);
		btnUpload = (Button) view.findViewById(R.id.inspect_btn_end);
		btnUpload.setOnClickListener(new UploadListener());
		btnStart = (Button) view.findViewById(R.id.inspect_btn_start);
		btnStart.setOnClickListener(new StartListener());
		listView = (ListView) view.findViewById(R.id.beacon_list_view);

		listView.setAdapter(new BeaconSimpleAdapter(parent, getData(),
				R.layout.beacon_list_item, new String[] { "img", "content", },
				new int[] { R.id.beacon_list_view, R.id.beacon_list_tv }));

		return view;
	}

	private List<Map<String, Object>> getData() {
		// Log.i("FragmentInspection", "getData size " + showList.size());
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		// map.put("img", R.drawable.position);
		map.put("content", "巡检记录");
		list.add(map);

		listData = list;
		return list;
	}

	private void setData(List<InspectedBeacon> ibList) {
		listData.clear();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("content", "巡检记录");
		listData.add(map);
		for (InspectedBeacon ib : ibList) {
			Map<String, Object> _map = new HashMap<String, Object>();
			_map.put("img", R.drawable.position);
			_map.put("content", ib.getBuildingName() + " | " + ib.getFloor()
					+ " 层 | " + ib.getDescription() + " , 有" + ib.getCount()
					+ " 条巡检记录");
			listData.add(_map);
		}
	}

	@Override
	public void handleUpdateMessage(Message msg) {
		if (msg.what == Constants.MSG.UPLOAD) {
			Bundle b = msg.getData();
			boolean status = b.getBoolean("status");
			if (status) {
				Toast.makeText(parent, "上传成功", 3000).show();
			} else {
				Toast.makeText(parent, "上传失败", 3000).show();
			}
			btnUpload.setText(R.string.btnStartUpload);
			btnUpload.setClickable(true);

		} else if (msg.what == Constants.MSG.SHOW_BEACON) {
			Bundle b = msg.getData();
			ArrayList<InspectedBeacon> list = (ArrayList<InspectedBeacon>) b
					.getSerializable("showList");
			// list 不会是null
			Log.i("FragmentInspection",
					"handleUpdateMessage size " + list.size());
			setData(list);
			((SimpleAdapter) listView.getAdapter()).notifyDataSetChanged();

		}

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		parent = (HomeActivity) activity;
	}

	private class StartListener implements View.OnClickListener {
		@Override
		public void onClick(View arg0) {

			final Intent intent = new Intent();
			intent.setAction("com.bupt.indoorpostion.ScanService");
			if (!MessageUtil.checkLogin(parent.getApplicationContext())) {
				return;
			}
			if (startScanning == false) {
				startScanning = true;
				btnStart.setText(R.string.btnStarting);
				btnimage.setImageResource(images[0]);
				((FragmentServiceCallback) parent).startOrStopActivityService(
						intent, true);
			} else {
				startScanning = false;
				btnStart.setText(R.string.btnStartContent);
				// bAdapter.disable();
				btnimage.setImageResource(images[1]);
				((FragmentServiceCallback) parent).startOrStopActivityService(
						intent, false);
			}
		}
	}

	class UploadListener implements View.OnClickListener {

		@Override
		public void onClick(View arg0) {
			if (startScanning == true) {
				Toast.makeText(parent, "请先结束巡检", 3000).show();
				return;
			}
			if (!MessageUtil.checkLogin(parent.getApplicationContext())) {
				return;
			}
			btnUpload.setText(R.string.btnUploadding);
			btnUpload.setClickable(false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					Log.d("upload", "开始上传报告");
					boolean status = ModelService.uploadRecord(parent);
					boolean neighbor = ModelService.uploadNeighbor(parent);
					boolean inspection = ModelService.uploadInspection(parent);
					Message msg = new Message();
					msg.what = Constants.MSG.UPLOAD;
					Bundle b = new Bundle();
					b.putBoolean("status", status && neighbor && inspection);
					msg.setData(b);
					msg.what = Constants.MSG.UPLOAD;
					Log.d("上传测试", "" + status);
					parent.handler.sendMessage(msg);
				}
			}).start();

		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Restore State Here
		if (!restoreStateFromArguments()) {
			// First Time running, Initialize something here
		}
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
			b.putBundle("internalSavedViewState8954201239547", savedState);
		}
	}

	private boolean restoreStateFromArguments() {
		Bundle b = getArguments();
		savedState = b.getBundle("internalSavedViewState8954201239547");
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
		if (savedState != null) {
			// 比如
			startScanning = savedState.getBoolean("startScanning");
			Log.i("fragment restoreState startScanning", "" + startScanning);
			if (startScanning) {

				btnStart.setText(R.string.btnStarting);
				btnimage.setImageResource(images[0]);
			} else {
				btnStart.setText(R.string.btnStartContent);
				btnimage.setImageResource(images[1]);
			}
		}
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

	public class BeaconSimpleAdapter extends SimpleAdapter {
		private LayoutInflater mInflater;
		private final float titleFontSize;
		private final float screenWidth; // 屏幕宽
		private final float screenHeight; // 屏幕高

		public BeaconSimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);

			// 获取屏幕的长和宽
			DisplayMetrics dm = new DisplayMetrics();
			dm = context.getResources().getDisplayMetrics();
			screenWidth = dm.widthPixels;
			screenHeight = dm.heightPixels;
			// 设置标题字体大小
			titleFontSize = adjustTextSize();
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 0) {
				// if (convertView == null) {
				convertView = mInflater.inflate(R.layout.beacon_list_title,
						null);
				// }
			} else {
				// if (convertView == null) {
				convertView = mInflater
						.inflate(R.layout.beacon_list_item, null);
				// }
				ImageView im = (ImageView) convertView
						.findViewById(R.id.beacon_list_view);
				TextView tv_content = (TextView) convertView
						.findViewById(R.id.beacon_list_tv);
				// if (position == 0) {
				// // tv_content.setTextSize(20); // 设置字体大小，
				// // convertView.setBackgroundColor(Color.WHITE);
				// // tv_content.setTextColor(Color.BLACK);
				// } else {
				// // tv_content.setTextSize(10); // 设置字体大小，
				// //
				// convertView.setBackgroundColor(Color.parseColor("#EAEAEA"));
				// // tv_content.setTextColor(Color.BLACK);
				// }
			}

			return super.getView(position, convertView, parent);
		}

		float adjustTextSize() {
			float textsize = 12;
			// 在这实现你自己的字体大小算法，可根据之前计算的屏幕的高和宽来按比例显示
			textsize = screenWidth / 320 * 12;

			return textsize;
		}
	}
}

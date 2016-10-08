package com.bupt.indooranalysis;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.bupt.indoorPosition.bean.Beacon;
import com.bupt.indoorPosition.bean.BeaconInfo;
import com.bupt.indoorPosition.bean.IndoorRecord;
import com.bupt.indoorPosition.bean.InspectDisplay;
import com.bupt.indoorPosition.bean.InspectedBeacon;
import com.bupt.indoorPosition.bean.Neighbor;
import com.bupt.indoorPosition.bean.Speed;
import com.bupt.indoorPosition.bean.UserSetting;
import com.bupt.indoorPosition.model.ModelService;
import com.bupt.indoorPosition.model.UserService;
import com.bupt.indoorPosition.uti.BeaconUtil;
import com.bupt.indoorPosition.uti.MessageUtil;
import com.bupt.indoorPosition.uti.SignalUtil;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

public class ScanService extends Service {
	private Handler handler;
	private BluetoothAdapter bAdapter;
	private boolean startScanning = false;
	private String LOG_TAG = "SCANSERVICE";

	private Beacon prePosition = null;
	private Set<Beacon> beaconSet;
	public Set<Beacon> mbeaconSet;
	private Timer positionTimer;
	private Timer findLostBeaconTimer;

	private List<Neighbor> neighbors = new ArrayList<Neighbor>();
	private IndoorRecord cellState = new IndoorRecord();
	private BeaconInfo beaconinfo = new BeaconInfo();
	private TelephonyManager telephonyManager;
	private ConnectivityManager connectivityManager;
	private MyPhonestateListener myPhonestateListener;
	private MediaPlayer mp3;
	public final static String SER_KEY = "com.bupt.indoorpostion.ser";
	private List<InspectedBeacon> showList = new ArrayList<InspectedBeacon>();
	private int speedCount = 0;
	private Timestamp startTime;
	// 重启BLE扫描计时
	private int scanCount = 0;
	// 蓝牙没有反应计时
	private int bleNoReactCount = 0;
	Map<String, Integer> map = new HashMap<String, Integer>();

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// handler = new MainHandler();
		// init phonestate
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		myPhonestateListener = new MyPhonestateListener();
		telephonyManager.listen(myPhonestateListener,
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
						| PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
						| PhoneStateListener.LISTEN_CALL_STATE
						| PhoneStateListener.LISTEN_CELL_INFO
						| PhoneStateListener.LISTEN_CELL_LOCATION
						| PhoneStateListener.LISTEN_DATA_ACTIVITY
						| PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
						| PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR
						| PhoneStateListener.LISTEN_SERVICE_STATE);

		// telephonyManager.listen(myPhonestateListener,
		// PhoneStateListener.LISTEN_CELL_LOCATION);
		// if (telephonyManager.getCellLocation() != null) {
		// // 获取当前基站信息
		// myPhonestateListener.onCellLocationChanged(telephonyManager
		// .getCellLocation());
		// }

		// init bluetooth
		beaconSet = new HashSet<Beacon>();
		// 打开蓝牙
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(ScanService.this, R.string.ble_not_supported,
					Toast.LENGTH_SHORT).show();
			// finish();
		}
		Log.d("bluetooth", "ok");
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		bAdapter = bluetoothManager.getAdapter();
		bAdapter.enable();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (bAdapter != null) {
			if (!bAdapter.isEnabled())
				bAdapter.enable();
			Log.d("bluetooth", "start scaning");
			bAdapter.startLeScan(mLeScanCallback);
			// test Intent
			// Intent mIntent = new
			// Intent(this,com.bupt.indoorPosition.show.ShowBeacon.class);
			// Bundle mBundle = new Bundle();
			//
			// for(Beacon b:beaconSet){
			// mbeaconSet.add(b);
			// }
			// test Intent
			// 退避算法
			// 计时开始
			UserSetting us = UserService.getUserSetting(ScanService.this);
			final boolean isSpeedTest = us.isTestSpeed();
			Log.i("ScanService 用户设置是否测速", "" + us.isTestSpeed());
			startTime = new Timestamp(System.currentTimeMillis());
			//
			positionTimer = new Timer();
			positionTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					final Speed speed = new Speed();
					// speedCount();
					speedCount = (speedCount + 1) % 6;
					// 记录信号强度，beacon和邻区
					// 当没有检测到beacon或者非法信号，不做记录
					Beacon currentPostion = ModelService.recordIndoorData(
							ScanService.this,
							(IndoorRecord) (cellState.clone()), neighbors,
							beaconSet, beaconinfo, speed, true);
					if (currentPostion != null
							&& "暂无设备".equals(currentPostion.getMac())) {
						// 当前环境没有beacon
						return;
					}
					ModelService.updateDisplayList(currentPostion, showList,
							ScanService.this);
					sendBroadcast(MessageUtil
							.getHomeReceiverListDisplayBundle((ArrayList<InspectedBeacon>) showList));
					Log.i("上传下载速度测试计数器", "" + speedCount);
					if (speedCount == 5) {
						boolean tobeornottobe = ModelService.retreat(map,
								currentPostion);

						if (tobeornottobe && isSpeedTest) {

							new Thread(new Runnable() {
								@Override
								public void run() {

									ModelService.speedTest(ScanService.this,
											speed, cellState);
								}
							}).start();
						}
					}
					//

					// Iterator<Beacon> iter = beaconSet.iterator();
					// while (iter.hasNext())
					// System.out.println(iter.next());
					// 广播
					Intent intent = new Intent();
					ArrayList<String> list = new ArrayList<String>();
					for (Beacon b : beaconSet) {
						list.add(b.getMac());
						list.add("" + b.getRssi());
					}

					intent.putStringArrayListExtra("beaconlist", list);
					intent.setAction("com.bupt.indoorPosition.show.MyReceiver");
					sendBroadcast(intent);

					//
					if (currentPostion != prePosition) {
						prePosition = currentPostion;
						mp3 = MediaPlayer.create(ScanService.this,
								R.raw.change2);

						try {
							mp3.stop();
							mp3.prepare();
							mp3.start(); // 开始播放
						} catch (Exception e) {
							e.printStackTrace(); // 在控制台（control）上打印出异常
						}

						Log.d("position timer", "device change");
					}
				}
			}, 1000, 2000);
			findLostBeaconTimer = new Timer();
			findLostBeaconTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					// 每20* Beacon.TRANSMIT_PERIOD的时间重启一次BLE
					int restartThreshold = 20;
					int noReactThreashold = 1;
					int invalidBeacon = BeaconUtil.scanLostBeacon(beaconSet);
					int total = beaconSet.size();
					if (bleNoReactCount == noReactThreashold - 1) {
						/**
						 * 蓝牙在noReactThreashold*TRANSMIT_PERIOD时间内没有触发任何回调，
						 * 针对小米等机型，可能通过重启一次蓝牙来缓解这种问题。
						 * 对于华为等机型，蓝牙回调非常频繁，在有Beacon的范围内,
						 * noReactThreashold可能永远保持为零
						 */
						bleRestart();
						Log.i("ScansService", "蓝牙未响应重启");
					} else {
						if (total == 0 || invalidBeacon == total
								|| scanCount == restartThreshold - 1) {
							/**
							 * 蓝牙有响应,但可能没有潜在位置的Beacon可能没有被更新。
							 * 没有检测到蓝牙，或者蓝牙全部失效，或者时间达到restartThreshold
							 * *TRANSMIT_PERIOD ， 重启一次蓝牙
							 */
							bleRestart();
							Log.i("ScansService", "beacon失效或周期性重启");

						}
					}
					bleNoReactCount = (bleNoReactCount + 1) % noReactThreashold;
					scanCount = (scanCount + 1) % restartThreshold;
				}
			}, 3000, Beacon.TRANSMIT_PERIOD);

		}
		return START_STICKY;
	}

	/**
	 * 对于小米手机，每个Beacon可能只会被扫描一次，此时需要重启扫描
	 */
	private void bleRestart() {
		bAdapter.stopLeScan(mLeScanCallback);
		bAdapter.startLeScan(mLeScanCallback);
	}

	@Override
	public void onDestroy() {
		bAdapter.stopLeScan(mLeScanCallback);
		if (positionTimer != null)
			positionTimer.cancel();
		if (findLostBeaconTimer != null)
			findLostBeaconTimer.cancel();
		Log.i("scan service bluetooth", "end");
		Timestamp end = new Timestamp(System.currentTimeMillis());
		int min = (int) Math.ceil((end.getTime() - startTime.getTime())
				/ (1000 * 60));
		if (showList != null && showList.size() > 0) {
			InspectedBeacon ib = showList.get(0);
			int buildingNumber = ib.getBuildingNumber();
			ModelService.insertInspection(this, startTime, end, buildingNumber);
			Toast.makeText(this, "巡检总共进行了 " + min + " min, 成功记录", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "巡检总共进行了 " + min + " min, 写入记录失败\n可能是没有更新数据",
					Toast.LENGTH_LONG).show();
		}
		telephonyManager.listen(myPhonestateListener,
				PhoneStateListener.LISTEN_NONE);
		super.onDestroy();
	}

	class MyPhonestateListener extends PhoneStateListener {

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			super.onSignalStrengthsChanged(signalStrength);

			SignalUtil.updateCellLocation(connectivityManager,
					telephonyManager, cellState, neighbors);
			// Toast.makeText(ScanService.this,
			// "network " + cellState.getNetworkType(), 500).show();
			SignalUtil.updateWireless(signalStrength, cellState);

			// Toast.makeText(
			// ScanService.this,
			// "可能是XG信号强度" + cellState.getSignalStrength() + " "
			// + cellState.getRsrq() + " "
			// + cellState.getNetworkType() + " "
			// + cellState.getSinr(), 500).show();
			// SignalUtil.classInspector(signalStrength);
		}

		@Override
		public void onServiceStateChanged(ServiceState serviceState) {
			super.onServiceStateChanged(serviceState);
			Log.i(LOG_TAG, "current voice state"
					+ serviceState.getState());

		}

		@Override
		public void onMessageWaitingIndicatorChanged(boolean mwi) {
			super.onMessageWaitingIndicatorChanged(mwi);
			Log.i(LOG_TAG,
					" message-waiting indicator " + mwi);
		}

		@Override
		public void onCallForwardingIndicatorChanged(boolean cfi) {

			super.onCallForwardingIndicatorChanged(cfi);
			Log.i(LOG_TAG,
					" call-forwarding indicator  " + cfi);
		}

		@Override
		public void onCellLocationChanged(CellLocation location) {

			super.onCellLocationChanged(location);
			Log.i(LOG_TAG, " current celllocation cid  "
					+ ((GsmCellLocation) location).getCid());
			Log.i(LOG_TAG, " neighbor cell number "
					+ telephonyManager.getNeighboringCellInfo().size());
		}

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			super.onCallStateChanged(state, incomingNumber);
			Log.i(LOG_TAG, " state " + state
					+ " incomingNumber " + incomingNumber);
		}

		@Override
		public void onDataConnectionStateChanged(int state) {

			super.onDataConnectionStateChanged(state);
			Log.i(LOG_TAG, " state " + state);

		}

		@Override
		public void onDataConnectionStateChanged(int state, int networkType) {

			super.onDataConnectionStateChanged(state, networkType);
			Log.i(LOG_TAG, " state " + state
					+ " networkType " + networkType);
		}

		@Override
		public void onDataActivity(int direction) {

			super.onDataActivity(direction);
			Log.i(LOG_TAG, " direction " + direction);
		}

		@Override
		public void onCellInfoChanged(List<CellInfo> cellInfo) {
			super.onCellInfoChanged(cellInfo);
			Log.i(LOG_TAG, " cellInfo size "
					+ (cellInfo == null ? "null" : cellInfo.size()));
		}

	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			// Log.d("callback", device.getAddress() + "\n" + rssi);
			bleNoReactCount = 0;
			if (device.getAddress() != null && rssi <= 0) {
				int txPower = BeaconUtil.getBeaconTxPower(scanRecord);
				// 针对某些没有设置txpower的蓝牙芯片，设置默认的参考发射功率
				if (txPower > 0) {
					txPower = -65;
				}

				int dis = BeaconUtil.calculateAccuracy(txPower, rssi);
				ModelService.updateBeacon(ScanService.this, beaconSet,
						new Beacon(device.getAddress(), rssi, txPower, dis));

			}
		}
	};

}

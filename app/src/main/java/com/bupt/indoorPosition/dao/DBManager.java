package com.bupt.indoorPosition.dao;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bupt.indoorPosition.bean.Beacon;
import com.bupt.indoorPosition.bean.BeaconInfo;
import com.bupt.indoorPosition.bean.IndoorRecord;
import com.bupt.indoorPosition.bean.InspectedBeacon;
import com.bupt.indoorPosition.bean.LocalizationBeacon;
import com.bupt.indoorPosition.bean.Inspection;
import com.bupt.indoorPosition.bean.Inspector;
import com.bupt.indoorPosition.bean.Neighbor;
import com.bupt.indoorPosition.bean.Speed;
import com.bupt.indoorPosition.bean.SpeedTestStatus;
import com.bupt.indoorPosition.bean.UserSetting;
import com.bupt.indoorPosition.fragment.FragmentInspection.BeaconSimpleAdapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBManager {
	private DBHelper dbHelper;
	private SQLiteDatabase db;

	/* speedteststatus */
	private static final String DELETE_ALL_TABLESPEEDTEST = "delete from " + DBHelper.tableSpeedTest;
	private static final String INSERT_TABLESPEEDTEST = "insert into " + DBHelper.tableSpeedTest
			+ " (status) values (?);";
	private static final String SELECT_TABLESPEEDTEST = "select status from " + DBHelper.tableSpeedTest;
	/* beaconinfo */
	private static final String DELETE_ALL_BEACONINFOLIST = "delete from " + DBHelper.tableBeaconInfoList;
	private static final String INSERT_BEACONINFOLIST = "insert into " + DBHelper.tableBeaconInfoList
			+ " (uuidFK,mac,beaconRssi,bTxPower,distance) values (?,?,?,?,?);";
	private static final String SELECT_ALL_BEACONINFOLIST = "select uuidFK,mac,beaconRssi,bTxPower,distance from "
			+ DBHelper.tableBeaconInfoList;
	/* speed */
	private static final String DELETE_ALL_SPEEDLIST = "delete from " + DBHelper.tableSpeedList;
	private static final String INSERT_SPEEDLIST = "insert into " + DBHelper.tableSpeedList
			+ " (uuidFK,dl_bps,ul_bps) values (?,?,?);";
	private static final String SELECT_ALL_SPEEDLIST = "select uuidFK,dl_bps,ul_bps from " + DBHelper.tableSpeedList;

	/* indoorrecord */
	private static final String INSERT_INDOORRECORD = "insert into " + DBHelper.tableIndoorRecord
			+ " (signalStrength,cid,position,time,netType,networkType,lac,mnc,uuid,rsrp,rsrq,sinr,imsi) "
			+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?);";
	private static final String DELETE_ALL_INDOORRECORD = "delete from " + DBHelper.tableIndoorRecord;
	private static final String SELECT_ALL_INDOORRECORD = "select signalStrength,cid,position,"
			+ "time,netType,networkType,lac,mnc,uuid,rsrp,rsrq,sinr,imsi from " + DBHelper.tableIndoorRecord;
	/* neighbor */
	private static final String DELETE_ALL_NEIGHBORLIST = "delete from " + DBHelper.tableNeighborRecord;
	private static final String INSERT_NEIGHBOR = "insert into " + DBHelper.tableNeighborRecord
			+ " (uuidFK,cid,signalStrength,network,lac,time) values (?,?,?,?,?,?)";
	private static final String SELECT_NEIGHBOR = "select uuidFK,cid,signalStrength,network,lac,time from "
			+ DBHelper.tableNeighborRecord;

	/* localization */
	private static final String DELETEL_ALL_LOCALIZATION = "delete from " + DBHelper.tableLocalization;
	private static final String INSERT_LOCALIZATION = "insert into " + DBHelper.tableLocalization
			+ " (_id,buildingName,buildingNumber,description,x,y,floor) values (?,?,?,?,?,?,?);";
	private static final String SELECT_LOCALIZATION = "select _id from " + DBHelper.tableLocalization
			+ " where _id=?";
	private static final String SELECT_LOCALIZATIONXY = "select x,y from " + DBHelper.tableLocalization
			+ " where _id=?";
	
	private static final String SELECT_ALL_LOCALIZATION = "select _id from " + DBHelper.tableLocalization;

	/* beacon */
	private static final String DELETEL_ALL_BEACON = "delete from " + DBHelper.tableBeacon;
	private static final String INSERT_BEACON = "insert into " + DBHelper.tableBeacon
			+ " (_id,buildingName,buildingNumber,description,floor) values (?,?,?,?,?);";
	private static final String SELECT_BEACONS_IN_SAME_BUILDING = "select _id,floor,description,buildingName,buildingNumber from "
			+ DBHelper.tableBeacon + " where buildingNumber = (select buildingNumber from " + DBHelper.tableBeacon
			+ " where _id = ?) order by floor";
	private static final String SELECT_BUILDING = "select buildingName,buildingNumber from " + DBHelper.tableBeacon
			+ " where _id =? ";
	private static final String SELECT_BEACON = "select _id from " + DBHelper.tableBeacon + " where _id=?";
	private static final String SELECT_ALL_BEACON = "select _id from " + DBHelper.tableBeacon;
	/* inspection */
	private static final String DELETE_INPECTION = "delete from " + DBHelper.tableInspection;
	private static final String INSERT_INPECTION = "insert into " + DBHelper.tableInspection
			+ "(username,startTime,endTime,duration,buildingId,province,city,longitude,latitude) "
			+ "values(?,?,?,?,?,?,?,?,?)";
	private static final String SELECT_ALL_INSPECTION = "select username,startTime,endTime,duration,"
			+ "buildingId,province,city,longitude,latitude from " + DBHelper.tableInspection;
	/* inspector */
	private static final String DELETE_INPECTOR = "delete from " + DBHelper.tableInspector;
	private static final String INSERT_INPECTOR = "insert into " + DBHelper.tableInspector
			+ "(username,phoneNumber,imsi,imei,province,city,companyName,password) " + "values(?,?,?,?,?,?,?,?)";
	private static final String SELECT_ALL_INSPECTOR = "select username,phoneNumber,imsi,"
			+ "imei,province,city,companyName,password" + " from " + DBHelper.tableInspector;

	/* cookie */
	private static final String DELETE_COOKIE = "delete from " + DBHelper.tableCookie;
	private static final String INSERT_COOKIE = "insert into " + DBHelper.tableCookie + "(key,value) " + "values(?,?)";
	private static final String SELECT_ALL_COOKIE = "select key,value " + " from " + DBHelper.tableCookie;

	/* usersetting */
	private static final String DELETE_USER_SETTING = "delete from " + DBHelper.tableSetting;
	private static final String INSERT_USER_SETTING = "insert into " + DBHelper.tableSetting + "(username,isTestSpeed) "
			+ "values(?,?)";
	private static final String SELECT_USER_SETTING = "select username,isTestSpeed " + " from " + DBHelper.tableSetting
			+ " where username=?";

	/* beaconDebug */
	private static final String INSERT_BEACON_DEBUG = "insert into " + DBHelper.tableBeaconDebug
			+ " (mac,time,beaconRssi,bTxPower,uuidFK) values(?,?,?,?,?)";
	private static final String DELETE_BEACON_DEBUG = "delete from " + DBHelper.tableBeaconDebug;
	private static final String SELECT_BEACON_DEBUG = "select mac,time,beaconRssi,bTxPower,uuidFK from "
			+ DBHelper.tableBeaconDebug;

	/* tableTrainDataCollection */
	private static final String INSERT_TRAINDATA_DEBUG = "insert into " + DBHelper.tableTrainDataCollection
			+ " (mac, beaconRssi,uuid,marker) values(?,?,?,?)";
	private static final String DELETE_TRAINDATA_DEBUG = "delete from " + DBHelper.tableTrainDataCollection;
	private static final String SELECT_TRAINDATA_DEBUG = "select mac, beaconRssi,uuid,marker from "
			+ DBHelper.tableTrainDataCollection;

	public DBManager(Context context) {
		dbHelper = DBHelper.getInstance(context);
		db = dbHelper.getWritableDatabase();
	}

	public void deleteAllSpeedTest() {
		db.execSQL(DELETE_ALL_TABLESPEEDTEST);
	}

	public void insertSpeedTest(SpeedTestStatus speedteststatus) {
		db.execSQL(INSERT_TABLESPEEDTEST, new Object[] { speedteststatus.getStatus() });
	}

	public List<SpeedTestStatus> selectAllSpeedTest() {
		Cursor c = db.rawQuery(SELECT_TABLESPEEDTEST, new String[] {});
		List<SpeedTestStatus> list = new ArrayList<SpeedTestStatus>();
		while (c.moveToNext()) {
			String status = c.getString(c.getColumnIndex("status"));
			list.add(new SpeedTestStatus(status));
		}
		c.close();
		return list;
	}

	public void insertSpeedList(Speed speed) {
		db.execSQL(INSERT_SPEEDLIST, new Object[] { speed.getUuidFK(), speed.getDl_bps(), speed.getUl_bps() });
	}

	public List<Speed> selectAllSpeedList() {
		Cursor c = db.rawQuery(SELECT_ALL_SPEEDLIST, new String[] {});
		List<Speed> list = new ArrayList<Speed>();
		while (c.moveToNext()) {
			String uuidFK = c.getString(c.getColumnIndex("uuidFK"));
			int dl_bps = c.getInt(c.getColumnIndex("dl_bps"));
			int ul_bps = c.getInt(c.getColumnIndex("ul_bps"));
			list.add(new Speed(uuidFK, dl_bps, ul_bps));
		}
		c.close();
		return list;
	}

	public void deleteAllSpeedList() {
		db.execSQL(DELETE_ALL_SPEEDLIST);
	}

	public void deleteAllNeighborList() {
		db.execSQL(DELETE_ALL_NEIGHBORLIST);
	}

	public void insertBeaconInfo(BeaconInfo beaconinfolist) {
		db.execSQL(INSERT_BEACONINFOLIST, new Object[] { beaconinfolist.getUuidFK(), beaconinfolist.getMac(),
				beaconinfolist.getBeaconRssi(), beaconinfolist.getbTxPower(), beaconinfolist.getDistance() });
	}

	public void deleteAllBeaconInfo() {
		db.execSQL(DELETE_ALL_BEACONINFOLIST);
	}

	public List<BeaconInfo> selectAllBeaconInfo() {
		Cursor c = db.rawQuery(SELECT_ALL_BEACONINFOLIST, new String[] {});
		List<BeaconInfo> list = new ArrayList<BeaconInfo>();
		while (c.moveToNext()) {
			String uuidFK = c.getString(c.getColumnIndex("uuidFK"));
			String mac = c.getString(c.getColumnIndex("mac"));
			int beaconRssi = c.getInt(c.getColumnIndex("beaconRssi"));
			int bTxPower = c.getInt(c.getColumnIndex("bTxPower"));
			int distance = c.getInt(c.getColumnIndex("distance"));
			list.add(new BeaconInfo(uuidFK, mac, beaconRssi, bTxPower, distance));
		}
		c.close();
		return list;
	}

	// 新增定位beacon数据库操作
	public void refreshLocalization(List<LocalizationBeacon> ids) {
		if (ids == null) {
			Log.d("DBManager", "list is null");
			return;
		}
		db.beginTransaction();
		db.execSQL(DELETEL_ALL_LOCALIZATION);
		Log.d("ShowBeacon", "" + ids.size());
		for (LocalizationBeacon bi : ids) {
			db.execSQL(INSERT_LOCALIZATION, new Object[] { bi.getMac(), bi.getBuildingName(), bi.getBuildingNumber(),
					bi.getDescription(), bi.getX(), bi.getY(), bi.getFloor() });
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public boolean isContainLocalization(String id) {
		// Log.d("DBManager", "enter");
		Cursor c = db.rawQuery(SELECT_LOCALIZATION, new String[] { id });
		boolean find = false;
		while (c.moveToNext()) {
			String _id = c.getString(c.getColumnIndex("_id"));
			// Log.d("DBManager",_id);
			if (_id != null && _id.equals(id)) {
				find = true;
				break;
			}
		}
		c.close();
		return find;
	}

	public List<Integer> LocalizationXY(String id) {
		Cursor c = db.rawQuery(SELECT_LOCALIZATIONXY, new String[] { id });
		List<Integer> list = new ArrayList<Integer>();
		while (c.moveToNext()) {
			int x = c.getInt(c.getColumnIndex("x"));
			int y = c.getInt(c.getColumnIndex("y"));
			list.add(x);
			list.add(y);
		}
		c.close();
		return list;
	}

	public List<String> readLocalization() {
		Cursor c = db.rawQuery(SELECT_ALL_LOCALIZATION, new String[] {});
		// Cursor c =db.query(DBHelper.tableBeacon, new String[]{"_id"}, null,
		// null, null, null, null);
		List<String> list = new ArrayList<String>();
		while (c.moveToNext()) {
			String id = c.getString(c.getColumnIndex("_id"));
			list.add(id);
		}
		c.close();
		return list;
	}

	// (_id,buildingName,buildingNumber,description,floor) values (?,?,?,?,?);";
	public void refreshBeacon(List<InspectedBeacon> ids) {
		if (ids == null) {
			Log.d("DBManager", "list is null");
			return;
		}
		db.beginTransaction();
		db.execSQL(DELETEL_ALL_BEACON);
		Log.d("ShowBeacon", "" + ids.size());
		for (InspectedBeacon bi : ids) {
			db.execSQL(INSERT_BEACON, new Object[] { bi.getMac(), bi.getBuildingName(), bi.getBuildingNumber(),
					bi.getDescription(), bi.getFloor() });
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public boolean isContainBeacon(String id) {
		// Log.d("DBManager", "enter");
		Cursor c = db.rawQuery(SELECT_BEACON, new String[] { id });
		boolean find = false;
		while (c.moveToNext()) {
			String _id = c.getString(c.getColumnIndex("_id"));
			// Log.d("DBManager",_id);
			if (_id != null && _id.equals(id)) {
				find = true;
				break;
			}
		}
		c.close();
		return find;
	}

	public List<String> readBeacon() {
		Cursor c = db.rawQuery(SELECT_ALL_BEACON, new String[] {});
		// Cursor c =db.query(DBHelper.tableBeacon, new String[]{"_id"}, null,
		// null, null, null, null);
		List<String> list = new ArrayList<String>();
		while (c.moveToNext()) {
			String id = c.getString(c.getColumnIndex("_id"));
			list.add(id);
		}
		c.close();
		return list;
	}

	// SELECT_BEACONS_IN_SAME_BUILDING
	public List<InspectedBeacon> getBeaconInfo(String mac) {
		Cursor c = db.rawQuery(SELECT_BEACONS_IN_SAME_BUILDING, new String[] { mac });
		List<InspectedBeacon> list = new ArrayList<InspectedBeacon>();
		while (c.moveToNext()) {
			// floor,description
			String id = c.getString(c.getColumnIndex("_id"));
			int floor = c.getInt(c.getColumnIndex("floor"));
			String description = c.getString(c.getColumnIndex("description"));
			String buildingName = c.getString(c.getColumnIndex("buildingName"));
			int buildingNumber = c.getInt(c.getColumnIndex("buildingNumber"));
			InspectedBeacon bi = new InspectedBeacon();
			bi.setMac(id);
			bi.setFloor(floor);
			bi.setDescription(description);
			bi.setCount(0);
			bi.setBuildingNumber(buildingNumber);
			bi.setBuildingName(buildingName);
			list.add(bi);
		}
		c.close();
		return list;
	}

	// SELECT_BUILDING
	public String getInspectedBuilding(String mac) {
		Cursor c = db.rawQuery(SELECT_BUILDING, new String[] {});
		String str = "";
		while (c.moveToNext()) {
			// buildingName,buildingNumber
			str = c.getString(c.getColumnIndex("buildingName"));
		}
		c.close();
		return str;
	}

	// "
	// (signalStrength,cid,position,time,netType,networkType,lac,mnc,uuid,rsrp,rsrq,sinr)
	// "
	// + "values (?,?,?,?,?,?,?,?,?,?,?,?,?);";
	public void insertIndoorRecord(IndoorRecord record) {
		db.execSQL(INSERT_INDOORRECORD,
				new Object[] { record.getSignalStrength(), record.getCid(), record.getPosition(), record.getTime(),
						record.getNetType(), record.getNetworkType(), record.getLac(), record.getMnc(),
						record.getUuid(), record.getRsrp(), record.getRsrq(), record.getSinr(), record.getImsi() });
	}

	public void deleteAllIndoorRecord() {
		db.execSQL(DELETE_ALL_INDOORRECORD);
	}

	public List<IndoorRecord> selectAllIndoorRecord() {
		Cursor c = db.rawQuery(SELECT_ALL_INDOORRECORD, new String[] {});
		List<IndoorRecord> list = new ArrayList<IndoorRecord>();
		while (c.moveToNext()) {
			int signalStrength = c.getInt(c.getColumnIndex("signalStrength"));
			int cid = c.getInt(c.getColumnIndex("cid"));
			String position = c.getString(c.getColumnIndex("position"));
			// int beaconRssi = c.getInt(c.getColumnIndex("beaconRssi"));
			// int bTxPower = c.getInt(c.getColumnIndex("bTxPower"));
			// int distance = c.getInt(c.getColumnIndex("distance"));
			String netType = c.getString(c.getColumnIndex("netType"));
			String networkType = c.getString(c.getColumnIndex("networkType"));
			int lac = c.getInt(c.getColumnIndex("lac"));
			// String mcc = c.getString(c.getColumnIndex("mcc"));
			String mnc = c.getString(c.getColumnIndex("mnc"));
			String timeStr = c.getString(c.getColumnIndex("time"));
			// String cellRecordTimeStr = c.getString(c
			// .getColumnIndex("cellRecordTime"));
			String uuid = c.getString(c.getColumnIndex("uuid"));
			int rsrp = c.getInt(c.getColumnIndex("rsrp"));
			int rsrq = c.getInt(c.getColumnIndex("rsrq"));
			int sinr = c.getInt(c.getColumnIndex("sinr"));
			String imsi = c.getString(c.getColumnIndex("imsi"));
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Timestamp time = null;

			try {
				if (timeStr != null)
					time = new Timestamp(format.parse(timeStr).getTime());
				// if (cellRecordTimeStr != null)
				// cellRecordTime = new Timestamp(format.parse(
				// cellRecordTimeStr).getTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			list.add(new IndoorRecord(signalStrength, cid, position, time, netType, networkType, lac, mnc, uuid, rsrp,
					rsrq, sinr, imsi));
		}
		c.close();
		return list;
	}

	public void insertNeighborRecord(List<Neighbor> neighbors, String uuid) {
		if (neighbors == null) {
			Log.d("DBManager", "neighbors is null");
			return;
		}
		db.beginTransaction();
		for (Neighbor n : neighbors) {
			// (uuidFK,cid,signalStrength,time)
			db.execSQL(INSERT_NEIGHBOR,
					new Object[] { uuid, n.getCid(), n.getSignalStrength(), n.getNetwork(), n.getLac(), n.getTime() });
		}
		db.setTransactionSuccessful();
		db.endTransaction();

	}

	public List<Neighbor> getNeighborRecord() {
		Cursor c = db.rawQuery(SELECT_NEIGHBOR, new String[] {});
		List<Neighbor> list = new ArrayList<Neighbor>();
		while (c.moveToNext()) {
			int signalStrength = c.getInt(c.getColumnIndex("signalStrength"));
			int cid = c.getInt(c.getColumnIndex("cid"));
			int lac = c.getInt(c.getColumnIndex("lac"));
			String uuidFK = c.getString(c.getColumnIndex("uuidFK"));
			String network = c.getString(c.getColumnIndex("network"));
			String timeStr = c.getString(c.getColumnIndex("time"));
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Timestamp time = null;
			try {
				if (timeStr != null)
					time = new Timestamp(format.parse(timeStr).getTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			list.add(new Neighbor(uuidFK, cid, signalStrength, network, lac, time));
		}
		c.close();
		return list;
	}

	public void deleteInspection() {
		db.execSQL(DELETE_INPECTION);
	}

	// username,startTime,endTime,duration,buildingId,province,city,longitude,latitude
	public void insertInspection(Inspection inspection) {
		db.execSQL(INSERT_INPECTION,
				new Object[] { inspection.getUsername(), inspection.getStartTime(), inspection.getEndTime(),
						inspection.getDuration(), inspection.getBuildingId(), inspection.getProvince(),
						inspection.getCity(), inspection.getLongitude(), inspection.getLatitude() });
	}

	public List<Inspection> selectAllInspection() {
		Cursor c = db.rawQuery(SELECT_ALL_INSPECTION, new String[] {});
		List<Inspection> list = new ArrayList<Inspection>();
		while (c.moveToNext()) {
			// floor,description
			String username = c.getString(c.getColumnIndex("username"));
			int duration = c.getInt(c.getColumnIndex("duration"));
			int buildingId = c.getInt(c.getColumnIndex("buildingId"));
			String province = c.getString(c.getColumnIndex("province"));
			String city = c.getString(c.getColumnIndex("city"));
			double longitude = c.getDouble(c.getColumnIndex("longitude"));
			double latitude = c.getDouble(c.getColumnIndex("latitude"));
			String startTimeStr = c.getString(c.getColumnIndex("startTime"));
			String endTimeStr = c.getString(c.getColumnIndex("endTime"));
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Timestamp startTime = null, endTime = null;

			try {
				if (startTimeStr != null)
					startTime = new Timestamp(format.parse(startTimeStr).getTime());
				if (endTimeStr != null)
					endTime = new Timestamp(format.parse(endTimeStr).getTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			list.add(new Inspection(-1, username, startTime, endTime, duration, buildingId, province, city, longitude,
					latitude));
		}
		c.close();
		return list;
	}

	public void deleteInspector() {
		db.execSQL(DELETE_INPECTOR);
	}

	// username,phoneNumber,imsi,imei,province,city,companyName,password
	public boolean insertInspector(Inspector inspector) {
		if (inspector == null) {
			Log.d("DBManager", "inspection is null");
			return false;
		}
		db.beginTransaction();
		deleteInspector();
		db.execSQL(INSERT_INPECTOR,
				new Object[] { inspector.getUsername(), inspector.getPhoneNumber(), inspector.getImsi(),
						inspector.getImei(), inspector.getProvince(), inspector.getCity(), inspector.getCompanyName(),
						inspector.getPassword() });
		db.setTransactionSuccessful();
		db.endTransaction();
		return true;

	}

	public List<Inspector> selectAllInspector() {
		Cursor c = db.rawQuery(SELECT_ALL_INSPECTOR, new String[] {});
		List<Inspector> list = new ArrayList<Inspector>();
		while (c.moveToNext()) {
			// floor,description
			String username = c.getString(c.getColumnIndex("username"));
			String phoneNumber = c.getString(c.getColumnIndex("phoneNumber"));
			String imsi = c.getString(c.getColumnIndex("imsi"));
			String imei = c.getString(c.getColumnIndex("imei"));
			String province = c.getString(c.getColumnIndex("province"));
			String city = c.getString(c.getColumnIndex("city"));
			String companyName = c.getString(c.getColumnIndex("companyName"));
			String password = c.getString(c.getColumnIndex("password"));

			list.add(new Inspector(username, phoneNumber, imsi, imei, province, city, companyName, null, password));
		}
		c.close();
		return list;
	}

	// public void deleteAllCookie(){
	// db.execSQL(DELETE_COOKIE);
	// }
	public void updateCookie(Map<String, String> container) {
		if (container == null) {
			Log.d("DBManager", "container is null");
			return;
		}
		db.beginTransaction();
		db.execSQL(DELETE_COOKIE);

		for (String key : container.keySet()) {
			db.execSQL(INSERT_COOKIE, new Object[] { key, container.get(key) });
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public HashMap<String, String> selectAllCookie() {
		Cursor c = db.rawQuery(SELECT_ALL_COOKIE, new String[] {});
		HashMap<String, String> container = new HashMap<String, String>();
		while (c.moveToNext()) {
			// floor,description
			String key = c.getString(c.getColumnIndex("key"));
			String value = c.getString(c.getColumnIndex("value"));
			container.put(key, value);
		}
		c.close();
		return container;
	}

	public void deleteAllUserSetting() {
		db.execSQL(DELETE_USER_SETTING);
	}

	public void updateUserSetting(UserSetting us) {
		if (us == null)
			return;
		UserSetting _us = selectUserSetting(us.getUsername());
		if (_us == null) {
			db.execSQL(INSERT_USER_SETTING, new Object[] { us.getUsername(),
					us.isTestSpeed() == true ? Integer.valueOf(1) : Integer.valueOf(0) });
		} else {
			db.beginTransaction();
			db.execSQL(DELETE_USER_SETTING);
			db.execSQL(INSERT_USER_SETTING, new Object[] { us.getUsername(),
					us.isTestSpeed() == true ? Integer.valueOf(1) : Integer.valueOf(0) });
			db.setTransactionSuccessful();
			db.endTransaction();
		}
	}

	public void insertDeafaultUSIfNotExist(String username) {
		UserSetting _us = selectUserSetting(username);
		if (_us == null) {
			db.execSQL(INSERT_USER_SETTING, new Object[] { username, 0 });
		}
	}

	public UserSetting selectUserSetting(String username) {
		Cursor c = db.rawQuery(SELECT_USER_SETTING, new String[] { username });
		UserSetting us = null;
		while (c.moveToNext()) {
			boolean isTestSpeed;
			String un = c.getString(c.getColumnIndex("username"));
			int itp = c.getInt(c.getColumnIndex("isTestSpeed"));
			if (itp > 0)
				isTestSpeed = true;
			else
				isTestSpeed = false;
			us = new UserSetting();
			us.setTestSpeed(isTestSpeed);
			us.setUsername(un);
		}
		c.close();
		return us;
	}

	/**
	 * mac,time,beaconRssi,bTxPower,uuidFK
	 */
	public boolean insertBeaconDebug(Set<Beacon> set, String uuidFK) {
		if (set == null) {
			Log.d("DBManager", "set is null");
			return false;
		}
		db.beginTransaction();
		Iterator<Beacon> iterator = set.iterator();
		Timestamp t = new Timestamp(System.currentTimeMillis());
		while (iterator.hasNext()) {
			Beacon b = iterator.next();
			db.execSQL(INSERT_BEACON_DEBUG, new Object[] { b.getMac(), t, b.getRssi(), b.getTxPower(), uuidFK });
		}

		db.setTransactionSuccessful();
		db.endTransaction();
		return true;
	}

	public void deleteAllBeaconDebug() {
		db.execSQL(DELETE_BEACON_DEBUG);
	}

	/**
	 * mac, beaconRssi,uuid,marker
	 */
	public boolean insertTrainData(Set<Beacon> set, String uuid, String marker) {
		if (set == null) {
			Log.d("DBManager", "train data set is null");
			return false;
		}
		db.beginTransaction();
		Iterator<Beacon> iterator = set.iterator();

		while (iterator.hasNext()) {
			Beacon b = iterator.next();
			db.execSQL(INSERT_TRAINDATA_DEBUG, new Object[] { b.getMac(), b.getRssi(), uuid, marker });
		}

		db.setTransactionSuccessful();
		db.endTransaction();
		return true;
	}

	public void deleteAllTrainData() {
		db.execSQL(DELETE_TRAINDATA_DEBUG);
	}

	/**
	 * mac, beaconRssi,uuid,marker
	 */
	public List<Map<String, Object>> selectAllTrainData() {
		Cursor c = db.rawQuery(SELECT_TRAINDATA_DEBUG, new String[] {});
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while (c.moveToNext()) {

			String mac = c.getString(c.getColumnIndex("mac"));
			int rssi = c.getInt(c.getColumnIndex("beaconRssi"));
			String uuid = c.getString(c.getColumnIndex("uuid"));
			String marker = c.getString(c.getColumnIndex("marker"));
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("mac", mac);
			data.put("rssi", rssi);
			data.put("uuid", uuid);
			data.put("marker", marker);
			list.add(data);
		}
		c.close();
		return list;
	}

}

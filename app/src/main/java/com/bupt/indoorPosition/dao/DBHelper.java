package com.bupt.indoorPosition.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private static DBHelper instance;
	private static final String DATABASE_NAME = "indoor.db";
	private static final int DATABASE_VERSION = 1;
	public static final String tableBeaconInfoList = "beaconinfolist";
	public static final String tableSpeedList = "speedlist";
	public static final String tableBeacon = "beacon";
	public static final String tableLocalization = "localization";
	public static final String tableIndoorRecord = "indoorRecord";
	public static final String tableNeighborRecord = "neighborRecord";
	public static final String tableInspector = "inspector";
	public static final String tableInspection = "inspection";

	public static final String tableCookie = "tableCookie";
	public static final String tableSetting = "tableSetting";
	public static final String tableSpeedTest = "tablespeedtest";

	public static final String tableBeaconDebug = "tableBeaconDebug";
	public static String tableTrainDataCollection = "tableTrainDataCollection";

	public synchronized static DBHelper getInstance(Context context) {
		if (instance == null)
			instance = new DBHelper(context);
		return instance;

	}

	private DBHelper(Context context) {
		// CursorFactory设置为null,使用默认值
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// 数据库第一次被创建时onCreate会被调用
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("DBHelper ", "database on create ");
		// db.execSQL("drop table " + tableBeacon + "; drop table "
		// + tableIndoorRecord + "; drop table " + tableNeighborRecord);
		db.execSQL("CREATE TABLE IF NOT EXISTS " + tableSpeedTest
				+ "(status VARCHAR(32) PRIMARY KEY);");
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ tableBeaconInfoList
				+ "(uuidFK VARCHAR(64) PRIMARY KEY,mac varchar(32),beaconRssi int,bTxPower int,distance int);");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + tableSpeedList
				+ "(uuidFK VARCHAR(64) PRIMARY KEY,dl_bps int,ul_bps int);");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + tableBeacon
				+ "(_id VARCHAR(32) PRIMARY KEY, buildingName VARCHAR(64),"
				+ "buildingNumber int,description VARCHAR(64),floor int );");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + tableLocalization
				+ "(_id VARCHAR(32) PRIMARY KEY, buildingName VARCHAR(64),"
				+ "buildingNumber int,description VARCHAR(64),x int,y int,floor int );");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ tableIndoorRecord
				+ "(id INTEGER PRIMARY KEY AUTOINCREMENT,signalStrength int,"
				+ "cid int,position varchar(32),"
				+ "time timestamp,netType varchar(16),networkType varchar(16), "
				+ "lac varchar(16),mnc varchar(4),uuid varchar(128),rsrp int,rsrq int,sinr int,imsi varchar(16));");
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ tableNeighborRecord
				+ "(id INTEGER PRIMARY KEY AUTOINCREMENT,uuidFK varchar(128),"
				+ "cid int,signalStrength int,network varchar(16) ,lac int ,time timestamp);");
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ tableInspection
				+ "(id INTEGER PRIMARY KEY AUTOINCREMENT,username varchar(64),"
				+ "startTime timestamp,endTime timestamp,duration int,"
				+ "buildingId int,province varchar(32),city varchar(64),longitude double,latitude double);");
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ tableInspector
				+ "(username varchar(64) PRIMARY KEY,phoneNumber varchar(18),"
				+ "imsi varchar(16),imei varchar(16),province varchar(32),city varchar(64),"
				+ "companyName varchar(64),password varchar(64));");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + tableCookie
				+ "(key varchar(256) PRIMARY KEY,value varchar(256));");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + tableSetting
				+ "(username varchar(64),isTestSpeed int);");
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ tableBeaconDebug
				+ "(mac VARCHAR(32),time timestamp,beaconRssi int,bTxPower int,"
				+ "uuidFK VARCHAR(64), PRIMARY KEY(mac,time));");
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ tableTrainDataCollection
				+ "(mac VARCHAR(32),beaconRssi int,"
				+ "uuid VARCHAR(64),marker VARCHAR(16), PRIMARY KEY(uuid,mac));");
	}

	// 如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
	}

}

package xzw.szl.byr.mananger;

import java.util.concurrent.atomic.AtomicInteger;

import android.database.sqlite.SQLiteDatabase;

import xzw.szl.byr.db.DBHelper;
import xzw.szl.byr.utils.NetStatus;

public class DBManager {
	
	private static DBManager instance;
	private static DBHelper mDBHelper;
	private SQLiteDatabase mDB;
	
	private AtomicInteger mConnectCount = new AtomicInteger();
	//根据网络类型，定义缓存有效时间
	private final long TIME_INVALID = 1000 * 1000 * 60 * 60 * 24 * 30; // 1 mouth 
	private final long TIMEOUT_2G = 1000 * 1000 * 60 * 60 * 24; // one day
	private final long TIMEOUT_3G = 1000 * 1000 * 60 * 60 * 12;  // 12 hour
	private final long TIMEOUT_4G = 1000 * 1000 * 60 * 60; //60 minutes
	private final long TIME_WIFI = 1000 * 1000 * 60 * 30; // 30 minutes

	
	
	public static void initalizeDBHelper(DBHelper dbHelper) {
		instance = getInstance();
		mDBHelper = dbHelper;
	}
	public static DBManager getInstance() { // 双重校验锁
		
		if (instance == null) {
			
			synchronized (DBManager.class) {
				
				if (instance == null) {
					instance = new DBManager();
				}
			}
		}
		return instance;
	}
	
	public long timeoutOnCurrentNetwork(int type) {
		switch (type) {
		case NetStatus.NETWORKTYPE_2G:
		case NetStatus.NETWORKTYPE_WAP:
			return TIMEOUT_2G;
		case NetStatus.NETWORKTYPE_3G:
			return TIMEOUT_3G;
		case NetStatus.NETWORKTYPE_4G:
			return TIMEOUT_4G;
		case NetStatus.NETWORKTYPE_WIFI:
			return TIME_WIFI;
		case NetStatus.NETWORKTYPE_INVALID:
		default:
			return TIME_INVALID;
		}
	}
	
	public SQLiteDatabase openDatabase() {
		
		synchronized (instance) {
			if (mConnectCount.incrementAndGet() == 1) {
				mDB = mDBHelper.getWritableDatabase();
			}
			return mDB;
		}
	}
	
	//关闭数据连接 
	public void closeDatabase() {
	//	synchronized (instance) {
	//		if (mConnectCount.decrementAndGet() == 0) {
				mDB.close();
	//		}
	//	}
	}
}

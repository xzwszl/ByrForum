package xzw.szl.byr.db;

import xzw.szl.byr.mananger.DBManager;

public class DBTableHandler {
	
	
	
	public void deleteAllFromTable(String table) {
		DBManager.getInstance().openDatabase().delete(
				table, 
				null, 
				null);
	}
	
	public long  getCurrentTime() {
		return System.currentTimeMillis();
	}
}

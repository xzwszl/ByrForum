package xzw.szl.byr.db;

import xzw.szl.byr.mananger.DBManager;
import xzw.szl.byr.utils.ByrBase;
import xzw.szl.byr.utils.DataUtils;
import xzw.szl.byr.utils.NetStatus;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBListTableHandler extends DBTableHandler{
	
	private static DBListTableHandler instance;
	
	private DBListTableHandler() {};
	
	public static final String TYPE_TOPTEN = "topten";
	public static final String TYPE_FAVORITE = "favorite";
	public static final String TYPE_VOTE = "vote";
	
//	public static final String TYPE_BOARD = 2;
//	public static final String TYPE_ARTICLE = 3;
//	public static final String TYPE_INBOX = "";
//	public static final String TYPE_OUTBOX = 5;
//	public static final String TYPE_DELBOX = 6;
//	public static final String TYPE_AT = 7;
//	public static final String TYPE_REPLY = 8;
//	public static final String TYPE_THREAD = 9;
	
	public static DBListTableHandler getInstance() {  //单例模式
		
		if (instance == null) {
			synchronized (DBListTableHandler.class) {
				if (instance == null) instance = new DBListTableHandler();
			}
		}
		return instance;
	}
	//增删改查
	public void insertListTable(String type,int page ,String content) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.MsgList.LIST_COLUMN_TYPE, type);
		values.put(DBHelper.MsgList.LIST_COLUMN_PAGE, page);
		values.put(DBHelper.MsgList.LIST_COLUMN_CONTENT, content);
		values.put(DBHelper.MsgList.LIST_COLUMN_TIME,super.getCurrentTime());
		
		DBManager.getInstance().openDatabase().insert(DBHelper.MsgList.LIST_TABLE_NAME, null, values);
	}
	
	//query
	public String queryItemContent(String type,int page) {
		Cursor cursor = DBManager.getInstance().openDatabase().query(DBHelper.MsgList.LIST_TABLE_NAME, 
				new String[]{DBHelper.MsgList.LIST_COLUMN_CONTENT,DBHelper.MsgList.LIST_COLUMN_TIME},
				DBHelper.MsgList.LIST_COLUMN_TYPE + "=? and " + DBHelper.MsgList.LIST_COLUMN_PAGE + "=?", 
				new String[]{type,String.valueOf(page)},
				null,
				null,
				null);
		String content = null;
		//只要第一条记录就可以了
		if (cursor != null) {
			
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				 long time = Long.valueOf(cursor.getString(cursor.getColumnIndex(DBHelper.MsgList.LIST_COLUMN_TIME)));
				 long deta = DBManager.getInstance().timeoutOnCurrentNetwork(NetStatus.currentState);
				 long currentTime = System.currentTimeMillis();
				 
				 if (currentTime - time > deta) {
					 // 清除记录
					 deleteItemFromListTalbe(type, page);
				 } else {
					 content = cursor.getString(cursor.getColumnIndex(DBHelper.MsgList.LIST_COLUMN_CONTENT)); 
				 }
			}
			cursor.close();
		}
		return content;
	}
	
	//update one list
	public void updateItemListTable(String type,int page,String content) {
		
		ContentValues values = new ContentValues();
		values.put(DBHelper.MsgList.LIST_COLUMN_TYPE, type);
		values.put(DBHelper.MsgList.LIST_COLUMN_CONTENT, content);
		values.put(DBHelper.MsgList.LIST_COLUMN_PAGE,page);
		values.put(DBHelper.MsgList.LIST_COLUMN_TIME, super.getCurrentTime());
		
		DBManager.getInstance().openDatabase().update(DBHelper.MsgList.LIST_TABLE_NAME,
				values,
				DBHelper.MsgList.LIST_COLUMN_TYPE + "=? and " 
				+ DBHelper.MsgList.LIST_COLUMN_PAGE + "=?",
				new String[]{type,String.valueOf(page)});
	};
	
	//update all list every page should be reomved and insert new page
	public void updateTypeListTable(String type, int page, String content) {
		//query page is?存在  不存在-- insert or 存在   delete and insert
		//db持有数据库引用，所以其他的不会关闭，持有的引用都是同一个，故可以使用在事务中
		SQLiteDatabase db = DBManager.getInstance().openDatabase(); 

		db.beginTransaction();
		
		try {
			String result = queryItemContent(type, page);
			
			if (result != null) {
				deleteTypeFromListTable(type);
			}
			insertListTable(type, page, content);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			
		} finally {
			db.endTransaction();
		}
	
	}
	
	//update info of one item 
	public void updateInfoOfOneItem(String type,int page,String key,String replace,int pos) {
		SQLiteDatabase db = DBManager.getInstance().openDatabase(); 

		db.beginTransaction();
		
		try {
			String content = queryItemContent(type, page);
			if (content != null) {
				content = DataUtils.getStringAfterDeal(content, key, replace,  pos);
				updateItemListTable(type, page, content);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			
		} finally {
			db.endTransaction();
		}
	}
	
	// delete one item from listtable
	public void deleteItemFromListTalbe(String type, int page) {
		DBManager.getInstance().openDatabase().delete(DBHelper.MsgList.LIST_TABLE_NAME,
				DBHelper.MsgList.LIST_COLUMN_TYPE + " =? and "
				+ DBHelper.MsgList.LIST_COLUMN_PAGE + "=?",
				new String[]{type,String.valueOf(page)});
	}
	
	//delete all item of one type from listtable 
	public void deleteTypeFromListTable(String type) {
		DBManager.getInstance().openDatabase().delete(DBHelper.MsgList.LIST_TABLE_NAME,
				DBHelper.MsgList.LIST_COLUMN_TYPE + " =?",
				new String[]{type});
	}
	
	public void deleteAllFromListTable() {
		super.deleteAllFromTable(DBHelper.MsgList.LIST_TABLE_NAME);
		
	}
	
}

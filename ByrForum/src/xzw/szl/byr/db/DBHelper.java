package xzw.szl.byr.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author sony
 * 1 types of DB
 * list(topten|favorite) 1组数据
 * list(article/board/section) 多组数据 
 * 
 */
public class DBHelper extends SQLiteOpenHelper{
	
	public static final String DB_NAME = "byr.db";
	public static final int DB_VERSION = 2;
	
	public interface MsgList{
		public String LIST_TABLE_NAME = "table_list";
		public String LIST_COLUMN_ID = "id";
		public String LIST_COLUMN_TYPE = "type";
		public String LIST_COLUMN_PAGE = "page";
		public String LIST_COLUMN_CONTENT = "content";
		public String LIST_COLUMN_TIME = "time";
		
	}
	
	private static final String LIST_CREATE_TABLE = "CREATE TABLE " + MsgList.LIST_TABLE_NAME 
			+ "(" + MsgList.LIST_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ MsgList.LIST_COLUMN_TYPE + " VARCHAR(250) NOT NULL,"
			+ MsgList.LIST_COLUMN_PAGE + " INTEGER NOT NULL,"
			+ MsgList.LIST_COLUMN_CONTENT + " TEXT NOT NULL,"
			+ MsgList.LIST_COLUMN_TIME + " VARCHAR(250) NOT NULL);";
	
	public DBHelper(Context context) {
		super(context, DB_NAME,null,DB_VERSION);
	}

	

	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建数据库
		db.execSQL(LIST_CREATE_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		if (oldVersion != newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + MsgList.LIST_TABLE_NAME);
			onCreate(db);
		}	
	}
	
}

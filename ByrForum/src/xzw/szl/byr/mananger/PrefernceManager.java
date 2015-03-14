package xzw.szl.byr.mananger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public enum PrefernceManager {
	
	INSTANCE;
	
	public String currentUser;
	public boolean can2G3GOnImageload = false;
	public boolean canMessageRemind = false;
	public String mBoxDec="";
	public int mAtCount;
	public int mReplyCount;
	
	public static PrefernceManager getInstance() {
		return INSTANCE;
	}
	
	public boolean getCurrentStateOf2G3GOnImageload(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(currentUser, Context.MODE_PRIVATE);
		return preferences.getBoolean("2G3GOnImageload",true);
	}
	
	public boolean getCurrentStateOfMessageRemind(Context context) {
		return context.getSharedPreferences(currentUser, Context.MODE_PRIVATE).getBoolean("messageRemind", true);
	}
	
	public void update2G3GOnImageload(Context context,boolean state) {
		can2G3GOnImageload = state;
		SharedPreferences preferences = context.getSharedPreferences(currentUser, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putBoolean("2G3GOnImageload", state);
		edit.commit();
	}
	
	public void updateMessageRemind(Context context,boolean state) {
		canMessageRemind = state;
		SharedPreferences preferences = context.getSharedPreferences(currentUser, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putBoolean("messageRemind", state);
		edit.commit();
	}
	public int getCurrentThemeState(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(currentUser, Context.MODE_PRIVATE);
		return preferences.getInt("style", 0);
	}
	
	public void updateCurrenThemeState(Context context,int state) {
		SharedPreferences preferences = context.getSharedPreferences(currentUser, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putInt("style", state);
		edit.commit();
	}

	public String getCurrentUserName(Context context) {
		SharedPreferences prefrences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
		return prefrences.getString("account", null);
	}
	
	public String getCurrentUserPassword(Context context) {
		SharedPreferences prefrences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
		return prefrences.getString("password", null);
	}
	
	public void updateCurrentUser(Context context,String account, String password) {
		SharedPreferences prefrences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
		Editor editor = prefrences.edit();
		
		editor.putString("account",account);
		editor.putString("password",password);
		editor.commit();	
	}
	
	public String getCurrentBoxDes(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(currentUser, Context.MODE_PRIVATE);
		return preferences.getString("boxdes", "");
	}
	
	public int getCurrentReferCount(Context context,String type) {
		SharedPreferences preferences = context.getSharedPreferences(currentUser, Context.MODE_PRIVATE);
		return preferences.getInt(type,0);
	}
	
	public void updateCurrentBoxDes(Context context,String boxdes) {
		mBoxDec = boxdes;
		SharedPreferences prefrences = context.getSharedPreferences(currentUser, Context.MODE_PRIVATE);
		Editor editor = prefrences.edit();
		
		editor.putString("boxdes",boxdes);
		editor.commit();	
	}
	
	public void updateCurrentReferCount(Context context,String type,int count) {
		
		if (type.equals("at")) mAtCount = count;
		else mReplyCount = count;
		SharedPreferences prefrences = context.getSharedPreferences(currentUser, Context.MODE_PRIVATE);
		Editor editor = prefrences.edit();
		
		editor.putInt(type,count);
		editor.commit();	
	}
}

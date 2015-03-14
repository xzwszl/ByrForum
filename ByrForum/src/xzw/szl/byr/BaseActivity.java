package xzw.szl.byr;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;

import xzw.szl.byr.info.Count;
import xzw.szl.byr.info.Mailbox;
import xzw.szl.byr.mananger.ByrThreadPool;
import xzw.szl.byr.service.BroadcastService;
import xzw.szl.byr.utils.HttpUtils;
import xzw.szl.byr.utils.JsonUtils;
import xzw.szl.byr.utils.NetStatus;
import xzw.szl.byr.utils.ViewUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public abstract class BaseActivity extends SherlockFragmentActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		init();
		super.onCreate(savedInstanceState);
	

	}
	
	private void init() {
		ViewUtils.switchTheme(this);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	
	private BroadcastReceiver netweorkBR = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			
			NetStatus.currentState = NetStatus.getCurrentNetworkType(context);
//			ViewUtils.displayMessage(context, "" + NetStatus.currentState);
		}
		
	};
	
	@Override
	protected void onResume() {
		registerReceiver(netweorkBR, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		MobclickAgent.onResume(getApplicationContext());
		super.onResume();
	};
	
	@Override
	protected void onPause() {
		unregisterReceiver(netweorkBR);
		MobclickAgent.onPause(getApplicationContext());
		super.onPause();
	}
}

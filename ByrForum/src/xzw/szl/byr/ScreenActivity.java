package xzw.szl.byr;


import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateDialogActivity;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import xzw.szl.byr.R;
import xzw.szl.byr.mananger.ByrThreadPool;
import xzw.szl.byr.mananger.PrefernceManager;
import xzw.szl.byr.utils.ByrBase;
import xzw.szl.byr.utils.DataUtils;
import xzw.szl.byr.utils.FileUtils;
import xzw.szl.byr.utils.HttpUtils;
import xzw.szl.byr.utils.NetStatus;
import xzw.szl.byr.utils.ViewUtils;

public class ScreenActivity extends Activity {

	private SharedPreferences  preferences;
	private static Handler handler= new Handler();
//	private 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			
			@Override
			public void onUpdateReturned(int updateStatus, UpdateResponse arg1) {
				if (updateStatus == UpdateStatus.No || updateStatus == UpdateStatus.NotNow || updateStatus == UpdateStatus.Timeout
						|| UpdateStatus.Ignore == updateStatus || UpdateStatus.NoneWifi == updateStatus) {
					init();
				}
			}
		});
		
		UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {
			
			@Override
			public void onClick(int arg0) {
				//取消
				if (arg0 == 6) {
					init();
				}
			}
		});
		UmengUpdateAgent.update(getApplicationContext());
		setContentView(R.layout.activity_screen);
	}
	
	//	requestWindowFeature(Window.FEATURE_NO_TITLE);
		
	
	public void init() {
		getMetrics();
		//					initByrTHreadPool();
		
		FileUtils.createDir();
		NetStatus.currentState = NetStatus.getCurrentNetworkType(getApplicationContext());
		ViewUtils.isDayTheme = ViewUtils.isDayTheme(getApplicationContext());
		preferences = getSharedPreferences("user",Context.MODE_PRIVATE);
		String username = preferences.getString("account",null);
		String password = preferences.getString("password",null);
		
		if (username == null || password == null) {
			//打开LoginAcitvity
			goToActivity(LoginActivity.class,null);
		} else {
			// 打开HomeActivity
			HttpUtils.setBasicCredentitalsProvider(username, password);
			login();
		}
	}
	// 执行登录操作，判断网络连接
	private Runnable r = new Runnable() {
		public void run() {

			HttpUtils.httpRequest("/user/login.json",new HttpUtils.HttpRequestListener() {

				@Override
				public void onSuccess(String content) {
					
					goToActivity(HomeActivity.class,null);
				}

				@Override
				public void onFailed(String reason) {
					goToActivity(LoginActivity.class,reason);
				}

				@Override
				public void onError(Throwable e) {
					goToActivity(LoginActivity.class,getResources().getString(R.string.network_error));
				}
			});
		}
	};
	
	
	private void login() {
		ByrThreadPool.getTHreadPool().execute(r);
	}
	
	private void getMetrics() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		DataUtils.setMetrics(metrics);
	}
	
//	private void initByrTHreadPool() {
//		ByrThreadPool.CreateByrThreadPoll();
//	}
	
	private void goToActivity(final Class<?> clazz,final String tips) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				PrefernceManager.getInstance().can2G3GOnImageload = PrefernceManager.getInstance().getCurrentStateOf2G3GOnImageload(getApplicationContext());
				PrefernceManager.getInstance().canMessageRemind = PrefernceManager.getInstance().getCurrentStateOfMessageRemind(getApplicationContext());
				startActivity(new Intent(ScreenActivity.this,clazz));
				if (tips != null)
					ViewUtils.displayMessage(ScreenActivity.this, tips);
				ScreenActivity.this.finish();
				
			}
		};
		handler.postDelayed(r, 100);
	}
}

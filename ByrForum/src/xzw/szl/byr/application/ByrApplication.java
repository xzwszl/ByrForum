package xzw.szl.byr.application;
import org.apache.http.client.HttpClient;

import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import xzw.szl.byr.db.DBHelper;
import xzw.szl.byr.mananger.ByrThreadPool;
import xzw.szl.byr.mananger.DBManager;
import xzw.szl.byr.mananger.ImageCacheManager;
import xzw.szl.byr.mananger.ImageCacheManager2;
import xzw.szl.byr.service.BroadcastService;
import xzw.szl.byr.utils.HttpUtils;
import android.app.Application;
import android.content.Intent;

public class ByrApplication extends Application{
	

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		HttpUtils.context = this;
		//初始化数据库连接
		DBManager.initalizeDBHelper(new DBHelper(getApplicationContext()));
		MobclickAgent.updateOnlineConfig(getApplicationContext());
		AnalyticsConfig.enableEncrypt(true);
		UmengUpdateAgent.setDeltaUpdate(true);
	}

	@Override
	public void onTerminate() {
		
		HttpUtils.shutdownHttpClient();
		ByrThreadPool.getTHreadPool().shutdown();
		stopService(new Intent(this,BroadcastService.class));
		DBManager.getInstance().closeDatabase();
		ImageCacheManager.getInstance().clear();
		ImageCacheManager2.getInstance().clear();
		super.onTerminate();
	}

	@Override
	public void onLowMemory() {
		ImageCacheManager.getInstance().clear();
		ImageCacheManager2.getInstance().clear();
		super.onLowMemory();
	}
}

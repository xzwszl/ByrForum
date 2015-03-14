package xzw.szl.byr.assist;

import xzw.szl.byr.BaseActivity;
import xzw.szl.byr.R;
import xzw.szl.byr.db.DBListTableHandler;
import xzw.szl.byr.mananger.PrefernceManager;
import xzw.szl.byr.service.BroadcastService;
import xzw.szl.byr.utils.ViewUtils;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.zcw.togglebutton.ToggleButton;
import com.zcw.togglebutton.ToggleButton.OnToggleChanged;

public class PrsActivity extends BaseActivity implements OnToggleChanged, OnClickListener{
	
	private ToggleButton mToggleButton2G3GImage;
	private ToggleButton mToggleButtonMessageRemind;
	private TextView mTextViewCleanCache;
//	private TextView mTextViewCacheInfo;
	private TextView mTextViewAboutUs;
	private TextView mTextViewVersionInfo;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prs);
		init();
	}
	
	private void init() {
		
		mToggleButton2G3GImage = (ToggleButton) findViewById(R.id.toggle_2G3G);
		mToggleButtonMessageRemind = (ToggleButton) findViewById(R.id.toggle_remind);
		
		mToggleButton2G3GImage.setOnToggleChanged(this);
		mToggleButtonMessageRemind.setOnToggleChanged(this);
		
		if (!PrefernceManager.getInstance().can2G3GOnImageload) {
			mToggleButton2G3GImage.setToggleOn();
		} 
		
		if (!PrefernceManager.getInstance().canMessageRemind) {
			mToggleButtonMessageRemind.setToggleOn();
		}
		mTextViewCleanCache = (TextView) findViewById(R.id.text_cache);
//		mTextViewCacheInfo = (TextView) findViewById(R.id.text_cache_info);
		mTextViewAboutUs = (TextView) findViewById(R.id.text_aboutus);
		mTextViewVersionInfo = (TextView) findViewById(R.id.text_version);
		
		mTextViewCleanCache.setOnClickListener(this);
		mTextViewAboutUs.setOnClickListener(this);
		mTextViewVersionInfo.setOnClickListener(this);
	}

	@Override
	public void onToggle(View v,boolean on) {
	
		switch (v.getId()) {
		case R.id.toggle_2G3G:
			PrefernceManager.getInstance().update2G3GOnImageload(getApplicationContext(), !on);
			break;
			
		case R.id.toggle_remind:
			PrefernceManager.getInstance().updateMessageRemind(getApplicationContext(), !on);
			
			if (!on) {
				stopService(new Intent(this,BroadcastService.class));
			} else {
				startService(new Intent(this,BroadcastService.class));
			}
			break;
			
		default:
			break;
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.text_cache:
			//清除数据库
			DBListTableHandler.getInstance().deleteAllFromListTable();
			ViewUtils.displayMessage(this, "清除成功");
			break;
		case R.id.text_aboutus:
			showAboutUs();
			break;
		case R.id.text_version:
			showVersion();
			break;
			
		default:
			break;
		}
	}
	
	private void showAboutUs() {
		Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_aboutus);
		dialog.show();
	}
	private void showVersion() {
		Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_aboutus);
		PackageInfo info;
		try {
			info = getPackageManager().getPackageInfo(this.getPackageName(),0);
			((TextView)dialog.findViewById(R.id.dialog_content)).setText("心邮：我心邮你\nVersion: " + info.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			ViewUtils.displayMessage(this, "获取版本失败");
		}
		dialog.show();
	}
}

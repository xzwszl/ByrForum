package xzw.szl.byr.utils;


import xzw.szl.byr.R;
import xzw.szl.byr.mananger.PrefernceManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewUtils {
	
	public static boolean isDayTheme;

	
//	public static ProgressBar getProgressBar (Context context) {
//		ProgressBar progressBar = new ProgressBar(context,null,android.R.attr.progressBarStyleLarge);
//	    progressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
//	            LayoutParams.WRAP_CONTENT));
//	    progressBar.setIndeterminate(true);
//	    return progressBar;
////		View v = LayoutInflater.from(context).inflate(R.layout.loading, null);
////		ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
////	    progressBar.setIndeterminate(true);
////	    return progressBar;
//		 
//	}
	
	public static void displayMessage(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	// loading widget
	public static View getLoadingLayout(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.loading, null);
		TextView tv = (TextView) view.findViewById(R.id.text_state);
		tv.setText(R.string.loading_in);
		ImageView iv = (ImageView) view.findViewById(R.id.image_state);
		iv.setImageResource(R.drawable.loading);
		Animation loading = AnimationUtils.loadAnimation(context, R.anim.loading);
		iv.startAnimation(loading);
		return view;
	}
	
	public static View changeToError(View view,Context context) {
		TextView tv = (TextView) ((ViewGroup)view).getChildAt(1);
		ImageView iv = (ImageView) ((ViewGroup)view).getChildAt(0);
		if (tv.getText().equals(context.getString(R.string.loading_in))) {
			iv.clearAnimation();
			iv.setImageResource(R.drawable.error);
			tv.setText(R.string.loading_failed);
		}
		return view;
	}
	
	public static View changeToLoading(View view,Context context) {
		TextView tv = (TextView) ((ViewGroup)view).getChildAt(1);
		ImageView iv = (ImageView) ((ViewGroup)view).getChildAt(0);
		if (tv.getText().equals(context.getString(R.string.loading_failed))) {
			iv.setImageResource(R.drawable.loading);
			Animation loading = AnimationUtils.loadAnimation(context, R.anim.loading);
			iv.startAnimation(loading);
			tv.setText(R.string.loading_in);
		}
		return view;
	}
	
	// 加载失败 widget
	public static TextView getFailedlayout(Context context) {
		TextView progress = new TextView(context);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    params.gravity = Gravity.CENTER;//用这两行代码设置居中
	    progress.setLayoutParams(params);
	    progress.setText("加载中...");
	    return progress;
	}
	
//	public static void updateView(Context context,String cnt,int type,TextView progress) {
//		Drawable drawable = null;
//		if (type == 2) {
//			progress.setCompoundDrawables(null,drawable, null, null);
//			progress.setText(cnt);
//			return;
//		}
//		if (type == 0) {
//			drawable = context.getResources().getDrawable(R.drawable.failed);
//		} else if (type ==1){
//			drawable = context.getResources().getDrawable(R.drawable.error);
//		}
//		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//		progress.setCompoundDrawables(null,drawable, null, null);
//		progress.setText(cnt);
//	}
	
	public static void switchTheme(Context context) {
		int kind = PrefernceManager.getInstance().getCurrentThemeState(context);
		switch (kind) {
		case 0:
			//切换至日间模式
			context.setTheme(R.style.Theme_Day);
			isDayTheme = true;
			break;
		case 1:
			//切换至夜间模式
			context.setTheme(R.style.Theme_Night);
			isDayTheme = false;
			break;
		default:
			break;
		}
		updateThemeState(context, kind);
	}
	
	public static void updateThemeState(Context context, int state) {
		isDayTheme = state == 1 ? false : true;
		PrefernceManager.getInstance().updateCurrenThemeState(context,state);
	}
	

	
	public static boolean isDayTheme(Context context) {
		return isDayTheme;//getCurrentState(context) == 0;
	}
	
	public static boolean isNightTheme(Context context) {
		return !isDayTheme;//getCurrentState(context) == 1;
	}
}

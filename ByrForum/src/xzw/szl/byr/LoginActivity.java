package xzw.szl.byr;

import java.lang.ref.WeakReference;

import xzw.szl.byr.db.DBListTableHandler;
import xzw.szl.byr.dialog.ProgressDialog;
import xzw.szl.byr.mananger.ByrThreadPool;
import xzw.szl.byr.mananger.PrefernceManager;
import xzw.szl.byr.utils.HttpUtils;
import xzw.szl.byr.utils.ViewUtils;
import xzw.szl.byr.view.CircleImageView;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
public class LoginActivity extends Activity implements OnClickListener{
	
	private CircleImageView mImageViewFace;
	private EditText mEditTextAccount;
	private EditText mEditTextPassword;
	private Button mButtonLogin;
	private Button mButtonVisitor;
	
	private Dialog mDialog;
	private Handler handler = new LoginHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		init();
	}
	
	
	private void init() {
		mImageViewFace = (CircleImageView) findViewById(R.id.face);
		mImageViewFace.setOnClickListener(this);
		
		mEditTextAccount = (EditText)findViewById(R.id.account);
		mEditTextPassword = (EditText)findViewById(R.id.password);
		
		mButtonLogin = (Button) findViewById(R.id.login);
		mButtonLogin.setOnClickListener(this);
		
		mButtonVisitor = (Button) findViewById(R.id.visitor);
		mButtonVisitor.setOnClickListener(this);
		
		mDialog = new ProgressDialog(this,"登录中...");
		//initDialog();
	}
	
//	private void initDialog() {
//		mDialog = new Dialog(this,R.style.dialog);
//	//	mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		LinearLayout layout = new LinearLayout(this);
//		layout.setLayoutParams(new LayoutParams(
//				LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
//		layout.setGravity(Gravity.CENTER);
//		layout.setPadding(15, 5, 15, 5);
//		layout.setBackgroundResource(R.drawable.shape_dialog);
//		ProgressBar progressBar = new ProgressBar(this,null,android.R.attr.progressBarStyle);
//		progressBar.setLayoutParams(new LayoutParams(
//				LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
//		//progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progressbar));
//		TextView textView = new TextView(this);
//		textView.setText("登录ing...");
//		textView.setPadding(10,0,5, 0);
//		textView.setLayoutParams(new LayoutParams(
//				LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
//		textView.setTextSize(15.0f);
//		textView.setTextColor(getResources().getColor(android.R.color.white));
//		layout.addView(progressBar);
//		layout.addView(textView);
////		layout.setBackgroundColor();
//		layout.measure(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		
//		mDialog.setContentView(layout);	
//	}


	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.face:
			changeStyle((String)v.getTag());
			break;
		case R.id.login:
			login(true);
			break;
		case R.id.visitor:
			login(false);
			break;
		default:break;
		}
	}
	
	private void changeStyle(String sex) {
		
		if (sex.equals("f")) {
			mImageViewFace.setImageResource(R.drawable.face_default_m);
			mImageViewFace.setTag("m");
		} else {
			mImageViewFace.setImageResource(R.drawable.face_default_f);
			mImageViewFace.setTag("f");
		}
	}
	
	//用户访客都要登陆
	private void login(boolean isUser) {
	
		final String account = isUser ? mEditTextAccount.getText().toString():"guest";
		final String password = isUser ? mEditTextPassword.getText().toString() : null;
		
		if (isUser && (account.equals("") || password.equals(""))) {
			ViewUtils.displayMessage(LoginActivity.this,"账号和密码都不能为空");
			return;
		}
			
		mDialog.show();
			
		HttpUtils.setBasicCredentitalsProvider(account, password);
		
		ByrThreadPool.getTHreadPool().execute(new Runnable() {
			
			@Override
			public void run() {
				

//				PrefernceManager.getInstance().updateCurrentUser(getApplicationContext(), account, password);
				HttpUtils.httpRequest("/user/login.json",new HttpUtils.HttpRequestListener() {
				
					@Override
					public void onSuccess(String content) {

//							User user =(User)JsonUtils.toBean(content, User.class);
						final SharedPreferences prefrence = getSharedPreferences("user", Context.MODE_PRIVATE);
						String username = PrefernceManager.getInstance().getCurrentUserName(getApplicationContext());
						
						if(username == null || !username.equals("account")) {
							DBListTableHandler.getInstance().deleteAllFromListTable();
						}
						
						PrefernceManager.getInstance().updateCurrentUser(getApplicationContext(), account, password);
							
						handler.obtainMessage(0).sendToTarget();
					}
					
					@Override
					public void onFailed(String content) {
						handler.obtainMessage(1,content).sendToTarget();
					}

					@Override
					public void onError(Throwable e) {
						handler.obtainMessage(2).sendToTarget();
						
					}
				});
			}
		});
	}
	
	public static class LoginHandler extends Handler {
		private WeakReference<Activity> activity;

		public LoginHandler(Activity activity) {
			super();
			this.activity = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			
			if (activity == null) return;
			
			LoginActivity a = (LoginActivity)activity.get();
			switch (msg.what) {
			case 1:
				a.mDialog.dismiss();
				ViewUtils.displayMessage(a,(String)msg.obj);
				break;
			case 0:
				PrefernceManager.getInstance().currentUser = PrefernceManager.getInstance().getCurrentUserName(a);
				PrefernceManager.getInstance().can2G3GOnImageload = PrefernceManager.getInstance().getCurrentStateOf2G3GOnImageload(a);
				PrefernceManager.getInstance().canMessageRemind = PrefernceManager.getInstance().getCurrentStateOfMessageRemind(a);
				a.mDialog.dismiss();
				a.finish();
				Intent intent = new Intent(a,HomeActivity.class);
				a.startActivity(intent);
				break;
			case 2:
				a.mDialog.dismiss();
				ViewUtils.displayMessage(a, a.getResources().getString(R.string.network_error));
				break;

			default:
				
				break;
			}
		}
	}
	
	public void goToHome() {
		saveSharedPrefrence("guest",null);
		HttpUtils.setBasicCredentitalsProvider("guest", null);
		Intent intent = new Intent(this,HomeActivity.class);
		this.finish();
		startActivity(intent);
	}
	
	private void saveSharedPrefrence(String account, String password) {
		SharedPreferences prefrence = getSharedPreferences("user", Context.MODE_PRIVATE);
		
		Editor editor = prefrence.edit();	
		editor.putString("account",account);
		editor.putString("password",password);
		editor.commit();
	}
 }

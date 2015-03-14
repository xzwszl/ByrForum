package xzw.szl.byr.assist;

import java.lang.ref.SoftReference;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import xzw.szl.byr.BaseActivity;
import xzw.szl.byr.R;
import xzw.szl.byr.mananger.ByrThreadPool;
import xzw.szl.byr.utils.DataUtils;
import xzw.szl.byr.utils.HttpUtils;
import xzw.szl.byr.utils.ViewUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class PostMailActivity extends BaseActivity{

	private EditText mTitle;
	private EditText mReciver;
	private EditText mContent;
	private TextView mSubmit;
	
	private String reciver;
	private String title;
	private int index;
	private String box;
	private String content;
	private Handler handler;
	private boolean isReplySuccessed = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		ViewUtils.switchTheme(this);
		setContentView(R.layout.activity_postmail);
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
//			Intent upIntent = NavUtils.getParentActivityIntent(this);
//			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
//				TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
//			} else {
//				upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				NavUtils.navigateUpTo(this, upIntent);
//			}
			
			Intent intent = new Intent();
			intent.putExtra("isReplySuccessed", isReplySuccessed);
			setResult(Activity.RESULT_OK,intent);
			
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra("isReplySuccessed", isReplySuccessed);
		setResult(Activity.RESULT_OK,intent);
		super.onBackPressed();
	}
	private void init() {
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		handler = new PostMailHandler(this);
		mTitle = (EditText) findViewById(R.id.title);
		mReciver = (EditText) findViewById(R.id.reciver);
		mContent = (EditText) findViewById(R.id.content);
		mSubmit = (TextView) findViewById(R.id.submit);
		
		reciver = getIntent().getStringExtra("reciver");
		title = getIntent().getStringExtra("title");
		index = getIntent().getIntExtra("index",-1);
		box = getIntent().getStringExtra("box");
		content = getIntent().getStringExtra("content");
		
		if (reciver != null) {
			mReciver.setFocusable(false);
			mReciver.setText(reciver);
			if (title != null) {
				mTitle.setFocusable(false);
				mTitle.setText("Re: " + title);
			}
			if (content != null) {
				mContent.setText(DataUtils.getReplyContent(content, reciver));
			}
			setTitle("回复邮件");
		}
		
		mSubmit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String text = ((TextView)v).getText().toString();
				if (text.equals("提交成功") || text.equals("回复成功")) {
					ViewUtils.displayMessage(PostMailActivity.this,"已提交成功");
					return;
				} else if (text.equals("提交中...")){
					ViewUtils.displayMessage(PostMailActivity.this,"请勿重复提交");
					return;
				} else {
					if (mReciver.getText().toString().equals("")) {
						ViewUtils.displayMessage(PostMailActivity.this, "收件人不能为空");
					}
					if (reciver != null)
						ByrThreadPool.getTHreadPool().execute(replyRunnable);
					else
						ByrThreadPool.getTHreadPool().execute(sendRunnable);
					mSubmit.setBackgroundColor(getResources().getColor(R.color.yellow));
					mSubmit.setText("提交中...");
				}
			}
		});
	}
	
	private Runnable replyRunnable = new Runnable() {
		
		@Override
		public void run() {
			replyMail();
		}
	};
	
	private Runnable sendRunnable = new Runnable() {

		@Override
		public void run() {
			sendMail();
		}
	};
	
	private void replyMail() {
		HttpUtils.replyMail(box, index, title, mContent.getText().toString(), new HttpUtils.HttpRequestListener() {
			
			@Override
			public void onSuccess(String content) {
				handler.obtainMessage(3).sendToTarget();
			}
			
			@Override
			public void onFailed(String reason) {
				handler.obtainMessage(1,reason).sendToTarget();
			}
			
			@Override
			public void onError(Throwable e) {
				handler.obtainMessage(2).sendToTarget();
			}
		});
	}
	
	private void sendMail() {
		HttpUtils.postMail(mReciver.getText().toString(),mTitle.getText().toString(),mContent.getText().toString(),
				new HttpUtils.HttpRequestListener() {
			
			@Override
			public void onSuccess(String content) {
				handler.obtainMessage(0).sendToTarget();
			}
			
			@Override
			public void onFailed(String reason) {
				handler.obtainMessage(1,reason).sendToTarget();
			}
			
			@Override
			public void onError(Throwable e) {
				handler.obtainMessage(2).sendToTarget();
			}
		});
	}

	static class PostMailHandler extends Handler {
		
		private SoftReference<Activity> reference;
		
		public PostMailHandler(Activity activity) {
			reference = new SoftReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
		
			PostMailActivity acitivty = (PostMailActivity) reference.get();
			if (acitivty != null) {
				switch (msg.what) {
				case 0:
					acitivty.mSubmit.setText("提交成功");
					acitivty.mSubmit.setBackgroundColor(acitivty.getResources().getColor(R.color.grey));
					break;
				case 1:
					acitivty.mSubmit.setText((String)msg.obj);
					acitivty.mSubmit.setBackgroundColor(acitivty.getResources().getColor(R.color.red));
					break;
				default:
				case 2:
					ViewUtils.displayMessage(acitivty,acitivty.getResources().getString(R.string.network_error));
					break;
				case 3:
					acitivty.mSubmit.setText("回复成功");
					acitivty.isReplySuccessed = true;
					acitivty.mSubmit.setBackgroundColor(acitivty.getResources().getColor(R.color.grey));
					break;
				}
			}
		} 
	}
}

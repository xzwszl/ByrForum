package xzw.szl.byr.assist;

import java.lang.ref.SoftReference;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import pl.droidsonroids.gif.GifTextView;
import xzw.szl.byr.R;
import xzw.szl.byr.dialog.ForwardMailDialog;
import xzw.szl.byr.dialog.UserInfoDialog;
import xzw.szl.byr.info.Mail;
import xzw.szl.byr.mananger.ByrThreadPool;
import xzw.szl.byr.mananger.ImageCacheManager;
import xzw.szl.byr.utils.DataUtils;
import xzw.szl.byr.utils.HttpUtils;
import xzw.szl.byr.utils.JsonUtils;
import xzw.szl.byr.utils.MyTagHandler;
import xzw.szl.byr.utils.URLImageGetter;
import xzw.szl.byr.utils.ViewUtils;
import xzw.szl.byr.view.CircleImageView;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MailDetailActivity extends BaseDetailActivity implements OnClickListener{
	
	private int mIndex;
	private String mBox;
	
	private TextView mTitle;
	private TextView mName;
	private TextView mTime;
	private TextView mGif;
	private ImageView mReply;
	private ImageView mForward;
	private ImageView mEdit;
	private ImageView mDelete;
	private ImageView mFace;
	private static Handler handler;
	private Dialog mForwadMailDialog;
	
	private Mail mMail;
	private boolean isReplySuccessed = false;
	private boolean isDeleteSuccessed = false;
	private boolean isReadSuccessed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.switchTheme(this);
		setContentView(R.layout.activity_maildetail);
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case android.R.id.home:
//			Intent upIntent = NavUtils.getParentActivityIntent(this);
//			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
//				TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
//			} else {
//				upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				NavUtils.navigateUpTo(this, upIntent);
//			}
			Intent intent = new Intent();
			intent.putExtra("index", mIndex);
			intent.putExtra("isReplySuccessed", isReplySuccessed);
			intent.putExtra("isReadSuccessed", isReadSuccessed);
			setResult(Activity.RESULT_OK,intent);
			finish();
			return true;
		case R.id.board_refresh:
			showRefreshAnimation();
			getMailDetail();
			break;
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.putExtra("index", mIndex);
		intent.putExtra("isReplySuccessed", isReplySuccessed);
		intent.putExtra("isReadSuccessed", isReadSuccessed);
		setResult(Activity.RESULT_OK,intent);
		finish();
		super.onBackPressed();
	}
	
	private void init() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		mIndex = getIntent().getIntExtra("index", -1);
	    mBox = getIntent().getStringExtra("box");
	    
	    mFace = (CircleImageView) findViewById(R.id.face);
	    mTitle = (TextView) findViewById(R.id.title);
	    mName = (TextView) findViewById(R.id.name);
	    mTime = (TextView) findViewById(R.id.time);
	    mGif = (GifTextView) findViewById(R.id.content);
	    mReply = (ImageView) findViewById(R.id.reply);
	    mForward = (ImageView) findViewById(R.id.forward);
	    mEdit = (ImageView) findViewById(R.id.edit);
	    mDelete = (ImageView) findViewById(R.id.delete);
	    
	    handler = new MailHandler(this);
	    mForwadMailDialog = new ForwardMailDialog(this,mBox,mIndex);
	    mReply.setOnClickListener(this);
	    mForward.setOnClickListener(this);
	    mEdit.setOnClickListener(this);
	    mDelete.setOnClickListener(this);
	    mFace.setOnClickListener(this);
	    getMailDetail();
	}
	
	private void getMailDetail() {
//		showRefreshAnimation();
		ByrThreadPool.getTHreadPool().execute(r);
	}
	
	private Runnable r = new Runnable() {
		
		@Override
		public void run() {
			readMailDetail();
		}
	};
	
	private void readMailDetail() {
		
		HttpUtils.httpRequest("/mail/" + mBox + "/" + mIndex +  ".json", new HttpUtils.HttpRequestListener() {
			
			@Override
			public void onSuccess(String content) {
				
				Mail mail = (Mail) JsonUtils.toBean(content,Mail.class);
				
				if (mail != null)
					handler.obtainMessage(0, mail).sendToTarget();
			}
			
			@Override
			public void onFailed(final String reason) {
				handler.obtainMessage(1,reason).sendToTarget();
			}
			
			@Override
			public void onError(Throwable e) {
				handler.obtainMessage(1,getString(R.string.network_error)).sendToTarget();
			}
		});
	}
	
	private void deleteMail() {
		HttpUtils.deleteMail(mBox, mIndex, new HttpUtils.HttpRequestListener() {
			
			@Override
			public void onSuccess(String content) {
				handler.obtainMessage(2).sendToTarget();
			}
			
			@Override
			public void onFailed(String reason) {
				handler.obtainMessage(1,reason).sendToTarget();
			}
			
			@Override
			public void onError(Throwable e) {
				
			}
		});
	}
	
	private Runnable deleteRunnable =new Runnable() {
		
		@Override
		public void run() {
			deleteMail();
		}
	};

	private void updateViews() {
		
		if (mMail == null) return;
		mTitle.setText(mMail.getTitle());
		
		mTime.setText(DataUtils.getDateString(mMail.getPost_time()));
		if (mMail.getUser() != null) {
			
			if (mMail.getUser().getGender() != null && mMail.getUser().getGender().equals("f")) {
				mName.setTextColor(getResources().getColor(R.color.pink));
			} else {
				mName.setTextColor(getResources().getColor(R.color.blue));
			}
			mName.setText(mMail.getUser().getId());
			
			ImageCacheManager.INSTANCE.startAcquireImage(mMail.getUser().getFace_url(),
					ImageCacheManager.getFaceImageAcquireListener(mFace, handler));
		}
		
		CharSequence sequence = Html.fromHtml(DataUtils.getHtmlFromString(mMail.getContent(),mMail.getAttachment()), 
				new URLImageGetter((GifTextView)mGif, this, mMail.getAttachment(), handler,DataUtils.getScreenWidth(),DataUtils.getScreenHeight()), 
				new MyTagHandler(this, mMail.getAttachment()));
		if (mMail.getContent().contains("[em")) {
			mGif.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.bbb, R.drawable.bbb, R.drawable.bbb, R.drawable.bbb);
		}
		
		mGif.setMovementMethod(LinkMovementMethod.getInstance());
		mGif.setText(sequence);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.reply:
			Intent ir = new Intent(this,PostMailActivity.class);
			ir.putExtra("reciver", mMail.getUser().getId());
			ir.putExtra("box",mBox);
			ir.putExtra("content", mMail.getContent());
			ir.putExtra("index", mMail.getIndex());
			ir.putExtra("title", mMail.getTitle());
			startActivityForResult(ir,1);
			break;
		case R.id.forward:
			((ForwardMailDialog) mForwadMailDialog).reset();
			mForwadMailDialog.show();
			break;
		case R.id.edit:
			Intent intent = new Intent(this,PostMailActivity.class);
			startActivity(intent);
			break;
		case R.id.delete:
			ByrThreadPool.getTHreadPool().execute(deleteRunnable);
			break;
		case R.id.face:
			Dialog dialog = new UserInfoDialog(this, mMail.getUser(), handler);
			dialog.show();
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 1) {
				
				isReplySuccessed = data.getBooleanExtra("isReplySuccessed", false);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	static class MailHandler extends Handler {
		private SoftReference<MailDetailActivity> reference;
		
		public MailHandler(MailDetailActivity activity) {
			reference = new SoftReference<MailDetailActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			
			MailDetailActivity activity = reference.get();
			if (activity != null)
			switch (msg.what) {
			case 0:
				activity.mMail = (Mail) msg.obj;
				activity.updateViews();
				activity.clearRefreshAnimation();
				activity.isReadSuccessed = true;
				break;
			case 1:
				ViewUtils.displayMessage(activity,(String) msg.obj);
				activity.clearRefreshAnimation();
			case 2:
				ViewUtils.displayMessage(activity, "删除成功");
				activity.isDeleteSuccessed = true;
				Intent intent = new Intent();
				intent.putExtra("isDeleteSuccessed", activity.mIndex);
				activity.setResult(Activity.RESULT_OK,intent);
				activity.finish();
				break;

			default:
				break;
			}
		}
	} 
}

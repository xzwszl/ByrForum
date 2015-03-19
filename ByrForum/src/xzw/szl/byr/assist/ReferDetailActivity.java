package xzw.szl.byr.assist;

import java.lang.ref.SoftReference;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import pl.droidsonroids.gif.GifTextView;
import xzw.szl.byr.R;
import xzw.szl.byr.dialog.ForwardDialog;
import xzw.szl.byr.dialog.UserInfoDialog;
import xzw.szl.byr.info.Article;
import xzw.szl.byr.mananger.ByrThreadPool;
import xzw.szl.byr.mananger.ImageCacheManager2;
import xzw.szl.byr.utils.ByrBase;
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

public class ReferDetailActivity extends BaseDetailActivity implements OnClickListener{
	
	private int mId;
	private String mBoard;
	private int mIndex;
	private String mType;
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
	private Dialog mForwadDialog;
	
	private Article mArticle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
//		case android.R.id.home:
//			Intent upIntent = NavUtils.getParentActivityIntent(this);
//			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
//				TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
//			} else {
//				upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				NavUtils.navigateUpTo(this, upIntent);
//			}
//			finish();
//			return true;
		case R.id.board_refresh:
			showRefreshAnimation();
			getArticleDetail();
			break;
//			
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void init() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
	    mFace = (CircleImageView) findViewById(R.id.face);
	    mTitle = (TextView) findViewById(R.id.title);
	    mName = (TextView) findViewById(R.id.name);
	    mTime = (TextView) findViewById(R.id.time);
	    mGif = (GifTextView) findViewById(R.id.content);
	    mReply = (ImageView) findViewById(R.id.reply);
	    mForward = (ImageView) findViewById(R.id.forward);
	    mEdit = (ImageView) findViewById(R.id.edit);
	    mDelete = (ImageView) findViewById(R.id.delete);
	    
	    handler = new ArticleHandler(this);
	    mForwadDialog = new ForwardDialog(this,mBoard,mId);
	    mReply.setOnClickListener(this);
	    mForward.setOnClickListener(this);
	    mEdit.setOnClickListener(this);
	    mDelete.setOnClickListener(this);
	    mFace.setOnClickListener(this);
	    
	    
	    String content = getIntent().getStringExtra("content");
		if (content == null) {
			mId = getIntent().getIntExtra("id", -1);
		    mBoard = getIntent().getStringExtra("board");
		    mIndex = getIntent().getIntExtra("index", -1);
		    mType = getIntent().getStringExtra("type");
		    getArticleDetail();
		} else {
			Article article = (Article) JsonUtils.toBean(content,Article.class);
			
			if (article != null)
				handler.obtainMessage(0, article).sendToTarget();
		}
	}
	
	private void getArticleDetail() {
	
		ByrThreadPool.getTHreadPool().execute(r);
	}
	
	private Runnable r = new Runnable() {
		
		@Override
		public void run() {
			readArticleDetail();
		}
	};
	
	private void readArticleDetail() {
		
		HttpUtils.httpRequest("/article/" + mBoard + "/" + mId +  ".json", new HttpUtils.HttpRequestListener() {
			
			@Override
			public void onSuccess(String content) {
				
				Article article = (Article) JsonUtils.toBean(content,Article.class);
				
				if (article != null)
					handler.obtainMessage(0, article).sendToTarget();
			}
			
			@Override
			public void onFailed(final String reason) {
				handler.obtainMessage(1,reason).sendToTarget();
			}
			
			@Override
			public void onError(Throwable e) {
				handler.obtainMessage(2).sendToTarget();
			}
		});
	}
	
	private void deleteArticle() {
		HttpUtils.deleteRefer(mType, mIndex, new HttpUtils.HttpRequestListener() {
			
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
				handler.obtainMessage(4).sendToTarget();
			}
		});
	}
	
	private Runnable deleteRunnable =new Runnable() {
		
		@Override
		public void run() {
			deleteArticle();
		}
	};

	private void updateViews() {
		
		if (mArticle == null) return;
		mTitle.setText(mArticle.getTitle());
		
		mTime.setText(DataUtils.getDateString(mArticle.getPost_time()));
		if (mArticle.getUser() != null) {
			
			if (mArticle.getUser().getGender() != null && mArticle.getUser().getGender().equals("f")) {
				mName.setTextColor(getResources().getColor(R.color.pink));
			} else {
				mName.setTextColor(getResources().getColor(R.color.blue));
			}
			mName.setText(mArticle.getUser().getId());
			
			ImageCacheManager2.INSTANCE.startAcquireImage2(mArticle.getUser().getFace_url(),
					ImageCacheManager2.getFaceImageAcquireListener(mFace, handler),
					DataUtils.getDisplayValue(ByrBase.FACE_WIDTH),
					DataUtils.getDisplayValue(ByrBase.FACE_WIDTH),
					false);
		}
		
		CharSequence sequence = Html.fromHtml(DataUtils.getHtmlFromString(mArticle.getContent(),mArticle.getAttachment()), 
				new URLImageGetter((GifTextView)mGif, this, mArticle.getAttachment(), handler,DataUtils.getScreenWidth(),DataUtils.getScreenHeight()), 
				new MyTagHandler(this, mArticle.getAttachment()));
		if (mArticle.getContent().contains("[em")) {
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
			if (mArticle != null) {
				Intent ir = new Intent(this,PostArticleActivity.class);
				ir.putExtra("reid", mArticle.getId());
				ir.putExtra("board",mBoard);
				ir.putExtra("content", DataUtils.getReplyContent(mArticle.getContent(),mArticle.getUser().getId()));
				ir.putExtra("title", "Re:" + mArticle.getTitle());
				startActivity(ir);
			}
			break;
		case R.id.forward:
			((ForwardDialog) mForwadDialog).reset();
			mForwadDialog.show();
			break;
		case R.id.edit:
			Intent intent = new Intent(this,PostArticleActivity.class);
			startActivity(intent);
			break;
		case R.id.delete:
			ByrThreadPool.getTHreadPool().execute(deleteRunnable);
			break;
		case R.id.face:
			Dialog dialog = new UserInfoDialog(this, mArticle.getUser(), handler);
			dialog.show();
			break;
		}
	}
	
	static class ArticleHandler extends Handler {
		private SoftReference<ReferDetailActivity> reference;
		
		public ArticleHandler(ReferDetailActivity activity) {
			reference = new SoftReference<ReferDetailActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			
			ReferDetailActivity activity = reference.get();
			if (activity != null)
			switch (msg.what) {
			case 0:
				activity.mArticle = (Article) msg.obj;
				activity.updateViews();
				activity.clearRefreshAnimation();
				break;
			case 1:
				ViewUtils.displayMessage(activity,(String) msg.obj);
				activity.clearRefreshAnimation();
				break;
			case 2:
				ViewUtils.displayMessage(activity,activity.getResources().getString(R.string.network_error));
				activity.clearRefreshAnimation();
				break;
			case 3:
				ViewUtils.displayMessage(activity, "删除成功");
				
				Intent intent =  new Intent();
				intent.putExtra("isDeleteSuccessed", true);
				activity.setResult(Activity.RESULT_OK, intent);
				activity.finish();
				
				break;
			case 4:
				ViewUtils.displayMessage(activity,activity.getResources().getString(R.string.network_error));
				break;
			default:
				break;
			}
		}
	} 
}

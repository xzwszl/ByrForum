package xzw.szl.byr.assist;
import java.util.Locale;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import xzw.szl.byr.BaseActivity;
import xzw.szl.byr.R;
import xzw.szl.byr.mananger.ByrThreadPool;
import xzw.szl.byr.utils.HttpUtils;
import xzw.szl.byr.utils.ImageUtils;
import xzw.szl.byr.utils.ViewUtils;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PictureActivity extends BaseActivity {
	
	
	private String url;	//传进来的url
	private String type;
	private ProgressBar mProgressBar;
	private TextView mTv;
	private ImageView imageView;
	private MyHandler handler = new MyHandler();
	private PhotoViewAttacher mAttacher;
	
	private int mDownByteCount;
	private int mTotalByteCount;
	private int mDownloadState;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
//		ViewUtils.switchTheme(this);
		setContentView(R.layout.activity_picture);
		
		init();
		
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.picture, menu);
		return true;
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
			
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	void init() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mProgressBar = (ProgressBar) findViewById(R.id.picture_progress);
		mTv = (TextView) findViewById(R.id.progress_tv);
		mProgressBar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
					switch (mDownloadState) {
					case OnPicitureDownloadListener.DOWNLOAD_IDLE:
						download();
						break;
					case OnPicitureDownloadListener.DOWNLOADING:
						ViewUtils.displayMessage(PictureActivity.this, "Downloading....");
						break;
					case OnPicitureDownloadListener.DOWNLOADED:
						
						break;
					case OnPicitureDownloadListener.DOWNLOAD_ERROR:
						ViewUtils.displayMessage(PictureActivity.this, getResources().getString(R.string.network_error));
						break;
					}
				
				//此状态下可以下载	
			}
		});
		
		//imageView = (GifImageView) findViewById(R.id.picture);
		Intent intent = getIntent();
		url = intent.getStringExtra("url");	
		type = intent.getStringExtra("type");
		if (!type.toLowerCase(Locale.US).equals("gif")) {
			imageView = (PhotoView) findViewById(R.id.iv_photo);
			if(VERSION.SDK_INT >= 11){ //android3.0以上禁止硬件加速，否则大图显示不了 
				imageView.setLayerType(View.LAYER_TYPE_SOFTWARE,null); 
			}
		} else {
			imageView = (GifImageView) findViewById(R.id.gifpicture);
		}
//		Drawable bitmap = getResources().getDrawable(R.drawable.test);
//	    imageView.setImageDrawable(bitmap);
		mAttacher = new PhotoViewAttacher(imageView);
		mAttacher.setMaximumScale(30.0f);
//		mAttacher.setScaleType(ScaleType.CENTER_CROP);
		
		
	//	mAttacher.setScaleType(ScaleType.CENTER_CROP);

		//判断文件中是否有该图片，如果有，则下载完成
		if (ImageUtils.hasImage(url + type)) {
			mDownloadState = OnPicitureDownloadListener.DOWNLOADED;
			showImage(url+type);
		} else {
			mProgressBar.setVisibility(View.VISIBLE);
			mTv.setVisibility(View.VISIBLE);
			imageView.setVisibility(View.GONE);
			mDownByteCount = 0;
			mDownloadState = OnPicitureDownloadListener.DOWNLOAD_IDLE;
			download();
		}
	}
	
	
	//jiant
	public interface OnPicitureDownloadListener {
		
		int DOWNLOAD_IDLE = 0;	//空闲状态，由于网络问题，下载中断也是此状态
		int DOWNLOADING = 1;    
		int DOWNLOADED = 2;
		int DOWNLOAD_ERROR = 3;	//没有200Ok的情况
		
		//更新下载的状态，如果下载进度，更新进度
		void onRefreshDownloadState(int state);
		
		void onUpdateDownloadProgress(int currentByteCount,int totalByteCount);
	}
	
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			
			switch (mDownloadState) {
			case OnPicitureDownloadListener.DOWNLOAD_IDLE:
				mProgressBar.setVisibility(View.VISIBLE);
				mTv.setVisibility(View.VISIBLE);
				imageView.setVisibility(View.GONE);
				break;
			case OnPicitureDownloadListener.DOWNLOADING:
				mProgressBar.setProgress(100 * mDownByteCount / mTotalByteCount);
				mTv.setText(100 * mDownByteCount / mTotalByteCount + "%");
				break;
			case OnPicitureDownloadListener.DOWNLOADED:
				showImage(url + type);
				break;
			case OnPicitureDownloadListener.DOWNLOAD_ERROR:
				break;
				
			}
		}
	};
	
	private void download() {
		ByrThreadPool.getTHreadPool().execute(r);
	}
	
	
	private static class MyHandler extends Handler {
		
	}
	
	private void showImage(String urlpath) {
		mProgressBar.setVisibility(View.GONE);
		mTv.setVisibility(View.GONE);
		imageView.setVisibility(View.VISIBLE);
		imageView.setImageDrawable(ImageUtils.getDrawableFromSD(urlpath));
	}
	
	private Runnable r = new Runnable() {
		
		@Override
		public void run() {

			HttpUtils.getPicture(url,type,new OnPicitureDownloadListener() {
				
				@Override
				public void onUpdateDownloadProgress(final int currentByteCount,
						final int totalByteCount) {
					
					
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							mDownByteCount = currentByteCount;
							mTotalByteCount = totalByteCount;
							mProgressBar.setProgress(100 * mDownByteCount / mTotalByteCount);
							mTv.setText(100 * mDownByteCount / mTotalByteCount + "%");
							
							
						}
					});
				}
				
				@Override
				public void onRefreshDownloadState(final int state) {
					handler.post(new Runnable() {

						@Override
						public void run() {
							mDownloadState = state;
							switch (mDownloadState) {
								case OnPicitureDownloadListener.DOWNLOAD_IDLE:
									mProgressBar.setVisibility(View.VISIBLE);
									mTv.setVisibility(View.VISIBLE);
									imageView.setVisibility(View.GONE);
									break;
								case OnPicitureDownloadListener.DOWNLOADING:
									if (mTotalByteCount > 0) {
										mProgressBar.setProgress(100 * mDownByteCount / mTotalByteCount);
										mTv.setText(100 * mDownByteCount / mTotalByteCount + "%");
									}
									break;
								case OnPicitureDownloadListener.DOWNLOADED:
									showImage(url + type);
									break;
								case OnPicitureDownloadListener.DOWNLOAD_ERROR:
									ViewUtils.displayMessage(PictureActivity.this, getResources().getString(R.string.network_error));
									mDownloadState = OnPicitureDownloadListener.DOWNLOAD_IDLE;
									mProgressBar.setVisibility(View.VISIBLE);
									mTv.setVisibility(View.VISIBLE);
									imageView.setVisibility(View.GONE);
									break;
								}
							}
							
						
					});
											
				}
			},mDownByteCount);	
		}
	};
	
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Need to call clean-up
        mAttacher.cleanup();
    }
}

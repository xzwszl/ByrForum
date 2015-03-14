package xzw.szl.byr;

import java.lang.ref.SoftReference;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import pl.droidsonroids.gif.GifTextView;

import xzw.szl.byr.assist.PostArticleActivity;
import xzw.szl.byr.dialog.ForwardDialog;
import xzw.szl.byr.dialog.UserInfoDialog;
import xzw.szl.byr.info.Article;
import xzw.szl.byr.info.Pagination;
import xzw.szl.byr.info.Threads;
import xzw.szl.byr.info.User;
import xzw.szl.byr.mananger.ByrThreadPool;
import xzw.szl.byr.mananger.ImageCacheManager;
import xzw.szl.byr.utils.DataUtils;
import xzw.szl.byr.utils.HttpUtils;
import xzw.szl.byr.utils.JsonUtils;
import xzw.szl.byr.utils.MyTagHandler;
import xzw.szl.byr.utils.URLImageGetter;
import xzw.szl.byr.utils.ViewUtils;
import xzw.szl.byr.view.CircleImageView;
import xzw.szl.byr.view.FaceSelectView;
import xzw.szl.byr.view.FaceSelectView.OnAttachMentSelectListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ArticleActivity extends BaseActivity {
	
//	private LinearLayout layout;
//	private PopupWindow window;
//	private TextView reply;
//	private TextView update;
//	private TextView delete;
//	private TextView more;
//	private TextView progress;
	
	private ImageView mFace;
	private EditText mEditText;
	private ImageView mReply;
	private FaceSelectView mFaceSelectView;
	
	private Threads threads;
	//private Map<String,SoftReference<Bitmap>> cache;  //图片map
	//private Map<String,SoftReference<Drawable>> gifCache;
	private ArticleHandler handler;
	private PullToRefreshListView mPullToRefreshListView;
	
	private ForwardDialog mFDialog;
	private String board;
	private int aid;
	private int mCurretnPage;
	private boolean isRefreshable;
	private int lastPage;
	private String account;
	private final String TAG = "ArticleActivity";
	private Article mFArticle;
//	private boolean isBusy = false;
//	private int firstVisiblePosition= 0;
//	private int lastVisiblePosition =0;
//	private int totalCount = 0;
//	private SparseArray<Article> mDelay;
	private ILoadingLayout startLabels;
	private ILoadingLayout endLabels;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		ViewUtils.switchTheme(this);
		setContentView(R.layout.activity_article);
		
		init();
		
	}
		
	private void init() {
		
		
		
//		initPopupWindow();
//		mDelay = new SparseArray<Article>();
		account = getUserAccount();
		mCurretnPage = 1;
		isRefreshable = false;
		lastPage = 1;
		//HttpUtils.context = this;
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		Intent intent = getIntent();
		board = intent.getStringExtra("board");
		aid = intent.getIntExtra("aid",0);
		String title=intent.getStringExtra("atitle");
		setTitle(title);
		
		mFDialog = new ForwardDialog(this,board,0);
		handler = new ArticleHandler(this);
//		progress = ViewUtils.getFailedlayout(this);
//        progress.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				String value = ((TextView)v).getText().toString();
//				if (value.equals("加载中...")) {
//					
//				} else {
//					getArticles(board, aid, mCurretnPage);
//				}
//			}
//		});
        
		mPullToRefreshListView = (PullToRefreshListView)findViewById(R.id.pullToRefreshListView_article);
		mPullToRefreshListView.setMode(Mode.BOTH);
		startLabels = mPullToRefreshListView.getLoadingLayoutProxy(true,false);
		
		startLabels.setPullLabel("当前第一页");
//		startLabels.setRefreshingLabel("");
		startLabels.setReleaseLabel("当前第一页");
		
		endLabels = mPullToRefreshListView.getLoadingLayoutProxy(false,true);
		
		endLabels.setPullLabel("当前第一页");
//		endLabels.setRefreshingLabel("");
		endLabels.setReleaseLabel("当前第一页");
		
//		mPullToRefreshListView.setScrollingWhileRefreshingEnabled(false);
		//
//		mPullToRefreshListView.getRefreshableView().setScrollingCacheEnabled(false);
		mPullToRefreshListView.setAdapter(mAdapter);
//		((ViewGroup)mPullToRefreshListView.getParent()).addView(progress, 0);
		
		//mPullToRefreshListView.setEmptyView(progress);
//		mPullToRefreshListView.setHeader("当前第一页", "刷新中...", "当前第一页");
//		mPullToRefreshListView.setFooter("上拉加载第2页", "刷新中...", "放开以加载第2页");
//		mPullToRefreshListView.onReset();
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				
				if (isRefreshable) {
					
					if (mCurretnPage > 1) {
						isRefreshable = false;
						getArticles(board, aid, mCurretnPage - 1);
						return;
					} 
//					else if (progress.getVisibility() == View.VISIBLE){
//						isRefreshable = false;
//						getArticles(board, aid, mCurretnPage);
//						return;
//					} 
				}
				refreshView.onRefreshComplete();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (isRefreshable) {
					
					if (mCurretnPage < lastPage) {
						isRefreshable = false;
						getArticles(board, aid, mCurretnPage + 1);
						return;
					}
				} 
				refreshView.onRefreshComplete();
			}
			
		});
		
		mPullToRefreshListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				switch (scrollState) {
//				case OnScrollListener.SCROLL_STATE_IDLE:
//					isBusy = false;
//					
//					int i= firstVisiblePosition == 0?1:firstVisiblePosition;
//					int last = lastVisiblePosition >= totalCount -1 ? totalCount-1 : lastVisiblePosition+1;
//					for (int j=0;i<last-i;j++) {
//						
//						Article article = mDelay.get(i+j-1);
//						if (article != null) {
//							if(view.getChildAt(j) != null) {
//								ViewHolder holder = (ViewHolder) view.getChildAt(j).getTag();
//								if (holder == null) continue;
//								
//								if (article.getUser() != null && article.getUser().getFace_url() != null)
//									ImageCacheManager.INSTANCE.startAcquireImage(article.getUser().getFace_url(),ImageCacheManager.getFaceImageAcquireListener(holder.face,handler));
//								CharSequence sequence = Html.fromHtml(DataUtils.getHtmlFromString(
//										article.getContent(),article.getAttachment()),
//										new URLImageGetter(holder.content,ArticleActivity.this, article.getAttachment(), handler,view.getWidth(),view.getHeight()), new MyTagHandler(ArticleActivity.this,article.getAttachment()));
//								holder.content.setText(sequence);
//							}
//						} 
//					}
//					break;
//				case OnScrollListener.SCROLL_STATE_FLING:
//					isBusy = true;
//				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
//					isBusy = true;
//				default:
//					break;
//				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
//				
//				firstVisiblePosition = firstVisibleItem;
//				lastVisiblePosition = firstVisibleItem + visibleItemCount;
//				totalCount = totalItemCount;
			}
		});
		mFace = (ImageView) findViewById(R.id.article_face);
		mEditText = (EditText) findViewById(R.id.article_content);
		mReply = (ImageView) findViewById(R.id.article_send);
		mFaceSelectView = (FaceSelectView) findViewById(R.id.article_face_layout);
		
		mFace.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (mFaceSelectView.getVisibility() == View.GONE) {
					mFaceSelectView.setVisibility(View.VISIBLE);
					mFaceSelectView.setOnAttachMentListener(new OnAttachMentSelectListener() {
						
						@Override
						public void selected(String faceTag) {
							int pos = mEditText.getSelectionStart();
							Editable edit = mEditText.getText();
							edit.insert(pos, faceTag);
						}
					});
				} else {
					mFaceSelectView.setVisibility(View.GONE);
				}
				
			}
		});
		
		mReply.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//发送回复
				
				if (mEditText.getText().toString().equals("")) {
					ViewUtils.displayMessage(ArticleActivity.this,"您还没写回复内容哦~");
					return;
				}
				
				if (mFArticle == null) {
					ViewUtils.displayMessage(ArticleActivity.this,"您还没选择文章哦~");
					return;
				}
				ByrThreadPool.getTHreadPool().execute(new Runnable() {
					
					@Override
					public void run() {
						String content = mEditText.getText().toString() + "\n" + 
								DataUtils.getReplyContent(mFArticle.getContent(), mFArticle.getUser().getId());
						
						HttpUtils.postArticle(board, mFArticle.getTitle(), content,
								mFArticle.getId(), null, -1 ,null,new HttpUtils.HttpRequestListener() {
									
									@Override
									public void onSuccess(String content) {
										handler.obtainMessage(4).sendToTarget();
									}
									
									@Override
									public void onFailed(String reason) {
										handler.obtainMessage(5).sendToTarget();
										
									}
									
									@Override
									public void onError(Throwable e) {
										handler.obtainMessage(6).sendToTarget();
									}
								});
					}
				});
			}
		});
		
		getArticles(board, aid,1);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.article, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

//		int id = item.getItemId();
//
//		switch (id) {
//		case android.R.id.home:
//			Intent upIntent = NavUtils.getParentActivityIntent(this);
//			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
//				TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
//			} else {
//				upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				NavUtils.navigateUpTo(this, upIntent);
//			}
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}
	/**
	 * 获取Article的数据，每次访问9条数据
	 */
//	public void getArticles(final String board,final int id) {
//		
//		threads.getArticle().clear();
//		new Thread(){
//
//			@Override
//			public void run() {
//				
//				Article article = null;
//				int count = 10;
//				do {
//					
//					int tid = 0;
//					if (article == null) {
//						tid = id;
//					} else {
//						threads.getArticle().add(article);
//						tid = article.getThreads_next_id();
//					}
//					if (tid == 0 || count == 0) break;
//					article = (Article) JsonUtils.toBean(HttpUtils.getJsonData("/article/" + board + "/" + tid + ".json"), Article.class);
//					count--;
//				} while(article != null);
//				
//				handler.obtainMessage(0).sendToTarget();
//			}
//			
//		}.start();
//	}
	
	public void getArticles(String board,int id,int page) {
		
//		ViewUtils.updateView(this, "加载中...", 2, progress);
//		progress.setVisibility(View.VISIBLE);
		if (threads != null)
			threads.getArticle().clear();
		Runnable  r = new ArticleRunnable(board, id, page);
		ByrThreadPool.getTHreadPool().submit(r);
	}
	
//	private Runnable runnable = new Runnable() {
//		
//		@Override
//		public void run() {
//			// 下载
//			while (isRunning) {
//				
//				//队列飞空，则下载
//				while (!task.isEmpty()) {
//					Task t = task.poll();
//					// 先从sd卡读取
//					Bitmap bitmap = ImageUtils.getInternetImage(t.url);
//					if (bitmap != null) {
//						
//						
//						if (t.imageView != null) {
//							Bitmap bm = ImageUtils.compressImage(bitmap, DataUtils.getDisplayValue(40), DataUtils.getDisplayValue(40));
//							addBitmapToMemoryCache(t.url, bm);
//							bitmap.recycle();
//							handler.obtainMessage(1,t).sendToTarget();
//							
//						}
//						else {
//							addBitmapToMemoryCache(t.url, bitmap);
//							handler.obtainMessage(2,t).sendToTarget();
//						}
//					}
//				}
//				
//				//task队列为空，则等待
//				synchronized (this) {
//					try {
//						this.wait();
//					} catch (InterruptedException e) {
//						isRunning = false;
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//	};
	

//	class Task {
//		String url;
//		ImageView imageView;
//		URLDrawable bd;
//		GifTextView tv;
//		@Override
//		public boolean equals(Object o) {
//			Task t = (Task)o;
//			if (this.url.equals(t.url)) return true;
//			return false;
//		}
//		
//		
//	}
	
	private  static  class ArticleHandler extends Handler {
		private SoftReference<ArticleActivity> activity;
		
		public ArticleHandler(ArticleActivity activity) {
			this.activity = new SoftReference<ArticleActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			
			ArticleActivity articleActivity = activity.get();
			if (articleActivity != null) {
				switch (msg.what) {
				case 0:
					articleActivity.threads = (Threads) msg.obj;
					
					Pagination p = articleActivity.threads.getPagination();
					
					if (articleActivity.threads.getArticle() != null) {
						articleActivity.mFArticle = articleActivity.threads.getArticle().get(0);
						articleActivity.mEditText.setHint("回复： " + articleActivity.mFArticle.getUser().getId());
					}
					articleActivity.lastPage = p.getPage_all_count();
					articleActivity.mCurretnPage = p.getPage_current_count();
					
					if (articleActivity.mCurretnPage > 1) {
						articleActivity.startLabels.setPullLabel("下拉加载第" + (articleActivity.mCurretnPage-1) + "页");
						articleActivity.startLabels.setReleaseLabel("放开以加载第" + (articleActivity.mCurretnPage-1) + "页");
					} else {
						articleActivity.startLabels.setPullLabel("当前第一页");
						articleActivity.startLabels.setReleaseLabel("当前第一页");
					}
					
					if (articleActivity.mCurretnPage == articleActivity.lastPage) {
						articleActivity.endLabels.setPullLabel("当前最后一页");
						articleActivity.endLabels.setReleaseLabel("当前最后一页");
					} else {
						articleActivity.endLabels.setPullLabel("下拉加载第" + (articleActivity.mCurretnPage+1) + "页");
						articleActivity.endLabels.setReleaseLabel("放开以加载第" + (articleActivity.mCurretnPage+1) + "页");
//						articleActivity.mPullToRefreshListView.setFooter("下拉加载第" + (articleActivity.mCurretnPage+1) + "页", "刷新中...", "放开以加载第" + (articleActivity.mCurretnPage+1) + "页");
					}
					//articleActivity.mPullToRefreshListView.onRefreshComplete();
					articleActivity.isRefreshable = true;
//					articleActivity.progress.setVisibility(View.GONE);
					
					articleActivity.mPullToRefreshListView.onRefreshComplete();
				//	articleActivity.mListView.stopRefresh();
					
					((BaseAdapter)articleActivity.mAdapter).notifyDataSetChanged();
					articleActivity.mPullToRefreshListView.getRefreshableView().setSelection(0);
					break;
				case 1:
					articleActivity.mPullToRefreshListView.onRefreshComplete();
					ViewUtils.displayMessage(articleActivity, (String)msg.obj);
//					ViewUtils.updateView(articleActivity, (String)msg.obj,
//							0,articleActivity.progress);
//					articleActivity.progress.setVisibility(View.VISIBLE);
					articleActivity.isRefreshable = true;
					break;
				case 2:
					ViewUtils.displayMessage(articleActivity,"删除成功");
					break;
				case 3:
					ViewUtils.displayMessage(articleActivity, (String)msg.obj);
					break;
				case 4:
					ViewUtils.displayMessage(articleActivity,"回复成功");
					break;
				case 5:
					ViewUtils.displayMessage(articleActivity, (String)msg.obj);
					break;
				case 6: 												//getthread error
					articleActivity.mPullToRefreshListView.onRefreshComplete();
//					articleActivity.progress.setVisibility(View.VISIBLE);
//					ViewUtils.updateView(articleActivity, articleActivity.getResources().getString(R.string.network_error),
//							1,articleActivity.progress);
					articleActivity.isRefreshable = true;
					
					break;
			    default:break;
				}
			}
		}	
	}

//	private ImageAcquireListener getContentImageAcquireListener (
//			final TextView textView, final URLDrawable bd,final Handler handler) {
//		
//		return new ImageAcquireListener() {
//			
//			@Override
//			public void onSuccess(final Bitmap bitmap) {
//				
//				handler.post(new Runnable() {
//					
//					@Override
//					public void run() {
//						int w = bitmap.getWidth();
//						int h = bitmap.getHeight();
//						bd.drawable = new BitmapDrawable(null,bitmap);
//
//						
////						int width = (int) (articleActivity.mListView.getWidth() - 20 * metrics.density);
////						
////						if (w > width && width > 0) {
////							t.bd.drawable.setBounds(0, 0, width,width * h / w);
////							t.bd.setBounds(0, 0, width,width * h / w);
////						} else {
////							t.bd.drawable.setBounds((width-w)/2, 0, (w+width)/2,h);
////							t.bd.setBounds((width-w)/2, 0, (width+w)/2,h);
////						}
//						 textView.setText(textView.getText());
//					}
//				});
//
//			}
//			
//			@Override
//			public void onFailure() {
//
//			}
//		};
//	}
	
	private ListAdapter mAdapter = new BaseAdapter() {
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
		
			int type = getItemViewType(position);
			ViewHolder holder = null;
			if (convertView == null) {
				
				convertView = LayoutInflater.from(ArticleActivity.this).inflate(
						R.layout.article_item,parent,false);
				
				holder = new ViewHolder();
				holder.face = (CircleImageView) convertView.findViewById(R.id.image_face);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.time = (TextView)convertView.findViewById(R.id.time);
				holder.ison = (TextView)convertView.findViewById(R.id.ison);
				holder.level = (TextView) convertView.findViewById(R.id.level);
				holder.operation = (ImageView) convertView.findViewById(R.id.more_op);
				holder.content = (GifTextView)convertView.findViewById(R.id.content);
				holder.reply = (ImageView) convertView.findViewById(R.id.reply);
				holder.delete = (ImageView) convertView.findViewById(R.id.delete);
				holder.edit = (ImageView) convertView.findViewById(R.id.edit);
				holder.forward = (ImageView) convertView.findViewById(R.id.forward);
				
				convertView.setTag(holder);
				
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			
			final Article article = threads.getArticle().get(position);
			
			holder.reply.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ArticleActivity.this,PostArticleActivity.class);
					intent.putExtra("title", "Re:" + article.getTitle());
					String username = "";
					if (article.getUser() != null)
						username = article.getUser().getId();
					intent.putExtra("content", DataUtils.getReplyContent(article.getContent(),username));
					intent.putExtra("reid", article.getId());
					intent.putExtra("board", board);
					startActivity(intent);
				}
			});
			
			holder.forward.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mFDialog.reset();
					mFDialog.setId(article.getId());
					mFDialog.show();
				}
			});
			
			
			
			//holder.face.setTag(threads.getArticle().get(position).getUser().getFace_url());
			final User user = threads.getArticle().get(position).getUser();
			if (user!=null) {
				
				String url = user.getFace_url();
				if (url != null) {
//					if (!isBusy)
					ImageCacheManager.INSTANCE.startAcquireImage(user.getFace_url(),ImageCacheManager.getFaceImageAcquireListener(holder.face,handler));
				} else {
					holder.face.setImageResource(R.drawable.face_default_m);
				}
				//loadImage(holder.face,user.getFace_url());
				holder.name.setText(user.getId());
				holder.face.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						Log.i(TAG,"face_onclick");
						
						UserInfoDialog userInfoDialog = new UserInfoDialog(ArticleActivity.this,user,handler);
						
						userInfoDialog.show();
					}
				});
				
				if (user.getGender() != null && user.getGender().equals("f")) {
					holder.name.setTextColor(getResources().getColor(R.color.pink));
				} else {
					holder.name.setTextColor(getResources().getColor(R.color.blue));
				}
				
				if (account.equals(user.getId()) || user.isIs_admin()) {
//					System.out.println(position + "   " + account + "   " + user.getId() + "   " + account.equals(user.getId()));
					holder.delete.setVisibility(View.VISIBLE);
					holder.delete.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							Runnable r =new DeleteRunnable(article.getId());
							ByrThreadPool.getTHreadPool().execute(r);
						}
					});
				} else {
					holder.delete.setVisibility(View.GONE);
//					System.err.println(position + "   " + account + "   " + user.getId() + "   " + account.equals(user.getId()));
				}
				
				if (account.equals(user.getId())) {
					holder.edit.setVisibility(View.VISIBLE);
					holder.edit.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(ArticleActivity.this,PostArticleActivity.class);
							intent.putExtra("title", article.getTitle());
							intent.putExtra("content", article.getContent());
							intent.putExtra("id", article.getId());
							intent.putExtra("board", board);
							intent.putParcelableArrayListExtra("file", article.getAttachment().getFile());
							startActivity(intent);
						}
					});
				} else {
					holder.edit.setVisibility(View.GONE);
				}
			}
			holder.time.setText(DataUtils.getDateString(threads.getArticle().get(position).getPost_time()));
			String level = "";
			int page = threads.getPagination().getPage_current_count();
			if (position == 0 && page == 1) 	level = "楼主"; 
			else if (position == 1 && page == 1) level = "沙发";
			else if (position == 2 && page == 1) level = "板凳";
			else level = ((page-1) * 10 + position) + "楼";
			holder.level.setText(level);
			
			holder.operation.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Log.i(TAG,"operation_onclick");
					String tag = (String)v.getTag();
					if (tag.equals("gray")) {
						//showPopupWindow(v,position);
						((ImageView)v).setImageResource(R.drawable.more_add_green);
						v.setTag("green");
						((FrameLayout) v.getParent()).getChildAt(1).setVisibility(View.VISIBLE);
					}  else {
						((ImageView)v).setImageResource(R.drawable.more_add_gray);
						v.setTag("gray");
						v.invalidate();
						((FrameLayout) v.getParent()).getChildAt(1).setVisibility(View.GONE);
					}	
				}
			});
			holder.content.setMovementMethod(LinkMovementMethod.getInstance());
		//	holder.content.setBackgroundResource(R.drawable.bd);
//
			CharSequence sequence = Html.fromHtml(article.getContent(),
					new URLImageGetter(holder.content,ArticleActivity.this, article.getAttachment(), handler,parent.getWidth(),parent.getHeight()), new MyTagHandler(ArticleActivity.this,article.getAttachment()));
			holder.content.setText(sequence);

			
//			holder.content.setText(content);
			holder.content.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mEditText.setHint("回复： " + article.getUser().getId());
					mEditText.clearFocus();
					mEditText.setText("");
					mFArticle = article;
				}
			});
			
			//设置可move~
			//pass 
			//holder.ison.setBackgroundColor(Color.BLUE);
			
			if (type == 0) {
				convertView.setPadding(DataUtils.getDisplayValue(10), DataUtils.getDisplayValue(3), DataUtils.getDisplayValue(10), DataUtils.getDisplayValue(25));
			}
			return convertView;
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public Object getItem(int position) {
			return null;
		}
		
		@Override
		public int getCount() {
			if (threads != null && threads.getArticle() != null)
			return threads.getArticle().size();
			
			return 0;
		}

		@Override
		public int getItemViewType(int position) {
			return threads.getPagination().getItem_page_count() == position+1?0:1;
		}

		@Override
		public int getViewTypeCount() {
			
			return 2;
		}	
		
	};

	static class ViewHolder {
		CircleImageView face;
		TextView name;
		TextView level;
		TextView time;
		TextView ison;
		ImageView operation;
		GifTextView content;			//最复杂的
		ImageView reply;
		ImageView forward;
		ImageView edit;
		ImageView delete;
	}
//	@SuppressLint("NewApi")
//	private void showPopupWindow(final View v,int position) {
//		
//		final Article article = threads.getArticle().get(position);
//		
//		reply.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(ArticleActivity.this,PostArticleActivity.class);
//				intent.putExtra("title", "Re:" + article.getTitle());
//				String username = "";
//				if (article.getUser() != null)
//					username = article.getUser().getId();
//				intent.putExtra("content", DataUtils.getReplyContent(article.getContent(),username));
//				intent.putExtra("reid", article.getId());
//				intent.putExtra("board", board);
//				startActivity(intent);
//			}
//		});
//		
//		update.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(ArticleActivity.this,PostArticleActivity.class);
//				intent.putExtra("title", article.getTitle());
//				intent.putExtra("content", article.getContent());
//				intent.putExtra("id", article.getId());
//				intent.putExtra("board", board);
//				intent.putParcelableArrayListExtra("file", article.getAttachment().getFile());
//				startActivity(intent);
//			}
//		});
//		
//		delete.setOnClickListener(new View.OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				Runnable r =new DeleteRunnable(article.getId());
//				ByrThreadPool.getTHreadPool().execute(r);
//			}
//			
//		});
//		
//		
//		more.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				
//			}
//		});
		
		
//		ImageView image = (ImageView) layout.findViewById(R.id.triangle);
//		
//		window.setOnDismissListener(new PopupWindow.OnDismissListener() {
//			
//			@Override
//			public void onDismiss() {
//				// TODO Auto-generated method stub
//				((ImageView)v).setImageResource(R.drawable.more_add_gray);
//				v.setTag("gray");
//				
//			}
//		});
//	    
//		
//	    layout.measure(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//
//	    int[] location = new int[2];
//		// 得到anchor的位置
//		v.getLocationOnScreen(location);
//		Rect anchorRect = new Rect(location[0], location[1], location[0]
//				+ v.getWidth(), location[1] + v.getHeight());
//	    int xpos = (anchorRect.right - layout.getMeasuredWidth());
//	    int ypos = anchorRect.bottom;
//
//	    MarginLayoutParams params = (ViewGroup.MarginLayoutParams) 
//	    		image.getLayoutParams();
//	    params.leftMargin = (int)(layout.getMeasuredWidth() -
//	    		(anchorRect.width()+image.getMeasuredWidth())/2) ;
//	    window.showAtLocation(v,Gravity.NO_GRAVITY,xpos,ypos);
//	}
	
//	private void initPopupWindow() {
//		
//		layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.operation_popup,null);
//		window = new PopupWindow(layout, 
//				WindowManager.LayoutParams.WRAP_CONTENT,
//				WindowManager.LayoutParams.WRAP_CONTENT,true);
//		
//		
//		window.setBackgroundDrawable(new ColorDrawable(-000000));
//		window.setTouchable(true);
//	    window.setOutsideTouchable(true);
//	    
//	    
//		reply = (TextView) layout.findViewById(R.id.reply);
//		update = (TextView) layout.findViewById(R.id.update);
//		delete = (TextView) layout.findViewById(R.id.delete);
//		more = (TextView) layout.findViewById(R.id.more);
//	    
//	}
	

	private class DeleteRunnable implements Runnable {
		
		private int id;
		
		
		public DeleteRunnable(int id) {
			super();
			this.id = id;
		}


		@Override
		public void run() {
			HttpUtils.deleteArticle(board, id,new HttpUtils.HttpRequestListener() {
				
				@Override
				public void onSuccess(String content) {
					
					handler.obtainMessage(2).sendToTarget();
				}
				
				@Override
				public void onFailed(String reason) {
					handler.obtainMessage(3).sendToTarget();
				}
				
				@Override
				public void onError(Throwable e) {
					handler.obtainMessage(6).sendToTarget();
				}
			});
		}
	}
	
	private class ArticleRunnable implements Runnable {

		private String board;
		private int id;
		private int page;
	
		
		public ArticleRunnable(String board, int id, int page) {
			super();
			this.board = board;
			this.id = id;
			this.page = page;
		}


		@Override
		public void run() {
			
			HttpUtils.httpRequest("/threads/" + board + "/" + id + ".json?page=" + page, new HttpUtils.HttpRequestListener() {
				
				@Override
				public void onSuccess(String content) {
					Threads thread = (Threads) JsonUtils.toBean(content,Threads.class);
					
					if (thread != null) {
						for (Article a : thread.getArticle()) {
							a.setContent(DataUtils.getHtmlFromString(a.getContent(),a.getAttachment()));
						}
						handler.obtainMessage(0,thread).sendToTarget();
					}
				}
				
				@Override
				public void onFailed(String reason) {
					handler.obtainMessage(1,reason).sendToTarget();
				}
				
				@Override
				public void onError(Throwable e) {
					handler.obtainMessage(6).sendToTarget();
				}
			});
		}
		
	}
	
	private String getUserAccount() {
		SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
		return preferences.getString("account", "guest");
	}
}

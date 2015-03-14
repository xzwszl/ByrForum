package xzw.szl.byr;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import xzw.szl.byr.assist.PostArticleActivity;
import xzw.szl.byr.db.DBListTableHandler;
import xzw.szl.byr.info.Article;
import xzw.szl.byr.info.Board;
import xzw.szl.byr.info.Pagination;
import xzw.szl.byr.mananger.ByrThreadPool;
import xzw.szl.byr.utils.DataUtils;
import xzw.szl.byr.utils.HttpUtils;
import xzw.szl.byr.utils.JsonUtils;
import xzw.szl.byr.utils.ViewUtils;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BoardActivity extends BaseActivity{
	
	private Board mBoard;
	private ListView mListView;
	private BoardHander mHander;
	private ActionBar mActionBar;
	private EditText mEditText;
	private TextView mTextView;
	private String mBordName;
	private View mHeaderView;
//	private ProgressBar progressBar;
	private MenuItem refreshItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		ViewUtils.switchTheme(this);
		setContentView(R.layout.activity_board);
		init();
	}
	
	private void init() {
	
//		progressBar = ViewUtils.getProgressBar(this);
		addActionBar();
		mHander = new BoardHander(this);
		mEditText = (EditText)findViewById(R.id.got);
		mTextView = (TextView) findViewById(R.id.page);
		TextView pre = (TextView) findViewById(R.id.pre);
		TextView next = (TextView) findViewById(R.id.next);
		TextView first = (TextView) findViewById(R.id.first);
		TextView last = (TextView) findViewById(R.id.last);
		TextView go = (TextView) findViewById(R.id.go);
		pre.setOnClickListener(mClickListener);
		next.setOnClickListener(mClickListener);
		first.setOnClickListener(mClickListener);
		last.setOnClickListener(mClickListener);
		go.setOnClickListener(mClickListener);
		
		mListView = (ListView) findViewById(R.id.articlelist);
		
		mListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//if ()
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int  visibleItemCount, int totalItemCount) {
				
				if (view.getChildAt(firstVisibleItem) != null) {
					int top = view.getChildAt(0).getTop();
					if (firstVisibleItem == 0) {					
						mHeaderView.setPadding(0, -top, 0, top);
					} 
				} 
			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
					if (position == 0) return;
					
					Intent intent = new Intent(BoardActivity.this,ArticleActivity.class);
					intent.putExtra("board",mBoard.getName());
					intent.putExtra("aid", mBoard.getArticle().get(position-1).getId());
					intent.putExtra("atitle", mBoard.getArticle().get(position-1).getTitle());
					startActivity(intent);
			}
			
		});
		
		Intent intent = getIntent();
		mBordName = intent.getStringExtra("boardname");
		setTitle(mBordName);
		getArticlesData(1,false);
	}
	
	private void addActionBar() {
		getOverflowMenu();
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	private void getOverflowMenu() {
        try {
           ViewConfiguration config = ViewConfiguration.get(this);
           Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
           if(menuKeyField != null) {
               menuKeyField.setAccessible(true);
               menuKeyField.setBoolean(config, false);
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
	}
	
	private void getArticlesData (int page,boolean isFromNetwork) {
//		mListView.setEmptyView(progressBar);
		Runnable r = new ArticleRunnalbe(page,isFromNetwork);
		ByrThreadPool.getTHreadPool().execute(r);
	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.board, menu);
//		 SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//	     SearchView searchView = (SearchView)menu.findItem(R.id.search).getActionView();
//	     searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id) {
//		case android.R.id.home:
//			Intent upIntent = NavUtils.getParentActivityIntent(this);
//			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
//				TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
//			} else {
//				upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				NavUtils.navigateUpTo(this, upIntent);
//			}
//			return true;
		case R.id.write_article:
			Intent intent = new Intent(this,PostArticleActivity.class);
			intent.putExtra("board", mBordName);
			startActivity(intent);
			return true;
		case R.id.board_refresh:
			showRefreshAnimation(item);
			int page = 1;
			if (mBoard != null && mBoard.getPagination() != null) {
				Pagination pagination = mBoard.getPagination();
				page = pagination.getPage_current_count();
			}
			getArticlesData(page,true);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public View getHeader() {
		View view = LayoutInflater.from(this).inflate(R.layout.board_article_header, mListView,false);
		
		((TextView)view.findViewById(R.id.boardname)).setText("☆ ☆ " + mBoard.getDescription() +  " ☆ ☆");
		((TextView)view.findViewById(R.id.boardmanager)).setText("版主: " + mBoard.getManager());
		((TextView)view.findViewById(R.id.data)).setText("总帖数: " + mBoard.getPost_all_count() +
				"  今日发文: " + mBoard.getPost_today_count() + "  在线人数: " + mBoard.getUser_online_count());
		
		return view;
	}
	
	
	private ListAdapter mAdapter = new BaseAdapter() {
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder  viewHolder = null;
			Drawable drawable = null;
			if (convertView == null) {
				
				convertView = LayoutInflater.from(BoardActivity.this).
						inflate(R.layout.board_article,parent,false);
				
				viewHolder = new ViewHolder();
				viewHolder.authName = (TextView) convertView.findViewById(R.id.articleauth);
			//	viewHolder.articleState = (ImageView) convertView.findViewById(R.id.articlestate);
				viewHolder.article = (TextView) convertView.findViewById(R.id.articletitle);
				viewHolder.time = (TextView) convertView.findViewById(R.id.articletime);
				viewHolder.reply = (TextView)convertView.findViewById(R.id.articlereply);
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			Article article = mBoard.getArticle().get(position);
			
			if (article.getUser() == null || article.getUser().isIs_admin()) {
				viewHolder.authName.setText("admin");
			} else {
				viewHolder.authName.setText(article.getUser().getId());
				
//				if (article.getUser().getGender() != null && article.getUser().getGender().equals("f")) {
//					drawable = getResources().getDrawable(R.drawable.user_female2);
//				}
			}
			
			if (drawable == null) { 
				drawable = getResources().getDrawable(R.drawable.board_user);
			}
			drawable.setBounds(0, 0, DataUtils.getDisplayValue(12),DataUtils.getDisplayValue(12));
			viewHolder.authName.setCompoundDrawables(drawable, null, null, null);
			
			viewHolder.article.setText(article.getTitle());
			// 可以分别考虑
			drawable = getResources().getDrawable(R.drawable.article_normal);
		
			
			if (article.isIs_top()) {
				drawable = getResources().getDrawable(R.drawable.article10);
			}else if (article.getReply_count() < 10) {
				drawable = getResources().getDrawable(R.drawable.article2);
			} else if(article.getReply_count() >= 10) {
				drawable = getResources().getDrawable(R.drawable.article1);
			}
		//	drawable.setBounds(0, 0, DataUtils.getDisplayValue(20),DataUtils.getDisplayValue(20));
			drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
			Drawable drawable_attach = null;
			if (article.isHas_attachment()) {
				drawable_attach = getResources().getDrawable(R.drawable.article9);
				drawable_attach.setBounds(0, 0, drawable_attach.getIntrinsicWidth(),drawable_attach.getIntrinsicHeight());
			}
			viewHolder.article.setCompoundDrawables(drawable, null,drawable_attach,null);
			
			viewHolder.time.setText(DataUtils.getRelativeDateString(article.getPost_time()));
//			drawable = getResources().getDrawable(R.drawable.post_time);
//			drawable.setBounds(0, 0, DataUtils.getDisplayValue(12),DataUtils.getDisplayValue(12));
//			viewHolder.time.setCompoundDrawables(drawable, null, null, null);
			
			viewHolder.reply.setText(article.getReply_count()+"");
			drawable = getResources().getDrawable(R.drawable.article_reply);
			drawable.setBounds(0, 0, DataUtils.getDisplayValue(12),DataUtils.getDisplayValue(12));
			viewHolder.reply.setCompoundDrawables(drawable, null, null, null);
			
			return convertView;
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public Object getItem(int position) {
			return mBoard.getArticle().get(position);
		}
	
		@Override
		public int getCount() {

			if (mBoard.getArticle() != null)
				return mBoard.getArticle().size();
			else 
				return 0;
		}
	};
	
	static class ViewHolder {
		TextView authName;
		ImageView articleState;
		TextView article;
		TextView time;
		TextView reply;
	}
	
	public static class BoardHander extends Handler {
		private WeakReference<BoardActivity> board;
		
		public BoardHander (BoardActivity board) {
			this.board = new WeakReference<BoardActivity>(board);
		}

		@Override
		public void handleMessage(Message msg) {
			
			BoardActivity activity = board.get();
			if (activity != null) {
				
				switch (msg.what) {
				case 0:
						activity.mBoard = (Board)msg.obj;
						if (activity.mHeaderView == null){
							activity.mHeaderView = activity.getHeader();
							activity.mListView.addHeaderView(activity.mHeaderView);
						}
						activity.mHeaderView.setPadding(0, 0, 0, 0);
						activity.mListView.setAdapter(activity.mAdapter);
						Pagination pagination = activity.mBoard.getPagination();
						if (pagination != null)
						activity.mTextView.setText(pagination.getPage_current_count() + 
								"/" + pagination.getPage_all_count());
						activity.clearRefreshAnimation();
					break;
				case 1:
					ViewUtils.displayMessage(activity,(String)msg.obj);
					break;
				case 2:
					ViewUtils.displayMessage(activity,activity.getResources().getString(R.string.network_error));
					break;
				default:
					break;
				}
				
			}
		}
	}

	private OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
		
			if (mBoard != null && mBoard.getPagination() != null) {
			
				Pagination pagination = mBoard.getPagination();
				int cur = pagination.getPage_current_count();
				int total = pagination.getPage_all_count();
				switch (view.getId()) {
					case R.id.pre:
						if (cur == 1) displayMessage("已经第一页");
						else getArticlesData(cur-1,false);
						break;
					case R.id.next:
						if (cur == total) displayMessage("已经最后一页");
						else getArticlesData(cur+1,false);
						break;
					case R.id.first:
						getArticlesData(1,false);
						break;
					case R.id.last:
						getArticlesData(total,false);
						break;
					case R.id.go:
						String page = mEditText.getText().toString();
						if (page.matches("[0-9]+")) {
							getArticlesData(Integer.parseInt(page),false);
						} else {
							displayMessage("请输入整数");
						}
						break;
				}
			}
		}	
	};
	
	public void displayMessage(String msg){
		Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
	}
	
	private class ArticleRunnalbe implements Runnable {
		private int page;
		private boolean isFromNetwork;
		public ArticleRunnalbe (int page,boolean isFromNetwork) {
			this.page = page;
			this.isFromNetwork = isFromNetwork;
		}

		@Override
		public void run() {
				
			String content = isFromNetwork? null : DBListTableHandler.getInstance().queryItemContent(mBordName, page);
			if (content != null) {
				Board board = (Board) JsonUtils.toBean(content, Board.class);			
				if (board != null) {
					mHander.obtainMessage(0,board).sendToTarget();
				}	
			} else {
				HttpUtils.httpRequest("/board/" + mBordName + ".json?page=" + page, new HttpUtils.HttpRequestListener() {
					
					@Override
					public void onSuccess(String content) {
						Board board = (Board) JsonUtils.toBean(content, Board.class);			
						if (board != null) {
							mHander.obtainMessage(0,board).sendToTarget();
							DBListTableHandler.getInstance().updateTypeListTable(mBordName, page, content);
						}	
					}
					
					@Override
					public void onFailed(String reason) {
						mHander.obtainMessage(1,reason).sendToTarget();
					}
					
					@Override
					public void onError(Throwable e) {
						mHander.obtainMessage(2).sendToTarget();
					}
				});
			}
		}
		
	}
	public void clearRefreshAnimation() {
		if (refreshItem != null) {
			View view = refreshItem.getActionView();
			if (view != null) {
				view.clearAnimation();
				refreshItem.setActionView(null);
			}
		}
	}
	
	public void showRefreshAnimation(MenuItem item) {
		
		clearRefreshAnimation();        //清除动画
//		ImageView imageview = new ImageView(this);
//		imageview.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
//				LayoutParams.WRAP_CONTENT));
//		imageview.setImageResource(R.drawable.ic_action_refresh);
//		
//		item.setActionView(imageview);
		
		ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.board_action_view,null);
		
		
		item.setActionView(imageView);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.refresh);
		animation.setRepeatMode(Animation.RESTART);
		animation.setRepeatCount(Animation.INFINITE);
		imageView.startAnimation(animation);
		
		refreshItem = item;
	}
}

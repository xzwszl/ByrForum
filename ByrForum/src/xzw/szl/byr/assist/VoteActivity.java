package xzw.szl.byr.assist;

import java.lang.ref.WeakReference;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import xzw.szl.byr.BaseActivity;
import xzw.szl.byr.R;
import xzw.szl.byr.db.DBListTableHandler;
import xzw.szl.byr.info.ByrVote;
import xzw.szl.byr.info.Pagination;
import xzw.szl.byr.info.Vote;
import xzw.szl.byr.mananger.ByrThreadPool;
import xzw.szl.byr.mananger.ImageCacheManager2;
import xzw.szl.byr.utils.ByrBase;
import xzw.szl.byr.utils.DataUtils;
import xzw.szl.byr.utils.HttpUtils;
import xzw.szl.byr.utils.JsonUtils;
import xzw.szl.byr.utils.ViewUtils;
import xzw.szl.byr.view.CircleImageView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class VoteActivity extends BaseActivity implements OnRefreshListener {
	
	
	private SwipeRefreshLayout layout;
	private ListView mListView;
	private TextView bottomTextView;
	private ByrVote[] mByrVote;
	private String[] mVote;
	private String[] mVoteDes;
	private Handler mHandler;
	
	private int mCurrentVote;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		ViewUtils.switchTheme(this);
		setContentView(R.layout.activity_myarticle);                             //重用myarticle 的xml文件
		
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.vote, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
//		case android.R.id.home:
//			
//			Intent upIntent = NavUtils.getParentActivityIntent(this);
//			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
//				TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
//			} else {
//				upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				NavUtils.navigateUpTo(this, upIntent);
//			}
//			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private View bottomView() {
		
		TextView tv = new TextView(this);
		tv.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
				AbsListView.LayoutParams.WRAP_CONTENT));
		tv.setTextColor(getResources().getColor(R.color.light_grey));
		tv.setGravity(Gravity.CENTER);
		tv.setMinHeight(DataUtils.getDisplayValue(30,getApplicationContext()));
	//	tv.setBackgroundColor(getResources().getColor(R.color.yellow));
		tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String str = ((TextView)v).getText().toString();
				
				if (str.startsWith("第")) {
					int page = mByrVote[mCurrentVote].getPagination().getPage_current_count()+1;
					getVote(page,false);
					
				} else if (str.startsWith("数据加载失败")){
					getVote(1,false);
				}
			}
		});
		return tv;
	}
	
	private void initVote() {
		
		mCurrentVote = 0;
		mByrVote= new ByrVote[5];
		mVote = new String[]{"new","hot","all","list","join"};
		mVoteDes = new String[]{"最新投票","最热投票","全部投票","我的投票","我参与的投票"};
		mHandler = new VoteHandler(this);
	}
	
	private void init() {
		
		initVote();
		
		
		SpinnerAdapter spinnerAdapter = MyArrayAdapter.createFromResource(this, 
				R.array.action_vote, android.R.layout.simple_spinner_dropdown_item);
				
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		getSupportActionBar().setListNavigationCallbacks(spinnerAdapter, new OnNavigationListener() {
			
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				mCurrentVote = itemPosition;
				getVote(1,false);
				return true;
			}
		});
		
		bottomTextView = (TextView) bottomView();
		layout = (SwipeRefreshLayout) findViewById(R.id.layout_container);
		layout.setOnRefreshListener(this);
		layout.setColorSchemeColors(getResources().getColor(R.color.section_pos1)
				,getResources().getColor(R.color.section_pos4)
				,getResources().getColor(R.color.section_pos1)
				,getResources().getColor(R.color.section_pos4));
		
		mListView = (ListView) findViewById(R.id.listview_myarticle);
		mListView.addFooterView(bottomTextView);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if (mByrVote[mCurrentVote] != null && mByrVote[mCurrentVote].getVotes()!= null && mByrVote[mCurrentVote].getVotes().get(position) != null) {
					Vote vote = mByrVote[mCurrentVote].getVotes().get(position);
					
					if (vote.isIs_deleted()) {
						Toast.makeText(VoteActivity.this, "投票已删除", Toast.LENGTH_SHORT).show();
						return;
					}
					
					String voteid = vote.getVid();
					
					Intent intent = new Intent(VoteActivity.this,VoteDetailActivity.class);
					intent.putExtra("voteid", voteid);
					startActivity(intent);
				}
				
			}
	
		});
		
		getVote(1,false);
	}
	
	private BaseAdapter mAdapter = new  BaseAdapter() {
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if (convertView == null) {
				
				holder = new ViewHolder();
				convertView = LayoutInflater.from(VoteActivity.this).inflate(R.layout.vote_item, parent, false);
				
				holder.face = (CircleImageView) convertView.findViewById(R.id.vote_imageview);
				holder.id = (TextView) convertView.findViewById(R.id.vote_id);
				holder.title = (TextView) convertView.findViewById(R.id.vote_title);
				holder.end = (TextView) convertView.findViewById(R.id.vote_end);
				holder.num = (TextView) convertView.findViewById(R.id.vote_num);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			Vote vote = mByrVote[mCurrentVote].getVotes().get(position);

			if (vote!= null) {
				
				if (vote.getUser() != null) {
					
					String url = vote.getUser().getFace_url();
					if (url != null) 
						ImageCacheManager2.INSTANCE.startAcquireImage2(url,
								ImageCacheManager2.getFaceImageAcquireListener(holder.face,mHandler),
								DataUtils.getDisplayValue(ByrBase.FACE_WIDTH,getApplicationContext()),
								DataUtils.getDisplayValue(ByrBase.FACE_WIDTH,getApplicationContext()),
								false);
					else
						holder.face.setImageResource(R.drawable.face_default_m);
					
					holder.id.setText(vote.getUser().getId());
					
					if (vote.getUser().getGender()!= null && vote.getUser().getGender().equals("f")) {
						holder.id.setTextColor(getResources().getColor(R.color.pink));
					} else {
						holder.id.setTextColor(getResources().getColor(R.color.dark_blue));
					}
				}
				
				holder.title.setText(vote.getTitle());
				
				if (!vote.isIs_end()) {
					String time = DataUtils.getDateString(Integer.valueOf(vote.getEnd()));
					holder.end.setText("截至" + time.substring(0, time.length()-9));
				} else {
					holder.end.setText("已截止");
				//	holder.end.setText(VoteActivity.this.getResources().getColor(R.color.grey));
				}
				
				holder.num.setText(vote.getUser_count());
				
			} 
			
			return convertView;
		}
		
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public int getCount() {

			if (mByrVote[mCurrentVote] == null || mByrVote[mCurrentVote].getPagination() == null) 
				return 0;
			return mByrVote[mCurrentVote].getPagination().getItem_page_count();
		}
	};
	
	public class ViewHolder {
		ImageView face;
		TextView title;
		TextView start;
		TextView end;
		TextView id;
		TextView num;
	}
	
	private void getVote(final int page,final boolean isFromNetwork) {
		
		mAdapter.notifyDataSetChanged();
		if (mByrVote[mCurrentVote] == null || page > 1) {
			
			bottomTextView.setText("努力加载中哦哦~~");
			ByrThreadPool.getTHreadPool().execute(new Runnable() {
				
				@Override
				public void run() {
					getVoteDetail(mCurrentVote,page,isFromNetwork);
				}
			});
		} else {
			mHandler.obtainMessage(0,mCurrentVote).sendToTarget();
			
		}
	}
	
	private String getUrl(int index,int page) {
		
		String url = "/vote/category/" + mVote[index] +".json?page="+page ;
		
		
		if (index == 3) {           //我的投票
			String user = getSharedPreferences("user", Context.MODE_PRIVATE).getString("account",null);
			
			if (user != null)
				url += "&u=" + user;
		}
		
		return url;
	}
	
	private class MyVote {
		ByrVote bVote;
		int page;
		int index;
	}
	
	private synchronized void getVoteDetail (final int index,final int page,boolean isFromNetwork) {
		
		
		String content = isFromNetwork? null : DBListTableHandler.getInstance().queryItemContent(mVoteDes[index], page);
		
		if (content != null) {
			ByrVote bVote = (ByrVote) JsonUtils.toBean(content, ByrVote.class);
			
			if (bVote != null) {
				
				MyVote mv = new MyVote();
				mv.bVote = bVote;
				mv.index = index;
				mv.page = page;
				mHandler.obtainMessage(0,mv).sendToTarget();
			}
		} else {
			HttpUtils.httpRequest(getUrl(index,page), new HttpUtils.HttpRequestListener() {
				
				@Override
				public void onSuccess(String content) {
					
					ByrVote bVote = (ByrVote) JsonUtils.toBean(content, ByrVote.class);
					
					if (bVote != null) {
						
						MyVote mv = new MyVote();
						mv.bVote = bVote;
						mv.index = index;
						mv.page = page;
						mHandler.obtainMessage(0,mv).sendToTarget();
						DBListTableHandler.getInstance().updateTypeListTable(mVoteDes[index], page, content);
					}
				}
				
				@Override
				public void onFailed(String reason) {
					mHandler.obtainMessage(1,reason).sendToTarget();
				}
				
				@Override
				public void onError(Throwable e) {
					mHandler.obtainMessage(2).sendToTarget();
				}
			});
		}
	}
	
	@Override
	public void onRefresh() {
		mByrVote[mCurrentVote] = null;
		mAdapter.notifyDataSetChanged();
		getVote(1,true);
	}
	
	public static class VoteHandler extends Handler {
		private WeakReference<Activity> reference;
		
		
		public VoteHandler(Activity  activity) {
			
			this.reference = new WeakReference<Activity>(activity);
		}


		@Override
		public void handleMessage(Message msg) {
			
			VoteActivity activity = (VoteActivity) reference.get();
			
			if (activity != null) {
				switch (msg.what) {
				case 0:
					
					Pagination p = null;
					int index = -1;
					if (msg.obj instanceof Integer) {
						index = (Integer) msg.obj;
						p = activity.mByrVote[index].getPagination();
					} else {
						MyVote mv = (MyVote) msg.obj;
						index = mv.index;
						p = mv.bVote.getPagination();
						if (mv.page == 1 || activity.mByrVote[mv.index] == null) {
							activity.mByrVote[mv.index] = mv.bVote;
						} else {
							
							activity.mByrVote[mv.index].getPagination().setPage_current_count(p.getPage_current_count());
							activity.mByrVote[mv.index].getPagination().setItem_page_count(activity.mByrVote[mv.index].getPagination().getItem_page_count() + p.getItem_page_count());
							activity.mByrVote[mv.index].getVotes().addAll(mv.bVote.getVotes());
							
						}
					}

					if (p != null) {
						if (p.getItem_page_count() == 0 && p.getPage_all_count() == 1) {
							activity.bottomTextView.setText(activity.mVoteDes[index] + "为空╮(╯▽╰)╭");
						} else if (p.getPage_all_count() == p.getPage_current_count()) {
							activity.bottomTextView.setText("当前到达最后一页哦~~");
						} else
							activity.bottomTextView.setText("第" + p.getPage_current_count() + "/" + p.getPage_all_count() + "页  点击加载更多");
						activity.mAdapter.notifyDataSetChanged();                  //success
						activity.layout.setRefreshing(false);
					}
				//	}
					break;
				case 1:
					activity.bottomTextView.setText((String) msg.obj);
					activity.layout.setRefreshing(false);
					break;
				case 2:
					activity.bottomTextView.setText(activity.getResources().getString(R.string.network_error));
					break;
				case 3:
					ViewUtils.displayMessage(activity, activity.getResources().getString(R.string.network_error));
					break;
				default:
					break;
				}
			}
			
		}
	}
	
	public static class MyArrayAdapter<T> extends ArrayAdapter<T> {


	    public MyArrayAdapter(Context context, int textViewResourceId,
				T[] objects) {
			super(context,textViewResourceId, objects);
			// TODO Auto-generated constructor stub
		}

		public static MyArrayAdapter<CharSequence> createFromResource(Context context, int resource, int textViewResourceId) {
	        
			String[] strings = context.getResources().getStringArray(R.array.action_vote);
	    	return new MyArrayAdapter<CharSequence>(context,textViewResourceId,strings);
	    }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			return SetColourWhite(super.getView(position, convertView, parent));
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			return SetColourWhite(super.getDropDownView(position, convertView, parent));
		}
		
		private View SetColourWhite(View v) {
	        if (v instanceof TextView) ((TextView)v).setTextColor(Color.rgb(0xfff, 0xfff, 0xfff)); // white
	        return v;
	    }	
	}
}

package xzw.szl.byr.fragment;

import java.lang.ref.WeakReference;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import xzw.szl.byr.ArticleActivity;
import xzw.szl.byr.HomeActivity;
import xzw.szl.byr.R;
import xzw.szl.byr.db.DBListTableHandler;
import xzw.szl.byr.info.Article;
import xzw.szl.byr.info.TopTen;
import xzw.szl.byr.info.User;
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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
//import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class HomeFragment extends Fragment{
	
	private TopTen mTopTen;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private PullToRefreshListView mListView;
	private HomeHandler mHandler;
	private TextView  progress;
	private View view;
	
	public void setData(TopTen topTen) {
		mTopTen = topTen;
	}
	
	public TopTen getData() {
		return mTopTen;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		init(savedInstanceState);
		((HomeActivity)getActivity()).setHomeFragment(this);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		HomeActivity homeActivity = (HomeActivity) getActivity().getLastCustomNonConfigurationInstance();
		if (homeActivity != null) {
			mTopTen = ((HomeFragment)homeActivity.getFragment(1)).mTopTen;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		ViewUtils.switchTheme(getActivity());
		return inflater.inflate(R.layout.fragment_home,container,false);
	}
	
	private void initView() {
		view = ViewUtils.getLoadingLayout(getActivity());
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER;
		view.setLayoutParams(lp);
		view.setVisibility(View.GONE);
		view.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				TextView tv = (TextView)((ViewGroup) v).getChildAt(1);
				if (tv.getText().equals(getResources().getString(R.string.loading_failed))) {
					ViewUtils.changeToLoading(v, getActivity());
					getTopten(true);
				}
			}
		});
	}
	
	private void init(Bundle savedInstanceState) {
		
		initView();
//		progress = ViewUtils.getFailedlayout(getActivity());

		
//		progress.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				
//				String text = ((TextView)v).getText().toString();
//				if (text.startsWith("网络")) {	//判断网络是否打开
//					getActivity().recreate();
//				}
//			}
//		});
		mListView = (PullToRefreshListView) getActivity().findViewById(R.id.listview_topten);
		mListView.setMode(Mode.PULL_FROM_START);
		ILoadingLayout startLabels = mListView.getLoadingLayoutProxy(true,false);
		
		startLabels.setPullLabel("下拉刷新…");
		startLabels.setRefreshingLabel(" ");
		startLabels.setReleaseLabel("释放以刷新…");
		
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//				mListView.getLoadingLayoutProxy()
				getTopten(true);
			}
			
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
					int realPosition = position -1;
					Intent intent = new Intent(getActivity(),ArticleActivity.class);
					intent.putExtra("board",mTopTen.getArticle().get(realPosition).getBoard_name());
					intent.putExtra("aid", mTopTen.getArticle().get(realPosition).getId());
					intent.putExtra("atitle",mTopTen.getArticle().get(realPosition).getTitle());
					startActivity(intent);
			}
			
		});
		
		
		
//		mSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_container);
//		
//		mSwipeRefreshLayout.setOnRefreshListener(this);
//		mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.section_pos1)
//				,getResources().getColor(R.color.section_pos4)
//				,getResources().getColor(R.color.section_pos1)
//				,getResources().getColor(R.color.section_pos4));
		
		//mTextView = (TextView) getActivity().findViewById(R.id.textview_failure);
		
		mHandler  = new HomeHandler(this);
		((ViewGroup)mListView.getParent()).addView(view);
		mListView.setAdapter(mAdapter);
//		TextView emptyView = new TextView(getActivity());  
//		emptyView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));  
//		emptyView.setText("This appears when the list is empty");  
//		emptyView.setVisibility(View.GONE);
//		progress.setVisibility(View.GONE);
//		((ViewGroup)mListView.getParent().getParent()).addView(progress);
//		mListView.setEmptyView(progress);
	//	mListView.setEmptyView(emptyView);
		if (savedInstanceState == null) {
			getTopten(false);
			
		} else {
			
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private void getTopten(final boolean isFromNetwork) {
	//	ViewUtils.updateView(getActivity(), "加载中...", 2, progress);
		view.setVisibility(View.VISIBLE);
		mListView.setClickable(false);
		mTopTen = null;
		mAdapter.notifyDataSetChanged();
	//	progress.setVisibility(View.GONE);
		
		ByrThreadPool.getTHreadPool().execute(new Runnable() {
		
		@Override
		public void run() {
			
			String content =!isFromNetwork? DBListTableHandler.getInstance().queryItemContent(DBListTableHandler.TYPE_TOPTEN,1) : null;
			
			if (content != null) {
				TopTen topten = (TopTen) JsonUtils.toBean(content, TopTen.class);
				if (topten != null) {
					mHandler.obtainMessage(0,topten).sendToTarget();
				}
			} else {
				HttpUtils.httpRequest("/widget/topten.json", new HttpUtils.HttpRequestListener() {
					
					@Override
					public void onSuccess(String content) {
						TopTen topten = (TopTen) JsonUtils.toBean(content, TopTen.class);
						
						if (topten != null) {
							mHandler.obtainMessage(0,topten).sendToTarget();
							 DBListTableHandler.getInstance().updateItemListTable(DBListTableHandler.TYPE_TOPTEN,1,content);
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
	});
	}
	
	private BaseAdapter mAdapter = new BaseAdapter() {
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.topen, parent,false);
				holder.article = (TextView) convertView.findViewById(R.id.topten_article);
				holder.user = (TextView) convertView.findViewById(R.id.topten_user);
				holder.reply = (TextView) convertView.findViewById(R.id.topten_reply_up);
				holder.love = (ImageView) convertView.findViewById(R.id.topten_love);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			Article article = mTopTen.getArticle().get(position);
			
			User user = article.getUser();
			if (user != null) {
				holder.user.setText(user.getId());
				if (user.getGender() != null && user.getGender().equals("f")) {
					holder.user.setTextColor(getResources().getColor(R.color.pink));
				} else {
					holder.user.setTextColor(getResources().getColor(R.color.blue));
				}
			}
			
		    holder.article.setText(article.getTitle()+" ");
				
		//	holder.love.setText(article.getBoard_name());
			holder.reply.setText("\t" + article.getReply_count());
			Drawable drawable = getResources().getDrawable(R.drawable.article_reply);
			drawable.setBounds(0, 0, DataUtils.getDisplayValue(12), DataUtils.getDisplayValue(12));
			holder.reply.setCompoundDrawables(drawable,null,null, null);
			
			holder.love.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (v.getTag().equals("off")) {   //dakai
						((ImageView)v).setImageResource(android.R.drawable.star_on);
						v.setTag("on");
					} else {
						((ImageView)v).setImageResource(android.R.drawable.star_off);
						v.setTag("off");
					}
				}
			});
			return convertView;
		}
		
		@Override
		public long getItemId(int position) {
			
			return position;
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mTopTen.getArticle().get(position);
		}
		
		@Override
		public int getCount() {
			if (mTopTen == null) return 0;
			return mTopTen.getArticle().size();
		}

		@Override
		public int getItemViewType(int position) {
			//返回当前位置View的类型
			return super.getItemViewType(position);
		}

		@Override
		public int getViewTypeCount() {
			// 获取View类型总数
			return super.getViewTypeCount();
		}
		
		
	};
	
	class ViewHolder {
		TextView article;
		TextView user;
		ImageView love;
		TextView reply;
	}
	
	static class HomeHandler extends Handler {
		
		WeakReference<HomeFragment> reference;
		
		public HomeHandler(HomeFragment fragment) {
			this.reference = new WeakReference<HomeFragment>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			
			if (reference != null) {
				
				HomeFragment fragment = reference.get();
				switch (msg.what) {
				//更新listview
				case 0:
					fragment.view.setVisibility(View.GONE);
					fragment.mTopTen = (TopTen) msg.obj;
					fragment.mListView.onRefreshComplete();
					fragment.mListView.setClickable(true);
					fragment.mAdapter.notifyDataSetChanged();
//					fragment.mSwipeRefreshLayout.setRefreshing(false);
					break;
					
				case 1:				//更新textview
				//	fragment.mTextView.setText((String)msg.obj);
//					fragment.mSwipeRefreshLayout.setRefreshing(false);
					
					//ViewUtils.updateView(fragment.getActivity(), (String) msg.obj, 0, fragment.progress);
				case 2:
//					fragment.mSwipeRefreshLayout.setRefreshing(false);
				//	ViewUtils.updateView(fragment.getActivity(), fragment.getActivity().getResources().getString(R.string.network_error), 1, fragment.progress);
					ViewUtils.changeToError(fragment.view, fragment.getActivity());
					break;
				default:
					break;
				}
			}
		}	
	}

//	@Override
//	public void onRefresh() {
//			getTopten(true);
//	}
}

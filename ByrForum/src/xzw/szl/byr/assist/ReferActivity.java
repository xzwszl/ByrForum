package xzw.szl.byr.assist;

import java.lang.ref.SoftReference;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import xzw.szl.byr.BaseActivity;
import xzw.szl.byr.R;
import xzw.szl.byr.db.DBListTableHandler;
import xzw.szl.byr.info.ByrRefer;
import xzw.szl.byr.info.Pagination;
import xzw.szl.byr.info.Refer;
import xzw.szl.byr.info.User;
import xzw.szl.byr.mananger.ByrThreadPool;
import xzw.szl.byr.mananger.ImageCacheManager;
import xzw.szl.byr.swipe.SwipeAdapter;
import xzw.szl.byr.utils.ByrBase;
import xzw.szl.byr.utils.DataUtils;
import xzw.szl.byr.utils.HttpUtils;
import xzw.szl.byr.utils.JsonUtils;
import xzw.szl.byr.utils.ViewUtils;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ReferActivity extends BaseActivity implements OnRefreshListener{
	
	private ByrRefer mByrRefer;

	private ListView mListView;
	private TextView bottomTextView;
	private SwipeRefreshLayout layout;
	private Handler mHandler;
	private ProgressDialog progressDialog;
	
	private int page;
	private String type;  //at | reply
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		ViewUtils.switchTheme(this);
		setContentView(R.layout.activity_mail);
		
		init();
	}
	
	private void init() {
		
		page = 0;
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		mHandler = new ReferHandler(this);
		layout = (SwipeRefreshLayout) findViewById(R.id.layout_container);
		layout.setOnRefreshListener(this);
		layout.setColorSchemeColors(getResources().getColor(R.color.section_pos1)
				,getResources().getColor(R.color.section_pos4)
				,getResources().getColor(R.color.section_pos1)
				,getResources().getColor(R.color.section_pos4));
		
		bottomTextView = (TextView) bottomView();
		mListView = (ListView) findViewById(R.id.listview_mails);
		mListView.addFooterView(bottomTextView);
		
		mListView.setAdapter(swipeAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(ReferActivity.this,ReferDetailActivity.class);
				if (mByrRefer != null && mByrRefer.getArticle() != null) {
					Refer refer = mByrRefer.getArticle().get(position);
					
					if (!refer.isIs_read()) {
						markRefer(refer.getIndex(), position, true);
						refer.setIs_read(true);
//						
//						TextView title = (TextView) ((ViewGroup)((ViewGroup)((ViewGroup)view).getChildAt(1)).getChildAt(1)).getChildAt(0);
//					    
//						if (ViewUtils.isDayTheme(ReferActivity.this)) {
//							title.setTextColor(getResources().getColor(R.color.daymainFontColor));
//						} else {
//							title.setTextColor(getResources().getColor(R.color.nightmainFontColor));
//						}
					} else {
						intent.putExtra("index",refer.getIndex());
						intent.putExtra("board",refer.getBoard_name());
						intent.putExtra("id", refer.getId());
						intent.putExtra("type", type);
						startActivityForResult(intent,1);
					}
//					swipeAdapter.notifyDataSetChanged();
					
				
				}

			}
		});
		type = getIntent().getStringExtra("type");
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		
		if (type.equals("at")) {
			setTitle("@我的文章");
			nm.cancel(1);
		} else {
			setTitle("回复我的文章");
			nm.cancel(2);
		}
		getRefer(1,false);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
	//	getMenuInflater().inflate(R.menu.mail, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
//			case android.R.id.home:
//				Intent upIntent = NavUtils.getParentActivityIntent(this);
//				if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
//					TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
//				} else {
//					upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					NavUtils.navigateUpTo(this, upIntent);
//				}
//				finish();
//				return true;
			default :break;
		}
		return super.onOptionsItemSelected(item);
	}
	
//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//	}
//	
	public View bottomView () {
			
			TextView tv = new TextView(this);
			tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			tv.setGravity(Gravity.CENTER);
			tv.setPadding(0, DataUtils.getDisplayValue(10), 0, DataUtils.getDisplayValue(10));
			tv.setTextColor(getResources().getColor(R.color.light_grey));
			tv.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (!((TextView) v).getText().toString().equals("当前到达最后一页哦~~")) {
						getRefer(page+1,false);
					}
				}
			});
			return tv;
			
		}
	
	private void getReferDetail(final int page,boolean isFromNetwork) {
		
		String content = isFromNetwork? null : DBListTableHandler.getInstance().
				queryItemContent(type, page);
		
		if (content != null) {
			ByrRefer refer = (ByrRefer)JsonUtils.toBean(content, ByrRefer.class);
			if (refer != null) {
				mHandler.obtainMessage(0,refer).sendToTarget();
			}
		} else {
			HttpUtils.httpRequest("/refer/" + type + "/.json?page=" + page, new HttpUtils.HttpRequestListener() {
				
				@Override
				public void onSuccess(String content) {
					
					ByrRefer refer = (ByrRefer)JsonUtils.toBean(content, ByrRefer.class);
					if (refer != null) {
						mHandler.obtainMessage(0,refer).sendToTarget();
						DBListTableHandler.getInstance().updateTypeListTable(
								type, page, content);
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
	private void getRefer(final int page,final boolean isFromNetwork) {
		bottomTextView.setText("努力加载中哦哦~~");
		Runnable referRunnable = new Runnable() {
			
			@Override
			public void run() {
				getReferDetail(page,isFromNetwork);
			}
		};
		ByrThreadPool.getTHreadPool().execute(referRunnable);
	}
	
	private void deleteRefer(final int index,final int position) {
		ByrThreadPool.getTHreadPool().execute(new Runnable() {
			
			@Override
			public void run() {
				HttpUtils.deleteRefer(type, index, new HttpUtils.HttpRequestListener() {
					
					@Override
					public void onSuccess(String content) {
						
						//delete Refer from my list
						mHandler.obtainMessage(3,position).sendToTarget();
					}
					
					@Override
					public void onFailed(String reason) {
						mHandler.obtainMessage(1,"删除失败").sendToTarget();
					}
					
					@Override
					public void onError(Throwable e) {
						mHandler.obtainMessage(4).sendToTarget();
					}
				});
			}
		});
	}
	
	private void markRefer(final int index, final int position,final boolean isSkipToDetail) {
		
		ByrThreadPool.getTHreadPool().execute(new Runnable() {
			
			public void run() {
				HttpUtils.setReferRead(type, index, new HttpUtils.HttpRequestListener() {
				
					@Override
					public void onSuccess(String content) {
						Bundle bundle = new Bundle();
						bundle.putInt("position",position);
						bundle.putBoolean("isSkipToDetail",isSkipToDetail);
						bundle.putString("content", content);
						mHandler.obtainMessage(5,bundle).sendToTarget();
						
						DBListTableHandler.getInstance().updateInfoOfOneItem(
								type,position / ByrBase.mail_count + 1,"is_read", "true", position % ByrBase.mail_count  + 1);
					}
					
					@Override
					public void onFailed(String reason) {
						mHandler.obtainMessage(1,reason).sendToTarget();
					}
					
					@Override
					public void onError(Throwable e) {
						mHandler.obtainMessage(6).sendToTarget();
					}
				});
			}});
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//点击读取
		if (resultCode == RESULT_OK) {
			
			if (requestCode == 1) {
				boolean isDeleteSuccessed = data.getBooleanExtra("isDeleteSuccessed", false);
				
				if (isDeleteSuccessed) {
					getRefer(1, true);
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private SwipeAdapter swipeAdapter = new SwipeAdapter(){

		@Override
		public int getCount() {
			
			if (mByrRefer == null) {
				return 0;
			}
			return mByrRefer.getArticle().size();
		}

		@Override
		public Object getItem(int position) {
			
			return null;
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

		@Override
		public int getSwipeLayoutResourceId(int position) {
			// TODO Auto-generated method stub
			return R.id.swipe;
		}

		@Override
		public View generateView(int position, ViewGroup parent) {
			
			View v = LayoutInflater.from(ReferActivity.this).inflate(R.layout.mail_item2, parent,false);
			
			ViewHodler holder = new ViewHodler();
			holder.face = (ImageView)v.findViewById(R.id.mail_face);
			holder.time = (TextView) v.findViewById(R.id.mail_time);
			holder.id = (TextView) v.findViewById(R.id.mail_id);
			holder.title = (TextView) v.findViewById(R.id.mail_title);
			holder.mark = (ImageView) v.findViewById(R.id.mark);
			holder.trash = (ImageView) v.findViewById(R.id.trash);
			
			v.setTag(holder);
			return v;
		}

		@Override
		public void fillValues(final int position, View convertView) {
			ViewHodler holder = (ViewHodler) convertView.getTag();
			
			final Refer refer = mByrRefer.getArticle().get(position);
			
			if (refer!= null) {
				
				holder.title.setText(refer.getTitle());
				if (refer.isIs_read()) {
					if (ViewUtils.isDayTheme) 
						holder.title.setTextColor(getResources().getColor(R.color.daymainFontColor));
					else
						holder.title.setTextColor(getResources().getColor(R.color.nightmainFontColor));
				} else {
					holder.title.setTextColor(getResources().getColor(R.color.blue));
				}
				holder.time.setText(DataUtils.getDateString(refer.getTime()));
				
				User user = refer.getUser();
				if (user != null) {
					holder.id.setText(user.getId());
					if (user.getGender() != null && user.getGender().equals("f")) {
						holder.id.setTextColor(getResources().getColor(R.color.pink));
					} else {
						holder.id.setTextColor(getResources().getColor(R.color.dark_blue));
					}
					if (user.getFace_url() != null) {
						ImageCacheManager.getInstance().startAcquireImage(user.getFace_url(),
								ImageCacheManager.getFaceImageAcquireListener(holder.face, mHandler));
					}
				}
				
				holder.mark.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						markRefer(refer.getIndex(),position,false);
					}
				});
				
				holder.trash.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						progressDialog = ProgressDialog.show(ReferActivity.this,"","删除中，请稍后···");
						deleteRefer(refer.getIndex(),position);
					}
				});
			}
			
		}
		
	};
	
	static class ViewHodler{
		
		ImageView trash;
		ImageView mark;
		ImageView face;
		TextView title;
		TextView id;
		TextView time;
	}
	
	
	static class  ReferHandler extends Handler {
		
		private SoftReference<ReferActivity> reference;
		
		public ReferHandler(ReferActivity activity) {
			reference = new SoftReference<ReferActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			ReferActivity activity = reference.get();
			
			if (activity != null) {
				switch (msg.what) {
				case 0:
					ByrRefer refer = (ByrRefer) msg.obj;
					if (activity.mByrRefer == null) {
						activity.mByrRefer = refer;
					} else {
						Pagination pagination = activity.mByrRefer.getPagination();
						pagination.setPage_current_count(refer.getPagination().getPage_current_count());
						pagination.setItem_page_count(pagination.getItem_page_count() + refer.getPagination().getItem_page_count());
						activity.mByrRefer.getArticle().addAll(refer.getArticle());
					}
					
					Pagination p = activity.mByrRefer.getPagination();
					activity.page = p.getPage_current_count();
					if (p.getPage_all_count() == 1 && p.getItem_page_count() == 0) {
						activity.bottomTextView.setText("没有数据诶╮(╯▽╰)╭");
					} else if (p.getPage_all_count() == p.getPage_current_count()) {
						activity.bottomTextView.setText("当前到达最后一页哦~~");
					} else
						activity.bottomTextView.setText("第" + p.getPage_current_count() + "/" + p.getPage_all_count() + "页  点击加载更多");
					activity.swipeAdapter.notifyDataSetChanged();                  //success
					activity.layout.setRefreshing(false);
					break;
				case 1:
					if (activity.progressDialog != null) {
						activity.progressDialog.dismiss();
					}
					ViewUtils.displayMessage(activity, (String) msg.obj);
					activity.layout.setRefreshing(false);
					break;
				case 2:
					ViewUtils.displayMessage(activity, activity.getResources().getString(R.string.network_error));
					activity.layout.setRefreshing(false);
					break;
				case 3:
					activity.progressDialog.dismiss();
					ViewUtils.displayMessage(activity,"删除成功");
					activity.mByrRefer = null;
					activity.swipeAdapter.closeItem((Integer) msg.obj);
					activity.swipeAdapter.notifyDataSetChanged();
					activity.getRefer(1, true);
					break;
				case 4:
					if (activity.progressDialog != null) activity.progressDialog.dismiss();
					ViewUtils.displayMessage(activity, activity.getResources().getString(R.string.network_error));
					break;
				case 5:
					if (activity.mByrRefer != null) {
						Bundle bundle = (Bundle) msg.obj;
						int position = bundle.getInt("position");
						boolean isSkipToDeatil = bundle.getBoolean("isSkipToDetail");
						
						activity.mByrRefer.getArticle().get(position).setIs_read(true);
						if (isSkipToDeatil) {
							Intent intent = new Intent(activity,ReferDetailActivity.class);
							intent.putExtra("content", bundle.getString("content"));
							activity.startActivityForResult(intent, 1);
						}
						
						activity.swipeAdapter.notifyDataSetChanged();
						
						
					}
					break;
				case 6:
					ViewUtils.displayMessage(activity, activity.getResources().getString(R.string.network_error));
					break;
					
				default:
					break;
				}
			}
		}
	}


	@Override
	public void onRefresh() {
		page = 0;
		mByrRefer = null;
		swipeAdapter.notifyDataSetChanged();
		getRefer(page+1,true);
	}
}


package xzw.szl.byr.assist;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionbarsherlock.view.MenuItem;

import xzw.szl.byr.BaseActivity;
import xzw.szl.byr.R;
import xzw.szl.byr.info.Vote;
import xzw.szl.byr.info.VoteDetail;
import xzw.szl.byr.mananger.ByrThreadPool;
import xzw.szl.byr.mananger.PrefernceManager;
import xzw.szl.byr.utils.DataUtils;
import xzw.szl.byr.utils.HttpUtils;
import xzw.szl.byr.utils.JsonUtils;
import xzw.szl.byr.utils.ViewUtils;
import xzw.szl.byr.view.RadioTextView;
import xzw.szl.byr.view.VoteCountTextView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author gologo
 *展示投票详情页并进行投票操作
 *button 的作用有三个
 *1------黄色   加载数据中
 *2------绿色  投票
 *3------加载失败
 *3------灰色 已投票
 */
public class VoteDetailActivity extends BaseDetailActivity{

	private ListView mListView;
	private TextView mTitleTextView;
	private TextView mEndTextView;
	private Button mVoteButton;
	private VoteDetail mVoteDetail;
	private Map<String,Boolean> mChecked;
	private int[] colors;
	private int[] buttonColors;
	private String[] buttonStrings;
	private String voteid;
	private boolean voted;
	private boolean isDay = true;
	
	private Handler mHandler;
	private ProgressDialog mProgressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		ViewUtils.switchTheme(this);
		
		setContentView(R.layout.activity_votedetail);
		
		init();
	}
	
	private void init() {
		
		isDay = PrefernceManager.getInstance().getCurrentThemeState(this) == 0? true:false;
		colors = new int[]{
				getResources().getColor(R.color.section_pos1),
				getResources().getColor(R.color.section_pos2),
				getResources().getColor(R.color.section_pos3),
				getResources().getColor(R.color.section_pos4),
				getResources().getColor(R.color.section_pos5)
				};
		
		buttonColors = new int[] {
				getResources().getColor(R.color.yellow),
				getResources().getColor(R.color.green),
				getResources().getColor(R.color.red),
				getResources().getColor(R.color.grey),
				getResources().getColor(R.color.grey)
		};
		
		
		buttonStrings = new String[] {
				"努力加载中哦哦~~",
				"投  票",
				"加载失败~点击重新加载~",
				"已 投 票",
				"投票已截止"
		};
	
		mChecked = new HashMap<String, Boolean>();
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		mProgressDialog = ProgressDialog.show(this,"",getString(R.string.loading_hard));
		
		mHandler = new VoteDetailHandler(this);
		
		mListView = (ListView) findViewById(R.id.votedetail_listView);
		mListView.setAdapter(mAdapter);
		mListView.setItemsCanFocus(true);      //子控件获取焦点二item不获取焦点
		
		mTitleTextView = (TextView) findViewById(R.id.votedetail_title);
		mEndTextView = (TextView) findViewById(R.id.votedetail_end);
		mVoteButton = (Button) findViewById(R.id.votedetail_button);
		
		mVoteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String str = ((Button)v).getText().toString();
				if (str.equals(buttonStrings[0]) || str.equals(buttonStrings[2])) {
					getVoteDetail();
				} else if(str.startsWith("投  票")){
					postVote();
				} 
			}
		});
		
		voteid = getIntent().getStringExtra("voteid");
		if (voteid != null)
			getVoteDetail();
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
		case R.id.board_refresh:
			showRefreshAnimation();
			getVoteDetail();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void getVoteDetail() {
		
		voted =false;
		updateVoteStatus(0);
		ByrThreadPool.getTHreadPool().execute(runnable);
		
	}
	
	private void postVote() {
		updateVoteStatus(0);
		ByrThreadPool.getTHreadPool().execute(new Runnable() {
			
			@Override
			public void run() {
				List<String> list = new ArrayList<String>();
				
				for (String key: mChecked.keySet()) {
					if (mChecked.get(key)) {
						list.add(key);
					}
				}
				
				if (list.size() == 0) mHandler.obtainMessage(2).sendToTarget();
				else {
					
					
					HttpUtils.postVote(voteid, list, mVoteDetail,new HttpUtils.HttpRequestListener() {
						
						@Override
						public void onSuccess(String content) {
							VoteDetail votedetail = (VoteDetail) JsonUtils.toBean(content, VoteDetail.class);
							if (votedetail != null) {
								mHandler.obtainMessage(4, votedetail).sendToTarget();
							}
						}
						
						@Override
						public void onFailed(String reason) {
							mHandler.obtainMessage(1,reason).sendToTarget();
						}
						
						@Override
						public void onError(Throwable e) {
							mHandler.obtainMessage(3).sendToTarget();
						}
					});
				}
			}
				
		});
	}
	private void getVotemoreDetail() {
		
		
		HttpUtils.httpRequest("/vote/" + voteid + ".json", new HttpUtils.HttpRequestListener() {
			
			@Override
			public void onSuccess(String content) {
				VoteDetail voteDetail = (VoteDetail) JsonUtils.toBean(content, VoteDetail.class);
				
				if (voteDetail != null) {
					mHandler.obtainMessage(0, voteDetail).sendToTarget();
				}
			}
			
			@Override
			public void onFailed(String reason) {
				mHandler.obtainMessage(1, reason).sendToTarget();
			}
			
			@Override
			public void onError(Throwable e) {
				// TODO Auto-generated method stub
				mHandler.obtainMessage(1, getString(R.string.network_error)).sendToTarget();
			}
		});
	}
	
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			
			getVotemoreDetail();
		}
	};
	
	private void updateVoteStatus(int type) {
		
		
		String str = buttonStrings[type];
		if (type == 1 && mVoteDetail != null) {
			if (mVoteDetail.getVote().isIs_result_voted()) {
				str+=" (查看结果)";
			}
		}
		
		mVoteButton.setText(str);
		mVoteButton.setBackgroundColor(buttonColors[type]);
		
		if (type == 0 || type == 3 || type == 4) {
			mVoteButton.setClickable(false);
		} else {
			mVoteButton.setClickable(true);
		}
	}
	
	private void updateViews() {
		
		if (mVoteDetail != null) {
			
			int type = 0;
			mTitleTextView.setText(mVoteDetail.getVote().getTitle());
			String end = DataUtils.getDateString(Integer.valueOf(mVoteDetail.getVote().getEnd()));
			
			String limit = mVoteDetail.getVote().getLimit();
			
			String value = null;
			if (limit.equals("0")) value = "  单选";
			else value = "  多选（至多 " + limit + "）";
			
			mEndTextView.setText("截止日期: " + end + value + "\n参与人数: " + mVoteDetail.getVote().getUser_count());
			
			if (mVoteDetail.getVote().getVoted() == null) {
				voted = false;
				type = 1;
			} else {
				
				for (String item : mVoteDetail.getVote().getVoted().getViid()) {
					mChecked.put(item, true);
				}
				type = 3;
				voted = true;
			}
			if (mVoteDetail.getVote().isIs_end()) {
				type  = 4;
			}
			updateVoteStatus(type);
			mAdapter.notifyDataSetChanged();
		}
	}
	
	private BaseAdapter mAdapter = new BaseAdapter() {
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			
			if (convertView == null) {
				
				convertView = LayoutInflater.from(VoteDetailActivity.this).inflate(R.layout.votedetail_item, parent, false);
				
				holder = new ViewHolder();
				holder.options = (TextView) convertView.findViewById(R.id.options);
				holder.count = (VoteCountTextView) convertView.findViewById(R.id.count);
				holder.raido = (RadioTextView) convertView.findViewById(R.id.radio);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			final Vote.Option options = mVoteDetail.getVote().getOptions().get(position);
			
			if (options != null) {
				
				
				holder.options.setText(options.getLabel());

				if (mChecked.get(options.getViid())) {
					
					((RadioTextView) holder.raido).setChecked(true);
					
					if (voted) {
						((RadioTextView) holder.raido).setColor(getResources().getColor(R.color.grey));
					} else {
						if (isDay) {
							((RadioTextView) holder.raido).setColor(getResources().getColor(R.color.blue));
						} else {
							((RadioTextView) holder.raido).setColor(getResources().getColor(R.color.daymainitembk));
						}
					}
					
				} else {
					((RadioTextView) holder.raido).setChecked(false);
				}
				
				holder.raido.setText("");
				
				if (mVoteDetail.getVote().isIs_end()) {
					((RadioTextView) holder.raido).setEnd(true);
					((RadioTextView) holder.raido).setColor(getResources().getColor(R.color.grey));
				}
				if (!mVoteDetail.getVote().isIs_result_voted() || voted || mVoteDetail.getVote().isIs_end()) {
					
					float round = Float.valueOf(options.getNum()) / mVoteDetail.getVote().getVote_count();
					
					((VoteCountTextView)holder.count).setRound(round);
					((VoteCountTextView)holder.count).setCount(Integer.valueOf(options.getNum()));
					((VoteCountTextView)holder.count).setColor(colors[position%5]);
				}
				
				
				holder.raido.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						if (mVoteDetail.getVote().isIs_end()) {
							return;
						}
						
						if (voted) {
							ViewUtils.displayMessage(VoteDetailActivity.this, "您已参与投票了~");
							return;
						}
							
						boolean isChecked = mChecked.get(options.getViid());
						
						if (isChecked) {
							
							if (mChecked.get(options.getViid())) {
								 mChecked.put(options.getViid(),false);
								 ((RadioTextView)v).setChecked(false);
								 v.invalidate();
							}
							
						} else {
							
							
							int limit = Integer.valueOf(mVoteDetail.getVote().getLimit());
							if (limit == 0) {           //单选
							
								for (String key: mChecked.keySet()) {
									if (mChecked.get(key)) {
										mChecked.put(key, false);
										break;
									}
								}
								mChecked.put(options.getViid(), true);
								mAdapter.notifyDataSetChanged();
							} else {
								
								int count = 0;
								for (String key: mChecked.keySet()) {
									if (mChecked.get(key)) count++;
								}
								
								if (count < limit) {
									mChecked.put(options.getViid(),true);
									((RadioTextView)v).setChecked(true);
									v.invalidate();
								} else {
									ViewUtils.displayMessage(VoteDetailActivity.this, "投票到达上限~");
								}
							}
							
						}
						
					}
				});
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
			if (mVoteDetail == null || mVoteDetail.getVote() == null)
				return 0;
			return mVoteDetail.getVote().getOptions().size();
		}
		
	};
	
	class ViewHolder {
		TextView options;
		TextView count;
		TextView raido;
	}
	
	static class VoteDetailHandler extends Handler {
		
		private WeakReference<Activity> reference;
		
		public VoteDetailHandler(Activity activity) {
			reference = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			
			VoteDetailActivity activity = (VoteDetailActivity) reference.get();
			
			if (activity != null) {
				switch (msg.what) {
				case 0:
					if (activity.mProgressDialog != null) activity.mProgressDialog.dismiss();
					activity.mVoteDetail = (VoteDetail) msg.obj;
					
					for (Vote.Option option : activity.mVoteDetail.getVote().getOptions()) {
						if (option != null)
							activity.mChecked.put(option.getViid(), false);
					}
					activity.updateViews();
					activity.clearRefreshAnimation();
					break;
				case 1:
					if (activity.mProgressDialog != null) activity.mProgressDialog.dismiss();
					activity.updateVoteStatus(2);
					ViewUtils.displayMessage(activity,(String) msg.obj);
					activity.clearRefreshAnimation();
					break;
				case 2:
					ViewUtils.displayMessage(activity, "没选择投票内容(⊙o⊙)");
					break;
				case 3:
					ViewUtils.displayMessage(activity, activity.getResources().getString(R.string.network_error));
					break;
				case 4:
					activity.mVoteDetail = (VoteDetail) msg.obj;
					List<Vote.Option> options = activity.mVoteDetail.getVote().getOptions();
					for (Vote.Option option : options) {
						
						if (activity.mChecked.get(option.getViid())) {
							option.setNum(String.valueOf(Integer.valueOf(option.getNum())+1));
						}
					}
					activity.updateViews();
					break;
				default:
					break;
				}
			}
		}
	}
}

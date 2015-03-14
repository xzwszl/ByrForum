package xzw.szl.byr.assist;

import java.lang.ref.WeakReference;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import xzw.szl.byr.BaseActivity;
import xzw.szl.byr.R;
import xzw.szl.byr.db.DBListTableHandler;
import xzw.szl.byr.info.ByrMail;
import xzw.szl.byr.info.Mail;
import xzw.szl.byr.info.Pagination;
import xzw.szl.byr.mananger.ByrThreadPool;
import xzw.szl.byr.mananger.ImageCacheManager;
import xzw.szl.byr.mananger.ImageCacheManager.ImageAcquireListener;
import xzw.szl.byr.swipe.SwipeAdapter;
import xzw.szl.byr.swipe.SwipeLayout;
import xzw.szl.byr.utils.ByrBase;
import xzw.szl.byr.utils.DataUtils;
import xzw.szl.byr.utils.HttpUtils;
import xzw.szl.byr.utils.JsonUtils;
import xzw.szl.byr.utils.ViewUtils;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MailActivity extends BaseActivity implements OnRefreshListener{
	
	private ListView mListView;
	private TextView bottomeTextView;              //显示提示类信息和点击加载更多
	private SwipeRefreshLayout layout;
	private ProgressDialog progressDialog;
	private ByrMail[] mByrMail;
	
	private final int INBOX = 0;
	private final int OUTBOX = 1;
	private final int DELETED = 2;
	private String[] mBox;
 	private Handler mHandler;

    private int mCurrentBox;
    private int mCurrentPosition = -1;
    
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		ViewUtils.switchTheme(this);
		setContentView(R.layout.activity_mail);
		
		init();
	}
	
	
	private void init() {
		initMail();
		
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(0);
		
		setTitle("收件箱");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		layout = (SwipeRefreshLayout) findViewById(R.id.layout_container);
		layout.setOnRefreshListener(this);
		layout.setColorSchemeColors(getResources().getColor(R.color.section_pos1)
				,getResources().getColor(R.color.section_pos4)
				,getResources().getColor(R.color.section_pos1)
				,getResources().getColor(R.color.section_pos4));
		
		bottomeTextView = (TextView) bottomView();
		mListView = (ListView) findViewById(R.id.listview_mails);
		mListView.addFooterView(bottomeTextView);
		
		mListView.setAdapter(swipeAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				mCurrentPosition = position;
				if (mByrMail[mCurrentBox] != null && mByrMail[mCurrentBox].getMail() != null && mByrMail[mCurrentBox].getMail().get(position) != null) {
					
					if (!mByrMail[mCurrentBox].getMail().get(position).isIs_read()) {
//						mByrMail[mCurrentBox].getMail().get(position).setIs_read(true);
						
						TextView title = (TextView) ((ViewGroup)((ViewGroup)((ViewGroup)view).getChildAt(1)).getChildAt(1)).getChildAt(0);
						
						if (ViewUtils.isDayTheme(MailActivity.this)) {
							title.setTextColor(getResources().getColor(R.color.daymainFontColor));
						} else {
							title.setTextColor(getResources().getColor(R.color.nightmainFontColor));
						}
					}
					
					Intent intent = new Intent(MailActivity.this,MailDetailActivity.class);
					intent.putExtra("index",mByrMail[mCurrentBox].getMail().get(position).getIndex());
					intent.putExtra("box", mBox[mCurrentBox]);
					startActivityForResult(intent,1);

				}
			}
		});
		getMail(INBOX,1,false);
	}
	
	
	private void initMail() {
		mByrMail = new ByrMail[3];
		mBox = new String[]{"inbox","outbox","deleted"};
		mCurrentBox = INBOX;
		mHandler = new MailHandler(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getSupportMenuInflater().inflate(R.menu.mail, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case R.id.mail_in:
				mCurrentBox = INBOX;
				getMail(INBOX,1,false);
				setTitle("收件箱");
				return true;
			case R.id.mail_out:
				mCurrentBox = OUTBOX;
				getMail(OUTBOX, 1,false);
				setTitle("发件箱");
				return true;
			case R.id.mail_delete:
				mCurrentBox = DELETED;
				getMail(DELETED, 1,false);
				setTitle("垃圾箱");
				return true;
			case R.id.mail_post:
				Intent intent = new Intent(this, PostMailActivity.class);
				startActivity(intent);
				return true;
			case android.R.id.home:
//				Intent upIntent = NavUtils.getParentActivityIntent(this);
//				if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
//					TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
//				} else {
//					upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					NavUtils.navigateUpTo(this, upIntent);
//				}
//				request("/mail/info.json","mail");
//				finish();
//				return true;
			default :break;
		}
		return super.onOptionsItemSelected(item);
	}
	
//	@Override
//	public void onBackPressed() {
////		request("/mail/info.json","mail");
//		super.onBackPressed();
//	}
//		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 1) {
				int index = data.getIntExtra("index",-1);
				final boolean isReplySuccessed = data.getBooleanExtra("isReplySuccessed", false);
				boolean isDeleteSuccessed = data.getBooleanExtra("isDeleteSuccessed", false);
				final boolean isReadSuccessed = data.getBooleanExtra("isReadSuccessed", false);
				if (isDeleteSuccessed) {
					getMailDetail(mCurrentBox, 1, ByrBase.mail_count, true);
				} else if ((isReplySuccessed ||  isReadSuccessed)&& mByrMail[mCurrentBox] != null) {
					int size = mByrMail[mCurrentBox].getMail().size();
					for (int i = 0; i < size; i++) {
						Mail mail = mByrMail[mCurrentBox].getMail().get(i);
						if (index == mail.getIndex()) {
							
							final boolean hasReaded = mail.isIs_read();
							final boolean hasReplied = mail.isIs_reply();
							
							if (!hasReaded && isReadSuccessed) mail.setIs_read(isReadSuccessed);
							if (!hasReplied && isReplySuccessed) mail.setIs_reply(isReplySuccessed);
							
							swipeAdapter.notifyDataSetChanged();
							
							ByrThreadPool.getTHreadPool().execute(new Runnable() {
								
								@Override
								public void run() {
									if (!hasReplied && isReplySuccessed)
										DBListTableHandler.getInstance().updateInfoOfOneItem(mBox[mCurrentBox], 
											mCurrentPosition / ByrBase.mail_count + 1, "is_reply", "true", mCurrentPosition % ByrBase.mail_count + 1);
									
									if (!hasReaded && isReadSuccessed) {
										DBListTableHandler.getInstance().updateInfoOfOneItem(mBox[mCurrentBox], 
												mCurrentPosition / ByrBase.mail_count + 1, "is_read", "true", mCurrentPosition % ByrBase.mail_count + 1);
									}
								}
							});
							break;
						}
					}
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private ImageAcquireListener getFaceImageAcquireListener (final ImageView imageView) {
		
		return new ImageAcquireListener() {
			
			@Override
			public void onSuccess(final Bitmap bitmap) {
				
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						imageView.setImageBitmap(bitmap);
					}
				});
			}
			
			@Override
			public void onFailure() {

			}
		};
	}
	
	private void getMail(int index, int page,boolean isFromNetwork) {
		
	//	mListView.setEmptyView(ViewUtils.getProgressBar(this));
		swipeAdapter.notifyDataSetChanged();
		// 如果对应邮件箱为空，或者有新的页数，或者页数大于当前页数
		if (mByrMail[mCurrentBox] == null || mByrMail[mCurrentBox].getPagination().getPage_current_count() < page) {
			
			bottomeTextView.setText("努力加载中哦哦~~");
			ByrThreadPool.getTHreadPool().execute(new MailRunnable(page,isFromNetwork));
		} else {
			mHandler.obtainMessage(7,mCurrentBox).sendToTarget();
			mListView.setSelection(0);
		}
	}
	
 class  MyMail{
		ByrMail mail;
		int index;
		int page;
	}
	
	private synchronized void getMailDetail(final int index,final int page,final int count,boolean isFromNetwork) {
		
		String content = isFromNetwork == true ? null : DBListTableHandler.getInstance().queryItemContent(mBox[index], page);
		
		if (content != null) {
			ByrMail mail = (ByrMail) JsonUtils.toBean(content,ByrMail.class);
			
			if (mail != null) {
				
				MyMail mM = new MyMail();
				mM.mail = mail;
				mM.index = index;
				mM.page = page;
				
				mHandler.obtainMessage(0,mM).sendToTarget();
			}
		} else {

		HttpUtils.httpRequest("/mail/" + mBox[index] + ".json?page=" + page + "&count=" + count, new HttpUtils.HttpRequestListener() {
			
			@Override
			public void onSuccess(String content) {
				ByrMail mail = (ByrMail) JsonUtils.toBean(content,ByrMail.class);
				
				if (mail != null) {
					
					MyMail mM = new MyMail();
					mM.mail = mail;
					mM.index = index;
					mM.page = page;
					
					mHandler.obtainMessage(0,mM).sendToTarget();
					DBListTableHandler.getInstance().updateTypeListTable(mBox[index], page, content);
				}
			}
			
			@Override
			public void onFailed(String reason) {
				   mHandler.obtainMessage(1,reason).sendToTarget();
			}
			
			@Override
			public void onError(Throwable e) {
				mHandler.obtainMessage(5).sendToTarget();
			}
		});
	  }
	}
	
	private void deleteMail(final int index, final int num,final int position) {
		
		Runnable delete = new Runnable() {
			
			@Override
			public void run() {

				HttpUtils.deleteMail(mBox[index], num, new HttpUtils.HttpRequestListener() {
					
					@Override
					public void onSuccess(String content) {
						
//						if (mByrMail[index] != null && mByrMail[index].getMail() != null) {
//							
//							if (index != DELETED) {
//								mByrMail[DELETED]= null;
//							}
//							
//							List<Mail> mails = mByrMail[index].getMail();
//							mails.remove(position);
//							for (int i=0;i<position;i++) {
//								mails.get(i).setIndex(mails.get(i).getIndex()-1);
//							}
//						}
						Bundle bundle = new Bundle();
						bundle.putInt("position", position);
						bundle.putInt("index", index);
						mHandler.obtainMessage(2,bundle).sendToTarget();
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
			}
		};
		ByrThreadPool.getTHreadPool().execute(delete);
	}
	
	private void readMailDetail(final int index, int num, final int position) {
		HttpUtils.httpRequest("/mail/" + mBox[index] + "/" + num +  ".json", new HttpUtils.HttpRequestListener() {
			
			@Override
			public void onSuccess(String content) {
				
				if (mByrMail[index] != null && mByrMail[index].getMail() != null) {
					Mail mail = mByrMail[index].getMail().get(position);
					mail.setIs_read(true);
					Bundle bundle = new Bundle();
					bundle.putInt("position", position);
					bundle.putInt("index",index);
					mHandler.obtainMessage(4,bundle).sendToTarget();
				}
				
			}
			
			@Override
			public void onFailed(String reason) {
				mHandler.obtainMessage(3,reason).sendToTarget();
			}
			
			@Override
			public void onError(Throwable e) {
				mHandler.obtainMessage(6).sendToTarget();
			}
		});
	}
	private void readMail(final int index, final int num,final int position) {
		
		Runnable read = new Runnable() {
			
			@Override
			public void run() {
				readMailDetail(index, num, position);
			}
		};
		ByrThreadPool.getTHreadPool().execute(read);
	}
	
	private void updateDB(final int position,final int index) {
		
		
		Runnable update = new Runnable() {
			
			@Override
			public void run() {
				int page = position / ByrBase.mail_count + 1;
				DBListTableHandler.getInstance().updateInfoOfOneItem(mBox[index], page, "is_read", "true",  position % ByrBase.mail_count +1);
			}
		};
		ByrThreadPool.getTHreadPool().execute(update);
	}
	
	public class MailRunnable implements Runnable{
		
		private int page;
		private boolean isFromNetwork;
		
		public MailRunnable(int page,boolean isFromNetwork) {
			this.page = page;
			this.isFromNetwork = isFromNetwork;
		}

		@Override
		public void run() {
			getMailDetail(mCurrentBox,page,ByrBase.mail_count,isFromNetwork);
		}	
	}

	private SwipeAdapter swipeAdapter = new SwipeAdapter(){

		@Override
		public int getCount() {
			if (mByrMail[mCurrentBox] == null) 
				return 0;
			else
				return mByrMail[mCurrentBox].getMail().size();
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
			return R.id.swipe;
		}

		@Override
		public View generateView(int position, ViewGroup parent) {
			
			SwipeLayout view = (SwipeLayout) LayoutInflater.from(MailActivity.this).inflate(R.layout.mail_item2,parent,false);
			view.setDragEdge(SwipeLayout.DragEdge.Right);
			
			ViewHolder holder = new ViewHolder();
			holder.face = (ImageView) view.findViewById(R.id.mail_face);
			holder.name = (TextView) view.findViewById(R.id.mail_id);
			holder.title = (TextView) view.findViewById(R.id.mail_title);
			holder.reply = (TextView) view.findViewById(R.id.mail_reply);
			holder.time = (TextView) view.findViewById(R.id.mail_time);
			holder.trash = (ImageView) view.findViewById(R.id.trash);
			holder.mark = (ImageView) view.findViewById(R.id.mark);
			view.setTag(holder);
			return view;
		}

		@Override
		public void fillValues(final int position, View convertView) {
			
			ViewHolder holder = (ViewHolder) convertView.getTag();
			
			Mail mail = mByrMail[mCurrentBox].getMail().get(position);
				
		    holder.face.setImageResource(R.drawable.face_default_f);
			
			if (mail.getUser() != null) {
				holder.name.setText(mail.getUser().getId());
				if (mail.getUser().getGender() != null && mail.getUser().getGender().equals("f")) {
					holder.name.setTextColor(getResources().getColor(R.color.pink));
				} else {
					holder.name.setTextColor(getResources().getColor(R.color.dark_blue));
				}
				ImageCacheManager.INSTANCE.startAcquireImage(mail.getUser().getFace_url(), getFaceImageAcquireListener(holder.face));
				
			} else {
				holder.name.setText("未知");
			}
			holder.title.setText(mail.getTitle());
			
			if (!mail.isIs_read()) {
				holder.title.setTextColor(getResources().getColor(R.color.blue));
				//holder.title.setTextColor(getResources().getColor(R.color.grey));
			} else {
				if (ViewUtils.isDayTheme) 
					holder.title.setTextColor(getResources().getColor(R.color.daymainFontColor));
				else
					holder.title.setTextColor(getResources().getColor(R.color.nightmainFontColor));
			}
			holder.reply.setVisibility(View.VISIBLE);
			if (mail.isIs_reply()) {
				holder.reply.setText("已回复");
				holder.reply.setBackgroundColor(getResources().getColor(R.color.grey));
			} else {
				holder.reply.setText("未回复");
				holder.reply.setBackgroundColor(getResources().getColor(R.color.green));
			}
	
			holder.time.setText(DataUtils.getDateString(mail.getPost_time()));
			
			holder.trash.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					progressDialog = ProgressDialog.show(MailActivity.this,"", "删除中，请稍后···");
					deleteMail(mCurrentBox,mByrMail[mCurrentBox].getMail().get(position).getIndex(),position);
				}
			});
			
			if (mCurrentBox == OUTBOX) ((View)holder.mark.getParent()).setVisibility(View.GONE);
			else {
				((View)holder.mark.getParent()).setVisibility(View.VISIBLE);
			}
			holder.mark.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					readMail(mCurrentBox,mByrMail[mCurrentBox].getMail().get(position).getIndex(),position);
				}
			});
		}
	};
	
	public class ViewHolder{
		ImageView face;
		TextView title;
		TextView name;
		TextView reply;
		TextView time;
		ImageView trash;
		ImageView mark;
	}
	
	
	public View bottomView () {
		
		TextView tv = new TextView(this);
		tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		tv.setTextColor(getResources().getColor(R.color.light_grey));
		tv.setGravity(Gravity.CENTER);
		tv.setPadding(0, DataUtils.getDisplayValue(10), 0, DataUtils.getDisplayValue(10));
		tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (((TextView)v).getText().toString().startsWith("第")) {
					
					if (mByrMail[mCurrentBox] != null && mByrMail[mCurrentBox].getPagination() != null) {
						
						int page = mByrMail[mCurrentBox].getPagination().getPage_current_count() + 1;
						getMail(mCurrentBox, page,false);
					}
					
				} else if(((TextView)v).getText().toString().startsWith("数据加载失败")) {
					int  page = 1;
					if (mByrMail[mCurrentBox] != null && mByrMail[mCurrentBox].getPagination() != null) {
						page =  mByrMail[mCurrentBox].getPagination().getPage_current_count();
					}
					getMail(mCurrentBox, page,false);
				}
			}
		});
		
		return tv;
		
	}
	
	public static class MailHandler extends Handler {
		
		private WeakReference<MailActivity> mWReference;
		
		public MailHandler(MailActivity activity) {
			mWReference = new WeakReference<MailActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			
			MailActivity activity = mWReference.get();
			if (activity != null) {
				switch (msg.what) {
				case 0:
					MyMail mail = (MyMail) msg.obj;
					//activity.mByrMail[mail.index] = mail.mail;
					Pagination pagination = null;
					if (activity.mByrMail[mail.index] == null || mail.page == 1) {
						activity.mByrMail[mail.index] = mail.mail;
						pagination = activity.mByrMail[mail.index].getPagination();
					} else{
						pagination = activity.mByrMail[mail.index].getPagination();
						pagination.setItem_page_count(mail.mail.getPagination().getItem_page_count()+pagination.getItem_page_count());
						pagination.setPage_current_count(mail.mail.getPagination().getPage_current_count());
						activity.mByrMail[mail.index].getMail().addAll(mail.mail.getMail());
					}

					if (pagination != null) {
						if (pagination.getPage_all_count() == 1 && pagination.getItem_page_count() == 0) {
							activity.bottomeTextView.setText("您的" + activity.mBox[activity.mCurrentBox] + "为空╮(╯▽╰)╭");
						} else if (pagination.getPage_all_count() == pagination.getPage_current_count()) {
							activity.bottomeTextView.setText("当前到达最后一页哦~~");
						} else
						activity.bottomeTextView.setText("第" + pagination.getPage_current_count() + "/" + pagination.getPage_all_count() + "页  点击加载更多");
						activity.swipeAdapter.notifyDataSetChanged();                  //success
						activity.layout.setRefreshing(false);
					}
					break;
				case 7:
					int index = (Integer) msg.obj;
					pagination = activity.mByrMail[index].getPagination();
					if (pagination != null) {
						if (pagination.getPage_all_count() == 1 && pagination.getItem_page_count() == 0) {
							activity.bottomeTextView.setText("您的" + activity.mBox[index] + "为空╮(╯▽╰)╭");
						} else if (pagination.getPage_all_count() == pagination.getPage_current_count()) {
							activity.bottomeTextView.setText("当前到达最后一页哦~~");
						} else
						activity.bottomeTextView.setText("第" + pagination.getPage_current_count() + "/" + pagination.getPage_all_count() + "页  点击加载更多");
						activity.swipeAdapter.notifyDataSetChanged();                  //success
						activity.layout.setRefreshing(false);
					}
					break;
				
				case 1:
					if (activity.progressDialog != null) {
						ViewUtils.displayMessage(activity,"删除失败!!");
						activity.progressDialog.dismiss();
					}
					activity.bottomeTextView.setText((String)msg.obj);
					activity.layout.setRefreshing(false);
					break;
					// delete
				case 2:
					activity.progressDialog.dismiss();
					Bundle bundle = (Bundle) msg.obj;
					index = bundle.getInt("index");
					int position = bundle.getInt("position");
					activity.swipeAdapter.closeItem(position);
					ViewUtils.displayMessage(activity,"删除成功~");
					activity.mByrMail[index] = null;
					activity.swipeAdapter.notifyDataSetChanged();
					
					activity.getMail(index,1,true);
					break;
				case 3:
					ViewUtils.displayMessage(activity,(String) msg.obj);
					break;
				case 4:
					bundle = (Bundle) msg.obj;
					if (bundle.getInt("index") == activity.mCurrentBox) {
						activity.swipeAdapter.closeItem(bundle.getInt("position"));
						activity.swipeAdapter.notifyDataSetChanged();
					}
					ViewUtils.displayMessage(activity,"已读");
					activity.updateDB(bundle.getInt("position"),bundle.getInt("index"));
					break;
				case 5:                                             // wangluo gu zhang
					activity.bottomeTextView.setText(activity.getResources().getString(R.string.network_error));
					break;
				case 6:
					if (activity.progressDialog != null) {
						activity.progressDialog.dismiss();
					}
					ViewUtils.displayMessage(activity, activity.getResources().getString(R.string.network_error));
				default:
					break;
				}
			}
		}
	}

	@Override
	public void onRefresh() {
		mByrMail[mCurrentBox] = null;
		swipeAdapter.notifyDataSetChanged();
		getMail(mCurrentBox , 1,true);
	}
}

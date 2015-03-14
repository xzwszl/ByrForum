package xzw.szl.byr.dialog;

import xzw.szl.byr.R;
import xzw.szl.byr.assist.PostMailActivity;
import xzw.szl.byr.info.User;
import xzw.szl.byr.mananger.ImageCacheManager;
import xzw.szl.byr.utils.DataUtils;
import xzw.szl.byr.utils.ViewUtils;
import xzw.szl.byr.view.CircleImageView;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.ContactsContract.Contacts.Data;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class UserInfoDialog extends Dialog {

	private Context mContext;
	private ListView mListView;
	private TextView mTextViewForId;
	private ImageView mSendMail;
	private ImageView mAddFriend;
	private ImageView mFace;
	private TextView mBack;
	private Handler mHandler;
	
	private static String[] keys;
	public UserInfoDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.user_info);
	}
	
	public UserInfoDialog(Context context,final User user,Handler handler) {
		this(context);
		mContext = context;
		this.mHandler = handler;
		//判断权限，自己或者管理员才能看到某些项
		keys = new String[]{
					"基本信息",null,
					"昵 称：",user.getUser_name(),
					"性 别：", user.getGender() != null && user.getGender().equals("f")?"女":"男",
					"星 座：",user.getAstro(),
					"QQ：",user.getQq(),
					"MSN：",user.getMsn(),
					"主 页：",user.getHome_page(),
					"论坛属性",null,
					"论坛等级：",user.getLevel(),
					"用户帖子总数：",user.getPost_count()+"",
					"积分：",null,
					"用户身份：",user.getLevel(),
					"登陆次数：",user.getLogin_count()+"",
					"生命力：",user.getLife()+"",
					"注册时间：",DataUtils.getDateString(user.getFirst_login_time()) + "",
					"上次登录：",DataUtils.getDateString(user.getLast_login_time()) + "",
					"最后访问IP：",user.getLast_login_ip(),
					"当前状态：",user.isIs_online()==true?"在线":"离线"};
		
		mListView = (ListView) findViewById(R.id.listView_userinfo);
		mListView.setAdapter(adapter);
		
		mTextViewForId = (TextView) findViewById(R.id.textView_id);
		mTextViewForId.setText(user.getId());
		if (user.getGender() != null && user.getGender().equals("f")) {
			mTextViewForId.setTextColor(mContext.getResources().getColor(R.color.pink));
		} else {
			mTextViewForId.setTextColor(mContext.getResources().getColor(R.color.blue));
		}
		mSendMail = (ImageView) findViewById(R.id.imageView_sayhello);
		
		mSendMail.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext,PostMailActivity.class);
				intent.putExtra("reciver", user.getId());
				mContext.startActivity(intent);
			}
		});
		mAddFriend = (ImageView) findViewById(R.id.imageView_addfriend);
		mAddFriend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mBack = (TextView) findViewById(R.id.back);
		mBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UserInfoDialog.this.dismiss();
			}
		});
		
		mFace = (CircleImageView) findViewById(R.id.face);
		ImageCacheManager.getInstance().startAcquireImage(user.getFace_url(), ImageCacheManager.getFaceImageAcquireListener(mFace, mHandler));
	}
	
	
	private ListAdapter adapter = new BaseAdapter() {

		@Override
		public int getCount() {
			return keys.length/2;
		}

		@Override
		public Object getItem(int position) {
			return keys[position*2];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		

		@Override
		public int getItemViewType(int position) {

			if (keys[position*2].equals("基本信息") || keys[position*2].equals("论坛属性")) {
				return 0;
			} else return 1;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHodler holder = null;
			int type = getItemViewType(position);
			if (type == 0) {
//				TextView tv = null;
				if (convertView == null) {
//					convertView.setOrientation(LinearLayout.VERTICAL);
					convertView = new TextView(mContext);
					LayoutParams params = new LayoutParams(
							LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
					convertView.setLayoutParams(params);
//					((LinearLayout) convertView).addView(tv,params);
//					convertView.setTag(tv);
//					convertView.setLayoutParams(params);
				} 
				((TextView)convertView).setText(keys[position * 2]);
				((TextView)convertView).setTextColor(mContext.getResources().
						getColor(R.color.user_dialog_item));
			} else {
			//	if (keys[position *2 +1] == null)  return null;
				if (convertView == null) {
					convertView = LayoutInflater.from(mContext).
							inflate(R.layout.user_info_item, parent, false);
					
					holder = new ViewHodler();
					holder.key = (TextView) convertView.findViewById(R.id.textView_key);
					holder.value = (TextView) convertView.findViewById(R.id.textView_value);
					
					convertView.setTag(holder);
				} else {
					holder = (ViewHodler) convertView.getTag();
				}
				
				holder.key.setText(keys[position *2]);
				holder.value.setText(keys[position *2 + 1]);
			}
			return convertView;
		}
		
	};
	
	class ViewHodler {
		TextView key;
		TextView value;
	}
	
	
}

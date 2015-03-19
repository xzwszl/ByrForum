package xzw.szl.byr.fragment;

import xzw.szl.byr.BoardActivity;
import xzw.szl.byr.HomeActivity;
import xzw.szl.byr.HomeActivity.FavorListener;
import xzw.szl.byr.R;
import xzw.szl.byr.assist.MailActivity;
import xzw.szl.byr.assist.PrsActivity;
import xzw.szl.byr.assist.ReferActivity;
import xzw.szl.byr.assist.VoteActivity;
import xzw.szl.byr.info.MyFavorite;
import xzw.szl.byr.mananger.PrefernceManager;
import xzw.szl.byr.swipe.SwipeAdapter;
import xzw.szl.byr.swipe.SwipeLayout;
import xzw.szl.byr.utils.DataUtils;
import xzw.szl.byr.utils.ViewUtils;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.readystatesoftware.viewbadger.BadgeView;

public class OptionsFragment extends Fragment{

	private GridView mGridView;
	private GridView mGrViewFavor;
	private ScrollView mScrollView;
	private String[] mLabels;
	private int[]  mItems;
//	private String[] mCount;
	private HomeActivity activity;
//	private String[] mFavors;
	private MyFavorite myFavorite;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewUtils.switchTheme(getActivity());
		return inflater.inflate(R.layout.frament_options, container,false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		((HomeActivity)getActivity()).setOptionsFragment(this);
		initView();
	}
	
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
//		mScrollView.fullScroll(ScrollView.FOCUS_UP);
		super.onResume();
		mScrollView.post(new Runnable() {
			
			@Override
			public void run() {
				
				mScrollView.scrollTo(0,0);
			}
		});
	}

	private void init () {
		HomeActivity homeActivity = (HomeActivity) getActivity().getLastCustomNonConfigurationInstance();
		if (homeActivity != null) {
//			mCount = ((OptionsFragment)homeActivity.getFragment(0)).mCount;
		} else {
			//mCount = new String[]{"","",""};
		}
		mLabels = new String[]{
				"收件箱",
				"@我的文章",
				"回复我的文章",
				"投票",
				PrefernceManager.getInstance().getCurrentThemeState(getActivity())==0?"日间模式":"夜间模式",
				"设置",
		};
		
		
		mItems = new int[]{
				R.drawable.mail_middle,
				R.drawable.at_me_middle,
				R.drawable.reply_to_me_middle,
				R.drawable.voted_middle,
				R.drawable.day_middle,
				R.drawable.setting_middle,};
		
		
		
//		mIsCounted = new boolean[]{false,false,false};
		
//		
//		mFavors = new String[]{
//				"收件箱",
//				"@我的文章",
//				"回复我的文章",
//				"退出登录"
//		};
	}
	
	private void initView() {
		activity = (HomeActivity) getActivity();
		
		mScrollView = (ScrollView) getActivity().findViewById(R.id.scrollview);

		mGridView = (GridView) getActivity().findViewById(R.id.gridView_options);
		mGridView.setAdapter(mAdapter);
		
		mGrViewFavor = (GridView) getActivity().findViewById(R.id.gridView_favor);
		mGrViewFavor.setFocusable(false);
		mGrViewFavor.setAdapter(swipeAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				Toast.makeText(getActivity(), mFavors[position], Toast.LENGTH_SHORT).show();
				
				switch (position) {
				case 0:
					startActivity(new Intent(getActivity(),MailActivity.class));
					break;
				case 1:
					Intent intent = new Intent(getActivity(),ReferActivity.class);
					intent.putExtra("type", "at");
					startActivity(intent);
					break;
				case 2:
					
					intent = new Intent(getActivity(),ReferActivity.class);
					intent.putExtra("type", "reply");
					startActivity(intent);
					break;
				case 3:
					intent = new Intent(getActivity(),VoteActivity.class);
					startActivity(intent);
					break;
				case 4:
					int state = mLabels[4].equals("夜间模式")? 0:1;
					mLabels[4] = state==0?"日间模式":"夜间模式";
//					ngiht(state);
					ViewUtils.updateThemeState(getActivity(), state);
//					mAdapter.notifyDataSetChanged();
					
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
						getActivity().recreate();
					else {
						intent = getActivity().getIntent();
						getActivity().finish();
						startActivity(intent);
					}
					break;
				case 5:
					startActivity(new Intent(getActivity(),PrsActivity.class));
					break;
				}
			}
			
		});
		
		mGrViewFavor.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				int size = 0;
				if (myFavorite != null) {
					size = myFavorite.getBoard().size();
				}
				 
				if (position == size) {
					activity.select(2);
				} else {
					Intent intent = new Intent(getActivity(),BoardActivity.class);
					intent.putExtra("boardname", myFavorite.getBoard().get(position).getName());
					startActivity(intent);
				}
			}
			
		});
		activity.setFavorListener1(new FavorListener() {
			
			@Override
			public void onUpdate(MyFavorite favor) {
				myFavorite = favor;
				swipeAdapter.notifyDataSetChanged();
				
			}

			@Override
			public void onNofity(String type, String info) {
				
				if (type.equals("mail")) {
//					mCount[0] = info;
				} else if (type.equals("at")){
//					mCount[1] = info;
				} else {
//					mCount[2] = info;
				}
				mAdapter.notifyDataSetChanged();
			}
		});
		
		activity.getMyFavor(0);
	}
	
	/*给application加上遮罩，达到夜间模式的效果*/
//	private void ngiht(int state) {
//		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
//				android.view.WindowManager.LayoutParams.MATCH_PARENT,
//				android.view.WindowManager.LayoutParams.MATCH_PARENT,
//				android.view.WindowManager.LayoutParams.TYPE_APPLICATION,
//				android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | 
//				android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//				PixelFormat.TRANSLUCENT);
//		lp.gravity = Gravity.BOTTOM;
//		
//		final TextView tv = new TextView(getActivity());
//		tv.setBackgroundColor(0x00000000);
//		
//		final WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
//		wm.addView(tv, lp);
//		ViewUtils.updateThemeState(getActivity(), state);
////		mAdapter.notifyDataSetChanged();
//		getActivity().recreate();
//		handler.postDelayed(new Runnable() {
//			
//			@Override
//			publikc void run() {
//				wm.removeView(tv);
//			}
//		}, 200);
//	}
	
	private Handler handler = new Handler();
	private BaseAdapter mAdapter = new BaseAdapter() {
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			Drawable drawable = null;			
			int type = getItemViewType(position);
			BadgeViewHolder holder;
			if (convertView == null) {
				
				
				convertView = new FrameLayout(getActivity());
				LayoutParams  params = 
						new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
				convertView.setLayoutParams(params);
				
				//LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_1, parent, false);
				holder = new BadgeViewHolder();
				holder.text = new TextView(getActivity());
				holder.text.setLayoutParams(params);
				((ViewGroup) convertView).addView(holder.text);
				
				if (type != 3) {
					holder.badge = new BadgeView(getActivity(),holder.text);
				}
				
//				convertView = new TextView(getActivity());
//				LayoutParams  params = 
//						new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//				convertView.setLayoutParams(params);
				
				 convertView.setTag(holder);
//				drawable = getResources().getDrawable(mItems[position]);
//				convertView.setTag(drawable);
			} 
			else {
				holder = (BadgeViewHolder) convertView.getTag();
			}
			
			holder.text.setText(mLabels[position]);
			drawable = getResources().getDrawable(mItems[position]);
			drawable.setBounds(0, 0,drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			holder.text.setCompoundDrawables(null, drawable, null, null);
			holder.text.setCompoundDrawablePadding(DataUtils.getDisplayValue(3,getActivity().getApplicationContext()));
			holder.text.setPadding(0,DataUtils.getDisplayValue(3,getActivity().getApplicationContext()),0,DataUtils.getDisplayValue(3,getActivity().getApplicationContext()));
			holder.text.setGravity(Gravity.CENTER);
			if (ViewUtils.isDayTheme) {
				holder.text.setBackgroundColor(getResources().getColor(R.color.daymainitembk));
				holder.text.setTextColor(getResources().getColor(R.color.daymainFontColor));
			} else {
				holder.text.setBackgroundColor(getResources().getColor(R.color.nightmainitembk));
				holder.text.setTextColor(getResources().getColor(R.color.nightmainFontColor));
			}
			
			if (type == 0) {
				String boxdes = PrefernceManager.getInstance().getCurrentBoxDes(getActivity());
				if (boxdes != null && !boxdes.equals("")) {
					holder.badge.setText(boxdes);
					holder.badge.show();
				} else {
					holder.badge.hide();
				}
			} else if (type == 1 || type ==2) {
				String tp = type ==1?"at":"reply";
				int count = PrefernceManager.getInstance().getCurrentReferCount(getActivity(), tp);
				if (count== 0) {
					holder.badge.hide();
				} else {
					holder.badge.setText(count+"");
					holder.badge.show();
				}
			}
//				if (mIsCounted[position]) {
				//	if (PrefernceManager.getInstance().mBoxDec.equals(""))
				//	if (mCount[position].equals("")) 
				//		holder.badge.hide();
				//	else {
					
					
					
				//	}
//					else {
//						holder.badge.setText(mCount[position]);
//						holder.badge.show();
//					}
//				} else {
//					count(holder.badge, position);
//				}
			return convertView;
		//	((TextView)convertView).set
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public Object getItem(int position) {
			
			return mItems[position];
		}
		
		@Override
		public int getCount() {
			return mItems.length;
		}

		@Override
		public int getItemViewType(int position) {
			return position>=3?3:position;
		}

		@Override
		public int getViewTypeCount() {
			return 4;
		}
		
	};
	
	static class BadgeViewHolder {
		TextView text;
		BadgeView badge;
	}
	
	private SwipeAdapter swipeAdapter = new SwipeAdapter() {

		@Override
		public int getCount() {
			
			if (myFavorite == null) {
				return 1;
			}
			return myFavorite.getBoard().size()+1;
		}

		@Override
		public Object getItem(int position) {
			
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getSwipeLayoutResourceId(int position) {
			
			return R.id.swipe;
		}

		@Override
		public View generateView(int position, ViewGroup parent) {
			 View v =  LayoutInflater.from(getActivity()).inflate(R.layout.options_gridview_item,parent,false);
		     ((SwipeLayout) v).setDragEdge(SwipeLayout.DragEdge.Left);
		     
		     ViewHolder holder = new ViewHolder();
		     holder.tv = (TextView) v.findViewById(R.id.favor_item);
		     holder.trash = (ImageView) v.findViewById(R.id.trash);
		     v.setTag(holder);
		     return v;
		}

		@Override
		public void fillValues(final int position, View convertView) {
			
			int type = getItemViewType(position);
			
			ViewHolder holder = (ViewHolder) convertView.getTag();
			
			if (type == 0) {
				holder.tv.setText(myFavorite.getBoard().get(position).getDescription());
			 	((View)holder.trash.getParent()).setVisibility(View.VISIBLE);
//			 	 ((View)holder.tv.getParent()).setBackgroundColor(
//				    		getResources().getColor(R.color.yellow));
			} else {
				Drawable drawable = getResources().getDrawable(R.drawable.add_favor);
				drawable.setBounds(0, 0, DataUtils.getDisplayValue(20,getActivity().getApplicationContext()),DataUtils.getDisplayValue(20,getActivity().getApplicationContext()));
				holder.tv.setCompoundDrawables(drawable, null, null, null);
				if (ViewUtils.isDayTheme) {
					 ((View)holder.tv.getParent()).setBackgroundColor(
					    		getResources().getColor(R.color.daymainitembk));
				} else {
					 ((View)holder.tv.getParent()).setBackgroundColor(
					    		getResources().getColor(R.color.nightmainitembk));
				}
			   
			    ((LinearLayout)holder.trash.getParent()).setVisibility(View.GONE);   
			}
			
			holder.trash.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					activity.dealMyFavor("delete",0,"0",myFavorite.getBoard().get(position).getName());
				}
			});
		}

		@Override
		public int getItemViewType(int position) {

			if (position == getCount()-1) {
				return 1;
			} else 
				return 0;
		}

		@Override
		public int getViewTypeCount() {

			return 2;
		}
	};
	
	static class ViewHolder {
		TextView tv;
		ImageView trash;
	}
}

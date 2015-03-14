package xzw.szl.byr.fragment;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import xzw.szl.byr.BoardActivity;
import xzw.szl.byr.HomeActivity;
import xzw.szl.byr.R;
import xzw.szl.byr.HomeActivity.FavorListener;
import xzw.szl.byr.db.DBListTableHandler;
import xzw.szl.byr.dialog.ProgressDialog;
import xzw.szl.byr.info.Board;
import xzw.szl.byr.info.ByrSection;
import xzw.szl.byr.info.MyFavorite;
import xzw.szl.byr.info.Section;
import xzw.szl.byr.mananger.ByrThreadPool;
import xzw.szl.byr.utils.HttpUtils;
import xzw.szl.byr.utils.JsonUtils;
import xzw.szl.byr.utils.ViewUtils;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class SectionFragment extends Fragment{
	
	private ByrSection mByrSection;
	private Map<String,Section> mSectionMap;  //name——Section的映射。
	private ExpandableListView mExpandableListView;
	private MyExpandableListAdapter mExpandableListAdapter;
	private int[] colors;
	private Map<String,Boolean> mFavorited;   //is add to my favorites
	private MyFavorite mFavorite;
	private boolean isChangebySelf = false; // if others makes the map change,then this field is false;
	private  Handler mHandler;
	private Dialog mDialog;
	private String group = "{\"section_count\":10,\"section\":[" +
			"{\"name\":\"0\",\"description\":\"本站站务\",\"is_root\":true}," +
			"{\"name\":\"1\",\"description\":\"北邮校园\",\"is_root\":true}," +
			"{\"name\":\"2\",\"description\":\"学术科技\",\"is_root\":true}," +
			"{\"name\":\"3\",\"description\":\"信息社会\",\"is_root\":true}," +
			"{\"name\":\"4\",\"description\":\"人文艺术\",\"is_root\":true}," +
			"{\"name\":\"5\",\"description\":\"生活时尚\",\"is_root\":true}," +
			"{\"name\":\"6\",\"description\":\"休闲娱乐\",\"is_root\":true}," +
			"{\"name\":\"7\",\"description\":\"体育健身\",\"is_root\":true}," +
			"{\"name\":\"8\",\"description\":\"游戏对战\",\"is_root\":true}," +
			"{\"name\":\"9\",\"description\":\"乡亲乡爱\",\"is_root\":true}]}";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		HomeActivity homeActivity = (HomeActivity) getActivity().getLastCustomNonConfigurationInstance();
		if (homeActivity != null) {
			mByrSection = ((SectionFragment)homeActivity.getFragment(2)).mByrSection;
			mSectionMap = ((SectionFragment)homeActivity.getFragment(2)).mSectionMap;
			mFavorited = ((SectionFragment)homeActivity.getFragment(2)).mFavorited;
			mHandler = ((SectionFragment)homeActivity.getFragment(2)).mHandler;
			isChangebySelf = ((SectionFragment)homeActivity.getFragment(2)).isChangebySelf;
			colors = ((SectionFragment)homeActivity.getFragment(2)).colors;
		} else {
			colors = new int[]{getResources().getColor(R.color.section_pos1),getResources().getColor(R.color.section_pos2),
					getResources().getColor(R.color.section_pos3),getResources().getColor(R.color.section_pos4),getResources().getColor(R.color.section_pos5)};
			mSectionMap = new HashMap<String, Section>();
			mFavorited = new HashMap<String, Boolean>();
			mHandler= new SectionHandler(this);
			isChangebySelf = false;
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fragment_section,container,false);
	}


	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		((HomeActivity)getActivity()).setSectionFragment(this);
		initExpandableListView();
		
		getData();
		
		((HomeActivity)getActivity()).setFavorListener2(new FavorListener() {
			
			@Override
			public void onUpdate(MyFavorite favor) {
				mFavorite  = favor;
				updateMapFavorite();
				if (!isChangebySelf) {
					mExpandableListAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onNofity(String type, String info) {
				// TODO Auto-generated method stub
				
			}
		});
		
		((HomeActivity)getActivity()).getMyFavor(0);
	}
	
	private void updateMapFavorite() {
		
		mFavorited.clear();
		
		if (mFavorite != null) {
			for (Board board: mFavorite.getBoard()) {
				mFavorited.put(board.getName(),true);
			}
		}
	}
	private void initExpandableListView() {
		mDialog = new ProgressDialog(getActivity(), "加载中...");
		mExpandableListAdapter = new MyExpandableListAdapter();
		mExpandableListView = (ExpandableListView) getActivity().findViewById(R.id.sectionExpandable);
		mExpandableListView.setAdapter(mExpandableListAdapter);
		mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View view, int groupPosition,
					long id) {
				
				//获取board的信息;
				getBoardData(mByrSection.getSection().get(groupPosition).getName(),false);
				
				return false;
			}
		});
		mExpandableListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View view, int groupPosition,
					int childPosition, long id) {
			//	Toast.makeText(getActivity(), "onChildClick", Toast.LENGTH_SHORT).show();
				
				Section section = mSectionMap.get(mByrSection.getSection().get(groupPosition).getName());
				if (section != null) {
					Object object = mExpandableListAdapter.getChildObject(groupPosition, childPosition);
					if (object instanceof String) {
						String sub = (String) object;
						if (mSectionMap.containsKey(sub)) { // 已经下载过了
							mExpandableListAdapter.upDateSubOpenMap(sub);
							mExpandableListAdapter.notifyDataSetChanged();
						} else {
							getBoardData(sub,true);
						}
					} else if (object instanceof Board){
						Board board = (Board) object;
						startActivity(board.getName());
					}
				}
				return false;
			}
		});
		
//		int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
//		mExpandableListView.setIndicatorBounds(width-200, width-150);
	}
	
	
	private void getBoardData(String name,boolean isSub) {
		
		if (!mSectionMap.containsKey(name)) {
			mDialog.show();
			Runnable r = new BoardRunnable(name,isSub);
			ByrThreadPool.getTHreadPool().execute(r);
		}
	}
	
	private Runnable r = new Runnable() {
		
		@Override
		public void run() {
			
			HttpUtils.httpRequest("/section.json", new HttpUtils.HttpRequestListener() {
				
				@Override
				public void onSuccess(String content) {
					ByrSection byrSection = (ByrSection) JsonUtils.toBean(content,ByrSection.class);
					if (byrSection != null) {
						mHandler.obtainMessage(0,byrSection).sendToTarget();
					}
				}
				
				@Override
				public void onFailed(String reason) {
					mHandler.obtainMessage(2,reason).sendToTarget();
				}
				
				@Override
				public void onError(Throwable e) {
					mHandler.obtainMessage(3).sendToTarget();
					// TODO Auto-generated method stub
					
				}
			});
			
		}
	};
	public void getData() {
		
		//if (mByrSection == null) {
//			mExpandableListView.setEmptyView(((HomeActivity)getActivity()).getProgressBar());
//			ByrThreadPool.getTHreadPool().execute(r);
		//}
		
		mByrSection = (ByrSection) JsonUtils.toBean(group, ByrSection.class);
		mExpandableListAdapter.notifyDataSetChanged();
	}
	static class ViewHolder{
		TextView sectionName;
		TextView sectionDes;
		
	}
	public class MyExpandableListAdapter extends BaseExpandableListAdapter {
			
		private Map<String, Boolean> subOpenMap; //记录开关信息
		
		public MyExpandableListAdapter() {
			subOpenMap = new HashMap<String, Boolean>();
		}
		
		public void upDateSubOpenMap (String key) {
			
			if (subOpenMap.containsKey(key)) {
				if (subOpenMap.get(key)) {
					subOpenMap.put(key, false);
				} else {
					subOpenMap.put(key, true);
				}
			} else {
				subOpenMap.put(key, true);
			}
		}
		
		//childPosition 到 内部某个位置的 返回String 即为subSection,返回Board 即为Board项
		public Object getChildObject(int groupPosition,int childPosition) {
			int count = -1;
			Section section = mSectionMap.get(mByrSection.getSection().get(groupPosition).getName());
			if (section != null) {
				for (String sub : section.getSub_section()) {
					Section subSection = mSectionMap.get(sub);
					count++;
					if (count == childPosition) {
						return sub;
					}
					if (subOpenMap.containsKey(sub) && subOpenMap.get(sub) == true) {
						for (Board board:subSection.getBoard()) {
							count++;
							if (count == childPosition) {
								return board;
							}
						}
					}
				}
				for (Board board:section.getBoard()) {
					count++;
					if (count == childPosition) {
						return board;
					}
				}
			}
			return null;
		}
		
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
		@Override
		public boolean hasStableIds() {
			return true;
		}
		
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.section,parent,false);
				viewHolder = new ViewHolder();
				viewHolder.sectionName = (TextView)convertView.findViewById(R.id.sectionname);
//				Drawable bottom = getActivity().getResources().getDrawable(R.drawable.group_arrow_down);
//				bottom.setBounds(0, 0 - DataUtils.getDisplayValue(2), DataUtils.getDisplayValue(21),DataUtils.getDisplayValue(5));
//				viewHolder.sectionName.setCompoundDrawables(null, null, null, bottom);
				viewHolder.sectionDes = (TextView) convertView.findViewById(R.id.sectiondes);	
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
		
			viewHolder.sectionName.setBackgroundColor(colors[groupPosition % 5]);
			viewHolder.sectionName.setText(mByrSection.getSection().get(groupPosition).getName());
				
			viewHolder.sectionDes.setText(mByrSection.getSection().get(groupPosition).getDescription());

			return convertView;
		}
		
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}
		
		@Override
		public int getGroupCount() {
			if (mByrSection == null) return 0; 
			return mByrSection.getSection_count();
		}
		
		@Override
		public Object getGroup(int groupPosition) {
			return mByrSection.getSection().get(groupPosition);
		}
		
		@Override
		public int getChildrenCount(int groupPosition) {
			Section section = mSectionMap.get(mByrSection.getSection().get(groupPosition).getName());
			int count = 0;
			if (section != null) { 
				if (section.getBoard() != null) 
					count += section.getBoard().size();
				if (section.getSub_section() != null) {
					int subcount = section.getSub_section().size();              //加上子分区
					count += subcount;
					for (String sub:section.getSub_section()) {
						if (subOpenMap.containsKey(sub) && subOpenMap.get(sub) == true) {                     //处于打开状态
							Section subSection = mSectionMap.get(sub);
							count+=subSection.getSub_section().size();
							count+=subSection.getBoard().size();
						}
					}
					
				}
					
			}
			return count;
		}
		
		@Override
		public View getChildView(int groupPosition, int childPosition, 
				boolean isLastChild, View convertView,ViewGroup parent) {
			
			ChildViewHolder childViewHolder = null;
			SubViewHolder subViewHolder = null;
			
			int type = getChildType(groupPosition, childPosition);
			if (convertView == null) {
				
					if (type == 0) {
						convertView = LayoutInflater.from(getActivity()).inflate(R.layout.sub_section,parent,false);
						subViewHolder = new SubViewHolder();
						
						subViewHolder.subSection = (TextView) convertView.findViewById(R.id.subsection_item);
					//	subViewHolder.board = (ListView) convertView.findViewById(R.id.subsection_board);
						subViewHolder.image = (ImageView) convertView.findViewById(R.id.index_imageview);
					//	subViewHolder.board.setVisibility(View.GONE);
						convertView.setTag(subViewHolder);
					} else {
						convertView = LayoutInflater.from(getActivity()).inflate(R.layout.section_board,parent,false);
						childViewHolder = new ChildViewHolder();
						childViewHolder.index = (TextView) convertView.findViewById(R.id.sectionboardindex);
						childViewHolder.board = (TextView) convertView.findViewById(R.id.sectionboard);
						childViewHolder.favor = (ImageView) convertView.findViewById(R.id.favor);
						
						convertView.setTag(childViewHolder);
					}
			} else {
				if (type == 0)
					subViewHolder = (SubViewHolder) convertView.getTag();
				else
					childViewHolder = (ChildViewHolder) convertView.getTag();
			}
			
			if (type == 0) {
				
				String sub  = (String) getChildObject(groupPosition, childPosition);
				subViewHolder.subSection.setText(sub);
				
				if (subOpenMap.containsKey(sub) && subOpenMap.get(sub) == true) {
					subViewHolder.image.setImageResource(R.drawable.sub_up);
				} else {
					subViewHolder.image.setImageResource(R.drawable.sub_right);
				}
			} else if(type == 1){
				
				final Board board = (Board) getChildObject(groupPosition, childPosition);
				childViewHolder.index.setBackgroundColor(colors[groupPosition%5]);
				childViewHolder.board.setText(board.getDescription());
				
				if (mFavorited.containsKey(board.getName()) && mFavorited.get(board.getName())) {
					childViewHolder.favor.setImageResource(R.drawable.board_favord);
					childViewHolder.favor.setTag("red");
				} else {
					childViewHolder.favor.setImageResource(R.drawable.board_favor);
					childViewHolder.favor.setTag("gray");
				}
				childViewHolder.favor.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String tag = (String) v.getTag();
						
						if (tag.equals("gray")) {
							 ((HomeActivity) getActivity()).dealMyFavor("add", 0, "0", board.getName());
						} else {
							((HomeActivity) getActivity()).dealMyFavor("delete", 0, "0", board.getName());
						}
					}
				});
			}
			return convertView;
		}
		
		
		
		@Override
		public int getChildType(int groupPosition, int childPosition) {
			
			Object object = getChildObject(groupPosition, childPosition);
			
			if (object instanceof String) {
				return 0;
			} else if (object instanceof Board){
				return 1;
			} 
			return 2;
		}

		@Override
		public int getChildTypeCount() {
			
			return 3;					//0、subSection  1、board  2、no sub
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}
		
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}
	}
	static class SubViewHolder {
		ImageView image;
		TextView subSection;
	}
	static class ChildViewHolder {
		TextView board;
		TextView index;
		ImageView favor;
	}
	
	private static class SectionHandler extends Handler {
		WeakReference<SectionFragment> mWeakReference;
		
		public SectionHandler(SectionFragment fragment) {
			mWeakReference = new WeakReference<SectionFragment>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			SectionFragment fragment = mWeakReference.get();
			
			if (fragment != null) {
				switch (msg.what) {
				case 0:
					
					fragment.mByrSection = (ByrSection) msg.obj;
					fragment.mExpandableListAdapter.notifyDataSetChanged();
					break;
				case 1:
					Section section = (Section) msg.obj;
					fragment.mSectionMap.put(section.getName(), section);
					
					if (section.getParent() != null) {
						fragment.mExpandableListAdapter.upDateSubOpenMap(section.getName());
					}
					fragment.mExpandableListAdapter.notifyDataSetChanged();
					fragment.mDialog.dismiss();
					break;
				case 2:
					if (fragment.mDialog != null) fragment.mDialog.dismiss();
					ViewUtils.displayMessage(fragment.getActivity(),(String)msg.obj);
					break;
				case 3:
					if (fragment.mDialog != null) fragment.mDialog.dismiss();
					ViewUtils.displayMessage(fragment.getActivity(), fragment.getActivity().getResources().getString(R.string.network_error));
					break;
				}
			}
		}
	}
	
	private void startActivity(String name) {
		Intent intent = new Intent(getActivity(),BoardActivity.class);
		intent.putExtra("boardname", name);
		startActivity(intent);
	}
	
	private class BoardRunnable implements Runnable{
		
		private String name;
		private boolean isSub;
		
		public BoardRunnable(String name, boolean isSub) {
			super();
			this.name = name;
			this.isSub = isSub;
		}
		@Override
		public void run() {
			
				String content = DBListTableHandler.getInstance().queryItemContent(name, 1);
				if (content != null) {
					Section section = (Section) JsonUtils.toBean(content,Section.class);
					
					if (section != null) {
						mHandler.obtainMessage(1,section).sendToTarget();
					}
				}
				
				HttpUtils.httpRequest("/section/" + name + ".json", new HttpUtils.HttpRequestListener() {
					
					@Override
					public void onSuccess(String content) {
						Section section = (Section) JsonUtils.toBean(content,Section.class);
						
						if (section != null) {
							mHandler.obtainMessage(1,section).sendToTarget();
							DBListTableHandler.getInstance().updateItemListTable(name, 1, content);
						}
					}
					
					@Override
					public void onFailed(String reason) {
						mHandler.obtainMessage(2, reason).sendToTarget();
					}
					
					@Override
					public void onError(Throwable e) {
						mHandler.obtainMessage(3).sendToTarget();
					}
				});
		}
		
	}
}
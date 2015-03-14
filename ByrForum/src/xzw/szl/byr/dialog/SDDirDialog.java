package xzw.szl.byr.dialog;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import xzw.szl.byr.R;
import xzw.szl.byr.utils.ImageUtils;
import xzw.szl.byr.utils.ViewUtils;
import xzw.szl.byr.view.FaceSelectView.OnAttachMentSelectListener;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.storage.StorageManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SDDirDialog extends Dialog {

	private Context mContext;
	private String mCurrentDir;                   //当前目录
	private ListView mListView;
	private ImageView mImageVIewClose;
	private String[] mFile;
	private int level;
	private OnAttachMentSelectListener mAttachMentSelectListener;
	
	public SDDirDialog(Context context) {
		super(context);
	}
	
	public SDDirDialog(Context context,OnAttachMentSelectListener listener) {
		this(context);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sd_dir);
		
		mAttachMentSelectListener = listener;
		setTitle("SD卡目录");
		mContext = context;
		level = 0;
		mListView = (ListView) findViewById(R.id.listView_sddir);

		if (getFirstLevel()) {
			mListView.setAdapter(adapter);
		}
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					
					level--;
					if (level<0) {
						Toast.makeText(mContext, "已经是最上层了!!",Toast.LENGTH_SHORT).show();
						level = 0;
						return;
					}
					if (mCurrentDir == null) {
						SDDirDialog.this.dismiss();
						return;
					}
					File file = new File(mCurrentDir);
					mCurrentDir = file.getParent();
					file = new File(mCurrentDir);
					mFile = file.list();
					Arrays.sort(mFile);
					adapter.notifyDataSetChanged();
				} else {
					level++;
					String path = mCurrentDir != null ? mCurrentDir +  "/" + mFile[position-1]:mFile[position-1];
					File file = new File(path);
					if (file.isDirectory()) {
						String[] files = file.list();      //
						if (files != null) {
							mFile = files;
							Arrays.sort(mFile);
						} else {
							mFile = new String[0];
						}
						mCurrentDir = path;
						adapter.notifyDataSetChanged();
					} else {
						mAttachMentSelectListener.selected(path);
					}
				}
			}
		});
		
		mImageVIewClose = (ImageView) findViewById(R.id.imageview_sddir_back);
		mImageVIewClose.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SDDirDialog.this.dismiss();
			}
		});
	}
	
	public boolean getFirstLevel() {
		
		StorageManager manager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
		try {
			Method method = manager.getClass().getMethod("getVolumePaths",(Class<?>[])null);
			mFile = (String[]) method.invoke(manager, (Object[])null);
			Arrays.sort(mFile);
			return true;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private BaseAdapter adapter = new BaseAdapter() {
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new TextView(mContext);
				LayoutParams params = new LayoutParams(
						LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				convertView.setLayoutParams(params);
				convertView.setPadding(15, 10, 0, 5);
			}
			
			//图片
			if (position == 0) {
				Drawable drawable = mContext.getResources().
						getDrawable(R.drawable.upfolder);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
				((TextView)convertView).setCompoundDrawables(drawable,null,null,null);
				((TextView)convertView).setText("\t\t...返回上层");
			} else {
				Drawable drawable  =  null;
				String path =  mCurrentDir == null? mFile[position-1]:mCurrentDir + "/" + mFile[position-1];
				if (path.endsWith("jpg") || path.endsWith("png") || path.endsWith("jpeg") ) {
					drawable =new BitmapDrawable(null,ImageUtils.compressImage(path, 32, 32));
				} else {
					File file = new File(path);
					if (file.isDirectory()) {
						drawable = mContext.getResources().
								getDrawable(R.drawable.folder);
					} else {
						drawable = mContext.getResources().
								getDrawable(R.drawable.ordfile);
					}
				}
				drawable.setBounds(0, 0, 32,32);
				((TextView)convertView).setCompoundDrawables(drawable,null,null,null);
				((TextView)convertView).setText("\t\t" + mFile[position-1]);
				
				int color = ViewUtils.isDayTheme(mContext) == true ? R.color.daymainFontColor:R.color.nightmainFontColor;
				((TextView)convertView).setTextColor(mContext.getResources().getColor(color));
				
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
			return mFile[position-1];
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mFile.length+1;
		}

	};
}

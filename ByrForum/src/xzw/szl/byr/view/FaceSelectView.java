package xzw.szl.byr.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xzw.szl.byr.R;
import xzw.szl.byr.utils.ImageUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
public class FaceSelectView extends ViewGroup implements View.OnClickListener{

	
	public FaceSelectView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}


	public FaceSelectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FaceSelectView(Context context) {
		super(context);
		init(context);
	}
	
	
	private TextView mFace_Classics;
	private TextView mFace_Monkey;
	private TextView mFace_Tuzki;
	private TextView mFace_Onion;
	private GridView mGridView;
	private List<String> mFaces;
	private OnAttachMentSelectListener mAttachMentSelectListener;
	private Context mContext;
	
	private int width;
	
	public void init (Context context) {
		 mContext = context;
		 View layout = LayoutInflater.from(context).inflate(R.layout.face_layout,this,false);
		 mFace_Classics = (TextView) layout.findViewById(R.id.face_classics);
		 mFace_Monkey = (TextView)  layout.findViewById(R.id.face_monkey);
		 mFace_Tuzki = (TextView) layout.findViewById(R.id.face_tuzki);
		 mFace_Onion = (TextView) layout.findViewById(R.id.face_onion);
		 mGridView = (GridView) layout.findViewById(R.id.gridView1);
		 
		 mFace_Classics.setOnClickListener(this);
		 mFace_Monkey.setOnClickListener(this);
		 mFace_Tuzki.setOnClickListener(this);
		 mFace_Onion.setOnClickListener(this);
		 
		 addView(layout);
		 mFaces = new ArrayList<String>();
		 generateFaces("em",73);
		 mGridView.setAdapter(adapter);
		 
		width = mContext.getResources().getDisplayMetrics().widthPixels / 4;
		setTextType(1);
	}


	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthSize, heightSize);
	}


	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int mTotalHeight = 0;
	//	if (changed) {
			int childCount = getChildCount();
			
			for (int i=0;i<childCount;i++) {
				View child = getChildAt(i);
				
				int measureHeight = child.getMeasuredHeight();
				int measureWidth = child.getMeasuredWidth();
				child.layout(l, mTotalHeight, measureWidth,mTotalHeight+measureHeight);
				mTotalHeight += measureHeight;
			}
		//}
		
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.face_classics:
			 generateFaces("em",73);
			 adapter.notifyDataSetChanged();
			 setTextType(1);
			break;
		case R.id.face_monkey:
			 generateFaces("ema",41);
			 adapter.notifyDataSetChanged();
			 setTextType(2);
			break;
		case R.id.face_tuzki:
			 generateFaces("emb",23);
			 adapter.notifyDataSetChanged();
			 setTextType(3);
			break;
		case R.id.face_onion:
			 generateFaces("emc",58);
			 adapter.notifyDataSetChanged();
			 setTextType(4);
			break;
		}
		
	}
	
	private BaseAdapter adapter = new BaseAdapter() {
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new ImageView(mContext);
				
				AbsListView.LayoutParams params = new AbsListView.LayoutParams(
						AbsListView.LayoutParams.WRAP_CONTENT,AbsListView.LayoutParams.WRAP_CONTENT);
				convertView.setLayoutParams(params);
				convertView.setPadding(0, 5, 0, 5);	
			}
			
			try {
				
				
				Bitmap bitmap = BitmapFactory.decodeStream(mContext.getAssets().open(mFaces.get(position)+".gif"));
				((ImageView)convertView).setImageDrawable(new BitmapDrawable(
						null,ImageUtils.ZoomBitmap(bitmap, width, width)));
				bitmap.recycle();
				((ImageView)convertView).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mAttachMentSelectListener.selected("[" + mFaces.get(position) + "]");
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return convertView;
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public Object getItem(int position) {
			return mFaces.get(position);
		}
		
		@Override
		public int getCount() {
			return mFaces.size();
		}
	};

	public void setOnAttachMentListener(OnAttachMentSelectListener listener) {
		mAttachMentSelectListener = listener;
	}
	
	private void generateFaces(String staff,int count) {
		mFaces.clear();
		for (int i=0;i<count;i++) {
			mFaces.add(staff+(i+1));
		}
	}
	
	private void setTextType(int i) {
		switch(i) {
		case 1:
			setTextView(mFace_Classics, true);
			setTextView(mFace_Monkey, false);
			setTextView(mFace_Tuzki, false);
			setTextView(mFace_Onion, false);
			break;
		case 2:
			setTextView(mFace_Classics, false);
			setTextView(mFace_Monkey, true);
			setTextView(mFace_Tuzki, false);
			setTextView(mFace_Onion, false);
			break;
		case 3:
			setTextView(mFace_Classics, false);
			setTextView(mFace_Monkey, false);
			setTextView(mFace_Tuzki, true);
			setTextView(mFace_Onion, false);
			break;
		case 4:
			setTextView(mFace_Classics,false);
			setTextView(mFace_Monkey, false);
			setTextView(mFace_Tuzki, false);
			setTextView(mFace_Onion, true);
			break;
		}
	}
	
	private void setTextView(TextView tv,boolean ispressed) {
		if (ispressed) {
			tv.setTextColor(getResources().getColor(android.R.color.white));
			tv.setBackgroundColor(getResources().getColor(R.color.user_dialog_item));
		} else {
			tv.setTextColor(getResources().getColor(R.color.user_dialog_item));
			tv.setBackgroundColor(getResources().getColor(android.R.color.white));
		}
	}
	
	
	public interface OnAttachMentSelectListener{
		
		void selected(String filepath);
	}
}

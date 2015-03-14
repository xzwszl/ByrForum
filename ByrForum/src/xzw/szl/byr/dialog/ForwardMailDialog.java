package xzw.szl.byr.dialog;
import xzw.szl.byr.R;
import xzw.szl.byr.mananger.ByrThreadPool;
import xzw.szl.byr.utils.HttpUtils;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class ForwardMailDialog extends Dialog{

	
	private TextView mCancel;
	private EditText mReceiver;
	private TextView mSubmit;
	private Context context;
	private String mBox;
	private int mIndex;
	
	private static Handler handler = new Handler();
	
	public ForwardMailDialog(Context context) {
		
		super(context);
		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_forward);
		init();
	}
	
	public ForwardMailDialog(Context context, String box, int index) {
		this(context);
		this.mBox = box;
		this.mIndex = index;
	}
	
	public void SetBoard(String box) {
		this.mBox = box;
	}
	
	public void setId(int index){
		this.mIndex = index;
	}
	
	public void reset() {
		mSubmit.setBackgroundColor(context.getResources().getColor(R.color.green));
		mSubmit.setText("提交");
		
		mReceiver.setText("");
	}
	
	private void init() {
		
		mCancel = (TextView) findViewById(R.id.cancel);
		mReceiver = (EditText) findViewById(R.id.reciver);
		mSubmit = (TextView) findViewById(R.id.submit);
		findViewById(R.id.threads).setVisibility(View.GONE);
		findViewById(R.id.noref).setVisibility(View.GONE);
		findViewById(R.id.noatt).setVisibility(View.GONE);
		
		mCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ForwardMailDialog.this.dismiss();
			}
		});
		
		mSubmit.setText("提交");
		mSubmit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (((TextView)v).getText().equals("提交成功")) return;
				mCancel.setClickable(false);
				mSubmit.setText("提交ing~");
				
				ByrThreadPool.getTHreadPool().execute(r);
			}
		});
		
	}
	
	//转寄文章
	private Runnable r = new Runnable() {
		
		@Override
		public void run() {
			
			HttpUtils.postForwardBox(mBox, mIndex, mReceiver.getText().toString(), new HttpUtils.HttpRequestListener() {
				
				@Override
				public void onSuccess(String content) {
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							mSubmit.setBackgroundColor(context.getResources().getColor(R.color.light_grey));
							mSubmit.setText("提交成功");
							mCancel.setClickable(true);
						}
					});
				}
				
				@Override
				public void onFailed(final String reason) {
						handler.post(new Runnable() {
						
						@Override
						public void run() {
							mSubmit.setBackgroundColor(context.getResources().getColor(R.color.red));
							mSubmit.setText(reason);
							mCancel.setClickable(true);
						}
					});
				}
				
				@Override
				public void onError(Throwable e) {
					
				}
			});
		}
	};
}


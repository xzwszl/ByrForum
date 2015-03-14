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

public class ForwardDialog extends Dialog{

	
	private TextView mCancel;
	private EditText mReceiver;
	private TextView mSubmit;
	private CheckBox mThreads;
	private CheckBox mNoref;
	private CheckBox mNoatt;
	private Context context;
	private String board;
	private int id;
	private String[] params;
	
	private static Handler handler = new Handler();
	
	public ForwardDialog(Context context) {
		
		super(context);
		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_forward);
		init();
	}
	
	public ForwardDialog(Context context, String board, int id) {
		this(context);
		this.board = board;
		this.id = id;
	}
	
	public void SetBoard(String board) {
		this.board = board;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public void reset() {
		mSubmit.setBackgroundColor(context.getResources().getColor(R.color.green));
		mSubmit.setText("提交");
		
		mReceiver.setText("");
	}
	
	private void init() {
		
		params = new String[]{"0","0","0"}; 
		mCancel = (TextView) findViewById(R.id.cancel);
		mReceiver = (EditText) findViewById(R.id.reciver);
		mSubmit = (TextView) findViewById(R.id.submit);
		mThreads = (CheckBox) findViewById(R.id.threads);
		mNoref = (CheckBox) findViewById(R.id.noref);
		mNoatt = (CheckBox) findViewById(R.id.noatt);
		
		mCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ForwardDialog.this.dismiss();
			}
		});
		
		mSubmit.setText("提交");
		mSubmit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (((TextView)v).getText().equals("提交成功")) return;
				mCancel.setClickable(false);
				mSubmit.setText("提交ing~");
				params[0] = mThreads.isChecked()?"1":"0";
				params[1] = mNoref.isChecked()?"1":"0";
				params[2] = mNoatt.isChecked()?"1":"0";
				
				ByrThreadPool.getTHreadPool().execute(r);
			}
		});
		
	}
	
	//转寄文章
	private Runnable r = new Runnable() {
		
		@Override
		public void run() {
			
			HttpUtils.postForward(board, id, mReceiver.getText().toString(),new HttpUtils.HttpRequestListener() {
				
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
					// TODO Auto-generated method stub
					
				}
			}, params);
		}
	};
}

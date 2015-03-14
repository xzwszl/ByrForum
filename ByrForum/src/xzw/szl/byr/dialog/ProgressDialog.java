package xzw.szl.byr.dialog;

import xzw.szl.byr.R;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressDialog extends Dialog{

	public ProgressDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}
//	private Context mContext;
//	private String status;
	
	public ProgressDialog(Context context, String status) {
		
		this(context,R.style.dialog);
//		this.mContext = context;
//		this.status = status;
		
	//	mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LinearLayout layout = new LinearLayout(context);
		layout.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		layout.setGravity(Gravity.CENTER);
		layout.setPadding(15, 5, 15, 5);
		layout.setBackgroundResource(R.drawable.shape_dialog);
		ProgressBar progressBar = new ProgressBar(context,null,android.R.attr.progressBarStyle);
		progressBar.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		//progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progressbar));
		TextView textView = new TextView(context);
		textView.setText(status);
		textView.setPadding(10,0,5, 0);
		textView.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		textView.setTextSize(15.0f);
		textView.setTextColor(context.getResources().getColor(android.R.color.white));
		layout.addView(progressBar);
		layout.addView(textView);
//				layout.setBackgroundColor();
		layout.measure(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			
		this.setContentView(layout);	
	}	
}

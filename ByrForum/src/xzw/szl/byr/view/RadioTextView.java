package xzw.szl.byr.view;

import xzw.szl.byr.R;
import xzw.szl.byr.utils.DataUtils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.TextView;

public class RadioTextView extends TextView{
	
	private boolean isChecked;
	private boolean isEnd;
	private int color = getResources().getColor(R.color.radio_blue);
	Paint paint = new Paint();
	private int ordinary;
	public RadioTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public RadioTextView(Context context, AttributeSet attrs)  {
		super(context, attrs);
	}
	
	public RadioTextView(Context context) {
		super(context);
	}
	
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
	public boolean isChecked() {
		return isChecked;
	}
	
	public void setColor(int color) {
		this.color = color;
	}

	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		
		float width = getWidth();      // height == width
		paint.setStyle(Style.FILL);
		
		paint.setAntiAlias(true);
		
		if (isChecked) {
			paint.setColor(color);
			canvas.drawCircle(width/2, width/2, width*0.35f, paint);
		} else {
			paint.setColor(Color.WHITE);
		}
		
		
		
		paint.setStyle(Style.STROKE);
		float w = DataUtils.getDisplayValue(1);
		paint.setStrokeWidth(w);
		
		if (isChecked || isEnd) {
			paint.setColor(color);
		} else {
			paint.setColor(getCurrentTextColor());
		}
		canvas.drawCircle(width/2, width/2, width/2 - w, paint);
		super.onDraw(canvas);
	}
		
}

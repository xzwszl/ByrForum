package xzw.szl.byr.view;

import xzw.szl.byr.utils.DataUtils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.TextView;

public class VoteCountTextView extends TextView{
	
	private int count;
	private float round;
	private int color = 0;
	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	
	public VoteCountTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public VoteCountTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public VoteCountTextView(Context context) {
		super(context);
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public void setRound(float round) {
		this.round = round;
	}
	
	public void setColor(int color) {
		this.color = color;
	}

	
	@Override
	protected void onDraw(Canvas canvas) {
		
		
		if (color != 0) {
			int width = getWidth() - DataUtils.getDisplayValue(75);
			int height = getHeight();
			
			
			int end = (int)(width * round);
			
			paint.setStyle(Style.FILL);
			paint.setColor(color);
			
			canvas.drawRect(0, 0, end, height, paint);
			paint.setColor(Color.BLACK);
			
			
			paint.setTextSize(DataUtils.getDisplayValue(12));
			FontMetricsInt fontMetrics = paint.getFontMetricsInt();
			
			//paint.setAntiAlias(false);
			int baseline =   (height - fontMetrics.bottom + fontMetrics.top)/2 - fontMetrics.top;
			
			String value = "";
			if (count >= 10000)  {
				value += "上万";
			} else {
				value += count;
			}
			value +="(" +String.valueOf((int)(round * 100)) + "%)";
			canvas.drawText(value , end+10, baseline, paint);
		}
		super.onDraw(canvas);
	}
	
	
}

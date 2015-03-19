package xzw.szl.byr.utils;

import java.io.IOException;


import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifTextView;
import xzw.szl.byr.info.Attachment;
import xzw.szl.byr.info.ByrFile;
import xzw.szl.byr.mananger.ImageCacheManager2;
import xzw.szl.byr.mananger.ImageCacheManager2.ImageAcquireListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Html;
import android.widget.TextView;

public class URLImageGetter implements Html.ImageGetter{

	private GifTextView tv;
	private Context context;
	private Attachment attachment;
	private Handler handler;
	private int width;
	private int height;
	
	public URLImageGetter (GifTextView tv,Context context,Attachment attachment,Handler handler,int width,int height) {
		this.tv = tv;
		this.context = context;
		this.attachment = attachment;
		this.handler = handler;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public Drawable getDrawable(String source) {
		
				if (source == null || ("").equals(source)) return null;
				if (source.startsWith("em")) {
					String name = source + ".gif";
					try {
						GifDrawable gifDrawable = new GifDrawable(context.getAssets(),name);
//						BitmapDrawable gifDrawable = new BitmapDrawable(null,context.getAssets().open(name));
						gifDrawable.setBounds(0,0, gifDrawable.getIntrinsicWidth()*4/3, gifDrawable.getIntrinsicHeight()*4/3);
						//gifDrawable.start();
						return gifDrawable;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return null;
					}
				} else {
					
					String url = source;
					if (!source.startsWith("http") && attachment != null) {
					 ByrFile file = attachment.	getFile().get(Integer.parseInt(source)-1);
					 
					 url = file.getThumbnail_middle() + "/" + file.getName();
					}
					
					Bitmap bitmap = ImageCacheManager2.INSTANCE.getImageFromCache(url,width,height);
					
				//	if (bitmap == null) bitmap = ImageUtils.getSDImage(url);
					if (bitmap != null) {
						
					 	BitmapDrawable bd = new BitmapDrawable(null, bitmap);
						
					 	bd.setBounds(getDrawableRect(bitmap));
						
//						bd.setBounds(0,0,bd.getIntrinsicWidth(),bd.getIntrinsicHeight());
					 return bd;
					}
						
					if (bitmap == null) {	//从网络获取图片
						
						URLDrawable bd = new URLDrawable();
						bd.setBounds(0,0,bd.getIntrinsicWidth(),bd.getIntrinsicHeight());
						
						ImageCacheManager2.INSTANCE.startAcquireImage2(url,getContentImageAcquireListener(tv, bd) ,width, height,false);
						return bd;
					}
				}
				return null;
		}

	private Rect getDrawableRect(Bitmap bitmap) {
		
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		
		
//		float width = 0;
//		float height = 0;

//		if (w<=width && h <=height) {
			return new Rect((int)(width-w)/2, 0, (int)(width+w)/2, h);
//		} else if (w>width && h<=height){
//			return new Rect(0, 0, w,h);
//		} else if (w<=width && h > height) {
//			return new Rect((int)(width - w*height/h)/2, 0,(int) (width +  w*height/h)/2, (int)height);
//		} else {             //都大于的话
//			float r = height/h>width/w?height/h:width/w;
//			return new Rect((int)(width-w/r)/2, 0, (int)(width+w/r)/2, (int)(h/r));
//		}
	}
	static class URLDrawable extends BitmapDrawable {
		
		 BitmapDrawable drawable;

		@Override
		public void draw(Canvas canvas) {
			
			if (drawable!= null)
				drawable.draw(canvas);
		}
		
	}
	
	
	
	private ImageAcquireListener getContentImageAcquireListener (
			final TextView textView, final URLDrawable bd) {
		
		return new ImageAcquireListener() {
			
			@Override
			public void onSuccess(final Bitmap bitmap) {
				
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						
						bd.drawable = new BitmapDrawable(null,bitmap);
						Rect rect = getDrawableRect(bitmap);
						bd.setBounds(rect);
						bd.drawable.setBounds(rect);
//						bd.drawable.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
//						bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
//						bd.invalidateSelf();
						textView.setText(textView.getText());
					}
				});

			}
			
			@Override
			public void onFailure() {

			}
		};
	}
	
	
}
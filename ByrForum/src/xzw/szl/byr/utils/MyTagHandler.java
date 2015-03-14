package xzw.szl.byr.utils;

import java.util.Locale;

import org.xml.sax.XMLReader;

import xzw.szl.byr.assist.PictureActivity;
import xzw.szl.byr.info.Attachment;
import xzw.szl.byr.info.ByrFile;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;

public class MyTagHandler implements Html.TagHandler {
	
	private Context context;
	private Attachment attachment;
	public MyTagHandler(Context context,Attachment attachment) {
		this.context = context;
		this.attachment = attachment;
	}

	
	@Override
	public void handleTag(boolean opening, String tag, Editable output,
			XMLReader xmlReader) {
		
		if (tag.toLowerCase(Locale.US).equals("img")) {
			int len = output.length();
			ImageSpan[] images = output.getSpans(len-1, len, ImageSpan.class);
			
			//表情图片无法点击
			if (!images[0].getSource().startsWith("em"))
				output.setSpan(new ImageClick(context,images[0].getSource()),len-1,len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
	
	private class ImageClick extends ClickableSpan{
		private Context context;
		private String source;
		
		public ImageClick(Context context, String source) {
			this.context = context;
			this.source = source;
		}

		@Override
		public void onClick(View widget) {
			//Toast.makeText(context, url, Toast.LENGTH_SHORT).show();
			
			String type = "";
			String url = source;
			if (!source.startsWith("http") && attachment != null) {
				int pos = Integer.parseInt(source)-1;
				ByrFile file = attachment.getFile().get(pos);
				url = file.getUrl();
				type = file.getName().substring(file.getName().length()-4);
			}
			
			Intent intent = new Intent(context, PictureActivity.class);
			intent.putExtra("url", url);
			intent.putExtra("type", type);
			context.startActivity(intent);
		}	
	}
}

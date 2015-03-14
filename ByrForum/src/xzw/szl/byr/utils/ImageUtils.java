package xzw.szl.byr.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.InputSource;

import pl.droidsonroids.gif.GifDrawable;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

/**
 * @author gologo 2014/07/30
 */
public class ImageUtils {
	

//	public static Bitmap getInternetImage(String imageUrl) {
//		
//		Bitmap bitmap = null;
//		try {
//			//URL url = new URL("http://nforum.byr.edu.cn/byr/img/face_default_f.jpg");
//			URL url = new URL(imageUrl);
//			
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			
//			conn.setDoInput(true);
//			conn.setRequestMethod("GET");
//			bitmap = BitmapFactory.decodeStream(url.openStream());
//			
//			conn.disconnect();
//			
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		return bitmap;
//	}

	public static void getInternetImage(String url) {
		
		if (url == null) return;
		HttpUtils.getBitmap(url);
	}
	public static Bitmap getSDImage(String url,int ww,int hh) {
		
		if (url == null) return null;
		if (!Environment.getExternalStorageState().
				equals(Environment.MEDIA_MOUNTED)) {
			return null;
		}
		
		int pos = url.lastIndexOf('/');
		if (pos == -1) return null;
		String filepath = null;
		if (url.startsWith("http://")) {
			filepath = url.substring(7);
		} else if (url.startsWith("https://")){
			filepath = url.substring(8);
		}
		filepath = filepath.replace('/', '_');
		
		File file = new File(Environment.getExternalStorageDirectory()+ ByrBase.PIC_DIR + filepath);
		if (!file.exists()) {
			//文件不存在
			return null;
		}
		
		BitmapFactory.Options options= new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
	
		BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + ByrBase.PIC_DIR + filepath, options);
		
		options.inSampleSize = calculateInSamplesSize(options, ww, ww);

			
		
		options.inInputShareable = true;
		options.inPurgeable = true;
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + ByrBase.PIC_DIR + filepath, options);
	}
	
	public static void saveImagetoSD(String url,Bitmap bitmap) {
		
		if (bitmap==null || url == null) return;
		int pos = url.lastIndexOf('/');
		if (pos == -1) return;
		String filepath = null;
		if (url.startsWith("http://")) {
			filepath = url.substring(7);
		} else if (url.startsWith("https://")){
			filepath = url.substring(8);
		}
		filepath = filepath.replace('/', '_');
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return;
		}
		File file = new File(Environment.getExternalStorageDirectory() + ByrBase.PIC_DIR);
		
		//文件目录不存在
		if (!file.exists()) {
			file.mkdir();
		}
		try {
			FileOutputStream out = new FileOutputStream(
					Environment.getExternalStorageDirectory() + ByrBase.PIC_DIR + filepath);
			
			bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
			out.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void downloadImagetoSD(InputStream in, String url) {
		
		if (in==null || url == null) return;
		int pos = url.lastIndexOf('/');
		if (pos == -1) return;
		String filepath = null;
		if (url.startsWith("http://")) {
			filepath = url.substring(7);
		} else if (url.startsWith("https://")){
			filepath = url.substring(8);
		}
		filepath = filepath.replace('/', '_');
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return;
		}
		File file = new File(Environment.getExternalStorageDirectory() + ByrBase.PIC_DIR);
		
		//文件目录不存在
		if (!file.exists()) {
			file.mkdir();
		}
		try {
			FileOutputStream out = new FileOutputStream(
					Environment.getExternalStorageDirectory() + ByrBase.PIC_DIR + filepath);
			
			byte[] bf = new byte[1024];
			int len = 0;
			
			while ((len = in.read(bf)) != -1) {
				out.write(bf, 0, len);
			}
			out.close();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	public static Bitmap compressImage(Bitmap image,int reqWidth,int reqHeight) {
		
		if (image == null) return null;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
		
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inJustDecodeBounds = false;
		options.outHeight = image.getHeight();
		options.outWidth = image.getWidth();
		
		if (reqHeight == 0 || reqWidth == 0) {
			options.inSampleSize = calculateInSamplesSize(options, reqWidth, reqHeight);
		}
		
		Bitmap bitmap = BitmapFactory.decodeStream(isBm,null, options);
		image.recycle();
		
		try {
			isBm.close();
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
	}
	
	public static boolean hasImage (String url) {
		
		if (url == null) return false;
		String filepath =  DataUtils.getPathFromUrl(url);
		return FileUtils.isFileExist(ByrBase.PIC_DIR + filepath);
	}
		
	public static Drawable getDrawableFromSD(String url) {
		
		if (url == null) return null;
		
		String filepath = Environment.getExternalStorageDirectory() + 
				ByrBase.PIC_DIR + DataUtils.getPathFromUrl(url);
			try {
				if (url.endsWith("gif")) {
					GifDrawable d =  new GifDrawable(filepath);
					d.setBounds(0, 0, d.getIntrinsicWidth()*2, d.getIntrinsicHeight()*2);
					return d;
				} else {
					BitmapDrawable d = new BitmapDrawable(null,filepath);
				//	d.setBounds(0, 0, right, bottom);
					return d;
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
	}
	
	
	public static int calculateInSamplesSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight ) {
		
		int height = options.outHeight;
		int width = options.outWidth;
		
		int inSampleSize = 1;
		
		if (height > reqHeight || width > reqWidth) {
			
			int hR = 0;
			if (reqHeight != 0)
				hR = Math.round((float)height /reqHeight);
			int wR = 0;
			if (reqWidth != 0)
				wR = Math.round((float) width/ reqWidth);
			if (hR > 0 && wR >0)
				inSampleSize = hR>wR?hR:wR;
			else if (hR > 0) {
				inSampleSize = hR;
			} else if (wR > 0) {
				inSampleSize = wR;
			}
		}
		return inSampleSize;
	}
	
	public static Bitmap compressImage(String filePath,int reqWidth,int reqHeight){
		
		if (filePath == null) return null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		BitmapFactory.decodeFile(filePath,options);
		
		options.inSampleSize = calculateInSamplesSize(options, reqWidth, reqHeight);
		
		options.inJustDecodeBounds = false;
		
		options.inInputShareable = true;
		options.inPurgeable = true;
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeFile(filePath, options);
	}
	
	public static Bitmap ZoomBitmap(Bitmap bitmap,int reqWidth,int reqHeight) {
		
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		float wR = Float.MAX_VALUE,hR =Float.MAX_VALUE;
		if (reqWidth != 0)
			wR = (float) reqWidth/width;
		if (reqHeight != 0) 
			hR = (float) reqHeight / height;
		
		float scale = wR>hR?hR:wR;
		
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
	}
	
	public static Bitmap compressUrlImage(String url,int reqWidth,int reqHeight){
		
		String filePath = DataUtils.getPathFromUrl(url);
		return compressImage(Environment.getExternalStorageDirectory() + ByrBase.PIC_DIR + filePath, reqWidth, reqHeight);
	}
}

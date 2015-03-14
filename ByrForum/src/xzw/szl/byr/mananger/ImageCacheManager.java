package xzw.szl.byr.mananger;
import java.util.LinkedList;
import java.util.Queue;

import xzw.szl.byr.utils.DataUtils;
import xzw.szl.byr.utils.ImageUtils;
import xzw.szl.byr.utils.NetStatus;
import xzw.szl.byr.utils.ViewUtils;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.widget.ImageView;


public enum ImageCacheManager {
	
	INSTANCE;
	
	private  LruCache<String, Bitmap> cache;
	private volatile Queue<Task> tasks;
//	private Map<String,Set<ImageAcquireListener>> imageMap;
	private boolean isRunning;
	
	private ImageCacheManager() {
		createImageCache();
		createTaskQueue();
	}
	
	public static ImageCacheManager getInstance () {
		return INSTANCE;
	}
	
	private void createImageCache() {
		
		int cacheSize = (int) Runtime.getRuntime().maxMemory()/32;
		
		cache = new LruCache<String, Bitmap>(cacheSize) {

			@Override
			protected int sizeOf(String key, Bitmap value) {
				
				//getByteCount is API12 or higher
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
					return value.getByteCount();
				else 
					return value.getRowBytes() * value.getHeight();
			}
			
		};
	}
	
	public static ImageAcquireListener getFaceImageAcquireListener (final ImageView imageView,final Handler handler) {
		
		return new ImageAcquireListener() {
			
			@Override
			public void onSuccess(final Bitmap bitmap) {
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						imageView.setImageBitmap(bitmap);
					}
				});
			}
			
			@Override
			public void onFailure() {

			}
		};
	}
	
	private void addBitmapToMemoryCache(String key,Bitmap bitmap) {
		if (cache.get(key) == null && bitmap != null) {
			cache.put(key, bitmap);
		}
	}
	
	private void createTaskQueue() {
		tasks = new LinkedList<Task>();
//		imageMap = new HashMap<String, Set<ImageAcquireListener>>();
	}
	
	
	public Bitmap getImageFromCache(String url) {
		if (url == null) return null;
		
		if (NetStatus.is2G3GNetwork() && !PrefernceManager.getInstance().can2G3GOnImageload) {
			return null;
		}
		
		Bitmap bitmap = cache.get(url);
		
		if (bitmap == null || bitmap.isRecycled()) {
			cache.remove(url);
			bitmap = ImageUtils.getSDImage(url,DataUtils.getDisplayValue(40),DataUtils.getDisplayValue(40));
			if (bitmap != null)
				addBitmapToMemoryCache(url, bitmap);
		}
		//从sd卡读取	   
		return bitmap;
	}
	
	public void startAcquireImage(String url,ImageAcquireListener imageAcquireListener) {
		
		if (!isRunning) {
			isRunning = true;
			ByrThreadPool.getTHreadPool().execute(r);
		}
		
		//get bitmap from cache
		Bitmap bitmap = getImageFromCache(url);
		
		if (bitmap != null) {
			imageAcquireListener.onSuccess(bitmap);
		} else {
			Task task = new Task();
			task.url = url;
			task.listener = imageAcquireListener;
		//	if (!tasks.contains(task)) {   //下载队列里没有
				synchronized (r) {
					tasks.add(task);
					r.notify();
				}
		//	} 
		}
	}
	
	public void stopAcquireImage() {
		
		synchronized (r) {
			isRunning = false;
			r.notify();
		}
	}
	
	private Runnable r = new Runnable() {
		
		@Override
		public void run() {
			
			while (isRunning) {
				
				while (!tasks.isEmpty()) {
					
					Task t = tasks.poll();
					
					Bitmap bitmap = getImageFromCache(t.url);
					
					if (bitmap != null) {
						t.listener.onSuccess(bitmap);
					}  else {
						// download image from Internet
						ImageUtils.getInternetImage(t.url);
						bitmap = ImageUtils.getSDImage(t.url, DataUtils.getDisplayValue(40), DataUtils.getDisplayValue(40));
						if (bitmap == null) {
							t.listener.onFailure();
							continue;
						}
						
					//	Bitmap bm = ImageUtils.compressImage(bitmap, 0, 0);
						addBitmapToMemoryCache(t.url, bitmap);
						t.listener.onSuccess(bitmap);
					}
				
				}
				
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						
						isRunning = false;
						e.printStackTrace();
					}
					
				}
			}
			
			tasks.clear();
		}
	};
	
	private class Task{
		String url;
		ImageAcquireListener listener;
		
		
		@Override
		public boolean equals(Object o) {
			
			if (o == null) return false;
			
			Task t = (Task)o;
			if (this.url.equals(t.url)) return true;
			return false;
		}
	}
	
	//清空cache
	public void clear() {
		isRunning = false;
		tasks.clear();
		cache.evictAll();
	}
	
	public interface ImageAcquireListener {
		
		// 获取图片成功
		void onSuccess(Bitmap bitmap);
		
		//获取图片失败
		void onFailure();
	} 
	
	
}




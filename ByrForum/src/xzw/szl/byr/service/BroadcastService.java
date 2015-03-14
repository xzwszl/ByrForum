package xzw.szl.byr.service;

import xzw.szl.byr.R;
import xzw.szl.byr.assist.MailActivity;
import xzw.szl.byr.assist.ReferActivity;
import xzw.szl.byr.info.Count;
import xzw.szl.byr.info.Mailbox;
import xzw.szl.byr.info.Refer;
import xzw.szl.byr.mananger.ByrThreadPool;
import xzw.szl.byr.mananger.PrefernceManager;
import xzw.szl.byr.utils.HttpUtils;
import xzw.szl.byr.utils.JsonUtils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

/**
 * broadcast 3 kinds of info ,mail,at,reply
 * 
 * @author gologo
 *
 */
public class BroadcastService extends Service{

	public static final String BR_INFO = "byr.info.broadcast";
	private Intent intent = new Intent(BR_INFO);
	
	private Handler mHanlder = new BroadHandler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Mailbox mb = (Mailbox) msg.obj;
				if (mb.isCan_send()) {
					intent.putExtra("type", "mail");
					String info = "";
					if (mb.isNew_mail()) {
						info +="新";
					}
					if (mb.isFull_mail()) {
						info +="满";
					}
					PrefernceManager.getInstance().updateCurrentBoxDes(getApplicationContext(), info);
					if (!info.equals("")) {
						intent.putExtra("info", info);
						sendBroadcast(intent);
						notificate("新文章提醒", 0);
					}
				}
				break;
			case 1:
				Count count = (Count) msg.obj;
				intent.putExtra("type", "at");
				PrefernceManager.getInstance().updateCurrentReferCount(getApplicationContext(), "at", count.getNew_count());
				if (count.getNew_count() > 0) {
					intent.putExtra("info", count.getNew_count()+"");
					sendBroadcast(intent);
					notificate("有" + count.getNew_count()+ "个新@",1);
				}
				break;
			case 2:
				count = (Count) msg.obj;
				intent.putExtra("type", "reply");
				PrefernceManager.getInstance().updateCurrentReferCount(getApplicationContext(), "reply", count.getNew_count());
				if (count.getNew_count() > 0) {
					intent.putExtra("info", count.getNew_count()+"");
					sendBroadcast(intent);
					notificate("有" + count.getNew_count()+ "个新回复",2);
				}
				break;
			default:
				break;
			}
		}
		
	};
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		mHanlder.post(r);
	}
	
	private Runnable r = new Runnable() {
		
		@Override
		public void run() {
			request("/mail/info.json","mail");
			request("/refer/at/info.json","at");
			request("/refer/reply/info.json","reply");
			mHanlder.postDelayed(this, 600000); 
		}
	};

	@Override
	public void onDestroy() {
		mHanlder.removeCallbacks(r);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void request(final String url,final String type) {
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				
				HttpUtils.httpRequest(url, new HttpUtils.HttpRequestListener() {
					
					@Override
					public void onSuccess(String content) {
						 
						if (type.equals("mail")) {
							Mailbox mb  = (Mailbox) JsonUtils.toBean(content, Mailbox.class);
							mHanlder.obtainMessage(0,mb).sendToTarget();
						} else {
							Count count = (Count) JsonUtils.toBean(content, Count.class);
							
							if (type.equals("at")) {
								mHanlder.obtainMessage(1,count).sendToTarget();
							} else {
								mHanlder.obtainMessage(2,count).sendToTarget();
							}
						}
					}
					
					@Override
					public void onFailed(String reason) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onError(Throwable e) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		};
		ByrThreadPool.getTHreadPool().execute(r);
	}
	
	static class BroadHandler extends Handler{
		
	};                            
	
	private void notificate(String content,int type) {
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.defaults|=Notification.DEFAULT_VIBRATE;
		notification.tickerText = content;
		
		Intent intent = null;
		String title = null;
		switch (type) {
		case 0:
			intent = new Intent(BroadcastService.this,MailActivity.class);
			title = "邮件";
			break;
		case 1:
			intent = new Intent(BroadcastService.this,ReferActivity.class);
			intent.putExtra("type", "at");
			title = "@我的的文章";
			break;
		case 2:
			intent = new Intent(BroadcastService.this,ReferActivity.class);
			intent.putExtra("type", "reply");
			title = "回复我的文章";
			break;
		default:
			break;
		
		}
		PendingIntent pendIntent = PendingIntent.getActivity(BroadcastService.this, type, intent, PendingIntent.FLAG_ONE_SHOT);
		notification.setLatestEventInfo(BroadcastService.this, title, content, pendIntent);
		nm.notify(type, notification);
	}
	
}

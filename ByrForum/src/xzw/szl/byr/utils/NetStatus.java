package xzw.szl.byr.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * @author Sony
 *判断但当前网络状态，2G / 3G / 4G / WIfi等
 */
public class NetStatus {	
	
	public static final int NETWORKTYPE_INVALID = 0;
	public static final int NETWORKTYPE_WAP = 1;
	public static final int NETWORKTYPE_2G = 2;
	public static final int NETWORKTYPE_3G = 3;
	public static final int NETWORKTYPE_4G = 4;
	public static final int NETWORKTYPE_WIFI = 5;
	
	public static int currentState = NETWORKTYPE_WIFI;
	//判断网络是否可用？
	public static boolean isNetworkAvailable(Context context) {
		
		ConnectivityManager connectivityManager = (ConnectivityManager)
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		
		return networkInfo != null && networkInfo.isConnected();
	}
	//判断网络类型

	public static int getCurrentNetworkType(Context context) {
		
		ConnectivityManager connectivityManager = (ConnectivityManager)
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		
		if (networkInfo == null) {
			return NETWORKTYPE_INVALID;
		} else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return NETWORKTYPE_WIFI;
		} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			
			//2G,3G,4G==
			switch (networkInfo.getSubtype()) {
				case TelephonyManager.NETWORK_TYPE_CDMA://电信2G
				case TelephonyManager.NETWORK_TYPE_GPRS://联通2G
				case TelephonyManager.NETWORK_TYPE_EDGE://移动2G
					return NETWORKTYPE_2G;
					
				case TelephonyManager.NETWORK_TYPE_UMTS: //联通3G
				case TelephonyManager.NETWORK_TYPE_HSDPA: //联通3G
				case TelephonyManager.NETWORK_TYPE_EVDO_0: //电信3G
				case TelephonyManager.NETWORK_TYPE_EVDO_A: //电信3G
				case TelephonyManager.NETWORK_TYPE_EVDO_B: //电信3G
					return NETWORKTYPE_3G;
					
				case TelephonyManager.NETWORK_TYPE_LTE: //准4G、3.9G以后还会更新
					return NETWORKTYPE_4G;
	
				default:
					return NETWORKTYPE_INVALID;
				}
			
		}
		
		return NETWORKTYPE_INVALID;
	}
	//判断是不是wifi网络
	
	public static boolean isWifiNetwork() {
		return currentState == NETWORKTYPE_WIFI;
	}
	
	public static boolean is2G3GNetwork() {
		return currentState == NETWORKTYPE_2G || currentState == NETWORKTYPE_3G;
	}
	
}

package xzw.szl.byr.mananger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class ByrThreadPool {
	
	private static ExecutorService byrThreadPool = null;
	private static final int SIZE  = 5;
	
	public static ExecutorService createByrThreadPoll() {
		int num = Runtime.getRuntime().availableProcessors();
		
		num = num >SIZE?num:SIZE;
		
		return Executors.newFixedThreadPool(num);
	}
	
	public synchronized static ExecutorService getTHreadPool() {
		
		if (byrThreadPool == null) {
			byrThreadPool = createByrThreadPoll();
		}
		return byrThreadPool;
	}
	
	public synchronized static void close() {
		
		if (byrThreadPool != null) {
			byrThreadPool.shutdown();
		}
	} 
}

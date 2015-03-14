package xzw.szl.byr.mananger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class ByrThreadPool {
	
	private static final ExecutorService byrThreadPool = createByrThreadPoll();
	private static final int SIZE  = 5;
	
	public static ExecutorService createByrThreadPoll() {
		int num = Runtime.getRuntime().availableProcessors();
		
		num = num >SIZE?num:SIZE;
		
		return Executors.newFixedThreadPool(num);
	}
	
	public static ExecutorService getTHreadPool() {
		return byrThreadPool;
	}
}

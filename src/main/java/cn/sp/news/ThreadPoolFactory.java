package cn.sp.news;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolFactory {

	private static ThreadPoolExecutor threadPool;

	private ThreadPoolFactory() {
	}

	public static synchronized ThreadPoolExecutor getThreadPool() {
		if (threadPool == null) {
			String _corePoolSize = "10";
			String _maximumPoolSize = "15";
			String _keepAliveTime = "1000";
			String _workQueue = "5";
			threadPool = new ThreadPoolExecutor(Integer.parseInt(_corePoolSize), Integer.parseInt(_maximumPoolSize),
					Integer.parseInt(_keepAliveTime), TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(
							Integer.parseInt(_workQueue)), new ThreadPoolExecutor.DiscardOldestPolicy());
		}
		return threadPool;
	}

	public static void execute(Runnable r) {
		getThreadPool().execute(r);
	}
}
package com.utlife.core.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 业务处理管理类，线程池处理所有请求、耗时操作
 */
public class ThreadManager {

	private static volatile ThreadManager instance = null;
	// 线程池
	private ExecutorService pool;

	// private static final int POOL_SIZE = 5;

	public ThreadManager() {
		pool = new ThreadPoolExecutor(2, 5, 1000L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
	}

	public static ThreadManager getInstance() {
		if (instance == null) {
			synchronized (ThreadManager.class) {
				if (instance == null) {
					instance = new ThreadManager();
				}
			}
		}
		return instance;
	}

	public void start(Runnable runnable) {
		if (runnable != null) {
			pool.execute(runnable);
		}
	}

	public Future<?> submitTask(Runnable task) {
		if (task != null) {
			return pool.submit(task);
		}

		return null;
	}

	public void stop() {
		if (instance == null) {
			return;
		} else {
			synchronized (ThreadManager.class) {
				if (instance.pool != null) {
					instance.pool.shutdown();// 关闭线程池
					instance = null;
				}
			}
		}
	}
}

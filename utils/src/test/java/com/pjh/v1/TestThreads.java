package com.pjh.v1;

import java.util.HashMap;
import java.util.concurrent.*;

/**
 * 
 * @author 潘江辉
 *
 */
public class TestThreads implements Callable<HashMap> {
	private HashMap map;
	private final static int treadNum = 4;

	public TestThreads() {
		// TODO Auto-generated constructor stub
	}

	public TestThreads(HashMap inMap) {
		// TODO Auto-generated constructor stub
		this.map = inMap;
	}

	@Override
	public HashMap call() throws Exception {
		// TODO Auto-generated method stub
		HashMap map = new HashMap();
		map.put(Thread.currentThread().getName(), System.currentTimeMillis());
		System.out.println(this.map);
		return map;
	}

	public static void main(String[] args) {
		ExecutorService excutorService = Executors.newFixedThreadPool(treadNum);
		FutureTask<HashMap> futureTask = null;
		try {
			HashMap m ;
			for (int i = 0; i < 100; i++) {
				m = new HashMap();
				m.put("key", i);
				futureTask = new FutureTask<HashMap>(new TestThreads(m));
				excutorService.submit(futureTask);
				HashMap HashMap = futureTask.get();
//				System.out.println("结果：" + HashMap);
			}
//			excutorService.shutdown();
//			while(excutorService.awaitTermination(2, TimeUnit.SECONDS));
			System.out.println(excutorService.awaitTermination(2, TimeUnit.SECONDS));
			System.out.println("执行完成");

		}
		catch (Exception e) {
			// TODO: handle exception
			futureTask.cancel(true);
			System.out.println("异常");
			e.printStackTrace();
		}
		finally {
			excutorService.shutdown();
		}
	}
}

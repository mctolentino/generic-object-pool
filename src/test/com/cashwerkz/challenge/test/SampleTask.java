package com.cashwerkz.challenge.test;

import com.cashwerkz.challenge.ObjectPool;

public class SampleTask implements Runnable {
	
	private ObjectPool<SamplePoolObject> pool;
	private String taskName;
	
	public SampleTask(ObjectPool<SamplePoolObject> pool, String taskName){
		this.pool = pool;
		this.taskName = taskName;
	}

	@Override
	public void run() {
		int i = 0;
		while(i < 5){
			SamplePoolObject poolObject = pool.acquireObject();
			System.out.println("Task: " + taskName + " Acquired: " + poolObject + " In-Use:" + pool.getInUseCounter()  + " In-Pool:" + pool.getPoolSize());
			
			
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			pool.returnObject(poolObject);
			System.out.println("Task: " + taskName + " Returned: " + poolObject);
			i++;
		}
	}

}

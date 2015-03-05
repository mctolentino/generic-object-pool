package com.cashwerkz.challenge;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ObjectPool<T> {

	private ConcurrentLinkedQueue<T> pool;
	private AtomicInteger inUseCounter = new AtomicInteger(0);

	private final int initial;
	private final int threshold;
	private final int growth;
	private final int ceiling;
	private final Class<T> type;

	private ObjectPool(PoolBuilder<T> builder) {
		pool = builder.pool;
		initial = builder.initial;
		threshold = builder.threshold;
		growth = builder.growth;
		ceiling = builder.ceiling;
		type = builder.type;

		for (int i = 0; i < initial; i++) {
			pool.add(createGenericObject());
		}
	}

	public static class PoolBuilder<T> {

		private ConcurrentLinkedQueue<T> pool;
		private int initial = 5;
		private int threshold = 2;
		private int growth = 5;
		private int ceiling = 10;
		private Class<T> type;

		public PoolBuilder(Class<T> type) {
			this.type = type;
			pool = new ConcurrentLinkedQueue<T>();
		}

		public PoolBuilder<T> initial(int value) {
			initial = value;
			return this;
		}

		public PoolBuilder<T> threshold(int value) {
			threshold = value;
			return this;
		}

		public PoolBuilder<T> growth(int value) {
			growth = value;
			return this;
		}

		public PoolBuilder<T> ceiling(int value) {
			ceiling = value;
			return this;
		}

		public ObjectPool<T> build() {
			return new ObjectPool<T>(this);
		}

	}

	private T createGenericObject() {
		T object = null;
		try {
			object = type.newInstance();
			System.out.println(" Create Pooled Object: " + object);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return object;
	}

	public T acquireObject() {
		
		if( pool.size()-1 + inUseCounter.get() >= ceiling ){
			throw new IllegalStateException("Max Objects in Pool Reached. \n"
					+ "PoolSize : " + pool.size() + " inUseCounter: "
					+ inUseCounter.get() + " ceiling: " + ceiling);
		}
		
		T genericObject = pool.remove();
		inUseCounter.incrementAndGet();
	
		if (pool.size() == threshold
				&& (pool.size() + inUseCounter.get() <= ceiling)) {
			
			for (int i = 0; i < growth && (pool.size() + inUseCounter.get() < ceiling); i++) {
				pool.add(createGenericObject());
				System.out.println("BPoolSize: " + pool.size() + " inUseCounter: " + inUseCounter.get() + " Ceiling: " + ceiling);
			}
		} 

		return genericObject;
	}

	public void returnObject(T genericObject) {
		//System.out.println("Returned: " + genericObject + " inUseCounter: " + inUseCounter.decrementAndGet() + " PooledSize: " + pool.size());
		inUseCounter.decrementAndGet();
		pool.add(genericObject);
	}
	
	

	public int getInUseCounter() {
		return inUseCounter.get();
	}

	public boolean contains(T object) {
		return pool.contains(object);
	}

	public ConcurrentLinkedQueue<?> getPool() {
		return pool;
	}

	public int getPoolSize() {
		return pool.size();
	}

	public int getInitial() {
		return initial;
	}

	public int getThreshold() {
		return threshold;
	}

	public int getGrowth() {
		return growth;
	}

	public int getCeiling() {
		return ceiling;
	}

}

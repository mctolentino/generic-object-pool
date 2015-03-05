package com.cashwerkz.challenge;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ObjectPool<T> {

	private Queue<T> pool;
	private AtomicInteger inUseCounter = new AtomicInteger(0);

	private final int initial;
	private final int threshold;
	private final int growth;
	private final int ceiling;

	public ObjectPool(int initial, int threshold, int growth, int ceiling) {
		pool = new ConcurrentLinkedQueue<T>();
		this.initial = initial;
		this.threshold = threshold;
		this.growth = growth;
		this.ceiling = ceiling;

		initializePoolObjects(initial);
	}

	private void initializePoolObjects(int initialNumberOfObjects) {
		for (int i = 0; i < initialNumberOfObjects; i++) {
			pool.add(createGenericObject());
		}
	}

	protected abstract T createGenericObject(); 
	
	// side effects: growPoolIfThresholdReached()
	public synchronized T acquireObject() {
		T genericObject = null;

		if (!pool.isEmpty() && currentNumberOfObjects() <= ceiling) {
			genericObject = pool.remove();
			inUseCounter.incrementAndGet();
			growPoolIfThresholdReached();
			
		} else {
			throw new IllegalStateException("Max Objects in Pool Reached. \n"
					+ "PoolSize : " + pool.size() + " inUseCounter: "
					+ inUseCounter.get() + " ceiling: " + ceiling);
		}

		return genericObject;
	}

	private synchronized void growPoolIfThresholdReached() {
		// Max created objects should not exceed ceiling
		if (pool.size() <= threshold) {		
			for (int i = 0; i < growth && (currentNumberOfObjects() < ceiling); i++) {
				pool.add(createGenericObject());		
			}		
		}
	}

	private synchronized int currentNumberOfObjects() {
		return pool.size() + inUseCounter.get();
	}

	public synchronized void returnObject(T genericObject) {
		if (genericObject != null) {
			inUseCounter.decrementAndGet();
			pool.add(genericObject);
		}
	}

	public int getInUseCounter() {
		return inUseCounter.get();
	}

	public boolean contains(T object) {
		return pool.contains(object);
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

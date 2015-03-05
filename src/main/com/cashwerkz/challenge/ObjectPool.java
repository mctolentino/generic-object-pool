package com.cashwerkz.challenge;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ObjectPool<T> {

	private Queue<T> pool;
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

		initializePoolObjects(initial);
	}

	private void initializePoolObjects(int initialNumberOfObjects) {
		for (int i = 0; i < initialNumberOfObjects; i++) {
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
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return object;
	}

	// side effects: growPoolIfThresholdReached()
	public T acquireObject() {
		T genericObject = null;

		if (!pool.isEmpty()) {
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

	private void growPoolIfThresholdReached() {
		// Max created objects should not exceed ceiling
		if (pool.size() == threshold && (currentNumberOfObjects() <= ceiling)) {

			for (int i = 0; i < growth && (currentNumberOfObjects() < ceiling); i++) {
				pool.add(createGenericObject());
			}
		}
	}

	private int currentNumberOfObjects() {
		return pool.size() + inUseCounter.get();
	}

	public void returnObject(T genericObject) {
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

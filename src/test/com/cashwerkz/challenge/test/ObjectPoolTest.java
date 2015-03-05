package com.cashwerkz.challenge.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.cashwerkz.challenge.ObjectPool;

public class ObjectPoolTest {

	private ObjectPool<SamplePoolObject> pool;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		// int initial, int threshold, int growth, int ceiling
		pool = new ObjectPool<SamplePoolObject>(6, 4, 3, 10) {
			protected SamplePoolObject createGenericObject() {
				return new SamplePoolObject();
			}
		};
	}

	@Test
	public void testCreatePool() {
		assertNotNull(pool);
		assertEquals(pool.getPoolSize(), pool.getInitial());
		assertEquals(6, pool.getInitial());
		assertEquals(4, pool.getThreshold());
		assertEquals(3, pool.getGrowth());
		assertEquals(10, pool.getCeiling());
	}

	@Test
	public void testAcquireObjectFromPool() {
		SamplePoolObject poolObject = pool.acquireObject();
		assertFalse(pool.contains(poolObject));
	}

	@Test
	public void testReturnObjectToPool() {
		SamplePoolObject poolObject = (SamplePoolObject) pool.acquireObject();
		assertEquals(pool.getInitial() - 1, pool.getPoolSize());
		assertFalse(pool.contains(poolObject));

		pool.returnObject(poolObject);
		assertTrue(pool.contains(poolObject));
		assertEquals(pool.getInitial(), pool.getPoolSize());
	}

	@Test
	public void testIncreasePoolSizeByGrowthRateIfThresholdIsReached() {
		int initialPoolSizeBeforeGrowth = pool.getPoolSize();
		assertEquals(pool.getThreshold() + 2, initialPoolSizeBeforeGrowth);

		// Triggers threshold value
		pool.acquireObject();
		pool.acquireObject(); // expect pool size (4) + growth (3) = 7,
								// inUseCounter = 2
		assertEquals(7, pool.getPoolSize());
		assertEquals(2, pool.getInUseCounter());
		
		// Should stop adding objects if number of objects equals ceiling value
		// Trigger second threshold
		pool.acquireObject();
		pool.acquireObject();
		pool.acquireObject();
		// should only grow the pool upto ceiling value
		
		assertEquals(5, pool.getPoolSize());
		assertEquals(5, pool.getInUseCounter());		
	}

	@Test
	public void testThrowExceptionIfCeilingParameterIsExceeded() {
		// Use up all available objects in the Pool
		SamplePoolObject poolObject = pool.acquireObject();
		poolObject = pool.acquireObject();
		poolObject = pool.acquireObject();
		poolObject = pool.acquireObject();
		poolObject = pool.acquireObject();
		poolObject = pool.acquireObject();
		poolObject = pool.acquireObject();
		poolObject = pool.acquireObject();
		poolObject = pool.acquireObject();
		poolObject = pool.acquireObject();
		pool.returnObject(poolObject);
		poolObject = pool.acquireObject();

		assertEquals(0, pool.getPoolSize());
		assertEquals(pool.getCeiling(), pool.getInUseCounter());
		// Current PoolSize + Current InUseCounter should be equal to Ceiling
		// Value
		assertEquals(pool.getCeiling(),
				pool.getPoolSize() + pool.getInUseCounter());

		exception.expect(IllegalStateException.class);
		exception.expectMessage("Max Objects in Pool Reached.");
		poolObject = (SamplePoolObject) pool.acquireObject();
	}

	@Test
	public void testUsePoolByDifferentThreads() throws InterruptedException {
		
		System.out.println("Number of Pooled Objects Before Execution: " + pool.getPoolSize());
		
		// make 11 runnable tasks which acquires 5 pool objects, hold each
		// object, then return to pool.
		// 11th thread reaches ceiling count.
		ExecutorService executor = Executors.newFixedThreadPool(11);

		executor.execute(new SampleTask(pool, "1"));
		executor.execute(new SampleTask(pool, "2"));
		executor.execute(new SampleTask(pool, "3"));
		executor.execute(new SampleTask(pool, "4"));
		executor.execute(new SampleTask(pool, "5"));
		executor.execute(new SampleTask(pool, "6"));
		executor.execute(new SampleTask(pool, "7"));
		executor.execute(new SampleTask(pool, "8"));
		executor.execute(new SampleTask(pool, "9"));
		executor.execute(new SampleTask(pool, "10"));

		executor.execute(new SampleTask(pool, "11"));

		executor.shutdown();
		executor.awaitTermination(2, TimeUnit.MINUTES);

		System.out.println("\n Print All Pooled Objects: " + pool.getPoolSize());
		int size = pool.getPoolSize();
		for (int i = 0; i < size; i++) {
			System.out.println(pool.acquireObject());
		}
	}

}

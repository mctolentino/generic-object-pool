package com.cashwerkz.challenge.test;

import junit.framework.TestCase;

import org.junit.Test;

import com.cashwerkz.challenge.ObjectPool;

public class ObjectPoolTest extends TestCase {
	
	ObjectPool<SamplePoolObject> pool;
	
	public void setUp(){
		pool = new ObjectPool.PoolBuilder<SamplePoolObject>(SamplePoolObject.class).initial(5).threshold(4).growth(3).ceiling(9).build();
	}

	public void testCreatePool() {
		assertNotNull(pool);
		assertEquals(pool.getPoolSize(), pool.getInitial());
		assertEquals(5, pool.getInitial());
		assertEquals(4, pool.getThreshold());
		assertEquals(3, pool.getGrowth());
		assertEquals(9, pool.getCeiling());
		System.out.println("END");
	}
	
	public void testAcquireObjectFromPool(){
		SamplePoolObject poolObject = pool.acquireObject();
		assertFalse(pool.contains(poolObject));
		System.out.println("END");
	}
	
	public void testReturnObjectToPool(){
		SamplePoolObject poolObject = (SamplePoolObject)pool.acquireObject();
		assertFalse(pool.contains(poolObject));
		pool.returnObject(poolObject);
		assertTrue(pool.contains(poolObject));
		System.out.println("END");
	}
	
	public void testIncreasePoolSizeByGrowthRateIfThresholdIsReached(){
		int initialPoolSizeBeforeGrowth = pool.getPoolSize();
		assertEquals(pool.getThreshold()+1, initialPoolSizeBeforeGrowth);
		// Triggers threshold value
		SamplePoolObject poolObject = (SamplePoolObject)pool.acquireObject();
		assertEquals(pool.getPoolSize(), pool.getThreshold() + pool.getGrowth());
		System.out.println("END: " + pool.getPoolSize());
		
	}

	
	public void testThrowExceptionIfCeilingParameterIsExceeded(){
		int initialPoolSizeBeforeGrowth = pool.getPoolSize();
		assertEquals(pool.getThreshold()+1, initialPoolSizeBeforeGrowth);
		// Triggers threshold value (4)
		SamplePoolObject poolObject1 = (SamplePoolObject)pool.acquireObject();
		SamplePoolObject poolObject2 = (SamplePoolObject)pool.acquireObject();
		SamplePoolObject poolObject3 = (SamplePoolObject)pool.acquireObject();
		SamplePoolObject poolObject4 = (SamplePoolObject)pool.acquireObject();
		assertEquals(pool.getPoolSize() + pool.getInUseCounter(), pool.getCeiling() );
		SamplePoolObject poolObject5 = (SamplePoolObject)pool.acquireObject();
		pool.returnObject(poolObject5);
		pool.returnObject(poolObject5);
	
	}
	
	

}



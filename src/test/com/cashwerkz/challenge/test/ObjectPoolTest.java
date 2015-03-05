package com.cashwerkz.challenge.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
	public void setUp(){
		pool = new ObjectPool.PoolBuilder<SamplePoolObject>(SamplePoolObject.class)
									.initial(6)
									.threshold(4)
									.growth(3)
									.ceiling(10)
									.build();
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
	public void testAcquireObjectFromPool(){
		SamplePoolObject poolObject = pool.acquireObject();
		assertFalse(pool.contains(poolObject));
	}
	
	@Test
	public void testReturnObjectToPool(){
		SamplePoolObject poolObject = (SamplePoolObject)pool.acquireObject();
		assertEquals(pool.getInitial()-1, pool.getPoolSize());
		assertFalse(pool.contains(poolObject));
		pool.returnObject(poolObject);
		assertTrue(pool.contains(poolObject));
		assertEquals(pool.getInitial(), pool.getPoolSize());
		
	}
	
	@Test
	public void testIncreasePoolSizeByGrowthRateIfThresholdIsReached(){
		int initialPoolSizeBeforeGrowth = pool.getPoolSize();
		assertEquals(pool.getThreshold()+2, initialPoolSizeBeforeGrowth);
		// Triggers threshold value
		pool.acquireObject();
		pool.acquireObject(); // expect pool size (4) + growth (3) = 7, inUseCounter = 2
		assertEquals(7, pool.getPoolSize());
		assertEquals(2, pool.getInUseCounter());
	}

	@Test
	public void testThrowExceptionIfCeilingParameterIsExceeded(){
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
		// Current PoolSize + Current InUseCounter should be equal to Ceiling Value
		assertEquals(pool.getCeiling(), pool.getPoolSize() + pool.getInUseCounter());
		
		exception.expect(IllegalStateException.class);
		exception.expectMessage("Max Objects in Pool Reached.");
		poolObject = (SamplePoolObject)pool.acquireObject();
	}
	
	

}



An Object Pool is used to reduce the overhead of creating and instantiating a new object. Usually it's used to boost performance for objects that are heavy on resources like database, streams and sockets connections.

Concurrency is usually the issue with shared resources.

The main class that implements the generic object pool is ObjectPool.
I picked a queue that is thread-safe (ConcurrentLinkedQueue) to save the generic pooled objects. It is an abstract class, to delegate the createGenericObject method to its implementing class. My initial implementation of the createGenericObject method was a bit hacky, and it feels cleaner using this abstraction (you may check out the log in my github account.)

Main Classes
com.cashwerkz.challenge.ObjectPool

Test Classes
com.cashwerkz.challenge.test.ObjectPoolTest
- This is the main test class. Test cases:
 	public void testCreatePool()
	public void testAcquireObjectFromPool()
	public void testReturnObjectToPool()
	public void testIncreasePoolSizeByGrowthRateIfThresholdIsReached()
	public void testThrowExceptionIfCeilingParameterIsExceeded()
	public void testUsePoolByDifferentThreads() throws InterruptedException

com.cashwerkz.challenge.test.SamplePoolObject
- Sample object with an overriden toString() method, for easier and clearer debugging.

com.cashwerkz.challenge.test.SampleTask
- A runnable task, created to mimic separate processes that requests from the object pool.


Take-aways:
Always include test cases for concurrency. My initial implementations that looked good when run in a single thread, broke when faced with multiple concurrent processes.

	

An *Object Pool* is used to reduce the overhead of creating and instantiating a new object. Usually it's used to boost performance for objects that are heavy on resources like database, streams and sockets connections.

Concurrency is usually a major concern with these shared resources.


### Main Classes
- com.cashwerkz.challenge.ObjectPool
  - The main class that implements the generic object pool is ObjectPool.
  - A queue that is thread-safe (ConcurrentLinkedQueue) was used to save the generic pooled objects. 
  - This is an abstract class, and it delegates the createGenericObject method to the implementing class. 
  - Initial implementation of the createGenericObject method was a bit hacky (using newInstance()). Making the method abstract feels a bit cleaner. (You may check logs for previous implementation)

### Test Classes
- com.cashwerkz.challenge.test.ObjectPoolTest
 - This is the main test class. Test cases:
  - public void testCreatePool()
  - public void testAcquireObjectFromPool()
  - public void testReturnObjectToPool()
  - public void testIncreasePoolSizeByGrowthRateIfThresholdIsReached()
  - public void testThrowExceptionIfCeilingParameterIsExceeded()
  - public void testUsePoolByDifferentThreads() throws InterruptedException

- com.cashwerkz.challenge.test.SamplePoolObject
 - Sample object with an overriden toString() method, for easier and clearer debugging.

- com.cashwerkz.challenge.test.SampleTask
 - A runnable task, created to mimic separate processes that requests from the object pool.


### Take-aways
- Always include test cases for concurrency. My initial implementations that are running in a single thread, broke when faced with multiple concurrent processes. *Synchronized* methods saved the day.

	

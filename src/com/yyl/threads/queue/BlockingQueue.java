package com.yyl.threads.queue;

/**
 * ArrayBlockingQueue ：由数组结构组成的有界阻塞队列。
 * LinkedBlockingQueue ：由链表结构组成的有界阻塞队列。
 * PriorityBlockingQueue ：支持优先级排序的无界阻塞队列。
 * DelayQueue：使用优先级队列实现的无界阻塞队列。
 * SynchronousQueue：不存储元素的阻塞队列。
 * LinkedTransferQueue：由链表结构组成的无界阻塞队列。
 * LinkedBlockingDeque：由链表结构组成的双向阻塞队列。
 * <p>
 * <p>
 * <p>
 * <p>
 * BlockingQueue的两个常见阻塞场景：
 * 1、当队列中没有数据的情况下，消费者端的所有线程都会被自动阻塞（挂起），直到有数据放入队列。
 * 2、当队列中填满数据的情况下，生产者端的所有线程都会被自动阻塞（挂起），直到队列中有空闲的位置，线程被自动唤醒。
 * 这也是我们在多线程环境下，为什么需要BlockingQueue的原因。作为BlockingQueue的使用者，我们再也不需要关心什么时候需要阻塞线程，什么时候需要唤醒线程，因为这一切BlockingQueue都给你一手包办了。
 * <p>
 * <p>
 * BlockingQueue成员详细介绍
 * 1. ArrayBlockingQueue
 * 基于数组的阻塞队列实现，在ArrayBlockingQueue内部，维护了一个定长数组，以便缓存队列中的数据对象，这是一个常用的阻塞队列，除了一个定长数组外，
 * ArrayBlockingQueue内部还保存着两个整形变量，分别标识着队列的头部和尾部在数组中的位置。
 * <p>
 * ArrayBlockingQueue在生产者放入数据和消费者获取数据，都是共用同一个锁对象，由此也意味着两者无法真正并行运行，这点尤其不同于LinkedBlockingQueue；
 * 按照实现原理来分析，ArrayBlockingQueue完全可以采用分离锁，从而实现生产者和消费者操作的完全并行运行。Doug Lea之所以没这样去做，也许是因为ArrayBlockingQueue的数据写入和获取操作已经足够轻巧，
 * 以至于引入独立的锁机制，除了给代码带来额外的复杂性外，其在性能上完全占不到任何便宜。 ArrayBlockingQueue和LinkedBlockingQueue间还有一个明显的不同之处在于，
 * 前者在插入或删除元素时不会产生或销毁任何额外的对象实例，而后者则会生成一个额外的Node对象。这在长时间内需要高效并发地处理大批量数据的系统中，其对于GC的影响还是存在一定的区别。
 * 而在创建ArrayBlockingQueue时，我们还可以控制对象的内部锁是否采用公平锁，默认采用非公平锁。
 * <p>
 * <p>
 * <p>
 * <p>
 * 2. LinkedBlockingQueue
 * 基于链表的阻塞队列，同ArrayListBlockingQueue类似，其内部也维持着一个数据缓冲队列（该队列由一个链表构成），当生产者往队列中放入一个数据时，队列会从生产者手中获取数据，并缓存在队列内部，
 * 而生产者立即返回；只有当队列缓冲区达到最大值缓存容量时（LinkedBlockingQueue可以通过构造函数指定该值），才会阻塞生产者队列，直到消费者从队列中消费掉一份数据，生产者线程会被唤醒，
 * 反之对于消费者这端的处理也基于同样的原理。而LinkedBlockingQueue之所以能够高效的处理并发数据，还因为其对于生产者端和消费者端分别采用了独立的锁来控制数据同步，
 * 这也意味着在高并发的情况下生产者和消费者可以并行地操作队列中的数据，以此来提高整个队列的并发性能。
 * <p>
 * <p>
 * <p>
 * <p>
 * 作为开发者，我们需要注意的是，如果构造一个LinkedBlockingQueue对象，而没有指定其容量大小，LinkedBlockingQueue会默认一个类似无限大小的容量（Integer.MAX_VALUE），这样的话，
 * 如果生产者的速度一旦大于消费者的速度，也许还没有等到队列满阻塞产生，系统内存就有可能已被消耗殆尽了。
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * ArrayBlockingQueue和LinkedBlockingQueue是两个最普通也是最常用的阻塞队列，一般情况下，在处理多线程间的生产者消费者问题，使用这两个类足以。
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * ArrayBlockingQueue和LinkedBlockingQueue的区别
 * 1.队列中锁的实现不同
 * ArrayBlockingQueue实现的队列中的锁是没有分离的，即生产和消费用的是同一个锁；
 * LinkedBlockingQueue实现的队列中的锁是分离的，即生产用的是putLock，消费用的是takeLock
 * <p>
 * 2.在生产或消费时操作不同
 * ArrayBlockingQueue实现的队列中在生产和消费的时候，是直接将枚举对象插入或移除的；
 * LinkedBlockingQueue实现的队列中在生产和消费的时候，需要把枚举对象转换为Node<E>进行插入或移除，会影响性能
 * <p>
 * 3.队列大小初始化方式不同
 * ArrayBlockingQueue实现的队列中必须指定队列的大小；
 * LinkedBlockingQueue实现的队列中可以不指定队列的大小，但是默认是Integer.MAX_VALUE
 * <p>
 * <p>
 * 注意:
 * 1. 在使用LinkedBlockingQueue时，若用默认大小且当生产速度大于消费速度时候，有可能会内存溢出
 * 2. 在使用ArrayBlockingQueue和LinkedBlockingQueue分别对10,00000个简单字符做入队操作时，LinkedBlockingQueue的消耗是ArrayBlockingQueue消耗的10倍左右，
 * 即LinkedBlockingQueue消耗在1500毫秒左右，而ArrayBlockingQueue只需150毫秒左右。
 */
public class BlockingQueue {
}

#多线程

#####进程和线程的区别
一个程序至少有一个进程,一个进程至少有一个线程.\
进程在执行过程中拥有独立的内存单元，而多个线程共享内存，从而极大地提高了程序的运行效率。\
进程是系统进行资源分配和调度的一个独立单位.\
线程是进程的一个实体,是CPU调度和分派的基本单位
####lock原理


####lock和synchronized两者的区别？
```
1.性能不一致：资源竞争激励的情况下，lock性能会比synchronize好，竞争不激励的情况下，synchronize比lock性能好。
2.锁机制不一样：synchronize是在JVM层面实现的，系统会监控锁的释放与否。lock是代码实现的，
    需要手动释放，在finally块中释放。可以采用非阻塞的方式获取锁。
3.用法不一样：synchronize可以用在代码块上，方法上。lock通过代码实现，有更精确的线程语义。


synchronized锁住的是括号里的对象，而不是代码。对于非static的synchronized方法，锁的就是对象本身也就是this。
当synchronized锁住一个对象后，别的线程如果也想拿到这个对象的锁，就必须等待这个线程执行完成释放锁，
才能再次给对象加锁，这样才达到线程同步的目的。即使两个不同的代码段，都要锁同一个对象，那么这两个代码段也不能在多线程环境下同时运行。

所以我们在用synchronized关键字的时候，能缩小代码段的范围就尽量缩小，能在代码段上加同步就不要再整个方法上加同步。
这叫减小锁的粒度，使代码更大程度的并发
```
#### synchronized 和 static synchronized区别
````
在同一个对像中synchronized (this) 中的this是对像的锁

如果synchronized中的this加锁后，另一个线程要等待synchronized方法中的this解锁，获取到this锁才能向下执行
总结：一个对像中的所有synchronized方法是同步执行的，因为使用的是this锁    两个对像同时执行synchronized方法是不会同步的
因为synchronized的锁是对像自已，当有一个方法锁定后其它方法要等待第一个方法解锁后 获取this的锁 

static synchronized方法，static方法可以直接类名加方法名调用，方法中无法使用this，
    所以它锁的不是this，而是类的Class对象，所以，static synchronized方法也相当于全局锁，相当于锁住了代码段。
总结：static synchronized 所有对像同步执行这个方法,因为使用的是.class锁 

````
####volatile关键字使用规则
volatile提供内存可见性和禁止内存重排序\
volatile使用场景：\
1.对变量的写操作不依赖当前值，如多线程下执行a++，是无法通过volatile保证结果准确性的\
2.该变量没有包含在具有其他变量的不变式中
####多次start一个线程会怎么样
第二次调用start()方法的时候，线程可能处于终止或者其他（非NEW）状态，但是不论如何，都是不可以再次启动的
####线程有哪些状态
``````
线程生命周期
新建（NEW），表示线程被创建出来还没真正启动的状态，可以认为它是个Java内部状态。
就绪（RUNNABLE），表示该线程已经在JVM中执行，当然由于执行需要计算资源，它可能是正在运行，也可能还在等待系统分配给它CPU片段，在就绪队列里面排队。
    在其他一些分析中，会额外区分一种状态RUNNING，但是从Java API的角度，并不能表示出来。
阻塞（BLOCKED），这个状态和我们前面两讲介绍的同步非常相关，阻塞表示线程在等待Monitor lock。
    比如，线程试图通过synchronized去获取某个锁，但是其他线程已经独占了，那么当前线程就会处于阻塞状态。
等待（WAITING），表示正在等待其他线程采取某些操作。一个常见的场景是类似生产者消费者模式，
    发现任务条件尚未满足，就让当前消费者线程等待（wait），另外的生产者线程去准备任务数据，
    然后通过类似notify等动作，通知消费线程可以继续工作了。Thread.join()也会令线程进入等待状态。
计时等待（TIMED_WAIT），其进入条件和等待状态类似，但是调用的是存在超时条件的方法，比如wait或join等方法的指定超时版本
终止（TERMINATED）
``````
####Thread和Runnable的区别和联系
一是直接继承Thread类，二是实现Runnable接口
Thread实现了Runnable接口并进行了扩展，我们通常拿来进行比较只是写法上的比较，
而Thread和Runnable的实质是实现的关系
效果上没区别，写法上的区别而已。
####synchronized和ReentrantLock的区别
````
    synchronized同步锁 
         synchronized属于悲观锁，直接对区域或者对象加锁，性能稳定，可以使用大部分场景。
    ReentrantLock可重入锁（Lock接口） 
        1. 相对于synchronized更加灵活，可以控制加锁和放锁的位置
        2. 可以使用Condition来操作线程，进行线程之间的通信
        3.  核心类AbstractQueuedSynchronizer，通过构造一个基于阻塞的CLH队列容纳所有的阻塞线程，
            而对该队列的操作均通过Lock-Free（CAS）操作，但对已经获得锁的线程而言，ReentrantLock实现了偏向锁的功能。
    ReentrantReadWriteLock可重入读写锁（ReadWriteLock接口） 
         1. 相对于ReentrantLock，对于大量的读操作，读和读之间不会加锁，只有存在写时才会加锁，但是这个锁是悲观锁
         2. ReentrantReadWriteLock实现了读写锁的功能
         3. ReentrantReadWriteLock是ReadWriteLock接口的实现类。ReadWriteLock接口的核心方法是readLock()，writeLock()。
            实现了并发读、互斥写。但读锁会阻塞写锁，是悲观锁的策略。
    StampedLock戳锁 
        1.  ReentrantReadWriteLock虽然解决了大量读取的效率问题，但是，由于实现的是悲观锁，当读取很多时，
            读取和读取之间又没有锁，写操作将无法竞争到锁，就会导致写线程饥饿。所以就需要对读取进行乐观锁处理。
        2.  StampedLock加入了乐观读锁，不会排斥写入
        3.  当并发量大且读远大于写的情况下最快的的是StampedLock锁
        
    StampedLock控制锁有三种模式（排它写，悲观读，乐观读），一个StampedLock状态是由版本和模式两个部分组成，
        锁获取方法返回一个数字作为票据stamp，它用相应的锁状态表示并控制访问。
````
####synchronized锁普通方法和锁静态方法
1.对象锁钥匙只能有一把才能互斥，才能保证共享变量的唯一性\
2.在静态方法上的锁，和 实例方法上的锁，默认不是同样的，如果同步需要制定两把锁一样。\
3.关于同一个类的方法上的锁，来自于调用该方法的对象，如果调用该方法的对象是相同的，那么锁必然相同，否则就不相同。
    比如 new A().x() 和 new A().x(),对象不同，锁不同，如果A的单利的，就能互斥。\
4.静态方法加锁，能和所有其他静态方法加锁的 进行互斥\
5.静态方法加锁，和xx.class 锁效果一样，直接属于类的
####死锁的原理及排查方法
互斥条件：资源是独占的且排他使用，进程互斥使用资源，即任意时刻一个资源只能给一个进程使用，\
        其他进程若申请一个资源，而该资源被另一进程占有时，则申请者等待直到资源被占有者释放。\
不可剥夺条件：进程所获得的资源在未使用完毕之前，不被其他进程强行剥夺，而只能由获得该资源的进程资源释放。\
请求和保持条件：进程每次申请它所需要的一部分资源，在申请新的资源的同时，继续占用已分配到的资源。\
循环等待条件：:若干进程之间形成一种头尾相接的循环等待资源关系。 
####常用的线程池有几种？这几种线程池之间有什么区别和联系

````
newCachedThreadPool
    创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
    这种类型的线程池特点是：
        工作线程的创建数量几乎没有限制(其实也有限制的,数目为Interger.MAX_VALUE),
newFixedThreadPool
    创建一个指定工作线程数量的线程池。每当提交一个任务就创建一个工作线程，如果工作线程数量达到线程池初始的最大数，则将提交的任务存入到池队列中
FixedThreadPool是一个典型且优秀的线程池，它具有线程池提高程序效率和节省创建线程时所耗的开销的优点。
    但是，在线程池空闲时，即线程池中没有可运行任务时，它不会释放工作线程，还会占用一定的系统资源
newSingleThreadExecutor
    创建一个单线程化的Executor，即只创建唯一的工作者线程来执行任务，它只会用唯一的工作线程来执行任务，
    保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行
newScheduleThreadPool
    创建一个定长的线程池，而且支持定时的以及周期性的任务执行，支持定时及周期性任务执行
````
####线程池的实现原理是怎么样的

####什么样的场景该使用什么样的线程池比较合适
我们可以通过ThreadPoolExecutor来创建一个线程池

####synchronized的实现机制？
实现有两部分：monitor对象，线程，工作机制还是线程抢占对象使用权，
对象有自己的对象头，存储了对象的很多信息，其中有一个是标识被哪个线程持有，
对比AQS，线程从修改state，变为修改monitor的对象头，线程的等待区域动 AQS中的队列，变为monitor对象中的某个区域



####wait()和sleep()的区别
sleep来自Thread类，和wait来自Object类
调用sleep()方法的过程中，线程不会释放对象锁。而 调用 wait 方法线程会释放对象锁
sleep睡眠后不出让系统资源，wait让出系统资源其他线程可以占用CPU
sleep(milliseconds)需要指定一个睡眠时间，时间一到会自动唤醒






#### Handler源码  Looper  MessageQueue Message
```
Looper
每个Looper拥有一个消息队列，归属于一个线程,如果线程尝试创建第二个Looper就会出现异常
private static void prepare(boolean quitAllowed) {
    if (sThreadLocal.get() != null) {
        throw new RuntimeException("Only one Looper may be created per thread");
    }
    sThreadLocal.set(new Looper(quitAllowed));
}
private Looper(boolean quitAllowed) {
    mQueue = new MessageQueue(quitAllowed);
    mThread = Thread.currentThread();//创建一个消息队列和Looper所运行的线程就是当前线程
}
启动Looper   把这个线程Looper的queue里面的消息送去处理
     loop(){
        //MessageQueue  queue = sThreadLocal.get().mQueue
        // 确保线程就是本地线程，并实时跟踪线程身份
        Binder.clearCallingIdentity();
        Message msg = queue.next(); // MessageQueue可能会阻塞
        // msg.handler.dispatchMessage(msg)，消息发送到Handler回调
        msg.target.dispatchMessage(msg);
        // 确保消息在分发的时候线程没有改变
        final long newIdent = Binder.clearCallingIdentity();
     }
Looper的quit方法
    实际上执行了MessageQueue中的removeAllMessagesLocked方法，
    该方法的作用是把MessageQueue消息池中所有的消息全部清空，无论是延迟消息
    （延迟消息是指通过sendMessageDelayed或通过postDelayed等方法发送的需要延迟执行的消息）还是非延迟消息。
Looper的quitSafely方法
    实际上执行了MessageQueue中的removeAllFutureMessagesLocked方法，
    通过名字就可以看出，该方法只会清空MessageQueue消息池中所有的延迟消息，并将消息池中所有的非延迟消息派发出去让Handler去处理，
quit和quitSafely比较
    quitSafely相比于quit方法安全之处在于清空消息之前会派发所有的非延迟消息。
    Looper的quit方法从API Level 1就存在了，但是Looper的quitSafely方法从API Level 18才添加进来
    
Looper.loop();里面维护了一个死循环方法
在子线程中使用Looper.prepare();和Looper.loop();（不建议这么做，因为它会使线程无法执行结束，导致内存泄露）
        在子线程中，如果手动为其创建Looper，那么在所有的事情完成以后应该调用quit方法来终止消息循环，
        否则这个子线程就会一直处于等待（阻塞）的状态，而如果退出Looper以后，这个线程就会立刻（执行所有方法并）终止，
        因此建议在不需要Looper的时候终止Looper。(Looper.myLooper().quit();)
        
    
Message
    Message类是个final类
    recycle():回收当前message到全局池
    obj 无法跨进程发送 必须是Parcelable的类      Parcelable p = (Parcelable)obj;
    obtain() 调用obtain()或者obtainMessage(), 这样是从一个可回收对象池中获取Message对象。这样可以节省内存

MessageQueue
     MessageQueue，主要包含2个操作：插入和读取。读取操作会伴随着删除操作，插入和读取对应的方法分别为enqueueMessage和next，
     其中enqueueMessage的作用是往消息队列中插入一条消息，而next的作用是从消息队列中取出一条消息并将其从消息队列中移除。
     虽然MessageQueue叫消息队列，但是它的内部实现并不是用的队列，实际上它是通过一个单链表的数据结构来维护消息列表，
     单链表在插入和删除上比较有优势
    next() 取出下一个Message（从头部取），如果没有Message可以处理，就可以处理下IdleHandler。
            idle表示当前有空闲时间的时候执行，而运行到这一步的时候，表示消息队列处理已经是出于空闲时间了
            （队列中没有Message，或者头部Message的执行时间(when)在当前时间之后）
    enqueueMessage(...) messageQueue中的元素是按序按时间先后插入的（先执行的在前）。
    removeMessages()是先从队首删除，如果删除了则队首指向接下来的元素，重复这个过程，直到第一个不匹配的元素出现。
                接着从这个元素之后（after front）开始查找并删除，
                方法是链表删除后一个节点的方法，即p.next=nn。注意这里都是删除所有匹配的消息，而不是第一个匹配的
    quit(boolean safe)  如果是safe的退出，则执行removeAllFutureMessagesLocked()
                如果是unsafe的退出，则所有message都直接被删除并回收

HandlerThread
    由于HandlerThread的run方法是一个无限循环，因此当不需要使用的时候通过quit或者quitSafely方法来终止线程的执行。
IdleHandler
    注释可以了解到，这个接口方法是在消息队列全部处理完成后或者是在阻塞的过程中等待更多的消息的时候调用的，
    返回值false表示只回调一次，true表示可以接收多次回调


Looper 死循环为什么不会导致应用卡死？
    线程默认没有Looper的，如果需要使用Handler就必须为线程创建Looper。我们经常提到的主线程，也叫UI线程，
    它就是ActivityThread，ActivityThread被创建时就会初始化Looper，这也是在主线程中默认可以使用Handler的原因。

主线程 ActivityThread
 public static void main(String[] args) {
 
         ``````
         Looper.prepareMainLooper();//创建Looper和MessageQueue对象，用于处理主线程的消息
         ActivityThread thread = new ActivityThread();
         thread.attach(false);//建立Binder通道 (创建新线程)
         if (sMainThreadHandler == null) {
             sMainThreadHandler = thread.getHandler();
         }
         Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
         Looper.loop();//主线程死循环
         //如果能执行下面方法，说明应用崩溃或者是退出了...
         throw new RuntimeException("Main thread loop unexpectedly exited");
     }
     
主线程的死循环一直运行是不是特别消耗CPU资源呢？ 其实不然，这里就涉及到Linux pipe/epoll机制，简单说就是在主线程的MessageQueue没有消息时，
便阻塞在loop的queue.next()中的nativePollOnce()方法里，此时主线程会释放CPU资源进入休眠状态，直到下个消息到达或者有事务发生，
通过往pipe管道写端写入数据来唤醒主线程工作。这里采用的epoll机制，是一种IO多路复用机制，可以同时监控多个描述符，
当某个描述符就绪(读或写就绪)，则立刻通知相应程序进行读或写操作，本质同步I/O，即读写是阻塞的。 
所以说，主线程大多数时候都是处于休眠状态，并不会消耗大量CPU资源。 

ActivityThread 的动力是什么？
    其实承载ActivityThread的主线程就是由Zygote fork而创建的进程。
        从Linux角度来说进程与线程除了是否共享资源外，并没有本质的区别，都是一个task_struct结构体，
        在CPU看来进程或线程无非就是一段可执行的代码，CPU采用CFS调度算法，保证每个task都尽可能公平的享有CPU时间片
        

```
Messenger 
IBinder

















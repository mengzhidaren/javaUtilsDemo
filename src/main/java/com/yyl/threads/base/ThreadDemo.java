package com.yyl.threads.base;

/**
 * Created by Administrator on 2017/7/17/017.
 * <p>
 * <p>
 * Thread
 * n      start()：初始化线程，使线程开始执行，Java虚拟机调用该线程的run()。
 * n      yield()：线程暂停，回到可执行状态，锁保留，让出CPU时间，但只让给同优先级的线程，所以如果某线程使用yield()
 * <p>                后如果没有相同优先级的线程则该线程又会再次被执行。
 * n      sleep()：线程暂停，回到可执行状态，锁保留，让出CPU时间，可使不同优先级的线程获得执行的机会。
 * n      join()：等待该线程执行完毕。
 * n      setPriority()：设置线程优先级。JDK有10个优先级，但操作系统的优先级各不相同，所以无法做到完美的映射，
 * <p>                      可行的策略是只使用MIN_PRIORITY、NORM_PRIORITY和MAX_PRIORITY三个优先级。
 * n      setDaemon()：将该线程标记为守护线程或用户线程（后台线程），当正在运行的线程都是守护线程时，
 * <p>                     Java虚拟机退出（即程序立即结束，如书上的例子）。后台线程创建的任何线程都会自动被设置为后台线程。
 * n      interrupt()：中断线程。
 * n      interrupted()：测试当前线程是否已经中断并清除线程的中断标识。换句话说，如果连续两次调用该方法，则第二次调用必定返回 false。
 * <p>                         异常捕获时也将清除线程的中断标识，所以异常被捕获时这个标识总是为false。
 * n      isInterrupted()：测试线程是否已经中断，线程的中断状态不受该方法的影响。
 * n      run()：继承Thread或实现Runnable的一般目的就是改写run()，将要并发执行的代码需要写在里面。如果在子类中新建其他方法，如run1()，
 * <p>                 写在里面的代码是不能并发执行的。
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * //当前线程可转让cpu控制权，让别的就绪状态线程运行（切换）
 * public static Thread.yield()
 * //暂停一段时间
 * public static Thread.sleep()
 * //在一个线程中调用other.join(),将等待other执行完后才继续本线程。
 * public join()
 * //后两个函数皆可以被打断
 * public interrupte()
 * <p>
 * //submit方法有多重参数版本，及支持callable也能够支持runnable接口类型.
 * Future future = e.submit(new myCallable());
 * future.isDone() //return true,false 无阻塞
 * future.get() // return 返回值，阻塞直到该线程运行结束
 * <p>
 * <p>
 * <p>
 * <p>线程间的通信(线程间交互)
 * Java是通过object类的wait,notify,notifyAll这几个方法来实现线程之间的通信的
 * <p>
 * Wait：告诉当前进程放弃监视器并进入睡眠状态，直到其他进程进入同一监视器并调用notify为止。
 * Notify：唤醒同一对象监视器中调用wait的第一个线程。
 * notifyAll：唤醒同一对象监视器中调用wait的所有线程，具有最高优先级的线程首先被唤醒并执行。
 * <p>
 * Wait,notify,notifyAll这三个方法只能在synchronized方法中调用，即无论线程调用一个对象的wait还是notify方法，该线程必须得到该对象的锁旗标
 * 这样，notify只能唤醒同一对象监视器中调用wait的线程，使用多个对象监视器，我们就可以分组有多个wait，notify的情况，同组里的wait只能被同组的notify唤醒
 * <p>
 * <p>
 * <p>线程生命的控制
 * suspend方法，resume方法和stop方法。但是我们并不推荐使用这三个方法
 * （1）会导致死锁的发生。
 * （2）它允许一个线程通过直接控制另外一个线程的代码来直接控制那个线程。
 * 虽然stop方法可以避免死锁，但会带来另外的不足，如果一个线程正在操作共享数据段，操作过程没有完成就stop的话，将会导致数据的不完整性。因此stop方法我们也不提倡使用
 * 在实际的编程操作中我们推荐使用控制run方法中的循环条件的方式来结束一个线程，这也是实际情况中使用最多的。
 * <p>
 * <p>
 * <p>多线程死锁问题
 * 所谓死锁: 是指两个或两个以上的进程在执行过程中，因争夺资源而造成的一种互相等待的现象，若无外力作用，它们都将无法推进下去。
 * 发生死锁的原因一般是两个对象的锁相互等待造成的
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * 什么是线程安全
 * <p>
 * 又是一个理论的问题，各式各样的答案有很多，我给出一个个人认为解释地最好的：
 * 如果你的代码在多线程下执行和在单线程下执行永远都能获得一样的结果，那么你的代码就是线程安全的。
 * <p>
 * 这个问题有值得一提的地方，就是线程安全也是有几个级别的：
 * （1）不可变
 * 像String、Integer、Long这些，都是final类型的类，任何一个线程都改变不了它们的值，要改变除非新创建一个，因此这些不可变对象不需要任何同步手段就可以直接在多线程环境下使用
 * （2）绝对线程安全
 * 不管运行时环境如何，调用者都不需要额外的同步措施。要做到这一点通常需要付出许多额外的代价，Java中标注自己是线程安全的类，实际上绝大多数都不是线程安全的，
 * 不过绝对线程安全的类，Java中也有，比方说CopyOnWriteArrayList、CopyOnWriteArraySet
 * （3）相对线程安全
 * 相对线程安全也就是我们通常意义上所说的线程安全，像Vector这种，add、remove方法都是原子操作，不会被打断，但也仅限于此，
 * 如果有个线程在遍历某个Vector、有个线程同时在add这个Vector，99%的情况下都会出现ConcurrentModificationException，也就是fail-fast机制。
 * （4）线程非安全
 * 这个就没什么好说的了，ArrayList、LinkedList、HashMap等都是线程非安全的类
 * <p>
 * <p>
 * <p>
 * <p>
 * sleep方法和wait方法有什么区别
 * <p>
 * 这个问题常问，sleep方法和wait方法都可以用来放弃CPU一定的时间，不同点在于如果线程持有某个对象的监视器，sleep方法不会放弃这个对象的监视器，wait方法会放弃这个对象的监视器
 * <p>
 * <p>
 * <p>
 * ThreadLocal有什么用
 * 简单说ThreadLocal就是一种以空间换时间的做法，在每个Thread里面维护了一个以开地址法实现的ThreadLocal.ThreadLocalMap，把数据进行隔离，数据不共享，自然就没有线程安全方面的问题了
 * <p>
 * <p>
 * <p>
 * 为什么wait()方法和notify()/notifyAll()方法要在同步块中被调用
 * 这是JDK强制的，wait()方法和notify()/notifyAll()方法在调用前都必须先获得对象的锁
 * <p>
 * <p>
 * 怎么检测一个线程是否持有对象监视器
 * 我也是在网上看到一道多线程面试题才知道有方法可以判断某个线程是否持有对象监视器：Thread类提供了一个holdsLock(Object obj)方法，当且仅当对象obj的监视器被某条线程持有的时候才会返回true，
 * 注意这是一个static方法，这意味着“某条线程”指的是当前线程。
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * synchronized和ReentrantLock的区别
 * synchronized是和if、else、for、while一样的关键字，ReentrantLock是类，这是二者的本质区别。既然ReentrantLock是类，那么它就提供了比synchronized更多更灵活的特性，可以被继承、可以有方法、
 * 可以有各种各样的类变量，ReentrantLock比synchronized的扩展性体现在几点上：
 * （1）ReentrantLock可以对获取锁的等待时间进行设置，这样就避免了死锁
 * （2）ReentrantLock可以获取各种锁的信息
 * （3）ReentrantLock可以灵活地实现多路通知
 * 另外，二者的锁机制其实也是不一样的。ReentrantLock底层调用的是Unsafe的park方法加锁，synchronized操作的应该是对象头中mark word，这点我不能确定。
 * <p>
 * <p>
 * <p>
 * <p>
 * Java中用到的线程调度算法是什么
 * 抢占式。一个线程用完CPU之后，操作系统会根据线程优先级、线程饥饿情况等数据算出一个总的优先级并分配下一个时间片给某个线程执行。
 * <p>
 * <p>
 * <p>
 * Thread.sleep(0)的作用是什么
 * 这个问题和上面那个问题是相关的，我就连在一起了。由于Java采用抢占式的线程调度算法，因此可能会出现某条线程常常获取到CPU控制权的情况，
 * 为了让某些优先级比较低的线程也能获取到CPU控制权，可以使用Thread.sleep(0)手动触发一次操作系统分配时间片的操作，这也是平衡CPU控制权的一种操作。
 * <p>
 * <p>
 * <p>
 * 什么是CAS
 * CAS，全称为Compare and Set，即比较-设置。假设有三个操作数：内存值V、旧的预期值A、要修改的值B，当且仅当预期值A和内存值V相同时，才会将内存值修改为B并返回true，
 * 否则什么都不做并返回false。当然CAS一定要volatile变量配合，这样才能保证每次拿到的变量是主内存中最新的那个值，否则旧的预期值A对某条线程来说，
 * 永远是一个不会变的值A，只要某次CAS操作失败，永远都不可能成功。
 * <p>
 * 什么是AQS
 * 简单说一下AQS，AQS全称为AbstractQueuedSychronizer，翻译过来应该是抽象队列同步器。
 * 如果说java.util.concurrent的基础是CAS的话，那么AQS就是整个Java并发包的核心了，ReentrantLock、CountDownLatch、Semaphore等等都用到了它。
 * AQS实际上以双向队列的形式连接所有的Entry，比方说ReentrantLock，所有等待的线程都被放在一个Entry中并连成双向队列，前面一个线程使用ReentrantLock好了，则双向队列实际上的第一个Entry开始运行。
 * AQS定义了对双向队列所有的操作，而只开放了tryLock和tryRelease方法给开发者使用，开发者可以根据自己的实现重写tryLock和tryRelease方法，以实现自己的并发功能。
 * <p>
 * <p>
 * <p>
 * <p>
 * 怎么唤醒一个阻塞的线程
 * 如果线程是因为调用了wait()、sleep()或者join()方法而导致的阻塞，可以中断线程，并且通过抛出InterruptedException来唤醒它；
 * 如果线程遇到了IO阻塞，无能为力，因为IO是操作系统实现的，Java代码并没有办法直接接触到操作系统。
 * <p>
 * <p>
 * <p>
 * <p>
 * 什么是多线程的上下文切换
 * 多线程的上下文切换是指CPU控制权由一个已经正在运行的线程切换到另外一个就绪并等待获取CPU执行权的线程的过程。
 * <p>
 * <p>
 * <p>
 * <p>
 * 40、高并发、任务执行时间短的业务怎样使用线程池？并发不高、任务执行时间长的业务怎样使用线程池？并发高、业务执行时间长的业务怎样使用线程池？
 * <p>
 * 这是我在并发编程网上看到的一个问题，把这个问题放在最后一个，希望每个人都能看到并且思考一下，因为这个问题非常好、非常实际、非常专业。关于这个问题，个人看法是：
 * （1）高并发、任务执行时间短的业务，线程池线程数可以设置为CPU核数+1，减少线程上下文的切换
 * （2）并发不高、任务执行时间长的业务要区分开看：
 * a）假如是业务时间长集中在IO操作上，也就是IO密集型的任务，因为IO操作并不占用CPU，所以不要让所有的CPU闲下来，可以加大线程池中的线程数目，让CPU处理更多的业务
 * b）假如是业务时间长集中在计算操作上，也就是计算密集型任务，这个就没办法了，和（1）一样吧，线程池中的线程数设置得少一些，减少线程上下文的切换
 * （3）并发高、业务执行时间长，解决这种类型任务的关键不在于线程池而在于整体架构的设计，看看这些业务里面某些数据是否能做缓存是第一步，增加服务器是第二步，
 * 至于线程池的设置，设置参考（2）。最后，业务执行时间长的问题，也可能需要分析一下，看看能不能使用中间件对任务进行拆分和解耦。
 * <p>
 */
public class ThreadDemo implements Runnable {
    private int flag = 1;
    private Object obj1 = new Object(), obj2 = new Object();

    public void run() {
        System.out.println("flag=" + flag);
        if (flag == 1) {
            synchronized (obj1) {
                System.out.println("我已经锁定obj1，休息0.5秒后锁定obj2去！");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (obj2) {
                    System.out.println("1");
                }
            }
        }
        if (flag == 0) {
            synchronized (obj2) {
                System.out.println("我已经锁定obj2，休息0.5秒后锁定obj1去！");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (obj1) {
                    System.out.println("0");
                }
            }
        }
    }

    public static void main(String[] args) {
        ThreadDemo run01 = new ThreadDemo();
        ThreadDemo run02 = new ThreadDemo();
        run01.flag = 1;
        run02.flag = 0;
        Thread thread01 = new Thread(run01);
        Thread thread02 = new Thread(run02);
        System.out.println("线程开始喽！");
        thread01.start();
        thread02.start();
    }
}

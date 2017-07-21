package com.yyl.threads.java5;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Java多线程-新特征-原子量
 * <p>
 * 所谓的原子量即操作变量的操作是“原子的”，该操作不可再分，因此是线程安全的
 * <p>
 * 为何要使用原子变量呢，原因是多个线程对单个变量操作也会引起一些问题。在Java5之前，可以通过volatile、synchronized关键字来解决并发访问的安全问题，但这样太麻烦。
 * Java5之后，专门提供了用来进行单变量多线程并发安全访问的工具包java.util.concurrent.atomic，其中的类也很简单。
 * <p>
 * <p>
 * <p>
 * <p>
 * Atomic就是指线程的每次操作对其他线程都是立即可见的。而且获取和赋值变量都不是在线程的私有栈内，而是在一块公共的区域。多有线程都访问同一个区域。
 * 而且每次操作都是原子的，并且对其他线程是立即可见的
 * <p>
 * <p>
 * <p>
 * AtomicInteger的常用方法如下：
 * int addAndGet(int delta) ：以原子方式将输入的数值与实例中的值（AtomicInteger里的value）相加，并返回结果
 * boolean compareAndSet(int expect, int update) ：如果输入的数值等于预期值，则以原子方式将该值设置为输入的值。
 * int getAndIncrement()：以原子方式将当前值加1，注意：这里返回的是自增前的值。
 * void lazySet(int newValue)：最终会设置成newValue，使用lazySet设置值后，可能导致其他线程在之后的一小段时间内还是可以读到旧的值。
 * int getAndSet(int newValue)：以原子方式设置为newValue的值，并返回旧值。
 * <p>
 * <p>
 * <p>
 * * AtomicIntegerArray类主要是提供原子的方式更新数组里的整型，其常用方法如下
 * int addAndGet(int i, int delta)：以原子方式将输入值与数组中索引i的元素相加。
 * boolean compareAndSet(int i, int expect, int update)：如果当前值等于预期值，则以原子方式将数组位置i的元素设置成update值。
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * 原子更新基本类型类
 * 用于通过原子的方式更新基本类型，Atomic包提供了以下三个类：
 * AtomicBoolean：原子更新布尔类型。
 * AtomicInteger：原子更新整型。
 * AtomicLong：原子更新长整型。
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * 原子更新数组类
 * 通过原子的方式更新数组里的某个元素，Atomic包提供了以下三个类：
 * * AtomicIntegerArray：原子更新整型数组里的元素。
 * AtomicLongArray：原子更新长整型数组里的元素。
 * AtomicReferenceArray：原子更新引用类型数组里的元素。
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * 原子更新引用类型
 * 原子更新基本类型的AtomicInteger，只能更新一个变量，如果要原子的更新多个变量，就需要使用这个原子更新引用类型提供的类。Atomic包提供了以下三个类：
 * AtomicReference：原子更新引用类型。
 * AtomicReferenceFieldUpdater：原子更新引用类型里的字段。
 * AtomicMarkableReference：原子更新带有标记位的引用类型。可以原子的更新一个布尔类型的标记位和引用类型。构造方法是AtomicMarkableReference(V initialRef, boolean initialMark)
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>原子更新字段类
 * 如果我们只需要某个类里的某个字段，那么就需要使用原子更新字段类，Atomic包提供了以下三个类：
 * AtomicIntegerFieldUpdater：原子更新整型的字段的更新器。
 * AtomicLongFieldUpdater：原子更新长整型字段的更新器。
 * AtomicStampedReference：原子更新带有版本号的引用类型。该类将整数值与引用关联起来，可用于原子的更数据和数据的版本号，可以解决使用CAS进行原子更新时，可能出现的ABA问题。
 * 原子更新字段类都是抽象类，每次使用都时候必须使用静态方法newUpdater创建一个更新器。原子更新类的字段的必须使用public volatile修饰符。
 */
public class ThreadAtomicTest {


    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        //
//        Runnable t1 = new AtomicRunnable("张三", 2000);
//        Runnable t2 = new AtomicRunnable("李四", 3600);
//        Runnable t3 = new AtomicRunnable("王五", 2700);
//        Runnable t4 = new AtomicRunnable("老张", 600);
//        Runnable t5 = new AtomicRunnable("老牛", 1300);
//        Runnable t6 = new AtomicRunnable("胖子", 800);


        Lock lock = new ReentrantLock(false);
        Runnable t1 = new AtomicRunnable2("张三", 2000, lock);
        Runnable t2 = new AtomicRunnable2("李四", 3600, lock);
        Runnable t3 = new AtomicRunnable2("王五", 2700, lock);
        Runnable t4 = new AtomicRunnable2("老张", 600, lock);
        Runnable t5 = new AtomicRunnable2("老牛", 1300, lock);
        Runnable t6 = new AtomicRunnable2("胖子", 800, lock);


        // 执行各个线程
        pool.execute(t1);
        pool.execute(t2);
        pool.execute(t3);
        pool.execute(t4);
        pool.execute(t5);
        pool.execute(t6);
        // 关闭线程池
        pool.shutdown();
    }

    //虽然使用了原子量，但是程序并发访问还是有问题，那究竟问题出在哪里了
    static class AtomicRunnable implements Runnable {
        private static AtomicLong aLong = new AtomicLong(10000); // 原子量，每个线程都可以自由操作
        private String name; // 操作人
        private int x; // 操作数额

        AtomicRunnable(String name, int x) {
            this.name = name;
            this.x = x;
        }

        public void run() {
            System.out.println(name + "执行了" + x + "，当前余额：" + aLong.addAndGet(x));
        }
    }

    //原子量虽然可以保证单个变量在某一个操作过程的安全，但无法保证你整个代码块，或者整个程序的安全性。因此，通常还应该使用锁等同步机制来控制整个程序的安全性。
    // 下面是对这个错误修正
    static class AtomicRunnable2 implements Runnable {
        private static AtomicLong aLong = new AtomicLong(10000); // 原子量，每个线程都可以自由操作
        private String name; // 操作人
        private int x; // 操作数额
        private Lock lock;

        AtomicRunnable2(String name, int x, Lock lock) {
            this.name = name;
            this.x = x;
            this.lock = lock;
        }

        public void run() {
            lock.lock();
            System.out.println(name + "执行了" + x + "，当前余额：" + aLong.addAndGet(x));
            lock.unlock();
        }

    }
}



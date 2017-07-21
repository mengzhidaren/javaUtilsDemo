package com.yyl.threads.locks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * java5 Condition用法--实现线程间的通信
 * <p>
 * Condition的功能类似在传统线程技术中的Object.wait()和Object.natify()的功能，传统线程技术实现的互斥只能一个线程单独干，
 * 不能说这个线程干完了通知另一个线程来干，Condition就是解决这个问题的，实现线程间的通信。比如CPU让小弟做事，小弟说我先歇着并通知大哥，大哥就开始做事。
 * <p>
 * Condition 将 Object 监视器方法（wait、notify 和 notifyAll）分解成截然不同的对象，以便通过将这些对象与任意 Lock 实现组合使用，
 * 为每个对象提供多个等待 set（wait-set）。其中，Lock 替代了 synchronized 方法和语句的使用，Condition 替代了 Object 监视器方法的使用。
 * Condition实例实质上被绑定到一个锁上。要为特定 Lock 实例获得 Condition 实例，请使用其 newCondition() 方法。
 * <p>
 * 在java5中，一个锁可以有多个条件，每个条件上可以有多个线程等待，通过调用await()方法，可以让线程在该条件下等待。当调用signalAll()方法，又可以唤醒该条件下的等待的线程。
 * <p>
 * <p>
 * 在Java5之前我们的通信方式为：wait 和 notify。那么Condition的优势是支持多路等待，就是我可以定义多个Condition，每个condition控制线程的一条执行通路。传统方式只能是一路等待。
 * <p>
 * <p>
 * <p>
 * signal() 代替了 notify()，await() 代替了 wait()，signalAll() 代替 notifyAll()。
 * await()  是等待的意思，调它 就是  阻塞写线程。
 * signal 是发出信号的意思，调它 就是 唤醒读线程。
 * 我想，应当先对线程1、线程2，建 condition 对象 1： c_th1，对象 2: c_th2;
 * c_th1.await()   //  阻塞写线程1
 * c_th2.signal()   // 唤醒读线程2
 */
public class ConditionTest {
    public static void main(String[] args) {
        final ReentrantLock reentrantLock = new ReentrantLock();
        final Condition condition = reentrantLock.newCondition();
        Thread thread1 = new Thread(() -> {
            reentrantLock.lock();
            try {
                System.out.println("我要等一个新信号");
                condition.await();// 阻塞一直等待有condition 调用signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("拿到一个信号！！");
            reentrantLock.unlock();
        }, "waitThread1");

        Thread thread2 = new Thread(() -> {
            reentrantLock.lock();
            System.out.println("我拿到锁了");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            condition.signal();
            System.out.println("我发了一个信号！！");
            reentrantLock.unlock();
        }, "signalThread");
        thread1.start();
        thread2.start();

    }


}
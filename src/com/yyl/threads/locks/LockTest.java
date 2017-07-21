package com.yyl.threads.locks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 在Java5中，专门提供了锁对象，利用锁可以方便的实现资源的封锁，用来控制对竞争资源并发访问的控制，这些内容主要集中在java.util.concurrent.locks 包下面，
 * 里面有三个重要的接口Condition、Lock、ReadWriteLock。
 * Condition:
 * Condition 将 Object 监视器方法（wait、notify 和 notifyAll）分解成截然不同的对象，以便通过将这些对象与任意 Lock 实现组合使用，为每个对象提供多个等待 set （wait-set）。
 * Lock:
 * Lock 实现提供了比使用 synchronized 方法和语句可获得的更广泛的锁定操作。
 * ReadWriteLock:
 * ReadWriteLock 维护了一对相关的锁定，一个用于只读操作，另一个用于写入操作。
 */
public class LockTest {

    //1、Lock比传统线程模型中的synchronized方式更加面向对象，与生活中的锁类似，
    // 锁本身也应该是一个对象。两个线程执行的代码片段要实现同步互斥的效果，它们必须用同一个Lock对象。
    //         　　lock替代synchronized
    public static void main(String[] args) {
        Ticket ticket = new Ticket();
        new Thread(ticket, "窗口1售票").start();
        new Thread(ticket, "窗口2售票").start();
        new Thread(ticket, "窗口3售票").start();
    }

    static class Ticket implements Runnable {
        private int ticket = 100;
        private Lock lock = new ReentrantLock();

        @Override
        public void run() {
            while (true) {
                lock.lock();
                try {
                    if (ticket > 0) {
                        Thread.sleep(20);
                        System.out.println(Thread.currentThread().getName()
                                + "，余票量：" + ticket--);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}
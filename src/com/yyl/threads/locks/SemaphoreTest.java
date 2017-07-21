package com.yyl.threads.locks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Java多线程-新特征-信号量Semaphore
 * <p>
 * 信号量(Semaphore)，有时被称为信号灯，是在多线程环境下使用的一种设施, 它负责协调各个线程, 以保证它们能够正确、合理的使用公共资源。
 * <p>
 * Semaphore分为单值和多值两种，前者只能被一个线程获得，后者可以被若干个线程获得。
 * <p>
 * 在java中，还可以设置该信号量是否采用公平模式，如果以公平方式执行，则线程将会按到达的顺序（FIFO）执行，如果是非公平，则可以后请求的有可能排在队列的头部。
 * <p>
 * <p>
 * <p>
 * Semaphore当前在多线程环境下被扩放使用，操作系统的信号量是个很重要的概念，在进程控制方面都有应用。
 * Java并发库Semaphore 可以很轻松完成信号量控制，Semaphore可以控制某个资源可被同时访问的个数，通过 acquire() 获取一个许可，如果没有就等待，而 release() 释放一个许可
 * <p>
 * 信号量的特性如下：
 * 信号量是一个非负整数（车位数），所有通过它的线程（车辆）都会将该整数减一（通过它当然是为了使用资源），
 * 当该整数值为零时，所有试图通过它的线程都将处于等待状态。在信号量上我们定义两种操作： Wait（等待） 和 Release（释放）。
 * 当一个线程调用Wait（等待）操作时，它要么通过然后将信号量减一，要么一直等下去，直到信号量大于一或超时。Release（释放）实际上是在信号量上执行加操作，
 * 对应于车辆离开停车场，该操作之所以叫做“释放”是因为加操作实际上是释放了由信号量守护的资源。
 * <p>
 * Semaphore实现的功能就类似厕所有5个坑，假如有10个人要上厕所，那么同时只能有多少个人去上厕所呢？
 * 同时只能有5个人能够占用，当5个人中 的任何一个人让开后，其中等待的另外5个人中又有一个人可以占用了。
 * 另外等待的5个人中可以是随机获得优先机会，也可以是按照先来后到的顺序获得机会，
 * 这取决于构造Semaphore对象时传入的参数选项。单个信号量的Semaphore对象可以实现互斥锁的功能，
 * 并且可以是由一个线程获得了“锁”，再由另一个线程释放“锁”，这可应用于死锁恢复的一些场合。
 */
public class SemaphoreTest {
    public static void main(String[] args) {
        // 线程池
        ExecutorService exec = Executors.newCachedThreadPool();
        // 只能5个线程同时访问
        final Semaphore semp = new Semaphore(5);
        // 模拟20个客户端访问
        for (int index = 0; index < 10; index++) {
            final int NO = index;
            Runnable run = new Runnable() {
                public void run() {
                    try {
                        // 获取许可
                        semp.acquire();
                        System.out.println("Accessing: " + NO);
                        Thread.sleep((long) (Math.random() * 10000));
                        // 访问完后，释放
                        semp.release();
                        //availablePermits()指的是当前信号灯库中有多少个可以被使用
                        System.out.println("-----------------" + semp.availablePermits());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            exec.execute(run);
        }
        System.out.println("退出线程池: ");
        // 退出线程池
        exec.shutdown();
    }
}
package com.yyl.threads.locks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * java5的Semaphere同步工具
 * 　　Semaphore实现信号灯
 * <p>
 * 　　Semaphore可以维护当前访问自身的线程个数，并提供了同步机制。使用Semaphore可以控制同时访问资源的线程个数，例如，实现一个文件允许的并发访问数。
 * <p>
 * 　　假设一个文件同时可以被3个人访问，来了5个人，同时只有3个访问。3个中任何一个出来后，等待的就可以进去了。
 */
public class SemaphoreTest2 {
    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool();
        final Semaphore sp = new Semaphore(3);  //还有一个构造方法，Semaphore(int permits, boolean fair)  fair参数为true表示谁先来谁先进，一种公平的原则
        for (int i = 0; i < 10; i++) {
            Runnable runnable = new Runnable() {
                public void run() {
                    try {
                        sp.acquire();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                    System.out.println("getQueueLength " + sp.getQueueLength());//等待中的线程数
                    System.out.println("availablePermits " + sp.availablePermits());//已有并发数
                    System.out.println("drainPermits " + sp.drainPermits());

                    System.out.println("线程" + Thread.currentThread().getName() +
                            "进入，当前已有" + (3 - sp.availablePermits()) + "个并发");
                    try {
                        Thread.sleep((long) (Math.random() * 10000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("线程" + Thread.currentThread().getName() +
                            "即将离开");
                    sp.release();
                    //下面代码有时候执行不准确，因为其没有和上面的代码合成原子单元
                    System.out.println("线程" + Thread.currentThread().getName() +
                            "已离开，当前已有" + (3 - sp.availablePermits()) + "个并发");
                }
            };
            service.execute(runnable);
        }
    }
}

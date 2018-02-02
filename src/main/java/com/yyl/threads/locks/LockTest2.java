package com.yyl.threads.locks;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 1、Lock提供了无条件的、可轮询的、定时的、可中断的锁获取操作，所有加锁和解锁的方法都是显式的。
 * 2、ReentrantLock实现了lock接口，跟synchronized相比，ReentrantLock为处理不可用的锁提供了更多灵活性。
 * 3、使用lock接口的规范形式要求在finally块中释放锁lock.unlock()。如果锁守护的代码在try块之外抛出了异常，它将永远不会被释放。
 * <p>
 * <p>
 * Created by Administrator on 2017/7/17/017.
 */
public class LockTest2 {
    public static void main(String[] args) {
        new LockTest2().init();
    }

    private void init() {
        final Outputer outputer = new Outputer();
        //A线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    outputer.output("zhangsan");
                }

            }
        }).start();

        //B线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    outputer.output("lisi");
                }

            }
        }).start();

    }
    static class Outputer {
        Lock lock = new ReentrantLock();
        public void output(String name) {
            int len = name.length();
            lock.lock();
            try {
                for (int i = 0; i < len; i++) {
                    System.out.print(name.charAt(i));
                }
                System.out.println();
            } finally {
                lock.unlock();
            }
        }
    }
}

package com.yyl.threads.locks;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用多路Condition，可以扩展例子，老大打印完 -> 老二   老二-> 老三  老三-> 老大  老大-> 老二...
 * <p>
 * 面试题
 * * 第一个线程循环100次，第二个线程循环10次，第三个线程循环20次，如此循环50次，
 * 请写出程序 这里使用Condition
 */
public class ConditionTest3 {
    public static void main(String[] args) {
        final Business2 business = new Business2();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 50; i++) {
                    business.sub2(i);
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 50; i++) {
                    business.sub3(i);
                }
            }
        }).start();

        for (int i = 1; i <= 50; i++) {
            business.main(i);
        }
    }

    static class Business2 {
        Lock lock = new ReentrantLock();
        Condition condition1 = lock.newCondition();
        Condition condition2 = lock.newCondition();
        Condition condition3 = lock.newCondition();
        private int shoudeSub = 1;

        public void sub2(int i) {
            lock.lock();
            try {
                while (shoudeSub != 2) { // 这里也可以用 if ，用while比较好一些 As in the one argument
                    // version, interrupts and spurious wakeups are
                    // possible, and this method should always be
                    // used in a loop
                    try { // 防止线程有可能被假唤醒 (while放在这里提现了水准)
                        condition2.await();  //等待
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for (int j = 1; j <= 100; j++) {
                    System.out.println("sub2 thread sequence of " + j + ", loop of " + i);
                }
                shoudeSub = 3;
                condition3.signal();//唤醒
            } finally {
                lock.unlock();
            }
        }

        public void sub3(int i) {
            lock.lock();
            try {
                while (shoudeSub != 3) { // 这里也可以用 if ，用while比较好一些 As in the one argument
                    // version, interrupts and spurious wakeups are
                    // possible, and this method should always be
                    // used in a loop
                    try { // 防止线程有可能被假唤醒 (while放在这里提现了水准)
                        condition3.await();  //等待
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for (int j = 1; j <= 20; j++) {
                    System.out.println("sub3 thread sequence of " + j + ", loop of " + i);
                }
                shoudeSub = 1;
                condition1.signal();//唤醒
            } finally {
                lock.unlock();
            }
        }

        public void main(int i) {
            lock.lock();
            try {
                while (shoudeSub != 1) {
                    try {
                        condition1.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for (int j = 1; j <= 10; j++) {
                    System.out.println("main thread sequence of " + j + ", loop of "
                            + i);
                }
                shoudeSub = 2;
                condition2.signal();
            } finally {
                lock.unlock();
            }
        }
        /**
         *
         * synchronized (obj) { 这里的obj与obj.wait必须相同，否则会抛异常 while (<condition does
         * not hold>) obj.wait(); ... // Perform action appropriate to condition }
         */
    }
}
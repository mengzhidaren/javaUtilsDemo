package com.yyl.threads.locks;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition的一个例子：
 * 编写一个程序，开启3个线程 ，这三个线程的ID分别为 A，B, C,每个线程将自己的ID 在屏幕上打印10遍，要求输出的结果必须按顺序显示。
 * <p>
 * 如：ABCABCABC.....依次递归
 * <p>
 * 这里实现了一个比题目稍微难得例子，A 打印10次，B打印20次 ，C打印5次依次递归20次。
 */
public class ConditionTest4 {
    public static void main(String[] args) {
        final Alternative alternative = new Alternative();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 20; i++) {
                    alternative.loopA(i);
                }
            }
        }, "A").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 20; i++) {
                    alternative.loopB(i);
                }
            }
        }, "B").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 20; i++) {
                    alternative.loopC(i);
                    System.out.println("-----------------");
                }
            }
        }, "C").start();

    }

    static class Alternative {

        private int number = 1;
        private Lock lock = new ReentrantLock();
        private Condition condition1 = lock.newCondition();
        private Condition condition2 = lock.newCondition();
        private Condition condition3 = lock.newCondition();

        void loopA(int outerLoop) {
            lock.lock();
            try {
                while (number != 1) {
                    condition1.await();
                }
                for (int i = 1; i <= 10; i++) {
                    System.out.println(Thread.currentThread().getName() + "\t" + i + "\t" + outerLoop);
                }
                number = 2;
                condition2.signal();
            } catch (Exception e) {
            } finally {
                lock.unlock();
            }
        }

        void loopB(int outerLoop) {
            lock.lock();
            try {
                while (number != 2) {
                    condition2.await();
                }
                for (int i = 1; i <= 20; i++) {
                    System.out.println(Thread.currentThread().getName() + "\t" + i + "\t" + outerLoop);
                }
                number = 3;
                condition3.signal();
            } catch (Exception e) {

            } finally {
                lock.unlock();
            }
        }

        void loopC(int outerLoop) {
            lock.lock();
            try {
                while (number != 3) {
                    condition3.await();
                }
                for (int i = 1; i <= 5; i++) {
                    System.out.println(Thread.currentThread().getName() + "\t" + i + "\t" + outerLoop);
                }
                number = 1;
                condition1.signal();
            } catch (Exception e) {

            } finally {
                lock.unlock();
            }
        }
    }
}

package com.yyl.threads.java5;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * java5 Exchanger数据交换
 * Java并发API提供了一种允许2个并发任务间相互交换数据的同步应用。更具体的说，Exchanger类允许在2个线程间定义同步点，当2个线程到达这个点，他们相互交换数据类型，
 * 使用第一个线程的数据类型变成第二个的，然后第二个线程的数据类型变成第一个的。
 * 用于实现两个人之间的数据交换，每个人在完成一定的事务后想与对方交换数据，第一个先拿出数据的人将一直等待第二个人拿着数据到来时，才能彼此交换数据。
 */
public class ExchangerTest2 {

    private static long timeMillis;

    public static void main(String[] args) {
        Exchanger<Integer> exchanger = new Exchanger<>();
        timeMillis = System.currentTimeMillis();
        new Consumer(exchanger).start();
        new Producer(exchanger).start();
    }

    public static long getTime() {
        return (System.currentTimeMillis() - timeMillis) / 1000;
    }

    static class Producer extends Thread {
        Exchanger<Integer> exchanger = null;

        public Producer(Exchanger<Integer> exchanger) {
            super();
            this.exchanger = exchanger;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(3000);
                    System.out.println(Thread.currentThread().getName() + "   i = " + i + "  time=" +getTime());
                    int post = exchanger.exchange(i + 20);
                    System.out.println(Thread.currentThread().getName() + "   i = " + i + "  post= " + post+ "  time=" +getTime());
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    static class Consumer extends Thread {
        Exchanger<Integer> exchanger = null;

        public Consumer(Exchanger<Integer> exchanger) {
            super();
            this.exchanger = exchanger;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(5000);
                    System.out.println(Thread.currentThread().getName() + "   i = " + i+ "  time=" +getTime());
                    int get = exchanger.exchange(i);
                    System.out.println(Thread.currentThread().getName() + "   i = " + i + "  get= " + get+ "  time=" +getTime());
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }
}


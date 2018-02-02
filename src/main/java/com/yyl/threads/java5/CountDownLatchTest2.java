package com.yyl.threads.java5;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2017/7/19/019.
 */
public class CountDownLatchTest2 {

    public static void main(String[] args) {

        CountDownLatch latch = new CountDownLatch(5);
        LatchDemo2 ld = new LatchDemo2(latch);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            new Thread(ld).start();
        }
        try {
            latch.await();   //阻塞在这里等5个线程执行完后在运行下面   //    先执行完成的线程需要等待还没有执行完的线程
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        System.out.println("cost: " + (end - start));
    }

    static class LatchDemo2 implements Runnable {
        private CountDownLatch latch;

        public LatchDemo2(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {

            try {
                synchronized (this) {
                    for (int i = 0; i < 50000; i++) {  //找出50000以内的所有偶数
                        if (i % 2 == 0) {
                            System.out.println(i);
                        }
                    }
                }
            } finally {
                latch.countDown();   //为了让这一句一定执行可以放在finally中
            }
        }
    }
}
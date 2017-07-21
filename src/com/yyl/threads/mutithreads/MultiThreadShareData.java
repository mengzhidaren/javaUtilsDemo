package com.yyl.threads.mutithreads;

/**
 * 多个线程之间共享数据的方式
 * <p>
 * 设计四个线程，其中两个线程每次对j增加1，另外两个线程每次对j减少1。循环100次。
 *
 * @author Administrator
 */
public class MultiThreadShareData {
    private static ShareData data1 = new ShareData();

    public static void main(String[] args) {
        ShareData data2 = new ShareData();
        new Thread(new DecrementRunnable(data2)).start();
        new Thread(new IncrementRunnable(data2)).start();

      //  final ShareData data1 = new ShareData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                data1.decrement();

            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                data1.increment();

            }
        }).start();

    }


    /**
     * 创建线程类，负责对j减少1
     *
     * @author Administrator
     */
    static class DecrementRunnable implements Runnable {
        private ShareData data;

        public DecrementRunnable(ShareData data) {
            this.data = data;
        }

        public void run() {
            for (int i = 0; i < 100; i++) {
                data.decrement();
            }

        }
    }

    /**
     * 创建线程类，负责对j增加1
     *
     * @author Administrator
     */
    static class IncrementRunnable implements Runnable {
        private ShareData data;

        public IncrementRunnable(ShareData data) {
            this.data = data;
        }

        public void run() {
            for (int i = 0; i < 100; i++) {
                data.increment();
            }

        }
    }

    /**
     * 封装共享数据
     *
     * @author Administrator
     */
    static class ShareData {
        private int j = 0;

        /**
         * 每次对j增加1
         */
        public synchronized void increment() {
            j++;
            System.out.println("j++ =" + j);
        }

        /**
         * 每次对j减少1
         */
        public synchronized void decrement() {
            j--;
            System.out.println("j-- =" + j);
        }
    }
}
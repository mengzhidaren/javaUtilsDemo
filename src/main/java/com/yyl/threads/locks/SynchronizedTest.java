package com.yyl.threads.locks;


public class SynchronizedTest {

    public static void main(String[] args) throws InterruptedException {
      final   Sync sync = new Sync();
//        for (int i = 0; i < 3; i++) {
//            Thread thread = new MyThread(sync);
//            thread.setName(i + " MyThread ");
//            thread.start();
//        }
//
//        for (int i = 0; i < 3; i++) {
//            Thread thread = new MyThread2();
//            thread.setName(i + " MyThread2 ");
//            thread.start();
//        }

        new Thread(() -> {
            sync.test1();
        }).start();
        new Thread(() -> {
            sync.other();
        }).start();
    }


    static class Sync {
        public String name() {
            return "threadName=" + Thread.currentThread().getName() + "  ";
        }

        /**
         * 在同一个对像中
         *  synchronized (this) 中的this是对像的锁
         *  如果test1()中的this加锁后
         *  另一个线程other获取this 要等待 test1()中的this解锁 才能向下执行
         *  总结：一个对像中的所有synchronized方法是同步执行的
         *
         *  因为synchronized的锁是对像自已
         *  当有一个方法锁定后其它方法要等待第一个方法解锁后 获取this的锁
         *
         */
        public synchronized void other() {
            System.out.println(name() + "test other开始..");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(name() + "test other结束..");
        }

        /**
         * synchronized锁住的是括号里的对象，而不是代码。锁的就是对象本身也就是this
         * 只能防止多个线程同时执行同一个对象的同步代码段
         * tip:两个对像同时执行是不会同步的
         */
        public synchronized void test1() {
            System.out.println(name() + "test开始..");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(name() + "test结束..");
        }

        /**
         * test1 和 test11 功能一样
         */
        public void test11() {
            synchronized (this) {
                System.out.println("test2开始..");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("test2结束..");
            }
        }

        /**
         * static synchronized方法，static方法可以直接类名加方法名调用，方法中无法使用this，
         * 所以它锁的不是this，而是类的Class对象，所以，static synchronized方法也相当于全局锁，相当于锁住了代码段。
         */
        public static synchronized void test2() {
            System.out.println("test1开始..");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("test1结束..");
        }

        /**
         * test2 和 test22 功能一样
         */
        public void test22() {
            synchronized (Sync.class) {
                System.out.println("test3开始..");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("test3结束..");
            }
        }
    }

    static class MyThread extends Thread {

        private Sync sync;

        public MyThread(Sync sync) {
            this.sync = sync;
        }

        public void run() {
            sync.test2();
        }
    }

    static class MyThread2 extends Thread {

        public void run() {
            Sync sync = new Sync();
            sync.test22();
        }
    }
}

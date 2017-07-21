package com.yyl.threads.base;

/**
 * 线程的调度(守护线程)
 * Created by Administrator on 2017/7/17/017.
 * 守护线程与普通线程写法上基本没啥区别，调用线程对象的方法setDaemon(true)，则可以将其设置为守护线程。
 * <p>
 * 守护线程使用的情况较少，但并非无用，举例来说，JVM的垃圾回收、内存管理等线程都是守护线程。
 * 还有就是在做数据库应用时候，使用的数据库连接池，连接池本身也包含着很多后台线程，监控连接个数、超时时间、状态等等。
 * <p>
 * 该方法必须在启动线程前调用。
 * 该方法首先调用该线程的checkAccess方法，且不带任何参数。这可能抛出SecurityException（在当前线程中）。
 * <p>
 * on - 如果为 true，则将该线程标记为守护线程。
 * 抛出：
 * IllegalThreadStateException - 如果该线程处于活动状态。
 * SecurityException - 如果当前线程无法修改该线程。
 */
public class ThreadDaemon {
    public static void main(String[] args) {
        ThreadDaemon thread = new ThreadDaemon();
        Thread t1 = thread.new MyThread1();
        Thread t2 = new Thread(thread.new MyRunnable());
        t2.setDaemon(true); //设置为守护线程
        t1.setDaemon(false); //设置为守护线程

        t2.start();
        t1.start();
    }

    class MyThread1 extends Thread {
        public void run() {
            for (int i = 0; i < 5; i++) {
                System.out.println("线程1第" + i + "次执行！");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class MyRunnable implements Runnable {
        public void run() {
            for (long i = 0; i < 9999999L; i++) {
                System.out.println("守护线程   后台线程第" + i + "次执行！");
                try {
                    Thread.sleep(7);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

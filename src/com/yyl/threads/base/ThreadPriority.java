package com.yyl.threads.base;

/**
 * 线程的调度(优先级)
 * Created by Administrator on 2017/7/17/017.
 * <p>
 * 优先级 与线程休眠类似，线程的优先级仍然无法保障线程的执行次序。只不过，优先级高的线程获取CPU资源的概率较大，优先级低的并非没机会执行。
 * 线程的优先级用1-10之间的整数表示，数值越大优先级越高，默认的优先级为5。
 * 在一个线程中开启另外一个新线程，则新开线程称为该线程的子线程，子线程初始优先级与父线程相同
 */
public class ThreadPriority {
    public static void main(String[] args) {
        ThreadPriority thread = new ThreadPriority();
        Thread t1 = thread.new MyThread1();
        Thread t2 = new Thread(thread.new MyRunnable());
        t1.setPriority(10);
        t2.setPriority(1);

        t2.start();
        t1.start();
    }

    class MyThread1 extends Thread {
        public void run() {
            for (int i = 0; i < 10; i++) {
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
            for (int i = 0; i < 10; i++) {
                System.out.println("线程2第" + i + "次执行！");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

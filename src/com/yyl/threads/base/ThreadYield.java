package com.yyl.threads.base;

/**
 * * 线程的调度(让步)
 * Created by Administrator on 2017/7/17/017.
 * <p>
 * 线程的让步含义就是使当前运行着线程让出CPU资源，但是扔给谁不知道，仅仅是让出，线程状态回到可运行状态。
 * 功能是暂停当前正在执行的线程对象，并执行其他线程。
 *
 *
 * sleep()使当前线程进入停滞状态，所以执行sleep()的线程在指定的时间内肯定不会执行；yield()只是使当前线程重新回到可执行状态，所以执行yield()的线程有可能在进入到可执行状态后马上又被执行。
 2) sleep()可使优先级低的线程得到执行的机会，当然也可以让同优先级和高优先级的线程有执行的机会；yield()只能使同优先级的线程有执行的机会。
 */
public class ThreadYield {
    public static void main(String[] args) {
        Thread t1 = new MyThread1();
        Thread t2 = new Thread(new MyRunnable());

        t2.start();
        t1.start();
    }

    static class MyThread1 extends Thread {
        public void run() {
            for (int i = 0; i < 10; i++) {
                System.out.println("线程1第" + i + "次执行！");
               // Thread.yield();    1 2    1 2   1 2    1 2
            }
        }
    }

    static class MyRunnable implements Runnable {
        public void run() {
            for (int i = 0; i < 10; i++) {
                System.out.println("线程2第" + i + "次执行！");
                Thread.yield();
            }
        }
    }
}

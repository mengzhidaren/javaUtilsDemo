package com.yyl.threads.java5;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * java5 CyclicBarrier同步工具
 * <p>
 * CyclicBarrier是一个同步辅助类，它允许一组线程互相等待，直到到达某个公共屏障点(common barrier point)。在涉及一组固定大小的线程的程序中，
 * 这些线程必须不时地互相等待，此时CyclicBarrier很有用。因为该barrier 在释放等待线程后可以重用，所以称它为循环的barrier。
 * <p>
 * CyclicBarrier支持一个可选的Runnable命令，在一组线程中的最后一个线程到达之后（但在释放所有线程之前），
 * 该命令只在每个屏障点运行一次。若在继续所有参与线程之前更新共享状态，此屏障操作很有用。
 * <p>
 * 例如：组织人员（线程）郊游，约定一个时间地点（路障），人员陆续到达地点，等所有人员全部到达，开始到公园各玩各的，再到约定时间去食堂吃饭，等所有人到齐开饭……
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * CyclicBarrier和CountDownLatch的区别
 * 两个看上去有点像的类，都在java.util.concurrent下，都可以用来表示代码运行到某个点上，二者的区别在于：
 * （1）CyclicBarrier的某个线程运行到某个点上之后，该线程即停止运行，直到所有的线程都到达了这个点，所有线程才重新运行；
 *      CountDownLatch则不是，某线程运行到某个点上之后，只是给某个数值-1而已，该线程继续运行
 * （2）CyclicBarrier只能唤起一个任务，CountDownLatch可以唤起多个任务
 * （3）CyclicBarrier可重用，CountDownLatch不可重用，计数值为0该CountDownLatch就不可再用了
 * <p>
 * 链接：http://www.jianshu.com/p/31e1b3c97040
 */
public class CyclicBarrierTest {

    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool();
        final CyclicBarrier cb = new CyclicBarrier(3); //约定3个人
        for (int i = 0; i < 3; i++) { //产生3个人
            Runnable runnable = new Runnable() {
                public void run() {
                    try {
                        Thread.sleep((long) (Math.random() * 10000));
                        System.out.println("线程" + Thread.currentThread().getName() +
                                "即将到达集合地点1，当前已有" + (cb.getNumberWaiting() + 1) + "个已经到达，" + (cb.getNumberWaiting() == 2 ? "都到齐了，继续走啊" : "正在等候"));
                        cb.await();

                        Thread.sleep((long) (Math.random() * 10000));
                        System.out.println("线程" + Thread.currentThread().getName() +
                                "即将到达集合地点2，当前已有" + (cb.getNumberWaiting() + 1) + "个已经到达，" + (cb.getNumberWaiting() == 2 ? "都到齐了，继续走啊" : "正在等候"));
                        cb.await();

                        Thread.sleep((long) (Math.random() * 10000));
                        System.out.println("线程" + Thread.currentThread().getName() +
                                "即将到达集合地点3，当前已有" + (cb.getNumberWaiting() + 1) + "个已经到达，" + (cb.getNumberWaiting() == 2 ? "都到齐了，继续走啊" : "正在等候"));
                        cb.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            service.execute(runnable);
        }
        service.shutdown();
    }
}
package com.yyl.threads.java5;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CountDownLatch同步工具--控制多个线程执行顺序
 *
 * 好像倒计时计数器，调用CountDownLatch对象的countDown方法就将计数器减1，当到达0时，所有等待者就开始执行。
 * <p>
 * 一个同步辅助类，在完成一组正在其他线程中执行的操作之前，它允许一个或多个线程一直等待。用给定的计数初始化CountDownLatch。由于调用了countDown()方法，
 * 所以在当前计数到达零之前，await方法会一直受阻塞。之后，会释放所有等待的线程，await的所有后续调用都将立即返回。这种现象只出现一次——计数无法被重置。
 * 如果需要重置计数，请考虑使用CyclicBarrier。
 * <p>
 * CountDownLatch是一个通用同步工具，它有很多用途。将计数1初始化的CountDownLatch用作一个简单的开/关锁存器，或入口：在通过调用countDown()的线程打开入口前，
 * 所有调用await的线程都一直在入口处等待。用N初始化的 CountDownLatch可以使一个线程在N个线程完成某项操作之前一直等待，或者使其在某项操作完成N次之前一直等待。
 * <p>
 * CountDownLatch一个非常典型的应用场景是：有一个任务想要往下执行，但必须要等到其他的任务执行完毕后才可以继续往下执行。
 * 假如想要继续往下执行的任务调用一个CountDownLatch对象的await()方法，其他的任务执行完自己的任务后调用同一个CountDownLatch对象上的countDown()方法，
 * 这个调用await()方法的任务将一直阻塞等待，直到这个CountDownLatch对象的计数值减到0为止。
 * <p>
 * 示例：模拟发送命令与执行命令，主线程代表指挥官，新建3个线程代表战士；战士一直等待着指挥官下达命令，若指挥官没有下达命令，则战士们都必须等待。
 * 一旦命令下达，战士们都去执行自己的任务，指挥官处于等待状态，战士们任务执行完毕则报告给指挥官，指挥官则结束等待。
 */
public class CountdownLatchTest {

    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool(); // 创建一个线程池
        final CountDownLatch cdOrder = new CountDownLatch(1);// 指挥官的命令，设置为1，指挥官一下达命令，则cutDown,变为0，战士们执行任务
        final CountDownLatch cdAnswer = new CountDownLatch(3);// 因为有三个战士，所以初始值为3，每一个战士执行任务完毕则cutDown一次，当三个都执行完毕，变为0，则指挥官停止等待。

        for (int i = 0; i < 3; i++) {
            Runnable runnable = new Runnable() {
                public void run() {
                    try {
                        System.out.println("线程" + Thread.currentThread().getName() + "正准备接受命令");
                        cdOrder.await(); // 战士们都处于等待命令状态
                        System.out.println("线程" + Thread.currentThread().getName() + "已接受命令");
                        Thread.sleep((long) (Math.random() * 10000));
                        System.out.println("线程" + Thread.currentThread().getName() + "回应命令处理结果");
                        cdAnswer.countDown(); // 任务执行完毕，返回给指挥官，cdAnswer减1。
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            service.execute(runnable);// 为线程池添加任务
        }


        try {
            Thread.sleep((long) (Math.random() * 10000));
            System.out.println("线程" + Thread.currentThread().getName() + "即将发布命令");
            cdOrder.countDown(); // 发送命令，cdOrder减1，处于等待的战士们停止等待转去执行任务。
            System.out.println("线程" + Thread.currentThread().getName() + "已发送命令，正在等待结果");
            cdAnswer.await(); // 命令发送后指挥官处于等待状态，一旦cdAnswer为0时停止等待继续往下执行
            System.out.println("线程" + Thread.currentThread().getName() + "已收到所有响应结果");
        } catch (Exception e) {
            e.printStackTrace();
        }
        service.shutdown(); // 任务结束，停止线程池的所有线程

    }
}

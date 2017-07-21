package com.yyl.threads.queue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ConcurrentLinkedQueue并发队列
 *
 * ConcurrentLinkedQueue是Queue的一个安全实现．Queue中元素按FIFO原则进行排序．采用CAS操作，来保证元素的一致性。
 * LinkedBlockingQueue是一个线程安全的阻塞队列，它实现了BlockingQueue接口，
 * BlockingQueue接口继承自java.util.Queue接口，并在这个接口的基础上增加了take和put方法，这两个方法正是队列操作的阻塞版本。
 */
public class ConcurrentLinkedQueueTest {
    private static ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<Integer>();
    private static int count = 2; // 线程个数
    //CountDownLatch，一个同步辅助类，在完成一组正在其他线程中执行的操作之前，它允许一个或多个线程一直等待。
    private static CountDownLatch latch = new CountDownLatch(count);

    public static void main(String[] args) throws InterruptedException {
        long timeStart = System.currentTimeMillis();
        ExecutorService es = Executors.newFixedThreadPool(4);
        ConcurrentLinkedQueueTest.offer();
        for (int i = 0; i < count; i++) {
            es.submit(new Poll());
        }
        latch.await(); //使得主线程(main)阻塞直到latch.countDown()为零才继续执行
        System.out.println("cost time " + (System.currentTimeMillis() - timeStart) + "ms");
        es.shutdown();
    }

    /**
     * 生产
     */
    public static void offer() {
        for (int i = 0; i < 100000; i++) {
            queue.offer(i);
        }
    }


    /**
     * 消费
     *
     *
     * size()是要遍历一遍集合的，难怪那么慢，所以尽量要避免用size而改用isEmpty().
     */
    static class Poll implements Runnable {
        public void run() {
//             while (queue.size()>0) {// time 46422ms
            while (!queue.isEmpty()) {//  time  2360ms
                System.out.println(queue.poll());
            }
            latch.countDown();
        }
    }
}

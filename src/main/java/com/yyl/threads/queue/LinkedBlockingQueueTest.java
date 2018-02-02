package com.yyl.threads.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 多线程模拟实现生产者／消费者模型
 * <p>
 * ConcurrentLinkedQueue是Queue的一个安全实现．Queue中元素按FIFO原则进行排序．采用CAS操作，来保证元素的一致性。
 * LinkedBlockingQueue是一个线程安全的阻塞队列，它实现了BlockingQueue接口，
 * BlockingQueue接口继承自java.util.Queue接口，并在这个接口的基础上增加了take和put方法，这两个方法正是队列操作的阻塞版本。
 * <p>
 * <p>
 * add(anObject)：
 * 把anObject添加到BlockingQueue里，添加成功返回true，如果BlockingQueue空间已满则抛出异常。
 * offer(anObject)：
 * 表示如果可能的话，将anObject加到BlockingQueue里，即如果BlockingQueue可以容纳，则返回true，否则返回false。
 * put(anObject)：
 * 把anObject加到BlockingQueue里，如果BlockingQueue没有空间，则调用此方法的线程被阻断直到BlockingQueue里有空间再继续。
 * poll(time)：
 * 获取并移除此队列的头，若不能立即取出，则可以等time参数规定的时间，取不到时返回null。
 * take()：
 * 获取BlockingQueue里排在首位的对象，若BlockingQueue为空，阻断进入等待状态直到BlockingQueue有新的对象被加入为止。
 * clear()：
 * 从队列彻底移除所有元素。
 * remove()方法直接删除队头的元素
 * peek()方法直接取出队头的元素，并不删除
 */
public class LinkedBlockingQueueTest {
    /**
     * 定义装苹果的篮子
     */
    public class Basket {
        // 篮子，能够容纳3个苹果
        BlockingQueue<String> basket = new LinkedBlockingQueue<String>(3);

        // 生产苹果，放入篮子
        public void produce() throws InterruptedException {
            // put方法放入一个苹果，若basket满了，等到basket有位置
            basket.put("An apple");
        }

        // 消费苹果，从篮子中取走
        public String consume() throws InterruptedException {
            // take方法取出一个苹果，若basket为空，等到basket有苹果为止(获取并移除此队列的头部)
            return basket.take();
        }
    }

    // 定义苹果生产者
    class Producer implements Runnable {
        private String instance;
        private Basket basket;

        public Producer(String instance, Basket basket) {
            this.instance = instance;
            this.basket = basket;
        }

        public void run() {
            try {
                while (true) {
                    // 生产苹果
                    System.out.println("生产者准备生产苹果：" + instance);
                    basket.produce();
                    System.out.println("!生产者生产苹果完毕：" + instance);
                    // 休眠300ms
                    Thread.sleep(300);
                }
            } catch (InterruptedException ex) {
                System.out.println("Producer Interrupted");
            }
        }
    }

    // 定义苹果消费者
    class Consumer implements Runnable {
        private String instance;
        private Basket basket;

        public Consumer(String instance, Basket basket) {
            this.instance = instance;
            this.basket = basket;
        }

        public void run() {
            try {
                while (true) {
                    // 消费苹果
                    System.out.println("消费者准备消费苹果：" + instance);
                    System.out.println(basket.consume());
                    System.out.println("!消费者消费苹果完毕：" + instance);
                    // 休眠1000ms
                    Thread.sleep(1000);
                }
            } catch (InterruptedException ex) {
                System.out.println("Consumer Interrupted");
            }
        }
    }

    //    public static void main(String[] args) {
//        LinkedBlockingQueueTest base = new LinkedBlockingQueueTest();
//
//        // 建立一个装苹果的篮子
//        Basket basket = base.new Basket();
//
//        ExecutorService service = Executors.newCachedThreadPool();
//        Producer producer = base.new Producer("生产者001", basket);
//        Producer producer2 = base.new Producer("生产者002", basket);
//        Consumer consumer = base.new Consumer("消费者003", basket);
//        service.submit(producer);
//        service.submit(producer2);
//        service.submit(consumer);
//        // 程序运行5s后，所有任务停止
//        try {
//            Thread.sleep(1000 * 5);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        service.shutdownNow();
//    }

    /**
     * 阻塞队列的典型例子是BlockingQueue，非阻塞队列的典型例子是ConcurrentLinkedQueue
     *
     * @param args
     */
    public static void main(String[] args) {
        //LinkedBlockingQueue实现是线程安全的，实现了先进先出等特性，是作为生产者消费者的首选
        //put方法在队列满的时候会阻塞直到有队列成员被消费，
        //take方法在队列空的时候会阻塞，直到有队列成员被放进来
        BlockingQueue<String> basket = new LinkedBlockingQueue<String>(2);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        basket.put("one");
                        basket.put("two");
                        System.out.println("  add： presce");
                        basket.put("thread");
                        System.out.println("  add：over");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    System.out.println("take  " + basket.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
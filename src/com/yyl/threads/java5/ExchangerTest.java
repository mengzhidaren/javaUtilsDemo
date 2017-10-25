package com.yyl.threads.java5;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * java5 Exchanger数据交换
 * Java并发API提供了一种允许2个并发任务间相互交换数据的同步应用。更具体的说，Exchanger类允许在2个线程间定义同步点，当2个线程到达这个点，他们相互交换数据类型，
 * 使用第一个线程的数据类型变成第二个的，然后第二个线程的数据类型变成第一个的。
 * 用于实现两个人之间的数据交换，每个人在完成一定的事务后想与对方交换数据，第一个先拿出数据的人将一直等待第二个人拿着数据到来时，才能彼此交换数据。
 */
public class ExchangerTest {

    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool();
        final Exchanger<String> exchanger = new Exchanger();
        service.execute(new Runnable() {//A
            public void run() {
                try {

                    String data1 = "张三";
                    System.out.println("线程A    " + Thread.currentThread().getName() + "正在把数据'" + data1 + "'换出去");
                    Thread.sleep((long) (Math.random() * 10000));
                    String data2 = exchanger.exchange(data1);//阻塞在这里等线程B  执行交换数据
                    System.out.println("线程A    " + Thread.currentThread().getName() + "换回的数据为'" + data2 + "'");
                } catch (Exception e) {

                }
            }
        });
        service.execute(new Runnable() {//B
            public void run() {
                try {
                    String data1 = "李四";
                    System.out.println("线程B    " + Thread.currentThread().getName() + "正在把数据'" + data1 + "'换出去");
                    Thread.sleep((long) (Math.random() * 10000));

                    String data2 = exchanger.exchange(null);///阻塞在这里等线程A    执行交换数据
                    System.out.println("线程B    " + Thread.currentThread().getName() + "换回的数据为'" + data2 + "'");
                } catch (Exception e) {

                }
            }
        });

        service.execute(new Runnable() {//c
            public void run() {
                try {
                    String data1 = "王五";
                    System.out.println("线程C    " + Thread.currentThread().getName() + "正在把数据'" + data1 + "'换出去");
                    Thread.sleep((long) (Math.random() * 10000));
                    String data2 =exchanger.exchange("王五");///阻塞在这里等线程A    执行交换数据
                    System.out.println("线程C    " + Thread.currentThread().getName() + "换回的数据为'" + data2 + "'");
                } catch (Exception e) {

                }
            }
        });
        service.execute(new Runnable() {//c
            public void run() {
                try {
                    String data1 = "六六";
                    System.out.println("线程D    " + Thread.currentThread().getName() + "正在把数据'" + data1 + "'换出去");
                    Thread.sleep((long) (Math.random() * 10000));
                    String data2 = exchanger.exchange("六六");///阻塞在这里等线程A    执行交换数据
                    System.out.println("线程D    " + Thread.currentThread().getName() + "换回的数据为'" + data2 + "'");
                } catch (Exception e) {

                }
            }
        });
    }

    /**
     * 返回结果：
     *
     * 线程pool-1-thread-1正在把数据'张三'换出去
     *线程pool-1-thread-2正在把数据'李四'换出去
     *线程pool-1-thread-1换回的数据为'李四'
     *线程pool-1-thread-2换回的数据为'张三'
     */
}

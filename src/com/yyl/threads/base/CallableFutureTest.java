package com.yyl.threads.base;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Java多线程-新特性-有返回值的线程
 * <p>
 * <p>
 * 执行Callable任务后，可以获取一个Future的对象，在该对象上调用get就可以获取到Callable任务返回的Object了。
 *
 * get方法会阻塞 直到任务返回结果
 *
 */
public class CallableFutureTest {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws ExecutionException,
            InterruptedException {
        CallableFutureTest test = new CallableFutureTest();

        // 创建一个线程池
        ExecutorService pool = Executors.newFixedThreadPool(2);
        // 创建两个有返回值的任务
        Callable c1 = test.new MyCallable("A");
        Callable c2 = test.new MyCallable("B");
        System.out.println("start"+System.currentTimeMillis());
        // 执行任务并获取Future对象
        Future f1 = pool.submit(c1);
        Future f2 = pool.submit(c2);
        System.out.println("end"+System.currentTimeMillis());
        // 从Future对象上获取任务的返回值，并输出到控制台
        System.out.println(">>>" + f1.get().toString());
        System.out.println(">>>" + f2.get().toString());
        System.out.println("end2"+System.currentTimeMillis());
        // 关闭线程池
        pool.shutdown();
    }

    @SuppressWarnings("unchecked")
    class MyCallable implements Callable {
        private String name;

        MyCallable(String name) {
            this.name = name;
        }

        public Object call() throws Exception {
            if (name.equals("A")){
                Thread.sleep(6000);
            }else{
                Thread.sleep(4000);
            }
            return name + "任务返回的内容";
        }
    }
}
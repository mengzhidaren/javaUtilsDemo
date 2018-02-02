package com.yyl.threads.mutithreads;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * Created by Administrator on 2017/7/19/019.
 * <p>
 * <p>
 * * get()和join()有两个主要的区别：
 * join()方法同步返回,不能被中断。如果你中断调用join()方法的线程，这个方法将抛出InterruptedException异常。如果任务抛出任何未受检异常，
 * get()方法异步返回将返回一个ExecutionException异常，而join()方法将返回一个RuntimeException异常。
 * <p>
 * fork(); // 执行子任务
 * join(); // 子任务结束后返回对应结果
 * 异步执行命令 invoke()和invokeAll();
 */
public class ForkJoinPoolTest2 {

    public static void main(String[] args) {
        MyTask mt = new MyTask(1);

        ForkJoinPool forkJoinPool = new ForkJoinPool();

        Future<Integer> result = forkJoinPool.submit(mt);

        try {
            System.out.println(result.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        forkJoinPool.shutdown();
    }

    //计算1*1+2*2+3*3+...........100*100的值
    @SuppressWarnings("serial")
    public static class MyTask extends RecursiveTask<Integer> {
        int i;

        public MyTask(int i) {
            this.i = i;
        }

        @Override
        protected Integer compute() {
            if (i >= 100) {
                System.out.println("over  i=" + i);
                return i * i;
            }

            MyTask newTask2 = new MyTask(i + 1);
            newTask2.fork();
            System.out.println("i=" + i);
            return i * i + newTask2.join();

        }

    }

    public void test2() {
        //1到1亿，相加等于几   0.37
        Demo demo1 = new Demo(1, 50000000);
        Demo demo2 = new Demo(50000001, 100000000);
        demo1.fork();
        demo2.fork();
        System.out.println(demo1.join() + demo2.join());
    }

    public void test3() {
        ForkJoinPool fjp = new ForkJoinPool();
        Demo demo1 = new Demo(1, 50000000);
        Demo demo2 = new Demo(50000001, 100000000);
        System.out.println(fjp.invoke(demo1) + fjp.invoke(demo2));
    }

    static class Demo extends java.util.concurrent.RecursiveTask<Integer> {
        int start;
        int end;

        public Demo(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            int sum = 0;
            int s = start;
            int e = end;
            for (; s <= end; s++) {
                sum += s;
            }
            return sum;
        }
    }
}

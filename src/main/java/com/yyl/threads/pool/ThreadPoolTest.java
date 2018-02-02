package com.yyl.threads.pool;

import java.util.concurrent.*;

/**
 * Java多线程-新特性-线程池
 * <p>
 * <p>
 * <p>
 * Sun在Java5中，对Java线程的类库做了大量的扩展，其中线程池就是Java5的新特征之一
 * 有关Java5线程新特征的内容全部在java.util.concurrent下面
 * 在Java5之前，要实现一个线程池是相当有难度的，现在Java5为我们做好了一切
 * <p>
 * Created by Administrator on 2017/7/17/017.
 * <p>
 * Java5提供5种类型的线程池，分别如下：
 * <p>
 * 一：newCachedThreadPool-可变尺寸的线程池(缓存线程池)
 * (1)缓存型池子，先查看池中有没有以前建立的线程，如果有，就reuse(重用)，如果没有，就建立一个新的线程加入池中；
 * (2)缓存型池子，通常用于执行一些生存周期很短的异步型任务；因此一些面向连接的daemon型server中用得不多；
 * (3)能reuse(重用)的线程，必须是timeout IDLE内的池中线程，缺省timeout是60s，超过这个IDLE时长，线程实例将被终止及移出池；
 * (4)注意，放入CachedThreadPool的线程不必担心其结束，超过TIMEOUT不活动，其会自动被终止。
 * <p>
 * 二：newFixedThreadPool-固定大小的线程池
 * (1)newFixedThreadPool与cacheThreadPool差不多，也是能reuse就用，但不能随时建新的线程；
 * (2)其独特之处:任意时间点，最多只能有固定数目的活动线程存在，此时如果有新的线程要建立，只能放在另外的队列中等待，直到当前的线程中某个线程终止直接被移出池子；
 * (3)和cacheThreadPool不同，FixedThreadPool没有IDLE机制(可能也有，但既然文档没提，肯定非常长，类似依赖上层的TCP或UDP IDLE机制之类的)，所以FixedThreadPool多数针对一些很稳定很固定的正规并发线程，多用于服务器；
 * (4)从方法的源代码看，cache池和fixed池调用的是同一个底层池，只不过参数不同:
 * fixed池线程数固定，并且是0秒IDLE(无IDLE)；
 * cache池线程数支持0-Integer.MAX_VALUE(显然完全没考虑主机的资源承受能力)，60秒IDLE。
 * <p>
 * 三：ScheduledThreadPool-调度线程池
 * (1)调度型线程池；
 * (2)这个池子里的线程可以按schedule依次delay执行，或周期执行。
 * <p>
 * 四：SingleThreadExecutor-单例线程池
 * (1)单例线程，任意时间池中只能有一个线程；
 * (2)用的是和cache池和fixed池相同的底层池，但线程数目是1-1,0秒IDLE(无IDLE)。
 * 五、自定义线程池--ThreadPoolExecutor
 * <p>
 * <p>
 * <p>
 * 1.shutDown()  关闭线程池，不影响已经提交的任务
 * 2.shutDownNow() 关闭线程池，并尝试去终止正在执行的线程
 * 3.allowCoreThreadTimeOut(boolean value) 允许核心线程闲置超时时被回收
 * 4.submit 一般情况下我们使用execute来提交任务，但是有时候可能也会用到submit，使用submit的好处是submit有返回值
 */
public class ThreadPoolTest {
    public static void main(String[] args) {
        ThreadPoolTest test = new ThreadPoolTest();

        //创建一个可重用固定线程数的线程池
        ExecutorService pool = Executors.newFixedThreadPool(2);
        //创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口
        Thread t1 = test.new MyThread();
        Thread t2 = test.new MyThread();
        Thread t3 = test.new MyThread();
        Thread t4 = test.new MyThread();
        Thread t5 = test.new MyThread();
        //将线程放入池中进行执行
        pool.execute(t1);
        pool.execute(t2);
        pool.execute(t3);
        pool.execute(t4);
        pool.execute(t5);
        //关闭线程池
        pool.shutdown();
    }

    class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "正在执行。");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " 中断");
                e.printStackTrace();
            }
        }
    }

//    public static class ThreadPoolTest2 {
//        private static double[] d;
//        private class ThreadPoolExecutorTask implements Callable<Integer> {
//            private int first;
//            private int last;
//            public ThreadPoolExecutorTask(int first, int last) {
//                this.first = first;
//                this.last = last;
//            }
//            public Integer call() {
//                int subCount = 0;
//                for (int i = first; i <= last; i++) {
//                    if (d[i] < 0.5) {
//                        subCount++;
//                    }
//                }
//                return subCount;
//            }
//        }
//        public static void main(String[] args) {
//            d = createArrayOfRandomDoubles();
//            ThreadPoolExecutor tpe = new ThreadPoolExecutor(4, 4, Long.MAX_VALUE, TimeUnit.SECONDS, new LinkedBlockingQueue());
//            Future[] f = new Future[4];
//            int size = d.length / 4;
//            for (int i = 0; i < 3; i++) {
//                f[i] = tpe.submit(new ThreadPoolExecutorTask(i * size, (i + 1) * size - 1);
//            }
//            f[3] = tpe.submit(new ThreadPoolExecutorTask(3 * size, d.length - 1);
//            int n = 0;
//            for (int i = 0; i < 4; i++) {
//                n += f.get();
//            }
//            System.out.println("Found " + n + " values");
//        }
//    }
}




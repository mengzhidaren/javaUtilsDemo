package com.yyl.threads.base;

/**
 * 同步块（或方法）和 volatile 变量。这两种机制的提出都是为了实现代码线程的安全性。其中 Volatile 变量的同步性较差（但有时它更简单并且开销更低），而且其使用也更容易出错。
 * public volatile boolean exit = false;
 * 在定义exit时，使用了一个Java关键字volatile，这个关键字的目的是使exit同步，也就是说在同一时刻只能由一个线程来修改exit的值.
 * <p>
 * <p>
 * volatile关键字用于声明简单类型变量，如int、float、boolean等数据类型。如果这些简单数据类型声明为volatile，对它们的操作就会变成原子级别的。但这有一定的限制。
 * 例如，下面的例子中的 n 就不是原子级别的
 * <p>
 * <p>
 * volatile的主要作用是，修改对任何线程立即可见。
 */
public class ThreadVolatile extends Thread {
    public static volatile int n = 0;

    public void run() {
        for (int i = 0; i < 10; i++)
            try {
                n = n + 1;
                sleep(3); // 为了使运行结果更随机，延迟3毫秒
            } catch (Exception e) {
            }
    }

    public static void main(String[] args) throws Exception {

        Thread threads[] = new Thread[100];
        for (int i = 0; i < threads.length; i++)
            // 建立100个线程
            threads[i] = new ThreadVolatile();
        for (int i = 0; i < threads.length; i++)
            // 运行刚才建立的100个线程
            threads[i].start();
        for (int i = 0; i < threads.length; i++)
            // 100个线程都执行完后继续
            threads[i].join();
        System.out.println("n=" + ThreadVolatile.n);
        //结果应该为n=1000，而在执行上面代码时，很多时侯输出的n都小于1000，这说明n=n+1不是原子级别的操作
        //原因是声明为volatile的简单变量如果当前值由该变量以前的值相关，那么volatile关键字不起作用，也就是说如下的表达式都不是原子操作
        //n = n + 1;
        //n++
        //如果要想使这种情况变成原子操作，需要使用synchronized关键字
    }
}

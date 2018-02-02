package com.yyl.threads.base;

/**
 * Volatile关键字的作用
 * <p>
 * 一个非常重要的问题，是每个学习、应用多线程的Java程序员都必须掌握的。理解volatile关键字的作用的前提是要理解Java内存模型，这里就不讲Java内存模型了，可以参见第31点，
 * volatile关键字的作用主要有两个：
 * （1）多线程主要围绕可见性和原子性两个特性而展开，使用volatile关键字修饰的变量，保证了其在多线程之间的可见性，即每次读取到volatile变量，一定是最新的数据
 * （2）代码底层执行不像我们看到的高级语言—-Java程序这么简单，它的执行是Java代码–>字节码–>根据字节码执行对应的C/C++代码–>C/C++代码被编译成汇编语言–>和硬件电路交互，
 * 现实中，为了获取更好的性能JVM可能会对指令进行重排序，多线程下可能会出现一些意想不到的问题。使用volatile则会对禁止语义重排序，当然这也一定程度上降低了代码执行效率
 * 从实践角度而言，volatile的一个重要作用就是和CAS结合，保证了原子性，详细的可以参见java.util.concurrent.atomic包下的类，比如AtomicInteger。
 * <p>
 * 链接：http://www.jianshu.com/p/31e1b3c97040
 */
public class ThreadVolatile2 extends Thread {
    public static volatile int n = 0;

    public static synchronized void inc() {
        n++;
    }

    public void run() {
        for (int i = 0; i < 10; i++)
            try {
                inc(); // n = n + 1 改成了 inc();
                sleep(3); // 为了使运行结果更随机，延迟3毫秒

            } catch (Exception e) {
            }
    }

    public static void main(String[] args) throws Exception {

        Thread threads[] = new Thread[100];
        for (int i = 0; i < threads.length; i++)
            // 建立100个线程
            threads[i] = new ThreadVolatile2();
        for (int i = 0; i < threads.length; i++)
            // 运行刚才建立的100个线程
            threads[i].start();
        for (int i = 0; i < threads.length; i++)
            // 100个线程都执行完后继续
            threads[i].join();
        System.out.println("n=" + ThreadVolatile2.n);

        //其中inc方法使用了synchronized关键字进行方法同步   结果为n=1000
        //当变量的值由自身的上一个决定时，如n=n+1、n++等，volatile关键字将失效
        //只有当变量的值和自身上一个值无关时对该变量的操作才是原子级别的，如n = m + 1，使用volatile就是原子级别的。所以在使用volatile关键时一定要谨慎
        //并不是只要简单类型变量使用volatile修饰就对这个变量的所有操作都是原来操作
        //只有当变量的值和自身上一个值无关时对该变量的操作才是原子级别的
        //volatile的主要作用是，修改对任何线程立即可见。
    }
}

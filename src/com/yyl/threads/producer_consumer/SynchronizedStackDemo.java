package com.yyl.threads.producer_consumer;

/**
 * Created by Administrator on 2017/7/17/017.
 */
public class SynchronizedStackDemo {
    public static void main(String[] args) {
        // 下面的消费者类对象和生产者类对象所操作的是同一个同步堆栈对象
        SynchronizedStack stack = new SynchronizedStack(5);
        Runnable source = new Producer(stack);
        Runnable sink = new Consumer(stack);

        Thread t1 = new Thread(source);
        Thread t2 = new Thread(sink);
        t1.start();
        t2.start();
    }

}

/**
 * 生产者
 */
class Producer implements Runnable {
    private SynchronizedStack stack;

    public Producer(SynchronizedStack s) {
        stack = s;
    }

    public void run() {
        char ch;
        for (int i = 0; i < 100; i++) {
            // 随机产生100个字符
            ch = (char) (Math.random() * 26 + 'A');
            stack.push(ch);
            System.out.println("Produced:" + ch);
            try {
                // 每产生一个字符线程就睡眠一下
                Thread.sleep((int) (Math.random() * 1000));
            } catch (InterruptedException e) {

            }
        }
    }

}

/**
 * 同步堆栈类
 */
class SynchronizedStack {
    private int index = 0;
    private int size = 100;
    // 内存共享区
    private char[] data;

    public SynchronizedStack(int size) {
        System.out.println("栈被创建");
        this.size = size;
        data = new char[size];
    }

    /**
     * 生产数据
     *
     * @param c
     */
    public synchronized void push(char c) {
        while (index == size) {
            try {
                System.err.println("栈满了");
                this.wait();// 等待，直到有数据出栈
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
        data[index] = c;
        index++;
        this.notify();// 通知其它线程把数据出栈
    }

    /**
     * 消费数据
     *
     * @return
     */
    public synchronized char pop() {
        while (index == 0) {
            try {
                System.err.println("栈空了");
                this.wait();// 等待，直到有数据出栈
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
        index--; // 指针向下移动
        char ch = data[index];
        this.notify(); // 通知其它线程把数据入栈
        return ch;
    }

    // 显示堆栈内容
    public synchronized void print() {
        for (int i = 0; i < data.length; i++) {
            System.out.print(data[i]);
        }
        System.out.println();
        this.notify(); // 通知其它线程显示堆栈内容
    }
}

/**
 * 消费者
 */
class Consumer implements Runnable {
    private SynchronizedStack stack;

    public Consumer(SynchronizedStack s) {
        stack = s;
    }

    public void run() {
        char ch;
        for (int i = 0; i < 100; i++) {
            ch = stack.pop();
            System.out.println("Consumed:" + ch);
            try {
                // 每产生一个字符线程就睡眠一下
                Thread.sleep((int) (Math.random() * 1000));
            } catch (InterruptedException e) {
            }
        }
    }

}
package com.yyl.threads.base;

/**
 * Created by Administrator on 2017/7/17/017.
 * <p>
 * 同步哪些修改变量的代码，使用synchronized关键字同步方法或代码。
 * 当然这不是唯一控制并发安全的途径。
 * <p>
 * synchronized关键字使用说明
 * synchronized只能标记非抽象的方法，不能标识成员变量。
 */
public class ThreadSynchronized {
    /**
     * 余额
     */
    private int cash;

    /**
     * 同步方法
     */
    public synchronized void oper(int x) {
        try {
            Thread.sleep(10L);
            this.cash += x;
            System.out.println("线程" + Thread.currentThread().getName() + "运行结束，增加“" + x
                    + "”，当前用户账户余额为：" + cash);
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步代码块
     */
    public void oper2(int x) {
        try {
            Thread.sleep(10L);
            synchronized (this) {
                this.cash += x;
                System.out.println("线程" + Thread.currentThread().getName() + "运行结束，增加“" + x
                        + "”，当前用户账户余额为：" + cash);
            }
            Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

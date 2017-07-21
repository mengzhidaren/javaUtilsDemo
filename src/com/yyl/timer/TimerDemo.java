package com.yyl.timer;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Timer类的常用其他方法：
 * cancel()
 * 终止此计时器，丢弃所有当前已安排的任务。
 * purge()
 * 从此计时器的任务队列中移除所有已取消的任务。
 * schedule(TimerTask task, Date time)
 * 安排在指定的时间执行指定的任务。
 * <p>
 * TimerTask类的常用其他方法：
 * cancel()
 * 取消此计时器任务。
 * run()
 * 此计时器任务要执行的操作。
 * scheduledExecutionTime()
 * 返回此任务最近实际 执行的已安排 执行时间。
 */
public class TimerDemo {
    public static void main(String[] args) {
        timer1();
        //timer2();
        //timer3();
        //timer4();
    }

    // 第一种方法：设定指定任务task在指定时间time执行 schedule(TimerTask task, Date time)
    public static void timer1() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("-------设定要指定任务--------");
            }
        }, 2000);// 设定指定的时间time,此处为2000毫秒
    }

    // 第二种方法：设定指定任务task在指定延迟delay后进行固定延迟peroid的执行
    // schedule(TimerTask task, long delay, long period)
    public static void timer2() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("-------设定要指定任务--------");
            }
        }, 1000, 5000);
    }

    // 第三种方法：设定指定任务task在指定延迟delay后进行固定频率peroid的执行。
    // scheduleAtFixedRate(TimerTask task, long delay, long period)
    public static void timer3() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                System.out.println("-------设定要指定任务--------");
            }
        }, 1000, 2000);
    }

    // 第四种方法：安排指定的任务task在指定的时间firstTime开始进行重复的固定速率period执行．
    // Timer.scheduleAtFixedRate(TimerTask task,Date firstTime,long period)
    public static void timer4() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12); // 控制时
        calendar.set(Calendar.MINUTE, 0);    // 控制分
        calendar.set(Calendar.SECOND, 0);    // 控制秒

        Date time = calendar.getTime();     // 得出执行任务的时间,此处为今天的12：00：00

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                System.out.println("-------设定要指定任务--------");
            }
        }, time, 1000 * 60 * 60 * 24);// 这里设定将延时每天固定执行
    }
}
package com.yyl.threads.mutithreads;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 什么是Fork/Join框架
 * Fork/Join框架是Java7提供了的一个用于并行执行任务的框架， 是一个把大任务分割成若干个小任务，最终汇总每个小任务结果后得到大任务结果的框架。
 * <p>
 * <p>
 * Fork/Join框架主要有以下两个类组成.
 * ForkJoinPool 这个类实现了ExecutorService接口和工作窃取算法(Work-Stealing Algorithm).它管理工作者线程,并提供任务的状态信息,以及任务的执行信息
 * ForkJoinTask 这个类是一个将在ForkJoinPool执行的任务的基类.
 * <p>
 * Fork/Join框架提供了在一个任务里执行fork()和join()操作的机制和控制任务状态的方法.通常,为了实现Fork/Join任务,需要实现一个以下两个类之一的子类
 * RecursiveAction 用于任务没有返回值的场景
 * RecursiveTask 用于任务有返回值的场景.
 * <p>
 * <p>
 * <p>
 * ForkJoinPool这个类,它的核心就是要完成某一个目标任务,如果目标任务太大,那么就创建多个子任务.然后一直等待这些子任务完成.最终完成之前定下的目标任务
 * <p>
 * <p>
 * 1    当需要处理递归分治算法时，考虑使用ForkJoinPool。
 * 2    仔细设置不再进行任务划分的阈值，这个阈值对性能有影响。
 * 3    Java 8中的一些特性会使用到ForkJoinPool中的通用线程池。在某些场合下，需要调整该线程池的默认的线程数量。
 */
public class ForkJoinPoolTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinPool pool = new ForkJoinPool();
        //先定个小目标,先赚个一百万吧
        ForkJoinTask<Double> task = pool.submit(new MakeMoneyTask(1000000));
        do {
            try {
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!task.isDone());
        pool.shutdown();
        System.out.println(task.get());
    }


    public static class MakeMoneyTask extends RecursiveTask<Double> {

        private static final double MIN_GOAL_MONEY = 100000;
        private double goalMoney;
        private String name;
        private static final AtomicLong employeeNo = new AtomicLong();

        public MakeMoneyTask(double goalMoney) {
            this.goalMoney = goalMoney;
            this.name = "员工" + employeeNo.getAndIncrement() + "号";
        }

        @Override
        protected Double compute() {
            if (this.goalMoney < MIN_GOAL_MONEY) {
                System.out.println(name + ": 老板交代了,要赚 " + goalMoney + " 元,为了买车买房,加油吧....");
                return makeMoney();
            } else {
                int subThreadCount = ThreadLocalRandom.current().nextInt(10) + 2;
                System.out.println(name + ": 上级要我赚 " + goalMoney + ", 有点小多,没事让我" + subThreadCount + "个手下去完成吧," +
                        "每人赚个 " + Math.ceil(goalMoney * 1.0 / subThreadCount) + "元应该没问题...");
                List<MakeMoneyTask> tasks = new ArrayList<>();
                for (int i = 0; i < subThreadCount; i++) {
                    tasks.add(new MakeMoneyTask(goalMoney / subThreadCount));
                }
                Collection<MakeMoneyTask> makeMoneyTasks = invokeAll(tasks);
                double sum = 0;
                for (MakeMoneyTask moneyTask : makeMoneyTasks) {
                    try {
                        sum += moneyTask.get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(name + ": 嗯,不错,效率还可以,终于赚到 " + sum + "元,赶紧邀功去....");
                return sum;
            }
        }

        private Double makeMoney() {
            int sum = 0;
            int day = 1;
            try {
                while (true) {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(500));
                    double money = ThreadLocalRandom.current().nextDouble(MIN_GOAL_MONEY / 3);
                    System.out.println(name + ": 在第 " + (day++) + " 天赚了" + money);
                    sum += money;
                    if (sum >= goalMoney) {
                        System.out.println(name + ": 终于赚到 " + sum + " 元, 可以交差了...");
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return goalMoney;
        }
    }
}

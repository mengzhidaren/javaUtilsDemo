package com.yyl.threads.java7;



import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 *
 *
 *
 */
public class ForkJoinPoolTest {
    public static void main(String[] args) throws Exception {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Future<Integer> result = forkJoinPool.submit(new Calculator(1, 10000));
        System.out.println(result.get());
    }


    static class Calculator extends RecursiveTask<Integer> {
        private static final long serialVersionUID = 7333472779649130114L;

        private static final int THRESHOLD = 10;
        private int start;
        private int end;

        public Calculator(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public Integer compute() {
            int sum = 0;
            if ((end - start) < THRESHOLD) {    // 当问题分解到可求解程度时直接计算结果
                for (int i = start; i <= end; i++) {
                    sum += i;
                }
            } else {
                int middle = (start + end) >>> 1;
                // 将任务一分为二
                Calculator left = new Calculator(start, middle);
                Calculator right = new Calculator(middle + 1, end);
                left.fork();
                right.fork();
                // 注意：由于此处是递归式的任务分解，也就意味着接下来会二分为四，四分为八...

                sum = left.join() + right.join();   // 合并两个子任务的结果
            }
            return sum;
        }

    }
}

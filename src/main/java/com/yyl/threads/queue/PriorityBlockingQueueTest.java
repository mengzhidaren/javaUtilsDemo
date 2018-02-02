package com.yyl.threads.queue;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * PriorityBlockingQueue里面存储的对象必须是实现Comparable接口。队列通过这个接口的compare方法确定对象的priority。
 * PriorityBlockingQueue是一个基于优先级堆的无界的并发安全的优先级队列（FIFO），队列的元素按照其自然顺序进行排序
 * ，或者根据构造队列时提供的 Comparator 进行排序，具体取决于所使用的构造方法。
 * <p>
 * <p>
 * 由于PriorityBlockingQueue是基于堆的，所以这里简单介绍下堆的结构。堆是一种二叉树结构，堆的根元素是整个树的最大值或者最小值（称为大顶堆或者小顶堆），
 * 同时堆的每个子树都是满足堆的树结构。由于堆的顶部是最大值或者最小值，所以每次从堆获取数据都是直接获取堆顶元素，然后再将堆调整成堆结构。
 * <p>
 * <p>
 * 使用场景
 * PriorityBlockingQueue与普通阻塞队列的不同之处就是在于其支持对队列中的元素进行比较，而已决定出队的顺序，所以可以使用PriorityBlockingQueue实现高优先级的线程优先执行。
 */
public class PriorityBlockingQueueTest {


    public static void main(String[] args) throws InterruptedException {

        ExecutorService exec = Executors.newCachedThreadPool();
        PriorityBlockingQueue<Runnable> queue = new PriorityBlockingQueue<Runnable>();
        exec.execute(new PrioritizedTaskProducer(queue, exec));
        try {
            TimeUnit.MILLISECONDS.sleep(250);
        } catch (InterruptedException e) {
        }
        exec.execute(new PrioritizedTaskConsumer(queue));

    }


    static class PrioritizedTask implements Runnable, Comparable<PrioritizedTask> {
        private Random rand = new Random(47);
        private static int counter = 0;
        private final int id = counter++;
        private final int priority;

        protected static List<PrioritizedTask> sequence = new ArrayList<PrioritizedTask>();

        public PrioritizedTask(int priority) {
            this.priority = priority;
            sequence.add(this);
        }

        @Override
        public int compareTo(PrioritizedTask o) {
            //复写此方法进行任务执行优先级排序
//      return priority < o.priority ? 1 :
//          (priority > o.priority ? -1 : 0);
            if (priority < o.priority) {
                return -1;
            } else {
                if (priority > o.priority) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }

        @Override
        public void run() {
            //执行任务代码..
            try {
                TimeUnit.MILLISECONDS.sleep(rand.nextInt(250));
            } catch (InterruptedException e) {

            }
            System.out.println(this);
        }

        @Override
        public String toString() {
            return String.format("[%1$-3d]", priority) + " Task id : " + id;
        }

        public String summary() {
            return "( Task id : " + id + " _priority : " + priority + ")";
        }

        /**
         * 结束所有任务
         */
        static class EndSentinel extends PrioritizedTask {
            private ExecutorService exec;

            public EndSentinel(ExecutorService e) {
                super(Integer.MAX_VALUE);
                exec = e;
            }

            public void run() {
                int count = 0;
                for (PrioritizedTask pt : sequence) {
                    System.out.print(pt.summary());
                    if (++count % 5 == 0) {
                        System.out.println();
                    }
                }
                System.out.println();
                System.out.println(this + "Calling shutdownNow()");
                exec.shutdownNow();
            }
        }
    }

    /**
     * 制造一系列任务,分配任务优先级
     */
    static class PrioritizedTaskProducer implements Runnable {
        private Random rand = new Random(47);
        private Queue<Runnable> queue;
        private ExecutorService exec;

        public PrioritizedTaskProducer(Queue<Runnable> q, ExecutorService e) {
            queue = q;
            exec = e;
        }

        @Override
        public void run() {

            for (int i = 0; i < 20; i++) {
                queue.add(new PrioritizedTask(rand.nextInt(10)));
                Thread.yield();
            }

            try {
                for (int i = 0; i < 10; i++) {
                    TimeUnit.MILLISECONDS.sleep(250);
                    queue.add(new PrioritizedTask(10));
                }

                for (int i = 0; i < 10; i++) {
                    queue.add(new PrioritizedTask(i));
                }

                queue.add(new PrioritizedTask.EndSentinel(exec));

            } catch (InterruptedException e) {

            }

            System.out.println("Finished PrioritizedTaskProducer");
        }
    }


    /**
     * 使用PriorityBlockingQueue进行任务按优先级同步执行
     */
    static class PrioritizedTaskConsumer implements Runnable {
        private PriorityBlockingQueue<Runnable> q;

        public PrioritizedTaskConsumer(PriorityBlockingQueue<Runnable> q) {
            this.q = q;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    q.take().run();
                }
            } catch (InterruptedException e) {
            }
            System.out.println("Finished PrioritizedTaskConsumer");
        }

    }
}
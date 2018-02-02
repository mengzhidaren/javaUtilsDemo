package com.yyl.threads.java7;

/**
 *
 * Java 7中引入了TransferQueue，它比BlockingQueue多了一个叫transfer的方法，如果接收线程处于等待状态，该操作可以马上将任务交给它，
 * 否则就会阻塞直至取走该任务的线程出现。可以用TransferQueue代替BlockingQueue，因为它可以获得更好的性能。
 *
 */
public class TransferQueueTest {
}

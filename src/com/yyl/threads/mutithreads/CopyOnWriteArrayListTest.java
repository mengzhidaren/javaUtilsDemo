package com.yyl.threads.mutithreads;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Copy-On-Write简称COW，是一种用于程序设计中的优化策略。其基本思路是，从一开始大家都在共享同一个内容，当某个人想要修改这个内容的时候，
 * 才会真正把内容Copy出去形成一个新的内容然后再改，这是一种延时懒惰策略。从JDK1.5开始Java并发包里提供了两个使用CopyOnWrite机制实现的并发容器
 * CopyOnWrite容器非常有用，可以在非常多的并发场景中使用到。
 * <p>
 * <p>
 * 什么是CopyOnWrite容器
 * <p>
 * CopyOnWrite容器即写时复制的容器。通俗的理解是当我们往一个容器添加元素的时候，不直接往当前容器添加，而是先将当前容器进行Copy，
 * 复制出一个新的容器，然后新的容器里添加元素，添加完元素之后，再将原容器的引用指向新的容器。这样做的好处是我们可以对CopyOnWrite容器进行并发的读，
 * 而不需要加锁，因为当前容器不会添加任何元素。所以CopyOnWrite容器也是一种读写分离的思想，读和写不同的容器。
 * <p>
 * <p>
 * <p>
 * CopyOnWriteArrayList的实现原理
 * <p>
 * 添加的时候是需要加锁的，否则多线程写的时候会Copy出N个副本出来。
 * 读的时候不需要加锁，如果读的时候有多个线程正在向ArrayList添加数据，读还是会读到旧的数据，因为写的时候不会锁住旧的ArrayList。
 * <p>
 * <p>
 * <p>
 * CopyOnWrite的应用场景
 * CopyOnWrite并发容器用于读多写少的并发场景
 * <p>
 * <p>
 * CopyOnWrite的缺点
 * CopyOnWrite容器有很多优点，但是同时也存在两个问题，即内存占用问题和数据一致性问题。所以在开发的时候需要注意一下。
 * <p>
 * 内存占用问题。
 * 因为CopyOnWrite的写时复制机制，所以在进行写操作的时候，内存里会同时驻扎两个对象的内存，
 * 旧的对象和新写入的对象（注意:在复制的时候只是复制容器里的引用，只是在写的时候会创建新对象添加到新容器里，
 * 而旧容器的对象还在使用，所以有两份对象内存）。如果这些对象占用的内存比较大，比如说200M左右，那么再写入100M数据进去，
 * 内存就会占用300M，那么这个时候很有可能造成频繁的Yong GC和Full GC。之前我们系统中使用了一个服务由于每晚使用CopyOnWrite机制更新大对象，
 * 造成了每晚15秒的Full GC，应用响应时间也随之变长。
 * 针对内存占用问题，可以通过压缩容器中的元素的方法来减少大对象的内存消耗，比如，如果元素全是10进制的数字，可以考虑把它压缩成36进制或64进制。
 * 或者不使用CopyOnWrite容器，而使用其他的并发容器，如ConcurrentHashMap。
 * <p>
 * 数据一致性问题。CopyOnWrite容器只能保证数据的最终一致性，不能保证数据的实时一致性。
 * 所以如果你希望写入的的数据，马上能读到，请不要使用CopyOnWrite容器。
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * CopyOnWriteArrayList与Collections.synchronizedMap性能比较
 * 机制
 * CopyOnWriteArrayList：专为多线程并发设计的容器，“写入时复制”策略。
 * Collections.synchronizedMap：同步容器，独占策略。
 * <p>
 * 说明：
 * 1.在System.out.println(al.size());和System.out.println(cl.size());处需要设置断点，让main线程暂停以等待访问线程（TestThread）运行完后获得正确结果。
 * 2.总时间中未考虑JVM线程调度等花费的时间，这些时间远小于但访问线程的访问时间，可以忽略。
 * 分析：
 * 可以看到随着线程数不断翻倍，CopyOnWriteArrayList的访问时间基本也是翻倍，但Collections.synchronizedMap的时间则是*4。
 * 在两个线程下Collections.synchronizedMap访问时间大概是CopyOnWriteArrayList的5倍，但在64线程的时候就变成了200倍+。
 * 所以如果在容器完全只读的情况下CopyOnWriteArrayList绝对是首选。但CopyOnWriteArrayList采用“写入时复制”策略，
 * 对容器的写操作将导致的容器中基本数组的复制，性能开销较大。所以但在有写操作的情况下，CopyOnWriteArrayList性能不佳，
 * 而且如果容器容量较大的话容易造成溢出。代码中如果CopyOnWriteArrayList cl按照ArrayList al的方法初始化就会造成溢出。
 */
public class CopyOnWriteArrayListTest {


    //List在遍历的时候，如果被修改了会抛出java.util.ConcurrentModificationException错误。
    public static void main(String[] args) throws InterruptedException {
        List<String> a = new ArrayList<String>();
        a.add("a");
        a.add("b");
        a.add("c");
        final ArrayList<String> list = new ArrayList<String>(
                a);
        Thread t = new Thread(new Runnable() {
            int count = -1;

            @Override
            public void run() {
                while (true) {
                    list.add(count++ + "");
                }
            }
        });
        t.setDaemon(true);
        t.start();
        Thread.currentThread().sleep(3);
        for (String s : list) {
            System.out.println(s);
        }
        //这段代码运行的时候就会抛出java.util.ConcurrentModificationException错误。这是因为主线程在遍历list的时候，子线程在向list中添加元素。
    }

    public static class Resource3 {
        //CopyOnWriteArrayList类最大的特点就是，在对其实例进行修改操作（add/remove等）会新建一个数据并修改，
        // 修改完毕之后，再将原来的引用指向新的数组。
        // 这样，修改过程没有修改原来的数组。也就没有了ConcurrentModificationException错误。
        public static void main(String[] args) throws InterruptedException {
            List<String> a = new ArrayList<String>();
            a.add("a");
            a.add("b");
            a.add("c");
            final CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>(a);
            Thread t = new Thread(new Runnable() {
                int count = -1;

                @Override
                public void run() {
                    while (true) {
                        list.add(count++ + "");
                    }
                }
            });
            t.setDaemon(true);
            t.start();
            Thread.currentThread().sleep(3);
            for (String s : list) {
                System.out.println(list.hashCode());
                System.out.println(s);
            }
        }
    }
}

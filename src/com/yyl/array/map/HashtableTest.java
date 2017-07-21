package com.yyl.array.map;

/**
 * 和 HashMap 一样，Hashtable 也是一个散列表，它存储的内容是键值对。
 * Hashtable是通过"拉链法"实现的哈希表
 * <p>
 * <p>
 * <p>
 * public Hashtable(int initialCapacity, float loadFactor)： 用指定初始容量和指定加载因子构造一个新的空哈希表。useAltHashing 为 boolean，其如果为真，
 * 则执行另一散列的字符串键，以减少由于弱哈希计算导致的哈希冲突的发生。
 * public Hashtable(int initialCapacity)：用指定初始容量和默认的加载因子 (0.75) 构造一个新的空哈希表。
 * public Hashtable()：默认构造函数，容量为 11，加载因子为 0.75。
 * public Hashtable(Map<? extends K, ? extends V> t)：构造一个与给定的 Map 具有相同映射关系的新哈希表。
 * <p>
 * <p>
 * <p>
 * put 方法的整个流程为：
 * 判断 value 是否为空，为空则抛出异常；
 * 计算 key 的 list 值，并根据 list 值获得 key 在 table 数组中的位置 index，如果 table[index] 元素不为空，则进行迭代，如果遇到相同的 key，则直接替换，并返回旧 value；
 * 否则，我们可以将其插入到 table[index] 位置。
 * <p>
 * <p>
 * get 方法
 * 相比较于 put 方法，get 方法则简单很多。其过程就是首先通过 list()方法求得 key 的哈希值，然后根据 list 值得到 index 索引（上述两步所用的算法与 put 方法都相同）。
 * 然后迭代链表，返回匹配的 key 的对应的 value；找不到则返回 null。
 * <p>
 * <p>
 * <p>
 * <p>
 * Hashtable 与 HashMap 的简单比较
 * <p>
 * HashTable 基于 Dictionary 类，而 HashMap 是基于 AbstractMap。Dictionary 是任何可将键映射到相应值的类的抽象父类，而 AbstractMap 是基于 Map 接口的实现，它以最大限度地减少实现此接口所需的工作。
 * HashMap 的 key 和 value 都允许为 null，而 Hashtable 的 key 和 value 都不允许为 null。HashMap 遇到 key 为 null 的时候，调用 putForNullKey 方法进行处理，
 * 而对 value 没有处理；Hashtable遇到 null，直接返回 NullPointerException。
 * Hashtable 方法是同步，而HashMap则不是。我们可以看一下源码，Hashtable 中的几乎所有的 public 的方法都是 synchronized 的，而有些方法也是在内部通过 synchronized 代码块来实现。
 * 所以有人一般都建议如果是涉及到多线程同步时采用 HashTable，没有涉及就采用 HashMap，但是在 Collections 类中存在一个静态方法：synchronizedMap()，该方法创建了一个线程安全的 Map 对象，
 * 并把它作为一个封装的对象来返回。
 * <p>
 * <p>
 * <p>
 * HashMap和Hashtable的区别
 * <p>
 * HashMap和Hashtable都实现了Map接口，但决定用哪一个之前先要弄清楚它们之间的分别。主要的区别有：线程安全性，同步(synchronization)，以及速度
 * <p>
 * HashMap几乎可以等价于Hashtable，除了HashMap是非synchronized的，并可以接受null(HashMap可以接受为null的键值(key)和值(value)，而Hashtable则不行)。
 * HashMap是非synchronized，而Hashtable是synchronized，这意味着Hashtable是线程安全的，多个线程可以共享一个Hashtable；而如果没有正确的同步的话，多个线程是不能共享HashMap的。
 * Java 5提供了ConcurrentHashMap，它是HashTable的替代，比HashTable的扩展性更好。
 * 另一个区别是HashMap的迭代器(Iterator)是fail-fast迭代器，而Hashtable的enumerator迭代器不是fail-fast的。所以当有其它线程改变了HashMap的结构（增加或者移除元素），
 * 将会抛出ConcurrentModificationException，但迭代器本身的remove()方法移除元素则不会抛出ConcurrentModificationException异常。但这并不是一个一定发生的行为，要看JVM。
 * 这条同样也是Enumeration和Iterator的区别。
 * 由于Hashtable是线程安全的也是synchronized，所以在单线程环境下它比HashMap要慢。如果你不需要同步，只需要单一线程，那么使用HashMap性能要好过Hashtable。
 * HashMap不能保证随着时间的推移Map中的元素次序是不变的。
 * 要注意的一些重要术语：
 * <p>
 * 1) sychronized意味着在一次仅有一个线程能够更改Hashtable。就是说任何线程要更新Hashtable时要首先获得同步锁，其它线程要等到同步锁被释放之后才能再次获得同步锁更新Hashtable。
 * <p>
 * 2) Fail-safe和iterator迭代器相关。如果某个集合对象创建了Iterator或者ListIterator，然后其它的线程试图“结构上”更改集合对象，将会抛出ConcurrentModificationException异常。
 * 但其它线程可以通过set()方法更改集合对象是允许的，因为这并没有从“结构上”更改集合。但是假如已经从结构上进行了更改，再调用set()方法，将会抛出IllegalArgumentException异常。
 * <p>
 * 3) 结构上的更改指的是删除或者插入一个元素，这样会影响到map的结构。
 * <p>
 * 我们能否让HashMap同步？
 * <p>
 * HashMap可以通过下面的语句进行同步：
 * Map m = Collections.synchronizeMap(hashMap);
 * <p>
 * <p>
 * <p>
 * <p>导读：
 * 1 HashMap不是线程安全的
 * HashMap是map接口的子类，是将键映射到值的对象，其中键和值都是对象，并且不能包含重复键，但可以包含重复值。HashMap允许null key和null value，而hashtable不允许。
 * 2   HashTable是线程安全。
 * HashMap是Hashtable的轻量级实现（非线程安全的实现），他们都完成了Map接口，主要区别在于HashMap允许空（null）键值（key）,由于非线程安全，效率上可能高于Hashtable。
 * <p>
 * <p>
 * <p>
 * 面试回答
 * HashMap中键值 允许为空 并且是非同步的
 * 　　　　　Hashtable中键值 不允许为空 是同步的
 * 　　　　　继承不同，但都实现了Map接口
 */


public class HashtableTest {
}

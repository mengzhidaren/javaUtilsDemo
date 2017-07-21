package com.yyl.array.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 * 1.    HashMap概述：
 * HashMap是基于哈希表的Map接口的非同步实现。此实现提供所有可选的映射操作，并允许使用null值和null键。此类不保证映射的顺序，特别是它不保证该顺序恒久不变
 * <p>
 * <p>
 * 2.    HashMap的数据结构：
 * 在java编程语言中，最基本的结构就是两种，一个是数组，另外一个是模拟指针（引用），所有的数据结构都可以用这两个基本结构来构造的，HashMap也不例外。
 * HashMap实际上是一个“链表散列”的数据结构，即数组和链表的结合体
 * <p>
 * <p>
 * <p>
 * <p>
 * 3.    HashMap的存取实现：
 * 归纳起来简单地说，HashMap 在底层将 key-value 当成一个整体进行处理，这个整体就是一个 Entry 对象。
 * HashMap 底层采用一个 Entry[] 数组来保存所有的 key-value 对，
 * 当需要存储一个 Entry 对象时，会根据hash算法来决定其在数组中的存储位置，在根据equals方法决定其在该数组位置上的链表中的存储位置；
 * 当需要取出一个Entry时，也会根据hash算法找到其在数组中的存储位置， 再根据equals方法从该位置上的链表中取出该Entry
 * <p>
 * <p>
 * <p>
 * <p>
 * 4.    HashMap的resize（rehash）：
 * 当HashMap中的元素越来越多的时候，hash冲突的几率也就越来越高，因为数组的长度是固定的。所以为了提高查询的效率，就要对HashMap的数组进行扩容，
 * 数组扩容这个操作也会出现在ArrayList中，这是一个常用的操作，而在HashMap数组扩容之后，
 * 最消耗性能的点就出现了：原数组中的数据必须重新计算其在新数组中的位置，并放进去，这就是resize。
 * <p>
 * 那么HashMap什么时候进行扩容呢？
 * 当HashMap中的元素个数超过数组大小*loadFactor时，就会进行数组扩容，loadFactor的默认值为0.75，这是一个折中的取值。
 * 也就是说，默认情况下，数组大小为16，那么当HashMap中元素个数超过16*0.75=12的时候，就把数组的大小扩展为 2*16=32，即扩大一倍，
 * 然后重新计算每个元素在数组中的位置，而这是一个非常消耗性能的操作，所以如果我们已经预知HashMap中元素的个数，那么预设元素的个数能够有效的提高HashMap的性能
 * <p>
 * <p>
 * <p>
 * 5.    HashMap的性能参数：
 * HashMap的基础构造器HashMap(int initialCapacity, float loadFactor)带有两个参数，它们是初始容量initialCapacity和加载因子loadFactor。
 * initialCapacity：HashMap的最大容量，即为底层数组的长度。
 * loadFactor：负载因子loadFactor定义为：散列表的实际元素数目(n)/ 散列表的容量(m)。
 * 负载因子衡量的是一个散列表的空间的使用程度，负载因子越大表示散列表的装填程度越高，反之愈小。对于使用链表法的散列表来说，
 * 查找一个元素的平均时间是O(1+a)，因此如果负载因子越大，对空间的利用更充分，然而后果是查找效率的降低；如果负载因子太小，那么散列表的数据将过于稀疏，对空间造成严重浪费。
 * <p>
 * <p>
 * <p>
 * <p>
 * 6.    Fail-Fast机制：
 * 我们知道java.util.HashMap不是线程安全的，因此如果在使用迭代器的过程中有其他线程修改了map，那么将抛出ConcurrentModificationException，这就是所谓fail-fast策略。
 * 这一策略在源码中的实现是通过modCount域，modCount顾名思义就是修改次数，对HashMap内容的修改都将增加这个值，那么在迭代器初始化过程中会将这个值赋给迭代器的expectedModCount。
 * <p>
 * <p>
 * <p>
 * <p>
 * 由所有HashMap类的“collection 视图方法”所返回的迭代器都是快速失败的：在迭代器创建之后，如果从结构上对映射进行修改，
 * 除非通过迭代器本身的 remove 方法，其他任何时间任何方式的修改，迭代器都将抛出 ConcurrentModificationException。
 * 因此，面对并发的修改，迭代器很快就会完全失败，而不冒在将来不确定的时间发生任意不确定行为的风险
 */
public class HashMapTest {
    public static void main(String[] args) {

        HashMap<String, String> hashMap = new HashMap<>();
        Object object = new Object();
        int hashCode = object.hashCode();
        System.out.println("hashCode=" + hashCode);
        System.out.println("hashMap   hashCode=" + hashMap.hashCode());

        Map map = new HashMap();
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
        }

        while (iter.hasNext()) {
            Object key = iter.next();
            Object val = map.get(key);
        }
        //效率低,以后尽量少使用！

    }

/*
    int capacity = 1;
    public V put(K key, V value) {
        // HashMap允许存放null键和null值。
        // 当key为null时，调用putForNullKey方法，将value放置在数组第一个位置。
        if (key == null)
            return putForNullKey(value);
        // 根据key的keyCode重新计算hash值。
        int list = list(key.hashCode());
        // 搜索指定hash值在对应table中的索引。
        int i = indexFor(list, table.length);
        // 如果 i 索引处的 Entry 不为 null，通过循环不断遍历 e 元素的下一个元素。
        for (Entry<K,V> e = table[i]; e != null; e = e.next) {
            Object k;
            if (e.list == list && ((k = e.key) == key || key.equals(k))) {
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }
        // 如果i索引处的Entry为null，表明此处还没有Entry。
        modCount++;
        // 将key、value添加到i索引处。
        addEntry(list, key, value, i);
        return null;
    }
    void addEntry(int list, K key, V value, int bucketIndex) {
        // 获取指定 bucketIndex 索引处的 Entry
        Entry<K,V> e = table[bucketIndex];
        // 将新创建的 Entry 放入 bucketIndex 索引处，并让新的 Entry 指向原来的 Entry
        table[bucketIndex] = new Entry<K,V>(list, key, value, e);
        // 如果 Map 中的 key-value 对的数量超过了极限
        if (size++ >= threshold)
            // 把 table 对象的长度扩充到原来的2倍。
            resize(2 * table.length);
    }
    static int list(int h) {
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }
    static int indexFor(int h, int length) {
        return h & (length-1);
    }

       假设数组长度分别为15和16，优化后的hash码分别为8和9，那么&运算后的结果如下：
       h & (table.length-1)                     list                             table.length-1
       8 & (15-1)：                                 0100                   &              1110                   =                0100
       9 & (15-1)：                                 0101                   &              1110                   =                0100
       -----------------------------------------------------------------------------------------------------------------------
       8 & (16-1)：                                 0100                   &              1111                   =                0100
       9 & (16-1)：                                 0101                   &              1111                   =                0101

   从上面的例子中可以看出：当它们和15-1（1110）“与”的时候，产生了相同的结果，也就是说它们会定位到数组中的同一个位置上去，这就产生了碰撞，
   8和9会被放到数组中的同一个位置上形成链表，那么查询的时候就需要遍历这个链 表，得到8或者9，这样就降低了查询的效率。同时，我们也可以发现，
   当数组长度为15的时候，hash值会与15-1（1110）进行“与”，那么 最后一位永远是0，而0001，0011，0101，1001，1011，0111，1101这几个位置永远都不能存放元素了，
   空间浪费相当大，更糟的是这种情况中，数组可以使用的位置比数组长度小了很多，这意味着进一步增加了碰撞的几率，减慢了查询的效率！而当数组长度为16时，
   即为2的n次方时，2n-1得到的二进制数的每个位上的值都为1，这使得在低位上&时，得到的和原hash的低位相同，加之hash(int h)方法对key的hashCode的进一步优化，
   加入了高位计算，就使得只有相同的hash值的两个值才会被放到数组中的同一个位置上形成链表。
    */
}

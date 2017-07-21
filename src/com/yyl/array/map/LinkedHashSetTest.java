package com.yyl.array.map;

/**
 * LinkedHashSet 概述
 * 思考了好久，到底要不要总结 LinkedHashSet 的内容 = = 我在之前的博文中，分别写了 HashMap 和 HashSet，然后我们可以看到 HashSet 的方法基本上都是基于 HashMap 来实现的，说白了，
 * HashSet内部的数据结构就是一个 HashMap，其方法的内部几乎就是在调用 HashMap 的方法。
 * LinkedHashSet 首先我们需要知道的是它是一个 Set 的实现，所以它其中存的肯定不是键值对，而是值。此实现与 HashSet 的不同之处在于，LinkedHashSet 维护着一个运行于所有条目的双重链接列表。
 * 此链接列表定义了迭代顺序，该迭代顺序可为插入顺序或是访问顺序。
 * 看到上面的介绍，是不是感觉其与 HashMap 和 LinkedHashMap 的关系很像？
 * 注意，此实现不是同步的。如果多个线程同时访问链接的哈希Set，而其中至少一个线程修改了该 Set，则它必须保持外部同步。
 * <p>
 * <p>
 * <p>
 * LinkedHashSet与LinkedHashMap很相同，只不过LinkedHashMap存的是键值对，而LinkedHashSet存的只有值
 */
public class LinkedHashSetTest {
}

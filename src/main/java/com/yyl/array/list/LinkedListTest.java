package com.yyl.array.list;

/**
 * LinkedList 和 ArrayList 一样，都实现了 List 接口，但其内部的数据结构有本质的不同。LinkedList 是基于链表实现的（通过名字也能区分开来），
 * 所以它的插入和删除操作比 ArrayList 更加高效。但也是由于其为基于链表的，所以随机访问的效率要比 ArrayList 差。
 * <p>
 * <p>
 * LinkedList 继承自 AbstractSequenceList，
 * 实现了 List、Deque、Cloneable、java.io.Serializable 接口。
 * AbstractSequenceList 提供了List接口骨干性的实现以减少实现 List 接口的复杂度，
 * Deque 接口定义了双端队列的操作。
 * <p>
 * 在 LinkedList 中除了本身自己的方法外，还提供了一些可以使其作为栈、队列或者双端队列的方法。这些方法可能彼此之间只是名字不同，以使得这些名字在特定的环境中显得更加合适。
 * <p>
 * LinkedList 也是 fail-fast 的（前边提过很多次了）。
 * <p>
 * <p>
 * <p>
 * LinkedList 的方法实在是太多，在这没法一一举例分析。但很多方法其实都只是在调用别的方法而已，
 * 所以建议大家将其几个最核心的添加的方法搞懂就可以了，比如 linkBefore、linkLast。其本质也就是链表之间的删除添加等
 */
public class LinkedListTest {
}

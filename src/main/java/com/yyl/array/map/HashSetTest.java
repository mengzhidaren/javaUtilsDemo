package com.yyl.array.map;

import java.util.HashSet;

/**
 * HashSet 实现了 Set 接口，它不允许集合中有重复的值，当我们提到 HashSet 时，第一件事情就是在将对象存储在 HashSet 之前，
 * 要先确保对象重写 equals()和 hashCode()方法，这样才能比较对象的值是否相等，以确保set中没有储存相等的对象。如果我们没有重写这两个方法，将会使用这个方法的默认实现。
 * <p>
 * <p>
 * <p>
 * HashSet 和 HashMap 的区别
 * HashMap                                                	HashSet
 * HashMap实现了Map接口	                                     HashSet实现了Set接口
 * HashMap储存键值对	                                     HashSet仅仅存储对象
 * 使用put()方法将元素放入map中	                             使用add()方法将元素放入set中
 * HashMap中使用键对象来计算hashcode值	                     HashSet使用成员对象来计算hashcode值，对于两个对象来说hashcode可能相同，所以equals()方法用来判断对象的相等性，如果两个对象不同的话，那么返回false
 * HashMap比较快，因为是使用唯一的键来获取对象	             HashSet较HashMap来说比较慢
 */
public class HashSetTest {

    public static void main(String[] args) {

        HashSet<String> hashSet = new HashSet<>();
        hashSet.add("");
    }
}

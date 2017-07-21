package com.yyl.array.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ArrayList 可以理解为动态数组，用 MSDN 中的说法，就是 Array 的复杂版本。与 Java 中的数组相比，它的容量能动态增长。
 * ArrayList 是 List 接口的可变数组的实现。实现了所有可选列表操作，并允许包括 null 在内的所有元素。除了实现 List 接口外，
 * 此类还提供一些方法来操作内部用来存储列表的数组的大小。（此类大致上等同于 Vector 类，除了此类是不同步的。）
 * <p>
 * <p>
 * Fail-Fast 机制
 * <p>
 * ArrayList 也采用了快速失败的机制，通过记录 modCount 参数来实现。在面对并发的修改时，迭代器很快就会完全失败，
 * 而不是冒着在将来某个不确定时间发生任意不确定行为的风险。 关于 Fail-Fast 的更详细的介绍，我在之前将 HashMap 中已经提到。
 */
public class ArrayListTest {


    public static void main(String[] args) {

        //避免使用基本数据类型数组转换为列表
        int[] ints = {1, 2, 3, 4, 5};
        List list = Arrays.asList(ints);
        System.out.println("list'size：" + list.size());//list'size：1


        Integer[] ints2 = {1, 2, 3, 4, 5};
        List list2 = Arrays.asList(ints2);
        System.out.println("list'size：" + list2.size());
        System.out.println("list.get(0) 的类型:" + list2.get(0).getClass());
        System.out.println("list.get(0) == ints[0]：" + list2.get(0).equals(ints[0]));

        //  list2.add(6);//java.lang.UnsupportedOperationException
        //asList 返回的列表只不过是一个披着 list 的外衣，它并没有 list 的基本特性（变长）。该 list 是一个长度不可变的列表，传入参数的数组有多长，其返回的列表就只能是多长。所以：
        //  Java 细节（2.2）：不要试图改变 asList 返回的列表，否则你会自食苦果。
        test2();
    }


    public static void test2() {
        List<Integer> list1 = new ArrayList<Integer>();
        list1.add(1);
        list1.add(2);

        //通过subList生成一个与list1一样的列表 list3
        List<Integer> list3 = list1.subList(0, list1.size());
        //修改list3
        list1.add(3);

        System.out.println("list1'size：" + list1.size());
        System.out.println("list3'size：" + list3.size());
        //list1 正常输出，但是 list3 就抛出 ConcurrentModificationException 异常，，fail-fast？不错就是 fail-fast 机制
        //size 方法首先会通过 checkForComodification 验证，然后再返回this.size。

        //该方法表明当原列表的 modCount 与 this.modCount 不相等时就会抛出 ConcurrentModificationException。同时我们知道 modCount 在 new 的过程中 “继承”了原列表 modCount，
        // 只有在修改该列表（子列表）时才会修改该值（先表现在原列表后作用于子列表）。而在该实例中我们是操作原列表，原列表的 modCount 当然不会反应在子列表的 modCount 上啦，所以才会抛出该异常。


        //对于子列表视图，它是动态生成的，生成之后就不要操作原列表了，否则必然都导致视图的不稳定而抛出异常。最好的办法就是将原列表设置为只读状态，要操作就操作子列表：
        //通过subList生成一个与list1一样的列表 list3
        //     List<Integer> list3 = list1.subList(0, list1.size());

        //对list1设置为只读状态
        //     list1 = Collections.unmodifiableList(list1);
        //生成子列表后，不要试图去操作原列表，否则会造成子列表的不稳定而产生异常
    }

    public static void test3() {
        //有一个列表存在 1000 条记录，我们需要删除 100-200 位置处的数据，可能我们会这样处理
        List<Integer> list1 = new ArrayList<Integer>();
        for (int i = 0; i < list1.size(); i++) {
            if (i >= 100 && i <= 200) {
                list1.remove(i);
           /*
            * 当然这段代码存在问题，list remove之后后面的元素会填充上来，
            * 所以需要对i进行简单的处理，当然这个不是这里讨论的 问题。
            */
            }
        }


        //简单而不失华丽！！！！！
        list1.subList(100, 200).clear();
    }
}

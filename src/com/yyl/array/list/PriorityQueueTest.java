package com.yyl.array.list;

import java.util.PriorityQueue;

/**
 * Created by Administrator on 2017/7/20/020.
 * <p>
 * <p>
 * PriorityQueue是个基于优先级堆的极大优先级队列。
 *
 * PriorityQueue，即优先队列。优先队列的作用是能保证每次取出的元素都是队列中权值最小的
 *（Java的优先队列每次取最小元素，C++的优先队列每次取最大元素）。这里牵涉到了大小关系，
 * 元素大小的评判可以通过元素本身的自然顺序（natural ordering），也可以通过构造时传入的比较器（Comparator，类似于C++的仿函数）
 *
 *
 *
 * <p>
 * 使用PriorityQueue出现了问题。我刚开始只是把它当作一个一般的FIFO实现来使用，结果发现poll()的结果跟我想象的不一样，
 * 后来才发现，PriorityQueue会对入队的元素进行排序，所以在队列顶端的总是最小的元素。
 * <p>
 * <p>
 * <p>
 * PriorityQueue的add函数实现，
 * 最终跟踪到函数private void siftUpComparable
 * PriorityQueue内部成员数组queue其实是实现了一个二叉树的数据结构，这棵二叉树的根节点是queue[0]，左子节点是queue[1]，右子节点是queue[2]，
 * 而queue[3]又是queue[1]的左子节点，依此类推，给定元素queue[i]，该节点的父节点是queue[(i-1)/2]。因此parent变量就是对应于下标为k的节点的父节点。
 * <p>
 * 弄清楚了这个用数组表示的二叉树，就可以理解上面的代码中while循环进行的工作是，当欲加入的元素小于其父节点时，就将两个节点的位置交换。
 * 这个算法保证了如果只执行add操作，那么queue这个二叉树是有序的：该二叉树中的任意一个节点都小于以该节点为根节点的子数中的任意其它节点。
 * 这也就保证了queue[0]，即队顶元素总是所有元素中最小的
 * <p>
 * <p>
 * <p>
 * 察看函数poll()，最终追中到函数private E removeAt(int i)，
 * 这个函数的实现方法是，将队尾元素取出，插入到位置i，替代被删除的元素，然后做相应的调整，保证二叉树的有序，即任意节点都是以它为根节点的子树中的最小节点。
 * 进一步的代码就留给有兴趣的读者自行分析，要说明的是，对于queue这样的二叉树结构有一个特性，
 * 即如果数组的长度为length，那么所有下标大于length/2的节点都是叶子节点，其它的节点都有子节点。
 * <p>
 * <p>
 * <p>
 * 注意1：该队列是用数组实现，但是数组大小可以动态增加，容量无限。
 * 注意2:此实现不是同步的。不是线程安全的。如果多个线程中的任意线程从结构上修改了列表， 则这些线程不应同时访问 PriorityQueue 实例，这时请使用线程安全的PriorityBlockingQueue 类。
 * 注意3:不允许使用 null 元素。
 * 注意4：此实现为插入方法（offer、poll、remove() 和 add 方法）提供 O(log(n)) 时间；
 * 为 remove(Object) 和 contains(Object) 方法提供线性时间；
 * 为检索方法（peek、element 和 size）提供固定时间。
 * 注意5:方法iterator()中提供的迭代器并不保证以有序的方式遍历优先级队列中的元素。
 * 至于原因可参考下面关于PriorityQueue的内部实现
 * 如果需要按顺序遍历，请考虑使用 Arrays.sort(pq.toArray())。
 * 注意6：可以在构造函数中指定如何排序。
 * <p>
 * 注意7:此类及其迭代器实现了 Collection 和 Iterator 接口的所有可选 方法。
 * PriorityQueue的内部实现
 * PriorityQueue对元素采用的是堆排序，头是按指定排序方式的最小元素。堆排序只能保证根是最大（最小），整个堆并不是有序的。
 * 方法iterator()中提供的迭代器可能只是对整个数组的依次遍历。也就只能保证数组的第一个元素是最小的。
 */
public class PriorityQueueTest {


    public static void main(String[] args) {
        PriorityQueue<String> pq = new PriorityQueue<String>();
        pq.add("dog");
        pq.add("apple");
        pq.add("fox");
        pq.add("easy");
        pq.add("boy");
        while (!pq.isEmpty()) {
            for (String s : pq) {
                System.out.print(s + "   ");
            }
            System.out.println();
            System.out.println("pq.poll() = " + pq.poll());
            System.out.println("-----------------------");
        }
    }
}

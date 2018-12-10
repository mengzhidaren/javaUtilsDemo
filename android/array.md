#集合


######堆的结构
````
堆的特点
堆有大堆和小堆之分
小堆： 
①任意结点的关键码均小于等于他的左右孩子的关键码 
②位于堆顶的结点的关键码最小 
③从根结点到每个结点的路径上数组元素组成的序列都是递增的
大堆： 
①任意结点的关键码均大于等于他的左右孩子的关键码 
②位于堆顶的结点的关键码最大 
③从根结点到每个结点的路径上数组元素组成的序列都是递减的
堆排序（降序，用小堆）
堆排序的原理： 
①将堆顶元素和堆中最后一个元素交换 
②此时数组最后一个元素已经排好序了，因此将堆中元素减少一个 
③此时堆结构可能被破坏，再向下调整使其满足堆的性质 
④将以上三个步骤循环起来，直到排序完数组中的所有元素
````
######堆和栈的区别
````
栈（操作系统）：由操作系统自动分配释放 ，存放函数的参数值，局部变量的值等。其操作方式类似于数据结构中的栈。
堆（操作系统）： 一般由程序员分配释放， 若程序员不释放，程序结束时可能由OS回收，分配方式倒是类似于链表。
堆栈缓存方式
    栈使用的是一级缓存， 他们通常都是被调用时处于存储空间中，调用完毕立即释放。
    堆则是存放在二级缓存中，生命周期由虚拟机的垃圾回收算法来决定（并不是一旦成为孤儿对象就能被回收）。所以调用这些对象的速度要相对来得低一些。
堆栈数据结构区别
    栈（数据结构）：一种先进后出的数据结构。
    堆（数据结构）：堆可以被看成是一棵树，如：堆排序。
````
######什么是深拷贝和浅拷贝
简单点来说，就是假设B复制了A，当修改A时，看B是否会发生变化，如果B也跟着变了，说明这是浅拷贝，拿人手短，
如果A没变，那就是深拷贝，自食其力。\
let a=[0,1,2,3,4],b=a;\
console.log(a===b);\
a[0]=1;console.log(a,b);

print: true  \
区别：浅拷贝是拷贝一层，深层次的对象级别的就拷贝引用；深拷贝是拷贝多层，每一级别的数据都会拷贝出来； 



######什么是可重入锁？
ReentrantLock是可重入锁，什么是可重入锁呢？可重入锁就是当前持有该锁的线程能够多次获取该锁，无需等待。



######什么是AQS？
######AtomicInteger的实现原理，主要能说清楚CAS机制并且AtomicInteger是如何利用CAS机制实现的
```
AQS是JDK1.5提供的一个基于FIFO等待队列实现的一个用于实现同步器的基础框架，这个基础框架的重要性可以这么说，
JCU包里面几乎所有的有关锁、多线程并发以及线程同步器等重要组件的实现都是基于AQS这个框架。
AQS的核心思想是基于volatile int state这样的一个属性同时配合Unsafe工具对其原子性的操作来实现对当前锁的状态进行修改。
当state的值为0的时候，标识改Lock不被任何线程所占有。

volatile变量保证了变量的内存可见性，也就是所有工作线程中同一时刻都可以得到一致的值。

通过查看AtomicInteger的源码可知， 
private volatile int value;
public final boolean compareAndSet(int expect, int update) { 
    return unsafe.compareAndSwapInt(this, valueOffset, expect, update); 
}
通过申明一个volatile （内存锁定，同一时刻只有一个线程可以修改内存值）类型的变量，再加上unsafe.compareAndSwapInt的方法，来保证实现线程同步的。
CAS机制
    CAS指令在Intel CPU上称为CMPXCHG指令，它的作用是将指定内存地址的内容与所给的某个值相比，如果相等，则将其内容替换为指令中提供的新值，
    如果不相等，则更新失败。这一比较并交换的操作是原子的，不可以被中断
    使用Java5+提供的CAS特性而不是使用自己实现的的好处是Java5+中内置的CAS特性可以让你利用底层的你的程序所运行机器的CPU的CAS特性。
    这会使还有CAS的代码运行更快,使用CAS可以实现非阻塞式的原子性操作
cas缺点
    有ABA问题(即在更新前的值是A，但在操作过程中被其他线程更新为B，又更新为 A)，这时当前线程认为是可以执行的，
    其实是发生了不一致现象，如果这种不一致对程序有影响(真正有这种影响的场景很少，除非是在变量操作过程中以此变量为标识位做一些其他的事，
    比如初始化配置)，则需要使用AtomicStampedReference(除了对更新前的原值进行比较，也需要用更新前的 stamp标志位来进行比较)。
总结： 
    可以用CAS在无锁的情况下实现原子操作，但要明确应用场合，非常简单的操作且又不想引入锁可以考虑使用CAS操作，
    当想要非阻塞地完成某一操作也可以考虑CAS。不推荐在复杂操作中引入CAS，会使程序可读性变差，且难以测试，同时会出现ABA问题
```
######ReentrantLock、AQS的源代码
```
AbstractQueuedSynchronizer
ReentrantLock实现的前提就是AbstractQueuedSynchronizer，简称AQS，是java.util.concurrent的核心，
CountDownLatch、FutureTask、Semaphore、ReentrantLock等都有一个内部类是这个抽象类的子类
AQS是基于FIFO队列的实现，因此必然存在一个个节点

整个AQS是典型的模板模式的应用，设计得十分精巧，对于FIFO队列的各种操作在AQS中已经实现了，
AQS的子类一般只需要重写tryAcquire(int arg)和tryRelease(int arg)两个方法即可。

 final void lock() {
      if (compareAndSetState(0, 1))
          setExclusiveOwnerThread(Thread.currentThread());
      else
          acquire(1);
 }

假设线程1调用了ReentrantLock的lock()方法，那么线程1将会独占锁
    第一个获取锁的线程就做了两件事情：
    1、设置AbstractQueuedSynchronizer的state为1
    2、设置AbstractOwnableSynchronizer的thread为当前线程
然后线程2也要尝试获取同一个锁，在线程1没有释放锁的情况下必然是行不通的，所以线程2就要阻塞
    首先线程2尝试利用CAS去判断state是不是0，是0就设置为1，当然这一步操作肯定是失败的，因为线程1已经将state设置成了1，
    所以第2行必定是false，因此线程2走第5行的acquire方法
    
    
最后调用LockSupport的park方法阻塞住了当前的线程。至此，使用ReentrantLock让线程1独占锁、线程2进入FIFO队列并阻塞

unlock()的时候做了什么
    走AQS的release：
    先调用Sync的tryRelease尝试释放锁：
 1 final boolean acquireQueued(final Node node, int arg) {
 2     try {
 3         boolean interrupted = false;
 4         for (;;) {
 5             final Node p = node.predecessor();
 6             if (p == head && tryAcquire(arg)) {
 7                 setHead(node);
 8                 p.next = null; // help GC
 9                 return interrupted;
10             }
11             if (shouldParkAfterFailedAcquire(p, node) &&
12                 parkAndCheckInterrupt())
13                 interrupted = true;
14         }
15     } catch (RuntimeException ex) {
16         cancelAcquire(node);
17         throw ex;
18     }
19 }
    被阻塞的线程2是被阻塞在第12行，注意这里并没有return语句，也就是说，阻塞完成线程2依然会进行for循环。
    然后，阻塞完成了，线程2所在的Node的前驱Node是p，线程2尝试tryAcquire，成功，然后线程2就成为了head节点了
    ，把p的next设置为null，这样原头Node里面的所有对象都不指向任何块内存空间，h属于栈内存的内容，方法结束被自动回收，
    这样随着方法的调用完毕，原头Node也没有任何的引用指向它了，这样它就被GC自动回收了。此时，遇到一个return语句，
    acquireQueued方法结束，后面的Node也是一样的原理。

```


######判断单链表成环与否？
```
成环：
    可以是循环单链表，即首位相连；也可以是部分成环，即尾部和其他节点相连。
判断是否成环：
    使用快慢指针遍历链表：
慢指针：
    从头节点开始，一次跳一个节点。
快指针：
    从头节点开始，一次跳两个节点。
    如果是成环的，这两个指针一定会相遇。
代码实现：
  private static boolean isCyc(Node node){
        Node slow = node;
        Node fast = node;
        while (slow!=null&&fast!=null){
            slow = slow.next;
            fast = fast.next.next;
            if(slow==fast)
                return true;
        }
        return false;
    }
```
######链表翻转（即：翻转一个单项链表）
```
data域：存储数据元素信息的域称为数据域；　
    next域：存储直接后继位置的域称为指针域，它是存放结点的直接后继的地址（位置）的指针域（链域）。
    data域+ next域：组成数据ai的存储映射，称为结点；
    注意：①链表通过每个结点的链域将线性表的n个结点按其逻辑顺序链接在一起的。 　　
          ②每个结点只有一个链域的链表称为单链表（Single Linked List）。
     所谓的链表就好像火车车厢一样，从火车头开始，每一节车厢之后都连着后一节车厢。
     要实现单链表存储，首先是创建一结点类，其Java代码如下：
class Node {
	private int Data;// 数据域
	private Node Next;// 指针域
	public Node(int Data) {
		// super();
		this.Data = Data;
	}
}
（二）实现反转的方法：
    （1）递归反转法：在反转当前节点之前先反转后续节点。这样从头结点开始，层层深入直到尾结点才开始反转指针域的指向。
        简单的说就是从尾结点开始，逆向反转各个结点的指针域指向，其过程图如下所示：
        public static Node Reverse1(Node head) {
        		// head看作是前一结点，head.getNext()是当前结点，reHead是反转后新链表的头结点
        		if (head == null || head.getNext() == null) {
        			return head;// 若为空链或者当前结点在尾结点，则直接还回
        		}
        		Node reHead = Reverse1(head.getNext());// 先反转后续节点head.getNext()
        		head.getNext().setNext(head);// 将当前结点的指针域指向前一结点
        		head.setNext(null);// 前一结点的指针域令为null;
        		return reHead;// 反转后新链表的头结点
        }
    （2）遍历反转法：递归反转法是从后往前逆序反转指针域的指向，而遍历反转法是从前往后反转各个结点的指针域的指向。
       基本思路是：将当前节点cur的下一个节点 cur.getNext()缓存到temp后，然后更改当前节点指针指向上一结点pre。
        也就是说在反转当前结点指针指向前，先把当前结点的指针域用tmp临时保存，以便下一次使用，其过程可表示如下：
        public static Node reverse2(Node head) {
            if (head == null)
                return head;
            Node pre = head;// 上一结点
            Node cur = head.getNext();// 当前结点
            Node tmp;// 临时结点，用于保存当前结点的指针域（即下一结点）
            while (cur != null) {// 当前结点为null，说明位于尾结点
                tmp = cur.getNext();
                cur.setNext(pre);// 反转指针域的指向
     
                // 指针往下移动
                pre = cur;
                cur = tmp;
            }
            // 最后将原链表的头节点的指针域置为null，还回新链表的头结点，即原链表的尾结点
            head.setNext(null);
            
            return pre;
        }
```
######合并多个单有序链表（假设都是递增的）


######手写链表逆序代码

######讲一下对树，B+树的理解
```

```
######讲一下对图的理解


#####1.数组
数组存储区间是连续的，占用内存严重，故空间复杂的很大。但数组的二分查找时间复杂度小，为O(1);数组的特点是：寻址容易，插入和删除困难;
#####2.链表
链表存储区间离散，占用内存比较宽松，故空间复杂度很小，但时间复杂度很大，达O(N)。链表的特点是：寻址困难，插入和删除容易
#####3.哈希表:由数组+链表组成的
当我们往HashMap中put元素的时候，先根据key的hashCode重新计算hash值，根据hash值得到这个元素在数组中的位置(即下标)
如果数组该位置上已经存放有其他元素了，那么在这个位置上的元素将以链表的形式存放，新加入的放在链头，最先加入的放在链尾。
如果数组该位置上没有元素，就直接将该元素放到此数组中的该位置上。

######   集合Set实现Hash怎么防止碰撞
```
1，如果hash码值不相同，说明是一个新元素，存；
2,如果没有元素和传入对象（也就是add的元素）的hash值相等，那么就认为这个元素在table中不存在，将其添加进table；
    2（1），如果hash码值相同，且equles判断相等，说明元素已经存在，不存；
    2（2），如果hash码值相同，且equles判断不相等，说明元素不存在，存；
什么是hash码值？ 
在java中存在一种hash表结构，它通过一个算法，计算出的结果就是hash码值；这个算法叫hash算法； 
hash算法是怎么计算的呢？ 
是通过对象中的成员来计算出来的结果； 
如果成员变量是基本数据类型的值， 那么用这个值 直接参与计算； 
如果成员变量是引用数据类型的值，那么获取到这个成员变量的哈希码值后，再参数计算
```
######   ArrayList,LinkedList的区别
ArrayList是实现了基于动态数组的数据结构，LinkedList基于链表的数据结构。\
对于随机访问get和set，ArrayList觉得优于LinkedList，因为LinkedList要移动指针。\
对于新增和删除操作add和remove，LinedList比较占优势，因为ArrayList要移动数据。
######   ArrayList和Vector的主要区别  
ArrayList 和Vector底层都是采用数组方式存储数据\
Vector:线程同步  当Vector中的元素超过它的初始大小时，Vector会将它的容量翻倍，
ArrayList:线程不同步，但性能很好  当ArrayList中的元素超过它的初始大小时，ArrayList只增加50%的大小
######   HashMap和 HashTable 的区别：
HashTable比较老，是基于Dictionary 类实现的，\
HashTable 是线程安全的， HashMap 则是线程不安全的\
HashMap 则是基于Map接口实现的可以让你将空值作为一个表的条目的key或value

######  List、Map、Set实现类的源代码

```

##  ArrayList<T>:   默认大小 10
    transient Object[] elementData;
    public E set : elementData[index] = element;
    public E get : elementData[index]
    public E remove(int index):      System.arraycopy(es, i + 1, es, i, newSize - i);//重新排列数组
    
##  Vector<E>    默认大小 10
 protected Object[] elementData;
 public synchronized E get(int index) 
 public synchronized E set(int index, E element) 

##  LinkedList<T>:

transient Node<E> first;
transient Node<E> last;
 public E get(int index)
 public E set(int index, E element)
 public E remove(int index)
 private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;
 }


##  HashMap<K,V>    默认大小     1<<4=16   哈希表的加载因子0.75
 transient Node<K,V>[] table;
 transient Set<Map.Entry<K,V>> entrySet;

  static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;
  }

  static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
        TreeNode<K,V> parent;  // red-black tree links
        TreeNode<K,V> left;
        TreeNode<K,V> right;
        TreeNode<K,V> prev;    // needed to unlink next upon deletion
        boolean red;
  }
## HashSet<E>
  private transient HashMap<E,Object> map;
  private static final Object PRESENT = new Object();
    
    public boolean add(E e) {return map.put(e, PRESENT)==null; }
    public boolean remove(Object o) {return map.remove(o)==PRESENT; }
    
##  HashTable<K,V>         默认大小  11   哈希表的加载因子0.75
 private transient Entry<?,?>[] table;
  private static class Entry<K,V> implements Map.Entry<K,V> {
         final int hash;
         final K key;
         V value;
         Entry<K,V> next;
  }
  public synchronized V get(Object key)
  public synchronized V put(K key, V value) 
  public synchronized V remove(Object key) 

##  CurrentHashMap<K,V>   默认大小16

transient volatile Node<K,V>[] table;
private transient volatile Node<K,V>[] nextTable;
  static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        volatile V val;
        volatile Node<K,V> next;
.............


##  Stack<E> extends Vector<E> 
    public synchronized E pop() 
    public synchronized E peek()



##  TreeMap<K,V>
 private transient Entry<K,V> root;
 
 static final class Entry<K,V> implements Map.Entry<K,V> {
        K key;
        V value;
        Entry<K,V> left;
        Entry<K,V> right;
        Entry<K,V> parent;
        boolean color = BLACK;
 }
```

######  HashMap，hashtable，currenthashmap等集合的底层实现原理等，扩容，内部结构
######    TreeMap、HashMap、LindedHashMap的区别。 

######     Collection包结构，与Collections的区别。 

######    ThreadPool用法与优势。 

######  
```

```

######  for each和正常for的用在不同数据结构（ArrayList、set、hashmap）上的效率区别
```

```

######  
```

```

######  
```

```

######  
```

```





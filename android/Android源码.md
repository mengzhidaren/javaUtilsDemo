####    looper架构
````$xslt
Android为什么要设计只能通过Handler机制更新UI呢？ 
最根本的目的就是解决多线程并发问题， 

假设如果在一个activity当中，有多个线程去更新UI，并且都没有加锁机制，那么会产生什么样子的问题？ –》 更新界面错乱
如果对更新UI的操作都进行加锁处理的话又会产生什么样子的问题？–》 性能下降

Handler封装了消息的发送（主要包括消息发送给谁（一般是发送给handler自己）） 
Looper 
    内部包含一个消息队列也就是MessageQueue，所有的Handler发送的消息都走向这个消息队列 
Looper.Looper方法，
    就是一个死循环，不断的从MessageQueue取消息，如果有消息就处理消息，没有消息就阻塞

MessageQueue，就是一个消息队列，可以添加消息，并处理消息

######  为什么不能在子线程更新UI？
提高移动端更新UI的效率和和安全性，以此带来流畅的体验
````
##### Looper 死循环为什么不会导致应用卡死？
```
主线程循环消息队列执行消息没有消息时阻塞等待消息
阻塞在loop的queue.next()中的nativePollOnce()方法里，此时主线程会释放CPU资源进入休眠状态
主线程大多数时候都是处于休眠状态，并不会消耗大量CPU资源

主线程的死循环一直运行是不是特别消耗CPU资源呢？ 其实不然，这里就涉及到Linux pipe/epoll机制，简单说就是在主线程的MessageQueue没有消息时，
便阻塞在loop的queue.next()中的nativePollOnce()方法里，此时主线程会释放CPU资源进入休眠状态，直到下个消息到达或者有事务发生，
通过往pipe管道写端写入数据来唤醒主线程工作。这里采用的epoll机制，是一种IO多路复用机制，可以同时监控多个描述符，
当某个描述符就绪(读或写就绪)，则立刻通知相应程序进行读或写操作，本质同步I/O，即读写是阻塞的。 
所以说，主线程大多数时候都是处于休眠状态，并不会消耗大量CPU资源。 

线程默认没有Looper的，如果需要使用Handler就必须为线程创建Looper。我们经常提到的主线程，也叫UI线程，
它就是ActivityThread线程(UI线程)
ActivityThread被创建时就会初始化Looper，这也是在主线程中默认可以使用Handler的原因。

```
##### Handler.sendMessageDelayed()怎么实现延迟的
```
postDelay()一个10秒钟的Runnable A、消息进队，MessageQueue调用nativePollOnce()阻塞，Looper阻塞；
紧接着post()一个Runnable B、消息进队，判断现在A时间还没到、正在阻塞，把B插入消息队列的头部（A的前面），
然后调用nativeWake()方法唤醒线程；
MessageQueue.next()方法被唤醒后，重新开始读取消息链表，第一个消息B无延时，直接返回给Looper；
Looper处理完这个消息再次调用next()方法，MessageQueue继续读取消息链表，
第二个消息A还没到时间，计算一下剩余时间（假如还剩9秒）继续调用nativePollOnce()阻塞；
直到阻塞时间到或者下一次有Message进队；
```


#### Handler源码  Looper  MessageQueue Message
```
Looper
每个Looper拥有一个消息队列，归属于一个线程,如果线程尝试创建第二个Looper就会出现异常
private static void prepare(boolean quitAllowed) {
    if (sThreadLocal.get() != null) {
        throw new RuntimeException("Only one Looper may be created per thread");
    }
    sThreadLocal.set(new Looper(quitAllowed));
}
private Looper(boolean quitAllowed) {
    mQueue = new MessageQueue(quitAllowed);
    mThread = Thread.currentThread();//创建一个消息队列和Looper所运行的线程就是当前线程
}
启动Looper   把这个线程Looper的queue里面的消息送去处理
     loop(){
        //MessageQueue  queue = sThreadLocal.get().mQueue
        // 确保线程就是本地线程，并实时跟踪线程身份
        Binder.clearCallingIdentity();
        Message msg = queue.next(); // MessageQueue可能会阻塞
        // msg.handler.dispatchMessage(msg)，消息发送到Handler回调
        msg.target.dispatchMessage(msg);
        // 确保消息在分发的时候线程没有改变
        final long newIdent = Binder.clearCallingIdentity();
     }
Looper的quit方法
    实际上执行了MessageQueue中的removeAllMessagesLocked方法，
    该方法的作用是把MessageQueue消息池中所有的消息全部清空，无论是延迟消息
    （延迟消息是指通过sendMessageDelayed或通过postDelayed等方法发送的需要延迟执行的消息）还是非延迟消息。
Looper的quitSafely方法
    实际上执行了MessageQueue中的removeAllFutureMessagesLocked方法，
    通过名字就可以看出，该方法只会清空MessageQueue消息池中所有的延迟消息，并将消息池中所有的非延迟消息派发出去让Handler去处理，
quit和quitSafely比较
    quitSafely相比于quit方法安全之处在于清空消息之前会派发所有的非延迟消息。
    Looper的quit方法从API Level 1就存在了，但是Looper的quitSafely方法从API Level 18才添加进来
    
Looper.loop();里面维护了一个死循环方法
在子线程中使用Looper.prepare();和Looper.loop();（不建议这么做，因为它会使线程无法执行结束，导致内存泄露）
        在子线程中，如果手动为其创建Looper，那么在所有的事情完成以后应该调用quit方法来终止消息循环，
        否则这个子线程就会一直处于等待（阻塞）的状态，而如果退出Looper以后，这个线程就会立刻（执行所有方法并）终止，
        因此建议在不需要Looper的时候终止Looper。(Looper.myLooper().quit();)
        
    
Message
    Message类是个final类
    recycle():回收当前message到全局池
    obj 无法跨进程发送 必须是Parcelable的类      Parcelable p = (Parcelable)obj;
    obtain() 调用obtain()或者obtainMessage(), 这样是从一个可回收对象池中获取Message对象。这样可以节省内存

MessageQueue
     MessageQueue，主要包含2个操作：插入和读取。读取操作会伴随着删除操作，插入和读取对应的方法分别为enqueueMessage和next，
     其中enqueueMessage的作用是往消息队列中插入一条消息，而next的作用是从消息队列中取出一条消息并将其从消息队列中移除。
     虽然MessageQueue叫消息队列，但是它的内部实现并不是用的队列，实际上它是通过一个单链表的数据结构来维护消息列表，
     单链表在插入和删除上比较有优势
    next() 取出下一个Message（从头部取），如果没有Message可以处理，就可以处理下IdleHandler。
            idle表示当前有空闲时间的时候执行，而运行到这一步的时候，表示消息队列处理已经是出于空闲时间了
            （队列中没有Message，或者头部Message的执行时间(when)在当前时间之后）
    enqueueMessage(...) messageQueue中的元素是按序按时间先后插入的（先执行的在前）。
    removeMessages()是先从队首删除，如果删除了则队首指向接下来的元素，重复这个过程，直到第一个不匹配的元素出现。
                接着从这个元素之后（after front）开始查找并删除，
                方法是链表删除后一个节点的方法，即p.next=nn。注意这里都是删除所有匹配的消息，而不是第一个匹配的
    quit(boolean safe)  如果是safe的退出，则执行removeAllFutureMessagesLocked()
                如果是unsafe的退出，则所有message都直接被删除并回收

HandlerThread
    由于HandlerThread的run方法是一个无限循环，因此当不需要使用的时候通过quit或者quitSafely方法来终止线程的执行。
IdleHandler
    注释可以了解到，这个接口方法是在消息队列全部处理完成后或者是在阻塞的过程中等待更多的消息的时候调用的，
    返回值false表示只回调一次，true表示可以接收多次回调

主线程 ActivityThread
 public static void main(String[] args) {
 
         ``````
         Looper.prepareMainLooper();//创建Looper和MessageQueue对象，用于处理主线程的消息
         ActivityThread thread = new ActivityThread();
         thread.attach(false);//建立Binder通道 (创建新线程)
         if (sMainThreadHandler == null) {
             sMainThreadHandler = thread.getHandler();
         }
         Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
         Looper.loop();//主线程死循环
         //如果能执行下面方法，说明应用崩溃或者是退出了...
         throw new RuntimeException("Main thread loop unexpectedly exited");
     }
     
主线程的死循环一直运行是不是特别消耗CPU资源呢？ 其实不然，这里就涉及到Linux pipe/epoll机制，简单说就是在主线程的MessageQueue没有消息时，
便阻塞在loop的queue.next()中的nativePollOnce()方法里，此时主线程会释放CPU资源进入休眠状态，直到下个消息到达或者有事务发生，
通过往pipe管道写端写入数据来唤醒主线程工作。这里采用的epoll机制，是一种IO多路复用机制，可以同时监控多个描述符，
当某个描述符就绪(读或写就绪)，则立刻通知相应程序进行读或写操作，本质同步I/O，即读写是阻塞的。 
所以说，主线程大多数时候都是处于休眠状态，并不会消耗大量CPU资源。 

ActivityThread 的动力是什么？
    其实承载ActivityThread的主线程就是由Zygote fork而创建的进程。
        从Linux角度来说进程与线程除了是否共享资源外，并没有本质的区别，都是一个task_struct结构体，
        在CPU看来进程或线程无非就是一段可执行的代码，CPU采用CFS调度算法，保证每个task都尽可能公平的享有CPU时间片
        
Messenger   IBinder
```


######  bindService、startService区别
````
bindService参数多需要ServiceConnection  需要重写ServiceConnection的onServiceConnected方法
startService不可以跨进程	bindService可以跨进程

客户端的bindService方法
最终会执行到ContextImpl的bindServiceCommon方法
bindServiceCommon相比startServiceCommend多了一步sd参数的生成 sd是一个IServiceConnection
1. 由于ServiceConnection不能跨进程，所以使用了ServiceDispatcher.InnerConnection来保存ServiceConnection的信息， 
2. ServiceDispatcher.InnerConnection执行connected方法会执行ServiceConnection的onServiceConnected方法。

startService和bindService在AMS中最后都会执行到realStartServiceLocked方法。
realStartServiceLocked中会依次执行requestServiceBindingsLocked、sendServiceArgsLocked方法
requestServiceBindingsLocked：通过循环IntentBindRecord记录来执行onBind方法
sendServiceArgsLocked：通过循环pendingStarts记录来执行onStartCommend方法
IntentBindRecord记录在bindServiceLocked中赋值
pendingStarts（StartItem）记录在startServiceLocked中赋值
bindService流程执行onBind后，会比startService流程多一步，调用onServiceConnected方法，该步骤也是一个AMS调用过程。用到了ServiceDispatch类。

````


###### Messenger
````
Messenger 对 AIDL 进行了封装，也就是对 Binder 的封装，
Messenger是一种轻量级的IPC方案，通过它可以在不同进程中传递Message对象，在Message中放入我们需要传递的数据，
就可以实现数据的进程间传递了。其中本质还是Handler和AIDL的封装和结合

Messenger 中持有一个 IMessenger 的引用，在构造函数中可以通过 Handler 或者 Binder 的形式获得最终的 IMessenger 实现，然后调用它的 send() 方法

Messenger发送消息是经由Handler实现的，所以Messenger的消息是以MessageQueue去管理的，也就是一次只能处理一个消息，不能支持并发任务。


<<<< 优点不用手写AIDL了 我们可以使用它的实现来完成基于消息的跨进程通信>>>>

````
###### Android中的IPC机制
```
IPC是Inter-Process Communication的缩写，含义就是进程间通信或者跨进程通信
Android中的IPC方式
使用Bundler     适用场景----> 用于android四大组件间的进程间通信
    四大组件中三大组件（activity、service、receiver）都是支持在Intent中传递Bundle数据的，
    由于Bundle实现了Parcelable接口，所以它可以方便地在不同的进程间传输。
使用文件共享     适用场景---->用于单线程读写  无并发，交换简单的数据实时性不高的场景
    共享文件也是一种不错的进程间通信方式，两个进程间通过读/写同一个文件来交换数据，比如A进程把数据写入文件，B进程通过读取这个文件来获取数据。
使用Messenger    适用场景---->低并发的一对多即时通信，无RPC需求 或者 无须要返回结果的RPC需求
    Messenger可以翻译为信使,通过它可以在不同进程中传递Message对象
    Messenger是一种轻量级的IPC方案，它的底层实现是AIDL，实现Messenger有以下两个步骤，分为服务端进程和客户端进程
使用AIDL     适用场景---->一对多通信且有RPC需求
    远程服务跨进程通信的一种方式。
使用ContentProvider    适用场景---->一对多的进程之间的数据共享
    ContentProvider是Android中提供的专门用于不同应用间进行数据共享的方式，它的底层实现同样也是Binder。
使用Socket         适用场景---->  网络数据交换
    Socket也称为“套接字”，是网络通信中的概念，它分为流式套接字和用户数据套接字两种，分别应于网络的传输控制层中的TCP和UDP协议。

```
###### RPC框架
```
RPC——Remote Procedure Call Protocol，这是广义上的解释，远程过程调用
RPC:远程过程调用。RPC的核心并不在于使用什么协议。RPC的目的是让你在本地调用远程的方法，而对你来说这个调用是透明的，
你并不知道这个调用的方法是部署哪里。通过RPC能解耦服务，这才是使用RPC的真正目的。RPC的原理主要用到了动态代理模式，
至于http协议，只是传输协议而已。简单的实现可以参考spring remoting，复杂的实现可以参考dubbo。

RPC是一个软件结构概念，是构建分布式应用的理论基础
RPC是一种概念，http也是RPC实现的一种方式。论复杂度，dubbo/hessian用起来是超级简单的
```

####    ApplicationContext和ActivityContext的区别
```
这两者的生命周期是不同的，它们各自的使用场景不同，this.getApplicationContext()取的是这个应用程序的Context，
它的生命周期伴随应用程序的存在而存在；而Activity.this取的是当前Activity的Context，它的生命周期则只能存活于当前Activity，
```
####    请介绍下ContentProvider 是如何实现数据共享的？
```
一个程序可以通过实现一个Content provider的抽象接口将自己的数据完全暴露出去，而且Content providers是以类似数据库中表的方式将数据暴露。
Content providers存储和检索数据，通过它可以让所有的应用程序访问到，这也是应用程序之间唯一共享数据的方法
```
######  SpareArray原理
````
    它也是线程不安全的，允许value为null。
适用场景：
    数据量不大（千以内）
    空间比时间重要
    需要使用Map，且key为int类型。
````

###### LruCache
```
LRU全称为Least Recently Used，最近最少使用，是一种缓存置换算法
需要注意区分的是LRU和LFU。前者是最近最少使用，即淘汰最长时间未使用的对象；后者是最近最不常使用，即淘汰一段时间内使用最少的对象
LruCache默认情况下缓存的大小是由值的数量决定，重写sizeOf计算不同的值
LruCache不允许null作为一个key或value
LruCache这个类是线程安全的。自动地执行多个缓存操作通过synchronized 同步缓存:

如果LruCache缓存的某条数据明确地需要被释放，可以覆写entryRemoved(...)
如果LruCache缓存的某条数据通过key没有找到，可以覆写 create(K key)，
    这简化了调用代码，即使错过一个缓存数据，也不会返回null，而会返回通过create(K key)创造的数据。
如果想限制每条数据的缓存大小，可以覆写sizeOf(K key, V value)：

trimToSize() 循环移除最近最少使用的数据直到剩余缓存数据的大小等于小于最大缓存大小。

tip<可以当前一个封装过的LinkedHashMap 用来在内存里 存图片或者其它数据>
```
######  Android线程有没有上限？线程池有没有上限？
线程池尽量合适，比如常用的最佳算法是如果CPU是N核，则池大小是N/N+1，线程池线程太多会造成频繁的上下文切换/ 

####### Serializable 和Parcelable 的区别
两者最大的区别在于 存储媒介的不同，Serializable 使用 I/O 读写存储在硬盘上，而 Parcelable 是直接 在内存中读写。
很明显，内存的读写速度通常大于 IO 读写，所以在 Android 中传递数据优先选择 Parcelable。
######  AlertDialog,popupWindow,Activity区别
````$xslt
AlertDialog builder：用来提示用户一些信息,用起来也比较简单,设置标题类容 和按钮即可,如果是加载的自定义的view ,                                  调用 dialog.setView(layout);加载布局即可(其他的设置标题 类容 这些就不需要了)
popupWindow：就是一个悬浮在Activity之上的窗口，可以用展示任意布局文件
activity：Activity是Android系统中的四大组件之一，可以用于显示View。Activity是一个与用记交互的系统模块，几乎所              
有的Activity都是和用户进行交互的

区别：AlertDialog是非阻塞式对话框：AlertDialog弹出时，后台还可以做事情；而PopupWindow是阻塞式对话框：
PopupWindow弹出时，程序会等待，在PopupWindow退出前，程序一直等待，只有当我们调用了dismiss方法的后，PopupWindow退出，程序才会向下执行。

````


######  Android动画原理
````
Animation框架定义了透明度，旋转，缩放和位移几种常见的动画，而且控制的是整个View
实现原理是每次绘制视图时View所在的ViewGroup中的drawChild函数获取该View的Animation的Transformation值
然后调用canvas.concat(transformToApply.getMatrix())，通过矩阵运算完成动画帧，如果动画没有完成，继续调用invalidate()函数，
启动下次绘制来驱动动画
动画过程中的帧之间间隙时间是绘制函数所消耗的时间，可能会导致动画消耗比较多的CPU资源，最重要的是，动画改变的只是显示，并不能相应事件

逐帧动画(Drawable Animation)：播放一系列的图片来实现动画效果
补间动画(Tween Animation)：它并不会改变View属性的值，只是改变了View的绘制的位置,一个按钮在动画过后，不在原来的位置，但是触发点击事件的仍然是原来的坐标
属性动画(Property Animation)：动画的对象除了传统的View对象，还可以是Object对象，动画结束后，Object对象的属性值被实实在在的改变了

````
######  什么是Dalvik虚拟机
Dalvik虚拟机是Android平台的核心,它可以支持.dex格式的程序的运行
.dex格式是专为Dalvik设计的一种压缩格式,可以减少整体文件尺寸,提高I/O操作的速度,适合内存和处理器速度有限的系统
######  Dalvik虚拟机和JVM有什么区别
Dalvik 基于寄存器，而 JVM 基于栈。基于寄存器的虚拟机对于更大的程序来说，在它们编译的时候，花费的时间更短。
Dalvik执行.dex格式的字节码，而JVM执行.class格式的字节码
######  Android为每个应用程序分配的内存大小是多少
一般是16m或者24m,但是可以通过android:largeHeap申请更多内存
######  Intent和bundle在activity或fragment间进行通信，那么这个通信是如何实现的
````
在系统启动时，PackageManagerService就会启动，PMS将解析所有已安装的应用信息，构建信息表，
当用户通过Intent跳转到某个组件时，会根据Intent中包含的信息到PMS中查找对应的组件列表，最后跳转到目标组件当中


Parcelable的底层使用了Parcel机制。传递实际上是使用了binder机制，binder机制会将Parcel序列化的数据写入到一个共享内存中，
读取时也是binder从共享内存中读出字节流，然后Parcel反序列化后使用。这就是Intent或Bundle能够在activity或者跨进程通信的原理
这个共享内存就叫Binder transaction buffer，这块内存有一个大小限制，目前是1MB，而且是共用的，当超过了这个大小就会报错。


比如：
startActivity方法，通过一系列的调用：
startActivityForResult方法 执行execStartActivity方法
ActivityManagerService的startActivity方法 (另一个进程了)
ActivityStarter的startActivityMayWait方法  (获取Intent的Component对象，如果不为空.......)
ActivityStackSupervisor.resolveIntent方法
PMS的resolveIntent方法

基本流程：
首先获取Intent的Component对象，如果不为空，说明指定了Componet，那么就直接通过Componet找到ActivityInfo列表，并且这个列表size为1，
所以这个ActivityInfo就是指定需要跳转的组件。
如果没有指定Component，那就是隐式Intent调用，接着获取Intent传递的需要跳转的包名。
如果包名为空，则会通过ActivityIntentResolver等进行模糊匹配，比如根据Action、Category等。
如果包名不为空，则直接根据包名来获取到对应的ActivityInfo对象，而mActivities就是PMS存储的activity信息表。

````
####    ActivityThread，AMS，WMS的工作原理

######  activity启动流程   点击图标 或者启动activity2   及 Intent间进行通信
```
startActivity
1.Activity1调用startActivity，实际会调用Instrumentation类的execStartActivity方法，
    Instrumentation是系统用来监控Activity运行的一个类，Activity的整个生命周期都有它的影子。
2.通过跨进程的binder调用，进入到ActivityManagerService中，其内部会处理Activity栈。之后又通过跨进程调用进入到Activity2所在的进程中。
3.ApplicationThread是一个binder对象，其运行在binder线程池中，内部包含一个H类，该类继承于类Handler。
    ApplicationThread将启动Activity2的信息通过H对象发送给主线程。
4.主线程拿到Activity2的信息后，调用Instrumentation类的newActivity方法，其内通过ClassLoader创建Activity2实例。





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
######  
```

```
######  
```

```
######  
```

```











谈谈对Volley的理解

HttpUrlConnection 和 okhttp关系

####looper架构
````$xslt
Android为什么要设计只能通过Handler机制更新UI呢？ 
最根本的目的就是解决多线程并发问题， 

假设如果在一个activity当中，有多个线程去更新UI，并且都没有加锁机制，那么会产生什么样子的问题？ –》 更新界面错乱
如果对更新UI的操作都进行加锁处理的话又会产生什么样子的问题？–》 性能下降


Handler封装了消息的发送（主要包括消息发送给谁（一般是发送给handler自己）） 
Looper 
内部包含一个消息队列也就是MessageQueue，所有的Handler发送的消息都走向这个消息队列 
Looper.Looper方法，就是一个死循环，不断的从MessageQueue取消息，如果有消息就处理消息，没有消息就阻塞

MessageQueue，就是一个消息队列，可以添加消息，并处理消息


````
######Intent和bundle在activity或fragment间进行通信，那么这个通信是如何实现的
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
######bindService、startService区别
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




ActivityThread，AMS，WMS的工作原理

####ApplicationContext和ActivityContext的区别
这两者的生命周期是不同的，它们各自的使用场景不同，this.getApplicationContext()取的是这个应用程序的Context，
它的生命周期伴随应用程序的存在而存在；而Activity.this取的是当前Activity的Context，它的生命周期则只能存活于当前Activity，
####请介绍下ContentProvider 是如何实现数据共享的？
一个程序可以通过实现一个Content provider的抽象接口将自己的数据完全暴露出去，而且Content providers是以类似数据库中表的方式将数据暴露。
Content providers存储和检索数据，通过它可以让所有的应用程序访问到，这也是应用程序之间唯一共享数据的方法
####SpareArray原理
它也是线程不安全的，允许value为null。
````$xslt

适用场景：
数据量不大（千以内）
空间比时间重要
需要使用Map，且key为int类型。
````
####为什么不能在子线程更新UI？
提高移动端更新UI的效率和和安全性，以此带来流畅的体验
####LruCache默认缓存大小

####Android线程有没有上限？

####线程池有没有上限？


#####Serializable 和Parcelable 的区别
两者最大的区别在于 存储媒介的不同，Serializable 使用 I/O 读写存储在硬盘上，而 Parcelable 是直接 在内存中读写。
很明显，内存的读写速度通常大于 IO 读写，所以在 Android 中传递数据优先选择 Parcelable。



#####AlertDialog,popupWindow,Activity区别
````$xslt
AlertDialog builder：用来提示用户一些信息,用起来也比较简单,设置标题类容 和按钮即可,如果是加载的自定义的view ,                                  调用 dialog.setView(layout);加载布局即可(其他的设置标题 类容 这些就不需要了)
popupWindow：就是一个悬浮在Activity之上的窗口，可以用展示任意布局文件
activity：Activity是Android系统中的四大组件之一，可以用于显示View。Activity是一个与用记交互的系统模块，几乎所              
有的Activity都是和用户进行交互的

区别：AlertDialog是非阻塞式对话框：AlertDialog弹出时，后台还可以做事情；而PopupWindow是阻塞式对话框：
PopupWindow弹出时，程序会等待，在PopupWindow退出前，程序一直等待，只有当我们调用了dismiss方法的后，PopupWindow退出，程序才会向下执行。

````




(->)Android动画原理
Animation框架定义了透明度，旋转，缩放和位移几种常见的动画，而且控制的是整个View
实现原理是每次绘制视图时View所在的ViewGroup中的drawChild函数获取该View的Animation的Transformation值
然后调用canvas.concat(transformToApply.getMatrix())，通过矩阵运算完成动画帧，如果动画没有完成，继续调用invalidate()函数，启动下次绘制来驱动动画
动画过程中的帧之间间隙时间是绘制函数所消耗的时间，可能会导致动画消耗比较多的CPU资源，最重要的是，动画改变的只是显示，并不能相应事件




逐帧动画(Drawable Animation)：播放一系列的图片来实现动画效果
补间动画(Tween Animation)：它并不会改变View属性的值，只是改变了View的绘制的位置,一个按钮在动画过后，不在原来的位置，但是触发点击事件的仍然是原来的坐标
属性动画(Property Animation)：动画的对象除了传统的View对象，还可以是Object对象，动画结束后，Object对象的属性值被实实在在的改变了





(->)Dalvik虚拟机和JVM有什么区别
Dalvik 基于寄存器，而 JVM 基于栈。基于寄存器的虚拟机对于更大的程序来说，在它们编译的时候，花费的时间更短。
Dalvik执行.dex格式的字节码，而JVM执行.class格式的字节码

(->)什么是Dalvik虚拟机
Dalvik虚拟机是Android平台的核心,它可以支持.dex格式的程序的运行
.dex格式是专为Dalvik设计的一种压缩格式,可以减少整体文件尺寸,提高I/O操作的速度,适合内存和处理器速度有限的系统

(->)Android为每个应用程序分配的内存大小是多少
一般是16m或者24m,但是可以通过android:largeHeap申请更多内存














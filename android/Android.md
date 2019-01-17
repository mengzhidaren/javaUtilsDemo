######为什么Android引入广播机制?
a:从MVC的角度考虑(应用程序内) 
b：程序间互通消息(例如在自己的应用程序内监听系统来电)
c：效率上(参考UDP的广播协议在局域网的方便性)
d：设计模式上(反转控制的一种应用，类似监听者模式)
###### MVC 、MVP 和 MVVM 三种架构的区别和优点；
```
MVC 模式
视图（View）：用户界面。 
控制器（Controller）：业务逻辑 
模型（Model）：数据保存


MVP 模式将 Controller 改名为 Presenter，同时改变了通信方向。 
1. 各部分之间的通信，都是双向的。 
2. View 与 Model 不发生联系，都通过 Presenter 传递。 
3. View 非常薄，不部署任何业务逻辑，称为”被动视图”（Passive View），即没有任何主动性，而 Presenter非常厚，所有逻辑都部署在那里。


MVVM 模式将 Presenter 改名为 ViewModel，基本上与 MVP 模式完全一致。 
MVVM的问题
MVVM 的作者 John Gossman 的 批评 应该是最为中肯的。John Gossman 对 MVVM 的批评主要有两点： 
第一点：数据绑定使得 Bug 很难被调试。你看到界面异常了，有可能是你 View 的代码有 Bug，也可能是 Model 的代码有问题。
        数据绑定使得一个位置的 Bug 被快速传递到别的位置，要定位原始出问题的地方就变得不那么容易了。 
第二点：对于过大的项目，数据绑定需要花费更多的内存。


```

######dugger2源码
```
依赖注入是面向对象编程的一种设计模式，其目的是为了降低程序耦合，这个耦合就是类之间的依赖引起的
直接组合方式虽然简单，但是具有耦合性，为了解决这种耦合，可能就会多产生一些辅助类，让这种直接的依赖关系，变为间接，降低耦合。
跟大多数设计模式一样，为了达到高内聚低耦合，往往会有很多接口与类，Daager2也是如此

@Inject依赖注入  被标记的话，就会自动初始化这个类，从而完成依赖注入
@Component是一个接口或者抽象类  Component就像一个桥梁将他们连接起来 @Component(modules = MainModule.class)
@Module主要用来提供依赖    里面定义@Provides标注的以provide开头的方法就是提供依赖的，创建多个方法来提供不同的依赖

Dagger2与其他依赖注入框架不同，它是通过apt插件在编译阶段生成相应的注入代码
Dagger2就是一个依赖注入工具


```
###### Retrofit 的源码和原理；
```
Retrofit使用的，就是动态代理，方法注解、建造者和适配器等成熟的技术或模式，但是由于她的设计紧凑，而且动态代理屏蔽了很多过程上的细节，所以比较难以理解。

Retrofit是一个对OKHttp框架的简单封装的Android网络框架。本身只是简化了用户网络请求的参数配置等还能与Rxjava相结合，使用起来更加简便。
Retrofit采用注解方式开发。通过注解构建不同的请求和请求的参数，省去了创建大量类似的请求与方法，
实际上这些参数最终都会在OkHttp中组合成一个完整的Http的请求（包括请求的头和请求体），并通过OkHttp框架进行发送
Retrofit不直接做网络请求

动态代理：
INetApiService netApiService= retrofit.create(INetApiService.class);到Retrofit源码里看create函数，是一个动态代理
......


1.依赖倒置原则：  
（Retrofit底层虽然使用了OkHttpClient去处理网络请求，但她并没有使用okhttp3.call这个Call接口，而是自己又建了一个retrofit2.Call接口，
OkHttpCall继承的是retrofit2.Call，与okhttp3.call只是引用关系。这样的设计符合依赖倒置原则，可以尽可能的与OkHttpClient解耦。）
如果我们在接口中要求的函数返回值是个RxJava的Flowable对象那么我们只需要为Retrofit添加对应的扩展
Retrofit retrofit=new Retrofit.Builder()
.baseUrl(Config.DOMAIN)
.addConverterFactory(GsonConverterFactory.create())
 适配器模式
.addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //扩展适配器  返回指定 类型Flowable
.build();
就能得到Flowable类型的callWorker对象

2单一职责    比如OkHttpClient和ServiceMethod的各自职责与调用关系
3迪米特法则    内部实现再复杂，对于外部调用者也只展示他需要的那些功能，例如Retrofit。
4利用多个工厂类组成扩展列表   add***Factory(**)
5利用建造者模式把建造和使用分离  new Retrofit.Builder()


总结：
1.动态创建网络请求接口的实例（代理模式 - 动态代理）
2.创建 serviceMethod 对象（建造者模式 & 单例模式（缓存机制））
3.对 serviceMethod 对象进行网络请求参数配置：通过解析网络请求接口方法的参数、返回值和注解类型，
    从Retrofit对象中获取对应的网络请求的url地址、网络请求执行器、网络请求适配器 & 数据转换器。（策略模式）
4.对 serviceMethod 对象加入线程切换的操作，便于接收数据后通过Handler从子线程切换到主线程从而对返回数据结果进行处理（装饰模式）
最终创建并返回一个OkHttpCall类型的网络请求对象
```
######  okhttp3源码
```

```
######  rxjava2源码
```
RxJava是一个观察者模式的架构
被观察者(Observable)和观察者(Subscriber) 它们产生事件和处理事件
Backpressure背压是指在异步场景中，被观察者发送事件速度远快于观察者的处理速度的情况下，一种告诉上游的被观察者降低发送速度的策略
Observable类是不支持背压的 Observable是Reactive的一个抽象基类，Observable提供工厂方法

```
######  greedao源码
```
GreenDao
优点：
效率很高，插入和更新的速度是ormlite的2倍，加载实体的速度是ormlite的4.5倍。
文件较小（<100K），占用更少的内存 ，但是需要create Dao，
操作实体灵活：支持get，update，delete等操作

缺点：
学习成本较高。其中使用了一个java工程根据一些属性和规则去generate一些基础代码，类似于javaBean但会有一些规则，
另外还有QueryBuilder、Dao等API，所以首先要明白整个过程，才能方便使用。没有ORMLite那样封装的完整，
不过greenDao的官网上也提到了这一点，正是基于generator而不是反射，才使得其效率高的多。

ormlite
基于注解和反射的的方式,导致ormlite性能有着一定的损失(注解其实也是利用了反射的原理)
优点：
文档较全面，社区活跃，有好的维护，使用简单，易上手。
缺点：
基于反射，效率较低
```
######  Fresco源码  项目中图片加载用的什么以及原理
```
DraweeHierarchy(M)、DraweeView(V)、DraweeController(C)是按照MVC的模式来进行设计
DraweeHierarchy是Model提供绘制的drawable，
DraweeController是Controller负责图片的下载和处理等逻辑
DraweeView通过DraweeHolder持有了一个DraweeHierarchy和一个DraweeController

DraweeHierarchy:

通过GenericDraweeHierarchyBuilder的build方法会创建一个GenericDraweeHierarchy对象
它会实现SettableDraweeHierarchy接口，同时最终都会在对应的DraweeView中的Controller来调用
一共有6个接口方法，它们分别为：
void reset(); 重新初始化Hierarchy
void setImage(Drawable drawable, float progress, boolean immediate); 设置实际需要展示的图片，其中progress表示图片的加载质量进度(在渐进式中会使用到)
void setProgress(float progress, boolean immediate); 更新图片加载进度
void setFailure(Throwable throwable); 图片加载失败时调用，可以设置failureImage
void setRetry(Throwable throwable); 当图片加载失败时重新进行加载，可以设置retryImage
void setControllerOverlay(Drawable drawable); 用来设置图层覆盖
Fresco的图层树
那么这些图层结构是通过layers数组来体现的，可以来看下GenericDraweeHierarchy的源码

DraweeController:

ImagePipeline  数据管道
数据管道，自然是与网络请求与缓存数据有关。其实我们可以把它理解为多个管道的集合，最终显示的图片资源就是来自于它们中的其中一个
    fetchDecodedImage() 发送请求，返回decode image的数据源。
    fetchEncodedImage() 发送请求，返回encoded image的数据源。
    prefetchToBitmapCache() 发送预处理请求，获取预处理的bitmap缓存数据。
    prefetchToDiskCache() 发送预处理请求，获取预处理的磁盘缓存数据。
    submitFetchRequest() 发送请求，获取相应类型的数据源。
    submitPrefetchRequest() 发送预处理请求，获取相应类型的缓存数据。
    这里用的最多的还是fetchDecodedImage()
        Producer，这是一个生产者，其实我们会发现submitFetchRequest方法中的producer传入的其实是一个队列
ControllerListener

ControllerBuilder
既然是builder模式，最终的目的自然就是用来创建Controller在这里Controller的builder类是PipelineDraweeControllerBuilder
Controller
PipelineDraweeController继承于AbstractDraweeController
在PipelineDraweeController中主要的方法有三个

    Drawable createDrawable(CloseableImage closeableImage) 这是内部类DrawableFactory中的方法，是一个工厂，
              不言而喻它是用来创建Drawable的，在数据源返回的时候回调，进而显示到Hierarchy层。
    getDataSource() 获取数据源通道，与其建立联系。
    void setHierarchy(@Nullable DraweeHierarchy hierarchy) 设置Hierarchy图层，内部持有的其实是SettableDraweeHierarchy接口对象。
             所以内部调用的也就是它的6个接口方法。之前的文章也有提及，用来控制图片加载过程中的显示逻辑。

AbstractDraweeController中
onAttach与onDetach方法，它们分别是处理数据加载与释放
onAttach()
    在这个方法中mEventTracker是事件记录器，默认是开启的,如果要关闭则需要在*Fresco.initialize()之前调用DraweeEventTracker.disable()关闭；
    然后就是将其从资源推迟释放机制中取消,最后就是调用submitRequest()
    submitRequest()逻辑方面的处理，总的来说就是先从内存中获取如果存在就直接拿来用，否则就通过DataSource从网络或者是本地资源中获取。
    使用DataSource方式会使用到DataSubscriber，即订阅方式。当数据源已经获取到时，发送通知给订阅者，因此分别回调订阅者的方法。
    上述两种方式只要成功了都会交由*onNewResultInternal()处理，
    而失败则由onFailureInternal()处理，同时请求进度处理由onProgressUpdateInternal()*处理。
onDetach()*就简单多了，这里它只是对资源进行释放，释放的策略也是推迟释放策略DeferredReleaser。



Fresco内存缓存

Fresco 一共有三级缓存机制，其中前两级内存缓存都存储在java heap中，本地缓存存储在本地文件目录中。
CacheKey
Fresco中专门用于缓存键的接口，在CacheKeyFactory中定义了获取Cachekey的工厂方法。
有这两种类实现了CacheKey：
    BitmapMemoryCacheKey 
        用于已解码的内存缓存键，会对Uri字符串、缩放尺寸、解码参数,PostProcessor等关键参数进行hashCode作为唯一标识
        通过CacheKeyFactory中的getBitmapCacheKey工厂方法获取
    SimpleCacheKey
        普通的缓存键实现，使用传入字符串的hashCode作为唯一标识，所以需要保证相同键传入字符串相同。
        通过CacheKeyFactory的getEncodedCacheKey工厂方法实现

BitmapMemoryCache（已解码的内存缓存）
EncodedMemoryCache（未解码的内存缓存）
区别:
已解码内存缓存的数据是CloseableReference<CloseableBitmap>
未解码内存缓存的数据是CloseableReference。即他们的实现方式一样，
区别仅仅在于资源的测量与释放方式不同。它们使用ValueDescriptor来描述不同资源的数据大小，使用不同的ResourceReleaser来释放资源。

缓存算法
Fresco中定义的LRU缓存载体-CountingLruMap内存缓存中使用了LRU(Least Recent Used)来提高缓存功能
在CountingLruMap中使用了LinkedHashMap作为数据存储载体，这个HashMap很特别，它内部有一个双向链表，在做查找操作的时候，
从最先插入的单位开始查询。这就提供了一种好处：**它能够很快地删除掉最早插入的单位！**所以它非常适合LRU缓存来使用。
由于在LinkedHashMap中重复插入相同单位并不会影响链表顺序，所以要用CountingLruMap将它包装重写add方法
去重复，实现缓存池中已经使用的大小等

Fresco中实现具体内存缓存的类是CountingMemoryCache，它内部维持着几个重要参数：
ExclusiveEntries存储着未被使用的对象的CountingLruMap；
CachedEntries存储着所有对象的CountingLruMap；
MemoryCacheParams存储着最大缓存对象数量、缓存池大小等参数、
PARAMS_INTERCHECK_INTERVAL_MS检查缓存参数变化的事件间隔：5分钟；
它使用一个内部类Entry来封装缓存对象，除了记录缓存键、缓存对象之外，它还记录着该对象的引用数量（clientCount）及是否被缓存追踪（isOrphan）
注意：每个缓存对象只有满足clientCount为0并且isOrphan为true时才可以被释放


```
###### AOP IOC 的好处以及在 Android 开发中的应用；
AOP 面向切面编程
OP注解与使用
@Aspect：声明切面，标记类
@Pointcut(切点表达式)：定义切点，标记方法
@Before(切点表达式)：前置通知，切点之前执行
@Around(切点表达式)：环绕通知，切点前后执行
@After(切点表达式)：后置通知，切点之后执行
@AfterReturning(切点表达式)：返回通知，切点方法返回结果之后执行
@AfterThrowing(切点表达式)：异常通知，切点抛出异常时执行


######  recycleview的原理 
```
原文：https://blog.csdn.net/Yuequnchen/article/details/80336162 

dapterHelper: Update行为记录/整合/分发系统
LayoutManager: 布局系统分化了View管理和布局职责
ItemDecoration: View展示装饰器
RecycleBin更进一步，提供了多级缓存
提供了多级缓存,第二级缓存(ViewCacheExtension)是可选的，由使用者来自行定制，在缓存控制方面提供了扩展性。
第三级缓存(RecyclerViewPool)则实现RecyclerView之间的缓存分享
动画系统，ViewHolder机制

RecyclerView 滑动场景下的回收复用涉及到的结构体两个：mCachedViews 和 RecyclerViewPool。

mCachedViews 优先级高于 RecyclerViewPool，回收时，最新的 ViewHolder 都是往 mCachedViews 里放，如果它满了，
那就移出一个扔到 ViewPool 里好空出位置来缓存最新的 ViewHolder。复用时，也是先到 mCachedViews 里找 ViewHolder，但需要各种匹配条件，
概括一下就是只有原来位置的卡位可以复用存在 mCachedViews 里的 ViewHolder，如果 mCachedViews 里没有，那么才去 ViewPool 里找。

在 ViewPool 里的 ViewHolder 都是全新的 ViewHolder ，只要 type 一样，有找到，就可以拿出来复用，
但是需要触发 onBindViewHolder() 重新绑定下数据。mCachedViews 中的 ViewHolder 则可以直接拿来用

默认的情况下，cache 缓存 2 个 holder，RecycledViewPool 缓存 5 个 holder。
与 ListView 直接缓存 ItemView 不同，RecyclerView 缓存的是 ViewHolder。
ListView处理回收复用的类是RecycleBin一样，RecyclerView 用于回收ViewHolder的类是Recycler
```
######  组件化
```
多个module依赖Application

多组件project增量build比较快，是因为只有改动过的module才需要重新编译
模块合理划分

```

######  bitmap的处理，oom问题，超级大图处理


######  anr问题解决方法
######  webview的交互问题，滑动冲突问题等
######  项目中用到的框架及原理，比如OkHttp原理，eventbus，butternife等以及是否写过类似的
######  常见的比如单例，观察者，代理；讲究why？ when？ how？。比如单例的双重锁定什么时候失效,是否严谨以及为什么要那么写？！
```

```
######  Asset目录与res目录的区别。 
```
*res/raw和assets的相同点：
1.两者目录下的文件在打包后会原封不动的保存在apk包中，不会被编译成二进制。

*res/raw和assets的不同点：
1.res/raw中的文件会被映射到R.java文件中，访问的时候直接使用资源ID即R.id.filename；assets文件夹下的文件不会被映射到R.java中，访问的时候需要AssetManager类。
2.res/raw不可以有目录结构，而assets则可以有目录结构，也就是assets目录下可以再建立文件夹
```
######  Android怎么加速启动Activity。 
```
1. 减少onCreate时间
2. 减少主线程的阻塞时间
3. 提高Adapter和AdapterView的效率
4. 优化布局文件

分两种情况，启动应用 和 普通Activity 启动应用 ：Application 的构造方法，onCreate（） 方法中不要进行耗时操作，数据预读取(例如 init 数据) 放在异步中操作 
启动普通的Activity：A 启动B 时不要在 A 的 onPause（） 中执行耗时操作。因为 B 的 onResume（） 方法必须等待 A 的 onPause（） 执行完成后才能运行

```


````$xslt

apply()方法没有返回值；
apply()方法先提交到内存是一个原子操作，然后异步提交到Disk。
如果有两个editors同时修改preferences，最后一个调用apply()方法的会成功。apply()方法因为异步提交到Disk，所以效率更高
commit()方法有返回值；
commit()方法是直接提交到Disk，是一个原子操作，如果两个editors同时修改preferences，最后一个调用commit()方法的会成功。
总结：apply()方法和commit()方法都是先提交到内存，commit是同步提交到硬盘，并且有返回值；而apply()方法是异步提交到硬盘，没有返回值。

````



######Android两个应用能在同一个任务栈吗？
栈一般以包名命名，两个应用的签名和udid要相同

######Activity启动Service的两种方式
startService:生命周期和调用者不同.启动后若调用者未调用stopService而直接退出,Service仍会运行
bindService:生命周期与调用者绑定,调用者一旦退出,Service就会调用unBind->onDestory

######ViewHolder为什么要被声明成静态内部类
这个是考静态内部类和非静态内部类的主要区别之一。非静态内部类会隐式持有外部类的引用
ViewHolder加入一些复杂逻辑，做了一些耗时工作，那么如果ViewHolder是非静态内部类的话，就很容易出现内存泄露
######mipmap文件夹和drawable文件夹的区别
它只是用来放启动图标的,好处就是，你只用放一个mipmap图标，它就会给你各种版本（比如平板，手机）的apk自动生成相应分辨率的图标，以节约空间。
######Android 线程间通信有哪几种方式(重要)
共享内存(变量);文件，数据库;Handler; Java 里的 wait()，notify()，notifyAll()
######谈谈对接口与回调的理解
接口回调就是指: 可以把使用某一接口的类创建的对象的引用赋给该接口声明的接口变量，那么该接口变量就可以调用被类实现的接口的方法
#####设置了"singleTask"启动模式的Activity的特点： 
1. 设置了"singleTask"启动模式的Activity，它在启动的时候，会先在系统中查找属性值affinity等于它的属性值 taskAffinity的任务存在;
如果存在这样的任务，它就会在这个任务中启动，否则就会在新任务中启动。
因此，如果我们想要设置了"singleTask"启动模式的Activity在新的任务中启动，就要为它设置一个独立的taskAffinity属性值。 
2. 如果设置了"singleTask"启动模式的Activity不是在新的任务中启动时，它会在已有的任务中查看是否已经存在相应的Activity实例，
如果存在，就会把位于这个Activity实例上面的Activity全部结束掉，即最终这个Activity实例会位于任务的堆栈顶端中。
#####本地广播 和 全局广播 源码分析
```
BroadcastReceiver是针对应用间、应用与系统间、应用内部进行通信的一种方式
LocalBroadcastReceiver仅在自己的应用内发送接收广播，也就是只有自己的应用能收到，数据更加安全广播只在这个程序里，而且效率更高。

BroadcastReceiver 使用
1.制作intent（可以携带参数）
2.使用sendBroadcast()传入intent;
3.制作广播接收器类继承BroadcastReceiver重写onReceive方法（或者可以匿名内部类啥的）
4.在java中（动态注册）或者直接在Manifest中注册广播接收器（静态注册）使用registerReceiver()传入接收器和intentFilter
5.取消注册可以在OnDestroy()函数中，unregisterReceiver()传入接收器
LocalBroadcastReceiver 使用
LocalBroadcastReceiver不能静态注册，只能采用动态注册的方式。
在发送和注册的时候采用，LocalBroadcastManager的sendBroadcast方法和registerReceiver方法


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








































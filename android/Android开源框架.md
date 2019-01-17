######进程划分
```
1、如何查看进程解基本信息
对于任何一个进程，我们都可以通过adb shell ps|grep 的方式来查看它的基本信息
    值	解释
    u0_a16	USER 进程当前用户
    3881	进程ID
    1223	进程的父进程ID
    873024	进程的虚拟内存大小
    37108	实际驻留”在内存中”的内存大小
    com.wangjing.processlive	进程名
2、进程划分
Android中的进程跟封建社会一样，分了三流九等，Android系统把进程的划为了如下几种（重要性从高到低）
2.1、前台进程(Foreground process)
    场景： 
        - 某个进程持有一个正在与用户交互的Activity并且该Activity正处于resume的状态。 
        - 某个进程持有一个Service，并且该Service与用户正在交互的Activity绑定。 
        - 某个进程持有一个Service，并且该Service调用startForeground()方法使之位于前台运行。 
        - 某个进程持有一个Service，并且该Service正在执行它的某个生命周期回调方法，比如onCreate()、 onStart()或onDestroy()。 
        - 某个进程持有一个BroadcastReceiver，并且该BroadcastReceiver正在执行其onReceive()方法。
    用户正在使用的程序，一般系统是不会杀死前台进程的，除非用户强制停止应用或者系统内存不足等极端情况会杀死。
2.2、可见进程(Visible process)
    场景： 
        - 拥有不在前台、但仍对用户可见的 Activity（已调用 onPause()）。 
        - 拥有绑定到可见（或前台）Activity 的 Service
    用户正在使用，看得到，但是摸不着，没有覆盖到整个屏幕,只有屏幕的一部分可见进程不包含任何前台组件，
    一般系统也是不会杀死可见进程的，除非要在资源吃紧的情况下，要保持某个或多个前台进程存活
2.3、服务进程(Service process)
    场景 
        - 某个进程中运行着一个Service且该Service是通过startService()启动的，与用户看见的界面没有直接关联。
    在内存不足以维持所有前台进程和可见进程同时运行的情况下，服务进程会被杀死
2.4、后台进程(Background process)
    场景： 
        - 在用户按了”back”或者”home”后,程序本身看不到了,但是其实还在运行的程序，比如Activity调用了onPause方法
    系统可能随时终止它们，回收内存
2.5、空进程(Empty process)
    场景： 
        - 某个进程不包含任何活跃的组件时该进程就会被置为空进程，完全没用,杀了它只有好处没坏处,第一个干它!
        
3、内存阈值
上面是进程的分类，进程是怎么被杀的呢？系统出于体验和性能上的考虑，app在退到后台时系统并不会真正的kill掉这个进程，
而是将其缓存起来。打开的应用越多，后台缓存的进程也越多。在系统内存不足的情况下，系统开始依据自身的一套进程回收机制来判断要kill掉哪些进程，
以腾出内存来供给需要的app, 这套杀进程回收内存的机制就叫 Low Memory Killer。
那这个不足怎么来规定呢，那就是内存阈值，
我们可以使用cat /sys/module/lowmemorykiller/parameters/minfree来查看某个手机的内存阈值

进程是有它的优先级的，这个优先级通过进程的adj值来反映，它是linux内核分配给每个系统进程的一个值，代表进程的优先级，
进程回收机制就是根据这个优先级来决定是否进行回收，adj值定义在com.android.server.am.ProcessList类中
oom_adj的值越小，进程的优先级越高，普通进程oom_adj值是大于等于0的，而系统进程oom_adj的值是小于0的，
我们可以通过cat /proc/进程id/oom_adj可以看到当前进程的adj值。 



电话等进程的adj为-12已基本不可能被杀死了，
init.rc中将init进程的oom_adj设置为了-16，已经是永生进程了。

```
######进程保活的几种方式
```
1 Service设置成START_STICKY，kill 后会被重启（等待5秒左右），重传Intent，保持与重启前一样
2 通过 startForeground将进程设置为前台进程，做前台服务，优先级和前台应用一个级别，除非在系统内存非常缺，否则此进程不会被 kill
3 双进程Service：让2个进程互相保护，其中一个Service被清理后，另外没被清理的进程可以立即重启进程
4 QQ黑科技:在应用退到后台后，另起一个只有 1 像素的页面停留在桌面上，让自己保持前台状态，保护自己不被后台清理工具杀死
5 在已经root的设备下，修改相应的权限文件，将App伪装成系统级的应用（Android4.0系列的一个漏洞，已经确认可行）
6 Android系统中当前进程(Process)fork出来的子进程，被系统认为是两个不同的进程。当父进程被杀死的时候，子进程仍然可以存活，并不受影 响。
  鉴于目前提到的在Android-Service层做双守护都会失败，我们可以fork出c进程，多进程守护。死循环在那检查是否还存在，
  具体的思路如下 （Android5.0以下可行）
    1） 用C编写守护进程(即子进程)，守护进程做的事情就是循环检查目标进程是否存在，不存在则启动它。
    2）在NDK环境中将1中编写的C代码编译打包成可执行文件(BUILD_EXECUTABLE)。
    3） 主进程启动时将守护进程放入私有目录下，赋予可执行权限，启动它即可。
7 联系厂商，加入白名单
```
#####进程保活方案
```
1、开启一个像素的Activity  (据说这个是手Q的进程保活方案)
    只需要在锁屏的时候在本进程开启一个Activity，为了欺骗用户，让这个Activity的大小是1像素，并且透明无切换动画，
    在开屏幕的时候，把这个Activity关闭掉，所以这个就需要监听系统锁屏广播，试过了，的确好使
        1 MainActivity启动Service服务
        2 Service在启动服务时使用registerReceiver注册监听器，编写广播接收器监听锁屏和解锁action：
        3 OnePixelActivity(锁屏的时候启动一个像素的Activity)
    通过adb shell可以看到，在锁屏前应用所处的进程oom_adj值是较高的，锁屏后由于启动了Activity，oom_adj值降低了，
    进程的等级得到了相应的提高，变得更难以被回收了，这样可以一定程度上缓解我们的应用被第三方应用或系统管理工具在锁屏后为省电而被杀死的情况：

2.利用Notification提升权限  (微信用过的进程保活方案)
    这种方法也适用于Service在后台提供服务的场景。由于没有Activity的缘故，我们Service所在进程的oom_adj值通常是较高的，
    进程等级较低，容易被系统回收内存时清理掉。这时我们可以通过startForeground方法，把我们的服务提升为前台服务，提高进程的等级。
    但提升为前台服务必须绑定一个相应的Notification这是我们不愿意看到的。
    原理：Android 的前台service机制。但该机制的缺陷是通知栏保留了图标。
    对于 API level < 18 ：调用startForeground(ID， new Notification())，发送空的Notification ，图标则不会显示。
    对于 API level >= 18：在需要提优先级的service A启动一个InnerService，两个服务同时startForeground，
        且绑定同样的 ID。Stop 掉InnerService ，这样通知栏图标即被移除。
    这方案实际利用了Android前台service的漏洞。微信在评估了国内不少app已经使用后，才进行了部署。
    其实目标是让大家站同一起跑线上，哪天google 把漏洞堵了，效果也是一样的。
3.利用JobScheduler机制拉活
    JobService和JobScheduler是Android5.0（API 21）引入的新API，我们可以通过该机制来拉活我们的Service所在进程。
    首先我们通过继承JobService类来实现自己的Service，记得重写onStartJob和onStopJob方法。
    然后我们在onCreate方法里面通过JobScheduler来调度我们的Service，值得注意的是需要把参数设置为Persisted：
    使用JobService和把Service设置为Persisted都需要我们在Manifest中配置相应的参数：
    然后运行服务即可发现，在Service所在进程被杀掉后，我们的Service会自动重启：
    该方法依然有它的缺陷：
    首先，JobService只适用于Android5.0以上的系统；其次，当进程被force-stop指令杀死后，JobService依旧无法拉活进程。
4.进程相互唤醒
    顾名思义,就是指的不同进程,不同app之间互相唤醒,如你手机里装了支付宝、淘宝、天猫、UC等阿里系的app，
    那么你打开任意一个阿里系的app后，有可能就顺便把其他阿里系的app给唤醒了

1、jni保活，在5.0以前，android系统本身是不管理jni层的，所以用linux那套fork机制，可以让进程和app分开，
    就算关闭app也不会影响到。所以那时很多人说android非常的卡，幸运的是我那段时间用的ios，这些进程连用户都没法关掉，真的特别恶心

2、jobservice，在5.0之后，连native层也会受到系统限制，比如kill、doze之类的，也就是说无论是杀掉还是冻结，
都只和你启动的第一个进程有关，后面不再以父子的关系去看待，而是以历史同组的关系去看待。
这个历史同组真的是一个很关键的改变，其实不止killgroup的作用性，无论怎样写代码，
都脱离不了系统对你app的控制，比如让你何时休眠之类的。说了这么多，那该说下JobService了，
JobService的作用是什么呢？关键的作用和正确的用法是在app关闭后，可以在自动创建进程后台干任何事情，
包括拉活activity、service或者执行任何的代码。但JobService并不意味着你不受任何限制，比如受到doze、monitor之类的管理。

3、doze，doze是在android6.0的时候出现的，作用在锁屏之后，对app就行一系列的管理，可以说doze是一种底层机制。
感觉doze还是很友好的，比如说提供白名单api、延迟执行的操作等等。就是说你每个app都会给你机会去发通知之类的，
虽然时间受到限制，但除即时通信外应该也够了，关键是并没有关掉你的进程。并且如果在app中授权ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS，
doze就不会再去管你，也就是如果即时通信的话，加doze白名单，再通过JobService拉活，双服务守护，基本上没任何问题。
但人为了利益真的是不择手段，只要给我机会就天天发一堆垃圾消息，绝对不让用户清闲，时不时向后台收发点数据什么的，
造成doze对android的改观并不明显的感觉。而且任意一个app进白名单，随意weaklock的话，整个系统环境就会被破坏。

4、monitor，monitor可以看成是对doze的一种增强实现，并且是对手机系统管理的一种实现，在绝大部分7.0的rom中都会有这东西，
国产那几乎就是100%有了。在7.0的时候，这monitor悄无声息的多了一个白名单，这东西就真的是一个质的改变了，
随叫用户总是让Google背锅呢。这monitor也可以看作是一个用root权限的app，在升级到7.0默认好像就开启了，
那么它就先把除同等root权限的系统app排除外，对剩余的所有app就行管理（我升级幸免的就Alipay、WeChat、搜狗输入法），
把你这些app先全拉到sleeping列表中，之后安装的app默认也在sleeping列表中。那么它有多强呢，对列表中的app，只要app关闭后，
就算你通过JobService拉起，你也仅有15分钟的存活时间片，一旦耗光这15分钟的时间片，你就要永远说88了，
直到用户下次打开你的app。当然，如果用户手动把app加到白名单，那么就算被关闭也不会被退出任何一个进程。
说了这么多，结论就是就算你在doze白名单里面，也没任何作用，除非你能进monitor里面的白名单，否则就直接被拉进小黑屋出不来哦。

5、还有一种方法是拉活activity，不过有一定失败的概率，就是在destroy的时候判断是否关闭并拉活，不能保证100%成功，
而且成功会在后台列表中出现。。。以前我看有人在jni层有用类似的办法做守护进程，就是在关闭之前去开新的，
和系统抢时间，这个其实有一定合理性在，但属于是概率性事件了。如果使用JobService拉活activity也是和拉活service 同样的命运，
毕竟怎么也跑不掉系统调用的killgroup，JobService能拉活完全是可控的故意给你的机会，并不是某些人所说的BUG

```
######JobService     JobScheduler
```
JobService是继承自Service的抽象类。
看来JobService的本质还是Service，只不过封装了些额外的方法和逻辑。
JobService只是实际的执行和停止任务的回调入口。
那如何将这个入口告诉系统，就需要用到JobScheduler了。

常用方法：
schedule(JobInfo job)安排要执行的工作
cancel(int jobId)取消指定的工作
cancelAll()取消全部作业
工作描述（JobInfo）
介绍：通过内部的Builder类设置任务执行的条件，比如：充电状态，网络状态，空闲状态等。
实例化： JobInfo.Builder().build()
后台工作（JobService）

源码
    jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);//获取JobScheduler
    JobInfo.Builder builder = new JobInfo.Builder(123, new ComponentName(this, MyJobService.class));
    builder.setMinimumLatency(5000);//最小延迟时间
    builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);//网络类型   任务执行的条件
    builder.setRequiresCharging(true);//需要充电
    jobScheduler.schedule(builder.build());//将JobInfo加入调度。

```
####### 进程保活方案

```
总结：
前台进程(Foreground process)
可见进程(Visible process)
服务进程(Service process)
后台进程(Background process)
空进程(Empty process)



3.1. 利用 Activity 提升权限
3.1.1. 方案设计思想
监控手机锁屏解锁事件，在屏幕锁屏时启动1个像素的 Activity，在用户解锁时将 Activity 销毁掉。注意该 Activity 需设计成用户无感知。
通过该方案，可以使进程的优先级在屏幕锁屏时间由4提升为最高优先级1。
适用场景： 本方案主要解决第三方应用及系统管理工具在检测到锁屏事件后一段时间（一般为5分钟以内）内会杀死后台进程，已达到省电的目的问题。
适用版本： 适用于所有的 Android 版本。

3.2. 利用 Notification 提升权限
3.2.1. 方案设计思想
Android 中 Service 的优先级为4，通过 setForeground 接口可以将后台 Service 设置为前台 Service，使进程的优先级由4提升为2，
从而使进程的优先级仅仅低于用户当前正在交互的进程，与可见进程优先级一致，使进程被杀死的概率大大降低。
3.2.2. 方案实现挑战
从 Android2.3 开始调用 setForeground 将后台 Service 设置为前台 Service 时，必须在系统的通知栏发送一条通知，也就是前台 Service 与一条可见的通知
时绑定在一起的。对于不需要常驻通知栏的应用来说，该方案虽好，但却是用户感知的，无法直接使用。
3.2.3. 方案挑战应对措施
通过实现一个内部 Service，在 LiveService 和其内部 Service 中同时发送具有相同 ID 的 Notification，然后将内部 Service 结束掉。
随着内部 Service 的结束，Notification 将会消失，但系统优先级依然保持为2。
3.2.4. 方案适用范围
适用于目前已知所有版本。

4. 进程死后拉活的方案
4.1. 利用系统广播拉活
4.1.1. 方案设计思想
在发生特定系统事件时，系统会发出响应的广播，通过在 AndroidManifest 中“静态”注册对应的广播监听器，即可在发生响应事件时拉活。
4.1.2. 方案适用范围
适用于全部 Android 平台。但存在如下几个缺点：
1） 广播接收器被管理软件、系统软件通过“自启管理”等功能禁用的场景无法接收到广播，从而无法自启。
2） 系统广播事件不可控，只能保证发生事件时拉活进程，但无法保证进程挂掉后立即拉活。
因此，该方案主要作为备用手段。

4.2. 利用第三方应用广播拉活
4.2.1. 方案设计思想
该方案总的设计思想与接收系统广播类似，不同的是该方案为接收第三方 Top 应用广播。
通过反编译第三方 Top 应用，如：手机QQ、微信、支付宝、UC浏览器等，以及友盟、信鸽、个推等 SDK，找出它们外发的广播，在应用中进行监听，
样当这些应用发出广播时，就会将我们的应用拉活。
4.2.2. 方案适用范围
该方案的有效程度除与系统广播一样的因素外，主要受如下因素限制：
1） 反编译分析过的第三方应用的多少
2） 第三方应用的广播属于应用私有，当前版本中有效的广播，在后续版本随时就可能被移除或被改为不外发。
这些因素都影响了拉活的效果。

4.3. 利用系统Service机制拉活
4.3.1. 方案设计思想
将 Service 设置为 START_STICKY，利用系统机制在 Service 挂掉后自动拉活：
4.3.2. 方案适用范围
如下两种情况无法拉活：
Service 第一次被异常杀死后会在5秒内重启，第二次被杀死会在10秒内重启，第三次会在20秒内重启，一旦在短时间内 Service 被杀死达到5次，则系统不再拉起。
进程被取得 Root 权限的管理工具或系统工具通过 forestop 停止掉，无法重启。

4.4. 利用Native进程拉活(Android5.0 以下)
4.4.1. 方案设计思想
主要思想：利用 Linux 中的 fork 机制创建 Native 进程，在 Native 进程中监控主进程的存活，当主进程挂掉后，在 Native 进程中立即对主进程进行拉活。
主要原理：在 Android 中所有进程和系统组件的生命周期受 ActivityManagerService 的统一管理。
而且，通过 Linux 的 fork 机制创建的进程为纯 Linux 进程，其生命周期不受 Android 的管理。
4.4.3. 方案适用范围
该方案主要适用于 Android5.0 以下版本手机。
该方案不受 forcestop 影响，被强制停止的应用依然可以被拉活，在 Android5.0 以下版本拉活效果非常好。

4.5. 利用 JobScheduler 机制拉活
4.5.1. 方案设计思想
Android5.0 以后系统对 Native 进程等加强了管理，Native 拉活方式失效。系统在 Android5.0 以上版本提供了 JobScheduler 接口，
系统会定时调用该进程以使应用进行一些逻辑操作。
4.5.2. 方案适用范围
该方案主要适用于 Android5.0 以上版本手机。
该方案在 Android5.0 以上版本中不受 forcestop 影响，被强制停止的应用依然可以被拉活，在 Android5.0 以上版本拉活效果非常好。
仅在小米手机可能会出现有时无法拉活的问题。

4.6. 利用账号同步机制拉活
4.6.1. 方案设计思想
Android 系统的账号同步机制会定期同步账号进行，该方案目的在于利用同步机制进行进程的拉活
该方案需要在 AndroidManifest 中定义账号授权与同步服务。
4.6.2. 方案适用范围
该方案适用于所有的 Android 版本，包括被 forestop 掉的进程也可以进行拉活。
最新 Android 版本（Android N）中系统好像对账户同步这里做了变动，该方法不再有效。

5. 其他有效拉活方案
经研究发现还有其他一些系统拉活措施可以使用，但在使用时需要用户授权，用户感知比较强烈。
这些方案包括：
利用系统通知管理权限进行拉活
利用辅助功能拉活，将应用加入厂商或管理软件白名单。
这些方案需要结合具体产品特性来搞。
其他还有一些技术之外的措施，比如说应用内 Push 通道的选择：
国外版应用：接入 Google 的 GCM。
国内版应用：根据终端不同，在小米手机（包括 MIUI）接入小米推送、华为手机接入华为推送；其他手机可以考虑接入腾讯信鸽或极光推送与小米推送做 A/B Test。


```




######  Android中App可分配内存的大小
```
 google原生OS的默认值是16M，但是各个厂家的OS会对这个值进行修改。

比如本人小米2S为例，这个值应该是96M。
先看机器的内存限制，在/system/build.prop文件中：
heapgrowthlimit就是一个普通应用的内存限制，用ActivityManager.getLargeMemoryClass()获得的值就是这个。
而heapsize是在manifest中设置了largeHeap=true 之后，可以使用的最大内存值
结论就是，设置largeHeap的确可以增加内存的申请量。但不是系统有多少内存就可以申请多少，而是由dalvik.vm.heapsize限制。
你可以在app manifest.xml加 largetHeap=true



```


###### 插件化（activity如何加载及资源处理）
```
什么是动态加载技术
动态加载技术就是使用类加载器加载相应的apk、dex、jar(必须含有dex文件)，再通过反射获得该apk、dex、jar内部的资源
（class、图片、color等等）进而供宿主app使用。
关于动态加载使用的类加载器
PathClassLoader - 只能加载已经安装的apk，即/data/app目录下的apk。
DexClassLoader - 能加载手机中未安装的apk、jar、dex，只要能在找到对应的路径。

插件化技术主要解决两个问题：

2.1 类加载
代码加载 
    类的加载可以使用Java的ClassLoader机制，还需要组件生命周期管理。

2.2 单DexClassLoader与多DexClassLoader
通过给插件apk生成相应的DexClassLoader便可以访问其中的类，
这边又有两种处理方式，有单DexClassLoader和多DexClassLoader两种结构。
多DexClassLoader
    对于每个插件都会生成一个DexClassLoader，当加载该插件中的类时需要通过对应DexClassLoader加载。这样不同插件的类是隔离的，
    当不同插件引用了同一个类库的不同版本时，不会出问题。RePlugin采用的是该方案。
单DexClassLoader
    将插件的DexClassLoader中的pathList合并到主工程的DexClassLoader中。这样做的好处时，可以在不同的插件以及主工程间直接互相调用类和方法，
    并且可以将不同插件的公共模块抽出来放在一个common插件中直接供其他插件使用。Small采用的是这种方式。
互相调用
    插件调用主工程
        在构造插件的ClassLoader时会传入主工程的ClassLoader作为父加载器，所以插件是可以直接可以通过类名引用主工程的类。
    主工程调用插件
        若使用多ClassLoader机制，主工程引用插件中类需要先通过插件的ClassLoader加载该类再通过反射调用其方法。
        插件化框架一般会通过统一的入口去管理对各个插件中类的访问，并且做一定的限制。
        
        若使用单ClassLoader机制，主工程则可以直接通过类名去访问插件中的类。该方式有个弊病，
        若两个不同的插件工程引用了一个库的不同版本，则程序可能会出错，所以要通过一些规范去避免该情况发生。



2.3 资源加载   用AssetManager的隐藏方法addAssetPath。
    因此，只要将插件apk的路径加入到AssetManager中，便能够实现对插件资源的访问。
    具体实现时，由于AssetManager并不是一个public的类，需要通过反射去创建，并且部分Rom对创建的Resource类进行了修改，
    所以需要考虑不同Rom的兼容性。
资源路径的处理
    和代码加载相似，插件和主工程的资源关系也有两种处理方式
    合并式：addAssetPath时加入所有插件和主工程的路径
        合并式由于AssetManager中加入了所有插件和主工程的路径，因此生成的Resource可以同时访问插件和主工程的资源。
        但是由于主工程和各个插件都是独立编译的，生成的资源id会存在相同的情况，在访问时会产生资源冲突。
    
    独立式：各个插件只添加自己apk路径
        独立式时，各个插件的资源是互相隔离的，不过如果想要实现资源的共享，必须拿到对应的Resource对象。
资源冲突
合并式的资源处理方式，会引入资源冲突，原因在于不同插件中的资源id可能相同，所以解决方法就是使得不同的插件资源拥有不同的资源id。
资源id是由8位16进制数表示，表示为0xPPTTNNNN。PP段用来区分包空间，默认只区分了应用资源和系统资源，TT段为资源类型，NNNN段在同一个APK中从0000递增
所以思路是修改资源ID的PP段，对于不同的插件使用不同的PP段，从而区分不同插件的资源。
具体实现方式有两种
    1.修改aapt源码，编译期修改PP段。
    2.修改resources.arsc文件，该文件列出了资源id到具体资源路径的映射。
具体实现可以分别参考Atlas框架和Small框架。推荐第二种方式，不用入侵原有的编译流程。

三、四大组件支持

 Activity的支持是最复杂的 大致分为两种方式：
    ProxyActivity代理
    预埋StubActivity，hook系统启动Activity的过程
3.1 ProxyActivity代理
    在主工程中放一个ProxyActivy，启动插件中的Activity时会先启动ProxyActivity，在ProxyActivity中创建插件Activity，并同步生命周期
3.2 hook方式
如何通过hook的方式启动插件中的Activity，需要解决以下两个问题
    插件中的Activity没有在AndroidManifest中注册，如何绕过检测。
    如何构造Activity实例，同步生命周期
VirtualAPK为例，核心思路如下：
    先在Manifest中预埋StubActivity，启动时hook上图第1步，将Intent替换成StubActivity。
    hook第10步，通过插件的ClassLoader反射创建插件Activity\
    之后Activity的所有生命周期回调都会通知给插件Activity
3.3 其他组件
四大组件中Activity的支持是最复杂的，其他组件的实现原理要简单很多，简要概括如下
Service：Service和Activity的差别在于，Activity的生命周期是由用户交互决定的，而Service的生命周期是我们通过代码主动调用的，
    且Service实例和manifest中注册的是一一对应的。实现Service插件化的思路是通过在manifest中预埋StubService，
    hook系统startService等调用替换启动的Service，之后在StubService中创建插件Service，并手动管理其生命周期。
BroadCastReceiver：解析插件的manifest，将静态注册的广播转为动态注册。
ContentProvider：类似于Service的方式，对插件ContentProvider的所有调用都会通过一个在manifest中占坑的ContentProvider分发。



```
######  DexClassLoader和PathClassLoader的区别
```
区别在于调用父类构造器时，DexClassLoader多传了一个optimizedDirectory参数，这个目录必须是内部存储路径，用来缓存系统创建的Dex文件。
而PathClassLoader该参数为null，只能加载内部存储目录的Dex文件。

DexClassLoader：能够加载未安装的jar/apk/dex 
PathClassLoader：只能加载系统中已经安装过的apk

ClassLoader调用loadClass方法加载类
ClassLoader加载类时，先查看自身是否已经加载过该类，如果没有加载过会首先让父加载器去加载，
如果父加载器无法加载该类时才会调用自身的findClass方法加载，该机制很大程度上避免了类的重复加载

DexClassLoader的DexPathList
DexClassLoader重载了findClass方法，在加载类时会调用其内部的DexPathList去加载。DexPathList是在构造DexClassLoader时生成的，其内部包含了DexFile
DexPathList的loadClass会去遍历DexFile直到找到需要加载的类
有一种热修复技术正是利用了DexClassLoader的加载机制，将需要替换的类添加到dexElements的前面，这样系统会使用先找到的修复过的类。

DexClassLoader是一个可以从包含classes.dex实体的.jar或.apk文件中加载classes的类加载器。可以用于实现dex的动态加载、代码热更新等等。
这个类加载器必须要一个app的私有、可写目录来缓存经过优化的classes（odex文件），使用Context.getDir(String, int)方法可以创建一个这样的目录，
例如：
File dexOutputDir = context.getDir(“dex”, 0);

PathClassLoader提供一个简单的ClassLoader实现，可以操作在本地文件系统的文件列表或目录中的classes，但不可以从网络中加载classes。
```
######  热修复原理
```
热修复主要是通过android的类加载机制来实现的。Android中有两个类加载器PathClassLoader和DexClassLoader,
准确的来说应该是有三个，还有一个BaseDexClassLoader，BaseDexClassLoader是上面这两个类加载器的父类，
public class PathClassLoader extends BaseDexClassLoader{} 
public class BaseDexClassLoader extends ClassLoader{}

在BaseDexClassLoader里面有一个重要的属性DexPathList，DexPathList在BaseDexClassLoader的构造函数中被创建出来,
DexPathList里面有两个构造函数SplitDexPath()(将dexPath目录下的所有文件转成一个File集合） 
和makeDexElments()（将File集合转为Elment数组）， 在这个BaseDexClassLoader里面还有一个特别重要的方法，这也是它的核心方法。 
findClass()从Elemet数组中拿出一个个dex文件，在从dex文件中搜索class,正因为这个特性，
我们只需要将Element数组与App原Element数组合并，得到一个新的Element数组，要注意摆放的先后顺序，
然后将这个新的Element数组用反射的方式赋值给App当前类加载器的pathList中的Elements数组，
因为pathList的 findClass()是采用遍历方式一个个从Element中找class,而修复好的class所在的Element排在有Bug的class的Element的前面，
所以，当App再次从类加载器中拿Class时就只会拿到前面的Class,也就是Bug已经修复好的class。这就是热修复的原理
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





















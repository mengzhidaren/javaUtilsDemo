######插件化（activity如何加载及资源处理）
```





```

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
























######哈罗：（1个小时左右）
```

http 三次握手 层级说明

线程的生命周期 都有什么
线程池有几个，区别
wait()和sleep()的区别

悲观锁和乐观锁                                  (?)
   
 
MVC,MVP,MVVM架构                                
你用过哪些 设计模式  比如 图片框架用到了什么设计模式  
项目中用到的框架及原理                                 
比如OkHttp原理，eventbus，butternife等以及是否写过类似的 

数据库手写                                       (?)

本地广播 和 全局广播 区别  源代码如何实现的          (?)

进程保活方法  原理                                  (?)

Binder机制，进程通信

handler 机制  如何循环消息  
Looper 死循环为什么不会导致应用卡死  为什么要死循环   

activity启动模式
事件分发机制原理及滑动冲突的处理
如何自定义View viewGroup和View的区别 一些细节问题等等      
Android 绘图机制流程原理


内存优化 如何避免oom 内存泄露有几种  说明 原理




难点：
activity的启动流程  PMS 总过程                       
activity  Window 和 WindowManager 源码说明         


二面：（前面没回答好 10分钟不到pass）

项目里用到什么开源框架，你采用什么架构设计的，架构原理是什么                       
项目里的组件化如何实现：                                                         
组件化通信怎么做的：AB组件在壳子里(main)  你们项目里如何实现 A和main如何交互(方法和传参)  A组件调用B组件如何实现 ，如何交互      
有没有自已实现组件化开发                                                              

插件化用什么做的  原理            
  
有做过什么开源框架，如何实现                                               

公司项目保活怎么实现的  （其实要回答参考了什么什么,然后在回复如： 多进程保活机制里去1像素和server保活 ）   

用过什么开源库 如 原理说明   图片，数据库，网络                               



简历里写的CPU兼容如何做的      
                
有什么问题要问    (?)



```
###### 腾讯 上海 王者荣耀组 测试团队
```
http 三次握手 
网络层级说明 协议层说明  ssl原理
多线程断点下载原理 文件效验
hashmap源码实现   

长链接如何优化的  socket如何优化      

native  有写多少行ｃ代码（5000行）     
c 内存申请和　c++　内存申请 区别   delete 和 free区别　　　　
c 数组的数据结构　内存地址　排列                             
c++ 集合怎么实现的          
pthread 线程的生命周期            


```
    

######bilibili（18年底第一次面试 啥都没准备 0分）
```
http三次握手 四次挥手                           
http1.0协议详细说明                               
http2.0有什么功能  和1.0的比较                                           
tcp和udp的区别，tcp如何保证可靠的，丢包如何处理？tcp的阻塞和超时处理机制等等   
Https的工作流程，原理               
Http常见问题，协议                                             
Http和Https的区别
设计模式                                                    


多进程通信有几种  各个方式优缺点                                
多进程场景以及使用场景和常见问题，比如远程回调(其实就是通信方法如：AIDL)，数据大小限制，  
多进程保活机制                                              

线程锁  锁交互  锁方法 synchronized 锁普通方法和锁静态方法      
Bundle源码实现                                  
播放器源码实现 原理
ffmpeg解码流程 原理
硬件加速如何实现 原理
音视频同步方法如何实现 原理

直播采集端，摄像头采集和音频采集                            
录制屏幕，采集，音视频同步，渲染等                                   
推流
android平台下的硬编解码等                                     
有什么问题要问     (?)

```


###### find
```
如何申请更大内存给APP                                    
Git 版本切换 如何 修改master线上bug合并到Dev分支           
developer  d
master     m
d m都先提交到本地  checkout到m修改bug在提交到远程m  d checkout m


OOP面向对像 解决一个生活中的例子  面试官提了 大羊每2年生一小羊 寿命5年 用oop描述  
有什么问题要问

人事面：

为啥要离开呆了4年的公司
要求薪资多少（准确答案 现在薪资的基础上加10%到15%或者不加）
有什么问题要问     (?)


```
####### 么么直播(天天动听)    19.2.22
```
一面：
handler 原理  平常使用注意点
HashMap 源码实现   
Activity 四种启动方式  
fragment 传参  

Fresco源码 实现 
Fresco 缓存 源码 概述
socket源码优化
长链接时注意的点 

设计模式 
.......
二面：
ffmpeg 解码原理  从视频帧到yuv数据到opengl到shader流程   音频帧到pcm数据过程
硬解码 源代码过程概述 
OpengGl 环境 概述
EGL 绘制 camera 流程
camera预览面面 创建过程

h264压缩源码 头信息 和 分片方法      0x00000004  
音频帧数据 转 PCM  讨论了一下

HashMap 源码实现   

多线程 同步方法  同步优化  -----> 悲观锁
画了一个图 队列最大100  生产101大于 消费100时  如何优化     （？ 感觉回答的不好）

你用到的 源码设计模式  应用场景
Fresco 源码 重写一遍 如何设计 详细的 MVP思路
rxJava 设计模式 概述
intent 设计原理  为什么用intent 

有什么的问题？

三面：(前端总经理)

未来职业规划
为啥离开现在团队
对公司有什么想问的
现在面试了几家结果如何
对公司有什么顾虑的点



```
######非技术问题
```


 为什么离开公司
 
 希望加入什么团队
 
 负责哪块，这里会引发你项目中使用的技术问题
```

###### 没问到的 补充
```
JAVA 

弱引用、软引用区别                                        (?)
int、Integer有什么区别   主要考值传递和引用传递问题            (?)

ANDROID

插件化（activity如何加载及资源处理）
热修复原理
组件化
动态权限适配问题                                               (?)
换肤实现原理                                                    (?)

HandlerThread、IntentService理解                   
SharedPreference原理，能否跨进程？如何实现？                   (?)

消息分发机制 handler  Handler.sendMessageDelayed()怎么实现延迟的   





1.单例模式：好几种写法，要求会手写，分析优劣。一般双重校验锁中用到volatile，需要分析volatile的原理
2.观察者模式：要求会手写，有些面试官会问你在项目中用到了吗？实在没有到的可以讲一讲EventBus，它用到的就是观察者模式
3.适配器模式：要求会手写，有些公司会问和装饰器模式、代理模式有什么区别？
4.建造者模式+工厂模式：要求会手写
5.策略模式：这个问得比较少，不过有些做电商的会问。
6.MVC、MVP、MVVM：比较异同，选择一种你拿手的着重讲就行






（七）源码理解
一、网络框架库 Okhttp
二、消息通知 EventBus
    1.EventBus原理：建议看下源码，不多。内部实现：观察者模式+注解+反射
    2.EventBus可否跨进程问题？代替EventBus的方法（RxBus）
三、图片加载库（Fresco、Glide、Picasso）
    1.项目中选择了哪个图片加载库？为什么选择它？其他库不好吗？这几个库的区别
    2.项目中选择图片库它的原理，如Glide（LruCache结合弱引用），那么面试官会问LruCache原理，进而问LinkedHashMap原理，
    这样一层一层地问，所以建议看到不懂的追进去看。如Fresco是用来MVC设计模式，5.0以下是用了共享内存，那共享内存怎么用？
    Fresco怎么实现圆角？Fresco怎么配置缓存？
四、消息推送Push
    1.项目中消息推送是自己做的还是用了第三方？如极光。还有没有用过其他的？这几家有什么优势区别，基于什么原因选择它的？
    2.消息推送原理是什么？如何实现心跳连接？
五、TCP/IP、Http/Https
    网络这一块如果简历中写道熟悉TCP/IP协议，Http/Https协议，那么肯定会被问道，我就验证了。
    一般我会回答网络层关系、TCP和UDP的区别，TCP三次握手（一定要讲清楚，SYN、ACK等标记位怎样的还有报文结构都需要熟悉下），
    四次挥手。为什么要三次握手？DDoS攻击。为什么握手三次，挥手要四次？Http报文结构，
    一次网络请求的过程是怎样的？Http和Https有什么不同？SSL/TLS是怎么进行加密握手的？证书怎么校验？
    对称性加密算法和非对称加密算法有哪些？挑一个熟悉的加密算法简单介绍下？DNS解析是怎样的？
六、热更新、热修复、插件化
七、新技术




一、UI优化
a.合理选择RelativeLayout、LinearLayout、FrameLayout,RelativeLayout会让子View调用2次onMeasure，而且布局相对复杂时，onMeasure相对比较复杂，效率比较低，LinearLayout在weight>0时也会让子View调用2次onMeasure。LinearLayout weight测量分配原则。
b.使用标签<include><merge><ViewStub>
c.减少布局层级，可以通过手机开发者选项>GPU过渡绘制查看，一般层级控制在4层以内，超过5层时需要考虑是否重新排版布局。
d.自定义View时，重写onDraw()方法，不要在该方法中新建对象，否则容易触发GC，导致性能下降
e.使用ListView时需要复用contentView，并使用Holder减少findViewById加载View。
f.去除不必要背景，getWindow().setBackgroundDrawable(null)
g.使用TextView的leftDrawabel/rightDrawable代替ImageView+TextView布局

主要为了避免OOM和频繁触发到GC导致性能下降
a.Bitmap.recycle(),Cursor.close,inputStream.close()
b.大量加载Bitmap时，根据View大小加载Bitmap，合理选择inSampleSize，RGB_565编码方式；使用LruCache缓存
c.使用 静态内部类+WeakReference 代替内部类，如Handler、线程、AsyncTask
d.使用线程池管理线程，避免线程的新建
e.使用单例持有Context，需要记得释放，或者使用全局上下文
f.静态集合对象注意释放
g.属性动画造成内存泄露

四、其他性能优化
a.常量使用static final修饰
b.使用SparseArray代替HashMap
c.使用线程池管理线程
d.ArrayList遍历使用常规for循环，LinkedList使用foreach
e.不要过度使用枚举，枚举占用内存空间比整型大
f.字符串的拼接优先考虑StringBuilder和StringBuffer
g.数据库存储是采用批量插入+事务


```


 
 





















































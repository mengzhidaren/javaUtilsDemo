######为什么Android引入广播机制?
a:从MVC的角度考虑(应用程序内) 
b：程序间互通消息(例如在自己的应用程序内监听系统来电)
c：效率上(参考UDP的广播协议在局域网的方便性)
d：设计模式上(反转控制的一种应用，类似监听者模式)
###### MVC 、MVP 和 MVVM 三种架构的区别和优点；
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

###### Retrofit 的源码和原理；
Retrofit就是一个封装了Http请求的框架，底层的网络请求都是使用的Okhttp，
本身只是简化了用户网络请求的参数配置等，还能与Rxjava相结合，使用起来更加简便。
######  插件化（activity如何加载及资源处理）
```

```
######  组件化
```

```
######  热修复原理
```

```
######  内存泄漏如何处理及如何排查，LeakCanary原理
```

```
######  handler常见问题，机制，原理
```

```
######  项目中图片加载用的什么以及原理
```

```

######  recycleview的原理
```

```

######  bitmap的处理，oom问题，超级大图处理
```

```

######  MVC,MVP,MVVM架构
```

```
######  anr问题解决方法
```

```
######  webview的交互问题，滑动冲突问题等
```

```
######  intentservice和service的区别和原理
```

```

######  多进程场景以及所带来的问题
```

```
######  项目中用到的框架及原理，比如OkHttp原理，eventbus，butternife等以及是否写过类似的
```

```
######  常见的比如单例，观察者，代理；讲究why？ when？ how？。比如单例的双重锁定什么时候失效是否严谨以及为什么要那么写？！
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
######  
```

```
######  
```

```
######  
```

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














































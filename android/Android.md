######为什么Android引入广播机制?
a:从MVC的角度考虑(应用程序内) 
b：程序间互通消息(例如在自己的应用程序内监听系统来电)
c：效率上(参考UDP的广播协议在局域网的方便性)
d：设计模式上(反转控制的一种应用，类似监听者模式)

######插件化（activity如何加载及资源处理）

######组件化

######热修复原理



######SharedPreferences的apply与commit的区别
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














































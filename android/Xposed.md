######大概简述下Xposed的原理吧
```
Android基于Linux，第一个启动的进程自然是init进程，该进程会 
启动所有Android进程的父进程——Zygote(孵化)进程，该进程的启动配置在 
/init.rc脚本中，而Zygote进程对应的执行文件是/system/bin/app_process， 
该文件完成类库的加载以及一些函数的调用工作。在Zygote进程创建后， 
再fork出SystemServer进程和其他进程。

而Xposed Framework呢，就是用自己实现的app_process替换掉了系统原本 
提供的app_process，加载一个额外的jar包，然后入口从原来的： 
com.android.internal.osZygoteInit.main()被替换成了： 
de.robv.android.xposed.XposedBridge.main()， 
然后创建的Zygote进程就变成Hook的Zygote进程了，而后面Fork出来的进程 
也是被Hook过的。这个Jar包在： 
/data/data/de.rbov.android.xposed.installer/bin/XposedBridge.jar
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





























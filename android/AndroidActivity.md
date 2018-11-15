1 activity启动模式及应用场景：重点是问singleInstance的模式下是否新启动一个task，
别的应用启动这个activity在哪个task里以及跳转到别的页面如何跳转回来?

#####设置了"singleTask"启动模式的Activity的特点： 
1. 设置了"singleTask"启动模式的Activity，它在启动的时候，会先在系统中查找属性值affinity等于它的属性值 taskAffinity的任务存在;
如果存在这样的任务，它就会在这个任务中启动，否则就会在新任务中启动。
因此，如果我们想要设置了"singleTask"启动模式的Activity在新的任务中启动，就要为它设置一个独立的taskAffinity属性值。 
2. 如果设置了"singleTask"启动模式的Activity不是在新的任务中启动时，它会在已有的任务中查看是否已经存在相应的Activity实例，
如果存在，就会把位于这个Activity实例上面的Activity全部结束掉，即最终这个Activity实例会位于任务的堆栈顶端中。


























































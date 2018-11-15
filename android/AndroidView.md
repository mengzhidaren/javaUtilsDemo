#####ListView卡顿的原因以及优化策略
````$xslt
重用converView
减少findViewById()操作
避免在 getView 方法中做耗时的操作
Item的布局层次结构尽量简单，避免布局太深或者不必要的重绘
尽量能保证 Adapter 的 hasStableIds() 返回 true 这样在 notifyDataSetChanged() 的时候，如果item内容并没有变化，ListView 将不会重新绘制这个 View，达到优化的目的
ScollView内会包含多个ListView，可以把listview的高度写死固定下来
ListView 中元素避免半透明
尽量开启硬件加速
````
#####描述一下View的绘制原理？
````
描述一下View的绘制原理？
View的绘制流程主要分为三步：
onMeasure：测量视图的大小，从顶层父View到子View递归调用measure()方法，measure()调用onMeasure()方法，onMeasure()方法完成绘制工作。
onLayout：确定视图的位置，从顶层父View到子View递归调用layout()方法，父View将上一步measure()方法得到的子View的布局大小和布局参数，
将子View放在合适的位置上。
onDraw：绘制最终的视图，首先ViewRoot创建一个Canvas对象，然后调用onDraw()方法进行绘制。
onDraw()方法的绘制流程为
① 绘制视图背景。
② 绘制画布的图层。
③ 绘制View内容。
④ 绘制子视图，如果有的话。
⑤ 还原图层。
⑥ 绘制滚动条。
````
#####requestLayout()、invalidate()与postInvalidate()有什么区别？
````$xslt
requestLayout()：该方法会递归调用父窗口的requestLayout()方法，直到触发ViewRootImpl的performTraversals()方法，
此时mLayoutRequestede为true，会触发onMesaure()与onLayout()方法，不一定会触发onDraw()方法。
invalidate()：该方法递归调用父View的invalidateChildInParent()方法，直到调用ViewRootImpl的invalidateChildInParent()方法，
最终触发ViewRootImpl的performTraversals()方法，此时mLayoutRequestede为false，不会
触发onMesaure()与onLayout()方法，当时会触发onDraw()方法。

postInvalidate()：该方法功能和invalidate()一样，只是它可以在非UI线程中调用。
一般说来需要重新布局就调用requestLayout()方法，需要重新绘制就调用invalidate()方法。
````

#####描述一下Android的事件分发机制？
````$xslt
Android事件分发机制的本质：事件从哪个对象发出，经过哪些对象，最终由哪个对象处理了该事件。
此处对象指的是Activity、Window与View。
Android事件的分发顺序：Activity（Window） -> ViewGroup -> View
// 父View调用dispatchTouchEvent()开始分发事件
public boolean dispatchTouchEvent(MotionEvent event){
    boolean consume = false;
    // 父View决定是否拦截事件
    if(onInterceptTouchEvent(event)){
        // 父View调用onTouchEvent(event)消费事件，如果该方法返回true，表示
        // 该View消费了该事件，后续该事件序列的事件（Down、Move、Up）将不会在传递
        // 该其他View。
        consume = onTouchEvent(event);
    }else{
        // 调用子View的dispatchTouchEvent(event)方法继续分发事件
        consume = child.dispatchTouchEvent(event);
    }
    return consume;
}
````

#####SurfaceView和View的区别
SurfaceView中采用了双缓存技术，在单独的线程中更新界面
View在UI线程中更新界面

#####自定义View执行invalidate()方法,为什么有时候不会回调onDraw()
自定义一个view时，重写onDraw。调用view.invalidate(),会触发onDraw和computeScroll()。前提是该view被附加在当前窗口
view.postInvalidate(); //是在非UI线程上调用的
自定义一个ViewGroup，重写onDraw。onDraw可能不会被调用，原因是需要先设置一个背景(颜色或图)。
表示这个group有东西需要绘制了，才会触发draw，之后是onDraw。
因此，一般直接重写dispatchDraw来绘制viewGroup.自定义一个ViewGroup,dispatchDraw会调用drawChild.

####会对代码进行review吗？何时review？怎么review？
review?
其实就是代码再次查看评审。
什么时间该去Review？
在我们每一个小的版本迭代完后，就去Review代码。
怎么review？
下面是每次提交代码之前，可以参考的一份Review代码的清单，
事实证明这样做可以提高代码的质量和功能的稳定性。以下可能整理的不是很全

















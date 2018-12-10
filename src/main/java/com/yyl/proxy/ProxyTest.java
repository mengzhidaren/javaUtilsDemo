package com.yyl.proxy;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 代理的基本构成
 * 抽象角色：
 * 声明真实对象和代理对象的共同接口,这样可在任何使用真实对象的地方都可以使用代理对象。
 * 代理角色：
 * 代理对象内部含有真实对象的引用，从而可以在任何时候操作真实对象。代理对象提供一个与真实对象相同的接口，以便可以在任何时候替代真实对象。
 * 代理对象通常在客户端调用传递给真实对象之前或之后，执行某个操作，而不是单纯地将调用传递给真实对象，同时，代理对象可以在执行真实对象操作时，附加其他的操作，相当于对真实对象进行封装。
 * 真实角色：
 * 即为代理对象所代表的目标对象，代理角色所代表的真实对象，是我们最终要引用的对象。
 * <p></p>
 * 总结：如果一个类实现了一个接口那么就可以为这个类创建代理
 * java自带的proxy类可以创建动态类，代理对象提供一个与真实对象相同的接口
 */
public class ProxyTest {
    public static void main(String[] args) throws Exception {
        ArrayList target = new ArrayList<String>(); //目标类

        //通过自定义的getProxy方法，生成代理类对象，并添加切面代码。
        Collection collection = (Collection) getProxy(target, new MyAdvice());
        collection.add("aaaaaa");
        //结果：add running out of 0
//        target=(ArrayList)collection;
        target.add("ddddddd");

        System.out.println(target.toString());

    }

    //实现框架功能，生成代理只需要传递target目标类，和封装了系统功能的对象MyAdvice
    public static Object getProxy(final ArrayList target, final Advice advice) {

        Object proxy = Proxy.newProxyInstance(   //生成代理类对象
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {
                        advice.beforeAdvice(method); //切面代码
                        Object retVal = method.invoke(target, args);//调用目标类的方法
                        advice.afterAdvice(method);  //切面代码
                        return retVal;
                    }
                }
        );
        return proxy;
    }

    interface Advice {
        void beforeAdvice(Method method);

        void afterAdvice(Method method);
    }

    //封装了切面代码：
    static class MyAdvice implements Advice {
        private long beginTime = 0;

        public void afterAdvice(Method method) {
            long endTime = System.currentTimeMillis();
            System.out.println(method.getName() + " running out of =" + (endTime - beginTime));
        }

        public void beforeAdvice(Method method) {
            beginTime = System.currentTimeMillis();
        }
    }
}


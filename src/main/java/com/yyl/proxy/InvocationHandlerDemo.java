package com.yyl.proxy;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InvocationHandlerDemo {
    public static void main(String[] args) {
        LogHandler logHandler = new LogHandler();

        IHello helloProxy = (IHello) logHandler.bind(new HelloSpeaker());
        helloProxy.hello("Justin");
    }



     interface IHello {
        public void hello(String name);
    }
    static class HelloSpeaker implements IHello {


        public void hello(String name) {
            System.out.println("Hello, " + name);
        }

    }
    static  class LogHandler implements InvocationHandler {
        private Object delegate;

        private Logger logger = Logger.getLogger(this.getClass().getName());

        public Object bind(Object delegate) {
            this.delegate = delegate;
            return Proxy.newProxyInstance(delegate.getClass().getClassLoader(), delegate.getClass().getInterfaces(), this);
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = null;

            try {
                log("method starts..." + method);

                result = method.invoke(delegate, args);

                logger.log(Level.INFO, "method ends..." + method);
            } catch (Exception e) {
                log(e.toString());
            }

            return result;
        }

        private void log(String message) {
            logger.log(Level.INFO, message);
        }
    }

}

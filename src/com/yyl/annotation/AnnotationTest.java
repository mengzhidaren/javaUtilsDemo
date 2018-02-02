package com.yyl.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;


/**
 * 描述注解的保留到一个阶段(有效范围)
 * "@Retention"
 * RetentionPolicy
 * SOURCE	保留到源代码        (ide源代码)
 * CLASS	保留到Class字节码   (编译成.class文件)
 * RUNTIME	保留到运行时        (VM运行阶段)
 * <p>
 * <p>
 * <p>
 * <p>
 * "@Target"   说明在哪里使用该注解
 * ElementType
 * CONSTRUCTOR: 修饰构造器
 * FIELD:修饰域
 * LOCAL_VARIABLE:修饰局部变量
 * METHOD:修饰方法
 * PACKAGE:修饰包
 * PARAMETER:修饰参数
 * TYPE:修饰类、接口(包括注解类型) 或enum声明
 * <p>
 * <p>
 * <p>
 * /@Documented: 用于描述其它类型的Annotation应该被作为被标注的程序成员的公共API
 * <p>
 * <p>
 * "@Inherited" 阐述了某个被标注的类型是被继承的
 * <p>
 * -@Inherited 修饰的annotation类型被用于一个class，则这个annotation将被用于该class的子类。
 * 注意：@Inherited annotation类型是被标注过的class的子类所继承。类并不从它所实现的接口继承annotation，方法并不从它所重载的方法继承annotation。
 * 1.如果子类继承父类，并且重写了父类中的带有注解的方法，那么父类方法上的注解是不会被子类继承的。
 * 2.如果子类继承父类，但是没有重写父类中带有注解的方法，那么父类方法上的注解会被子类继承，就是说在子类中可以得到父类方法上的注解。
 */
@AnnotationTest.MyAnnotation(id = 12, name = "wgc", array = {1, 2, 3}, clazz = String.class, otherAnnotation = @AnnotationTest.OtherAnnotation(name = "other"))
public class AnnotationTest {

    @OtherAnnotation(name = "wgc")
    public void test() {

    }

    public static void main(String[] args) throws NoSuchMethodException {
        AnnotationTest annotationTest = new AnnotationTest();
        //isAnnotationPresent方法是在运行阶段判断的，只有注解保留到RUNTIME阶段才能发现
        if (annotationTest.getClass().isAnnotationPresent(MyAnnotation.class)) {
            System.out.println("Yes1");

            MyAnnotation myAnnotation = annotationTest.getClass().getAnnotation(MyAnnotation.class);
            System.out.println(myAnnotation.id());
            System.out.println(myAnnotation.array().length);
            //   System.out.println(myAnnotation.color());
            System.out.println(myAnnotation.clazz());
            System.out.println(myAnnotation.otherAnnotation());
        } else {
            System.out.println("No1");
        }

        Method method = annotationTest.getClass().getMethod("test", null);
        if (method.isAnnotationPresent(OtherAnnotation.class)) {
            System.out.println("yes2");
        } else {
            System.out.println("no2");
        }

        if (method.getClass().isAnnotationPresent(OtherAnnotation.class)) {
            System.out.println("yes3");
        } else {
            System.out.println("no3");
        }
    }


    @Retention(RetentionPolicy.RUNTIME)
    // @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.METHOD, ElementType.TYPE})//要想在类外使用注释 ElementType.TYPE
    public @interface MyAnnotation {
        String name() default "null";

        int id();//注解的属性

        int[] array();

        //   Color color();
        Class clazz();

        OtherAnnotation otherAnnotation();
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface OtherAnnotation {
        String name() default "null";


    }

}

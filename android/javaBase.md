#JAVA基础
#####equals与==的区别  

````
==是判断两个变量或实例是不是指向同一个内存空间 equals是判断两个变量或实例所指向的内存空间的值是不是相

1）对于==，比较的是值是否相等
            如果作用于基本数据类型的变量，则直接比较其存储的 “值”是否相等；
　　　　如果作用于引用类型的变量，则比较的是所指向的对象的地址
　　2）对于equals方法，注意：equals方法不能作用于基本数据类型的变量，equals继承Object类，比较的是是否是同一个对象
　　　　如果没有对equals方法进行重写，则比较的是引用类型的变量所指向的对象的地址；
　　　　诸如String、Date等类对equals方法进行了重写的话，比较的是所指向的对象的内容。
````
#####String、StringBuffer与StringBuilder的区别    
String 类型和 StringBuffer 类型的主要性能区别其实在于 String 是不可变的对象\
StringBuffer和StringBuilder底层是 char[]数组实现的\
StringBuffer是线程安全的，而StringBuilder是线程不安全的\
#####string 转换成 integer的方式及原理
````$xslt
integer.parseInt(string str)方法调用Integer内部的 
parseInt(string str,10)方法,默认基数为10，parseInt内部首先 
判断字符串是否包含符号（-或者+），则对相应的negative和limit进行 
赋值，然后再循环字符串，对单个char进行数值计算Character.digit(char ch, int radix) 
在这个方法中，函数肯定进入到0-9字符的判断（相对于string转换到int）， 
否则会抛出异常，数字就是如上面进行拼接然后生成的int类型数值。
````
#####泛型中extends和super的区别
````$xslt
? 通配符类型
<? extends T> 表示类型的上界，表示参数化类型的可能是T 或是 T的子类
<? super T> 表示类型下界（Java Core中叫超类型限定），表示参数化类型是此类型的超类型（父类型），直至Object
看了这个我是不太明白，换成白话是这个意思：
List<? extends T> 是说 这个list放的是T或者T的子类型的对象，但是不能确定具体是什么类型，所以可以get（），不能add（）（可以add null值）
List<? super T> 是说这个list放的是至少是T类型的对象，所以我可以add T或者T的子类型，但是get得到的类型不确定，所以不能get
````


#####闭包和局部内部类的区别
````$xslt
闭包（Closure）是一种能被调用的对象，它保存了创建它的作用域的信息。JAVA并不能显式地支持闭包，
但是在JAVA中，闭包可以通过“接口+内部类”来实现。
例如：一个接口程序员和一个基类作家都有一个相同的方法work，相同的方法名，但是其含义完全不同，这时候就需要闭包。

内部类的作用
内部类可以很好的实现隐藏。
一般的非内部类，是不允许有 private 与protected权限的，但内部类可以
内部类拥有外围类的所有元素的访问权限
可是实现多重继承
可以避免修改接口而实现同一个类中两种同名方法的调用。

````
#####谈谈对java多态的理解
````多态存在的三个必要条件：
    1、继承
    2、重写
    3、父类引用指向子类对象（向上转型）
    多态的优点
    消除类型之间的耦合关系
    可替换性
    可扩充性
    接口性
    灵活性
    简化性
````

#####静态代理和动态代理的区别，什么场景使用？

#####谈谈你对解析与分派的认识。

#####Java中实现多态的机制是什么

方法的重写Overriding和重载Overloading是Java多态性的不同表现.\
重写Overriding是父类与子类之间多态性的一种表现\
重载Overloading是一个类中多态性的一种表现.

#####Object类的equal和hashCode方法重写，为什么
重写了euqls方法的对象必须同时重写hashCode()方法。 \
两个对象的HashCode相同，不代表两个对象就相同，只能说明这两个对象在散列存储结构中，存放于同一个位置

#####你知道hash的实现吗？为什么要这样实现？
````$xslt
在Java 1.8的实现中，是通过hashCode()的高16位异或低16位实现的：(h = k.hashCode()) ^ (h >>> 16)，
主要是从速度、功效、质量来考虑的，这么做可以在bucket的n比较小的时候，
也能保证考虑到高低bit都参与到hash的计算中，同时不会有太大的开销。

hashCode注意点 
关于hashCode方法，一致的约定是： 
1、重写了euqls方法的对象必须同时重写hashCode()方法。 
2、如果两个对象equals相等，那么这两个对象的HashCode一定也相同 
3、如果两个对象的HashCode相同，不代表两个对象就相同，只能说明这两个对象在散列存储结构中，存放于同一个位置

hashCode作用 
从Object角度看，JVM每new一个Object，它都会将这个Object丢到一个Hash表中去，这样的话，下次做Object的比较或者取这个对象的时候（读取过程），它会根据对象的HashCode再从Hash表中取这个对象。这样做的目的是提高取对象的效率。若HashCode相同再去调用equal。 
HashCode是用于查找使用的，而equals是用于比较两个对象的是否相等的。
为什么重写 
实际开发的过程中在hashmap或者hashset里如果不重写的hashcode和equals方法的话会导致我们存对象的时候，把对象存进去了，取的时候却取不到想要的对象。 
重写了hashcode和equals方法可以迅速的在hashmap中找到键的位置； 
1、重写hashcode是为了保证相同的对象会有相同的hashcode； 
2、重写equals是为了保证在发生冲突的情况下取得到Entry对象（也可以理解是key或是元素）；
````
##### hashCode简介 
默认情况下，Object中的hashCode() 返回对象的32位jvm内存地址。也就是说如果对象不重写该方法，则返回相应对象的32为JVM内存地址。 



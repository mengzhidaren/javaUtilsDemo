package com.yyl.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 字节”的定义
 * 字节（Byte）是一种计量单位，表示数据量多少，它是计算机信息技术用于计量存储容量的一种计量单位
 * 字符”的定义
 * 字符是指计算机中使用的文字和符号，比如1、2、3、A、B、C、~！·#￥%……—*（）——+、等等。
 * “字节”与“字符” 它们完全不是一个位面的概念，所以两者之间没有“区别”这个说法。不同编码里，字符和字节的对应关系不同：
 * ASCII码中，一个英文字母（不分大小写）占一个字节的空间，一个中文汉字占两个字节的空间。一个二进制数字序列，在计算机中作为一个数字单元，一般为8位二进制数，换算为十进制。最小值0，最大值255。
 * UTF-8编码中，一个英文字符等于一个字节，一个中文（含繁体）等于三个字节
 * Unicode编码中，一个英文等于两个字节，一个中文（含繁体）等于两个字节。
 * <p>
 * 面试题
 * Java 中有几种类型的流？
 * 答：字节流，字符流。字节流继承于InputStream、OutputStream，字符流继承于Reader、Writer。在java.io 包中还有许多其他的流，主要是为了提高性能和使用方便。
 * <p>
 * 补充：关于Java的IO需要注意的有两点：
 * 一是两种对称性（输入和输出的对称性，字节和字符的对称性）；
 * 二是两种设计模式（适配器模式和装潢模式）。另外Java中的流不同于C#的是它只有一个维度一个方向。
 * <p>
 * 补充：下面用IO和NIO两种方式实现文件拷贝，这个题目在面试的时候是经常被问到的。
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * 什么时候使用字节流、什么时候使用字符流，二者的区别
 * 如果是音频文件、图片、歌曲，就用字节流好点，
 * 如果是关系到中文（文本）的，用字符流好点
 */
public class FileCopy_IO_NIO {


    public static void fileCopy(String source, String target) throws IOException {
        try (InputStream in = new FileInputStream(source)) {
            try (OutputStream out = new FileOutputStream(target)) {
                byte[] buffer = new byte[4096];
                int bytesToRead;
                while ((bytesToRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesToRead);
                }
            }
        }
    }

    public static void fileCopyNIO(String source, String target) throws IOException {
        try (FileInputStream in = new FileInputStream(source)) {
            try (FileOutputStream out = new FileOutputStream(target)) {
                FileChannel inChannel = in.getChannel();
                FileChannel outChannel = out.getChannel();
                ByteBuffer buffer = ByteBuffer.allocate(4096);
                while (inChannel.read(buffer) != -1) {
                    buffer.flip();
                    outChannel.write(buffer);
                    buffer.clear();
                }
            }
        }
    }
}

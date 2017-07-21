package com.yyl.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 输入流：把能够读取一个字节序列的对象称为输入流（百度百科）
 * ·输出流：把能够写一个字节序列的对象称为输出流（百度百科）
 * <p>
 * <p>
 * 2.字节流
 * （用字节流处理字符数据可能会有编码问题，因为字节流是以字节为单位，没有编码，而字符流是以字符为单位传送数据，字符流即以字节流+编码）
 * <p>
 * 两个顶层父类 （抽象类）及实现类
 * ·InputStream（读入内存）  ：所有字节输入流相关类的父类
 * ··FileInputStream ：obtain input bytes from a file in a file system,for reading streams of raw bytes(原始字节)
 * <p>                   such as image data..For writing streams of characters,consider using FileReader
 * ··ByteArrayInputStream:包含一个内置的缓冲器存储字节
 * <p>
 * <p>
 * <p>
 * ·OutputStream(写出内存)：所有和输出字节流相关类的父类
 * ··FileOutputStream：for writing data to a file or a FileDescriptor,for writing streams of raw data(原始字节)
 * <p>                   such as image data.For writing streams of characters,consider using FileWriter.初始化时要和文件关联，写出的目的地。没有该文件时会自动创建。
 * <p>
 * <p>
 * <p>
 * <p>
 * 3.字符流
 * ·两个顶层父抽象类及其实现类
 * ·Reader：for reading character streams
 * ··InputStreamReader:从字节流到字符流的桥梁：读取字节流然后按指定的编码方式进行解码（看不懂→能看懂）
 * ·FileReader:读字符文件的方便类，本质是InputStreamReader在构造时 指定了默认的编码方式，用于读取字符流
 * InputStreamReader 接收键盘上输入的数据，写入文件中（中文会乱码）
 * ··BufferedReader：从一个字符输入（character-input）流中读取文本（text），并进行缓冲字符,默认缓存8192（8M），行最长80
 * <p>
 * <p>
 * <p>
 * ·Writer:for writing to character streams （字符流的写操作基本上后面都需要进行flush()操作）
 * ··OutputStreamWriter :从字符流到字节流的桥梁：写出的字符被用指定的编码方式进行编码。
 */
public class FileTest {

    public static void main(String[] args) {
        File file = new File("d:/helloWorld.txt");
        InputStream in = null;
        try {
            if (!file.exists()) {                              //文件不存在则创建
                file.createNewFile();
            }
            in = new FileInputStream(file);
            byte[] buf = new byte[1024];                 //先写到缓存中，然后再一起写到其他外设中
            int length = 0;
            while ((length = in.read(buf)) != -1) {                //-1 represent the end of the file is reached    ，
                //字节一个一个地读入到内存
                System.out.println(new String(buf, 0, length)); //需要将int转为字节，如果为中文的话输出乱码字符  ，
                //此处其实是写出到了外设（控制台）上，System.out返回的是PrintStream对象
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

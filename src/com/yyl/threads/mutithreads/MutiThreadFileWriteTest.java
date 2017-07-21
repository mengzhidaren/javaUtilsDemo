package com.yyl.threads.mutithreads;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 测试利用多线程进行文件的写操作 
 */
public class MutiThreadFileWriteTest {

    public static void main(String[] args) throws Exception {
        // 预分配文件所占的磁盘空间，磁盘中会创建一个指定大小的文件
        File file = new File("D://abc.txt");
        if (file.exists()){
            file.delete();
            System.out.print("delete");
        }

        RandomAccessFile raf = new RandomAccessFile("D://abc.txt", "rw");
   //     raf.setLength(1024*1024); // 预分配 1M 的文件空间
   //     raf.close();

        // 所要写入的文件内容  
        String s1 = "第一个字符串";
        String s2 = "第二个字符串";
        String s3 = "第三个字符串";
        String s4 = "第四个字符串";
        String s5 = "第五个字符串";

        // 利用多线程同时写入一个文件  
        new FileWriteThread(1024*1,s1.getBytes()).start(); // 从文件的1024字节之后开始写入数据  
        new FileWriteThread(1024*2,s2.getBytes()).start(); // 从文件的2048字节之后开始写入数据  
        new FileWriteThread(1024*3,s3.getBytes()).start(); // 从文件的3072字节之后开始写入数据  
        new FileWriteThread(1024*4,s4.getBytes()).start(); // 从文件的4096字节之后开始写入数据  
        new FileWriteThread(1024*5,s5.getBytes()).start(); // 从文件的5120字节之后开始写入数据
        System.out.print("1 length="+raf.length());
        Thread.sleep(1000);

        System.out.print("2 length="+raf.length());
    }

    // 利用线程在文件的指定位置写入指定数据  
    static class FileWriteThread extends Thread{
        private int skip;
        private byte[] content;

        public FileWriteThread(int skip,byte[] content){
            this.skip = skip;
            this.content = content;
        }

        public void run(){
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile("D://abc.txt", "rw");
                raf.seek(skip);
                raf.write(content);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block  
                e.printStackTrace();
            } finally {
                try {
                    raf.close();
                } catch (Exception e) {
                }
            }
        }
    }

}  
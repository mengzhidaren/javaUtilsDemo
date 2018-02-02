package com.yyl.net.scoket;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 面试题22
 * TCP和UDP通信
 * 关于socket编程我们有两种通信协议可以进行选择。一种是数据报通信，另一种就是流通信。
 * <p>
 * 数据报通信
 * <p>
 * 数据报通信协议，就是我们常说的UDP（User Data Protocol 用户数据报协议）。UDP是一种无连接的协议，这就意味着我们每次发送数据报时，
 * 需要同时发送本机的socket描述符和接收端的socket描述符。因此，我们在每次通信时都需要发送额外的数据。
 * <p>
 * 流通信
 * <p>
 * 流通信协议，也叫做TCP(Transfer Control Protocol，传输控制协议)。和UDP不同，TCP是一种基于连接的协议。在使用流通信之前，
 * 我们必须在通信的一对儿socket之间建立连接。其中一个socket作为服务器进行监听连接请求。另一个则作为客户端进行连接请求。一旦两个socket建立好了连接，他们可以单向或双向进行数据传输。
 * <p>
 * <p>
 * UDP中，每次发送数据报时，需要附带上本机的socket描述符和接收端的socket描述符。
 * 而由于TCP是基于连接的协议，在通信的socket对之间需要在通信之前建立连接，因此会有建立连接这一耗时存在于TCP协议的socket编程。
 * <p>
 * 在UDP中，数据报数据在大小上有64KB的限制
 * UDP是一种不可靠的协议，发送的数据报不一定会按照其发送顺序被接收端的socket接受。
 * TCP是一种可靠的协议。接收端收到的包的顺序和包在发送端的顺序是一致的。
 * <p>
 * 简而言之，TCP适合于诸如远程登录(rlogin,telnet)和文件传输（FTP）这类的网络服务。因为这些需要传输的数据的大小不确定。
 * 而UDP相比TCP更加简单轻量一些。UDP用来实现实时性较高或者丢包不重要的一些服务。在局域网中UDP的丢包率都相对比较低。
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * 面试题
 * 23解析-Java Socket与TCP IP协议栈
 * 题目：Java Socket与TCP/IP协议栈的关系，为什么TCP连接需要三次握手与四次挥手？
 * <p>
 * TCP之所以采用三次握手建立连接的机制，是为了防止已失效的连接请求报文段突然又传送到了服务端，因而产生错误。在网络中不能确保数据包的一定可以发送成功，
 * 也不能确保数据包的发送顺序和到达顺序一致，三次握手机制避免了客户端与服务器之间在建立连接时可能因丢包而造成的一端 无法感知另一端状态的现象。
 * 关于TCP协议的四次挥手过程与原因与三次握手完全类同
 * <p>
 * TCP协议三次握手的具体过程
 * <><>百度</></>
 * <p>
 * <p>
 * <p>
 * <p>
 * TCP协议作为可靠传输协议，是所有协议中最常用的协议，什么？最常用的协议不是HTTP吗？HTTP协议只是TCP协议的应用协议而已。
 * TCP协议相关的开发难点在于服务器端的开发，需要考虑并发性能，本文以讲解了TCP的协议为主，因此只采用了BIO模式进行分析
 */
public class ScoketTest {

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(9999);
        Socket socket = ss.accept();
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        byte[] buffer = new byte[100];
        int length = is.read(buffer);
        String content = new String(buffer, 0, length);
        System.out.println("read from client:" + content);
        int strLength = content.length();
        String str = String.valueOf(strLength);
        os.write(str.getBytes());
        is.close();
        os.close();
        socket.close();
    }

    public static class ClientTest {
        public static void main(String[] args) throws Exception {
            Socket socket = new Socket("localhost", 9999);
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            String content = "This comes from client";
            os.write(content.getBytes());
            byte[] b = new byte[100];
            int length = is.read(b);
            String str = new String(b, 0, length);
            System.out.println("string's  length:" + str);
            is.close();
            os.close();
            socket.close();
        }

    }
}

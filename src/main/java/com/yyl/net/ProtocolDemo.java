package com.yyl.net;

import java.io.*;
import java.net.*;

/**
 * 1、两台计算机间进行通讯需要以下三个条件：
 * IP地址、协议、端口号
 * 2、TCP/IP协议：
 * 是目前世界上应用最为广泛的协议，是以TCP和IP为基础的不同层次上多个协议的集合，也成TCP/IP协议族、或TCP/IP协议栈
 * TCP：Transmission Control Protocol 传输控制协议
 * IP：Internet Protocol 互联网协议
 * 3、TCP/IP五层模型
 * 应用层：HTTP、FTP、SMTP、Telnet等
 * 传输层：TCP/IP
 * 网络层：
 * 数据链路层：
 * 物理层：网线、双绞线、网卡等
 * 4、IP地址
 * 为实现网络中不同计算机之间的通信，每台计算机都必须有一个唯一的标识---IP地址。
 * 32位二进制
 * 5、端口
 * 区分一台主机的多个不同应用程序，端口号范围为0-65535，其中0-1023位为系统保留。
 * 如：HTTP：80  FTP：21 Telnet：23
 * IP地址+端口号组成了所谓的Socket，Socket是网络上运行的程序之间双向通信链路的终结点，是TCP和UDP的基础
 * 6、Socket套接字：
 * 网络上具有唯一标识的IP地址和端口组合在一起才能构成唯一能识别的标识符套接字。
 * Socket原理机制：
 * 通信的两端都有Socket
 * 网络通信其实就是Socket间的通信
 * 数据在两个Socket间通过IO传输
 * 7、Java中的网络支持
 * 针对网络通信的不同层次，Java提供了不同的API，其提供的网络功能有四大类：
 * InetAddress:用于标识网络上的硬件资源，主要是IP地址
 * URL：统一资源定位符，通过URL可以直接读取或写入网络上的数据
 * Sockets：使用TCP协议实现的网络通信Socket相关的类
 * Datagram:使用UDP协议，将数据保存在用户数据报中，通过网络进行通信。
 */
public class ProtocolDemo {
transient float demo=1.1f;
    //InetAddress类用于标识网络上的硬件资源，标识互联网协议(IP)地址。
    public void test1() throws UnknownHostException {
        //获取本机的InetAddress实例
        InetAddress address = InetAddress.getLocalHost();
        address.getHostName();//获取计算机名
        address.getHostAddress();//获取IP地址
        byte[] bytes = address.getAddress();//获取字节数组形式的IP地址,以点分隔的四部分
        //获取其他主机的InetAddress实例
        InetAddress address2 = InetAddress.getByName("其他主机名");
        InetAddress address3 = InetAddress.getByName("IP地址");
    }

    //URL(Uniform Resource Locator)统一资源定位符，表示Internet上某一资源的地址，协议名：资源名称
    public void test2() throws MalformedURLException {
        //创建一个URL的实例
        URL baidu = new URL("http://www.baidu.com");
        URL url = new URL(baidu, "/index.html?username=tom#test");//？表示参数，#表示锚点
        url.getProtocol();//获取协议
        url.getHost();//获取主机
        url.getPort();//如果没有指定端口号，根据协议不同使用默认端口。此时getPort()方法的返回值为 -1
        url.getPath();//获取文件路径
        url.getFile();//文件名，包括文件路径+参数
        url.getRef();//相对路径，就是锚点，即#号后面的内容
        url.getQuery();//查询字符串，即参数
    }

    //使用URL读取网页内容
    //通过URL对象的openStream()方法可以得到指定资源的输入流，通过流能够读取或访问网页上的资源
    public void test22() throws Exception {
        //使用URL读取网页内容
        //创建一个URL实例
        URL url = new URL("http://www.baidu.com");
        InputStream is = url.openStream();//通过openStream方法获取资源的字节输入流
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");//将字节输入流转换为字符输入流,如果不指定编码，中文可能会出现乱码
        BufferedReader br = new BufferedReader(isr);//为字符输入流添加缓冲，提高读取效率
        String data = br.readLine();//读取数据
        while (data != null) {
            System.out.println(data);//输出数据
            data = br.readLine();
        }
        br.close();
        isr.close();
        is.close();
    }

    /**
     * TCP编程
     * <p>
     * *基于TCP协议的Socket通信，实现用户登录，服务端
     */
    public void test3() throws Exception {
        //1、创建一个服务器端Socket，即ServerSocket，指定绑定的端口，并监听此端口
        ServerSocket serverSocket = new ServerSocket(10086);//1024-65535的某个端口
        //2、调用accept()方法开始监听，等待客户端的连接
        Socket socket = serverSocket.accept();
        //3、获取输入流，并读取客户端信息
        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String info = null;
        while ((info = br.readLine()) != null) {
            System.out.println("我是服务器，客户端说：" + info);
        }
        socket.shutdownInput();//关闭输入流
        //4、获取输出流，响应客户端的请求
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        pw.write("欢迎您！");
        pw.flush();
        //5、关闭资源
        pw.close();
        os.close();
        br.close();
        isr.close();
        is.close();
        socket.close();
        serverSocket.close();
    }

    /**
     * TCP编程
     * <p>
     * *基于TCP协议的Socket通信，实现用户登录，客户端：
     */
    public void test33() throws Exception {
        //客户端
        //1、创建客户端Socket，指定服务器地址和端口
        Socket socket = new Socket("localhost", 10086);
        //2、获取输出流，向服务器端发送信息
        OutputStream os = socket.getOutputStream();//字节输出流
        PrintWriter pw = new PrintWriter(os);//将输出流包装成打印流
        pw.write("用户名：admin；密码：123");
        pw.flush();
        socket.shutdownOutput();
        //3、获取输入流，并读取服务器端的响应信息
        InputStream is = socket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String info = null;
        while ((info = br.readLine()) != null) {
            System.out.println("我是客户端，服务器说：" + info);
        }
        //4、关闭资源
        br.close();
        is.close();
        pw.close();
        os.close();
        socket.close();
    }

    /**
     * TCP编程
     * 应用多线程实现服务器与多客户端之间的通信
     */
    public void test333() throws Exception {
        //服务器代码
        ServerSocket serverSocket = new ServerSocket(10086);
        Socket socket = null;
        int count = 0;//记录客户端的数量
        while (true) {
            socket = serverSocket.accept();
            //  ServerThread serverThread = new ServerThread(socket);
            //   serverThread.start();
            count++;
            System.out.println("客户端连接的数量：" + count);
        }
    }

    /**
     * UDP编程
     * UDP协议（用户数据报协议）是无连接的、不可靠的、无序的,速度快
     * DatagramPacket类:表示数据报包
     * DatagramSocket类：进行端到端通信的类
     * 1、服务器端实现步骤
     * ① 创建DatagramSocket，指定端口号
     * ② 创建DatagramPacket
     * ③ 接受客户端发送的数据信息
     * ④ 读取数据
     */
    public void test4() throws Exception {
//服务器端，实现基于UDP的用户登录
        //1、创建服务器端DatagramSocket，指定端口
        DatagramSocket socket = new DatagramSocket(10010);
        //2、创建数据报，用于接受客户端发送的数据
        byte[] data = new byte[1024];//
        DatagramPacket packet = new DatagramPacket(data, data.length);
        //3、接受客户端发送的数据
        socket.receive(packet);//此方法在接受数据报之前会一致阻塞
        //4、读取数据
        String info = new String(data, 0, data.length);
        System.out.println("我是服务器，客户端告诉我" + info);
        //=========================================================
        //向客户端响应数据
        //1、定义客户端的地址、端口号、数据
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        byte[] data2 = "欢迎您！".getBytes();
        //2、创建数据报，包含响应的数据信息
        DatagramPacket packet2 = new DatagramPacket(data2, data2.length, address, port);
        //3、响应客户端
        socket.send(packet2);
        //4、关闭资源
        socket.close();
    }

    /**
     * UDP编程
     * 2、客户端实现步骤
     * ① 定义发送信息
     * ② 创建DatagramPacket，包含将要发送的信息
     * ③ 创建DatagramSocket
     * ④ 发送数据
     */
    public void test44() throws Exception {

        //客户端
        //1、定义服务器的地址、端口号、数据
        InetAddress address = InetAddress.getByName("localhost");
        int port = 10010;
        byte[] data = "用户名：admin;密码：123".getBytes();
        //2、创建数据报，包含发送的数据信息
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        //3、创建DatagramSocket对象
        DatagramSocket socket = new DatagramSocket();
        //4、向服务器发送数据
        socket.send(packet);
        //接受服务器端响应数据
        //======================================
        //1、创建数据报，用于接受服务器端响应数据
        byte[] data2 = new byte[1024];
        DatagramPacket packet2 = new DatagramPacket(data2, data2.length);
        //2、接受服务器响应的数据
        socket.receive(packet2);
        String raply = new String(data2, 0, packet2.getLength());
        System.out.println("我是客户端，服务器说：" + raply);
        //4、关闭资源
        socket.close();
    }

    //1、多线程的优先级问题：
//    根据实际的经验，适当的降低优先级，否侧可能会有程序运行效率低的情况
//    2、是否关闭输出流和输入流：
//    对于同一个socket，如果关闭了输出流，则与该输出流关联的socket也会被关闭，所以一般不用关闭流，直接关闭socket即可
//     3、使用TCP通信传输对象，IO中序列化部分
//     4、socket编程传递文件，IO流部分
}

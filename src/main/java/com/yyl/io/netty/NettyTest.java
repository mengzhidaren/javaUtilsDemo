package com.yyl.io.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * IO通信对比：
 * 客户端个数：
 * BIO   1:1
 * 伪异步IO  M:N
 * NIO  M:1
 * MIO  M:0
 * <p>
 * 类型：
 * BIO 阻塞同步
 * 伪异步IO 阻塞同步
 * NIO 非阻塞同步
 * AIO 非阻塞异步
 * <p>
 * 使用难度：BIO<伪异步IO<AIO<NIO
 * 调试难度：BIO<伪异步IO<NIO 约= AIO
 * 可靠性：BIO<伪异步IO<NIO 约= AIO
 * 吞吐量：BIO<伪异步IO<NIO 约= AIO
 * <p>
 * <p>
 * <p>
 * netty优势：API简单，性能高，入门门槛低，成熟稳健，修复了很多原生NIO的bug
 */
public class NettyTest {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new MyWebSocketChannelHandler());
            System.out.println("服务端开启等待客户端连接....");
            Channel ch = b.bind(8888).sync().channel();
            ch.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //优雅的退出程序
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }


}

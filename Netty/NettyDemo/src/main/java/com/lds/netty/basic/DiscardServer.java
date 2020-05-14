package com.lds.netty.basic;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class DiscardServer {

    private int port;
    public DiscardServer(int port) {
        this.port = port;
    }

    public void run()throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    //有连接到达时会创建通道
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //向子通道流水线添加Handler
                        ch.pipeline().addLast(new TimeDecoder1(),new TimeServerHandler());
                    }
                })
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            //sync()通过同步阻塞直到绑定成功
            ChannelFuture f = b.bind(port).sync();
            //等待直到server socket被关闭
            //这个例子，这个动作没发生，但是你可以优雅的shut down 服务
            f.channel().closeFuture().sync();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();

        }
    }

    public static void main(String[] args) throws Exception {
        int port = 12345;
        new DiscardServer(port).run();
    }
}

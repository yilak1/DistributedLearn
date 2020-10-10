package com.lds.netty.echoServer;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Scanner;

public class NettyEchoClient {

    private int serverPort;
    private String serverIp;
    Bootstrap b = new Bootstrap();

    public NettyEchoClient(int serverPort, String serverIp) {
        this.serverPort = serverPort;
        this.serverIp = serverIp;
    }

    public void runClient(){
        //创建reactor 线程组
        EventLoopGroup workerLoopGroup = new NioEventLoopGroup();
        try{

            b.group(workerLoopGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(serverIp, serverPort)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(NettyEchoClientHandler.INSTANCE);
                        }
                    });
            ChannelFuture channelFuture = b.connect();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()){
                        System.out.println("客户端连接成功");
                    }else {
                        System.out.println("客户端连接失败");
                    }
                }
            });
            channelFuture.sync();
            Channel channel = channelFuture.channel();
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                //获取输入的内容
                String next = scanner.next();
                byte[] bytes = (new Date() + " >>" + next).getBytes("UTF-8");
                //发送ByteBuf
                ByteBuf buffer = channel.alloc().buffer();
                buffer.writeBytes(bytes);
                channel.writeAndFlush(buffer);

            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            workerLoopGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        int port = 12345;
        String ip = "127.0.0.1";
        new NettyEchoClient(port, ip).runClient();
    }
}

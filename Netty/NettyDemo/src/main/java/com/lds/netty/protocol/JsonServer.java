package com.lds.netty.protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import jdk.nashorn.internal.runtime.linker.Bootstrap;

public class JsonServer {

    public void runServer(){

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {

            ServerBootstrap sb = new ServerBootstrap();
            sb.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(12345)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));
                                ch.pipeline().addLast(new StringDecoder());
                                ch.pipeline().addLast(new JsonMsgDecoder());
                        }
                    });
            sb.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            sb.option(ChannelOption.SO_KEEPALIVE, true);
            sb.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            // 6 开始绑定server
            // 通过调用sync同步方法阻塞直到绑定成功
            ChannelFuture sync = sb.bind().sync();
            // 7 等待通道关闭的异步任务结束
            // 服务监听通道会一直等待通道关闭的异步任务结束
            ChannelFuture channelFuture = sync.channel().closeFuture();
            channelFuture.sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }

    static class JsonMsgDecoder extends ChannelInboundHandlerAdapter{
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            String json = (String) msg;
            JsonMsg jsonMsg = JsonMsg.parseFromJson(json);
            System.out.println(jsonMsg);
        }
    }

    public static void main(String[] args) {
        new JsonServer().runServer();
    }
}

package com.lds.netty.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

public class JsonClient {
    static String content = "疯狂创客圈：高性能学习社群!";
    public void runClient(){
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        try {
            b.group(worker)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress("127.0.0.1", 12345))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldPrepender(4));
                            ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
                        }
                    });
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            ChannelFuture future = b.connect();
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("客户端连接成功");
                    }else {
                        System.out.println("客户端连接失败");
                    }
                }
            });

            // 阻塞,直到连接完成
            future.sync();
            Channel channel = future.channel();
            //发送 Json 字符串对象
            for (int i = 0; i < 1000; i++) {
                JsonMsg user = build(i, i+ "->" + content);
                channel.writeAndFlush(user.convertToJson());
                System.out.println(user.convertToJson());
            }
            channel.flush();
            // 7 等待通道关闭的异步任务结束
            // 服务监听通道会一直等待通道关闭的异步任务结束
            ChannelFuture closeFuture = channel.closeFuture();
            closeFuture.sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            worker.shutdownGracefully();
        }
    }

    //构建Json对象
    public JsonMsg build(int id, String content) {
        JsonMsg user = new JsonMsg();
        user.setId(id);
        user.setContent(content);
        return user;
    }

    public static void main(String[] args) {
        new JsonClient().runClient();
    }
}

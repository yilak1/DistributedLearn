package com.lds.netty.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class ProtoBufClient {

    private static String content = "我是柳敦盛123！！";

    public void runClient(){
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        try{
            b.group(worker)
                    .remoteAddress("127.0.0.1", 12345)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            ch.pipeline().addLast(new ProtobufEncoder());
                        }
                    });
            ChannelFuture connect = b.connect().sync();
            connect.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("客户端连接成功");
                    }else {
                        System.out.println("客户端连接失败");
                    }
                }
            });
            Channel channel = connect.channel();

            //发送 Protobuf 对象
            for (int  i = 0; i < 1000; i++) {
                MsgProtos.Msg user = build(i, i + "->" + content);
                channel.writeAndFlush(user);
                System.out.println("发送报文数：" + i);
            }
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            worker.shutdownGracefully();
        }

    }

    //构建ProtoBuf对象
    public MsgProtos.Msg build(int id, String content) {
        MsgProtos.Msg.Builder builder = MsgProtos.Msg.newBuilder();
        builder.setId(id);
        builder.setContent(content);
        return builder.build();
    }

    public static void main(String[] args) {
        new ProtoBufClient().runClient();
    }
}

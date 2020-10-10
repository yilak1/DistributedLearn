package com.lds.netty.protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;

public class ProtoBufServer {

    public void runServer(){
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(boss, worker)
                    .localAddress(12345)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            //需要提供一个POJO默认实例，会根据这个实例找到对应的解析器
                            ch.pipeline().addLast(new ProtobufDecoder(MsgProtos.Msg.getDefaultInstance()));
                            ch.pipeline().addLast(new ProtobufBussinessDecoder());

                        }
                    });

            ChannelFuture channelFuture = b.bind().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }


    }

    static class ProtobufBussinessDecoder extends ChannelInboundHandlerAdapter{
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            MsgProtos.Msg protoMsg = (MsgProtos.Msg) msg;
            //经过pipeline的各个decoder，到此Person类型已经可以断定
            System.out.println("收到一个 MsgProtos.Msg 数据包 =》");
            System.out.println("protoMsg.getId():=" + protoMsg.getId());
            System.out.println("protoMsg.getContent():=" + protoMsg.getContent());
        }
    }
    public static void main(String[] args) {
        new ProtoBufServer().runServer();
    }
}

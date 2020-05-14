package com.lds.netty.basic;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //不需要释放msg因为write的最后会释放msg
        ctx.write(msg);
        ctx.flush();
    }
}

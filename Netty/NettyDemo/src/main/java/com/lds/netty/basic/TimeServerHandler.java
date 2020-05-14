package com.lds.netty.basic;

import com.lds.pojo.UnixTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
//        final ByteBuf time = ctx.alloc().buffer(4);
//        time.writeInt((int)(System.currentTimeMillis()/1000L+ 2208988800L));
//        final ChannelFuture f = ctx.writeAndFlush(time);
//        f.addListener(new ChannelFutureListener() {
//            public void operationComplete(ChannelFuture future) throws Exception {
//                    assert f == future;
//                    ctx.close();
//            }
//        });
        ChannelFuture f = ctx.writeAndFlush(new UnixTime());
        f.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

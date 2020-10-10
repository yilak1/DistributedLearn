package com.lds.netty.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * ReplayingDecoder在读取数据之前会检查缓冲区是否有足够的字节，没有则停止解码
 */
public class Byte2IntegerReplayDecoder extends ReplayingDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int i = in.readInt();
        System.out.println("解码出一个整数: " + i);
        out.add(i);
    }
}

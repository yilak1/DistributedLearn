package com.lds.netty.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 解码器用于将byte转换成int 这个ByteBuf 由ByteToMessageDecoder release，不需要我们释放
 */
public class Byte2IntegerDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= 4) {
            int i = in.readInt();
            System.out.println("解码一个整数" + i);
            out.add(i);
        }
    }
}

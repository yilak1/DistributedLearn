package com.lds.netty.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class StringHeaderDecoder extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        //可读大小小于int，头还没读满，return
        if (in.readableBytes() < 4) {
            return;
        }

        //头已经完整
        //在真正开始从buffer读取数据之前，调用markReaderIndex()设置回滚点
        // 回滚点为 header的readIndex位置
        in.markReaderIndex();
        int length =in.readInt();
        //从buffer中读出头的大小，这会使得readIndex前移
        //剩余长度不够body体，reset 读指针
        if (in.readableBytes() < length) {
            //读指针回滚到header的readIndex位置处，没进行状态的保存
            in.resetReaderIndex();
            return;
        }
        // 读取数据，编码成字符串
        byte[] inBytes = new byte[length];
        in.readBytes(inBytes, 0, length);
        out.add(new String(inBytes, "UTF-8"));
    }
}

package com.lds.netty.encoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class String2IntegerEncoder extends MessageToMessageEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        char[] chars = msg.toCharArray();
        for (char c : chars){
            //48 是0的编码，57 是9 的编码
            if (c >=48 && c <=57){
                out.add(new Integer(c - '0'));
            }
        }
    }
}

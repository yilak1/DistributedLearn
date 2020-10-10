package com.lds.netty.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

public class Integer2ByteEncoderTester {

    @Test
    public void testIntegerToByteDecoder(){
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel >() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new Integer2ByteEncoder());
            }
        });

        for(int i = 0; i < 100; i++) {
            embeddedChannel.write(i);
        }
        embeddedChannel.flush();
        //取得通道的出站数据帧
        ByteBuf buf = embeddedChannel.readOutbound();
        while (null !=  buf) {
            System.out.println("o = " + buf.readInt());
            buf = embeddedChannel.readOutbound();
        }

        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

package com.lds.netty.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

public class String2IntegerEncoderTester {

    @Test
    public void testString2IntegerEncoder(){
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new Integer2ByteEncoder());
                ch.pipeline().addLast(new String2IntegerEncoder());
            }
        });

        for (int j = 0; j < 100; j++) {
            String s = "i am " + j;
            embeddedChannel.write(s);
        }

        embeddedChannel.flush();
        ByteBuf buf = embeddedChannel.readOutbound();
        while (null != buf) {
            System.out.println("o = " + buf.readInt());
            buf = (ByteBuf) embeddedChannel.readOutbound();
        }
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

package com.lds.netty.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

/**
 * 用来测试IntegerAddDecoder类
 */
public class IntegerAddDecoderTest {

    @Test
    public void test(){
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new IntegerAddDecoder());
                ch.pipeline().addLast(new IntegerProcessHandler());
            }
        });

        for (int i = 0; i < 100; i++) {
            ByteBuf buf = Unpooled.buffer();
            buf.writeInt(i);
            embeddedChannel.writeInbound(buf);
        }
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package com.lds.netty.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Random;

/**
 * 用于测试StringReplayDecoder ，StringReplayDecoder 两个字符串解包
 */
public class StringReplayDecoderTester {

    static String content = "疯狂创客圈：高性能学习社群!";
    @Test
    public void test(){
        EmbeddedChannel embeddedChannel =  new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {

                ch.pipeline().addLast(new StringReplayDecoder());
//                ch.pipeline().addLast(new StringReplayDecoder());
                ch.pipeline().addLast(new StringProcessHandler());
            }
        });

        byte[] bytes = content.getBytes(Charset.forName("utf-8"));
        for (int i = 0; i < 100; i++) {
            //1-3之间的随机数
            int random = new Random().nextInt(2) + 1;
            ByteBuf buf = Unpooled.buffer();
            buf.writeInt(bytes.length * random);
            for (int j = 0; j < random; j++) {
                buf.writeBytes(bytes);
            }
            embeddedChannel.writeInbound(buf);
        }

        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package com.lds.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

public class InHandlerDemoTest {

    @Test
    public void testInHandlerLifeCircle(){
        final InHandlerDemo inHandlerDemo = new InHandlerDemo();
        //初始化处理器
        ChannelInitializer i = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(inHandlerDemo);
            }
        };
        //创建嵌入式通道
        EmbeddedChannel channel = new EmbeddedChannel(i);
        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(1);
        //模拟入站，写入一个入站包
        channel.writeInbound(buf);
        channel.flush();
        //模拟入站，再写入一个入站包
        channel.writeInbound(buf);
        channel.flush();
        //通道关闭
        channel.close();
        try {
            Thread.sleep(Integer.MAX_VALUE);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }
}

package com.lds.netty.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Random;

/**
 * netty内置的Decoder
 */
public class NettyOpenBoxDecoder {

    public static final int MAGICCODE = 9999;
    public static final int VERSION = 100;
    static String spliter = "\r\n";
    static String spliter2 = "\t";
    static String content = "疯狂创客圈：高性能学习社群!";
    /**
     * LineBasedFrameDecoder 使用实例分割换行符
     */
    @Test
    public void testLineBasedFrameDecoder() {
        try {
            EmbeddedChannel embeddedChannel = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {
                @Override
                protected void initChannel(EmbeddedChannel ch) throws Exception {
                    ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                    ch.pipeline().addLast(new StringDecoder());
                    ch.pipeline().addLast(new StringProcessHandler());
                }
            });
            for (int i = 0; i < 100; i++) {
                int random = new Random().nextInt(2) + 1;
                ByteBuf buf = Unpooled.buffer();
                for (int k = 0; k <  random; k++) {
                    buf.writeBytes(content.getBytes("UTF-8"));
                }
                buf.writeBytes(spliter.getBytes("UTF-8"));
                embeddedChannel.writeInbound(buf);
            }
            Thread.sleep(Integer.MAX_VALUE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * DelimiterBasedFrameDecoder :用于分割任意定界符
     */
    @Test
    public void testDelimiterBasedFrameDecoder(){
        try {
            final ByteBuf delimiter = Unpooled.copiedBuffer(spliter2.getBytes("UTF-8"));
            EmbeddedChannel embeddedChannel = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {
                @Override
                protected void initChannel(EmbeddedChannel ch) throws Exception {
                    ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, true, delimiter));
                    ch.pipeline().addLast(new StringDecoder());
                    ch.pipeline().addLast(new StringProcessHandler());
                }
            });

            for (int i = 0; i < 100; i++) {
                int random = new Random().nextInt(2) + 1;
                ByteBuf buf = Unpooled.buffer();
                for (int k = 0; k <  random; k++) {
                    buf.writeBytes(content.getBytes("UTF-8"));
                }
                buf.writeBytes(spliter2.getBytes("UTF-8"));
                embeddedChannel.writeInbound(buf);
            }
            Thread.sleep(Integer.MAX_VALUE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * LengthFieldBasedFrameDecoder:
     *                int maxFrameLength,         //发送的数据包最大长度
     *                 int lengthFieldOffset,     //长度字段偏移量
     *                 int lengthFieldLength,     //长度字段自己占用的字节数
     *                 int lengthAdjustment,      //长度字段的偏移量矫正
     *                 int initialBytesToStrip)  //丢弃的起始字节数
     */
    @Test
    public void testLengthFieldBasedFrameDecoder1(){
        try {
            final LengthFieldBasedFrameDecoder spliter =
                    new LengthFieldBasedFrameDecoder(1024, 0,4, 0, 4);
            ChannelInitializer i = new ChannelInitializer<EmbeddedChannel>() {
                protected void initChannel(EmbeddedChannel ch) {
                    ch.pipeline().addLast(spliter);
                    ch.pipeline().addLast(new StringDecoder(Charset.forName("UTF-8")));
                    ch.pipeline().addLast(new StringProcessHandler());
                }
            };
            EmbeddedChannel channel = new EmbeddedChannel(i);

            for (int j = 1; j <= 100; j++) {
                ByteBuf buf = Unpooled.buffer();
                String s = j + "次发送->" + content;
                byte[] bytes = s.getBytes("UTF-8");
                buf.writeInt(bytes.length);
                System.out.println("bytes length = " + bytes.length);
                buf.writeBytes(bytes);
                channel.writeInbound(buf);
            }

            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}

package com.lds.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.junit.Test;

import java.nio.charset.Charset;

/**
 * 测试直接内存和堆内存 directBuffer 和buffer分配的是直接内存， heapBuffer分配的是堆内存
 * 堆内存可以直接用array操作，直接内存需要用getBytes
 **/
public class BufferTypeTest {
   final static Charset UTF_8 = Charset.forName("UTF-8");

    //堆缓冲区
    @Test
    public  void testHeapBuffer() {
       //取得堆内存
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        byteBuf.writeBytes("你好啊".getBytes(UTF_8));
        if (byteBuf.hasArray()) {
            byte[] array = byteBuf.array();
            int offset = byteBuf.arrayOffset() + byteBuf.readerIndex();
            int length = byteBuf.readableBytes();
            System.out.println(new String(array,offset,length));
        }
        byteBuf.release();
    }

    //直接缓冲区
    @Test
    public  void testDirectBuffer() {
        ByteBuf directBuf =  ByteBufAllocator.DEFAULT.directBuffer();  //也是直接内存
        directBuf.writeBytes("疯狂创客圈:高性能学习社群".getBytes(UTF_8));
        if (!directBuf.hasArray()) {
            int length = directBuf.readableBytes();
            byte[] array = new byte[length];
            //读取数据到堆内存
            directBuf.getBytes(directBuf.readerIndex(), array);
            System.out.println(new String(array, UTF_8));
        }
        directBuf.release();
    }
}

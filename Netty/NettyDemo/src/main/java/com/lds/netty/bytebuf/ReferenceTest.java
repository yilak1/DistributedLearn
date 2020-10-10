package com.lds.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.junit.Test;

/**
 * 用于测试ByteBuf引用计数垃圾收集
 * 创建buffer计数为1,retain计数加1，计数为0再调用release会抛出异常。
 **/
public class ReferenceTest {

    @Test
    public  void testRef()
    {

        ByteBuf buffer  = ByteBufAllocator.DEFAULT.buffer();
        System.out.println("after create:"+buffer.refCnt());
        buffer.retain();
        System.out.println("after retain:"+buffer.refCnt());
        buffer.release();
        System.out.println("after release:"+buffer.refCnt());
        buffer.release();
        System.out.println("after release:"+buffer.refCnt());
        //错误:refCnt: 0,不能再retain
        buffer.retain();
        System.out.println("after retain:"+buffer.refCnt());
    }
}

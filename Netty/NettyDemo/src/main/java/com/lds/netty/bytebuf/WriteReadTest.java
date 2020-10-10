package com.lds.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * ByteBuf 读写测试，
 */
public class WriteReadTest {
    public static void main(String[] args) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(9, 100);
        System.out.println(byteBuf);
        byteBuf.writeBytes(new byte[]{1, 2, 3, 4});
        System.out.println(byteBuf);
        getByteBuf(byteBuf);
        System.out.println(byteBuf);
        readByBuf(byteBuf);
        System.out.println(byteBuf);

    }

    //读取一个字节
    private static void readByBuf(ByteBuf buffer) {
        while (buffer.isReadable()) {
            System.out.println(buffer.readByte());
        }
    }

    //读取一个字节，不改变指针
    private static void getByteBuf(ByteBuf buffer) {
        for (int  i = 0; i < buffer.readableBytes(); i++) {
            System.out.println(buffer.getByte(i));
        }
    }
}

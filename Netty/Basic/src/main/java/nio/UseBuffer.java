package nio;

import org.junit.Test;

import java.nio.IntBuffer;
import java.util.logging.Logger;

public class UseBuffer {
    IntBuffer intBuffer = null;
    @Test
    //分配容量
    public void allocateTest(){
        intBuffer = IntBuffer.allocate(20);
        System.out.println(intBuffer.position());
        System.out.println(intBuffer.limit());
        System.out.println(intBuffer.capacity());
    }
    @Test
    //写缓冲区
    public void  putTest(){
        intBuffer = IntBuffer.allocate(10);
        for (int i = 0; i < 5; i++) {
            intBuffer.put(i);
        }
        System.out.println(intBuffer.position());
        System.out.println(intBuffer.limit());
        System.out.println(intBuffer.capacity());
    }
    @Test
    //读模式
    public void flipTest(){
        intBuffer = IntBuffer.allocate(10);
        for (int i = 0; i < 5; i++) {
            intBuffer.put(i);
        }
        intBuffer.flip();//mark = -1;
        System.out.println(intBuffer.position());
        System.out.println(intBuffer.limit());
        System.out.println(intBuffer.capacity());

    }
    @Test
    //读取数据 get() rewind()
    public void getTest(){
        intBuffer = IntBuffer.allocate(10);
        for (int i = 0; i < 5; i++) {
            intBuffer.put(i);
        }
        intBuffer.flip();//mark = -1;
        for (int i = 0; i < 2; i++) {
            System.out.print(intBuffer.get());
        }
        System.out.println();
        System.out.println(intBuffer.position());
        System.out.println(intBuffer.limit());
        System.out.println(intBuffer.capacity());
        for (int i = 0; i < 2; i++) {
            System.out.println(intBuffer.get());
        }
        System.out.println();
        System.out.println(intBuffer.position());
        System.out.println(intBuffer.limit());
        System.out.println(intBuffer.capacity());
        intBuffer.rewind();
        for (int i = 0; i < 2; i++) {
            System.out.println(intBuffer.get());
        }
        System.out.println();
        System.out.println(intBuffer.position());
        System.out.println(intBuffer.limit());
        System.out.println(intBuffer.capacity());
    }

    @Test
    public void afterReset(){
        intBuffer = IntBuffer.allocate(10);
        for (int i = 0; i < 5; i++) {
            intBuffer.put(i);
        }
        intBuffer.flip();//mark = -1;
        for (int i = 0; i < 5; i++) {
            if (i == 2) {
                intBuffer.mark();
            }
            System.out.println(intBuffer.get());

        }
        intBuffer.reset();
        System.out.println("position:" + intBuffer.position());
        System.out.println("limit:" + intBuffer.limit());
        System.out.println("capcity:" + intBuffer.capacity());
        for (int i =2; i < 5; i++) {
            System.out.println(intBuffer.get());
        }

    }
}

package nio;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class UseFileChannel {
    //@Test
    //获得通道方式
    public void getChannelTest() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream("/home/lds");
        FileChannel channel1 = fis.getChannel();
        FileOutputStream fout = new FileOutputStream("/home/lds");
        FileChannel channel2 = fout.getChannel();
        RandomAccessFile file = new RandomAccessFile("file.txt", "rw");
        FileChannel channel = file.getChannel();
    }
    //@Test
    //读数据示例
    public void readChannelTest() throws IOException {
        RandomAccessFile file = new RandomAccessFile("file.txt", "rw");
        FileChannel channel = file.getChannel();
        ByteBuffer buff = ByteBuffer.allocate(20);
        int length = -1;
        while ((length = channel.read(buff)) != -1) {

        }
    }
    @Test
    //使用管道复制文件
    public void copyFile(){
        String sourcePath = "/home/lds/a.cpp";
        String destPath = "/home/lds/b.cpp";
        try(FileInputStream fis = new FileInputStream(sourcePath);
            FileChannel channel1 = fis.getChannel();
            FileOutputStream fos = new FileOutputStream(destPath);
            FileChannel channel2 = fos.getChannel();){
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int length = -1;
            while ((length = channel1.read(buffer)) != -1){
                System.out.println("读取到的length" + length);
                buffer.flip();
                int outLength = 0;
                while ((outLength = channel2.write(buffer)) != 0) {
                    System.out.println("写入的字节数" + outLength);
                }
                buffer.clear();
            }
            channel2.force(true);
        }catch (IOException e) {
        }
    }

    @Test
    public void fastCopyTest(){
        String sourcePath = "/home/lds/a.cpp";
        String destPath = "/home/lds/b.cpp";
        try(FileInputStream fis = new FileInputStream(sourcePath);
            FileChannel inChannel = fis.getChannel();
            FileOutputStream fos = new FileOutputStream(destPath);
            FileChannel outChannel = fos.getChannel();){
            long size = inChannel.size();
            long pos = 0;
            long count = 0;
            while (pos < size) {
                //每次复制最多1024个字节，没有就复制剩余的
                count = size - pos > 1024?  1024:size - pos;
                //复制内存，偏移量pos+count长度
                pos+= outChannel.transferFrom(inChannel,pos, count);
            }
            outChannel.force(true);

        }catch (IOException e) {
        }
    }
}

package nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class NioSendClient {
    private Charset charset = Charset.forName("UTF-8");
    /**
     * 向服务器传送数据
     */
    public void sendFile(){
        String sourcePath = "/home/lds/a.cpp";
        String destPath = "/home/lds/b.cpp";
        try(
                FileChannel fic = new FileInputStream(sourcePath).getChannel();

        ){
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.socket().connect(new InetSocketAddress("127.0.0.1", 12345));
            socketChannel.configureBlocking(false);
            //没有连接则自旋
            while (!socketChannel.finishConnect()){

            }
            //发送文件名称
            ByteBuffer fileNameByteBuffer = charset.encode("b.cpp");
            socketChannel.write(fileNameByteBuffer);

            //发送文件长度
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.putLong(new File(sourcePath).length());
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();

            //发送文件内容
            int length = -1;
            long progress = 0;
            while ((length = fic.read(buffer)) != -1) {
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();
                progress += length;
            }
            if (length == -1) {
                socketChannel.shutdownOutput();
                if (socketChannel != null) {
                    socketChannel.close();
                }
            }
            System.out.println(progress);

        }catch (IOException e) {

        }

    }

    public static void main(String[] args) {
        new NioSendClient().sendFile();
    }
}

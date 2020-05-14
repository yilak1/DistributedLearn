package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NioDiscardClient {

    public void send() throws IOException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 12345));
        socketChannel.configureBlocking(false);
        //socketChannel.connect();
        //没有连接成功则自旋
        while (!socketChannel.finishConnect()){

        }

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put("hello world".getBytes());
        buffer.flip();
        socketChannel.write(buffer);
        socketChannel.shutdownOutput();
        socketChannel.close();
    }

    public static void main(String[] args) throws IOException {
        new NioDiscardClient().send();
    }
}

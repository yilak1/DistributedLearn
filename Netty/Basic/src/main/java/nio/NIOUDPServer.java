package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class NIOUDPServer {
    public void receive() throws IOException {
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
        datagramChannel.bind(new InetSocketAddress("127.0.0.1", 12345));
        Selector selector = Selector.open();
        datagramChannel.register(selector, SelectionKey.OP_READ);
        //通过选择器轮寻IO事件
        while (selector.select() > 0) {
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                //可读事件
                if (selectionKey.isReadable()) {
                    SocketAddress client = datagramChannel.receive(buffer);
                    buffer.flip();
                    System.out.println(new String(buffer.array(), 0, buffer.limit()));
                    buffer.clear();
                }
            }
            iterator.remove();

        }
        selector.close();
        datagramChannel.close();
    }

    public static void main(String[] args) throws IOException {
        new NIOUDPServer().receive();
    }
}

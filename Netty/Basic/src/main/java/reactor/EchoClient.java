package reactor;

import util.DateUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class EchoClient {

    public void start() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 12345));
        while (!socketChannel.finishConnect()){

        }
        System.out.println("连接成功");
        Processer processer = new Processer(socketChannel);
        new Thread(processer).start();
    }
    static class Processer implements Runnable{

        Selector selector;
        SocketChannel socketChannel;
        public Processer(SocketChannel socketChannel) throws IOException {
            this.socketChannel = socketChannel;
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
        }
        @Override
        public void run() {
            try {
                while (!Thread.interrupted()){
                    selector.select();
                    Set<SelectionKey> selected = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selected.iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey selectionKey = keyIterator.next();
                        if(selectionKey.isWritable()){
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            Scanner scanner = new Scanner(System.in);
                            if (scanner.hasNext()) {
                                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                                String next = scanner.next();
                                buffer.put((DateUtil.getNow() + " >>" + next).getBytes());
                                buffer.flip();
                                socketChannel.write(buffer);
                                buffer.clear();
                            }

                        }
                        if (selectionKey.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int length = 0;
                            while ((length = socketChannel.read(buffer)) > 0) {
                                buffer.flip();
                                System.out.println(new String(buffer.array(), 0, length));
                                buffer.clear();
                            }
                        }
                        keyIterator.remove();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) throws IOException {
        new EchoClient().start();
    }
}

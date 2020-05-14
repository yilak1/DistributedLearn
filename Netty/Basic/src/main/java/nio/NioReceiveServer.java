package nio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NioReceiveServer {

    private Charset charset = Charset.forName("UTF-8");
    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    Map<SelectableChannel, Client> clientMap = new HashMap<>();
    /**
     * 内部类，服务端保存的客户端对象，对应一个客户端文件
     */
    static class Client{
        String fileName;
        long fileLength;
        long startTime;
        InetSocketAddress remoteAddress;
        FileChannel fileChannel;

        @Override
        public String toString() {
            return "Client{" +
                    "fileName='" + fileName + '\'' +
                    ", fileLength=" + fileLength +
                    '}';
        }
    }

    public void startServer() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 12345);
        serverSocketChannel.bind(address);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (selector.select() > 0) {
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey selectionKey = keyIterator.next();
                if (selectionKey.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    if (socketChannel == null) continue;
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector,SelectionKey.OP_READ);
                    Client client = new Client();
                    client.remoteAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
                    clientMap.put(socketChannel,client);
                }else if (selectionKey.isReadable()) {
                    processData(selectionKey);
                }
                //NIO特点只会累加，已选择的健不会删除
                //不删除下次还会被选中
                keyIterator.remove();
            }
        }

    }

    public void processData(SelectionKey key){
        Client client = clientMap.get(key.channel());
        SocketChannel socketChannel = (SocketChannel) key.channel();
        int num = 0;
        try{
            buffer.clear();
            while ((num = socketChannel.read(buffer)) > 0){
                buffer.flip();
                if (client.fileName == null) {
                    String filename = charset.decode(buffer).toString();
                    System.out.println(filename);
                    String destPath = "/home/lds/";
                    client.fileName = filename;
                    String fullName = destPath + filename;
                    File file = new File(fullName);
                    FileChannel fileChannel = new FileOutputStream(file).getChannel();
                    client.fileChannel = fileChannel;

                }else if (client.fileLength == 0) {
                    long fileLength = buffer.getLong();
                    client.fileLength = fileLength;
                    client.startTime = System.currentTimeMillis();
                }else {
                    client.fileChannel.write(buffer);
                }
                buffer.clear();
            }
        } catch (IOException e) {
            key.cancel();
            e.printStackTrace();
            return;
        }
        //读取到结束标志
        if (num == -1) {
            try {
                client.fileChannel.close();
                key.cancel();
                System.out.println(client);
                System.out.println("传输秒数" + (System.currentTimeMillis() - client.startTime));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NioReceiveServer().startServer();
    }
}

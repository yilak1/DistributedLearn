package reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class EchoServerReactor implements Runnable{
    Selector selector;
    ServerSocketChannel serverSocketChannel;
    public EchoServerReactor() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        selector = Selector.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 12345));
        //分步处理,第一步,接收accept事件
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //添加接收连接的处理器
        selectionKey.attach(new AcceptHandler());
    }

    @Override
    public void run() {
        try{
            while (!Thread.interrupted()){
                //开启selector轮寻
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    //分发处理，如果是连接事件则调用AcceptHandler，如果是读事件则调用EchoHandler
                    dispatch(selectionKey);
                }
            }
        }catch (IOException e) {

        }
    }
    //分发
    public void dispatch(SelectionKey selectionKey) {
        //因为每一个Hanlder都实现了Runnable所以多态形式即可
        Runnable handle = (Runnable)selectionKey.attachment();
        if (handle != null) {
            //注意调用的是run方法,所以都是在一个线程中
            handle.run();
        }

    }
    class AcceptHandler implements Runnable{

        @Override
        public void run() {
            try {
                SocketChannel channel = serverSocketChannel.accept();
                if (channel != null) {
                    //通过AcceptHandler调用EchoHandler
                    new EchoHandler(selector, channel);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) throws IOException {
        new Thread(new EchoServerReactor()).start();;
    }
}

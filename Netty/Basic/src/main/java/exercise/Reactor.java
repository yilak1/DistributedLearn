package exercise;

import reactor.EchoHandler;

import javax.management.relation.RoleUnresolved;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Reactor implements Runnable {

    ServerSocketChannel serverSocketChannel;
    Selector selector;
    public Reactor() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        selector = Selector.open();
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 12345));
        serverSocketChannel.configureBlocking(false);
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new AcceptorHandler());

    }
    @Override
    public void run() {
        try {
            while (selector.select() > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey selectionKey = keyIterator.next();
                    dispatch(selectionKey);
                    keyIterator.remove();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dispatch(SelectionKey selectionKey) {
        Runnable runnable = (Runnable) selectionKey.attachment();
        if (runnable != null) {
            runnable.run();
        }
    }
    class AcceptorHandler implements Runnable{

        @Override
        public void run() {
            SocketChannel socketChannel = null;
            try {
                socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    new EchoHandler(selector, socketChannel);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

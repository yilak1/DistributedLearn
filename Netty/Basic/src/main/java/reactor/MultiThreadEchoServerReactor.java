package reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 1.引入多个选择器
 * 2.设计一个新的子反应器，一个子反应器负责查询一个选择器
 * 3.开启多个反应器的处理线程，一个线程负责执行一个子反应器
 */
public class MultiThreadEchoServerReactor {

    ServerSocketChannel serverSocketChannel;
    //用于选择哪一个selector
    AtomicInteger next = new AtomicInteger(0);
    //selector 集合
    Selector[] selectors = new Selector[2];
    //引入多个子反应器
    SubReactor[] subReactors = null;
    public MultiThreadEchoServerReactor() throws IOException {
        //初始化操作，并注册监听事件
        selectors[0] = Selector.open();
        selectors[1] = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        //与socket bind的区别
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 12345));
        SelectionKey selectionKey = serverSocketChannel.register(selectors[0], SelectionKey.OP_ACCEPT);
        selectionKey.attach(new AcceptorHandler());

        SubReactor subReactor1 = new SubReactor(selectors[0]);
        SubReactor subReactor2 = new SubReactor(selectors[1]);
        subReactors = new SubReactor[]{subReactor1, subReactor2};
    }

    public void startService(){
        new Thread(subReactors[0]).start();
        new Thread(subReactors[1]).start();
    }
    //子反应器内部类
    class SubReactor implements Runnable{

        final Selector selector;

        public SubReactor(Selector selector) {
            this.selector = selector;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()){
                    selector.select();
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey sk = keyIterator.next();
                        dispatch(sk);
                        keyIterator.remove();
                    }
                    selectionKeys.clear();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void dispatch(SelectionKey sk) {
            Runnable handle = (Runnable) sk.attachment();
            if (handle != null) {
                //调用之前attach绑定到选择键的handler处理器对象
                handle.run();
            }
        }
    }
    // Handler:新连接处理器
    class AcceptorHandler implements Runnable {
        public void run() {
            try {
                SocketChannel channel = serverSocketChannel.accept();
                if (channel != null)
                    new MultiThreadEchoHandler(selectors[next.get()], channel);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //next原子的增加，如果等于2就重置成0.循环使用selector
            if (next.incrementAndGet() == selectors.length) {
                next.set(0);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new MultiThreadEchoServerReactor().startService();
    }
}

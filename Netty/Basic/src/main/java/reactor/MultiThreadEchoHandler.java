package reactor;

import javax.print.attribute.standard.ReferenceUriSchemesSupported;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadEchoHandler implements Runnable{

    final SocketChannel socketChannel;
    final SelectionKey selectionKey;
    final ByteBuffer buffer = ByteBuffer.allocate(1024);
    static final int RECIEVING = 0, SENDING = 1;
    int state = RECIEVING;
    static ExecutorService pool = Executors.newFixedThreadPool(4);

    public MultiThreadEchoHandler(Selector selector, SocketChannel c) throws IOException {
        socketChannel = c;
        socketChannel.configureBlocking(false);
        //仅仅取得选择键，后设置感兴趣的IO事件
        selectionKey = socketChannel.register(selector, 0);
        //将本Handler作为sk选择键的附件，方便事件dispatch
        selectionKey.attach(this);
        //向sk选择键注册Read就绪事件
        selectionKey.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }
    @Override
    public void run() {
        //异步任务，在独立的线程池中执行
        pool.execute(new AsyncTask());
    }

    public synchronized void asyncRun(){
        try{
            if (state == SENDING) {
                socketChannel.write(buffer);
                buffer.clear();
                selectionKey.interestOps(SelectionKey.OP_READ);
                state = RECIEVING;
            }
            if (state == RECIEVING){
                int len = 0;
                while ((len= socketChannel.read(buffer)) > 0) {
                    buffer.flip();
                    System.out.println(new String(buffer.array(), 0, len));
                    buffer.clear();
                }
                buffer.flip();
                selectionKey.interestOps(SelectionKey.OP_WRITE);
                state = SENDING;
            }
            //处理结束了, 这里不能关闭select key，需要重复使用
            //sk.cancel();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    class AsyncTask implements Runnable {
        @Override
        public void run() {
            MultiThreadEchoHandler.this.asyncRun();
        }
    }
}

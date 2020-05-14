package reactor;

import com.sun.org.apache.regexp.internal.RE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class EchoHandler implements Runnable{

    final SocketChannel socketChannel;
    final SelectionKey selectionKey;
    final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    static final int RECIEVING = 0, SENDING = 1;
    int state = RECIEVING;
    //构造方法，李狗大神的写法
    public EchoHandler(Selector selector, SocketChannel socketChannel) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);
        this.selectionKey = this.socketChannel.register(selector, 0);

        //将Handler作为选择建的附件
        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    @Override
    public void run() {
        try {

            if (state == SENDING) {
                //发送数据
                socketChannel.write(byteBuffer);
                byteBuffer.clear();
                //写完数据后要读数据
                selectionKey.interestOps(SelectionKey.OP_READ);
                state= RECIEVING;
            }else if (state == RECIEVING){
                //接收数据
                int length = 0;
                while ((length = socketChannel.read(byteBuffer)) > 0) {
                    byteBuffer.flip();
                    selectionKey.interestOps(SelectionKey.OP_WRITE);
                    state = SENDING;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

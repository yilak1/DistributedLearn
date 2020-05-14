package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class NioUDPClient {
    public void send(){
        try {
            DatagramChannel datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false);
            ByteBuffer buffer =   ByteBuffer.allocate(1024);
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                buffer.put(scanner.next().getBytes());
                buffer.flip();
                datagramChannel.send(buffer, new InetSocketAddress("127.0.0.1", 12345));
                buffer.clear();
            }
            datagramChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new NioUDPClient().send();
    }
}

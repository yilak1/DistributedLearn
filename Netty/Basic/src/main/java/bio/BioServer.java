package bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioServer {
    private static ServerSocket serverSocket;
    private static ExecutorService executorService = Executors.newFixedThreadPool(5);

    public static void main(String[] args) throws IOException {
        start();
    }

    public static void start() throws IOException {
        try{
            serverSocket = new ServerSocket(IoConst.DEFAULT_PORT);
            System.out.println("服务器端口启动"+ serverSocket.getLocalPort());
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("有新的客户端连接----" );
                //当有新的客户端接入时，打包成一个任务，投入线程池
                executorService.execute(new BioHandler(socket));
            }
        }finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }
}

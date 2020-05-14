package bio;

import javax.jws.soap.SOAPBinding;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class BioClient{
    public static void main(String[] args) throws IOException {
        System.out.println("hello");
        Socket socket = new Socket(IoConst.DEFAULT_SERVER_IP, IoConst.DEFAULT_PORT);
        System.out.println("请输入请求消息");
        //启动读取服务器线程
        new ReadMsg(socket).start();
        PrintWriter pw = null;
        //允许客户端在控制台输入数据，然后发送服务端
        while (true) {
            pw = new PrintWriter(socket.getOutputStream());
            pw.println(new Scanner(System.in).next());
            pw.flush();
        }

    }
    private static class ReadMsg extends Thread {
        Socket socket;
        public ReadMsg(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()))) {
                String line = null;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                clear();
            }
        }
        public void clear(){
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BioHandler implements Runnable{
    Socket socket;
    public BioHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String message;
            String result;
            //通过输入流读取客户端传输的数据
            //如果已经读到输入流尾部，返回null,退出循环
            //如果得到非空值，就将结果进行业务处理
            while ((message = br.readLine())!= null) {
                System.out.println("Server accept message:" + message);
                result = IoConst.response(message);
                out.println(result);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

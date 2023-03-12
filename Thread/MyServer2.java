package Thread;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

// 需要处理多个用户发过来的消息

public class MyServer2 {
    public static void main(String[] args) {
        try{
            ServerSocket serverSocket = new ServerSocket(8000);
            System.out.println("waiting for connection ...");

            while(true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected : " + clientSocket.getRemoteSocketAddress());

                // 创建新的线程处理客户端请求
                ServerThread serverThread = new ServerThread(clientSocket);
                serverThread.start();
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
        
    }
}
class ServerThread extends Thread{
    Socket clientSocket;
    ServerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    public void run() {
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            String inputLine, outputLine;
            while((inputLine = in.readLine()) != null) {
                outputLine = "Server received: " + inputLine;
                out.println(outputLine);
            }

            in.close();
            out.close();
            clientSocket.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
        
    }
}

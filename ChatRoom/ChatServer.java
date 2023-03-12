package ChatRoom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private ServerSocket serverSocket;
    private List<Socket> clients;
    public  ChatServer(int port) {
        try{
            serverSocket = new ServerSocket(port);
            clients = new ArrayList<>();
            System.out.println("服务器启动,等待客户端连接");
        }catch (IOException e) {
            e.printStackTrace();
            System.out.println("服务器启动失败" + e.getMessage());
        }
    }
    public void start() {
        while(true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("客户端连接成功");
                // 将客户端socket放入列表中
                clients.add(clientSocket);
                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
            }catch(IOException e) {
                System.out.println("客户端连接失败" + e.getMessage());
            }
        }
    }
    private class ClientHandler implements Runnable{
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try{
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            }catch(IOException e) {
                System.out.println("创建客户端进程失败" + e.getMessage());
            }
        }

        @Override
        public void run() {
            while(true) {
                try{
                    // 循环读取客户端的信息
                    String message = in.readLine();
                    if(message == null) {
                        // 客户端断开连接
                        break;
                    }
                    System.out.println("收到来自客户端的信息:" + message);
                    // 转发消息给所有其他客户端
                    for(Socket client : clients) {
                        if(client != clientSocket) {
                            PrintWriter pw = new PrintWriter(client.getOutputStream(), true);
                            pw.println(message);
                        }
                    }
                }catch(IOException e) {
                    System.out.println("客户端处理消息失败" + e.getMessage());
                }finally {
                    clients.remove(clientSocket);
                    try{
                        clientSocket.close();
                        System.out.println("客户端断开连接");
                    }catch(IOException e) {
                        System.out.println("客户端关闭失败" + e.getMessage());
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer(8888);
        chatServer.start();
    }
}

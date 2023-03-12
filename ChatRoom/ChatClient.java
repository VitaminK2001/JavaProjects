package ChatRoom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ChatClient(String serverAddress, int serverPort) {
        try{
            // 通过服务端的套接字生成输入输出流，因为输入是从服务端接受，输出是输向服务端
            socket = new Socket(serverAddress, serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(String message) {
        out.println(message);
    }
    public String receiveMessage() {
        String message = null;
        try {
            // 接收消息
            message = in.readLine();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }
    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatClient chatClient1 = new ChatClient("localhost", 8888);
        ChatClient chatClient2 = new ChatClient("localhost", 8888);
        chatClient1.sendMessage("i am chatClient1");
        chatClient2.sendMessage("i am chatClient2");
    }
}
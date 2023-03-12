package Thread;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MyClient2 {
    public static void main(String[] args) {
        try{
            String host = "localhost";
            int port = 8000;
            Socket socket = new Socket(host, port);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

            String message = "Hello, Server!";
            printWriter.println(message);
            String response = bufferedReader.readLine();
            System.out.println("Server response: " + response);
        
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}

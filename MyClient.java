import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MyClient {
    static int size = 15;
    static int[][] table = new int[size+2][size+2];
    String name;
    String nameBlack, nameWhite;
    BufferedReader readServer;
    PrintWriter writeServer;

    public static void main(String[] args) {
        MyClient myClient = new MyClient();
        myClient.run();
    }

    public void run() {
        try{
            Socket socket = new Socket("localhost", 8888);
            InputStream inputStream = socket.getInputStream();  
            readServer = new BufferedReader(new InputStreamReader(inputStream));
            OutputStream outputStream = socket.getOutputStream();
            writeServer = new PrintWriter(outputStream, true);

            while(true) {
                String message = readServer.readLine();
                if(message == null) break; // 和服务器断开连接

                Scanner sc = new Scanner(System.in);
                
                // 对局开始, 输入姓名
                if(message.startsWith("START")){    
                    if(message.substring(5).equals("1")){
                        System.out.println("你是黑棋执手,请输入你的姓名: ");
                        nameBlack = sc.nextLine();
                        name = nameBlack;
                        writeServer.println(nameBlack);
                        System.out.println(readServer.readLine());
                    }else {
                        System.out.println("你是白棋执手,请输入你的姓名: ");
                        nameWhite = sc.nextLine();
                        name = nameWhite;
                        writeServer.println(nameWhite);
                        System.out.println(readServer.readLine());
                    }
                    // 接受服务器发来的对局开始信息
                    System.out.println(readServer.readLine());
                }

                // 轮到你了
                else if(message.startsWith("YOUR_TURN")){
                    // 回收棋盘
                    tableMessageFromServer();


                    // 打印棋盘
                    printTable(table, size);

                    //   判断是否可以落子
                    int row = 1, col = 1;
                    boolean canPlace = false;
                    while(!canPlace){
                        String input = sc.nextLine();
                        row = input.charAt(0) - 'A' + 1;
                        col = Integer.valueOf(input.substring(1));

                        if(row < 1 || row > size || col < 1 || col > size) {
                            System.out.println("输入错误请重新输入");
                        }else if(table[row][col] != 0) {
                            System.out.println("此处已落子");
                        }else {
                            canPlace = true;
                            table[row][col] = name.equals(nameBlack) ? 1 : 2;
                            printTable(table, size);
                        }
                    }

                    // 传送棋盘
                    tableMessageToServer();
                    // 传送输入行列
                    writeServer.println(String.valueOf(row) + "," + String.valueOf(col));
                }
                
                // 轮到对手
                else if(message.startsWith("OPPONENT_MOVE")){
                    System.out.println("等待对手落子...");
                }
                
                // 对手向你发消息
                else if(message.startsWith("MESSAGE")){

                }
                
                // 对局结束
                else if(message.startsWith("WIN")){
                    System.out.println("你已获胜!");
                }

                else if(message.startsWith("LOSE")){
                    System.out.println("你的对手已经获胜!");
                    tableMessageFromServer();
                    printTable(table, size);
                }
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void printTable(int[][] table, int size) {
        System.out.print("   ");
        for(int i = 1; i <= size; ++i) {
            System.out.print(i < 10 ? i + "  " : i + " ");
        }
        System.out.println();

        for(int i = 1; i <= size; ++i) {
            char title = (char)('A' + i-1);
            System.out.print(title + "  ");
            for(int j = 1; j <= size; ++j) {
                int value = table[i][j];
                char c = ' ';
                switch(value) {
                    case 0 : c = '.'; break;
                    case 1 : c = 'x'; break;
                    case 2 : c = 'o'; break;
                }
                System.out.print(c + "  ");
            }
            System.out.println();
        }
    }

    public void tableMessageFromServer() {
        try{
            String messageFromServer = readServer.readLine();
            String[] subStrings = messageFromServer.split(",");
            int index = 0;
            for(int i = 0; i < size+2; ++i) {
                for(int j = 0; j < size+2; ++j) {
                    table[i][j] = Integer.parseInt(subStrings[index]);
                    index++;
                }
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void tableMessageToServer() {
        int[] flatArr = Arrays.stream(table).flatMapToInt(Arrays::stream).toArray();
        String tableMessageToServer = Arrays.stream(flatArr).mapToObj(String::valueOf).collect(Collectors.joining(","));
        writeServer.println(tableMessageToServer);
    }
}

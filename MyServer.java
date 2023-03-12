import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MyServer {
    static int size = 15;
    static int[][] table = new int[size+2][size+2];

    String nameBlack;
    String nameWhite;
    BufferedReader client1In;
    PrintWriter client1Out;
    BufferedReader client2In;
    PrintWriter client2Out;

    public static void main(String[] args) {
        MyServer myServer = new MyServer();
        myServer.run();
    }

    public void run() {
        try{
            ServerSocket serverSocket = new ServerSocket(8888);
            
            System.out.println("Waiting for first player connection...");
            Socket clientSocket1 = serverSocket.accept();
            System.out.println("First player connected.");

            System.out.println("Waiting for second player connection...");
            Socket clientSocket2 = serverSocket.accept();
            System.out.println("Second player connected.");

            InputStream inputStream1 = clientSocket1.getInputStream();
            OutputStream outputStream1 = clientSocket1.getOutputStream();
            client1In = new BufferedReader(new InputStreamReader(inputStream1));
            client1Out = new PrintWriter(outputStream1, true);

            InputStream inputStream2 = clientSocket2.getInputStream();
            OutputStream outputStream2 = clientSocket2.getOutputStream();
            client2In = new BufferedReader(new InputStreamReader(inputStream2));
            client2Out = new PrintWriter(outputStream2, true);

            // 输入玩家姓名
            client1Out.println("START1");
            nameBlack = client1In.readLine();
            

            client2Out.println("START2");
            nameWhite = client2In.readLine();

            // 表示欢迎
            client1Out.println("欢迎你,"+nameBlack+". 你的对手是<"+nameWhite+">");
            client2Out.println("欢迎你,"+nameWhite+". 你的对手是<"+nameBlack+">");

            
            client1Out.println("====================================对局开始====================================");
            client2Out.println("====================================对局开始====================================");

            int curPlayer = 0;
            int win = 0;
            // 传送棋盘并回收棋盘
            do{
                String playerName = curPlayer == 0 ? nameBlack : nameWhite;
                
                if(playerName.equals(nameBlack)) {
                    client1Out.println("YOUR_TURN");
                    client2Out.println("OPPONENT_MOVE");
                }else {
                    client2Out.println("YOUR_TURN");
                    client1Out.println("OPPONENT_MOVE");
                }

                // 传送棋盘
                tableMessageToClient(playerName);

                // 回收棋盘
                tableMessageFromClient(playerName);

                // 获得用户输入的行与列
                String rowCol;
                if(playerName.equals(nameBlack)) {
                    rowCol = client1In.readLine();
                }else {
                    rowCol = client2In.readLine();
                }
                String[] subs = rowCol.split(",");
                int row = Integer.parseInt(subs[0]);
                int col = Integer.parseInt(subs[1]);
                win = check(table, row, col);

                curPlayer += 1;
                curPlayer %= 2;

            }while(win == 0);

            if(win == 1){
                client1Out.println("WIN");
                client2Out.println("LOSE");
                tableMessageToClient(nameWhite);
            }else if(win == 2) {
                client2Out.println("WIN");
                client1Out.println("LOSE");
                tableMessageFromClient(nameBlack);
            }


        }catch(IOException e) {
            e.printStackTrace();
        }

    }
    public void tableMessageToClient(String player) {
        int[] flatArr = Arrays.stream(table).flatMapToInt(Arrays::stream).toArray();
        String tableMessageFromServer = Arrays.stream(flatArr).mapToObj(String::valueOf).collect(Collectors.joining(","));
        if(player.equals(nameBlack)) {
            client1Out.println(tableMessageFromServer);
        }else {
            client2Out.println(tableMessageFromServer);
        }
    }

    public void tableMessageFromClient(String player) {
        String messageFromClient;
        try{
            if(player.equals(nameBlack)) {
                messageFromClient = client1In.readLine();
            }else {
                messageFromClient = client2In.readLine();
            }
            String[] subStrings = messageFromClient.split(",");
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

    public int check(int[][] table, int x, int y) {
        boolean test = false;
        test = test || moreThanFive(table, x, y, 0, -1);
        test = test || moreThanFive(table, x, y, 1, -1);
        test = test || moreThanFive(table, x, y, 1, 0);
        test = test || moreThanFive(table, x, y, 1, 1);
        if(test) {
            return table[x][y];
        }
        return 0;
    }
    public boolean moreThanFive(int[][] table, int x, int y, int dx, int dy) {
        int count = 0;
        count += count (table, x, y, dx, dy);
        count += count (table, x, y, -dx, -dy);
        count -= 1;
        return count >= 5;
    }
    public int count(int[][] table, int ox, int oy, int dx, int dy){
        int ov = table[ox][oy];
        int value;
        int count = 0;
        do{
            count++;
            ox += dx;
            oy += dy;
            value = table[ox][oy];
        }while(ov == value);
        return count;
    }   
}

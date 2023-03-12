import java.util.Scanner;

public class Main{
    public static void main(String[] args) {
        Main enter = new Main();
        enter.run();
    }
    public void run() {
        // 输入玩家姓名
        Scanner sc = new Scanner(System.in);
        System.out.println("输入黑棋棋手名称: ");
        String nameBlack = sc.nextLine();
        System.out.println("输入白棋棋手名称: ");
        String nameWhite = sc.nextLine();

        // 打印棋盘
        int curPlayer = 0;
        int size = 15;
        int[][] table = new int[size+2][size+2];
        printTable(table, size);

        int win = 0;
        do{
            //   下棋
            String playerName = curPlayer == 0 ? nameBlack : nameWhite;
            System.out.println("该" + playerName + "下棋了");

            //   判断是否可以落子
            int row = 1, col = 1;
            boolean canPlace = false;
            while(!canPlace){
                String input = sc.nextLine();
                row = input.charAt(0) - 'A' + 1;
                col = Integer.valueOf(input.substring(1));

                //   打印棋盘
                if(row < 1 || row > size || col < 1 || col > size) {
                    System.out.println("输入错误请重新输入");
                }else if(table[row][col] != 0) {
                    System.out.println("此处已落子");
                }else {
                    canPlace = true;
                    table[row][col] = curPlayer == 0 ? 1 : 2;
                    printTable(table, size);
                }
            }

            curPlayer += 1;
            curPlayer %= 2;

            //      判断输赢
            //      判断*当前落子*是否连成5个
            win = check(table, row, col);

        }while(win == 0);

        // 打印结果
        String winPlayer = win == 1 ? nameBlack : nameWhite;
        System.out.println(winPlayer + "获胜");
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
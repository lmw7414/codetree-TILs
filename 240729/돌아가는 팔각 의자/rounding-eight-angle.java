import java.io.*;

public class Main {
    static int[][] arr = new int[4][8];
    static int K;
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        for(int i = 0; i< 4; i++) {
            String input = br.readLine();
            for(int j = 0; j < 8; j++) {
                arr[i][j] = input.charAt(j) - '0';
            }
        }
        K = Integer.parseInt(br.readLine());
        for(int i = 0; i< K; i++) {
            String[] input = br.readLine().split(" ");
            int chairIdx = (input[0].charAt(0)- '0') - 1;
            int dir = input[1].charAt(0)- '0';
            calc(chairIdx, dir);
        }
        System.out.println(getAnswer());
    }


    public static void calc(int num, int dir) {
        if(num < 0 || num > 3) return;
        boolean left = num - 1 >= 0 && arr[num][6] != arr[num - 1][2]; // 왼쪽 방향 회전되는지 체크
        boolean right = num + 1 < 4 && arr[num][2] != arr[num + 1][6];  // 오른쪽 방향 회전되는지 체크
        setOctagonalChair(num, dir);  // 회전
        if(left) {
            calc(num - 1, -dir);
            return;
        } 
        if(right) {
            calc(num + 1, -dir);
            return;
        }
    }


    // 팔각의자 회전
    public static void setOctagonalChair(int chairIdx, int dir) {
        if(dir == 1) { // 시계방향 ->
            int temp = arr[chairIdx][7];
            for(int i = 7; i > 0; i--) {
                arr[chairIdx][i] = arr[chairIdx][i - 1];
            }
            arr[chairIdx][0] = temp;
        } else { // 반시계 방향 <-
            int temp = arr[chairIdx][0];
            for(int i = 0; i < 7; i++) {
                arr[chairIdx][i] = arr[chairIdx][i + 1];
            }
            arr[chairIdx][7] = temp;
        }
    }

    public static int getAnswer() {
        return arr[0][0] + 2 * arr[1][0] + 4 * arr[2][0] + 8 * arr[3][0];
    }
}
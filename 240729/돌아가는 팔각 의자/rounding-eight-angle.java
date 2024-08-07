import java.io.*;

public class Main {
    static int[][] arr = new int[4][8];
    static int[] dirArr;
    static boolean[] visited;
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
            int chairIdx = Integer.parseInt(input[0]) - 1;
            int dir = Integer.parseInt(input[1]);
            visited = new boolean[4];
            dirArr = new int[4];
            setDir(chairIdx, dir);
            setOctagonalChair();
        }
        System.out.println(getAnswer());
    }


    public static void setDir(int num, int dir) {
        boolean left = num - 1 >= 0 && arr[num][6] != arr[num - 1][2] && !visited[num - 1]; // 왼쪽 방향 회전되는지 체크
        boolean right = num + 1 < 4 && arr[num][2] != arr[num + 1][6] && !visited[num + 1];  // 오른쪽 방향 회전되는지 체크
        dirArr[num] = dir;
        visited[num] = true;
        if(left) {
            setDir(num - 1, -dir);
        } 
        if(right) {
            setDir(num + 1, -dir);
        }
    }


    // 팔각의자 회전
    public static void setOctagonalChair() {
        for(int chairIdx = 0; chairIdx < 4; chairIdx++) {
            int dir = dirArr[chairIdx];
            if(dir == 1) { // 시계방향 ->
                int temp = arr[chairIdx][7];
                for(int i = 7; i > 0; i--) {
                    arr[chairIdx][i] = arr[chairIdx][i - 1];
                }
                arr[chairIdx][0] = temp;
            } else if(dir == -1){ // 반시계 방향 <-
                int temp = arr[chairIdx][0];
                for(int i = 0; i < 7; i++) {
                    arr[chairIdx][i] = arr[chairIdx][i + 1];
                }
                arr[chairIdx][7] = temp;
            }
        }
        
    }

    public static int getAnswer() {
        return arr[0][0] + 2 * arr[1][0] + 4 * arr[2][0] + 8 * arr[3][0];
    }
}
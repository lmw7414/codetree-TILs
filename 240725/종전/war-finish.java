import java.util.*;
import java.io.*;
/**
[문제 설명]
1. 기울어진 사각형으로 배열에서 만듦
- 1번 부족 : 직사각형 경계와 그 안의 지역
- 2번 부족 : 좌측 상단 경계의 윗부분. 위쪽 꼭지점 포함. 왼쪽 꼭지점 불포함
- 3번 부족 : 우층 상단 경계의 윗부분. 왼쪽 꼭지점 포함
- 4번 부족 : 왼쪽 꼭지점 포함
- 5번 부족 : 아래쪽 꼭지점 포함
*/

public class Main {
    static int N;
    static int answer = Integer.MAX_VALUE;
    static int[][] arr;
    static int[][] v = new int[4][2];  // 상단, 좌측, 하단, 우측

    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        N = Integer.parseInt(br.readLine());
        arr = new int[N][N];
    
        for(int i = 0; i< N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < N; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }   
        for(int i = 0; i < N - 1; i++) {
            for(int j = 1; j < N - 1; j++) {
                v[0][0] = i;
                v[0][1] = j;
                makeBorder(1, i, j);
            }
        }
        System.out.println(answer);
    }

    public static void makeBorder(int depth, int x, int y) {
        int nx = x;
        int ny = y;
        
        if(depth == 1) { // 좌하로 이동
            while(!isOutOfRange(nx + 1, ny - 1)) {
                nx += 1;
                ny -= 1;
                v[depth][0] = nx;
                v[depth][1] = ny;
                makeBorder(depth + 1, nx, ny);
            }
        } else if(depth == 2) { // 우하로 이동
            while(!isOutOfRange(nx + 1, ny + 1)) {
                nx += 1;
                ny += 1;
                v[depth][0] = nx;
                v[depth][1] = ny;
                makeBorder(depth + 1, nx, ny);
            }
        } else if(depth == 3) { // 우상으로 이동
            while(!isOutOfRange(nx - 1, ny + 1)) {
                nx -= 1;
                ny += 1;
                v[depth][0] = nx;
                v[depth][1] = ny;
                if(Math.abs(nx - v[0][0]) == Math.abs(ny - v[0][1])) {
                    makeBorder(depth + 1, nx, ny);
                }
            }
        } else {  // 종료 조건
            fillTribe();
            return;
        }
    }
    // 범위를 벗어나면 true
    public static boolean isOutOfRange(int x, int y) {
        return x < 0 || y < 0 || x >= N || y >= N;
    }

    public static void fillTribe() {
        int[][] board = new int[N][N];
        int[] tribe = new int[5];
        
        // 2번 부족 채우기 -> 1
        int maxY = v[0][1];
        for(int x = 0; x < v[1][0]; x++) {
            if(x >= v[0][0]) maxY--;
            for(int y = 0; y <= maxY; y++) {
                board[x][y] = 1;
            }
        }

        // 3번 부족 채우기 -> 2
        int maxX = v[3][0];
        for(int y = N - 1; y > v[0][1]; y--) {
            if(y <= v[3][1]) maxX--;
            for(int x = 0; x <= maxX; x++) {
                board[x][y] = 2;
            }
        }

        // 4번 부족 채우기 -> 3
        maxX = v[1][0];
        for(int y = 0; y < v[2][1]; y++) {
            if(y >= v[1][1]) maxX++;
            for(int x = N - 1; x >= maxX; x--) {
                board[x][y] = 3;
            }
        }

        // 5번 부족 채우기 -> 4
        maxY = v[2][1];
        for(int x = N - 1; x > v[3][0]; x--) {
            if(x <= v[2][0]) maxY++;
            for(int y = N - 1; y >= maxY; y--) {
                board[x][y] = 4;
            }
        }

        // 부족 별 인구 수 계산
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                tribe[board[i][j]] += arr[i][j];
            }
        }
        int max = 0;
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < 5; i++) {
            max = Math.max(max, tribe[i]);
            min = Math.min(min, tribe[i]);
        }
        //printArr(board);
        //System.out.println(max - min + " \n");
        
        answer = Math.min(answer, max - min);
    }

    public static void printArr(int[][] arr) {
        for(int i = 0; i< N; i++) {
            for(int j = 0; j < N; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }   
    }
}
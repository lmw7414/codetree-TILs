import java.util.*;
import java.io.*;

public class Main {
    static int N, M, Q;
    static int[][] arr;
    static boolean[][] check;

    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());
        arr = new int[N][M];
        for(int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < M; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        for(int i = 0; i < Q; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());
            int k = Integer.parseInt(st.nextToken());

            rotate(x, d, k);
            boolean result = erase();
            if(!result) normalization();
            //printArr();
        }
        System.out.println(getResult());

    }

    // 회전
    public static void rotate(int x, int d, int k) {
        if(d == 1) k = M - k; // 반시계
        for(int i = x; i <= N; i += x) {
            int temp = arr[i-1][k];
            for(int j = 0; j < M-1; j++) {
                arr[i-1][(k + j) % M] = arr[i-1][j];
            }
            arr[i-1][M-2] = temp;
        }
    }

    // 인접한 것 지우기
    public static boolean erase() {
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        int cnt = 0;
        // 인접한 숫자는 0으로 변경
        check = new boolean[N][M];
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < M; j++) {
                if(check[i][j] || arr[i][j] == 0) continue;
                Queue<int[]> queue = new ArrayDeque<>();
                queue.add(new int[]{i, j});
                check[i][j] = true;
                boolean flag = false;
                int val = arr[i][j];
                while(!queue.isEmpty()) {
                    int[] cur = queue.poll();
                    for(int d = 0; d < 4; d++) {
                        int nx = cur[0] + dx[d];
                        int ny = (cur[1] + dy[d] + M) % M;
                        if(nx >= N || nx < 0) continue;
                        if(check[nx][ny] || arr[nx][ny] != val) continue;
                        arr[nx][ny] = 0;
                        check[nx][ny] = true;
                        queue.add(new int[]{nx, ny});
                        flag = true;
                    }
                }
                if(flag) {
                    cnt++;
                    arr[i][j] = 0;
                }
            }
        }
        if(cnt > 0) // 하나라도 지워진 경우
            return true;
        return false; // 이번 턴에 지워진게 없는 경우
    }
    // 정규화
    public static void normalization() {
        int sum = 0;
        int cnt = 0;
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < M; j++) {
                if(arr[i][j] == 0) continue;
                sum += arr[i][j];
                cnt++;
            }
        }
        if (cnt == N * M) return;  // 원판에 남은 수가 없는 경우
        int avg = sum / cnt;

        for(int i = 0; i < N; i++) {
            for(int j = 0; j < M; j++) {
                if(arr[i][j] > avg) arr[i][j]--;
                else if(arr[i][j] < avg) arr[i][j]++;
            }
        }
    }

    // 결과 확인
    public static int getResult() {
        int sum = 0;
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < M; j++) {
                if(arr[i][j] == 0) continue;
                sum += arr[i][j];
            }
        }
        return sum;
    }

    // 결과 확인
    public static void printArr() {
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < M; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
    }
}
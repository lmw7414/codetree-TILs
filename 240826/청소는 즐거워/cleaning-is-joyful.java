import java.util.*;
import java.io.*;

/*
1. 왼 -> 아 -> 오 -> 위 (1 -> 1 -> 2 -> 2 -> 3 -> 3 ... )
2. 빗자루가 이동할 때마다 빗자루가 이동한 위치의 격자에 있는 먼지가 비율에 맞게 함께 이동
3. a% : 다른 격자에 이동한 먼지의 양을 모두 합한 것을 이동한 위치에 있던 먼지의 양에서 빼고 남은 먼지에 해당
*/

public class Main {
    static int N;
    static int answer = 0;
    static int[] dx = {0, 1, 0, -1}; // 좌하우상
    static int[] dy = {-1, 0, 1, 0};
    static int[][] arr;
    static int[][] dist = {
        {0, 0, 2, 0, 0},
        {0, 10, 7, 1, 0},
        {5, -1, 0, 0, 0},
        {0, 10, 7, 1, 0},
        {0, 0, 2, 0, 0}
    };
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        N = Integer.parseInt(br.readLine());
        arr = new int[N][N];

        StringTokenizer st;
        for(int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < N; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        
        move(N/2, N/2, 0, 1, 0);
        System.out.println(answer);
    }

    public static void move(int x, int y, int dir, int maxDepth, int level) {
        for(int depth = 0; depth < maxDepth; depth++) {
            x += dx[dir];
            y += dy[dir];
            // 먼지 이동
            moveDust(x, y, dir);
            if(x == 0 && y == 0) return;
        }
        if(level == 1) {
            level = 0;
            maxDepth++;
        } else level++;
        
        move(x, y, (dir + 1) % 4, maxDepth, level);
    }

    public static void moveDust(int x, int y, int d) {
        int[][] scope = getScope(d);
        int value = arr[x][y];
        int dustCnt = 0;
        int ax = 0, ay = 0;

        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                if(scope[i][j] == 0) continue;
                if(scope[i][j] == -1) {
                    ax = i;
                    ay = j;
                    continue;
                }
                int nx = x - 2 + i;
                int ny = y - 2 + j;
                if(outOfRange(nx, ny)) {  // 격자 밖으로 떨어짐
                    answer += value * scope[i][j] / 100;
                }else {
                    arr[nx][ny] += value * scope[i][j] / 100;
                }
                dustCnt += value * scope[i][j] / 100;
            }
        }
        // a% setting
        if(!outOfRange(x - 2 + ax, y - 2 + ay)) {
            arr[x - 2 + ax][y - 2 + ay] += value - dustCnt;
        } else answer += value - dustCnt;
    }

    // 방향에 따른 비율 설정
    public static int[][] getScope(int d) {
        if(d == 0) {
            return dist;
        }else if(d == 2) {
            int[][] scope = new int[5][5];
            for(int i = 0; i < 5; i++) {
                for(int j = 0; j < 5; j++) {
                    scope[i][4 -j] = dist[i][j];
                }
            }
            return scope;
        } else {
            int[][] scope = new int[5][5];
            if(d == 3) {
                for(int i = 0; i < 5; i++) {
                    for(int j = 0; j < 5; j++) {
                        scope[j][i] = dist[i][j];
                    }
                }
                return scope;
            }
            else {
                for(int i = 0; i < 5; i++) {
                    for(int j = 0; j < 5; j++) {
                        scope[4 - j][4- i] = dist[i][j];
                    }
                }
                return scope;
            }
        }
    }

    // 범위를 벗어나면 true
    public static boolean outOfRange(int x, int y) {
        return x < 0 || y < 0 || x >= N || y >= N;
    }
}
import java.util.*;
import java.io.*;

public class Main {
    static int N;
    static int answer = 0;
    static int ID = 1;
    static int[][] arr, territory;
    static int[] choose = new int[2];
    static int[][] pos = new int[1000][2];

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        N = Integer.parseInt(br.readLine());
        arr = new int[N][N];
        
        for(int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < N; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        
        divide();
        permutation(1, 0);
        for(int i = 0; i < 3; i++) {
            rotate();
            divide();
            permutation(1, 0);
        }
        System.out.println(answer);
    }

    public static void divide() {
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        boolean[][] visit = new boolean[N][N];
        
        ID = 1;  // 최대 10개의 영역
        territory = new int[N][N];
       
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(visit[i][j]) continue;
                int key = arr[i][j];
                territory[i][j] = ID;
                pos[ID][0] = i;
                pos[ID][1] = j;
                Queue<int[]> queue = new ArrayDeque<>();
                queue.add(new int[]{i, j});
                while(!queue.isEmpty()) {
                    int[] cur = queue.poll();

                    for(int d = 0; d < 4; d++) {
                        int nx = cur[0] + dx[d];
                        int ny = cur[1] + dy[d];
                        if(nx < 0 || ny < 0 || nx >= N || ny >= N) continue;
                        if(visit[nx][ny]) continue;
                        if(arr[nx][ny] != key) continue;
                        territory[nx][ny] = ID;
                        visit[nx][ny] = true;
                        queue.add(new int[]{nx, ny});
                    }
                }
                ID++;
            }
        }
    }

    public static void permutation(int idx, int depth) {
        if(depth == 2) {
            answer += calc(choose[0], choose[1]);
            return;
        }
        for(int i = idx; i < ID; i++) {
            choose[depth] = i;
            permutation(i + 1, depth + 1);
        }
    }

    // (그룹 a에 속한 칸의 수 + 그룹 b에 속한 칸의 수) x 그룹 a를 이루고 있는 숫자 값 x 그룹 b를 이루고 있는 숫자값 x 그룹 a와 그룹 b가 서로 맞닿아 있는 변의 수
    public static int calc(int a, int b) {
        int cntA, cntB, valueA, valueB;
        cntA = cntB = 0;
        valueA = arr[pos[a][0]][pos[a][1]];
        valueB = arr[pos[b][0]][pos[b][1]];
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(territory[i][j] == a) cntA++;
                else if(territory[i][j] == b) cntB++;
            }
        }

        int area = getCoveredArea(pos[a][0], pos[a][1], a, b);
        return (cntA + cntB) * valueA * valueB * area;
    }

    public static int getCoveredArea(int ax, int ay, int a, int b) {
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        boolean[][] visit = new boolean[N][N];
        int cnt = 0;
        Queue<int[]> queue = new ArrayDeque<>();
        visit[ax][ay] = true;
        queue.add(new int[]{ax, ay});
            while(!queue.isEmpty()) {
                int[] cur = queue.poll();    
                for(int d = 0; d < 4; d++) {
                    int nx = cur[0] + dx[d];
                    int ny = cur[1] + dy[d];
                    if(nx < 0 || ny < 0 || nx >= N || ny >= N) continue;
                    if(visit[nx][ny]) continue;
                    if(territory[nx][ny] == a) {
                        visit[nx][ny] = true;
                        queue.add(new int[]{nx, ny});
                    } else {
                        if(territory[nx][ny] == b) cnt++;
                    }
                }
            }
        return cnt;
    }

    // 십자가, 모서리 전부 회전(rotate1, rotate2)
    public static void rotate() {
        int[][] order = {{0, 0}, {0, N/2 + 1}, {N/2 + 1, 0}, {N/2 + 1, N/2 + 1}} ;
        rotate1();
        for(int i = 0; i < 4; i++) {
            rotate2(order[i][0], order[i][1]);
        }
    }

    // 십자가 반시계방향 90도 회전
    public static void rotate1() {
        int[] temp = new int[N];
        int x = N/2;

        //1. 가로 임시 배열 저장
        for(int i = N - 1; i >= 0; i--) {
            temp[N - 1 - i] = arr[x][i];
        }
        //2. 원본 새로 배열을 가로배열로 복사
        for(int i = 0; i < N; i++) {
            arr[x][i] = arr[i][x];
        }
        //3. 임시 배열 -> 원본배열 세로배열 위치에 저장
        for(int i = 0; i < N; i++) {
            arr[i][x] = temp[i];
        }
    }

    //  모서리 시계방향 90도 회전
    public static void rotate2(int x, int y) {
        int[][] temp = copyArr(x, y);
        for(int i = 0; i < N/2; i++) {
            for(int j = 0; j < N/2; j++) {
                arr[x + j][y + N/2 - 1 - i] = temp[i][j];
            }
        }
    }

    public static int[][] copyArr(int x, int y) {
        int[][] temp = new int[N/2][N/2];

        for(int i = 0; i < N/2; i++) {
            for(int j = 0; j < N/2; j++) {
                temp[i][j] = arr[x + i][y + j];
            }
        }
        return temp;
    }

    public static void printArr(int[][] arr) {
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                System.out.print(arr[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }
}
import java.util.*;
import java.io.*;

public class Main {
    static int N, M;
    static int[][] arr;
    static Queue<int[]> supplements;
    static int[] dx = {0, 0, -1, -1, -1,  0,  1, 1, 1}; // 12시 기준 반 시계 방향
    static int[] dy = {0, 1,  1,  0, -1, -1, -1, 0, 1};
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        arr = new int[N][N];
        for(int i = 0; i< N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j<N; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        initSupplements();
        for(int i = 0; i<M; i++) {
            st = new StringTokenizer(br.readLine());
            int d = Integer.parseInt(st.nextToken());
            int p = Integer.parseInt(st.nextToken());
            simulate(d, p);
        }
        System.out.println(getResult());
    }
    public static void initSupplements() {
        supplements = new ArrayDeque<>();
        supplements.add(new int[] {N - 2, 0});
        supplements.add(new int[] {N - 1, 0});
        supplements.add(new int[] {N - 2, 1});
        supplements.add(new int[] {N - 1, 1});
    }

    // 영양제 이동 후 투입
    // 영양제 투입 시점에 +1하여 증가시키고
    // 주변 리브로수의 높이가 2 이상인 것을 카운트하여 증가
    public static void simulate(int d, int p) {
        boolean[][] visit = new boolean[N][N];
        int[][] before;
        while(!supplements.isEmpty()) {
            int[] cur = supplements.poll();
            cur[0] = (cur[0] + dx[d] * p) % N;
            cur[1] = (cur[1] + dy[d] * p) % N;
            arr[cur[0]][cur[1]]++; // 영양제 투입 자리 1 증가
            before = copyArr(); 

            arr[cur[0]][cur[1]] += countRibrosu(before, cur[0], cur[1]);
            visit[cur[0]][cur[1]] = true;
        }
        buySupplement(visit);
    }

    // 대각선 방향 높이가 1 이상인 리브로수 카운트
    public static int countRibrosu(int[][] before, int x, int y) {
        int result = 0;
        for(int d = 2; d <= 8; d+=2) {
            int nx = x + dx[d];
            int ny = y + dy[d];
            if(nx < 0 || ny < 0 || nx >= N || ny >= N) continue;
            if(before[nx][ny] >= 1) result++;
        }
        return result;
    } 

    // 높이 2이상인 리브로수는 2를 베어서 영양제 구매하고 영양제를 해당 위치에 올려둠
    public static void buySupplement(boolean[][] visit) {
        for(int i = 0; i< N; i++) {
            for(int j = 0; j<N; j++) {
                if(visit[i][j]) continue;
                if(arr[i][j] < 2) continue;
                arr[i][j] -= 2;
                supplements.add(new int[]{i, j});
            }
        }
    }

    //남아있는 리브로수 높이 계산
    public static int getResult() {
        int result = 0;
        for(int i = 0; i< N; i++) {
            for(int j = 0; j<N; j++) {
                result += arr[i][j];
            }
        }
        return result;
    }

    public static int[][] copyArr() {
        int[][] copy = new int[N][N];
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                copy[i][j] = arr[i][j];
            }
        }
        return copy;
    }
}
import java.util.*;
import java.io.*;

public class Main {
    static int N, L, R, answer;
    static int[][] arr;
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        L = Integer.parseInt(st.nextToken());
        R = Integer.parseInt(st.nextToken());
        arr = new int[N][N];

        for(int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < N; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        while(true) {
            if(check()) break;
            answer++;
        }
        System.out.println(answer);
    }

    public static boolean check() {
        boolean flag = true;
        boolean[][] visit = new boolean[N][N];
        int[] dx = {-1,1,0,0};
        int[] dy = {0,0,-1,1};

        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(visit[i][j]) continue;
                visit[i][j] = true;
                int sum = arr[i][j];
                List<Point> list = new ArrayList<>();
                Queue<Point> queue = new ArrayDeque<>();
                list.add(new Point(i, j));
                queue.add(new Point(i, j));
                while(!queue.isEmpty()) {
                    Point cur = queue.poll();
                    for(int d = 0;  d < 4; d++) {
                        int nx = cur.x + dx[d];
                        int ny = cur.y + dy[d];
                        if(outOfRange(nx, ny)) continue;
                        if(visit[nx][ny]) continue;
                        if(!isIn(Math.abs(arr[cur.x][cur.y] - arr[nx][ny]))) continue;
                        list.add(new Point(nx, ny));
                        queue.add(new Point(nx, ny));
                        visit[nx][ny] = true;
                        sum += arr[nx][ny];

                    }
                }
                if(list.size() > 1) {
                    movePopulation(list, sum);
                    flag = false;
                }
            }
        }
        return flag;
    }

    public static void movePopulation(List<Point> list, int sum) {
        int size = list.size();
        int newValue = sum / size;
        for(Point point : list) arr[point.x][point.y] = newValue; 
    }

    public static boolean isIn(int val) {
        return L <= val && val <= R;
    }

    public static boolean outOfRange(int x, int y) {
        return x < 0 || y < 0 || x >= N || y >= N;
    }

    static class Point {
        int x, y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
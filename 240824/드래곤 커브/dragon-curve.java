import java.util.*;
import java.io.*;

/*
1. 0차 드래곤 커브는 길이가 1인 선분
2. 1차 드래곤 커브는 0차 드래곤 커브 복제 -> 해당 드래곤 커브의 끝점을 기준으로 시계방향 90도 회전하여 연결
3. n차 드래곤 커브는 n-1 차 드래곤 커브의 끝점에 n-1차 드래곤 커브를 복제한 뒤 시계방향으로 90도 회전시킨 뒤 연결한 도형
*/

public class Main {
    static int N;
    static boolean[][] arr = new boolean[101][101];
    static int X, Y, D, G;  // 시작점 xy, 시작방향, 차수
    static int[] dx = {0, -1, 0 ,1}; //  오상좌하
    static int[] dy = {1, 0, -1, 0};
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        N = Integer.parseInt(br.readLine());

        StringTokenizer st;
        for(int n = 0; n < N; n++) {
            st = new StringTokenizer(br.readLine());
            X = Integer.parseInt(st.nextToken());
            Y = Integer.parseInt(st.nextToken());
            D = Integer.parseInt(st.nextToken());
            G = Integer.parseInt(st.nextToken());
            List<Point> path = new ArrayList<>();
            path.add(new Point(X, Y));  // 시작점
            path.add(new Point(X + dx[D], Y + dy[D]));  // 끝점
            rotate(path, 0);
        }
        System.out.println(calc());
        
    }

    public static void rotate(List<Point> path, int degree) {
        if(degree == G) {
            for(Point p : path) {
                arr[p.x][p.y] = true;
            }
            return;
        } else {
            int[] dir = new int[path.size() - 1];
            for(int i = 0; i < path.size() - 1; i++) {  // 끝점에서부터 시작점으로 돌아오며 방향 체크
                dir[i] = getDir(path.get(path.size() - i - 1), path.get(path.size() - i - 2));
            }
            // for(int d : dir) System.out.print(d + " ");
            // System.out.println("\n-------------");
            Point start = path.get(path.size() - 1);
            
            for(int d : dir) {
                int nd = (d + 3) % 4;  // 위에서 기록한 방향을 90도 회전
                //System.out.println(start.x + " " + start.y);
                Point next = new Point(start.x + dx[nd], start.y + dy[nd]);
                path.add(next);
                start = next;
            }
            rotate(path, degree + 1);
        }

    }

    // 끝에서부터 다음 위치의 방향
    public static int getDir(Point end, Point start) {
        int x = end.x - start.x;
        int y = end.y - start.y;

        if(x == 0 && y == 1) return 2; // 좌
        else if(x == 0 && y == -1) return 0;  // 우
        else if(x == -1 && y == 0) return 3; // 하
        else return 1;  // 상
    }

    public static int calc() {
        int answer = 0;
        for(int i = 0; i < 100; i++) {
            for(int j = 0; j < 100; j++) {
                if(arr[i][j]) {
                    if(arr[i][j + 1] && arr[i + 1][j] && arr[i + 1][j + 1]) answer++;
                }
            }
        }
        return answer;
    }

    public static void printArr() {
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                if(arr[i][j]) System.out.print("1");
                else System.out.print("0");
            }
            System.out.println();
        }
    }

    static class Point {
        int x, y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
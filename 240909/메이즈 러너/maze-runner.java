import java.util.*;
import java.io.*;

public class Main {
    static int N, M, K;
    static int[][] arr;
    static Point[] people;
    static Point exit;
    static int answer = 0;
    static int exitCnt = 0;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        arr = new int[N][N];
        people = new Point[M];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        for (int m = 0; m < M; m++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            people[m] = new Point(x, y);
        }
        st = new StringTokenizer(br.readLine());
        int x = Integer.parseInt(st.nextToken()) - 1;
        int y = Integer.parseInt(st.nextToken()) - 1;
        exit = new Point(x, y);

        for (Point human : people) {
            if (human.x == exit.x && human.y == exit.y) {
                human.end();
                exitCnt++;
            }
        }

        // 탈출 조건
        // 1. 모든 참가자 전원 탈출 or 시간 초과
        // 모든 참가자들의 이동 거리 합과 출구 좌표
        for (int k = 0; k < K; k++) {
            move(); // 참가자 전원 이동
            if (exitCnt == M) break; // 전원 탈출
            // 가장 작은 정사각형 찾기
            int[] square = find();
            if (square == null) break;
            rotate(square[0], square[1], square[2]); // 미로 회전
        }

        System.out.println(answer);
        System.out.println((exit.x + 1) + " " + (exit.y + 1));
    }

    public static void move() {
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        for (Point human : people) {
            if (human.isFinish()) continue; // 이미 도착한 사람
            int d = getDir(human, exit); // 1. 최단 거리 이동
            if (d == -1) continue; // 이동할 곳이 없음
            int nx = human.x + dx[d];
            int ny = human.y + dy[d];
            if (outOfRange(nx, ny)) continue;
            if (arr[nx][ny] > 0) continue; // 앞에 벽이 있는 경우
            if (nx == exit.x && ny == exit.y) {
                human.end();
                exitCnt++;
            } else {
                human.x = nx;
                human.y = ny;
            }
            answer++;
        }
    }

    public static int getDir(Point human, Point exit) {
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        int max = 100;
        // 1. 4방 중 출구와 가장 가까운 방향 찾기
        for (int d = 0; d < 4; d++) {
            int nx = human.x + dx[d];
            int ny = human.y + dy[d];
            if (outOfRange(nx, ny)) continue;
            int dist = getDist(new Point(nx, ny), exit);
            if (dist > max) continue;
            max = dist;
        }
        // 2. 출구와의 거리를 다시 계산했을 때 같은 경우(상하 우선순위를 적용시켜주기 위함)
        for (int d = 0; d < 4; d++) {
            int nx = human.x + dx[d];
            int ny = human.y + dy[d];
            if (outOfRange(nx, ny)) continue;
            if (arr[nx][ny] > 0) continue;
            int dist = getDist(new Point(nx, ny), exit);
            if (dist == max) return d;
        }
        return -1;
    }

    // 90도 시계방향 회전
    public static void rotate(int r, int c, int size) {
        int[][] temp = new int[size][size];

        // 1. copy & 내구도 감소
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (arr[i + r][j + c] == 0) continue;
                temp[i][j] = arr[i + r][j + c] - 1;
            }
        }

        // 2. 범위 안에 있는 사람 그리고 출구 추가
        List<Point> included = new LinkedList<>();
        for (Point human : people) {
            if (human.isFinish()) continue;
            if (!isInside(human, r, c, size)) continue;
            included.add(human);
        }
        included.add(exit);
        boolean[] check = new boolean[included.size()];

        // 90도 회전
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                arr[r + j][c + size - i - 1] = temp[i][j];
                for (int idx = 0; idx < included.size(); idx++) {
                    if (check[idx]) continue;
                    Point p = included.get(idx);
                    if (p.x == r + i && p.y == c + j) {
                        p.x = r + j;
                        p.y = c + size - i - 1;
                        check[idx] = true;
                    }
                }
            }
        }
    }

    // 최적의 정사각형 찾기
    public static int[] find() {
        int[] result = new int[3]; // 정사각형 시작점 x, 시작점 y, 크기
        int[] dx = {-1, -1, -1, 0, 1, 1, 1, 0}; // 8방
        int[] dy = {-1, 0, 1, 1, 1, 0, -1, -1};
        int[][] visit = new int[N][N];
        int dist = 1;
        Queue<Point> queue = new ArrayDeque<>();
        for (int i = 0; i < N; i++) Arrays.fill(visit[i], 100);
        visit[exit.x][exit.y] = 0;
        roll(visit); // 사람 추가 (음수로 확인)
        queue.add(exit);
        //1. BFS로 사람과 가장 가까운 거리 찾기 - 바로 break;
        while (!queue.isEmpty()) {
            boolean flag = false;
            Point cur = queue.poll();
            for (int d = 0; d < 8; d++) {
                int nx = cur.x + dx[d];
                int ny = cur.y + dy[d];
                if (outOfRange(nx, ny)) continue;
                if (visit[nx][ny] < 0) { // 참가자 발견
                    flag = true;
                    dist = visit[cur.x][cur.y] + 1;
                    break;
                }
                if (visit[nx][ny] <= visit[cur.x][cur.y]) continue; // 이전에 방문한 곳

                queue.add(new Point(nx, ny));
                visit[nx][ny] = visit[cur.x][cur.y] + 1;
            }
            if (flag) break;
        }
        result[2] = dist + 1;
        //2. 해당 거리에서 박스 체크
        for (int i = exit.x - dist; i < N - dist; i++) {
            for (int j = exit.y - dist; j < N - dist; j++) {
                if (outOfRange(i, j)) continue;
                if (!isInside(exit, i, j, dist + 1)) continue;
                for (int x = i; x <= i + dist; x++) {
                    for (int y = j; y <= j + dist; y++) {
                        if (visit[x][y] < 0) {
                            result[0] = i;
                            result[1] = j;
                            return result;
                        }
                    }
                }
            }
        }
        return null;
    }

    // 배열에 사람 위치 음수로 등록
    public static void roll(int[][] arr) {
        for (int m = 0; m < M; m++) {
            if (people[m].isFinish()) continue;
            arr[people[m].x][people[m].y] = -m - 1;
        }
    }

    //정사각형 시작좌표와 변의 길이를 매개변수로
    public static boolean isInside(Point human, int r, int c, int size) {
        if (r <= human.x && c <= human.y && r + size > human.x && c + size > human.y) return true;
        return false;
    }

    // 벗어나면 true
    public static boolean outOfRange(int x, int y) {
        return x < 0 || y < 0 || x >= N || y >= N;
    }

    public static int getDist(Point human, Point exit) {
        return Math.abs(human.x - exit.x) + Math.abs(human.y - exit.y);
    }

    static class Point {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void end() { // 참가자 탈출 시
            x = -1;
            y = -1;
        }

        public boolean isFinish() { // 참가자가 이미 탈출했는지
            if (x == -1 && y == -1) return true;
            return false;
        }
    }
}

import java.util.*;
import java.io.*;

/*
3명 이상이 한팀(머리사람--중간--꼬리사람)
각 팀은 주어진 이동 선을 따라서만 이동
1. 각 팀은 머리사람을 따라서 한 칸 이동
2. 각 라운드마다 공이 정해진 선을 따라 던져진다. n개의 행, n개의 열
    좌 -> 우
    하 -> 상
    우 -> 좌
    상 -> 하
3. 공이 던져지는 경우에 해당 선에 사람이 있으면 최초에 만나게 되는 사람만이 공을 얻게 되어 점수를 얻게 됩니다.
 - 점수는 해당 사람이 머리 사람 기준으로 k번째라면 k^2만큼의 점수를 얻음
 - 공을 획득하면 머리사람과 꼬리 사람이 바뀜
*/

public class Main {
    static int N, M, K;
    static int[][] arr, groupArr;
    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, -1, 1};
    static int answer = 0;
    static Point[][] groups;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        arr = new int[N][N];
        groupArr = new int[N][N];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 그룹별 선두 선미 체크
        int maxId = groupCheck();
        //printArr(groupArr);
        calc(maxId);
    }


    public static void calc(int maxId) {
        for (int round = 0; round < K; round++) {
            // 1. 이동
            List<Point>[] list = new List[maxId + 1];
            for (int i = 1; i <= M; i++) {
                Point[] group = groups[i];
                list[i] = moveGroup(group);
//                for (Point p : list[i]) {
//                    System.out.printf("%d:[%d, %d]-> ", arr[p.x][p.y], p.x, p.y);
//                }
//                System.out.println();

            }
            // 2. 공 굴리기
            int key = round / N % 4;
            Point p = null;
            switch (key) {
                case 0:
                    p = first(round);
                    break;
                case 1:
                    p = second(round);
                    break;
                case 2:
                    p = third(round);
                    break;
                case 3:
                    p = fourth(round);
                    break;
            }
            if (p == null) continue;
            // 3. 점수 계산
            //System.out.printf("hit %d: [%d, %d]\n",arr[p.x][p.y], p.x, p.y);
            int hit = groupArr[p.x][p.y];
            int score = 0;
            for (Point next : list[hit]) {
                score++;
                if (next.x == p.x && next.y == p.y) break;
            }
            //System.out.println("score:" + score);
            answer += score * score;
            // 4. reverse
            reverse(hit);
        }
        System.out.println(answer);
    }

    public static List<Point> moveGroup(Point[] group) {
        // 선두 먼저 이동
        List<Point> list = findLine(group[0]);
        group[1].x = list.get(list.size() - 2).x;
        group[1].y = list.get(list.size() - 2).y;
        Point p = list.get(0);
        boolean flag = isThereFour(p.x, p.y); // 4가 있으므로 4로 이동

        if (!flag) {
            int previousX = list.get(list.size() - 1).x;
            int previousY = list.get(list.size() - 1).y;
            for (int i = 0; i < list.size(); i++) {

                if(i != list.size() - 1) move(list.get(i).x, list.get(i).y, previousX, previousY);
                int tx = list.get(i).x;
                int ty = list.get(i).y;
                list.get(i).x = previousX;
                list.get(i).y = previousY;
                previousX = tx;
                previousY = ty;
            }

            return list;
        }


        int previousX = -1;
        int previousY = -1;
        for (int d = 0; d < 4; d++) {
            int nx = group[0].x + dx[d];
            int ny = group[0].y + dy[d];
            if (OOB(nx, ny)) continue;
            if (arr[nx][ny] == 4) {
                previousX = nx;
                previousY = ny;
                break;
            }
        }

        for (int i = 0; i < list.size(); i++) {
            p = list.get(i);
            int tx = p.x;
            int ty = p.y;

            move(p.x, p.y, previousX, previousY);
            p.x = previousX;
            p.y = previousY;
            previousX = tx;
            previousY = ty;
        }
        return list;
    }

    public static List<Point> findLine(Point first) {
        boolean[][] visit = new boolean[N][N];
        List<Point> result = new ArrayList<>();
        Queue<Point> queue = new ArrayDeque<>();
        queue.add(first);
        result.add(first);
        visit[first.x][first.y] = true;
        while (!queue.isEmpty()) {
            Point cur = queue.poll();
            for (int d = 0; d < 4; d++) {
                int nx = cur.x + dx[d];
                int ny = cur.y + dy[d];
                if (OOB(nx, ny) || visit[nx][ny]) continue;
                if (arr[nx][ny] == 0 || arr[nx][ny] == 4) continue;
                if (arr[cur.x][cur.y] == 1 && isThereTwo(cur.x, cur.y) && arr[nx][ny] == 3) continue;
                visit[nx][ny] = true;
                result.add(new Point(nx, ny));
                queue.add(new Point(nx, ny));
            }
        }
        return result;
    }

    public static boolean isThereFour(int x, int y) {
        for (int d = 0; d < 4; d++) {
            int nx = x + dx[d];
            int ny = y + dy[d];
            if (OOB(nx, ny)) continue;
            if (arr[nx][ny] == 0) continue;
            if (arr[nx][ny] == 4) return true;
        }
        return false;
    }

    public static boolean isThereTwo(int x, int y) {
        for (int d = 0; d < 4; d++) {
            int nx = x + dx[d];
            int ny = y + dy[d];
            if (OOB(nx, ny)) continue;
            if (arr[nx][ny] == 0) continue;
            if (arr[nx][ny] == 2) return true;
        }
        return false;
    }

    public static void move(int curX, int curY, int nextX, int nextY) {
        int temp = arr[curX][curY];
        arr[curX][curY] = arr[nextX][nextY];
        arr[nextX][nextY] = temp;
    }

    public static void reverse(int id) {
        Point temp = groups[id][0];
        groups[id][0] = groups[id][1];
        groups[id][1] = temp;
        move(groups[id][0].x, groups[id][0].y, groups[id][1].x, groups[id][1].y);
    }

    // 그룹별 선두 선미 체크
    public static int groupCheck() {
        groups = new Point[M + 1][2];
        boolean[][] visit = new boolean[N][N];
        int id = 1;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (arr[i][j] != 1) continue;

                // 선두 발견
                visit[i][j] = true;
                groupArr[i][j] = id;
                groups[id][0] = new Point(i, j);
                Queue<Point> queue = new ArrayDeque<>();
                queue.add(groups[id][0]);
                int x3 = 0;
                int y3 = 0;
                while (!queue.isEmpty()) {
                    Point cur = queue.poll();
                    for (int d = 0; d < 4; d++) {
                        int nx = cur.x + dx[d];
                        int ny = cur.y + dy[d];
                        if (OOB(nx, ny)) continue;
                        if (arr[nx][ny] == 0) continue;
                        if (visit[nx][ny]) continue;
                        visit[nx][ny] = true;
                        groupArr[nx][ny] = id;
                        if (arr[nx][ny] == 3) {
                            x3 = nx;
                            y3 = ny;
                        }
                        queue.add(new Point(nx, ny));
                    }
                }
                groups[id++][1] = new Point(x3, y3);
            }
        }
        return id - 1;
    }

    public static Point first(int round) {
        int row = round % N;
        for (int i = 0; i < N; i++) {
            if (arr[row][i] > 0 && arr[row][i] < 4) {
                return new Point(row, i);
            }
        }
        return null;
    }

    public static Point second(int round) {
        int col = round % N;
        for (int i = 0; i < N; i++) {
            if (arr[N - i - 1][col] > 0 && arr[N - i - 1][col] < 4) {
                return new Point(N - i - 1, col);
            }
        }
        return null;
    }

    public static Point third(int round) {
        int row = round % N;
        for (int i = 0; i < N; i++) {
            if (arr[N - row - 1][N - i - 1] > 0 && arr[N - row - 1][N - i - 1] < 4) {
                return new Point(N - row - 1, N - i - 1);
            }
        }
        return null;
    }

    public static Point fourth(int round) {
        int col = round % N;
        for (int i = 0; i < N; i++) {
            if (arr[i][N - col - 1] > 0 && arr[i][N - col - 1] < 4) {
                return new Point(i, N - col - 1);
            }
        }
        return null;
    }

    public static boolean OOB(int x, int y) {
        return x < 0 || y < 0 || x >= N || y >= N;
    }

    public static void printArr(int[][] arr) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(arr[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    static class Point {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}

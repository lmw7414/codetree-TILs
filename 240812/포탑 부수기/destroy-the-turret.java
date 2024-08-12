import java.util.*;
import java.io.*;

/**
 * [문제 해결 프로세스]
 * 1. 가장 약한 포탑 찾기 -> 공격자 선정
 * 2. 가장 강한 포탑 찾기 -> 공격
 * 3. 레이저 공격 -> 우하좌상, 가장 강한 포탑 외 해당 경로의 포탑 역시 공격 대상이 됨
 * 4. 포탄 공격 -> 레이저 공격 불가 시.
 * -> 가장 강한 포탑의 공격력을 출력
 */

public class Main {
    static int N, M, K;
    static int[][] arr, cntArr;
    static boolean[][] relative;
    static int idx = 0;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        arr = new int[N][M];
        cntArr = new int[N][M];  // 공격 턴 카운트 배열

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < M; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        for (int k = 0; k < K; k++) {
            relative = new boolean[N][M]; // 관련 없는 포탑 체크 배열
            // 공격자 선정
            Point attacker = findAttacker();
            relative[attacker.x][attacker.y] = true;
            // 공격대상 선정
            Point strongest = strongestTower();
            relative[strongest.x][strongest.y] = true;
            // 레이저 공격
            List<Point> result = laiserAttack(attacker, strongest);
            if (result != null) {
//                System.out.print("laiser path : ");
//                for (Point p : result) {
//                    System.out.print(p.x + " " + p.y + " ->");
//                }
//                System.out.println();
                laiserBomb(result, arr[attacker.x][attacker.y], strongest);
            } else { // 레이저 불가시 포탄 공격
                bomb(attacker, strongest);
            }
//            printArr(arr);
            repair(); // 공격과 무관한 포탄 정비 +1
            addAttackTurn(attacker); // 턴 증가
        }

        getResult();
    }

    public static Point findAttacker() {
        int power = 10000;
        int bx = -1, by = -1;
        for (int y = M - 1; y >= 0; y--) {
            for (int x = N - 1; x >= 0; x--) {
                if (arr[x][y] == 0) continue;
                if (power >= arr[x][y]) {
                    if (power == arr[x][y] && cntArr[bx][by] < cntArr[x][y]) continue;
                    power = arr[x][y];
                    bx = x;
                    by = y;
                }
            }
        }
        arr[bx][by] += N + M;
//        System.out.println("attacker : " + bx + " " + by);
        return new Point(bx, by);
    }

    /*
     * 1. 공격력 가장 높음
     * 2. 공격한지 오래된 포탑
     * 3. 행과 열이 가장 작음
     * 4. 열 값이 가장 작음
     */
    public static Point strongestTower() {
        int power = 0;
        int bx = -1, by = -1;
        for (int y = 0; y < M; y++) {
            for (int x = 0; x < N; x++) {
                if (arr[x][y] == 0 || relative[x][y]) continue;
                if (power <= arr[x][y]) {
                    if (power == arr[x][y]) {
                        if (cntArr[bx][by] > cntArr[x][y]) continue;
                        if (bx + by < x + y) continue;
                        if (by < y) continue;
                    }
                    power = arr[x][y];
                    bx = x;
                    by = y;
                }
            }
        }
//        System.out.println("victim : " + bx + " " + by);
        return new Point(bx, by);
    }

    public static List<Point> laiserAttack(Point start, Point victim) {
        int[][] pathArr = new int[N][M];
        for (int i = 0; i < N; i++) Arrays.fill(pathArr[i], 100000);
        pathArr[start.x][start.y] = 0;
        int[] dx = {0, 1, 0, -1}; // 우하좌상
        int[] dy = {1, 0, -1, 0};
        Queue<Point> queue = new ArrayDeque<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            Point cur = queue.poll();
            if (cur.x == victim.x && cur.y == victim.y) break;
            for (int d = 0; d < 4; d++) {
                int nx = (N + cur.x + dx[d]) % N;
                int ny = (M + cur.y + dy[d]) % M;
                if (arr[nx][ny] == 0) continue;
                if (pathArr[nx][ny] < pathArr[cur.x][cur.y] + 1) continue;
                pathArr[nx][ny] = pathArr[cur.x][cur.y] + 1;
                queue.add(new Point(nx, ny));
            }
        }
        return findPath(start, victim, pathArr);
    }

    public static List<Point> findPath(Point start, Point victim, int[][] path) {
        int[] dx = {0, 1, 0, -1}; // 우하좌상
        int[] dy = {1, 0, -1, 0};
        List<Point> list = new ArrayList<>();
        int curX = start.x;
        int curY = start.y;
        int cur = 0;

        for (int d = 0; d < 4; d++) {
            int nx = (N + curX + dx[d]) % N;
            int ny = (M + curY + dy[d]) % M;
            if (path[nx][ny] == cur + 1) {
                curX = nx;
                curY = ny;
                list.add(new Point(curX, curY));
                if (nx == victim.x && ny == victim.y) return list;
                cur++;
                d = -1;
            }
        }
        return null;
    }


    public static void laiserBomb(List<Point> path, int power, Point victim) {
        for (Point target : path) {
            relative[target.x][target.y] = true;
            if (target.x == victim.x && target.y == victim.y) arr[target.x][target.y] -= power;
            else arr[target.x][target.y] -= power / 2;
            if (arr[target.x][target.y] < 0) arr[target.x][target.y] = 0;
        }
    }

    public static void bomb(Point attacker, Point victim) {
        arr[victim.x][victim.y] -= arr[attacker.x][attacker.y];
        if (arr[victim.x][victim.y] < 0) arr[victim.x][victim.y] = 0;
        int[] dx = {0, 1, 0, -1, 1, 1, -1, -1}; // 우하좌상
        int[] dy = {1, 0, -1, 0, 1, -1, 1, -1};
        for (int i = 0; i < 8; i++) {
            int nx = (N + victim.x + dx[i]) % N;
            int ny = (M + victim.y + dy[i]) % M;
            if (arr[nx][ny] == 0) continue;
            if (nx == attacker.x && ny == attacker.y) continue;
            arr[nx][ny] -= arr[attacker.x][attacker.y] / 2;
            if (arr[nx][ny] < 0) arr[nx][ny] = 0;
            relative[nx][ny] = true;
        }

    }


    public static void repair() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (relative[i][j] || arr[i][j] == 0) continue;
                arr[i][j]++;
            }
        }
    }

    // 공격자를 제외한 나머지는 턴 증가. 공격자는 턴 초기화
    public static void addAttackTurn(Point attack) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (arr[i][j] == 0) continue;
                if (attack.x == i && attack.y == j) cntArr[i][j] = 0;
                else cntArr[i][j]++;
            }
        }
    }

    public static void getResult() {
        int max = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                max = Math.max(max, arr[i][j]);
            }
        }
        System.out.println(max);
    }

    public static void printArr(int[][] arr) {
        System.out.println();
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j] + "\t");
            }
            System.out.println("\n");
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
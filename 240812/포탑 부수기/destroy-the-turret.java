import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

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
            if (strongest.x == -1) break;
            relative[strongest.x][strongest.y] = true;
            // 레이저 공격
            arr[attacker.x][attacker.y] += N + M;
            List<Point> result = laiserAttack(attacker, strongest);
            if (result != null) {
//                System.out.print("laiser path : ");
//                for (Point p : result) {
//                    System.out.print(p.x + " " + p.y + " ->");
//                }
//                System.out.println();
                laserBomb(result, attacker, strongest);
            } else { // 레이저 불가시 포탄 공격
                bomb(attacker, strongest);
            }
            repair(); // 공격과 무관한 포탄 정비 +1
            addAttackTurn(attacker); // 턴 증가
//            printArr(arr);
        }

        getResult();
    }

    public static Point findAttacker() {
        int power = 10000;
        int bx = -1, by = -1;
        for (int y = M - 1; y >= 0; y--) {
            for (int x = N - 1; x >= 0; x--) {
                if (arr[x][y] == 0) continue;
                if (power < arr[x][y]) continue;
                if (bx == -1 && by == -1) {
                    power = arr[x][y];
                    bx = x;
                    by = y;
                    continue;
                }
                if (isAttackBest(new Point(bx, by), new Point(x, y))) {
                    power = arr[x][y];
                    bx = x;
                    by = y;
                }

            }
        }

//        System.out.println("attacker : " + bx + " " + by);
        return new Point(bx, by);
    }

    public static boolean isAttackBest(Point best, Point cur) {
        if (arr[best.x][best.y] > arr[cur.x][cur.y]) return true;
        else if (arr[best.x][best.y] == arr[cur.x][cur.y]) {
            if (cntArr[best.x][best.y] > cntArr[cur.x][cur.y]) return true;
            else if (cntArr[best.x][best.y] == cntArr[cur.x][cur.y]) {
                if (best.x + best.y < cur.x + cur.y) return true;
                else if (best.x + best.y == cur.x + cur.y) {
                    if (best.y < cur.y) return true;
                }
            }
        }
        return false;
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
                if (power > arr[x][y]) continue;
                if (bx == -1 && by == -1) {
                    power = arr[x][y];
                    bx = x;
                    by = y;
                    continue;
                }
                if (isStrongest(new Point(bx, by), new Point(x, y))) {
                    power = arr[x][y];
                    bx = x;
                    by = y;
                }
            }
        }
//        System.out.println("victim : " + bx + " " + by);
        return new Point(bx, by);
    }

    public static boolean isStrongest(Point best, Point cur) {
        if (arr[best.x][best.y] < arr[cur.x][cur.y]) return true;
        else if (arr[best.x][best.y] == arr[cur.x][cur.y]) {
            if (cntArr[best.x][best.y] < cntArr[cur.x][cur.y]) return true;
            else if (cntArr[best.x][best.y] == cntArr[cur.x][cur.y]) {
                if (best.x + best.y > cur.x + cur.y) return true;
                else if (best.x + best.y == cur.x + cur.y) {
                    if (best.y > cur.y) return true;
                }
            }
        }
        return false;
    }

    public static List<Point> laiserAttack(Point start, Point victim) {
        int[][] pathArr = new int[N][M];
        Point[][] pArr = new Point[N][M]; // 이전 것을 기록
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
                if(pArr[nx][ny] == null) {
                    pArr[nx][ny] = new Point(cur.x, cur. y);
                }
                pathArr[nx][ny] = pathArr[cur.x][cur.y] + 1;
                queue.add(new Point(nx, ny));
            }
        }
//        printArr(pathArr);
        return findPath(victim, start, pArr);
    }

    public static List<Point> findPath(Point start, Point dest, Point[][] path) {
        List<Point> list = new ArrayList<>();
        Point cur = start;
        while(true) {
           list.add(cur);
           cur = path[cur.x][cur.y];
           if (cur == null) return null;
           if (cur.x == dest.x && cur.y == dest.y) return  list;
        }
    }


    public static void laserBomb(List<Point> path, Point attack, Point victim) {
        int power = arr[attack.x][attack.y];
        arr[victim.x][victim.y] -= power;
        if (arr[victim.x][victim.y] < 0) arr[victim.x][victim.y] = 0;
        for (Point target : path) {
            relative[target.x][target.y] = true;
            if (target.x == victim.x && target.y == victim.y) continue;
            if (target.x == attack.x && target.y == attack.y) continue;
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
//        System.out.println("cntArr");
//        printArr(cntArr);
//        System.out.println();
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
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if(arr[i][j] == 100000) System.out.print("INF ");
                else System.out.print(arr[i][j] + "\t");
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
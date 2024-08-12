import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

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
            // 공격자 & 공격 대상 선정
            Point weaker = new Point(-1, -1);
            Point stronger = new Point(-1, -1);
            findWeakestAndStrongest(weaker, stronger);
            if (weaker.x == stronger.x && weaker.y == stronger.y) break;
            relative[weaker.x][weaker.y] = true;
            relative[stronger.x][stronger.y] = true;

            // 레이저 공격
            arr[weaker.x][weaker.y] += N + M;
            List<Point> result = laserAttack(weaker, stronger);
            if (result != null) laserBomb(result, weaker, stronger);
            else bomb(weaker, stronger);

            repair(); // 공격과 무관한 포탄 정비 +1
            addAttackTurn(weaker); // 턴 증가
        }
        //printArr(arr);
        getResult();
    }

    public static void findWeakestAndStrongest(Point weaker, Point stronger) {
        int wPower = 10000, sPower = 0;
        for (int x = 0; x < N; x++) {
            for (int y = 0; y < M; y++) {
                if (arr[x][y] == 0) continue;
                if (wPower >= arr[x][y]) {
                    if (weaker.x == -1 && weaker.y == -1) {
                        wPower = arr[x][y];
                        weaker.x = x;
                        weaker.y = y;
                    } else if (isAttackBest(weaker, new Point(x, y))) {
                        wPower = arr[x][y];
                        weaker.x = x;
                        weaker.y = y;
                    }
                }
                if (sPower <= arr[x][y]) {
                    if (stronger.x == -1 && stronger.y == -1) {
                        sPower = arr[x][y];
                        stronger.x = x;
                        stronger.y = y;
                    } else if (isStrongest(stronger, new Point(x, y))) {
                        sPower = arr[x][y];
                        stronger.x = x;
                        stronger.y = y;
                    }
                }
            }
        }
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

    public static List<Point> laserAttack(Point start, Point victim) {
        int[][] pathArr = new int[N][M];
        Point[][] pArr = new Point[N][M]; // 이전 경로를 기록
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
                if (pArr[nx][ny] == null) {
                    pArr[nx][ny] = new Point(cur.x, cur.y);
                }
                pathArr[nx][ny] = pathArr[cur.x][cur.y] + 1;
                queue.add(new Point(nx, ny));
            }
        }
        return findPath(victim, start, pArr);
    }

    // 레이저 경로 탐색(공격대상으로 부터 공격자까지 이동)
    public static List<Point> findPath(Point start, Point dest, Point[][] path) {
        List<Point> list = new ArrayList<>();
        Point cur = start;
        while (true) {
            list.add(cur);
            cur = path[cur.x][cur.y];
            if (cur == null) return null;
            if (cur.x == dest.x && cur.y == dest.y) return list;
        }
    }

    // 레이저 경로에 있는 포탑 공격
    public static void laserBomb(List<Point> path, Point attack, Point victim) {
        int power = arr[attack.x][attack.y];
        for (Point target : path) {
            relative[target.x][target.y] = true;
            if (target.x == attack.x && target.y == attack.y) continue;

            if (target.x == victim.x && target.y == victim.y) arr[victim.x][victim.y] -= power; // 공격 대상자
            else arr[target.x][target.y] -= power / 2;
            // 포탑 파괴되는 경우
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

    // 공격과 무관한 포탑 정비
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/*
[문제 설명]
1. 4x4 격자에 m개의 몬스터와 팩맨이 주어짐
2. 몬스터는 상하좌우, 대각선 방향 중 1개를 이동할 수 있음
3. 턴 단위로 진행
  - 몬스터 복제 시도
    - 현재 위치에 알 생성(몬스터와 동일한 방향)
  - 몬스터 이동
    - 한 칸 이동
    - 몬스터 시체, 팩맨, 경계를 벗어난 경우 -> 반시계 45도로 이동 가능할 때까지 회전하며 방향 선택(8방향 모두 이동 불가능하면 해당 몬스터는 정지)
  - 팩맨 이동
    - 3칸 이동
    - 몬스터를 가장 많이 먹을 수 있는 방향으로 이동
    - 상 -> 좌 -> 하 -> 우 순으로 우선순위가 있음
    - 이동하며 몬스터를 먹으며, 알은 먹지 않음
  - 몬스터 시체 소멸
    - 몬스터의 시체는 2턴간 유지
  - 몬스터 복제 완성
    - 알 형태의 몬스터 부화
4. 모든 턴이 진행되고 난 뒤 살아남은 몬스터의 마리 수를 출력해라

[입력]
- m : 몬스터 마리 (최대 10마리)
- t : 진행되는 턴의 수 (최대 25턴)
- r, c : 행 열

[문제 해결 프로세스]
1. 몬스터의 정보를 담을 객체 생성
  - 방향, 현재 좌표, 현재 상태(몬스터, 알, 시체), 생성부터 현재까지의 턴
2. 현재의 위치를 모두 기록하는 보드 생성(해당 좌표의 몬스터 마리 수)
3. 몬스터 배열 리스트 생성
  - 몬스터 복제
  - 몬스터 이동
4. 팩맨이 이동할 방향 선택
  - 몬스터 3마리 전부 먹는 것이 베스트
  - DFS로 최적 경로 탐색
*/

public class Main {
    static int m, t, r, c;
    static List<Monster> monsters = new LinkedList<>();  // 몬스터
    static List<Monster> carcasses = new LinkedList<>(); // 사체
    static Queue<Monster> eggs = new LinkedList<>();     // 알

    static int[][] board = new int[4][4];                // 몬스터
    static int[][] carcass = new int[4][4];              // 사체
    static int[] dx = {-1, -1, 0, 1, 1, 1, 0, -1};       // 상 좌 하 우(반시계)
    static int[] dy = {0, -1, -1, -1, 0, 1, 1, 1};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        m = Integer.parseInt(st.nextToken());
        t = Integer.parseInt(st.nextToken());

        st = new StringTokenizer(br.readLine());
        r = Integer.parseInt(st.nextToken()) - 1;
        c = Integer.parseInt(st.nextToken()) - 1;

        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            int d = Integer.parseInt(st.nextToken()) - 1;
            monsters.add(new Monster(x, y, d));
            board[x][y]++;
        }
        System.out.println(game());
    }

    public static int game() {
        for (int turn = 0; turn < t; turn++) {
            // 몬스터 복제
            copyMonster();
            // 몬스터 이동
            moveMonster();
            // 팩맨 이동
            movePackman();
            // 몬스터 사체 소멸
            extinction();
            // 몬스터 부화 완성 - 알에서 몬스터로
            hatch();
        }
        return countMonster();
    }

    // 몬스터 복제
    public static void copyMonster() {
        for (Monster monster : monsters) {
            eggs.add(monster.layEgg());
        }
    }

    // 몬스터 이동
    public static void moveMonster() {
        for (Monster monster : monsters) {
            // 현재 위치의 몬스터 개수 변경
            board[monster.x][monster.y]--;

            int nx = monster.x + dx[monster.dir];
            int ny = monster.y + dy[monster.dir];
            int cnt = 0;
            while (!monster.movable(nx, ny)) {
                if (cnt > 7) {
                    nx = monster.x;
                    ny = monster.y;
                    break;
                }
                monster.changeDir();
                nx = monster.x + dx[monster.dir];
                ny = monster.y + dy[monster.dir];
                cnt++;
            }
            monster.x = nx;
            monster.y = ny;

            board[nx][ny]++;
        }
    }

    // 팩맨 이동
    public static void movePackman() {

        int bestCnt = -1;
        int bestF, bestS, bestT;
        bestF = bestS = bestT = 0;

        for (int f = 0; f < 8; f += 2) {
            int fx = r + dx[f];
            int fy = c + dy[f];
            if (isOutOfRange(fx, fy)) continue;

            for (int s = 0; s < 8; s += 2) {
                int sx = fx + dx[s];
                int sy = fy + dy[s];
                if (isOutOfRange(sx, sy)) continue;

                for (int t = 0; t < 8; t += 2) {
                    int tx = sx + dx[t];
                    int ty = sy + dy[t];
                    if (isOutOfRange(tx, ty)) continue;
                    int tCnt = findMonster(fx, fy, sx, sy, tx, ty);
                    if (bestCnt < tCnt) {
                        bestCnt = tCnt;
                        bestF = f;
                        bestS = s;
                        bestT = t;
                    }
                }
            }
        }
        // 보드에서 몬스터 제거 -> 사체 보드로 이동
        // 몬스터 리스트 -> 사체 리스트
        toCarcass(r + dx[bestF], c + dy[bestF],
                r + dx[bestF] + dx[bestS], c + dy[bestF] + dy[bestS],
                r + dx[bestF] + dx[bestS] + dx[bestT], c + dy[bestF] + dy[bestS] + dy[bestT]
        );

        r = r + dx[bestF] + dx[bestS] + dx[bestT];
        c = c + dy[bestF] + dy[bestS] + dy[bestT];
    }

    public static void toCarcass(int fx, int fy, int sx, int sy, int tx, int ty) {
        Queue<Monster> monsterQueue = new ArrayDeque<>();
        monsterQueue.addAll(monsters);
        monsters.clear();
        while (!monsterQueue.isEmpty()) {
            Monster monster = monsterQueue.poll();
            if (
                    (monster.x == fx && monster.y == fy) ||
                            (monster.x == sx && monster.y == sy) ||
                            (monster.x == tx && monster.y == ty)
            ) {
                board[monster.x][monster.y]--;
                carcass[monster.x][monster.y]++;
                carcasses.add(monster);
                monsters.remove(monster);
            } else {
                monsters.add(monster);
            }
        }
    }

    public static int findMonster(int fx, int fy, int sx, int sy, int tx, int ty) {
        int answer = 0;
        boolean[][] visit = new boolean[4][4];
        if (!visit[fx][fy]) {
            answer += board[fx][fy];
            visit[fx][fy] = true;
        }
        if (!visit[sx][sy]) {
            answer += board[sx][sy];
            visit[sx][sy] = true;
        }
        if (!visit[tx][ty]) {
            answer += board[tx][ty];
            visit[tx][ty] = true;
        }
        return answer;

    }

    // 몬스터 사체 소멸
    public static void extinction() {
        Queue<Monster> carcessQueue = new ArrayDeque<>();
        carcessQueue.addAll(carcasses);
        carcasses.clear();
        while (!carcessQueue.isEmpty()) {
            Monster ca = carcessQueue.poll();
            if (ca.turn < 2) {
                ca.turn++;
                carcasses.add(ca);
            } else {
                carcass[ca.x][ca.y]--;
            }
        }
    }

    public static boolean isOutOfRange(int nx, int ny) {
        if (nx < 0 || ny < 0 || nx >= 4 || ny >= 4) return true;  // 경계를 벗어난 경우
        return false;
    }

    // 몬스터 부화 완성
    public static void hatch() {
        while (!eggs.isEmpty()) {
            Monster egg = eggs.poll();
            monsters.add(egg);
            board[egg.x][egg.y]++;
        }
    }

    // 몬스터 개수 계산
    public static int countMonster() {
        int answer = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                answer += board[i][j];
            }
        }
        return answer;
    }

    static class Monster {
        int x;
        int y;
        int dir;  // 최대 8방향
        int turn;  // 시체일 경우에만 필요

        public Monster(int x, int y, int dir) {
            this.x = x;
            this.y = y;
            this.dir = dir;
            this.turn = 0;
        }

        public Monster layEgg() {
            return new Monster(this.x, this.y, this.dir);
        }

        public boolean movable(int nx, int ny) {
            if (nx < 0 || ny < 0 || nx >= 4 || ny >= 4) return false;  // 경계를 벗어난 경우
            if (nx == r && ny == c) return false;  // 해당 위치에 팩맨이 존재하는 경우
            if (carcass[nx][ny] > 0) return false; // 시체가 있는 경우
            return true;
        }

        public void changeDir() {
            this.dir = (this.dir + 1) % 8;
        }
    }
}
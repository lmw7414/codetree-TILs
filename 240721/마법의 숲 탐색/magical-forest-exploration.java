import java.util.*;
import java.io.*;

/**
 * [문제설명]
 * 1. 숲의 동쪽, 서쪽, 남쪽은 마법의 벽으로 막혀 있으며, 정령들은 숲의 북쪽을 통해서만 숲에 들어올 수 있다.
 * 2. 총 K명의 정령은 각자 골렘을 타고 숲을 탐색.
 * 3. 각 골렘은 십자 모양의 구조를 가지고 있으며 중앙 칸을 포함해 5칸 차지
 * 4. 골렘의 중앙을 제외한 4칸 중 한 칸은 골렘의 출구.
 * 5. 정령은 어떤 방향에서든 골렘에 탑승할 수 있지만 골렘에서 내릴 때는 정해진 출구로만 내릴 수 있음
 * 6. i번째로 숲을 탐색하는 골렘은 숲의 가장 북쪽에서 시작해 골렘의 중앙이 Ci열이 되도록 하는 위치에서 내려오기 시작
 * 7. 초기 골렘의 출구는 Di의 방향에 위치해 있음
 * 8. 골렘은 숲을 탐색하기 위해 다음과 같은 우선 순위로 이동. 더 이상 움직이지 못할 때까지 반복
 * - 남으로 이동 -> 서로 이동 후 남으로 이동 -> 동으로 이동 후 남으로 이동
 * - 가장 남쪽에 도달해 더이상 이동할 수 없으면 정령은 골렘 내에서 상하좌우 인접한 칸으로 이동 가능
 * - 단 현재 위치하고 있는 골렘의 출구가 다른 골렘과 인접하고 있다면 해당 출구를 통해 다른 골렘으로 이동 가능
 * - 정령은 갈 수 있는 모든 칸 중 가장 남쪽의 칸으로 이동하고 종료. 이때 해당 정령의 위치는 최종위치가 됨
 * 9. 정령의 최종 위치의 행번호의 합을 구해야 하기에 정령들의 최종 위치를 누적
 * 10. 최대한 남쪽으로 이동했지만 골렘의 몸 일부가 숲을 벗어난 상태라면, 해당 골렘을 포함해 숲에 위치한 모든 골렘들이 숲을 빠져 나간 뒤 다음 골렘부터 새롭게 숲의 탐색 시작
 * - 단, 이 경우에는 정령이 도달하는 최종 위치를 답에 포함시키지 않는다.
 * -> 골렘들이 숲에 진입함에 따라 각 정령들이 최종적으로 위치한 행의 총합을 구하는 프로그램을 작성하라.
 * 숲이 다시 텅 비게 되도 행의 총합은 누적된다.
 * [조건]
 * 최대 5 x 5 || 70 x 70
 * 정령 최대 1000명
 * [입력]
 * 출발하는 열, 출구 방향 정보
 * [해결 프로세스]
 * 1. 입력 받은 골렘을 남쪽으로 이동
 * 2. 다 내려왔으면 골렘의 최종 x값 계산
 * 3. 또는 출구로 다른 골렘으로 건너가며 최종값 계산
 */
public class Main {
    static int R, C, K;
    static int answer = 0;
    static int[][] board;
    static int[] dx = {-1, 0, 1, 0}; // 위 오 아 왼 (시계)
    static int[] dy = {0, 1, 0, -1};
    static GR[] grs;
    static boolean[] visit;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        board = new int[R + 3][C + 1];
        grs = new GR[K + 1];

        for (int i = 1; i <= K; i++) {
            st = new StringTokenizer(br.readLine());
            int c = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());
            grs[i] = new GR(c, d);
            visit = new boolean[K + 1];
            move(i);
        }

        System.out.println(answer);
    }

    public static void move(int idx) {
        int nx = grs[idx].x;
        int ny = grs[idx].y;
        int nd = grs[idx].d;
        while (true) {
            if (nx == R + 1) break; // 이미 바닥에 도착했으면 정지
            // 아래 체크
            if (checkDownSide(nx, ny)) {
                nx += 1;
                continue;
            }
            // 왼쪽 체크
            if (checkLeftSide(nx, ny)) {
                nx += 1;
                ny -= 1;
                nd = changeDir(nd, -1);
                continue;
            }
            // 오른쪽 체크
            if (checkRightSide(nx, ny)) {
                nx += 1;
                ny += 1;
                nd = changeDir(nd, 1);
                continue;
            }
            break; // 아무 곳도 갈 수 없으면 정지
        }

        if (nx - 1 < 3) {
            // 클리어 시켜야 함
            board = new int[R + 3][C + 1];
        } else {
            enrollBoard(idx, nx, ny);
            grs[idx].x = nx;
            grs[idx].y = ny;
            grs[idx].d = nd;
            answer += calc(idx, 0);
        }

    }

    // 정령의 이동
    // 현재에서 남쪽 방향의 좌표와 출구에서 이동을 통해 움직인 남쪽 좌표와 비교
    // 출구에서 갈 수 있는 골렘의 수는 1개
    public static int calc(int idx, int result) {
        visit[idx] = true;
        result = Math.max(grs[idx].x - 1, result);  // 현재 위치의 골렘 x + 1값과 result 중 최대값 선택
        int[] exit = grs[idx].getExit();

        for (int i = 0; i < 4; i++) {
            int exitX = exit[0] + dx[i];
            int exitY = exit[1] + dy[i];
            if (!(checkRange(exitX, exitY) && exitX > 2)) continue;
            if (board[exitX][exitY] == 0) continue;
            if (visit[board[exitX][exitY]]) continue;

            return calc(board[exitX][exitY], result);
        }
        return result;
    }

    // 아래, 왼, 오 체크
    public static boolean checkRange(int x, int y) {
        return ((x <= R + 2) && (y > 0) && (y <= C));
    }

    public static boolean checkDownSide(int x, int y) {
        if (!checkRange(x + 2, y)) return false;
        return (board[x + 1][y - 1] == 0 && board[x + 2][y] == 0 && board[x + 1][y + 1] == 0);  // true이면 이동 가능
    }

    public static boolean checkLeftSide(int x, int y) {
        if (!checkRange(x, y - 2)) return false;
        if (!checkRange(x + 2, y - 1)) return false;
        return (board[x - 1][y - 1] == 0 && board[x][y - 2] == 0 && board[x + 1][y - 1] == 0 && board[x + 1][y - 2] == 0 && board[x + 2][y - 1] == 0);  // true이면 이동 가능
    }

    public static boolean checkRightSide(int x, int y) {
        if (!checkRange(x, y + 2)) return false;
        if (!checkRange(x + 2, y + 1)) return false;
        return (board[x - 1][y + 1] == 0 && board[x][y + 2] == 0 && board[x + 1][y + 1] == 0 && board[x + 1][y + 2] == 0 && board[x + 2][y + 1] == 0);  // true이면 이동 가능
    }

    public static void enrollBoard(int idx, int x, int y) {
        board[x][y] = idx;
        board[x - 1][y] = idx;
        board[x + 1][y] = idx;
        board[x][y - 1] = idx;
        board[x][y + 1] = idx;
    }

    public static int changeDir(int d, int dir) {
        d += dir;
        if (d == -1) d = 3;
        else if (d == 4) d = 0;
        return d;
    }

    static class GR {
        int x;
        int y;
        int d; // 0 위, 1 오, 2 아, 3 왼

        public GR(int y, int d) {
            this.x = 1;
            this.y = y;
            this.d = d;
        }

        public int[] getExit() {
            return new int[]{x + dx[d], y + dy[d]};
        }
    }
}
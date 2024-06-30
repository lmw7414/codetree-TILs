import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * [문제 설명]
 * - n x m 격자판에서 움직이는 콤팡이 채취
 * - 빨간색으로 표시된 숫자 : 곰팡이 크기를 의미
 * - 파란색으로 표시된 숫자 : 속력을 의미
 * --
 * 1. 첫번째 열부터 탐색(위에서 아래로)
 * 2. 곰팡이 채취 후 해당 칸은 빈칸이 된다.
 * 3. 해당 열의 채취가 완료되면 곰팡이는 이동을 시작
 * 4. 벽에 도달한 곰팡이는 방향을 반대로 바꾸고, 속력은 유지한 채 이동
 * 5. 한 칸에 두마리 이상의 곰팡이가 있을 때는 크기가 큰 곰팡이가 다른 곰팡이를 잡아먹음
 * 6. 오른쪽 열로 이동
 * -> 채취한 곰팡이 크기의 총합을 구하여라
 * [입력]
 * n x m: 격자 크기
 * k : 곰팡이 수
 * <p>
 * [문제 해결 프로세스]
 * 1. 첫번째 열 -> 마지막 열까지 이동할 때까지 프로그램 실행
 * 2. 해당 열의 곰팡이 탐색()
 * 3. 이동 위치를 저장할 배열 생성 후 곰팡이 이동
 * 4. 이미 곰팡이가 존재한다면 크기가 큰 곰팡이가 우선
 * 5. 곰팡이를 저장할 곰팡이 리스트 생성
 */

public class Main {
    static int n, m, k, answer;
    static Mold[] molds;
    static int[][] arr;
    static int[] dx = {-1, 0, 1, 0}; // 상좌하우
    static int[] dy = {0, -1, 0, 1};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        arr = new int[n][m];
        molds = new Mold[k + 1];
        for (int i = 1; i <= k; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            int s = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());
            int b = Integer.parseInt(st.nextToken());
            molds[i] = new Mold(x, y, s, d, b);
            arr[x][y] = i;
        }
        calc();
        System.out.println(answer);

    }

    public static void calc() {
        for (int i = 0; i < m; i++) {
            int moldIdx = findMold(i); // 곰팡이 번호 찾기
            if (moldIdx != -1) { // 곰팡이를 찾은 경우
                answer += molds[moldIdx].b;
                arr[molds[moldIdx].x][molds[moldIdx].y] = 0;
                molds[moldIdx] = null;

            }
            arr = moveMolds(); // 곰팡이 전체 이동
        }
    }

    public static int[][] moveMolds() {
        int[][] moveMap = new int[n][m];
        for (int i = 1; i < molds.length; i++) {
            if (molds[i] == null) continue;
            move(i, moveMap);
        }
        return moveMap;
    }

    public static void move(int moldIdx, int[][] moveMap) {
        Mold mold = molds[moldIdx];
        int moveCnt = mold.s;
        int nx = mold.x;
        int ny = mold.y;
        for (int i = 0; i < moveCnt; i++) {
            nx += dx[mold.d];
            ny += dy[mold.d];
            if (nx < 0 || ny < 0 || nx >= n || ny >= m) {
                // 지금 nx ny는 범위를 벗어난 상태이므로 이전으로 되돌림
                nx -= dx[mold.d];
                ny -= dy[mold.d];
                mold.changeD(); // 방향 전환
                i--;
            }
        }
        if (moveMap[nx][ny] != 0) { // 해당위치에 곰팡이 먼저 존재
            if (mold.b > molds[moveMap[nx][ny]].b) {
                molds[moveMap[nx][ny]] = null;
                moveMap[nx][ny] = moldIdx;
                mold.x = nx;
                mold.y = ny;
                molds[moldIdx] = mold;
            } else {
                molds[moldIdx] = null;
            }
        } else {
            moveMap[nx][ny] = moldIdx;
            mold.x = nx;
            mold.y = ny;
            molds[moldIdx] = mold;
        }

    }

    public static int findMold(int col) {
        for (int i = 0; i < n; i++) {
            if (arr[i][col] != 0) {
                return arr[i][col];
            }
        }
        return -1;
    }

    public static void printArr(int[][] arr) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                System.out.print(arr[i][j] + "\t");
            }
            System.out.println();
        }
    }

    static class Mold {
        int x;
        int y;
        int s; // 속도
        int d; // 방향
        int b; // 크기

        public Mold(int x, int y, int s, int d, int b) {
            if (d == 1) d = 0;
            else if (d == 4) d = 1;

            this.x = x;
            this.y = y;
            if(d % 2 == 0) this.s = s % (2 * (n - 1));
            else this.s = s % (2 * (m - 1));
            this.d = d;
            this.b = b;
        }

        // 방향전환
        public int changeD() {
            int newD = (d + 2) % 4;
            this.d = newD;
            return newD;
        }
    }

}
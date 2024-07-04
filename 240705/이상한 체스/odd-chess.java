import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * [문제 설명]
 * 1번 : 정해진 방향 한방향으로만 이동가능  -> 0,1,2,3 중 하나(경우의 수 4)
 * 2번 : 좌우로 이동 -> {0, 1}, {2, 3} 중 하나 -> 상하 좌우(경우의수 2)
 * 3번 : 상좌, 상우, 하좌, 하우 -> {0,2}, {0,3}, {1,2}, {1,3} -> 상하, 좌우 없음(경우의수 4)
 * 4번 : 상좌우, 좌상하, 하좌우, 우상하 -> {0,2,3}, {2,0,1}, {1,2,3}, {3,0,1} -> 4개중 1개 빼고 다(경우의 수 4)
 * 5번 : 상하좌우 -> {0,1,2,3} -> 경우의 수 1
 * 6번 : 상대 말
 * 1. 각각의 말은 네 방향 중 한가지 방향을 선택할 수 있음
 * 2. 체스판의 말들의 방향을 적절히 설정하여 갈 수 없는 격자의 크기를 최소화하라 -> 최대한 많이 가라
 * 3. 본인 말은 뛰어 넘어갈 수 있다
 * 4. 상대 말은 뛰어 넘어갈 수 없다.
 * [조건]
 * 1 ≤ n, m ≤ 8
 * 자신의 말의 개수는 최대 8개를 넘지 않는다
 * 시간 제한 1초
 * [문제 해결프로세스]
 * 1. 조합을 통해 각 말의 방향을 설정
 * 2. 각 말의 설정이 완료되었다면 visit 배열을 만들어 체크
 * 3. 각 말의 이동방향에 따른 이동 메서드 필요.
 */
public class Main {

    static int N, M;
    static int[][] arr;
    static boolean[][] visit;
    static int[] dx = {-1, 1, 0, 0}; // 상하좌우
    static int[] dy = {0, 0, -1, 1};
    static List<Piece> pieces = new ArrayList<>();
    static int[] piecesDir;
    static int answer = Integer.MAX_VALUE;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        arr = new int[N][M];
        visit = new boolean[N][M];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < M; j++) {
                int num = Integer.parseInt(st.nextToken());
                if (num > 0 && num < 6) {
                    pieces.add(new Piece(i, j, num));
                }
                if (num != 0) visit[i][j] = true;
                arr[i][j] = num;
            }
        }
        piecesDir = new int[pieces.size()];
        DFS(0);
        System.out.println(answer);
    }

    public static void DFS(int depth) {
        if (depth == pieces.size()) {
            boolean[][] copy = copyVisit();
            for (int i = 0; i < piecesDir.length; i++) {
                copy = calc(pieces.get(i), i, copy);
            }
            int ans = 0;
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    if (!copy[i][j]) ans++;
                }
            }
            answer = Math.min(answer, ans);
            return;
        }
        // 체스 번호를 기준으로 방향 등록

        Piece piece = pieces.get(depth);
        if (piece.n == 1) {
            for (int j = 0; j < 4; j++) {
                piecesDir[depth] = j;
                DFS(depth + 1);
            }
        } else if (piece.n == 2) {
            for (int j = 0; j < 2; j++) {
                piecesDir[depth] = j;
                DFS(depth + 1);
            }
        } else if (piece.n == 3) {
            for (int j = 0; j < 4; j++) {
                piecesDir[depth] = j;
                DFS(depth + 1);
            }
        } else if (piece.n == 4) {
            for (int j = 0; j < 4; j++) {
                piecesDir[depth] = j;
                DFS(depth + 1);
            }
        } else if (piece.n == 5) {
            piecesDir[depth] = 0;
            DFS(depth + 1);
        }


    }

    public static boolean[][] calc(Piece piece, int idx, boolean[][] copy) {
        int[] dir = null;
        if (piece.n == 1) dir = one(piecesDir[idx]);
        else if (piece.n == 2) dir = two(piecesDir[idx]);
        else if (piece.n == 3) dir = three(piecesDir[idx]);
        else if (piece.n == 4) dir = four(piecesDir[idx]);
        else if (piece.n == 5) dir = five(piecesDir[idx]);

        for (int d : dir) {
            int nx = piece.x;
            int ny = piece.y;
            while (true) {
                nx += dx[d];
                ny += dy[d];
                if (nx < 0 || ny < 0 || nx >= N || ny >= M || arr[nx][ny] == 6) break; // 범위를 벗어나거나 상대 말인 경우
                copy[nx][ny] = true; // 방문 했으므로 방문체크
            }
        }
        return copy;
    }

    public static boolean[][] copyVisit() {
        boolean[][] copy = new boolean[N][M];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                copy[i][j] = visit[i][j];
            }
        }
        return copy;
    }

    // 경우의 수 4
    public static int[] one(int dir) {
        switch (dir) {
            case 0:
                return new int[]{0};
            case 1:
                return new int[]{1};
            case 2:
                return new int[]{2};
            case 3:
                return new int[]{3};
            default:
                return null;
        }
    }

    // 경우의 수 2
    public static int[] two(int dir) {
        switch (dir) {
            case 0:
                return new int[]{0, 1};
            case 1:
                return new int[]{2, 3};
            default:
                return null;
        }
    }

    // 경우의 수 4
    public static int[] three(int dir) {
        switch (dir) {
            case 0:
                return new int[]{0, 2};
            case 1:
                return new int[]{0, 3};
            case 2:
                return new int[]{1, 2};
            case 3:
                return new int[]{1, 3};
            default:
                return null;
        }
    }

    // 경우의 수 4
    public static int[] four(int dir) {
        switch (dir) {
            case 0:
                return new int[]{0, 1, 2};
            case 1:
                return new int[]{0, 2, 3};
            case 2:
                return new int[]{1, 2, 3};
            case 3:
                return new int[]{0, 1, 3};
            default:
                return null;
        }
    }

    //경우의 수 1
    public static int[] five(int dir) {
        return new int[]{0, 1, 2, 3};
    }

    static class Piece {
        int x;
        int y;
        int n;

        public Piece(int x, int y, int n) {
            this.x = x;
            this.y = y;
            this.n = n;
        }
    }

}
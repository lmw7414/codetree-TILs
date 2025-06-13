
import java.util.*;
import java.io.*;

public class Main {
    static int L, N, Q;
    static int[][] arr, map;
    static Knight[] knights;
    static int[] damage;
    static int[] dx = {-1, 0, 1, 0}; // 상우하좌
    static int[] dy = {0, 1, 0, -1};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        L = Integer.parseInt(st.nextToken()); // 3~40
        N = Integer.parseInt(st.nextToken()); // 1~30
        Q = Integer.parseInt(st.nextToken()); // 1~100
        arr = new int[L + 1][L + 1];
        map = new int[L + 1][L + 1];
        for(int i = 1; i <= L; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 1; j <= L; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        knights = new Knight[N + 1];
        damage = new int[N + 1]; // 누적 대미지 기록
        for(int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            int r, c, h, w, k;
            r = Integer.parseInt(st.nextToken());
            c = Integer.parseInt(st.nextToken());
            h = Integer.parseInt(st.nextToken());
            w = Integer.parseInt(st.nextToken());
            k = Integer.parseInt(st.nextToken());
            knights[i] = new Knight(i, r, c, h, w, k);
            List<Point> pos = getKnightPos(knights[i]);
            for(Point p : pos) map[p.x][p.y] = i;
        }
        //printArr();
        for(int q = 0; q < Q; q++) {
            st = new StringTokenizer(br.readLine());
            int id = Integer.parseInt(st.nextToken());
            int dir = Integer.parseInt(st.nextToken());
            if(knights[id].isDead == 2) continue; // 이미 죽은 기사라면 무시
            if(isMove(id, dir)) {
                move(id, dir, true);
                for(int i = 1; i <= N; i++) {
                    if(knights[i].isDead == 1) {
                        List<Point> pos = getKnightPos(knights[i]);
                        for(Point p : pos) map[p.x][p.y] = 0;
                        knights[i].isDead = 2;
                    } else if(knights[i].isDead == 0) {
                        knights[i].k -= knights[i].damage;
                    }
                    knights[i].damage = 0;
                }
            }
            //printArr();
        }
        int answer = 0;
        for(int i = 1; i  <= N; i++) {
            Knight knight = knights[i];
            //System.out.print(knight);
            if(knight.isDead == 2) continue;
            answer += damage[i];
        }
        System.out.println(answer);
    }

    public static boolean isMove(int id, int dir) {
        List<Point> pos = getKnightPos(knights[id]);
        Set<Integer> set = new HashSet<>();
        for(Point p : pos) {
            int nx = p.x + dx[dir];
            int ny = p.y + dy[dir];
            if(OOB(nx, ny)) return false; // 움직일 수 없음
            if(map[nx][ny] == id) continue;
            if(map[nx][ny] != 0) { // 나 자신이 아니고, 0이 아닌 다른 기사라면
                set.add(map[nx][ny]);
            }
        }
        if(!set.isEmpty()) {
            boolean flag = true;
            for(int next : set) {
                flag = isMove(next, dir);
                if(!flag) return false; // 움직일 수 없다면
            }
        }
        return true;
    }

    public static void move(int id, int dir, boolean ordered) {
        List<Point> pos = getKnightPos(knights[id]);
        Set<Integer> set = new HashSet<>();
        for(Point p : pos) {
            int nx = p.x + dx[dir];
            int ny = p.y + dy[dir];
            if(map[nx][ny] == id) continue;
            if(map[nx][ny] != 0) { // 나 자신이 아니고, 0이 아닌 다른 기사라면
                set.add(map[nx][ny]);
            }
        }
        if(!set.isEmpty()) { // 움직일 수 있는 경우
            for(int next : set) {
                move(next, dir, false);
            }
        }
        
        for(Point p : pos) map[p.x][p.y] = 0; // 현재의 배열 0으로 변경
        // 함정이 있는지 체크
        int cnt = 0;
        for(Point p : pos) {
            int nx = p.x + dx[dir];
            int ny = p.y + dy[dir];
            if(arr[nx][ny] == 1) cnt++;
        }
        // 기사 위치 최신화
        Knight target = knights[id];
        target.r += dx[dir];
        target.c += dy[dir];
        if(!ordered) { // 명령 받은 기사가 아닌 경우
            damage[id] += cnt;
            target.damage = cnt;
            if(target.k <= cnt) { // 충격에 사망하는 경우
                target.isDead = 1;
            }
        }
        for(Point p : pos) {
            int nx = p.x + dx[dir];
            int ny = p.y + dy[dir];
            map[nx][ny] = id;
        }
    }


    public static void printArr() {
        for(int i = 1; i <= L; i++) {
            for(int j = 1; j <= L; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    public static List<Point> getKnightPos(Knight knight) {
        List<Point> pos = new ArrayList<>();
        for(int x = knight.r; x < knight.r + knight.h; x++) {
            for(int y = knight.c; y < knight.c + knight.w; y++) {
                pos.add(new Point(x, y));
            }
        }
        return pos;
    }

    public static boolean OOB(int x, int y) {
        return x <= 0 || y <= 0 || x > L || y > L || arr[x][y] == 2;
    }

    static class Point {
        int x, y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Knight {
        int id;
        int r, c, h, w, k;
        int damage;
        int isDead; // 0 생존 1 이제 죽을 예정 // 2 죽음
        public Knight(int id, int r, int c, int h, int w, int k) {
            this.id = id;
            this.r = r;
            this.c = c;
            this.h = h;
            this.w = w;
            this.k = k;
            damage = 0;
            isDead = 0;
        }

        @Override
        public String toString() {
            return String.format("[id: %d, r: %d, c: %d, k: %d, damage: %d, isDead: %s]\n", id, r, c, k, damage, isDead);
        }
    }
}
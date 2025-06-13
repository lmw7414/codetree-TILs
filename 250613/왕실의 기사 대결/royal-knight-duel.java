
import java.util.*;
import java.io.*;

/*
왕실의 기사는 (r,c)를 기준으로 (r+h, c+w)까지의 직사각형 형태를 띄고 있음
각 기사의 체력은 K로 주어짐
1. 기사 이동
 - 왕에게 명령을 받은 기사는 상하좌우 중 하나로 한 칸 이동 가능
 - 만양 이동하려는 위치에 다른 기사가 있다면 그 기사도 연쇄적으로 한칸 밀려남
 - 마지막 기사부터 이동 시켜야하고, 마지막 기사마저 움직일 수 없다면 모두 움직일 수 없음
 - 체스판에 사라진 기사는 명령할 수 없음
2. 대결 대미지
 - 명령을 받은 기사가 다른 기사를 밀치게 되면, 밀려난 기사들은 피해를 입게 됨.
 - 해당 기사가 이동한 곳에서 w X h 직사각형 내에 놓여 있는 함정의 수만큼만 피해를 입음
 - 체력 이상의 피해를 받으면 사라짐
 - 명령을 받는 기사는 피해를 입지 않음
 - 기사들은 모두 밀린 이후에 대미지를 입게 됨
 - 밀렸더라도 밀쳐지 위치에 함정이 전혀 없다면 그 기사는 피해를 전혀 입지 않게 됨
*/

public class Main {
    static int L, N, Q;
    static int[][] arr, map;
    static Knight[] knights;
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
            if(knights[id].isDead) continue; // 이미 죽은 기사라면 무시
            if(isMove(id, dir)) {
                move(id, dir, true);
                //printArr();
            }
        }
        int answer = 0;
        for(int i = 1; i  <= N; i++) {
            Knight knight = knights[i];
            if(knight.isDead) continue;
            answer += knight.damage;
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
        if(set.isEmpty()) { // 움직일 수 있는 경우
            return true;
        } else { // 다음 기사가 존재하는 경우
            // System.out.print(id + " 다음 존재 ");
            // for(int next : set) System.out.print(next + ", ");
            // System.out.println();
            boolean flag = true;
            for(int next : set) {
                flag = isMove(next, dir);
                if(!flag) return false; // 움직일 수 없다면
            }
            return true;
        }
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
        if(set.isEmpty()) { // 움직일 수 있는 경우
            // 현재의 배열 0으로 변경
            for(Point p : pos) map[p.x][p.y] = 0;
            // 함정이 있는지 체크
            int cnt = 0;
            for(Point p : pos) {
                int nx = p.x + dx[dir];
                int ny = p.y + dy[dir];
                if(arr[nx][ny] == 1) cnt++;
            }
            Knight target = knights[id];
            target.r = pos.get(0).x + dx[dir];
            target.c = pos.get(0).y + dy[dir];
            if(!ordered) {
                if(target.k <= cnt) { // 충격에 사망하는 경우
                    target.k -= cnt;
                    target.isDead = true;
                    return;
                } else {
                    target.k -= cnt;
                    target.damage += cnt; // 대미지 누적
                }
            }
            for(Point p : pos) {
                int nx = p.x + dx[dir];
                int ny = p.y + dy[dir];
                map[nx][ny] = id;
            }
            //System.out.printf("%d 이동, 피해: %d\n", id, cnt);
        } else { // 다음 기사가 존재하는 경우
            for(int next : set) {
                move(next, dir, false);
            }
        }
    }

    public static void printArr() {
        for(int i = 1; i <= L; i++) {
            for(int j = 1; j <= L; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }


    // public static boolean move(int id, int dir, boolean ordered) {
    //     List<Point> pos = getKnightPos(knights[id]);
    //     Set<Integer> set = new HashSet<>();
    //     for(Point p : pos) {
    //         int nx = p.x + dx[dir];
    //         int ny = p.y + dy[dir];
    //         if(OOB(nx, ny)) return false; // 움직일 수 없음
    //         if(map[nx][ny] != 0 && map[nx][ny] != id) { // 나 자신이 아니고, 0이 아닌 다른 기사라면
    //             set.add(map[nx][ny]);
    //         }
    //     }
    //     if(set.isEmpty()) { // 움직일 수 있는 경우
    //         // 현재의 배열 0으로 변경
    //         for(Point p : pos) map[p.x][p.y] = 0;
    //         // 함정이 있는지 체크
    //         int cnt = 0;
    //         for(Point p : pos) {
    //             int nx = p.x + dx[dir];
    //             int ny = p.y + dy[dir];
    //             if(arr[nx][ny] == 1) cnt++;
    //         }
    //         Knight target = knights[id];
    //         target.r = pos.get(0).x + dx[dir];
    //         target.c = pos.get(0).y + dy[dir];
    //         if(!ordered) {
    //             if(target.k <= cnt) { // 충격에 사망하는 경우
    //                 target.k -= cnt;
    //                 target.isDead = true;
    //                 return true;
    //             } else {
    //                 target.k -= cnt;
    //                 target.damage += cnt; // 대미지 누적
    //             }
    //         }
    //         for(Point p : pos) {
    //             int nx = p.x + dx[dir];
    //             int ny = p.y + dy[dir];
    //             map[nx][ny] = id;
    //         }
    //         System.out.printf("%d 이동, 피해: %d\n", id, cnt);
    //         return true;
    //     } else { // 다음 기사가 존재하는 경우
    //         for(int next : set) {
    //             move(next, dir, false);
    //             if(!isMove) return false; // 움직일 수 없다면
    //         }
    //         return true;
    //     }
    // }

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
        boolean isDead;
        public Knight(int id, int r, int c, int h, int w, int k) {
            this.id = id;
            this.r = r;
            this.c = c;
            this.h = h;
            this.w = w;
            this.k = k;
            damage = 0;
            isDead = false;
        }
    }
}
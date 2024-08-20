import java.util.*;
import java.io.*;
/**
 기절한 산타, 격자 밖의 산타는 움직일 수 없음
 1) 루돌프의 움직임
 - 가장 가까운 산타를 향해 1칸 돌진(게임에서 탈락하지 않은 산타)
 - 산타의 우선순위는 R좌표가 큼, R이 같다면 C가 큰 산타
 - 8방향으로 이동 가능하고, 가장 가까운 산타로 한 칸 돌진
 2) 산타의 움직임
 - 루돌프에게 거리가 가장 가까워지는 방향으로 1칸 이동
 - 산타와 동일한 위치에는 갈 수 없고, 나갈 수 없음
 - 움직일 수 있는 칸이 없으면 정지
 - 움직일 수 있어도 루돌프로부터 멀어진다면 정지
 - 상우하좌(0,2,4,6)
 3) 충돌
 - 루돌프가 움직여서 충돌이 발생한 경우
 - 해당 상타는 C만큼의 점수를 얻게 됨
 - 산타는 자신이 이동해온 반대 방향으로 C칸 만큼 밀려나게 됨
 - 밀려나는 것은 포물선 모양을 그리며 밀려나는 것이기 때문에 이동하는 도중 충돌이 일어나지 않음
 - 밀려난 위치가 밖이라면 산타는 탈락
 - 밀려난 칸에 산타가 있는 경우 상호작용 발생
 - 산타가 움직여서 충돌이 발생한 경우
 - 해당 산타는 D만큼의 점수를 얻게 됨
 - 산타는 자신이 이동해온 반대 방향으로 D칸 만큼 뒤로 밀리남
 4) 상호작용
 - 산타는 충돌 후 해당 위치에 산타가 있다면 해당 산타는 같은 방향으로 한칸 밀려나감
 - 게임 밖으로 나갈 경우 탈락
 5) 기절
 - 산타는 루돌프와 충돌 후 기절
 - 한 턴 쉼
 - 기절한 도중 충돌이나 상호작용으로 인해 밀려날 수 있음
 - 루돌프는 기절한 산타를 돌진 대상으로 선택할 수 있음
 6) 게임 종료
 - P명의 산타가 모두 탈락한다면 게임 종료
 - 매턴 이후 아직 탈락하지 않은 산타에게는 1점 추가 부여
 */

public class Main {
    static int N, M, P, C, D;
    static int[] dx = {-1,-1, 0, 1, 1,  1,  0, -1}; // 8방향 상 우상 우 우하 우 우좌 좌 좌상
    static int[] dy = { 0, 1, 1, 1, 0, -1, -1, -1};
    static Point rudolf;
    static Santa[] santas;
    static int[][] arr;  // 100번 루돌프 // 인덱스 123... 산타
    static int[] tArr; // 기절한 턴 계산

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        arr = new int[N + 1][N + 1];
        M = Integer.parseInt(st.nextToken()); // 턴 수
        P = Integer.parseInt(st.nextToken()); // P명의 산타
        santas = new Santa[P];
        tArr = new int[P + 1];
        C = Integer.parseInt(st.nextToken());
        D = Integer.parseInt(st.nextToken());

        // rudolf
        st = new StringTokenizer(br.readLine());
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        rudolf = new Point(x, y);
        arr[x][y] = 100;

        //santa
        for(int i = 0; i < P; i++) {
            st = new StringTokenizer(br.readLine());
            int idx = Integer.parseInt(st.nextToken());
            x = Integer.parseInt(st.nextToken());
            y = Integer.parseInt(st.nextToken());
            santas[i] = new Santa(idx, x, y);
            arr[x][y] = idx;
        }
        Arrays.sort(santas, (a1, b1) -> a1.idx - b1.idx);

        simulate();
        printScore();

    }

    public static void simulate() {

        for (int turn = 1; turn <= M; turn++) {
            for(int i = 1; i <= P; i++) {
                if(tArr[i] != 0 && santas[i-1].status != -1) {
                    tArr[i]--;
                    if(tArr[i] == 0) {
                        santas[i-1].status = 0;
                    }
                }
            }
            // 1. rudolf's turn
            moveRudolf(findClosestSanta());
            // 2. santas' turn
            moveSantas();
            // 3. end Of Turn
            boolean flag = true;  // true : all santa is dead
            for(Santa santa : santas) {
                if(santa.status != -1) { // 생존
                    santa.score += 1;
                    flag = false;
                }
            }
            if(flag) break;
            // System.out.println("turn : " + turn);
            // printArr();
            // printScore();
        }
    }

    /*
     santa : 이동하려는 산타
     move : 이동해야하는 거리
     d : 이동해야하는 방향
     */
    public static void collision(Santa santa, int move, int d) {
        int nx = santa.point.x + (move * dx[d]);
        int ny = santa.point.y + (move * dy[d]);
        if(isOutOfRange(nx, ny)) {
            arr[santa.point.x][santa.point.y] = 0;
            santa.status = -1; // santa die
            return;
        }

        if(arr[nx][ny] != 0) {  // if exist santa
            collision(santas[arr[nx][ny] - 1], 1, d);
        }

        arr[santa.point.x][santa.point.y] = 0;
        arr[nx][ny] = santa.idx;
        santa.point.x = nx;
        santa.point.y = ny;

    }

    public static void moveRudolf(Santa santa) {
        // if(santa == null) return;
        int minDist = Integer.MAX_VALUE;
        int bestX = -1;
        int bestY = -1;
        for(int d = 0; d < 8; d++) {
            int nx = rudolf.x + dx[d];
            int ny = rudolf.y + dy[d];
            if(isOutOfRange(nx, ny))continue;
            if(arr[nx][ny] == santa.idx) {  // 산타와 충돌
                santa.status = 1;
                tArr[santa.idx] += 2;
                santa.score += C;
                collision(santa, C, d);
                bestX = nx;
                bestY = ny;
                break;
            }
            int dist = getDistance(new Point(nx, ny), santa.point);
            if(minDist > dist) {
                bestX = nx;
                bestY = ny;
                minDist = dist;
            }
        }

        // change Rudolf Pos
        arr[rudolf.x][rudolf.y] = 0;
        arr[bestX][bestY] = 100;
        rudolf.x = bestX;
        rudolf.y = bestY;
    }

    public static Santa findClosestSanta() {
        int minDist = Integer.MAX_VALUE;
        Santa closest = null;
        for(Santa santa : santas) {
            if(santa.status == -1) continue;
            int dist = getDistance(rudolf, santa.point);
            if(minDist >= dist) {
                if(minDist == dist) {
                    if(closest.point.x <= santa.point.x) {
                        if(closest.point.x == santa.point.x) {
                            if(closest.point.y < santa.point.y) {
                                closest = santa;
                                minDist = dist;
                            }
                        } else {
                            closest = santa;
                            minDist = dist;
                        }
                    }
                } else {
                    closest = santa;
                    minDist = dist;
                }
            }
        }
        return closest;
    }


    /*
    1. 모든 산타 이동
    2. 충돌이 있다면 튕겨나가야 함
    */
    public static void moveSantas() {
        for(Santa santa : santas) {
            if(santa.status == 0) {
                int bestX = santa.point.x;
                int bestY = santa.point.y;
                int minDist = getDistance(rudolf, new Point(bestX, bestY));
                boolean isCollision = false;
                for(int d = 0; d < 8; d+=2) {
                    int nx = santa.point.x + dx[d];
                    int ny = santa.point.y + dy[d];

                    if(isOutOfRange(nx, ny))continue;
                    if(arr[nx][ny] == 100) { // collision
                        isCollision = true;
                        santa.status = 1;
                        tArr[santa.idx] += 2;
                        collision(santa, D - 1, (d + 4) % 8);
                        santa.score += D;
                        break;
                    }
                    if(arr[nx][ny] != 0) continue; // 해당위치에 산타 존재
                    int dist = getDistance(rudolf, new Point(nx, ny));
                    if(minDist > dist) {
                        minDist = dist;
                        bestX = nx;
                        bestY = ny;
                    }
                }
                if(!isCollision) {
                    arr[santa.point.x][santa.point.y] = 0;
                    arr[bestX][bestY] = santa.idx;
                    santa.point.x = bestX;
                    santa.point.y = bestY;
                }
            }
        }
    }

    public static int getDistance(Point p1, Point p2) {
        return (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);
    }

    public static boolean isOutOfRange(int x, int y) {
        return x < 1 || y < 1 || x > N || y > N;
    }

    static class Point {
        int x, y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void printArr() {
        for(int i = 1; i <= N; i++) {
            for(int j = 1; j <= N; j++) {
                System.out.print(arr[i][j] + "\t");
            }
            System.out.println("\n");
        }
    }

    public static void printScore() {
        for(Santa santa : santas) {
            System.out.print(santa.score + " ");
        }
        System.out.println();
    }

    static class Santa {
        int idx;
        Point point;
        int status; // -1 사망 0 정상 1 기절
        int score;

        public Santa(int idx, int x, int y) {
            this.idx = idx;
            this.point = new Point(x, y);
            this.status = 0;
            this.score = score;
        }
    }
}
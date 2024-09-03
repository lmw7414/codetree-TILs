import java.util.*;
import java.io.*;

/*
- 1분마다 사람이 움직임 -> 편의점으로 이동
1. 격자에 있는 사람들 최단거리로 1칸 움직임
2. 편의점에 도착 시, 편의점에 멈추고, 다른 사람들은 해당 편의점이 있는 칸을 지날 수 없음(아마 경로 재설정?)
  - 격자에 있는 사람들이 모두 이동한 뒤에 해당 칸을 지나갈 수 없음
3. t번 사람은 자신이 가고 싶은 편의점과 가장 가까이에 있는 베이스캠프에 들어감
 - 행이 작은 베이스 캠프, 같다면 열이 작은 베이스 캠프
 - 다른 사람들은 해당 베이스 캠프에 지나갈 수 없음
*/

public class Main {
    static int N, M;
    static int[] dx ={-1, 0, 0, 1}; // 상 좌 우 하
    static int[] dy = {0, -1, 1, 0};
    static boolean[][] visit;
    static int[][] arr;
    static Obj[] people;
    static List<Obj> camps = new ArrayList<>();
    static Obj[] convenis;
    static Queue<Obj> temp = new ArrayDeque<>(); // 임시 좌표 -> 다음 턴 이동 불가능한 곳 담는 큐
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        visit = new boolean[N][N];
        arr = new int[N][N];

        people = new Obj[M + 1];
        convenis = new Obj[M + 1]; 

        for(int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < N; j++) {
                int num = Integer.parseInt(st.nextToken());
                arr[i][j] = num;
                if(num == 1) {
                    camps.add(new Obj(i, j, 0));
                }
            }
        }
        // 편의점, 사람 생성
        for(int m = 1; m <= M; m++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            convenis[m] = new Obj(x, y, 0);
            people[m] = new Obj(-1, -1, -1);
        }

        int time = 0;

        while(true) {
            time++;
            temp.clear();
            // 사람이동
            for(int i = 1; i <= M; i++) {
                if(i > time) break;
                Obj human = people[i];
                if(human.status == -1) {  // 출발 전
                    // 베이스 캠프로 이동
                    Obj camp = findBaseCamp(convenis[i]);
                    temp.add(camp);
                    human.status = 0;
                    human.x = camp.x;
                    human.y = camp.y;
                } else if(human.status == 0) { // 이동 중
                    move(i);
                } else continue; // 이미 편의점 도착
            }


            if(allArrived()) break; // 모든 사람 도착
            // 이동 불가로 변경
            while(!temp.isEmpty()) {
                Obj cur = temp.poll();
                visit[cur.x][cur.y] = true;
                cur.status = 1;
                
            }
        }
        System.out.println(time);
    }

    // 내가 가려는 편의점과 가장 가까운 베이스 캠프 찾기
    public static Obj findBaseCamp(Obj conveni) {
        Obj best = null;
        int[][] map = BFS(conveni.x, conveni.y);
        int min = Integer.MAX_VALUE;
        for(Obj camp : camps) {
            if(camp.status == 1) continue;
            int dist = map[camp.x][camp.y];
            if(min > dist) {
                best = camp;
                min = dist;
            }
        }
        return best;
    }


    // 특정 지점으로 부터 근처 거리 계산 후 BFS맵 전달
    public static int[][] BFS(int x, int y) {
        int[][] map = new int[N][N];
        for(int i = 0; i < N; i++) {
            Arrays.fill(map[i], Integer.MAX_VALUE);
        }            
        map[x][y] = 0;

        Queue<Point> queue = new ArrayDeque<>();
        queue.add(new Point(x, y));

        while(!queue.isEmpty()) {
            Point cur = queue.poll();
            if(arr[cur.x][cur.y] == 1) break;
            for(int d = 0; d < 4; d++) {
                int nx = cur.x + dx[d];
                int ny = cur.y + dy[d];
                if(nx < 0 || ny < 0 || nx >= N || ny >= N) continue;
                if(visit[nx][ny]) continue;
                if(map[nx][ny] < map[cur.x][cur.y] + 1) continue;

                map[nx][ny] = map[cur.x][cur.y] + 1;
                queue.add(new Point(nx, ny));
            }
        }
        return map;
    }

    // 사람이동
    public static void move(int idx) {
        int[][] map = new int[N][N];
        for(int i = 0; i < N; i++) Arrays.fill(map[i], Integer.MAX_VALUE);
        Obj human = people[idx];
        Obj conveni = convenis[idx];
        map[human.x][human.y] = 0;

        Queue<Point> queue = new ArrayDeque<>();
        queue.add(new Point(human.x, human.y));

        while(!queue.isEmpty()) {
            Point cur = queue.poll();
            if(cur.x == conveni.x && cur.y == conveni.y) break; // 해당 편의점 발견

            for(int d = 0; d < 4; d++) {
                int nx = cur.x + dx[d];
                int ny = cur.y + dy[d];
                if(nx < 0 || ny < 0 || nx >= N || ny >= N) continue;
                if(visit[nx][ny]) continue;
                if(map[nx][ny] < map[cur.x][cur.y] + 1) continue;

                map[nx][ny] = map[cur.x][cur.y] + 1;
                queue.add(new Point(nx, ny));
            }
        }
        int dist = map[conveni.x][conveni.y];
        // 만약 거리가 1이라면 편의점 도착
        if(dist == 1) {
            human.x = conveni.x;
            human.y = conveni.y;
            human.status = 1;
            temp.add(conveni);
            return;
        } 

        // 이동
        Point cur = new Point(convenis[idx].x, convenis[idx].y);        
        for(int d = 0; d < 4; d++) {
            int nx = cur.x + dx[d];
            int ny = cur.y + dy[d];
            if(nx < 0 || ny < 0 || nx >= N || ny >= N) continue;
            if(map[nx][ny] == dist - 1) {
                if(nx == human.x && ny == human.y) break;
                cur = new Point(nx, ny);
                dist--;
                d = -1;
            }
        }
        human.x = cur.x;
        human.y = cur.y;
    }

    // 모든 사람 도착
    public static boolean allArrived() {
        for(int i = 1; i <= M; i++) {
            if(people[i].status != 1) return false;
        }
        return true;
    }

    static class Point {
        int x, y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    static class Obj {
        int x, y;
        int status; // 인간 :  출발전 -1, 이동중 0, 도착 1 / 건물 : 공터 0, 폐쇄 1

        public Obj(int x, int y, int status) {
            this.x = x;
            this.y = y;
            this.status = status;
        }
    }
}
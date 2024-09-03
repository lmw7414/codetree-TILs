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
    static Human[] people;
    static Point[] convenis;
    static Queue<Point> temp = new ArrayDeque<>(); // 임시 좌표 -> 다음 턴 이동 불가능한 곳 담는 큐

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        
        visit = new boolean[N][N];
        arr = new int[N][N];
        people = new Human[M + 1];
        convenis = new Point[M + 1]; 

        for(int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < N; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        // 편의점, 사람 생성
        for(int m = 1; m <= M; m++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            convenis[m] = new Point(x, y);
            people[m] = new Human(-1, -1);
        }

        int time = 0;

        while(true) {
            time++;
            // System.out.println(time + "turn----------------");
            // 1. 격자 안 사람이동
            for(int i = 1; i <= M; i++) {
                Human human = people[i];
                if(human.status == 0) { // 이동 중
                    move(i);
                }
                // System.out.println("human" + i + " : " + human.x + " " + human.y);
            }


            //2. 격자 밖 사람 이동
            if(time <= M) {
                // 베이스 캠프로 이동
                Human human = people[time];
                Point camp = findBaseCamp(convenis[time]);
                temp.add(camp);
                human.status = 0;
                human.x = camp.x;
                human.y = camp.y;
                move(time);
            }
            
            // 이동 불가로 변경
            while(!temp.isEmpty()) {
                Point cur = temp.poll();
                visit[cur.x][cur.y] = true;                
            }
            if(allArrived()) break; // 모든 사람 도착
        }
        System.out.println(time + 1);
    }

    // 내가 가려는 편의점과 가장 가까운 베이스 캠프 찾기
    public static Point findBaseCamp(Point conveni) {
        int[][] map = new int[N][N];
        for(int i = 0; i < N; i++) {
            Arrays.fill(map[i], Integer.MAX_VALUE);
        }            
        map[conveni.x][conveni.y] = 0;

        Queue<Point> queue = new ArrayDeque<>();
        List<Point> camps = new ArrayList<>();
        queue.add(new Point(conveni.x, conveni.y));

        while(!queue.isEmpty()) {
            Point cur = queue.poll();
            if(arr[cur.x][cur.y] == 1) break;
            for(int d = 0; d < 4; d++) {
                int nx = cur.x + dx[d];
                int ny = cur.y + dy[d];
                if(nx < 0 || ny < 0 || nx >= N || ny >= N) continue;
                if(visit[nx][ny]) continue;
                if(map[nx][ny] < map[cur.x][cur.y] + 1) continue;
                if(arr[nx][ny] == 1) camps.add(new Point(nx, ny));
                map[nx][ny] = map[cur.x][cur.y] + 1;
                queue.add(new Point(nx, ny));
            }
        }
        // printArr(map);
        Point best = null;
        for(Point camp : camps) {
            if(best == null) best = camp;
            else if(map[best.x][best.y] >= map[camp.x][camp.y]){
                best = best.isBest(camp);
            }
        }
        return best;
    }

    // 이동 경로 생성
    public static Stack<Point> newWay(int idx) {
        int[][] map = new int[N][N];
        for(int i = 0; i < N; i++) Arrays.fill(map[i], Integer.MAX_VALUE);
        Human human = people[idx];
        Point conveni = convenis[idx];
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

        // 이동
        Stack<Point> way = new Stack<>();
        way.push(new Point(convenis[idx].x, convenis[idx].y));
        Point cur = new Point(convenis[idx].x, convenis[idx].y);
             
        for(int d = 0; d < 4; d++) {
            int nx = cur.x + dx[d];
            int ny = cur.y + dy[d];
            if(nx < 0 || ny < 0 || nx >= N || ny >= N) continue;
            if(map[nx][ny] == dist - 1) {
                if(map[nx][ny] == 0) break;
                cur = new Point(nx, ny);
                way.push(new Point(nx, ny));
                dist--;
                d = -1;
            }
        }
        // System.out.print(idx +" : ");
        // for(Point next : way) {
        //     System.out.print(next.x + " " + next.y + "->");
        // }
        // System.out.println();
        return way;
    }

    // 사람이동
    public static void move(int idx) {
        Human human = people[idx];
        if(human.way == null || !isSafeWay(human.way)) {
            human.way = newWay(idx);
        }
        Point next = human.way.pop();
        human.x = next.x;
        human.y = next.y;
        if(next.x == convenis[idx].x && next.y == convenis[idx].y) { // 다음 경로가 편의점
            human.status = 1;
            temp.add(next);
        }
    }
    public static boolean isSafeWay(Stack<Point> way) {
        for(Point next : way) {
            if(visit[next.x][next.y]) return false; // 가려는 경로에 길이 막혔다면            
        }
        return true;
    }

    // 모든 사람 도착
    public static boolean allArrived() {
        for(int i = 1; i <= M; i++) {
            if(people[i].status != 1) return false;
        }
        return true;
    }

    public static void printArr(int[][] arr) {
        System.out.println();
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                if(arr[i][j] == Integer.MAX_VALUE) System.out.print("INF" + "\t");
                else System.out.print(arr[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    static class Point {
        int x, y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point isBest(Point o) {
            if(this.x == o.x) return this.y < o.y ? this : o;
            return this.x < o.x ? this : o;
        }
    }
    static class Human {
        int x, y;
        int status; // 출발전 -1, 이동중 0, 도착 1
        Stack<Point> way;

        public Human(int x, int y) {
            this.x = x;
            this.y = y;
            status = -1;
        }

    }
}
import java.util.*;
import java.io.*;

/*
1. 현재방향 기준 왼쪽으로 이동한 적이 없을 시 좌회전 후 1칸 전진
2. 인도이거나 이미 방문한 경우 좌로 회전 반복
3. 모든 방향에 대해서 방문한 경우 현재 방향을 유지한 채 한칸 후진(후진 불가하다면 작동 중지)
-> 자율 주행 자동차의 작동이 멈췄을 때의 거쳐갔던 면적 계산하기

[입력]
도로 0 인도 1
*/

public class Main {
    static int N, M;
    static int[] dir = {0, 1, 2, 3}; //북(위) 동(오) 남(아래) 서(왼) -> 위 왼 아래 오
    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};
    static int[][] arr;
    static boolean [][] visit;
    static int answer = 1;
    static Point car;
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        arr = new int[N][M];
        visit = new boolean[N][M];

        st = new StringTokenizer(br.readLine());
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        int dir = Integer.parseInt(st.nextToken());
        
        car = new Point(x, y, dir);  // 자동차 초기 위치
        visit[x][y] = true; // 처음위치 방문 체크

        for(int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < M; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        System.out.println(move());

    }

    public static int move() {
        int dCnt = 0;
        while(true) {
            if(dCnt == 4) {
                // 후진 가능하다면 dCnt 0, answer++ 변경 후 이동
                int nx = car.x + dx[(car.dir + 2) % 4];
                int ny = car.y + dy[(car.dir + 2) % 4];
                if(arr[nx][ny] != 1) {
                    dCnt = 0;
                    car.x = nx;
                    car.y = ny;
                    continue;
                } else {
                    return answer;
                } 
            }

            int nd = nextDir(car.dir);
            int nx = car.x + dx[nd];
            int ny = car.y + dy[nd];

            if(arr[nx][ny] != 1 && !visit[nx][ny]) { // 다음 위치가 방문한 적 없고, 인도도 아닌경우
                dCnt = 0;
                answer++;
                car.x = nx;
                car.y = ny;
                car.dir = nd;
                visit[nx][ny] = true;
            } else {
                dCnt++;
                car.dir = nd;
            }
            
            
        }
    }

    public static int nextDir(int dir) {
        dir -= 1;
        if(dir == -1) return 3;
        return dir;
    }

    static class Point {
        int x;
        int y;
        int dir;
        public Point(int x, int y, int dir) {
            this.x = x;
            this.y = y;
            this.dir = dir;
        }
    }
}
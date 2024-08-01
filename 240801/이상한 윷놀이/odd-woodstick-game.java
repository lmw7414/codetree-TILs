import java.util.*;
import java.io.*;
/*
n x n 격자판
격자판은 흰색, 빨간색, 파란색 중 하나의 색을 가지고 있음.
말은 총 k개
이동방향 또한 정해져 있음

1번~k번이 다 이동하면 한턴
흰색 : 그냥 이동
빨간색 : 빨간색으로 가기 전 순서 뒤집기
파란색 : 이동하려는 말만 방향 전환 후 해당 방향으로 이동. 이동이 불가능하면 방향만 전환 후 그대로 있음
범위 벗어나는 경우 -> 파란색처럼 생각
쌓여있는 말 : 본인 위에 있는 말과 전부 이동

종료 조건
- 말이 4개 이상 겹치는 경우
-> 게임이 종료되는 순간의 턴의 번호를 구하는 프로그램
[입력]
0 흰색, 1 빨강, 2 파랑
d 1 오 2 왼 3 위 4 아
*/
public class Main {
    static int N, K;
    static int[][] arr;  // 격자판의 색 저장
    static List[][] board; // 말의 번호를 저장하기 위함
    static List<Point> points = new ArrayList<>();
    static int[] dx = {0, 0, 0, -1, 1}; // 오 왼 위 아
    static int[] dy = {0, 1, -1, 0, 0};
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        board = new List[N+2][N+2];
        for(int i = 1; i<= N; i++) {
            for(int j = 1; j <= N; j++) {
                board[i][j] = new ArrayList<>();
            }
        }
        arr = new int[N+2][N+2];
        for(int i = 0; i< N + 2; i++) Arrays.fill(arr[i], 2);
        for(int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 1; j <= N; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        for(int k = 0 ; k < K; k++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            int d = Integer.parseInt(st.nextToken());
            board[x][y].add(k);
            points.add(new Point(x, y, d));
        }

        System.out.println(calc());
    }

    // 답이 1000보다 크거나 불가능한 경우 -1
    public static int calc() {
        int turn = 1;
        while(true) {
            if(turn > 1000) return -1;
            for(int idx = 0; idx < points.size(); idx++) {
                Point cur = points.get(idx);
                if(board[cur.x][cur.y].size() >= 4) return turn; // 4개 이상 체크
                move(board[cur.x][cur.y], idx, cur.x + dx[cur.d], cur.y + dy[cur.d]);
            }
            if(board[points.get(points.size()-1).x][points.get(points.size()-1).y].size() >= 4) return turn; // 4개 이상 체크
            turn++;
        }
    }
    // 그냥 이동
    public static void white(List<Integer> oldList, int idx, int x, int y) {
        List<Integer> temp = new ArrayList<>();  // 새로 이동할 말 배열
        boolean flag = false;
        for(int i = 0; i < oldList.size(); i++) {
            if(oldList.get(i) == idx) flag = true;
            if(flag) temp.add(oldList.get(i));
        }    
        for(int moveIdx : temp) {
            oldList.remove(Integer.valueOf(moveIdx));
            board[x][y].add(moveIdx);
            points.get(moveIdx).x = x;
            points.get(moveIdx).y = y;
        }
    }

    public static void red(List<Integer> oldList, int idx, int x, int y) {
        List<Integer> temp = new ArrayList<>();
        boolean flag = false;
        for(int i = 0; i < oldList.size(); i++) {
            if(oldList.get(i) == idx) flag = true;
            if(flag) temp.add(oldList.get(i));
        }
        temp = reverse(temp);    
        for(int moveIdx : temp) {
            oldList.remove(Integer.valueOf(moveIdx));
            board[x][y].add(moveIdx);
            points.get(moveIdx).x = x;
            points.get(moveIdx).y = y;
        }
    }

    /**
    * oldList : 기존 위치의 배열
    * idx : 현재 이동하려는 말의 idx
    * x, y : 다음에 이동할 좌표
    */
    public static void move(List<Integer> oldList, int idx, int x, int y) {
        int color = arr[x][y];
        if(color == 0) { // 흰색
            white(oldList, idx, x, y);
        } else if(color == 1) { // 빨간색
            red(oldList, idx, x, y);
        } else { // 파란색
        Point cur = points.get(idx);
        cur.d = changeDir(cur.d); // 방향 변경
        int nextPos = arr[cur.x + dx[cur.d]][cur.y + dy[cur.d]]; // 다음 갈 위치 확인
        if(nextPos == 2) return; // 파란색인 경우 정지
        move(board[cur.x][cur.y], idx, cur.x + dx[cur.d], cur.y + dy[cur.d]); // 파란색이 아닌 경우 이동
        }
    }

    public static List<Integer> reverse(List<Integer> list) {
        List<Integer> temp = new ArrayList<>();
        for(int i = list.size() - 1; i >= 0; i--) {
            temp.add(list.get(i));
        }
        return temp;
    }

    // 방향 전환
    public static int changeDir(int curDir) {
        if(curDir == 1 || curDir == 2) {
            if(curDir == 1) return 2;
            else return 1;
        } else {
            if(curDir == 3) return 4;
            else return 3;
        }
    }

    static class Point {
        int x;
        int y;
        int d;
        public Point(int x, int y, int d) {
            this.x = x;
            this.y = y;
            this.d = d;
        }
    }
}
import java.util.*;
import java.io.*;
/*
[문제 해결 프로세스]
1. 병원 중 m개를 선택하는 경우
-> m개의 병원이 남았을 때 사람들과 병원간의 거리
*/
public class Main {
    static int N, M;
    static List<Point> humans = new ArrayList<>();
    static List<Point> hospitals = new ArrayList<>();
    static int[] choose;
    static int answer = Integer.MAX_VALUE;

    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        for(int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < N; j++) {
                int num = Integer.parseInt(st.nextToken());
                if(num == 1) {
                    humans.add(new Point(i, j));
                }else if(num == 2) {
                    hospitals.add(new Point(i, j));
                }
            }
        }

        choose = new int[M];
        permutation(0, 0);
        System.out.println(answer);        
    }

    public static void permutation(int depth, int idx) {
        if(depth == M) {
            calc();
            return;
        }
        for(int i = idx; i < hospitals.size(); i++) {
            choose[depth] = i;
            permutation(depth + 1, i + 1);
        }
    }
    public static void calc() {
        int result = 0;
        for(Point human : humans) {
            int min = Integer.MAX_VALUE;
            for(int idx : choose) {
                Point hospital = hospitals.get(idx);
                int dist = getDist(human.x, human.y, hospital.x, hospital.y);
                min = Math.min(min, dist);
            }
            result += min;
        }
        answer = Math.min(result, answer);
    }

    public static int getDist(int x1, int y1, int x2, int y2) {
        return Math.abs(x2 - x1) + Math.abs(y2 - y1);
    }

    static class Point {
        int x, y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
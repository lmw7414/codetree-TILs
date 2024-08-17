import java.util.*;
import java.io.*;


/**
 [문제 해결 프로세스]
 1) 맵 생성
 - 일반 루트 2,4,6,8,10 ... 36, 38, 40
 - 10, 20, 30 일 경우 빨간 라인으로.
 - 10 : 13, 16, 19, 25, 30, 35, 40
 - 20 : 22, 24, 25, 30, 35, 40
 - 30 : 28, 27, 26, 25, 30, 35, 40
 2) DFS로 풀기(중복 순열)
 - 10개를 두고 어떤 말을 이동할 지 조합
 -
 */
public class Main {
    static int MAX = 10;
    static int[] move = new int[10];
    static int[] cases = new int[10];
    static int answer = 0;
    static int[] original = new int[]{0,2,4,6,8,10,12,14,16,18,20,22,24,26,28,30,32,34,36,38,40,0};    // 0에서 시작
    static int[] arr10 = new int[]{10,13,16,19,25,30,35,40,0};     // 10에서 시작
    static int[] arr20 = new int[]{20,22,24,25,30,35,40,0};        // 20에서 시작
    static int[] arr30 = new int[]{30,28,27,26,25,30,35,40,0};     // 30에서 시작
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        for(int i = 0; i < 10; i++) {
            move[i] = Integer.parseInt(st.nextToken());
        }
        permutation(0);
        //cases = new int[]{0,0,0,1,0,2,2,0,2,2};
        //calc();
        System.out.println(answer);
    }

    public static void permutation(int depth) {
        if(depth == MAX) {
            int result = calc();
            if (result == -1) return;
            answer = Math.max(result, answer);
            return;
        }
        for(int i = 0; i <= 3; i++) {
            cases[depth] = i;
            permutation(depth + 1);
        }
    }

    public static int calc() {
        int result = 0;
        List<Piece> pieces = new ArrayList<>();
        for(int i = 0; i < 4; i++) pieces.add(new Piece());

        for(int idx = 0; idx < MAX; idx++) {
            Piece cur = pieces.get(cases[idx]);

            for(Piece other : pieces) {
                if(other == cur) continue;
                if(cur.isSamePlace(other, move[idx])) { // 같은 위치이면 true -> 이동 못함
                    return -1;
                }
            }
            //int next = cur.go(move[idx]);

            result += cur.go(move[idx]);
            // System.out.print(cases[idx] + " " + cur.curIdx);
            // System.out.println(" " + result + " ");
        }
        // System.out.println();
        if(result == 230) {
            for(int i = 0 ; i < MAX; i++) System.out.print(cases[i] + " ");
            System.out.println("result : " + result);
            System.out.println();
        }
        
        return result;
    }
    // 0 20 30 0
    static class Piece {
        int[] arr;
        int curIdx;
        public Piece() {
            this.arr = original;
            curIdx = 0;
        }
        public void setArr() {
            if(arr[curIdx] == 10) {
                this.arr = arr10;
                curIdx = 0;
            } else if(arr[curIdx] == 20) {
                this.arr = arr20;
                curIdx = 0;
            } else if(arr[curIdx] == 30) {
                this.arr = arr30;
                curIdx = 0;
            }

        }
        public int go(int move) {
            curIdx += move;
            if(curIdx >= arr.length - 1) curIdx = arr.length - 1; // 도착지에 도착
            int val = arr[curIdx];
            if(this.arr == original) setArr();

            return val;
        }

        public boolean isSamePlace(Piece other, int move) {
            if(other.isEndOfArr()) return false; // 해당 말은 마지막 위치이므로 상관 없음
            int nextIdx = this.curIdx + move > this.arr.length - 1 ? this.arr.length - 1 : this.curIdx + move;
            
            if(this.arr == original) {
                if(original[nextIdx] == 10 ||
                        original[nextIdx] == 20 ||
                        original[nextIdx] == 30) {
                    if(other.arr != original && other.curIdx == 0) return true;
                }
                return false;
            } else {  // 25, 30 , 35, 40 인 경우
                if(this.arr[nextIdx] == other.arr[other.curIdx]) return true;
                return false;
            }
        }

        public boolean isEndOfArr() {  // true -> 마지막 위치
            if(arr[curIdx] == arr.length - 1) return true;
            return false;
        }
    }
}
import java.util.*;
import java.io.*;

public class Main {
    static boolean[][] yellow = new boolean[10][4];
    static boolean[][] red = new boolean[10][4];
    static int answer = 0;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int K = Integer.parseInt(br.readLine());
        StringTokenizer st;
        for (int k = 0; k < K; k++) {
            st = new StringTokenizer(br.readLine());
            int t = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            int[][] yType = getType(t, x, y);
            move(yellow, yType);
            if (t == 2) t = 3;
            else if (t == 3) t = 2;
            int[][] rType = getType(t, y, x);
            move(red, rType);
        }
        System.out.println(answer);
        System.out.println(count());
    }

    public static void move(boolean[][] arr, int[][] type) {
        int[][] next = nextPos(arr, type);
        while (!checkOutside(next)) {
            deleteLine(arr, 9);
            next = nextPos(arr, type);
        }
        for (int[] n : next) {
            arr[n[0]][n[1]] = true;
        }
        crash(arr);
    }

    public static int[][] nextPos(boolean[][] arr, int[][] type) {
        int[][] result = copyType(type);
        int[][] next = copyType(type);
        while (checkBlock(arr, next)) {
            result = copyType(next);
            for (int[] b : next) {
                b[0] += 1;
            }
        }
//        for(int[] n : result) System.out.println(n[0] + " " + n[1]);
        return result;
    }

    // 사이 구역 체크 - 걸치면 false
    public static boolean checkOutside(int[][] type) {
        for (int[] t : type) {
            if (t[0] > 3 && t[0] <= 5) return false;
        }
        return true;
    }

    public static boolean checkBlock(boolean[][] arr, int[][] type) {
        for (int[] block : type) {
            if (block[0] > 9) return false;
            if (arr[block[0]][block[1]]) return false;
        }
        return true;
    }

    public static int[][] copyType(int[][] type) {
        int[][] result = new int[type.length][2];
        for (int i = 0; i < type.length; i++) {
            for (int j = 0; j < 2; j++)
                result[i][j] = type[i][j];
        }
        return result;
    }

    public static void crash(boolean[][] arr) {
        for (int i = 6; i < 10; i++) {
            int cnt = 0;
            for (int j = 0; j < 4; j++) {
                if (arr[i][j]) cnt++;
            }
            if (cnt == 4) {
                answer++;
                deleteLine(arr, i);
            }
        }
    }

    public static void deleteLine(boolean[][] arr, int idx) {
        for (int i = idx; i > 6; i--) {
            arr[i] = arr[i - 1];
        }
        arr[6] = new boolean[4];
    }

    public static int count() {
        int cnt = 0;
        for (int i = 6; i < 10; i++) {
            for (int j = 0; j < 4; j++) {
                if (yellow[i][j]) cnt++;
                if (red[i][j]) cnt++;
            }
        }
        return cnt;
    }

    public static int[][] getType(int t, int x, int y) {
        switch (t) {
            case 1:
                return new int[][]{{x, y}};
            case 2:
                return new int[][]{{x, y}, {x, y + 1}};
            case 3:
                return new int[][]{{x, y}, {x + 1, y}};
        }
        return null;
    }
}
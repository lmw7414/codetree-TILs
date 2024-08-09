import java.util.*;
import java.io.*;
/**
1) 유물 1차 획득 가치를 최대화
2) 회전한 각도가 가장 작은 방법을 선택
3) 회전 중심 좌표의 열이 가장 작은 구간
4) 열이 같다면 행이 가장 작은 구간 선택

1. 
유물 획득
- 상하좌우 인접한 같은 종류의 유물 조각은 서로 연결되어 있고, 이 조각들이 3개 이상 연결된 경우 조각이 되어 사라짐

새로 생기는 조각
- 열번호가 작은 순으로
- 열번호가 같다면 행번호가 큰 순으로
- 단 벽면의 숫자는 충분히 많이 적혀있어 생겨날 조각의 수가 부족할 경우는 없음

2. 유물 연쇄 획득
- 새로운 유물조각이 생겨난 이후에도 조각들이 3개이상 연결될 수 있음
- 다시 유물 획득으로 이어짐
- 또 다시 새로운 조각 생성

-> 각 턴마다 획득한 유물의 가치의 총합을 출력하는 프로그램
-> 단 아직 K번의 턴을 진행하지 못했지만, 어떠한 방법으로도 유물을 획득할 수 없다면 그 즉시 종료.
-> 이 경우 얻을 수 있는 유물이 존재하지 않음으로 종료되는 턴에 아무 값도 출력하지 않음
*/

public class Main {
    static StringBuilder sb = new StringBuilder();;
    static int K, M;
    static int[][] arr = new int[5][5];
    static int[] pieces;
    static int p = 0;
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        pieces = new int[M];

        for(int i = 0; i < 5; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j = 0; j < 5; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        st = new StringTokenizer(br.readLine());
        for(int i = 0; i < M; i++) pieces[i] = Integer.parseInt(st.nextToken());

        for(int k = 0; k < K; k++) {
            int result = findBest();
            if(result == 0) break;

            while(true) {
                fillPiece();
                int res = calc(arr);
                if(res == 0) break;
                result += res;
            }

            sb.append(result).append(" ");
        }
        System.out.print(sb.toString());
    }

    // 회전
    // d : 90 -> 1, 180 -> 2 270 -> 3
    public static int[][] rotate(int x, int y, int d) {
        int[] dx = {-1, -1, 1, 1}; // 왼쪽 위, 오른쪽 위, 오른쪽 아리, 왼쪽 아래 시작점
        int[] dy = {-1, 1, 1, -1};
        int[] kx = {0, 1, 0, -1};  // 구간별 증가
        int[] ky = {1, 0, -1, 0};
        int[][] temp = copyArr();
        
        for(int i = 0; i< 4; i++) {
            int ntx = x + dx[(i + d) % 4];
            int nty = y + dy[(i + d) % 4];
            int nax = x + dx[i % 4];
            int nay = y + dy[i % 4];

           for(int j = 0; j < 3; j++) {
                temp[ntx][nty] = arr[nax][nay];
                ntx += kx[(i + d) % 4];
                nty += ky[(i + d) % 4];
                nax += kx[i % 4];
                nay += ky[i % 4];
            }
        }   
        return temp;       
    }

    public static int[][] copyArr() {
        int[][] temp = new int[5][5];
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                temp[i][j] = arr[i][j];
            }
        }
        return temp;
    }

    // 주어진 배열에서 최대로 없앨 수 있는 유물 개수 구하기
    // 열이 가장 작고, 행이 가장 작고 -> x가 작고, y가 작고
    public static int findBest() {

        int[][] best = arr;
        int max = 0;
        // 회전 시킬 중심점
        for(int d = 1; d < 4; d++) {
            for(int i = 1; i < 4; i++) {
                for(int j = 1; j < 4; j++) {
                // 회전 후 유물 획득 개수 확인
                    int[][] temp = rotate(j, i, d);
                    int result = calc(temp);
                    if(result > max) {
                        best = temp;
                        max = result;
                    }   
                }
            }
        }
        arr = best;
        return max;
    }

    public static int calc(int[][] board) {
        int[] dx = {-1, 1, 0, 0}; // 상하좌우
        int[] dy = {0, 0, -1, 1};
        boolean[][] visit = new boolean[5][5];
        int result = 0;

        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                if(visit[i][j]) continue;
                visit[i][j] = true;
                List<int[]> erase = new ArrayList<>();
                int key = board[i][j];
                erase.add(new int[]{i, j});
                Queue<int[]> queue = new ArrayDeque<>();
                queue.add(new int[]{i, j});
                while(!queue.isEmpty()) {
                    int[] cur = queue.poll();
                    for(int d = 0; d < 4; d++) {
                        int nx = cur[0] + dx[d];
                        int ny = cur[1] + dy[d];

                        if(nx < 0 || ny < 0 || nx >= 5 || ny >= 5) continue;
                        if(visit[nx][ny]) continue;
                        if(key != board[nx][ny]) continue;

                        visit[nx][ny] = true;
                        queue.add(new int[]{nx, ny});
                        erase.add(new int[]{nx, ny});
                    }
                }
                if(erase.size() >= 3) {
                    result += erase.size();
                    for(int[] point : erase) {
                        board[point[0]][point[1]] = 0;
                    }
                }
            }
        }
        return result;
    }

    public static void fillPiece() {
        for(int y = 0; y < 5; y++) {
            for(int x = 4; x >= 0; x--) {
                if(arr[x][y] == 0) {
                    arr[x][y] = pieces[p++];
                }
            }
        }
        
    }

    public static void printArr() {
        System.out.println("--------------------");
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                System.out.print(arr[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println("--------------------");
    }
}
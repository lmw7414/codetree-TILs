import java.util.*;
import java.io.*;

/*
100 경주 시작 준비 | 초기 한번
  - 처음 토끼들은 전부 1행 1열 -> 토끼 리스트
200 경주 진행 | 최대 2000번
  - 가장 우선 순위가 높은 토끼를 뽑아 멀리 보내주는 것을 K번 반복
  - 현재까지의 총 점프 횟수가 적은 토끼 > 행번호 + 열번호가 작은 토끼 > 열번호가 작은 토끼 > 고유번호가 작은 토끼
  - 1. 상하좌우 네방향으로 각각 d 만큼 이동했을 때의 위치(격자를 벗어나면 방향을 바꿔 한 칸 이동)
  - 2. 이렇게 구해진 4개의 위치 중 (행번호 + 열번호가 큰 칸 > 행번호가 큰 칸 > 열번호가 큰칸) 가장 높은 우선순위로 이동
  - 3. 이동한 토끼를 제외한 나머지는 r + c 만큼의 점수를 동시에 얻음
  - 4. 위의 과정을 K번 반복
  - 5. 모두 반복 후 (행번호 + 열번호가 큰 칸 > 행번호가 큰 칸 > 열번호가 큰칸 > 고유번호가 큰 토끼)에서 높은 우선순위의 토끼를 골라 점수 S를 더해줌
    -> 이 경우 K번 턴 동안 한번이라도 뽑혔던 적이 있던 토끼 중 가장 우선순위가 높은 토끼를 골라야 함
300 이동거리 변경 | 최대 2000번
  - id가 t인 토끼의 이동거리를 L배 해줌
400 최고의 토끼 선정 | 마지막 한번
  - 토끼중 가장 높은 점수
*/

public class Main {
    static int Q;
    static int N, M, P, L;
    //static int[][] arr;
    static int[] dx = {-1, 0, 1, 0}; // 상 우 하 좌
    static int[] dy = {0, 1, 0, -1};
    static Map<Integer, Rabbit> hm = new HashMap<>();
    static PriorityQueue<Rabbit> rabbits = new PriorityQueue<>((a1, b1) -> {
        if (a1.count == b1.count) {
            if (a1.x + a1.y == b1.x + b1.y) {
                if (a1.y == b1.y) return a1.id - b1.id;
                return a1.y - b1.y;
            }
            return (a1.x + a1.y) - (b1.x + b1.y);
        }
        return a1.count - b1.count;
    });

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Q = Integer.parseInt(br.readLine());
        StringTokenizer st;
        for (int q = 0; q < Q; q++) {
            st = new StringTokenizer(br.readLine());
            int cmd = Integer.parseInt(st.nextToken());
            if (cmd == 100) {
                N = Integer.parseInt(st.nextToken());
                M = Integer.parseInt(st.nextToken());
                //arr = new int[N + 1][M + 1];
                P = Integer.parseInt(st.nextToken());
                for (int p = 0; p < P; p++) {
                    int id = Integer.parseInt(st.nextToken());
                    int dist = Integer.parseInt(st.nextToken());
                    enroll(id, dist);
                }
            } else if (cmd == 200) {
                int K = Integer.parseInt(st.nextToken());
                int S = Integer.parseInt(st.nextToken());
                play(K, S);
            } else if (cmd == 300) {
                int id = Integer.parseInt(st.nextToken());
                int L = Integer.parseInt(st.nextToken());
                Rabbit r = hm.get(id);
                r.dist *= L;
                // 이동 거리 L배 증가
            } else if (cmd == 400) {
                printResult();
                break;
            }
        }
    }

    // 100 경주 시작 준비
    public static void enroll(int id, int dist) {
        Rabbit rabbit = new Rabbit(id, dist);
        hm.put(id, rabbit);
        rabbits.add(rabbit);
    }

    // 200 경주 진행
    public static void play(int K, int S) {
        for (int k = 0; k < K; k++) {
            Rabbit rabbit = rabbits.poll();
            rabbit.count += 1;
            Pos next = bestPos(rabbit.x, rabbit.y, rabbit.dist);
            rabbit.x = next.x;
            rabbit.y = next.y;
            int score = next.x + next.y;

            for (Rabbit r : rabbits) r.score += score;
            rabbits.add(rabbit);
        }

        Rabbit best = null;
        for (Rabbit r : rabbits) {
            best = bestRabbit(best, r);
        }
        best.score += S;
    }

    //(행번호 + 열번호가 큰 칸 > 행번호가 큰 칸 > 열번호가 큰칸 > 고유번호가 큰 토끼)에서 높은 우선순위의 토끼를 골라 점수 S를 더해줌
    public static Rabbit bestRabbit(Rabbit best, Rabbit r) {
        if (best == null) return r;
        if (best.x + best.y == r.x + r.y) {
            if (best.x == r.x) {
                if (best.y == r.y) {
                    if (best.id > r.id) return best;
                    else return r;
                } else {
                    if (best.y > r.y) return best;
                    else return r;
                }
            } else {
                if (best.x > r.x) return best;
                else return r;
            }
        } else {
            if (best.x + best.y > r.x + r.y) return best;
            else return r;
        }

    }

    // 사방 탐색 (행번호 + 열번호가 큰 칸 > 행번호가 큰 칸 > 열번호가 큰칸)
    public static Pos bestPos(int x, int y, long d) {
        Pos best = null;
        int size = 0;
        int width = 0;
        for (int i = 0; i < 4; i++) {
            if (i % 2 == 0) width = N;
            else width = M;
            size = (width - 1) * 2;

            int nd = (int) (d % size);
            Pos p = null;
            if (i % 2 == 0) {
                if (x + nd < width) {
                    p = new Pos(x + nd, y);
                } else {
                    p = new Pos(width - (x + nd - width), y);
                }
            } else {
                if (y + nd < width) {
                    p = new Pos(x, y + nd);
                } else {
                    p = new Pos(x, width - (y + nd - width));
                }
            }
            best = isBetterPos(best, p);


//            if(size - nd < width) {
//                if(i % 2 == 0) p = new Pos(size - nd, y);
//                else p = new Pos(x, size - nd);
//            }else {
//                if(i % 2 == 0) p = new Pos(nd, y);
//                else p = new Pos(x, nd);
//            }
        }
        return best;
    }

    public static Pos isBetterPos(Pos best, Pos o) {
        if (best == null) return o;
        if (best.x + best.y == o.x + o.y) {
            if (best.x == o.x) {
                if (best.y > o.y) return best;
                else return o;
            } else {
                if (best.x > o.x) return best;
                else return o;
            }
        } else {
            if (best.x + best.y > o.x + o.y) return best;
            else return o;
        }
    }

    // 400 최고의 토끼 선정
    public static void printResult() {
        long answer = 0;
        for (Rabbit r : rabbits) {
            answer = Math.max(answer, r.score);
        }
        System.out.println(answer);
    }

    static class Rabbit {
        int id;
        int x, y;
        long dist;
        int count;
        long score;

        public Rabbit(int id, int dist) {
            this.id = id;
            x = 1;
            y = 1;
            this.dist = dist;
            count = 0;
            score = 0;
        }
    }

    static class Pos {
        int x, y;

        public Pos(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
import java.util.*;
import java.io.*;

/*
최적의 여행 경험 제공
N개의 도시, M개의 간선
도시는 0번 ~ N-1번
간선은 양방향
두 도시 사이의 간선은 여러개 존재할 수 있음. 자기자신으로 돌아오는 간선도 있음

1. 랜드 건설
-  출발 : V | 도착 : U | 가중치 W
2. 여행 상품 생성
-  (id, 매출, 도착지)
3. 여행 상품 취소
4. 최적 여행 상품 판매
- 조건에 맞는 여행 상품 선택 -> 상품을 판매함으로써 얻게 되는 이득(매출 - 비용)이 최대
- 비용 : 출발지 ~ 도착지까지의 최단거리. 다익스트라?
- 동일한 조건의 경우 id가 낮은 순
- 손해의 경우 판매 불가
- 판매 가능 상품 중 우선 순위 높은 상품 id 출력 후 관리 목록에서 제거.
- 없다면 -1 출력
5. 출발지 변경
- 여행 상품의 출발지를 전부 s로 변경
- cost 변경될 수 있음


*/
public class Main {
    static int N, M;
    static int[] dist; // 특정 구간으로 부터 거리
    static List<Land>[] adjList;
    static int INF = 987654321;
    static PriorityQueue<Package> packagePQ = new PriorityQueue<>();
    static Queue<Package> prohibit = new ArrayDeque<>();
    static Set<Integer> ids = new HashSet<>();
    static StringBuilder sb = new StringBuilder();

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int Q = Integer.parseInt(br.readLine());  // 최대 10만
        StringTokenizer st = null;
        for(int q = 0; q < Q; q++) {
            st = new StringTokenizer(br.readLine());
            int command = Integer.parseInt(st.nextToken());
            if (command == 100) {  // 랜드 건설
                N = Integer.parseInt(st.nextToken()); // 최대 2000
                M = Integer.parseInt(st.nextToken()); // 최대 10000
                init(N);
                for(int m = 0; m < M; m++) {
                    int V = Integer.parseInt(st.nextToken());
                    int U = Integer.parseInt(st.nextToken());
                    int W = Integer.parseInt(st.nextToken()); // 최대 100
                    buildLand(V, U, W);
                }
                dijkstra(0);
            } else if(command == 200) { // 상품 생성
                int id = Integer.parseInt(st.nextToken());
                int revenue = Integer.parseInt(st.nextToken());
                int dest = Integer.parseInt(st.nextToken()); // 최대 100
                addPackage(id, revenue, dest);
            } else if (command == 300) {
                int id = Integer.parseInt(st.nextToken());
                cancelPackage(id);
            } else if (command == 400) {
                sell();
            } else if (command == 500) {
                int start = Integer.parseInt(st.nextToken());
                dijkstra(start);
                retestPackage();
            }
        }
        System.out.print(sb.toString());
    } 

    // 100
    public static void buildLand(int v, int u, int w) {
        adjList[v].add(new Land(u, w));
        adjList[u].add(new Land(v,w));
    }

    // 200  여행 상품 생성
    public static void addPackage(int id, int revenue, int dest) {
        ids.add(id);
        if(revenue - dist[dest] < 0) { // 판매 불가 상품
            prohibit.add(new Package(id, revenue, dest));
        } else {
            packagePQ.add(new Package(id, revenue, dest));
        }
    }

    // 300
    public static void cancelPackage(int id) {
        ids.remove(id);
    }

    // 400 최적의 여행 상품 id 출력. 없으면 -1
    public static void sell() {
        while(true) {
            if(packagePQ.isEmpty()) break;
            int id = packagePQ.poll().id;
            if(!ids.contains(id)) continue; // 이미 제거된 id 인 경우 다시 뽑기
            else {
                ids.remove(id);
                sb.append(id).append("\n");
                return;
            }
        }
        sb.append(-1).append("\n");
    }


    // 500
    public static void dijkstra(int start) {
        Arrays.fill(dist, INF);
        dist[start] = 0;
        PriorityQueue<Land> pq = new PriorityQueue<>((a1, b1) -> a1.w - b1.w);
        pq.add(new Land(start, 0));

        while(!pq.isEmpty()) {
            Land cur = pq.poll();
            if(cur.w > dist[cur.u]) continue;

            for(Land next : adjList[cur.u]) {
                if(dist[next.u] > dist[cur.u] + next.w) {
                    dist[next.u] = dist[cur.u] + next.w;
                    pq.add(new Land(next.u, dist[next.u]));
                }
            }
        }
    }

    // 판매 불가 상품 다시 확인
    public static void retestPackage() {
        List<Package> temp = new ArrayList<>();
        while(!prohibit.isEmpty()) {
            Package cur = prohibit.poll();

            if(cur.revenue - dist[cur.dest] < 0) { // 판매 불가 상품
                temp.add(cur);
            } else {
                packagePQ.add(cur);
            }
        }
        for(Package p : temp) prohibit.add(p);
    }

    public static void init(int n) {
        dist = new int[n];
        adjList = new List[n];
        for(int i = 0; i < n; i++) {
            adjList[i] = new ArrayList<Land>();
        }
    }

    static class Land {
        int u;
        int w;
        public Land(int u, int w) {
            this.u = u;
            this.w = w;
        }
    }

    static class Package implements Comparable<Package> {
        int id;
        int revenue;
        int dest;
        int cost;

        public Package(int id, int revenue, int dest) {
            this.id = id;
            this.revenue = revenue;
            this.dest = dest;
            this.cost = revenue - dist[dest];
        }

        public int setCost(int dist) {
            return revenue - dist;
        }

        // cost가 최대인 것
        // cost가 같다면 id가 작은 것
        @Override
        public int compareTo(Package p) {
            if (this.cost == p.cost) return this.id - p.id;
            return p.cost - this.cost;
        }
    }
}
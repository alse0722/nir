import java.util.*;

public class GraphParserGeo0_2 {

    // Функция для декодирования graph6 формата
    private static int[][] decodeGraph6(String graph6) {
        int n = graph6.charAt(0) - 63;
        int[][] graph = new int[n][n];

        int bitIdx = 0;
        for (int i = 1; i < graph6.length(); i++) {
            int val = graph6.charAt(i) - 63;
            for (int bit = 5; bit >= 0; bit--) {
                if (bitIdx >= n * (n - 1) / 2) {
                    break;
                }
                int row = 0;
                while (bitIdx >= row * (row + 1) / 2) {
                    row++;
                }
                int col = bitIdx - (row * (row - 1) / 2);
                graph[row][col] = (val >> bit) & 1;
                graph[col][row] = graph[row][col];
                bitIdx++;
            }
        }
        return graph;
    }

    // Метод для вычисления независимого геодезического числа
    private static int calculateIndependentGeoNumber(int[][] graph) {
        int n = graph.length;
        int[][] dist = floydWarshall(graph);
        int minGeoSetSize = Integer.MAX_VALUE;

        // Перебор всех подмножеств вершин с динамическим программированием
        for (int subset = 1; subset < (1 << n); subset++) {
            int subsetSize = Integer.bitCount(subset);
            if (subsetSize >= minGeoSetSize) {
                continue;
            }

            if (isIndependentGeoSet(subset, graph, dist, n)) {
                minGeoSetSize = subsetSize;
            }
        }

        // Если minGeoSetSize не изменилось, это означает, что не найдено никакого независимого геодезического множества
        return (minGeoSetSize == Integer.MAX_VALUE) ? 0 : minGeoSetSize;
    }

    // Вспомогательный метод для проверки, является ли подмножество независимым геодезическим множеством
    private static boolean isIndependentGeoSet(int subset, int[][] graph, int[][] dist, int n) {
        List<Integer> vertices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if ((subset & (1 << i)) != 0) {
                vertices.add(i);
            }
        }

        // Проверка, является ли подмножество независимым
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                if (graph[vertices.get(i)][vertices.get(j)] == 1) {
                    return false;
                }
            }
        }

        // Проверка, что каждая вершина графа лежит на геодезической, соединяющей пару вершин из подмножества
        boolean[] covered = new boolean[n];
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                int u = vertices.get(i);
                int v = vertices.get(j);
                for (int k = 0; k < n; k++) {
                    if (dist[u][k] + dist[k][v] == dist[u][v]) {
                        covered[k] = true;
                    }
                }
            }
        }

        for (int i = 0; i < n; i++) {
            if (!covered[i]) {
                return false;
            }
        }

        return true;
    }

    // Алгоритм Флойда-Уоршалла для нахождения кратчайших путей между всеми парами вершин
    private static int[][] floydWarshall(int[][] graph) {
        int n = graph.length;
        int[][] dist = new int[n][n];
        final int INF = Integer.MAX_VALUE / 3; // Используем аддитивную константу вместо деления

        // Инициализация матрицы расстояний
        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], INF);
            dist[i][i] = 0;
            for (int j = 0; j < n; j++) {
                if (graph[i][j] == 1) {
                    dist[i][j] = 1;
                }
            }
        }

        // Применение алгоритма Флойда-Уоршалла
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][j] > dist[i][k] + dist[k][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }

        return dist;
    }

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int vertexCount = 0;
        int graphCount = 0;
        Map<Integer, Integer> geoStats = new HashMap<>();
        long startTime = System.currentTimeMillis();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                break;
            }
            int[][] graph = decodeGraph6(line);
            if (vertexCount == 0) {
                vertexCount = graph.length;
            }
            int geoNumber = calculateIndependentGeoNumber(graph);
            geoStats.put(geoNumber, geoStats.getOrDefault(geoNumber, 0) + 1);
            graphCount++;
        }

        long endTime = System.currentTimeMillis();
        double elapsedTime = (endTime - startTime) / 1000.0;

        System.out.println("Number of vertices: " + vertexCount);
        System.out.println("Total graphs processed: " + graphCount);
        System.out.println("Elapsed time: " + elapsedTime + " seconds");
        System.out.println("Geo statistics:");

        System.out.print("[");
        boolean first = true;
        for (Map.Entry<Integer, Integer> entry : geoStats.entrySet()) {
            if (!first) {
                System.out.print(", ");
            }
            System.out.print("{geo_number: " + entry.getKey() + ", count: " + entry.getValue() + "}");
            first = false;
        }
        System.out.println("]");
    }
}

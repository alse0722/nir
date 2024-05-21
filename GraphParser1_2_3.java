import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GraphParser1_2_3 {

    // Функция для декодирования graph6 формата
    private static int[][] decodeGraph6(String graph6) {
        int n = graph6.charAt(0) - 63;
        int[][] graph = new int[n][n];

        int bitIdx = 0;
        for (int i = 1; i < graph6.length(); i++) {
            int val = graph6.charAt(i) - 63;
            for (int bit = 5; bit >= 0; bit--) {
                if (bitIdx >= n * (n - 1) / 2) break;
                int row = 0;
                while (bitIdx >= row * (row + 1) / 2) row++;
                int col = bitIdx - (row * (row - 1) / 2);
                graph[row][col] = (val >> bit) & 1;
                graph[col][row] = graph[row][col];
                bitIdx++;
            }
        }
        return graph;
    }

    // Метод для вычисления числа полного доминирования
    private static int calculateTotalDominatingNumber(int[][] graph) {
        int n = graph.length;
        int minDominatingSetSize = n;

        // Перебор всех подмножеств вершин
        for (int subset = 1; subset < (1 << n); subset++) {
            int subsetSize = Integer.bitCount(subset);

            // Если подмножество больше уже найденного минимального доминирующего множества, пропускаем его
            if (subsetSize >= minDominatingSetSize) continue;

            // Проверка, является ли текущее подмножество полным доминирующим множеством
            if (isTotalDominatingSet(subset, graph, n)) {
                minDominatingSetSize = subsetSize;
            }
        }

        return minDominatingSetSize;
    }

    // Вспомогательный метод для проверки, является ли подмножество полным доминирующим множеством
    private static boolean isTotalDominatingSet(int subset, int[][] graph, int n) {
        boolean[] dominated = new boolean[n];
        boolean[] hasNeighbor = new boolean[n];

        for (int u = 0; u < n; u++) {
            if ((subset & (1 << u)) != 0) {
                boolean hasLocalNeighbor = false;
                for (int v = 0; v < n; v++) {
                    if (graph[u][v] == 1) {
                        dominated[v] = true;
                        hasLocalNeighbor = true;
                    }
                }
                if (!hasLocalNeighbor) {
                    return false;
                }
                hasNeighbor[u] = true;
            }
        }

        // Проверяем, что все вершины графа доминируемы
        for (int v = 0; v < n; v++) {
            if (!dominated[v]) {
                return false;
            }
        }

        return true;
    }

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int vertexCount = 0;
        int graphCount = 0;
        Map<Integer, Integer> dominationStats = new HashMap<>();
        long startTime = System.currentTimeMillis();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) break;
            int[][] graph = decodeGraph6(line);
            if (vertexCount == 0) {
                vertexCount = graph.length;
            }
            int domNumber = calculateTotalDominatingNumber(graph);
            dominationStats.put(domNumber, dominationStats.getOrDefault(domNumber, 0) + 1);
            graphCount++;
        }

        long endTime = System.currentTimeMillis();
        double elapsedTime = (endTime - startTime) / 1000.0;

        System.out.println("Number of vertices: " + vertexCount);
        System.out.println("Total graphs processed: " + graphCount);
        System.out.println("Elapsed time: " + elapsedTime + " seconds");
        System.out.println("Domination statistics:");

        System.out.print("[");
        boolean first = true;
        for (Map.Entry<Integer, Integer> entry : dominationStats.entrySet()) {
            if (!first) {
                System.out.print(", ");
            }
            System.out.print("{dom_number: " + entry.getKey() + ", count: " + entry.getValue() + "}");
            first = false;
        }
        System.out.println("]");
    }
}

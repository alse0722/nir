import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GraphParser3 {

    private static int calculateTotalDominatingNumber(int[] graph, int n) {
        int minDomNum = n; // максимальное возможное число полного доминирования

        for (int i = 1; i < (1 << n); i++) {
            boolean[] dominated = new boolean[n];
            int domSetSize = 0;
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) != 0) {
                    domSetSize++;
                    for (int u = 0; u < n; u++) {
                        if ((graph[j] & (1 << u)) != 0) dominated[u] = true;
                    }
                }
            }
            boolean allDominated = true;
            for (boolean v : dominated) {
                if (!v) {
                    allDominated = false;
                    break;
                }
            }
            if (allDominated) {
                minDomNum = Math.min(minDomNum, domSetSize);
            }
        }
        return minDomNum;
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
            int n = line.charAt(0) - 63;
            int[] graph = new int[n];
            for (int i = 1; i < line.length(); i++) {
                int val = line.charAt(i) - 63;
                graph[i - 1] = val;
            }
            if (vertexCount == 0) vertexCount = n;
            int domNumber = calculateTotalDominatingNumber(graph, n);
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
            if (!first) System.out.print(", ");
            System.out.print("{dom_number: " + entry.getKey() + ", count: " + entry.getValue() + "}");
            first = false;
        }
        System.out.println("]");
    }
}

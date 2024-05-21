import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GraphParser1_2_1 {

  // Функция для декодирования graph6 формата
  private static List<List<Integer>> decodeGraph6(String graph6) {
    int n = graph6.charAt(0) - 63;
    List<List<Integer>> graph = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      graph.add(new ArrayList<>(n));
      for (int j = 0; j < n; j++) {
        graph.get(i).add(0);
      }
    }

    int bitIdx = 0;
    for (int i = 1; i < graph6.length(); i++) {
      int val = graph6.charAt(i) - 63;
      for (int bit = 5; bit >= 0; bit--) {
        if (bitIdx >= n * (n - 1) / 2)
          break;
        int row = 0;
        while (bitIdx >= row * (row + 1) / 2)
          row++;
        int col = bitIdx - (row * (row - 1) / 2);
        graph.get(row).set(col, (val >> bit) & 1);
        graph.get(col).set(row, (val >> bit) & 1);
        bitIdx++;
      }
    }
    return graph;
  }

  // Метод для вычисления числа полного доминирования
  private static int calculateTotalDominatingNumber(List<List<Integer>> graph) {
    int n = graph.size();
    int minDominatingSetSize = n;

    // Оптимизированный перебор всех подмножеств вершин
    for (int i = 1; i < (1 << n); i++) {
      if (Integer.bitCount(i) >= minDominatingSetSize) {
        continue;
      }

      if (isTotalDominatingSet(i, graph, n)) {
        minDominatingSetSize = Integer.bitCount(i);
      }
    }

    return minDominatingSetSize;
  }

  // Вспомогательный метод для проверки, является ли подмножество полным
  // доминирующим множеством
  private static boolean isTotalDominatingSet(int subset, List<List<Integer>> graph, int n) {
    boolean[] dominated = new boolean[n];
    boolean[] hasNeighbor = new boolean[n];

    for (int u = 0; u < n; u++) {
      if ((subset & (1 << u)) != 0) {
        boolean hasLocalNeighbor = false;
        for (int v = 0; v < n; v++) {
          if (graph.get(u).get(v) == 1) {
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
      if (line.isEmpty())
        break;
      List<List<Integer>> graph = decodeGraph6(line);
      if (vertexCount == 0) {
        vertexCount = graph.size();
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

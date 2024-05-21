import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GraphParser1_1 {

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

  private static int calculateTotalDominatingNumber(List<List<Integer>> graph) {
    int n = graph.size();
    boolean[] dominatingSet = new boolean[n]; // Флаги для отслеживания вершин в доминирующем множестве
    int domNum = 0; // Число полного доминирования

    for (int v = 0; v < n; v++) {
      if (!dominatingSet[v]) { // Если текущая вершина не покрыта
        domNum++; // Увеличиваем число полного доминирования
        dominatingSet[v] = true; // Помечаем вершину как покрытую
        // Помечаем всех смежных вершин как покрытые
        for (int u = 0; u < n; u++) {
          if (graph.get(v).get(u) == 1) {
            dominatingSet[u] = true;
          }
        }
      }
    }

    return domNum;
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

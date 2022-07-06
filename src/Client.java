import aStar.DefaultScorer;
import aStar.RouteFinder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Cluster;

import clustering.DBSCANApache;
import clustering.DataFilter;
import clustering.DataPoint;
import clustering.FuzzyKMeansApache;
import lenz.htw.coshnost.net.NetworkClient;
import lenz.htw.coshnost.world.GraphNode;

public class Client {

  public static void main(String[] args) {

    // Start Server: java -Djava.library.path=lib/native -jar coshnost.jar

    NetworkClient client = new NetworkClient(null, "Teamname", "Juhu, ich habe gewonnen!");

    int myNumber = client.getMyPlayerNumber();
    // client.getScore(0);
    // client.getBotSpeed(0); //Konstanten

    // float[] botDirection = client.getBotDirection(0); // vermutlich nicht
    // notwendig
    // client.changeMoveDirection(0, 0); // vermutlich nicht notwendig
    // System.out.println(recentUpdate);
    // System.out.println(myNumber);

    GraphNode[] graph = client.getGraph(); // immer Größe 10242
    /*
     * System.out.println(graph[0].isBlocked()); // ist ein Graben oder nicht
     * System.out.println(graph[0].getOwner()); // wem gehört das Feld? (0,1,2 =
     * player number, -1 = leer)
     * System.out.println(graph[0].getX());
     */ // Ortsvektor
    // float[] pos = graph[0].getPosition();

    RouteFinder routeFinder = new RouteFinder(new DefaultScorer());
    List<GraphNode> route = routeFinder.findRoute(graph[0], graph[15]);
    System.out.println("a star route" + route);

    // GraphNode[] neighbors = graph[0].getNeighbors(); // immer genau 5 oder 6
    // Nachbarn

    double radius = 0.04;
    DBSCANApache DBSCAN = new DBSCANApache(radius, 5);
    DBSCANApache DBSCAN1 = new DBSCANApache(radius, 5);
    DBSCANApache DBSCAN2 = new DBSCANApache(radius, 5);
    DBSCANApache PITHOLES = new DBSCANApache(0.09, 5);
    // FuzzyKMeansApache FuzzyKMeans = new FuzzyKMeansApache(1, 2);
    DataFilter FILTER = new DataFilter();
    long recentId = -1;
    while (client.isGameRunning()) {
      long currentUpdateId = client.getMostRecentUpdateId();
      client.changeMoveDirection(0, (float) -0.5276859402656555, (float) -0.5276859402656555, (float) 0.0);
      client.changeMoveDirection(1, (float) -0.5276859402656555, (float) -0.5276859402656555, (float) 0.0);
      client.changeMoveDirection(2, (float) -0.5276859402656555, (float) -0.5276859402656555, (float) 0.0);

      if (currentUpdateId != recentId) {
        recentId = currentUpdateId;
        GraphNode[] netGraph = client.getGraph();
        // getNumberOfFreeFields(netGraph);

        if (recentId == 10) {

          /*
           * ArrayList<DataPoint> playersP = FILTER.getPlayersNodes(myNumber, netGraph);
           * ArrayList<DataPoint> opponentP = FILTER.getOpponentsNodes(myNumber,
           * netGraph);
           * ArrayList<DataPoint> occupiedP = FILTER.getOccupiedNodes(myNumber, netGraph);
           * ArrayList<DataPoint> blankP = FILTER.getBlankNodes(netGraph);
           */
          ArrayList<ArrayList<DataPoint>> allFilteredDataTypes = FILTER.getAllTypeOfNodes(myNumber, netGraph);

          System.out.println("\nMy Number: " + myNumber + "\n");
          // --------- DBSCAN- Measure execution time - start
          long startTime = System.nanoTime();
          List<Cluster<DataPoint>> dbscanCluster = DBSCAN.cluster(allFilteredDataTypes.get(0));
          List<Cluster<DataPoint>> dbscanCluster1 = DBSCAN1.cluster(allFilteredDataTypes.get(1));
          List<Cluster<DataPoint>> dbscanCluster2 = DBSCAN2.cluster(allFilteredDataTypes.get(2));
          List<Cluster<DataPoint>> dbscanCluster3 = PITHOLES.cluster(allFilteredDataTypes.get(4));

          // List<Cluster<DataPoint>> dbscanCluster2 = DBSCANBlank.cluster(p2);
          long endTime = System.nanoTime();
          long duration = (endTime - startTime);
          System.out.println("DBSCAN Elapsed Time in nano seconds: " + duration);
          // --------- DBSCAN- Measure execution time - end

          // --------- FuzzyKMeans - Measure execution time - start
          /*
           * long startTime2 = System.nanoTime();
           * List<CentroidCluster<DataPoint>> fuzzyKMansCluster =
           * FuzzyKMeans.cluster(allFilteredDataTypes.get(1));
           * for (CentroidCluster<DataPoint> c : fuzzyKMansCluster) {
           * System.out.println("FuzzyK Center of Cluster " + c.toString() + ": " +
           * Arrays.toString(c.getCenter().getPoint()));
           * }
           * long endTime2 = System.nanoTime();
           * long duration2 = (endTime2 - startTime2);
           * System.out.println("FuzzyK Elapsed Time in nano seconds: " + duration2);
           * //--------- FuzzyKMeans - Measure execution time - end
           */

        }
      }
    }

  }

  public static int getNumberOfFreeFields(GraphNode[] netGraph) {
    int free = 0;
    for (GraphNode node : netGraph) {
      if (node.getOwner() == -1)
        free++;
    }
    System.out.println("UPDATED GRAPH: free: " + free);
    return free;
  }

  /*
   * Bot 0 (einfarbig) hat die höchste Geschwindigkeit, erhöht eigenen Farbanteil
   * Bot 1 (gepunktet) ist am langsamsten, setzt eigenen Farbanteil hoch
   * Bot 2 (gestreift) kann sich über Gräben bewegen, löscht alle Farben inklusive
   * der eigenen
   */

  /*
   * public static GraphNode aStar(GraphNode start, GraphNode target) {
   * PriorityQueue<GraphNode> closedList = new PriorityQueue<>();
   * PriorityQueue<GraphNode> openList = new PriorityQueue<>();
   * 
   * openList.add(start);
   * 
   * while (!openList.isEmpty()) {
   * GraphNode n = openList.peek();
   * if (n == target) {
   * return n;
   * }
   * 
   * for (GraphNode node : n.getNeighbors()) {
   * // TODO
   * // Bot 0 -> sucht nach leeren Feldern (je mehr auf einem Haufen, desto
   * besser)
   * // Bot 1 -> sucht nach leeren & von Gegnern gefüllten Feldern (je näher,
   * desto besser)
   * // Bot 2 -> sucht nach von Gegnern gefüllten Feldern (je mehr auf einem
   * Haufen, desto besser)
   * }
   * 
   * openList.remove(n);
   * closedList.add(n);
   * }
   * return null;
   * }
   */
}

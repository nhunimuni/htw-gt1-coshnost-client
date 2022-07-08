import aStar.DefaultScorer;
import aStar.RouteFinder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import clustering.ClusterExtension;
import clustering.DBSCANApache;
import clustering.DataFilter;
import clustering.DataPoint;
import lenz.htw.coshnost.net.NetworkClient;
import lenz.htw.coshnost.world.GraphNode;
import util.Tuple;

public class Client {
  // Start Server: java -Djava.library.path=lib/native -jar coshnost.jar

  public static void main(String[] args) {
    NetworkClient client = new NetworkClient(null, "Minu", "uwu");
    int myNumber = client.getMyPlayerNumber();
    RouteFinder routeFinder = new RouteFinder(new DefaultScorer());

    double radius = 0.05;
    DBSCANApache DBSCAN = new DBSCANApache(radius, 5);
    DataFilter FILTER = new DataFilter();
    long recentId = -1;

    ArrayList<ArrayList<DataPoint>> allFilteredDataTypes = new ArrayList<>();
    Tuple targetForBot0 = new Tuple(); // One colored - fast
    Tuple targetForBot1 = new Tuple(); // Dotted - strong but slow
    Tuple targetForBot2 = new Tuple(); // Striped - Deletion

    while (client.isGameRunning()) {
      long currentUpdateId = client.getMostRecentUpdateId();
      if (currentUpdateId != recentId) {
        recentId = currentUpdateId;
        GraphNode[] graph = client.getGraph();

        if (recentId % 50 == 1) {
          allFilteredDataTypes = FILTER.getAllTypeOfNodes(myNumber, graph);
        }

        if (recentId > 0 && allFilteredDataTypes.size() > 0 && targetForBot0.route.size() == 0) {
          GraphNode bot0Node = findGraphNodeByPosition(graph, client.getBotPosition(myNumber, 0));
          GraphNode bot1Node = findGraphNodeByPosition(graph, client.getBotPosition(myNumber, 1));
          GraphNode bot2Node = findGraphNodeByPosition(graph, client.getBotPosition(myNumber, 2));
          List<ClusterExtension> emptyClusters = DBSCAN.cluster(allFilteredDataTypes.get(3));
          List<ClusterExtension> occupiedCluster = DBSCAN.cluster(allFilteredDataTypes.get(2));
          List<ClusterExtension> opponentCluster = DBSCAN.cluster(allFilteredDataTypes.get(1));

          // BOT 0
          // Mainly empty cluster but should also target occupied cluster!
          if (bot0Node != null && emptyClusters.size() > 0) {
            /*
             * emptyClusters.forEach(cluster -> {
             * List<Float> centroidList = cluster.getCentroid();
             * float[] centroidArray = new float[centroidList.size()];
             * for (int n = 0; n < centroidList.size(); n++) {
             * centroidArray[n] = centroidList.get(n);
             * }
             * GraphNode center = findGraphNodeByPosition(graph, centroidArray);
             * List<GraphNode> route;
             * route = routeFinder.findRoute(bot0Node, center);
             * 
             * // Find cluster nearest to our bot
             * if (targetForBot0.route.size() == 0 || route.size() <
             * targetForBot0.route.size()) {
             * targetForBot0.cluster = cluster;
             * targetForBot0.route = route;
             * System.out.println("new target cluster" + cluster.getCentroid().toString());
             * }
             * });
             */
            Tuple plannedMove = planMove(graph, emptyClusters, routeFinder, bot0Node, targetForBot0);
            targetForBot0.cluster = plannedMove.cluster;
            targetForBot0.route = plannedMove.route;
          }

          // BOT 1 - Strong but slow
          // Cluster of occupied and empty and find die closest one
          if (bot1Node != null && emptyClusters.size() > 0) {
            List<ClusterExtension> joinedCluster = Stream.concat(emptyClusters.stream(), occupiedCluster.stream())
                .toList();
            Tuple plannedMove = planMove(graph, joinedCluster, routeFinder, bot1Node, targetForBot1);
            targetForBot1.cluster = plannedMove.cluster;
            targetForBot1.route = plannedMove.route;
          }

          // BOT 2 - Deletion
          // Cluster of other oponnents
          // Maybe also follow them at some point?
          // Also remove own occupied nodes from path
        }

        if (targetForBot0.route.size() > 0) {
          setMove(targetForBot0, client);
        }

        if (targetForBot1.route.size() > 0) {
          setMove(targetForBot1, client);
        }
      }
    }
  }

  /**
   * 
   * @param graph
   * @param clusters
   * @param routeFinder
   * @param botStartNode
   * @param botTargetNode
   * @return
   */
  public static Tuple planMove(GraphNode[] graph, List<ClusterExtension> clusters, RouteFinder routeFinder,
      GraphNode botStartNode, Tuple botTargetNode) {
    Tuple targetNode = new Tuple();
    clusters.forEach(cluster -> {
      List<Float> centroidList = cluster.getCentroid();
      float[] centroidArray = new float[centroidList.size()];
      for (int n = 0; n < centroidList.size(); n++) {
        centroidArray[n] = centroidList.get(n);
      }
      GraphNode center = findGraphNodeByPosition(graph, centroidArray);
      List<GraphNode> route;
      route = routeFinder.findRoute(botStartNode, center);

      // Find cluster nearest to our bot
      if (botTargetNode.route.size() == 0 || route.size() < botTargetNode.route.size()) {
        targetNode.cluster = cluster;
        targetNode.route = route;
      }
    });

    return targetNode;
  }

  /**
   * Set move for bot.
   */
  public static void setMove(Tuple target, NetworkClient client) {
    GraphNode nextNode = target.route.get(0);
    client.changeMoveDirection(0, nextNode.x, nextNode.y, nextNode.z);
    target.route.remove(0);
  }

  /**
   * Find a matching GraphNode on the graph net by the position
   *
   * @param graph
   * @param position
   * @return
   */
  public static GraphNode findGraphNodeByPosition(GraphNode[] graph, float[] position) {
    return Arrays.stream(graph).filter(a -> !a.blocked)
        .min(Comparator.comparingDouble(a -> distance(position, a.getPosition()))).orElse(null);
  }

  /**
   * Euclidean Distance 3D
   *
   * @param arg0
   * @param arg1
   * @return
   */
  private static double distance(float[] arg0, float[] arg1) {
    double a = Math.pow(arg1[0] - arg0[0], 2);
    double b = Math.pow(arg1[1] - arg0[1], 2);
    double c = Math.pow(arg1[2] - arg0[2], 2);
    return Math.sqrt(a + b + c);
  }
}
